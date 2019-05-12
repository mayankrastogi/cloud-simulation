package cs441.project.cloudsim.jobs.mapreduce

import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletSendTask, NetworkCloudlet}


/**
  * Defines an individual reducer task which will take the output written by the mapper processes it and writes/persists to the fileSystem.
  *
  * @param reducerId The reducer task Id
  * @param length    The length of the task executed by the reducer
  * @param pes       The  number of processing elements
  */
class Reducer(reducerId: Int, length: Long, pes: Int) {

  val cloudlet = new NetworkCloudlet(reducerId, length, pes)

  /**
    * Get the network cloudlet representing the reducer's tasks
    *
    * @return the [[NetworkCloudlet]] representing reducer
    */
  def getReducerCloudlet: NetworkCloudlet = {
    cloudlet
  }

  /**
    * Get the Id associated with the reducer
    *
    * @return reducer Id
    */
  def getReducerId: Int = {
    reducerId
  }


  /**
    * Defines an individual reducer running specific task each as a cloudlet
    *
    * @param memoryAllocated memory allocated to each reducer
    * @return [[NetworkCloudlet]] representing the reducer task
    */
  def run(memoryAllocated: Long, mapper: NetworkCloudlet, numberOfPacketsToReceive: Long): NetworkCloudlet = {

    //TODO : Mimic some operation on Distributed File system, define number of packets to write

    addReceiveTask(mapper, memoryAllocated, numberOfPacketsToReceive)

    // Adding an execution task for this mapper
    addExecutionTask(memoryAllocated)

    //Todo: persist the response


    //TODO : Optionally send the sendResponseTask¬

    cloudlet
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
    * Adds a receive task to the list of tasks of the given {@link NetworkCloudlet}.
    *
    * @param cloudlet       the { @link NetworkCloudlet} the task will belong to
    * @param sourceCloudlet the { @link NetworkCloudlet} expected to receive packets from (representing the mapper)
    */
  protected def addReceiveTask(mapper: NetworkCloudlet, taskMemory: Long, numberOfPackets: Long): Unit = {
    val task = new CloudletReceiveTask(cloudlet.getTasks.size, mapper.getVm)
    task.setMemory(taskMemory)
    task.setExpectedPacketsToReceive(numberOfPackets)
    cloudlet.addTask(task)
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
