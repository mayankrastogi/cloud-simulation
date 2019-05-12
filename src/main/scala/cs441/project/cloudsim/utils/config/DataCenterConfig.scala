package cs441.project.cloudsim.utils.config

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  * Java Bean compatible class that holds the configuration for a single data center.
  */
class DataCenterConfig {

  /**
    * A data center may have host machines with heterogeneous configurations. Each object in the hosts list denotes
    * a specific configuration of host machines, along with the number of machines of this configuration that are
    * present in this data center.
    */
  var hosts: List[HostConfig] = _
  /**
    * Policy of the data center for allocating hosts to virtual machines.
    */
  @BeanProperty var vmAllocationPolicy: String = _
  /**
    * Monetary cost per second for using a CPU in the data center.
    */
  @BeanProperty var costPerSecond: Double = _
  /**
    * Monetary cost per Megabyte of RAM usage in the data center.
    */
  @BeanProperty var costPerMemory: Double = _
  /**
    * Monetary cost per Megabyte of file storage in the data center.
    */
  @BeanProperty var costPerStorage: Double = _
  /**
    * Monetary cost per Megabit of bandwidth in the data center.
    */
  @BeanProperty var costPerBandwidth: Double = _
  /**
    * Configuration for all the [[org.cloudbus.cloudsim.network.switches.RootSwitch]]es in the data center.
    */
  @BeanProperty var rootSwitches: SwitchConfig = _
  /**
    * Configuration for all the [[org.cloudbus.cloudsim.network.switches.AggregateSwitch]]es in the data center.
    */
  @BeanProperty var aggregateSwitches: SwitchConfig = _
  /**
    * Configuration for all the [[org.cloudbus.cloudsim.network.switches.EdgeSwitch]]es in the data center.
    */
  @BeanProperty var edgeSwitches: SwitchConfig = _

  /**
    * Getter for Java compatible list of [[hosts]].
    *
    * @return The Java compatible list of [[HostConfig]]s.
    */
  def getHosts: java.util.List[HostConfig] = hosts.asJava

  /**
    * Setter for Java compatible list of [[hosts]]
    *
    * @param hosts The Java compatible list of [[HostConfig]]s.
    */
  def setHosts(hosts: java.util.List[HostConfig]): Unit = {
    this.hosts = hosts.asScala.toList
  }

  override def toString: String = s"DataCenterConfig(hosts: $hosts, vmAllocationPolicy: $vmAllocationPolicy, costPerSecond: $costPerSecond, costPerMemory: $costPerMemory, costPerStorage: $costPerStorage, costPerBandwidth: $costPerBandwidth, rootSwitches: $rootSwitches, aggregateSwitches: $aggregateSwitches, edgeSwitches: $edgeSwitches)"
}
