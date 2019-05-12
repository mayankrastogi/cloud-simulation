package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudbus.cloudsim.core.Simulation
import org.cloudsimplus.listeners.EventInfo

class Server(simulation: Simulation) {

  //To Read from Configuration File
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
    this.getVmList:::List(createListOfScalableVms(VM_SCALE_LIMIT))

    if(this.getVmList.size > VMS){
      return true
    }
    else{
      return false
    }
  }

  def onClockTickListener(evt: EventInfo): Unit={
      this.getVmList.foreach( vm =>{
          print("\t\tTime %6.1f: Vm %d CPU Usage: %6.2f%% (%2d vCPUs. Running Cloudlets: #%02d) Upper Threshold: %.2f History Entries: %d\n",
            evt.getTime,vm.getId,vm.getCpuPercentUsage*100.0,
            vm.getNumberOfPes,
            vm.getCloudletScheduler.getCloudletExecList.size(),
            vm.getPeVerticalScaling.getUpperThresholdFunction.apply(vm),
            vm.getUtilizationHistory.getHistory.size())
      }
      )
  }

  def createListOfScalableVms(vm_scale_limit: Int):List[Vm]={
    (1 to vm_scale_limit)
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