package cs441.project.cloudsim.utils

import java.io.InvalidClassException

import com.typesafe.config.{Config, ConfigBeanFactory}
import com.typesafe.scalalogging.LazyLogging
import cs441.project.cloudsim.utils.config.{DataCenterConfig, HostConfig, SwitchConfig}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.{AggregateSwitch, EdgeSwitch, RootSwitch, Switch}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared}

import scala.collection.JavaConverters._

/**
  * Utility object for creating data centers, its network, hosts, and processors.
  */
object DataCenterUtils extends LazyLogging {

  /**
    * Loads the configuration of data centers present in a cloud architecture configuration.
    *
    * @param config A cloud architecture [[Config]] containing the key "data-centers".
    * @return A list of [[DataCenterConfig]]s for each data center in the cloud architecture.
    */
  def loadDataCenterConfigList(config: Config): List[DataCenterConfig] = {
    logger.trace(s"loadDataCenterConfigList(config: $config)")

    config
      .getConfigList("data-centers")
      .asScala
      // Create instance of DataCenterConfig from the raw Config object
      .map(ConfigBeanFactory.create(_, classOf[DataCenterConfig]))
      .toList
  }

  /**
    * Creates data centers with the provided configuration and associates them with the provided simulation.
    *
    * The number of data centers created is equal to the number of config objects present in the provided list.
    *
    * @param simulation           The [[CloudSim]] simulation that this data center is part of.
    * @param dataCenterConfigList A list of [[DataCenterConfig]]s.
    * @return List of [[Datacenter]]s with the supplied configuration.
    */
  def createDataCenters(simulation: CloudSim, dataCenterConfigList: List[DataCenterConfig]): List[Datacenter] = {
    logger.trace(s"createDataCenters(simulation: $simulation, dataCenterConfigList: $dataCenterConfigList)")

    dataCenterConfigList.map { config =>
      // Create the host machines
      val hostList = createHosts(config.hosts)

      // Create the data center
      val dataCenter = new NetworkDatacenter(
        simulation,
        hostList.asJava,
        createVmAllocationPolicy(config.vmAllocationPolicy)
      )

      // Set data center characteristics
      dataCenter
        .getCharacteristics
        .setCostPerSecond(config.costPerSecond)
        .setCostPerMem(config.costPerMemory)
        .setCostPerStorage(config.costPerStorage)
        .setCostPerBw(config.costPerBandwidth)

      // Set up the network in the data center
      createNetwork(
        simulation,
        dataCenter,
        config.edgeSwitches,
        config.aggregateSwitches,
        config.rootSwitches
      )

      // Collect the data center
      dataCenter
    }
  }

  /**
    * Creates a set of host machines with the supplied list of configurations.
    *
    * Each [[HostConfig]] in the `hostConfigList` specifies a configuration of host machines and the number of machines
    * that should be created having that configuration.
    *
    * @param hostConfigList List of [[HostConfig]]s.
    * @return List of [[Host]]s with the provided configurations.
    */
  def createHosts(hostConfigList: List[HostConfig]): List[Host] = {
    logger.trace(s"createHosts(hostConfigList: $hostConfigList)")

    hostConfigList.flatMap { config =>
      (1 to config.number)
        .map { _ =>
          val peList = createCPUs(config.cores, config.mips)
          val host = new NetworkHost(config.ram, config.bandwidth, config.storage, peList.asJava)
          host.setVmScheduler(createVmScheduler(config.vmScheduler))
        }
    }
  }

  /**
    * Creates a list of [[Pe]]s with the provided processing power.
    *
    * @param noOfCPUs     Number of processors/cores needed.
    * @param mipsCapacity The processing power of each CPU (in MIPS - million instructions per second)
    * @return List of [[Pe]]s with the provided processing power.
    */
  def createCPUs(noOfCPUs: Int, mipsCapacity: Double): List[Pe] = {
    (1 to noOfCPUs)
      .map { _ =>
        new PeSimple(mipsCapacity)
      }
      .toList
  }

  /**
    * Creates a new instance of the specified VM scheduling policy.
    *
    * Supported policy names: [SpaceShared, TimeShared]. If an invalid policy name is passed, the default value
    * "TimeShared" will be used.
    *
    * @param name Name of the policy (defaults to "TimeShared").
    * @return Instance of the specified policy.
    */
  def createVmScheduler(name: String = "TimeShared"): VmScheduler = {
    name match {
      case "SpaceShared" => new VmSchedulerSpaceShared()
      case "TimeShared" => new VmSchedulerTimeShared()
      case _ =>
        logger.warn(s"Invalid VmScheduler: '$name'. Using 'TimeShared' instead.")
        new VmSchedulerTimeShared()
    }
  }

  /**
    * Creates a new instance of the specified VM allocation policy.
    *
    * Supported policy names are [BestFit, FirstFit, WorstFit]. If an invalid policy name is passed, the default value
    * "WorstFit" will be used.
    *
    * @param name Name of the policy (defaults to "WorstFit").
    * @return Instance of the specified policy.
    */
  def createVmAllocationPolicy(name: String = "WorstFit"): VmAllocationPolicy = {
    name match {
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "Simple" | "WorstFit" => new VmAllocationPolicySimple()
      case _ =>
        logger.warn(s"Invalid VmAllocationPolicy: '$name'. Using 'Simple' (WorstFit) instead.")
        new VmAllocationPolicySimple()
    }
  }

  /**
    * Creates internal network of the data center.
    *
    * Each host machine in the data center is connected to an edge switch. Each edge switch is connected to an
    * aggregate switch. All the aggregate switches are connected to a single root switch which connects the data center
    * with the external network.
    *
    * @param simulation            The [[CloudSim]] simulation that the data center is part of.
    * @param datacenter            The data center for which the network is to be created.
    * @param edgeSwitchConfig      The configuration of the edge switches.
    * @param aggregateSwitchConfig The configuration of the aggregate switches.
    * @param rootSwitchConfig      The configuration of the root switches.
    */
  def createNetwork(simulation: CloudSim, datacenter: NetworkDatacenter, edgeSwitchConfig: SwitchConfig, aggregateSwitchConfig: SwitchConfig, rootSwitchConfig: SwitchConfig): Unit = {
    logger.trace(s"createNetwork(simulation: $simulation, datacenter: $datacenter, edgeSwitchConfig: $edgeSwitchConfig, aggregateSwitchConfig: $aggregateSwitchConfig, rootSwitchConfig: $rootSwitchConfig)")

    // A data center network will have one root switch
    val rootSwitch = createSwitch[RootSwitch](
      simulation,
      datacenter,
      rootSwitchConfig.ports,
      rootSwitchConfig.bandwidth,
      rootSwitchConfig.switchingDelay
    )

    // Create and connect the aggregate switches
    val aggregateSwitches =
      (1 to aggregateSwitchConfig.number)
        .map { _ =>
          // Create the switch
          val aggregateSwitch = createSwitch[AggregateSwitch](
            simulation,
            datacenter,
            aggregateSwitchConfig.ports,
            aggregateSwitchConfig.bandwidth,
            aggregateSwitchConfig.switchingDelay
          )
          // Associate the switch with the data center
          datacenter.addSwitch(aggregateSwitch)

          // Connect the aggregate switch and the root switch with each other
          aggregateSwitch.getUplinkSwitches.add(rootSwitch)
          rootSwitch.getDownlinkSwitches.add(aggregateSwitch)

          // Collect the switch
          aggregateSwitch
        }

    // Create and connect the edge switches
    val edgeSwitches =
      (1 to edgeSwitchConfig.number)
        .map { i =>
          // Create the switch
          val edgeSwitch = createSwitch[EdgeSwitch](
            simulation,
            datacenter,
            edgeSwitchConfig.ports,
            edgeSwitchConfig.bandwidth,
            edgeSwitchConfig.switchingDelay
          )
          // Associate the switch with the data center
          datacenter.addSwitch(edgeSwitch)

          // Connect the edge switches with the appropriate aggregate switches and vice-versa
          val switchNum = i / aggregateSwitchConfig.ports
          edgeSwitch.getUplinkSwitches.add(aggregateSwitches(switchNum))
          aggregateSwitches(switchNum).getDownlinkSwitches.add(edgeSwitch)

          // Collect the switch
          edgeSwitch
        }

    // Connect the host machines in the data center with the appropriate edge switch
    datacenter.getHostList[NetworkHost].asScala.foreach { host =>
      val switchNum = getSwitchIndex(host, edgeSwitchConfig.ports)
      edgeSwitches(switchNum).connectHost(host)
    }
  }

  /**
    * Creates a [[Switch]] of the specified type with the provided configuration.
    *
    * @param simulation     The [[CloudSim]] simulation this switch is part of.
    * @param dataCenter     The data center which contains this switch.
    * @param ports          Number of ports in this switch.
    * @param bandwidth      The bandwidth (in Mbps) of this switch.
    * @param switchingDelay The switching delay of this switch.
    * @tparam T The type of this Switch. Should be one of [[RootSwitch]], [[AggregateSwitch]], or [[EdgeSwitch]].
    * @return A [[Switch]] of the specified type.
    */
  def createSwitch[T <: Switch : Manifest](simulation: CloudSim, dataCenter: NetworkDatacenter, ports: Int, bandwidth: Double, switchingDelay: Double): T = {
    // Get the reference to the generic type class
    implicit val cls: Class[_] = manifest[T].runtimeClass

    logger.trace(s"createSwitch[${cls.getName}](simulation: $simulation, dataCenter: $dataCenter, ports: $ports, bandwidth: $bandwidth, switchingDelay: $switchingDelay)")

    // Instantiate the appropriate switch
    val switch = (
      if (is[RootSwitch]) {
        new RootSwitch(simulation, dataCenter)
      }
      else if (is[AggregateSwitch]) {
        new AggregateSwitch(simulation, dataCenter)
      }
      else if (is[EdgeSwitch]) {
        new EdgeSwitch(simulation, dataCenter)
      }
      else {
        throw new InvalidClassException(s"${cls.getCanonicalName} is not a recognized type of switch. Should be one of org.cloudbus.cloudsim.network.switches.{RootSwitch, AggregateSwitch, EdgeSwitch}")
      })
      .asInstanceOf[T]

    // Configure the switch's properties
    switch.setPorts(ports)
    switch.setDownlinkBandwidth(bandwidth)
    switch.setUplinkBandwidth(bandwidth)
    switch.setSwitchingDelay(switchingDelay)

    // Return the switch
    switch
  }

  /**
    * Tests whether an implicit class is of the supplied generic class type.
    *
    * @param cls The expected class.
    * @tparam C The actual class.
    * @return Whether the expected class and the actual class are the same.
    */
  def is[C <: AnyRef : Manifest](implicit cls: Class[_]): Boolean = cls == manifest[C].runtimeClass

  /**
    * Gets the index of a switch where a Host will be connected,
    * considering the number of ports the switches have.
    * Ensures that each set of N Hosts is connected to the same switch
    * (where N is defined as the number of switch's ports).
    * Since the host ID is long but the switch array index is int,
    * the module operation is used safely convert a long to int
    * For instance, if the id is 2147483648 (higher than the max int value 2147483647),
    * it will be returned 0. For 2147483649 it will be 1 and so on.
    *
    * @param host        the Host to get the index of the switch to connect to
    * @param switchPorts the number of ports (N) the switches where the Host will be connected have
    * @return the index of the switch to connect the host
    */
  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = (host.getId % Int.MaxValue).toInt / switchPorts
}
