package cs441.project.cloudsim.policies.allocation

import java.util.Optional

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

/**
  * A VM allocation policy that tries to place VMs which are closest to VMs previously submitted by the new VM's broker.
  *
  * It first checks whether the new VM can be placed within the same host as the last VM. If not, it tries to place the
  * new VM in the same rack (hosts connected to the same TOR switch) as the last VM. If it fails to do so, it searches
  * for a suitable host in the same row (hosts connected to the same aggregate switch) as the last VM. If still no
  * suitable host could be found, it searches the rest of the data center for a suitable host using "First Fit" policy.
  *
  * NOTE: This policy only works with [[NetworkHost]]s. For other kind of hosts, it falls back to "First Fit" policy.
  * Another caveat is that VM's submitted in bulk (without any delay) will always result in the "First Fit" policy to be
  * used, since the list of VM's submitted by the broker doesn't get populated until all VMs submitted at once have been
  * provisioned by the data center.
  */
class VmAllocationPolicyNearestHost extends VmAllocationPolicyAbstract {

  /**
    * Places the provided VM in the closest suitable host to the last VM submitted by the new VM's broker.
    *
    * @param vm The new VM to be placed in a host.
    * @return The host where the VM should be placed.
    */
  override def defaultFindHostForVm(vm: Vm): Optional[Host] = {

    // Find VMs already created by this VM's broker
    val relatedVMs = vm.getBroker.getVmExecList[Vm].asScala

    // Select a host for placing the VM
    val selectedHost =
      if (relatedVMs.nonEmpty) {
        // Select the first VM submitted by the broker and find the host machine it is running on
        relatedVMs.last.getHost match {
          // If the host is a NetworkHost, find the closest host available that can host the VM
          case nearestHost: NetworkHost => findNearestHostSuitableForVm(vm, nearestHost)
          // Fall-back to First Fit policy for other types of hosts
          case _ => findFirstFitHost(vm)
        }
      }
      else {
        // No VM was created by the broker before, so find a suitable host using First Fit policy
        findFirstFitHost(vm)
      }

    // Wrap the host in Java Optional
    Optional.ofNullable(selectedHost.orNull)
  }

  /**
    * Searches the network for the closest host suitable for placing the VM.
    *
    * @param vm         The VM that needs to be placed in a host.
    * @param sourceHost The host machine from where to begin the search.
    * @return The suitable host for placing the provided VM.
    */
  private def findNearestHostSuitableForVm(vm: Vm, sourceHost: NetworkHost): Option[Host] = {
    // If the source host can accommodate this VM then select it
    if (sourceHost.isSuitableForVm(vm)) {
      Some(sourceHost)
    }
    // Otherwise, keep searching
    else {
      // Search for a host that is connected to the same edge switch as the source host
      val nearestEdgeSwitch = sourceHost.getEdgeSwitch
      val hostInSameRack = findFirstFitHost(
        vm,
        nearestEdgeSwitch.
          getHostList
          .asScala
          .toList
          .filterNot(_ == sourceHost)
      )
      // If a host is found, return it, otherwise keep searching
      hostInSameRack.orElse {
        // Find the other edge switches connected to the source host's parent aggregate switch, i.e. the same row
        val nearestAggregateSwitch = nearestEdgeSwitch.getUplinkSwitches.asScala.headOption
        if (nearestAggregateSwitch.isDefined) {
          // Find a suitable host in the rest of the racks
          val edgeSwitchesInRow = nearestAggregateSwitch.get.getDownlinkSwitches.asScala.toList.filterNot(_ == nearestEdgeSwitch)
          val hostsInRow = edgeSwitchesInRow.flatMap { switch =>
            switch.asInstanceOf[EdgeSwitch].getHostList.asScala
          }
          val suitableHost = findFirstFitHost(vm, hostsInRow)

          // If a free host was found, return it, otherwise fall-back to First Fit policy to find a host in the rest of
          // the data center
          suitableHost.orElse(findFirstFitHost(vm, getHostList[Host].asScala.toList.filterNot(hostsInRow.contains)))
        }
        else {
          // An aggregate switch was not found, so there must be a problem with the network configuration
          None
        }
      }
    }
  }

  /**
    * Finds a suitable host from the provided list of hosts for placing the provided VM using "First Fit" policy.
    *
    * @param vm       The VM that needs to be placed
    * @param hostList List of [[Host]]s for finding a suitable host. If omitted or `null`, all the hosts in the data
    *                 center are searched for a suitable host, essentially equivalent to using
    *                 [[org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit]]
    * @return A host suitable for placing the VM.
    */
  private def findFirstFitHost(vm: Vm, hostList: List[Host] = null): Option[Host] = {
    val searchList = if (hostList == null) getHostList[Host].asScala else hostList
    searchList.find(_.isSuitableForVm(vm))
  }
}
