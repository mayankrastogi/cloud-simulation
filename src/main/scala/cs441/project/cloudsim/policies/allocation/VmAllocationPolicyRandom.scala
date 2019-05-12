package cs441.project.cloudsim.policies.allocation

import java.util.Optional

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Defines a static VM allocation policy to randomly select a suitable Host to place a given VM.
  */
class VmAllocationPolicyRandom extends VmAllocationPolicyAbstract {

  /**
    * Randomly selects a host for placing the VM.
    *
    * @param vm The VM to find a suitable host for
    * @return An [[Optional]] containing a suitable Host to place the VM or an empty [[Optional]] if no suitable Host
    *         was found
    */
  override def defaultFindHostForVm(vm: Vm): Optional[Host] = {
    val hostList =
      getHostList[Host]
        .asScala
        .filter(_.isSuitableForVm(vm))

    Optional.ofNullable(
      if (hostList.nonEmpty) {
        Random.shuffle(hostList).headOption.orNull
      }
      else {
        null
      }
    )
  }
}
