package cs441.project.cloudsim.policies.allocation

import java.util.Optional

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract
import org.cloudbus.cloudsim.distributions.UniformDistr
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

/**
  * Defines a static VM allocation policy to randomly select a suitable Host to place a given VM.
  *
  * It generates a random number from a uniform distribution between [-1..1] to randomly select a host which can
  * accommodate the given VM.
  */
class VmAllocationPolicyRandom extends VmAllocationPolicyAbstract {

  /**
    * Random number generator from a Uniform Distribution for generating values between [-1, 1]
    */
  private val random = new UniformDistr(-1, 2)

  /**
    * Randomly selects a host for placing the VM.
    *
    * @param vm The VM to find a suitable host for
    * @return An [[Optional]] containing a suitable Host to place the VM or an empty [[Optional]] if no suitable Host
    *         was found
    */
  override def defaultFindHostForVm(vm: Vm): Optional[Host] = {
    Optional.ofNullable(
      getHostList[Host]
        .asScala
        .toStream
        .filter(_.isSuitableForVm(vm))
        .sortBy(_ => random.sample())
        .headOption
        .orNull
    )
  }
}
