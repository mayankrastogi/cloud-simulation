package cs441.project.cloudsim.jobs

import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, NetworkCloudlet}
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

/**
  * A simple job for testing the driver program.
  */
class JobSimple extends Job {

  private var configId: Int = 0
  private var broker: DatacenterBroker = _
  private var simulation: Simulation = _

  override def setSimulation(configId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    this.configId = configId
    this.broker = broker
    this.simulation = simulation
  }

  override def getVmList: List[Vm] = {
    (1 to 5)
      .map { _ =>
        new VmSimple(1000, 2)
          .setBw(10000)
          .setRam(1024)
          .setSize(100)
      }
      .toList
  }

  override def getCloudletList: List[Cloudlet] = {
    (1 to 20)
      .map { _ =>
        val cloudlet = new NetworkCloudlet(10000, 2)
        Seq(new CloudletExecutionTask(0, 10000)).foreach(cloudlet.addTask)
        cloudlet
      }
      .toList
  }
}
