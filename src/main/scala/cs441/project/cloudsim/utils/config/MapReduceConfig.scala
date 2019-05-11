package cs441.project.cloudsim.utils.config

import com.typesafe.config.ConfigFactory

class MapReduceConfig(jobId: Int) {
  val configs = ConfigFactory.load("MapReduceJob")

  val path = "MapReduce[" + jobId + "]"

  val INPUT_FILE_SIZE = configs.getInt(path + ".inputFileSize") * 1024 * 1024 * 1024 //Converting Gigs to bytes


  val INPUT_SPLIT_SIZE = configs.getInt(path + ".inputSplitSize")

  val NUMBER_OF_WORKER_NODES = configs.getInt(path + ".numberOfNodesAvailable")

  val NODES_BANDWIDTH = configs.getLongList(path + ".nodesBandWith")
  val NODES_LENGTH = configs.getLongList(path + ".nodesLength")
  val NODES_SIZE = configs.getLongList(path + ".nodesSize")
  val NODES_RAM = configs.getLongList(path + ".nodesRAM")
  val NODES_PES = configs.getLongList(path + ".nodesPES")
  val MAPPER_MEMORY_ALLOCATED = configs.getLong(path + ".mapperMemoryAllocated")
  val REDUCER_MEMORY_ALLOCATED = configs.getLong(path + ".reducerMemoryAllocated")
  val MAPPER_RESULT_SIZE = configs.getLongList(path + ".mapperResultSize")
  val PACKET_SIZE = configs.getIntList(path + ".packetSize")
  val MAPPER_LENGTH = configs.getLong(path + ".mapperLength")
  val REDUCER_LENGTH = configs.getLong(path + ".reducerLength")


}
