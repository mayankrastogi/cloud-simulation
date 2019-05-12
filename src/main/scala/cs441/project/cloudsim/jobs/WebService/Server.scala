package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple

import scala.collection.JavaConverters._
import org.cloudbus.cloudsim.core.Simulation
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}
import org.cloudsimplus.listeners.EventInfo

class Server(broker: DatacenterBroker) {

  //To Read from Configuration File
  var VMS: Int = 10
  var VM_PES: Int = 4
  var VM_SCALE_LIMIT: Int = 5
  var vm_id: Int = 0

  def getVmList(): List[Vm] = {
    (1 to VMS).map(x => new VmSimple(x, 1000, VM_PES).
      setBw(5000).setRam(1024).setSize(1000).
      setCloudletScheduler(new CloudletSchedulerTimeShared)).toList
  }

  def initiate_autoscale(): Boolean = {
    //Create list of scalable VMs

    this.getVmList.foreach(vm => createHorizontalVmScaling(vm))

    return true
  }

  /*def onClockTickListener(evt: EventInfo): Unit={
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

  def createListOfScalableVms(vm_scale_limit: Int):List[Vm]= {
    (1 to vm_scale_limit).map { _ =>
      vm_id = vm_id + 1
      var vm: Vm = new VmSimple(vm_id, 1000, VM_PES)
        .setBw(10000)
        .setRam(1024)
        .setSize(100)
        .setCloudletScheduler(new CloudletSchedulerTimeShared)
      createHorizontalVmScaling(vm)
      new_vm_list:::List(vm)
    }

    return new_vm_list
  }*/

  def createHorizontalVmScaling(vm: Vm): Unit = {
    var horizontalScaling: HorizontalVmScaling = new HorizontalVmScalingSimple()
    horizontalScaling.setVmSupplier(() => createVm).setOverloadPredicate(vm => isVmOverloaded(vm))
    vm.setHorizontalScaling(new HorizontalVmScalingSimple)
  }

  def isVmOverloaded(vm: Vm): Boolean = {
    if (vm.getCpuPercentUsage > 0.7) {
      return true
    }
    else {
      return false
    }
  }

  def createVm(): Vm = {
    vm_id = VMS + 1
    return new VmSimple(vm_id, 1000, VM_PES)
      .setBw(10000)
      .setRam(1024*2)
      .setSize(500)
      .setCloudletScheduler(new CloudletSchedulerTimeShared)
  }
}
