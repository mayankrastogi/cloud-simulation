package cs441.project.cloudsim.jobs.WebService

import cs441.project.cloudsim.utils.config.WebServiceConfigReader
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}

class Server(serviceID: Int) {


  val serviceConf = new WebServiceConfigReader(serviceID)

  var vmID = 1

  var activeInstances: List[Vm] = _

  def initiate_autoscale = {
    //Create list of scalable VMs

    this.getVmList.foreach(vm => {

      createHorizontalVmScaling(vm)
    })
  }

  def getVmList: List[Vm] = {
    activeInstances = (1 to serviceConf.VMS).map(x => new VmSimple(x, serviceConf.VM_MIPS, serviceConf.VM_PES).setBw(serviceConf.VM_BW).setRam(serviceConf.VM_RAM)
      .setSize(serviceConf.VM_SIZE)
      .setCloudletScheduler(new CloudletSchedulerTimeShared)
    ).toList
    activeInstances
  }


  /**
    * A {@link Predicate} that checks if a given VM is overloaded or not,
    * based on upper CPU utilization threshold.
    * A reference to this method is assigned to each {@link HorizontalVmScaling} created.
    *
    * @param vm the VM to check if it is overloaded
    * @return true if the VM is overloaded, false otherwise
    * @see #createHorizontalVmScaling(Vm)
    */
  private def isVmOverloaded(vm: Vm) = vm.getCpuPercentUsage > 0.7


  def createVm(): Vm = {
    vmID = vmID + 1
    return new VmSimple(vmID, serviceConf.VM_MIPS, serviceConf.VM_PES)
      .setBw(serviceConf.VM_BW)
      .setRam(serviceConf.VM_RAM)
      .setSize(serviceConf.VM_SIZE)
      .setCloudletScheduler(new CloudletSchedulerTimeShared)
  }

  /**
    * Creates a {@link HorizontalVmScaling} object for a given VM.
    *
    * @param vm the VM for which the Horizontal Scaling will be created
    * @see #createListOfScalableVms(int)
    */
  private def createHorizontalVmScaling(vm: Vm): Unit = {
    val horizontalScaling = new HorizontalVmScalingSimple
    horizontalScaling.setVmSupplier(() => createVm()).setOverloadPredicate(isVmOverloaded)
    vm.setHorizontalScaling(horizontalScaling)
  }
}
