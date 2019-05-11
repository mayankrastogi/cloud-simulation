package cs441.project.cloudsim.mapreduce

import com.typesafe.config.ConfigFactory


object ConfigLoader {
  val configs = ConfigFactory.load("MapReduceJob")

  val INPUT_FILE_SIZE = configs.getInt("inputFileSize") * 1024 * 1024 * 1024 //Converting Gigs to bytes


  val INPUT_SPLIT_SIZE = configs.getInt("inputSplitSize")

  val MAP_TASK_LENGTH = configs.getLong("mapLength")

  val WORKER_NODES = configs.getList("VMS")


}
