package cs441.project.cloudsim.utils.config

import scala.beans.BeanProperty

/**
  * Java Bean compatible class that holds the configuration for a single type of switch in the data center.
  */
class SwitchConfig {
  /**
    * Number of switches of this type present in the data center.
    */
  @BeanProperty var number: Int = _
  /**
    * Number of ports present in this type of switch.
    */
  @BeanProperty var ports: Int = _
  /**
    * The up-link and down-link bandwidths of this type of switch.
    */
  @BeanProperty var bandwidth: Int = _
  /**
    * The latency of this type of switch to process a packet before forwarding it up/down the link.
    */
  @BeanProperty var switchingDelay: Int = _

  override def toString: String = s"SwitchConfig(number: $number, ports: $ports, bandwidth: $bandwidth, switchingDelay: $switchingDelay)"
}
