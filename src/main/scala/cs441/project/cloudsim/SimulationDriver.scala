package cs441.project.cloudsim

import com.typesafe.config.ConfigFactory
import cs441.project.cloudsim.jobs.{Job, JobSimple}
import cs441.project.cloudsim.policies.loadbalancing.DatacenterBrokerMaxMin
import cs441.project.cloudsim.utils.DataCenterUtils
import org.cloudbus.cloudsim.brokers.DatacenterBroker
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
object SimulationDriver {

  /**
    * Entry point for the driver program.
    *
    * @param args Command-line arguments to the program.
    */
  def main(args: Array[String]): Unit = {

    // Load the main config file that defines the cloud architectures that will be simulated
    val config = ConfigFactory.load()
    val cloudArchitectures = config.getConfigList("architectures").asScala

    // Load the jobs that will be run on each cloud architecture
    // TODO: Update jobs list, possibly make it dynamic
    val jobs: List[Job] = List(new JobSimple)

    // Simulate the different cloud architectures and run jobs on them
    cloudArchitectures.foreach { architectureConfig =>

      // Create a new simulation
      val simulation = new CloudSim()

      // Initialize the data centers for this simulation
      val dataCenterConfigList = DataCenterUtils.loadDataCenterConfigList(architectureConfig)
      val dataCenters = DataCenterUtils.createDataCenters(
        simulation,
        dataCenterConfigList
      )

      // Submit the different jobs by creating a new broker for each job
      val brokers = jobs.map(submitJob(_, simulation))

      // Run the simulation and print the results
      simulation.start()
      printResults(brokers)
    }
  }

  /**
    * Creates a new [[DatacenterBroker]] and submits the VMs and Cloudlets provided by the job to the broker.
    *
    * @param job        The [[Job]] to run in the data centers.
    * @param simulation The [[CloudSim]] simulation in which this job should run.
    * @return The [[DatacenterBroker]] that was created for this job.
    */
  def submitJob(job: Job, simulation: CloudSim): DatacenterBroker = {

    // Create a new broker for this job
    val broker = new DatacenterBrokerMaxMin(simulation)

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
  def printResults(brokers: List[DatacenterBroker]): Unit = {
    brokers.foreach { broker =>
      new CloudletsTableBuilder(broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]])
        .setTitle(s"SIMULATION RESULTS: ${broker.getName}")
        .addColumn(new TextTableColumn("CPU Cost", "USD"), cloudlet => "$%.2f".format(cloudlet.getCostPerSec * cloudlet.getActualCpuTime))
        .addColumn(new TextTableColumn("Bandwidth Cost", "USD"), cloudlet => "$%.2f".format(cloudlet.getAccumulatedBwCost))
        .addColumn(new TextTableColumn("Total Cost", "USD"), cloudlet => "$%.2f".format(cloudlet.getTotalCost))
        .build()
    }
  }
}
