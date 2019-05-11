package cs441.project.cloudsim.mapreduce

import com.typesafe.config.Config
import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.network.NetworkVm

/**
  * Models a resource manager, which takes in the Job requests allocated/schedules the jobs among the different available nodes.
  *
  */

class ResourceManager extends Job {


  //  val reducer = new Reducer
  val MAPPER_PES = 2
  val REDUCER_PES = 2

  var simulation: Simulation = _

  var mappers: List[Mapper] = _
  var reducers: List[Reducer] = _

  def allocateMappers(numberOfMappers: Int, inputSplitSize: Int, mapperSize: Long): List[Mapper] = {
    (1 to numberOfMappers).map(iMapper => new Mapper(iMapper, inputSplitSize, MAPPER_PES).run(mapperSize)).toList

  }


  /**
    * Returns the associated mapper with given reducer Id
    *
    * @param iReducer the reducer ID
    * @return the mapper which emitted its input
    */
  def getAssociatedMapper(iReducer: Int): NetworkCloudlet = {
    mappers.filter(mapper => mapper.getMapperId == iReducer).head.getMapperCloudlet

  }

  /**
    * Returns the associated reducer with given mapper id
    *
    * @param iMapper the mapperID
    * @return the reducer who will take the given mapper's output
    */
  def getAssociateReducer(iMapper: Int): NetworkCloudlet = {
    reducers.filter(reducer => reducer.getReducerId == iMapper).head.getReducerCloudlet
  }

  def allocateReducers(numberOfReducers: Int, reducerLength: Long): List[Reducer] = {

    val NumberOfPackets = 20 // Todo get the right number of packets


    (1 to numberOfReducers).map(iReducer => new Reducer(iReducer, reducerLength, REDUCER_PES)).toList

  }


  def configureWorkerNodes(datacenterBroker: DatacenterBroker): List[NetworkVm] = {

    val numberOfWorkerNodes = ConfigLoader.NUMBER_OF_WORKER_NODES
    createAndSubmitVms(datacenterBroker, numberOfWorkerNodes)

  }

  /**
    * Receives the Job requests and schedules the job tasks among the worker nodes.
    *
    * @param jobConf Job config describing the hadoop job to run on the cluster
    */

  def receiveJobRequest(job: String, datacenterBroker: DatacenterBroker): Unit = {


    val inputFileSize = ConfigLoader.INPUT_FILE_SIZE
    val inputSplitSize = ConfigLoader.INPUT_SPLIT_SIZE

    // Decide on the number of mappers and reducers based on the Input file size and the split size

    val numberOfMappers, numberOfReducers = inputFileSize / inputSplitSize


    //Define the Vms/Resources on which mappers and reducers are simulated
    configureWorkerNodes(datacenterBroker)


    //TODO : Decide on the mapperSize and its relation with the inputFileSize
    val individualMapperSize = 1024

    // Allocate mappers job
    mappers = allocateMappers(numberOfMappers, inputSplitSize, individualMapperSize)

    //Todo : Decide on the reducer length
    val reducerLength = 30

    reducers = allocateReducers(numberOfReducers, reducerLength)

    //Todo: Decide on the result size
    val resultSize = 1024
    mappers.foreach(mapper => mapper.persistAndCommunicateMapResponse(getAssociateReducer(mapper.getMapperId), resultSize))

    //Allocate reducers job


    //Todo: Decide on memory allocated and number of packets
    val memoryAllocated = 1024
    val numberOfPacketsToReceive = 20
    reducers.map(reducer => reducer.run(memoryAllocated, getAssociatedMapper(reducer.getReducerId), numberOfPacketsToReceive))


  }

  /**
    * creates a virtual machine with time shared cloudlet scheduler
    *
    * @param id        : VmID
    * @param vmLength  : Length of VM in MIPS
    * @param pesNumber ; Number of processing elements
    * @param ram       : Ram size
    * @param bw        : Bandwidth associated with VM
    * @param vmSize    : The VM image size
    * @return constructed [[NetworkVm]]
    */

  def createNetworkVm(id: Int, vmLength: Int, pesNumber: Int, ram: Int, bw: Int, vmSize: Int): NetworkVm = {
    val vm = new NetworkVm(id, vmLength, pesNumber)
    vm.setRam(ram).setBw(bw).setSize(vmSize).setCloudletScheduler(new CloudletSchedulerTimeShared)
    vm

  }

  /**
    * Creates a list of virtual machines in a Datacenter for a given broker
    * and submit the list to the broker.
    *
    * @param broker The broker that will own the created VMs
    * @return the list of created VMs
    */

  def createAndSubmitVms(datacenterBroker: DatacenterBroker, numberOfVms: Int): List[NetworkVm] = {


    //TODO - MAKE IT CONFIGURABLE
    val nodeLegth = List(1000, 2000, 3000, 40000, 5000)
    val nodeRam = List(1024, 2048, 1024, 1024, 2048)
    val nodeBandWidth = List(1000, 10000, 10000, 10000, 10000)
    val nodeSize = List(1024, 1024, 1024, 1024, 1024)
    val nodePesNumber = List(2, 2, 2, 2, 2)


    (1 to numberOfVms).map(i => createNetworkVm(i, nodeLegth(i), nodePesNumber(i), nodeRam(i), nodeBandWidth(i), nodeSize(i))).toList

  }


  /**
    * The config ID for a simulation of this job type.
    *
    * @param configId The ID representing the variation of this job type that is to be simulated.
    * @param broker   Reference to the datacenter broker that the job may use for dynamic creation/submission of
    *                 cloudlets and VMs.
    */
  override def setSimulation(configId: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    this.simulation = simulation
  }

  /**
    * The VMs that this job needs for its execution.
    *
    * This method should create the necessary [[Vm]] objects along with the required configuration.
    *
    * @return List of VMs
    */
  override def getVmList: List[Vm] = ???

  /**
    * The cloudlets that this job needs to complete its execution.
    *
    * This method should create the necessary [[Cloudlet]]s along with the required configuration.
    *
    * @return List of Cloudlets
    */
  override def getCloudletList: List[Cloudlet] = ???
}
