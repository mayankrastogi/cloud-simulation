package cs441.project.cloudsim.jobs.WebService

import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import scala.collection.JavaConverters._

/*
*   This Class Defines a Web-Service Job having a Client-Server Model.
*
*/

class WebServiceJob_Random extends Job {

  private var configId: Int = 1
  private var broker_for_random: DatacenterBroker = _
  private var simulation: Simulation = _
  private var client: Client = _
  private var server: Server = _
  private var TIME_TO_TERMINATE_SIMULATION: Int = 60

  override def setSimulation(configId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    this.configId = configId
    this.broker_for_random = broker
    this.simulation = simulation

    this.client = new Client(broker)  //Set Client
    this.server = new Server(simulation)  //Set Server
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

    if(flag.equals(true)){
      print("Auto Scaling Enabled..!!")
    }

  }

  override def getVmList: List[Vm] = {
    server.getVmList
 }

  override def getCloudletList: List[Cloudlet] = {
    client.getCloudletList()
  }
  
}