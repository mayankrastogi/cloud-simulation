package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple

class Server {

  //TO Read from Configuration File
  var VMS:Int = 10
  var VM_PES: Int = 4

  def getVmList: List[Vm] = {
    (1 to VMS)
      .map { _ =>
        new VmSimple(1000, VM_PES)
          .setBw(10000)
          .setRam(1024)
          .setSize(100)
          .setCloudletScheduler(new CloudletSchedulerTimeShared)
      }
      .toList
  }

}
