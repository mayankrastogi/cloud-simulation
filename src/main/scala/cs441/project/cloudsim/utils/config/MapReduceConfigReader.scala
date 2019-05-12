package cs441.project.cloudsim.utils.config

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

/**
  * Map reduce Job config loader. Loads the specific Job configuration file from the resources directory
  *
  * @param jobId The id identifying the map reduce job in configs
  */
class MapReduceConfig(jobId: Int) {
  val AllConfs = ConfigFactory.load("MapReduceJob")

  val jobConf = AllConfs.getConfigList("MapReduce").get(jobId)

  val path = ""

  val INPUT_FILE_SIZE = jobConf.getInt(path + "inputFileSize") * 1024 * 1024 * 1024 //Converting Gigs to bytes


  val INPUT_SPLIT_SIZE = jobConf.getInt(path + "inputSplitSize") * 1024 * 1024

  val NUMBER_OF_WORKER_NODES = jobConf.getInt(path + "numberOfNodesAvailable")

  val NODES_BANDWIDTH = jobConf.getIntList(path + "nodesBandWidth").asScala
  val NODES_LENGTH = jobConf.getIntList(path + "nodesLength").asScala
  val NODES_SIZE = jobConf.getIntList(path + "nodesSize").asScala
  val NODES_RAM = jobConf.getIntList(path + "nodesRam").asScala
  val NODES_PES = jobConf.getIntList(path + "nodesPES").asScala
  val MAPPER_PES = jobConf.getInt(path + "mapperPES")
  val REDUCER_PES = jobConf.getInt(path + "reducerPES")
  val MAPPER_MEMORY_ALLOCATED = jobConf.getLong(path + "mapperMemoryAllocated")
  val REDUCER_MEMORY_ALLOCATED = jobConf.getLong(path + "reducerMemoryAllocated")
  val MAPPER_RESULT_SIZE = jobConf.getLongList(path + "mapperResultSize").asScala
  val PACKET_SIZE = jobConf.getInt(path + "packetSize")
  val MAPPER_LENGTH = jobConf.getLong(path + "mapperLength")
  val REDUCER_LENGTH = jobConf.getLong(path + "reducerLength")


}
