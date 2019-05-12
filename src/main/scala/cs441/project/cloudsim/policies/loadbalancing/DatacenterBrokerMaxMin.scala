package cs441.project.cloudsim.policies.loadbalancing

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

/**
  * A data center broker that uses Max-min load balancing algorithm to place Cloudlets on the available VMs.
  *
  * The algorithm prioritizes smaller cloudlets (ones with smaller estimated execution time) over larger cloudlets and
  * assigns them to VMs first.
  *
  * @param simulation The [[CloudSim]] instance that represents the simulation the Entity is related to.
  * @param name       Name of the broker.
  */
class DatacenterBrokerMaxMin(simulation: CloudSim, name: String) extends DatacenterBrokerSimple(simulation, name) {

  // Auxiliary constructor
  def this(simulation: CloudSim) {
    this(simulation, "")
  }

  /**
    * Sorts the list of cloudlets in the waiting queue by using the Max-min algorithm before requesting the data center
    * to create the cloudlets on VMs.
    */
  override def requestDatacentersToCreateWaitingCloudlets(): Unit = {
    // Sort the cloudlets in the waiting queue using Max-min algorithm
    val sortedCloudletList =
      mapCloudletsMaxMin(
        getCloudletWaitingList[Cloudlet].asScala.toList,
        getVmCreatedList[Vm].asScala.toList
      )

    // Replace the waiting queue with the sorted list
    getCloudletWaitingList.clear()
    getCloudletWaitingList[Cloudlet].addAll(sortedCloudletList.asJava)

    // Invoke the base class's method to continue requesting the data center for allocating VMs to the cloudlets
    super.requestDatacentersToCreateWaitingCloudlets()
  }

  /**
    * Sorts a list of cloudlets using the Max-min algorithm.
    *
    * @param cloudletList The list of cloudlets in the waiting queue.
    * @param vmList       The list of VMs created by the broker.
    * @return The sorted list of cloudlets in the waiting queue.
    */
  def mapCloudletsMaxMin(cloudletList: List[Cloudlet], vmList: List[Vm]): List[Cloudlet] = {
    cloudletList
      // Estimate minimum execution times for each cloudlet
      .map(estimateExecutionTimeAndGetMin(_, vmList))
      // Zip both the lists together
      .zip(cloudletList)
      // Sort the cloudlets in descending order of their estimated execution times
      .sortBy { case (executionTime, _) => -executionTime }
      // Return the sorted list of cloudlets
      .map { case (_, cloudlet) => cloudlet }
  }

  /**
    * Estimates the minimum time it will take to execute the given cloudlet on any of the available VMs that are
    * suitable for running the cloudlet.
    *
    * @param cloudlet The cloudlet for which the execution time is to be estimated.
    * @param vmList   The list of VMs created by the broker.
    * @return The minimum estimated execution time.
    */
  def estimateExecutionTimeAndGetMin(cloudlet: Cloudlet, vmList: List[Vm]): Double = {
    // Get list of VMs created by the broker
    vmList
      // Filter out VMs where the cloudlet cannot be executed
      .withFilter(_.isSuitableForCloudlet(cloudlet))
      // Estimate the execution time
      .map(vm => cloudlet.getTotalLength / (vm.getTotalMipsCapacity - vm.getTotalCpuMipsUsage))
      // Find the minimum value
      .min
  }
}
