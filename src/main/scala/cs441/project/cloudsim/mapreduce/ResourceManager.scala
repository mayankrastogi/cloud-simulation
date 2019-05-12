package cs441.project.cloudsim.mapreduce

import ch.qos.logback.classic.Logger
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import cs441.project.cloudsim.jobs.Job
import cs441.project.cloudsim.utils.config.MapReduceConfigReader
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.network.NetworkVm
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.Random


/**
  * Models a resource manager, which takes in the Job requests allocated/schedules the jobs among the different available nodes as specified by the job conf
  *
  * @param jobConf The id specifying the map reduce job config
  */

class ResourceManager extends Job with LazyLogging {


  //  val reducer = new Reducer
  var mapReduceConf: MapReduceConfigReader = _



  var simulation: Simulation = _

  var mappers: List[Mapper] = _
  var reducers: List[Reducer] = _

  var dataCenterBroker: DatacenterBroker = _

  var workerNodes: List[NetworkVm] = _


  def allocateMappers(numberOfMappers: Int, inputSplitSize: Int, mapperSize: Long): List[Mapper] = {
    logger.info("Sid : Allocating {} mappers", numberOfMappers)
    val mapperPES = mapReduceConf.MAPPER_PES
    (0 until numberOfMappers).map(iMapper => new Mapper(iMapper, inputSplitSize, mapperPES).run(mapperSize)).toList

  }


  /**
    * Returns the associated mapper with given reducer Id
    *
    * @param iReducer the reducer ID
    * @return the mapper which emitted its input
    */
  def getAssociatedMapper(iReducer: Int): Mapper = {
    mappers.filter(mapper => mapper.getMapperId == (iReducer - 1) / 10).head
  }

  /**
    * Returns the associated reducer with given mapper id
    *
    * @param iMapper the mapperID
    * @return the reducer who will take the given mapper's output
    */
  def getAssociateReducer(iMapper: Int): NetworkCloudlet = {
    reducers.filter(reducer => (reducer.getReducerId - 1) / 10 == iMapper).head.getReducerCloudlet
  }

  /**
    * Allocate the reducers for the job
    *
    * @param numberOfReducers number of reducers to be allocated
    * @param reducerLength    the length of each reducers
    * @return List[[Reducer]] representing each reducer task
    */
  def allocateReducers(numberOfReducers: Int, reducerLength: Long): List[Reducer] = {
    val reducerPES = mapReduceConf.REDUCER_PES

    (0 until numberOfReducers).map(iReducer => new Reducer((iReducer + 1) * 10, reducerLength, reducerPES)).toList

  }


  /**
    * Configure the worker nodes for the map-reduce jobs
    *
    * @return list of [[NetworkVm]]s representing worker nodes
    */
  def configureWorkerNodes(): List[NetworkVm] = {

    val numberOfWorkerNodes = mapReduceConf.NUMBER_OF_WORKER_NODES
    val workerNodes = createNetworkedVms(numberOfWorkerNodes)
    dataCenterBroker.submitVmList(workerNodes.asJava)
    workerNodes


  }

  def getNumberOfPackets(reducerId: Int): Int = {


    //    val resultSize: Long = mapReduceConf.MAPPER_RESULT_SIZE(reducer.getReducerId)
    //    (resultSize / mapReduceConf.PACKET_SIZE).toLong
    getAssociatedMapper(reducerId).numberOfPackets

  }

  def getAllCloudlets(): List[NetworkCloudlet] = {
    mappers.map(mapper => mapper.getMapperCloudlet) ::: reducers.map(reducer => reducer.getReducerCloudlet)
  }

  /**
    * Assign/Bind cloudlets to the VMs in the roundRobin fashion
    *
    * @param mappers     All the mappers that needs to be assigned to a workerNode
    * @param reducers    All the reducers that needs be assigned to a workerNode
    * @param workerNodes The worker nodes on which the mappers and reducers would be scheduled
    */

  def assignVmsToCloudlets(mappers: List[Mapper], reducers: List[Reducer], workerNodes: List[NetworkVm]) = {

    val numberOfMappers, numberOfReducers = mappers.length
    val numberOfVms = workerNodes.length

    val allCloudlets = getAllCloudlets()

    var counter = -1
    allCloudlets.foreach(cloudelet => {
      counter += 1
      cloudelet.setVm(workerNodes(counter % numberOfVms))
    })

    // distribute the  mappers and reducers equally among the available number of vms


    //    mappers.foreach(mapper => mapper.getMapperCloudlet.setVm())
  }

  def generateMappersResult() = {
    Random.nextInt(64 * 1024)
  }

  /**
    * Receives the Job requests and schedules the job tasks among the worker nodes.
    *
    * @param datacenterBroker broker associated with the job
    */

  def receiveJobRequest(): Unit = {


    val inputFileSize = mapReduceConf.INPUT_FILE_SIZE
    val inputSplitSize = mapReduceConf.INPUT_SPLIT_SIZE

    // Decide on the number of mappers and reducers based on the Input file size and the split size

    val numberOfMappers, numberOfReducers = inputFileSize / inputSplitSize


    //Define the Vms/Resources on which mappers and reducers are simulated
    workerNodes = configureWorkerNodes()


    val individualMapperSize = mapReduceConf.MAPPER_LENGTH


    // Allocate mappers job
    mappers = allocateMappers(numberOfMappers, inputSplitSize, individualMapperSize)


    val reducerLength = mapReduceConf.REDUCER_LENGTH

    reducers = allocateReducers(numberOfReducers, reducerLength)

    //Bind the mappers with the vm

    assignVmsToCloudlets(mappers, reducers, workerNodes)


    val packetSize = mapReduceConf.PACKET_SIZE
    val resultSize = generateMappersResult()

    val numberOfPackets = resultSize / packetSize

    mappers.foreach(mapper => {

      mapper.persistAndCommunicateMapResponse(getAssociateReducer(mapper.getMapperId), numberOfPackets, packetSize)
      mapper.setResultsPackets(numberOfPackets)
    })

    //Allocate reducers job


    val reducerMemoryAllocated = mapReduceConf.REDUCER_MEMORY_ALLOCATED
    reducers.map(reducer => reducer.run(reducerMemoryAllocated, getAssociatedMapper(reducer.getReducerId).getMapperCloudlet, getNumberOfPackets(reducer.getReducerId)))


    dataCenterBroker.submitCloudletList(getAllCloudlets().asJava)
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

  def createNetworkedVms(numberOfVms: Int): List[NetworkVm] = {

    (0 until numberOfVms).map(i => createNetworkVm(i, mapReduceConf.NODES_LENGTH(i), mapReduceConf.NODES_PES(i), mapReduceConf.NODES_RAM(i), mapReduceConf.NODES_BANDWIDTH(i), mapReduceConf.NODES_SIZE(i))).toList
  }


  /**
    * The config ID for a simulation of this job type.
    *
    * @param configId The ID representing the variation of this job type that is to be simulated.
    * @param broker   Reference to the datacenter broker that the job may use for dynamic creation/submission of
    *                 cloudlets and VMs.
    */
  override def setSimulation(jobConf: Int, broker: DatacenterBroker, simulation: Simulation): Unit = {
    mapReduceConf = new MapReduceConfigReader(jobConf)
    dataCenterBroker = broker
    this.simulation = simulation
    receiveJobRequest()
  }

  /**
    * The VMs that this job needs for its execution.
    *
    * This method should create the necessary [[Vm]] objects along with the required configuration.
    *
    * @return List of VMs
    */
  override def getVmList: List[Vm] = {
    workerNodes
  }


  /**
    * The cloudlets that this job needs to complete its execution.
    *
    * This method should create the necessary [[Cloudlet]]s along with the required configuration.
    *
    * @return List of Cloudlets
    */
  override def getCloudletList: List[Cloudlet] = {
    getAllCloudlets()
  }
}
