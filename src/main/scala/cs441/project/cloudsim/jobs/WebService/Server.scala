package cs441.project.cloudsim.jobs.WebService

import com.typesafe.config.Config
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}

class Server(config: Config) {

  //TO Read from Configuration File
  val VMS: Int = config.getInt("Vms.VMS_NUMBER")
  val VM_PES: Int = config.getInt("Vms.VM_PES")
  val VM_MIPS: Int = config.getInt("Vms.VM_MIPS")
  val VM_BW: Int = config.getInt("Vms.VM_BW")
  val VM_RAM: Int = config.getInt("Vms.VM_RAM")
  val VM_SIZE: Int = config.getInt("Vms.VM_SIZE")
  var vm_id: Int = VMS

  def initiate_autoscale(): Boolean = {
    //Create list of scalable VMs

    this.getVmList.foreach(vm => createHorizontalVmScaling(vm))

    return true
  }

  def getVmList: List[Vm] = {
    (1 to VMS)
      .map { x =>
        new VmSimple(x, VM_MIPS, VM_PES)
          .setBw(VM_BW)
          .setRam(VM_RAM)
          .setSize(VM_SIZE)
          .setCloudletScheduler(new CloudletSchedulerTimeShared)
      }
      .toList
  }

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
    vm_id = vm_id + 1
    return new VmSimple(vm_id, VM_MIPS, VM_PES)
      .setBw(VM_BW)
      .setRam(VM_RAM)
      .setSize(VM_SIZE)
      .setCloudletScheduler(new CloudletSchedulerTimeShared)
  }
}
