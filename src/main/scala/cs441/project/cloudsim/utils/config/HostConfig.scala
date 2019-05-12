package cs441.project.cloudsim.utils.config

import scala.beans.BeanProperty

/**
  * Java Bean compatible class that holds the configuration for a single type of host machine in the data center.
  */
class HostConfig {
  /**
    * Number of host machines present in the data center with this configuration.
    */
  @BeanProperty var number: Int = _
  /**
    * The RAM (in MB) present in this type of host machine.
    */
  @BeanProperty var ram: Long = _
  /**
    * The disk storage size (in MB) attached to this type of host machine.
    */
  @BeanProperty var storage: Long = _
  /**
    * The banddwidth (in Mbps - Mega bits per second) of this type of host machine
    */
  @BeanProperty var bandwidth: Long = _
  /**
    * The processing power (in Million Instructions Per Second) of each core in this type of host machine.
    */
  @BeanProperty var mips: Double = _
  /**
    * The number of cores present in this type of host machine.
    */
  @BeanProperty var cores: Int = _
  /**
    * The policy used by this type of host machine for scheduling VMs.
    */
  @BeanProperty var vmScheduler: String = _

  override def toString: String = s"HostConfig(number: $number, ram: $ram, storage: $storage, bandwidth: $bandwidth, mips: $mips, cores: $cores, vmScheduler: $vmScheduler)"
}
