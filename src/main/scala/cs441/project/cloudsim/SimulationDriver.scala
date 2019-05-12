package cs441.project.cloudsim

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import cs441.project.cloudsim.jobs.Job
import cs441.project.cloudsim.jobs.mapreduce.ResourceManager
import cs441.project.cloudsim.policies.loadbalancing.{DatacenterBrokerMaxMin, DatacenterBrokerMinMin}
import cs441.project.cloudsim.utils.config.MapReduceConfig
import cs441.project.cloudsim.utils.{DataCenterUtils, SimulationStatistics}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}

import scala.collection.JavaConverters._

/**
  * Simulates a bunch of different cloud architectures and runs several types of jobs on them.
  *
  * Each job is provided with its own broker and is responsible for creating the required virtual machines and
  * submitting cloudlets to them along with the desired scheduling, load-balancing, and auto-scaling policies. The
  * results of running these jobs are printed to the console once the simulation completes for each cloud architecture.
  */
object SimulationDriver extends LazyLogging {

  /**
    * Entry point for the driver program.
    *
    * @param args Command-line arguments to the program.
    */
  def main(args: Array[String]): Unit = {

    // Load the main config file that defines the cloud architectures that will be simulated
    val config = ConfigFactory.load()
    val cloudArchitectures = config.getConfigList("architectures").asScala


    // Simulate the different cloud architectures and run jobs on them
    cloudArchitectures.foreach { architectureConfig =>

      val architectureName = architectureConfig.getString("name")
      val iterations = architectureConfig.getInt("iterations")
      logger.info(s"Starting simulation for architecure: $architectureName")
      logger.info(s"The simulation will run for $iterations iterations")

      // Get the load balancer to use from the config
      val loadBalancer = architectureConfig.getString("load-balancer")
      logger.info(s"Simulation will use load balancer: $loadBalancer")

      val statsCollection = (1 to iterations).flatMap { i =>
        logger.info(s"Initializing simulation for iteration: $i")

        // Create a new simulation
        val simulation = new CloudSim()

        // Initialize the data centers for this simulation
        val dataCenterConfigList = DataCenterUtils.loadDataCenterConfigList(architectureConfig)
        val dataCenters = DataCenterUtils.createDataCenters(
          simulation,
          dataCenterConfigList
        )

        // Load the jobs that will be run on each cloud architecture
        val jobs: List[Job] =
          (1 to MapReduceConfig.getNumberOfJobs).map(_ => new ResourceManager)
            .toList

        // Submit the different jobs by creating a new broker for each job
        val brokers = jobs.map(submitJob(_, simulation, loadBalancer))
        logger.info("Submitted creation of VMs and Cloudlets for all jobs.")

        // Run the simulation and print the results
        logger.info("Starting simulation...")
        simulation.start()
        logger.info("Simulation completed.")
        printResults(brokers, architectureName, i, iterations)
      }

      // Collect statistics and print them
      val stats = consolidateStatistics(statsCollection)
      stats.foreach { case (job, jobStats) =>
        logger.info(s"Statistics for job: $job")
        println(jobStats)
      }
    }
  }

  /**
    * Creates a new [[DatacenterBroker]] and submits the VMs and Cloudlets provided by the job to the broker.
    *
    * @param job        The [[Job]] to run in the data centers.
    * @param simulation The [[CloudSim]] simulation in which this job should run.
    * @return The [[DatacenterBroker]] that was created for this job.
    */
  def submitJob(job: Job, simulation: CloudSim, loadBalancer: String): DatacenterBroker = {

    // Create a new broker for this job
    val broker = loadBalancer match {
      case "MaxMin" => new DatacenterBrokerMaxMin(simulation)
      case "MinMin" => new DatacenterBrokerMinMin(simulation)
      case "Default" | _ => new DatacenterBrokerSimple(simulation)
    }
    broker.setName(broker.getName + job.getClass.getSimpleName)

    // Initialize the job
    // TODO: Figure out proper way to send configId
    job.setSimulation(0, broker, simulation)

    // Fetch the VMs and Cloudlets
    val vmList = job.getVmList
    val cloudletList = job.getCloudletList

    // Spin up the VMs and submit the cloudlets to them
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)

    // Return the broker
    broker
  }

  /**
    * Prints the simulation result.
    *
    * @param brokers List of brokers in the simulation.
    */
  def printResults(brokers: List[DatacenterBroker], architectureName: String, iteration: Int, totalIterations: Int): List[SimulationStatistics] = {
    brokers.map { broker =>
      val cloudletList = broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]].asScala

      new CloudletsTableBuilder(cloudletList.asJava)
        .setTitle(s"SIMULATION RESULTS: ${broker.getName}")
        .addColumn(new TextTableColumn("CPU Cost", "USD"), cloudlet => "$ %.2f".format(cloudlet.getCostPerSec * cloudlet.getActualCpuTime))
        .addColumn(new TextTableColumn("Bandwidth Cost", "USD"), cloudlet => "$ %.2f".format(cloudlet.getAccumulatedBwCost))
        .addColumn(new TextTableColumn("Total Cost", "USD"), cloudlet => "$ %.2f".format(cloudlet.getTotalCost))
        .build()

      // Collect results
      val stats = SimulationStatistics(
        broker.getName,
        architectureName,
        iteration,
        cloudletList.map(_.getTotalLength).sum / cloudletList.size,
        cloudletList.map(cloudlet => cloudlet.getFinishTime - cloudlet.getExecStartTime).sum / cloudletList.size,
        cloudletList.map(cloudlet => cloudlet.getActualCpuTime * cloudlet.getCostPerSec).sum / cloudletList.size,
        cloudletList.map(_.getAccumulatedBwCost).sum / cloudletList.size,
        cloudletList.map(_.getTotalCost).sum / cloudletList.size,
        totalIterations
      )
      logger.info(s"Statistics for ${stats.jobName} for iteration $iteration:")
      println(stats)
      stats
    }
  }

  /**
    * Computes the average of values from a list of simulation statistics collected over multiple iterations. The
    * results are grouped by the job name.
    *
    * @param statsCollection Collection of [[SimulationStatistics]] collected over multiple runs of various jobs.
    * @return Map with job's name as the key and its consolidated execution statistics as the value.
    */
  def consolidateStatistics(statsCollection: Seq[SimulationStatistics]): Map[String, SimulationStatistics] = {
    statsCollection
      .groupBy(_.jobName.split("\\d").last)
      .mapValues(statsList => {
        SimulationStatistics(
          statsList.head.jobName,
          statsList.head.cloudArchitecture,
          0,
          statsList.map(_.averageCloudletLength).sum / statsList.size,
          statsList.map(_.averageExecutionTime).sum / statsList.size,
          statsList.map(_.averageCPUCost).sum / statsList.size,
          statsList.map(_.averageBandwidthCost).sum / statsList.size,
          statsList.map(_.averageTotalCost).sum / statsList.size,
          0
        )
      })
  }
}
