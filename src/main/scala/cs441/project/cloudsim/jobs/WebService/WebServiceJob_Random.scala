package cs441.project.cloudsim.jobs.WebService

import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, NetworkCloudlet}
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

class WebServiceJob_Random extends Job {

  private var configId: Int = 1
  private var broker_for_random: DatacenterBroker = _
  private var simulation: Simulation = _
  private var client: Client = _
  private var server: Server = _

  override def setSimulation(configId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    this.configId = configId
    this.broker_for_random = broker
    this.simulation = simulation
  }

  override def getVmList: List[Vm] = {



  }

  override def getCloudletList: List[Cloudlet] = {

  }

}


