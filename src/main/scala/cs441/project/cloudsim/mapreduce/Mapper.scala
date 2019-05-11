package cs441.project.cloudsim.mapreduce

import cs441.project.cloudsim.jobs.Job
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletTask, NetworkCloudlet, CloudletSendTask}

/**
  * Defines an individual mapper which will work on a particular file split and executes the map task [[CloudletExecutionTask]]
  * After done with execution it will emit/write the map result simulated using the [[CloudletSendTask]]
  *
  * @param mapperId
  * @param length
  * @param pes
  */
class Mapper(mapperId: Int, length: Long, pes: Int) {

  val cloudlet = new NetworkCloudlet(mapperId, length, pes)


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


  def persistAndCommunicateMapResponse(destinationCloudlet: NetworkCloudlet, resultSize: Long): Unit = {

    //TODO : Mimic some operation on Distributed File system
    val fileSize = 129393 // Defines packets to send
    val packetSize = 1024
    val numberOfPackets = (resultSize / packetSize).toInt

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
    task.setMemory(length)
    cloudlet.addTask(task)
    (1 to numberOfPacketSends).map(_ => task.addPacket(destinationCloudlet, packetLength))
  }


}
