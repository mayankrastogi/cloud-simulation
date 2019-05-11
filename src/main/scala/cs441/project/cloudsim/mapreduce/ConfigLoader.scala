package cs441.project.cloudsim.mapreduce

import com.typesafe.config.ConfigFactory


object ConfigLoader {
  val configs = ConfigFactory.load("MapReduceJob")

  val path = "MapReduce"

  val INPUT_FILE_SIZE = configs.getInt(path + ".inputFileSize") * 1024 * 1024 * 1024 //Converting Gigs to bytes


  val INPUT_SPLIT_SIZE = configs.getInt(path + ".inputSplitSize")

  val MAP_TASK_LENGTH = configs.getLong(path + ".mapLength")

  val WORKER_NODES = configs.getList(path + ".vms")

  val NUMBER_OF_WORKER_NODES = configs.getInt(path + ".numberOfNodesAvailable")


}
