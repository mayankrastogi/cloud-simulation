package cs441.project.cloudsim.jobs

import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.vms.Vm

/**
  * Simulates a type of job - e.g. Map Reduce, Streaming, Web Server, etc.
  */
trait Job {

  /**
    * The config ID for a simulation of this job type.
    *
    * @param configId The ID representing the variation of this job type that is to be simulated.
    * @param broker   Reference to the datacenter broker that the job may use for dynamic creation/submission of
    *                 cloudlets and VMs.
    */
  def setSimulation(configId: Int, broker: DatacenterBroker): Unit

  /**
    * The VMs that this job needs for its execution.
    *
    * This method should create the necessary [[Vm]] objects along with the required configuration.
    *
    * @return List of VMs
    */
  def getVmList: List[Vm]

  /**
    * The cloudlets that this job needs to complete its execution.
    *
    * This method should create the necessary [[Cloudlet]]s along with the required configuration.
    *
    * @return List of Cloudlets
    */
  def getCloudletList: List[Cloudlet]
}
