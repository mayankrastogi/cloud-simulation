package cs441.project.cloudsim.jobs.mapreduce

import com.typesafe.config.ConfigFactory
import cs441.project.cloudsim.SimulationDriver.printResults
import cs441.project.cloudsim.jobs.Job
import cs441.project.cloudsim.utils.DataCenterUtils
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters._


object MapReduceApplication extends App {

  val config = ConfigFactory.load()
  val cloudArchitectures = config.getConfigList("architectures").asScala
  cloudArchitectures.foreach { architectureConfig =>

    // Create a new simulation
    val simulation = new CloudSim()

    // Initialize the data centers for this simulation
    val dataCenterConfigList = DataCenterUtils.loadDataCenterConfigList(architectureConfig)
    val dataCenters = DataCenterUtils.createDataCenters(
      simulation,
      dataCenterConfigList,
      // TODO: Make VmAllocationPolicy configurable in the data center config
    )
    val broker = new DatacenterBrokerSimple(simulation)
    val myJob: Job = new ResourceManager

    myJob.setSimulation(0,broker,simulation)

    simulation.start()

    new CloudletsTableBuilder(broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]])
      .setTitle(s"SIMULATION RESULTS: ${broker.getName}")
      .build()
  }

}
