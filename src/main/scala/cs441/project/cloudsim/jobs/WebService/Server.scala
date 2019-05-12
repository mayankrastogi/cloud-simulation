package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudbus.cloudsim.core.Simulation
import org.cloudsimplus.listeners.EventInfo

class Server(simulation: Simulation) {

  //TO Read from Configuration File
  var VMS:Int = 10
  var VM_PES: Int = 4
  var VM_SCALE_LIMIT: Int = 5

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

  def initiate_autoscale():Boolean={
    //Create list of scalable VMs

    simulation.addOnClockTickListener(onClockTickListener)


  }

  def onClockTickListener(evt: EventInfo): Unit={

  }

}
