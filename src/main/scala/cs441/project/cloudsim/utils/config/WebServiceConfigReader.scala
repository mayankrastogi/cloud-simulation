package cs441.project.cloudsim.utils.config

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

/**
  * Web Service config loader. Loads the specific Job configuration file from the resources directory
  *
  * @param serviceID The id identifying the web service in configs
  */
class WebServiceConfigReader(serviceID: Int) {


  //TO Read from Configuration File


  val AllConfs = ConfigFactory.load("WebServices")

  val serviceConf = AllConfs.getConfigList("WebService").get(serviceID)

  val VMS: Int = serviceConf.getInt("VMS_NUMBER")
  val VM_PES: Int = serviceConf.getInt("VM_PES")
  val VM_MIPS: Int = serviceConf.getInt("VM_MIPS")
  val VM_BW: Int = serviceConf.getInt("VM_BW")
  val VM_RAM: Int = serviceConf.getInt("VM_RAM")
  val VM_SIZE: Int = serviceConf.getInt("VM_SIZE")
  var vm_id: Int = serviceConf.getInt("VM_ID")

  val INITIAL_CLIENTS: Int = serviceConf.getInt("INITIAL_CLIENTS")
  val TIME_TO_REQUESTS = serviceConf.getInt("TIME_TO_REQUESTS")

  var CLOUDLET_PES = serviceConf.getInt("CLOUDLET_PES")
  val CLOUDLET_LENGTH_LL: Int = serviceConf.getInt("CLOUDLET_LENGTH_LL")
  val CLOUDLET_LENGTH_UL: Int = serviceConf.getInt("CLOUDLET_LENGTH_UL")


  val CLOUDLET_FILE_SIZE: Int = serviceConf.getInt("CLOUDLET_FILE_SIZE")
  val CLOUDLET_OUTPUT_SIZE: Long = serviceConf.getInt("CLOUDLET_OUTPUT_SIZE")


}
