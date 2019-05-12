package cs441.project.cloudsim.jobs.mapreduce

import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletTask, NetworkCloudlet, CloudletSendTask}
import scala.collection.JavaConverters._

/**
  * Defines an individual mapper which will work on a particular file split and executes the map task [[CloudletExecutionTask]]
  * After done with execution it will emit/write the map result simulated using the [[CloudletSendTask]]
  *
  * @param mapperId
  * @param length
  * @param pes
  */
class Mapper(mapperId: Int, length: Long, pes: Int) {

  var numberOfPackets: Int = _

  /**
    * Sets the resultant number of packets to be sent by the mapper
    *
    * @param numberOfPackets The number of packets
    */
  def setResultsPackets(numberOfPackets: Int): Unit = this.numberOfPackets = numberOfPackets

  /**
    * Gets the number of packets sent
    *
    * @return numberOfPackets
    */
  def getResultsPackets: Int = this.numberOfPackets


  val cloudlet = new NetworkCloudlet(mapperId, length, pes)


  /**
    * Gets the mapper Id
    *
    * @return the mapper Id
    */
  def getMapperId: Int = {
    mapperId
  }

  def getMapperCloudlet: NetworkCloudlet = cloudlet

  /**
    * Defines an individual map task running specific task each
    *
    * @param memoryAllocated memory allocated for each mapper
    * @return [[NetworkCloudlet]] representing the running mapper
    */
  def run(memoryAllocated: Long): Mapper = {

    //TODO : Mimic some operation on Distributed File system

    // Adding an execution task for this mapper
    addExecutionTask(memoryAllocated)

    this
  }

  /**
    * Adds an execution task to the list of tasks of the given [[NetworkCloudlet]]
    *
    * @param taskMemory the execution length of the task
    */
  protected def addExecutionTask(taskMemory: Long): Unit = {
    val task = new CloudletExecutionTask(cloudlet.getTasks.size, length)
    task.setMemory(taskMemory)
    cloudlet.addTask(task)
  }

  /**
    * Persists and communicates the mappers result
    *
    * @param destinationCloudlet The cloudlet that will receive the mappers result
    * @param numberOfPackets     the number packets representing the result
    * @param packetSize          the size of individual packet
    */
  def persistAndCommunicateMapResponse(destinationCloudlet: NetworkCloudlet, numberOfPackets: Int, packetSize: Int): Unit = {

    addSendTask(destinationCloudlet, packetSize, numberOfPackets)


  }

  /**
    * Adds a send task to the list of tasks of the given {@link NetworkCloudlet}.
    *
    * @param sourceCloudlet      the { @link NetworkCloudlet} from which packets will be sent
    * @param destinationCloudlet the destination { @link NetworkCloudlet} to send packets to
    */
  protected def addSendTask(destinationCloudlet: NetworkCloudlet, packetLength: Long, numberOfPacketSends: Int): Unit = {
    val task = new CloudletSendTask(cloudlet.getTasks.size)
    task.setMemory(length / 2)
    cloudlet.addTask(task)
    (1 to numberOfPacketSends).map(_ => task.addPacket(destinationCloudlet, packetLength))
  }


}
