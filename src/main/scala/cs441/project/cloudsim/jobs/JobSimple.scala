package cs441.project.cloudsim.jobs

import cs441.project.cloudsim.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, NetworkCloudlet}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

/**
  * A simple job for testing the driver program.
  */
class JobSimple extends Job {

  private var configId: Int = 0
  private var broker: DatacenterBroker = _

  override def setSimulation(configId: Int, broker: DatacenterBroker): Unit = {
    this.configId = configId
    this.broker = broker
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
