package cs441.project.cloudsim.utils.config

import com.typesafe.config.ConfigFactory

object WebServiceConfig {

  /**
    * Gets the number of web services running on the data center
    *
    * @return
    */
  def getNumberOfServices: Int = {
    ConfigFactory.load("WebServices").getConfigList("WebService").size()
  }

}
