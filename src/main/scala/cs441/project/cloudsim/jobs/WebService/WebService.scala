package cs441.project.cloudsim.jobs.WebService

import com.typesafe.config.{Config, ConfigFactory}
import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.vms.Vm

/**
  * Defines a Web service modelling real client requests varying in time and size.
  * The server autoScales based on the requests and handles the burstiness of the client traffic
  */

class WebService extends Job {

  var server:Server = _
  var clients:Client = _
  override def setSimulation(serviceId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {


    server = new Server(serviceId)
    clients = new Client(broker,serviceId)
//    this.configId = configId
//    this.broker_for_random = broker
//    this.simulation = simulation
//    this.configptr = config.getConfigList("WebServiceJob_Random").get(configId)
//    this.TIME_TO_TERMINATE_SIMULATION = configptr.getInt("TIME_TO_TERMINATE_SIMULATION")
//    this.client = new Client(broker, configptr) //Set Client
//    this.server = new Server(configptr) //Set Server
////    simulation.terminateAt(TIME_TO_TERMINATE_SIMULATION)

    //submit cloudlets on each clock tick of simulation using a Uniform distribution with a probabilty of 40%
    simulation.addOnClockTickListener(clients.submitRandomCloudLets)

    /*
     *  Autoscaling comes into motion here...
     *
     *  A feature of verticalCPU scaling according to the dynamic threshold of the Cloudlets.
     *  Every VM will check at specific time intervals if its PEs are under/over utilized according to a
     *  dynamic computed utilization threshold. Then it requests such PEs to be up or down scaled.
     */

     server.initiate_autoscale


  }

  override def getVmList: List[Vm] = {
    server.activeInstances
  }

  override def getCloudletList: List[Cloudlet] = {
    clients.getClientCloudletList
  }

}
