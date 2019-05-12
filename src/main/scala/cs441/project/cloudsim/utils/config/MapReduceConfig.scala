package cs441.project.cloudsim.utils.config

import com.typesafe.config.ConfigFactory

/**
  * Loads the configs and returns the number of map reduce job configured
  */
object MapReduceConfig {

  /**
    * Gets the number of Map Reduce Jobs
    *
    * @return
    */
  def getNumberOfJobs: Int = {
    ConfigFactory.load("MapReduceJob").getConfigList("MapReduce").size()
  }

}
