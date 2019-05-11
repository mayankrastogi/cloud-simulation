package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple

class Server {

  var VMS:Int = 10
  var VM_PES: Int = 4

  def VmList: List[Vm] = {
    (1 to VMS)
      .map { _ =>
        new VmSimple(1000, VM_PES)
          .setBw(10000)
          .setRam(1024)
          .setSize(100)
      }
      .toList
  }

}
