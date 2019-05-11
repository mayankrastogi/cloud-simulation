package cs441.project.cloudsim.mapreduce

import cs441.project.cloudsim.Job
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletTask, NetworkCloudlet, CloudletSendTask}


/**
  * Defines an individual mapper which will work on a particular file split and executes the map task [[CloudletExecutionTask]]
  * After done with execution it will emit/write the map result simulated using the [[CloudletSendTask]]
  */
class Mapper {


  def run(mapperId : String, length:Long): Unit =
  {




  }

  /**
    * Adds an execution task to the list of tasks of the given [[NetworkCloudlet]] .
    *
    * @param cloudlet the [[NetworkCloudlet]] the task will belong to
    */
  protected def addExecutionTask(cloudlet: NetworkCloudlet): Unit = {
    val task = new CloudletExecutionTask(cloudlet.getTasks.size, NETCLOUDLET_EXECUTION_TASK_LENGTH)
    task.setMemory(NETCLOUDLET_RAM)
    cloudlet.addTask(task)
  }

  /**
    * Adds a receive task to the list of tasks of the given  [[NetworkCloudlet]].
    *
    * @param cloudlet the  [[NetworkCloudlet]] the task will belong to
    * @param sourceCloudlet the  [[NetworkCloudlet]] expected to receive packets from
    */
  protected def addReceiveTask(cloudlet: NetworkCloudlet, sourceCloudlet: NetworkCloudlet): Unit = {
    val task = new CloudletReceiveTask(cloudlet.getTasks.size, sourceCloudlet.getVm)
    task.setMemory(NETCLOUDLET_RAM)
    task.setExpectedPacketsToReceive(NUMBER_OF_PACKETS_TO_SEND)
    cloudlet.addTask(task)
  }





}
