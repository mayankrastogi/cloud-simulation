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
    this.server = new Server()  //Set Server

    //broker_for_random.submitCloudletList(client.getCloudletList().asJava) //Submit initial Cloudlet list before start of the simulation
    //broker_for_random.submitVmList(server.getVmList.asJava) //Submit VM list before start of the simulation

    simulation.terminateAt(TIME_TO_TERMINATE_SIMULATION)

    //submit cloudlets on each clock tick of simulation using a Uniform distribution with a probabilty of 40%
    simulation.addOnClockTickListener(client.submitRandomCloudelts)

    /*Autoscaling comes into motion here*/
  }

  override def getVmList: List[Vm] = {
    server.getVmList
 }

  override def getCloudletList: List[Cloudlet] = {
    client.getCloudletList()
  }

}