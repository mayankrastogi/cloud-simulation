package cs441.project.cloudsim.jobs.WebService

import com.typesafe.config.{Config, ConfigFactory}
import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.vms.Vm

/*
*   This Class Defines a Web-Service Job having a Client-Server Model.
*
*/

class WebServiceJob_Random extends Job {

  var configptr: Config = _
  private var configId: Int = _
  private var broker_for_random: DatacenterBroker = _
  private var simulation: Simulation = _
  private var client: Client = _
  private var server: Server = _
  private var config = ConfigFactory.load("WebService")
  private var TIME_TO_TERMINATE_SIMULATION: Int = _

  override def setSimulation(configId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    this.configId = configId
    this.broker_for_random = broker
    this.simulation = simulation
    this.configptr = config.getConfigList("WebServiceJob_Random").get(configId)
    this.TIME_TO_TERMINATE_SIMULATION = configptr.getInt("TIME_TO_TERMINATE_SIMULATION")
    this.client = new Client(broker, configptr) //Set Client
    this.server = new Server(configptr) //Set Server
    simulation.terminateAt(TIME_TO_TERMINATE_SIMULATION)

    //submit cloudlets on each clock tick of simulation using a Uniform distribution with a probabilty of 40%
    simulation.addOnClockTickListener(client.submitRandomCloudelts)

    /*
     *  Autoscaling comes into motion here...
     *
     *  A feature of verticalCPU scaling according to the dynamic threshold of the Cloudlets.
     *  Every VM will check at specific time intervals if its PEs are under/over utilized according to a
     *  dynamic computed utilization threshold. Then it requests such PEs to be up or down scaled.
     */

    var flag: Boolean = server.initiate_autoscale()

    if (flag.equals(true)) {
      print("Auto Scaling Enabled..!!")
    }

  }

  override def getVmList: List[Vm] = {
    server.getVmList
  }

  override def getCloudletList: List[Cloudlet] = {
    client.getCloudletList()
  }

  object WebServiceConfig {
    def getWebServiceConfig(): Int = {
      val configList = config.getConfigList("WebServiceJob_Random")
      configList.size()
    }

  }

}
