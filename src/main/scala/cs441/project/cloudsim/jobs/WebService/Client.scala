package cs441.project.cloudsim.jobs.WebService

import com.typesafe.config.Config
import cs441.project.cloudsim.utils.config.WebServiceConfigReader
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.distributions.{ContinuousDistribution, UniformDistr}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic, UtilizationModelFull}
import org.cloudsimplus.listeners.EventInfo

import scala.collection.JavaConverters._
import scala.util.Random


/**
  * Represents the client which generates bursts of requests to the server
  *
  * @param broker        the broker of the web service job
  * @param serviceConfig the service Config
  */
class Client(broker: DatacenterBroker, serviceId: Int) {


  val clientConfig = new WebServiceConfigReader(serviceId)

  var cloudletList: List[Cloudlet] = (1 to clientConfig.INITIAL_CLIENTS).map(_ => createCloudlet()).toList
  //  var random: ContinuousDistribution = _
  //  random = new UniformDistr

  var clientCounter: Int = _

  /**
    * returns the requests in bursts to the server
    *
    * @return list of clients as cloudlets
    */
  def getClientCloudletList: List[Cloudlet] = {
    return cloudletList
  }


  /**
    * Submits the client requests in ramndom fashion to the clients for a given period of time
    *
    * @param eventInfo the event Information triggering the clients
    */
  def submitRandomCloudLets(eventInfo: EventInfo): Unit = {
    val time = eventInfo.getTime.toLong
    //    if (probability == 1 && time <= 50) {
    if (false) {
      val numberOfCloudlets = generateClientLoad()
      println("\t#Creating %d Cloudlets at time %d.\n", numberOfCloudlets, time)


      val clientCloudlets = (0 until numberOfCloudlets).map(id => createCloudlet()).toList

      cloudletList = cloudletList ::: clientCloudlets
      broker.submitCloudletList(clientCloudlets.asJava)

    }

  }

  /**
    * Generating the random client code
    *
    * @return the random requets to the clients
    */
  def generateClientLoad(): Int = {
    val rand = new Random
    rand.nextInt(10)
  }

  def probability(): Int = {
    new Random().nextInt(2)
  }

  /**
    * Creates a specific request to be executed on the client
    *
    * @return the cloudlet representing the request on the server
    */
  def createCloudlet(): Cloudlet = {
    clientCounter += 1

    new CloudletSimple(clientCounter, getRandomLength(clientConfig.CLOUDLET_LENGTH_LL, clientConfig.CLOUDLET_LENGTH_UL), clientConfig.CLOUDLET_PES)
      .setFileSize(clientConfig.CLOUDLET_FILE_SIZE)
      .setOutputSize(clientConfig.CLOUDLET_OUTPUT_SIZE).setUtilizationModel(new UtilizationModelFull)
  }

  def getRandomLength(start: Int, end: Int): Int = {
    var rand: Random = new Random()
    start + rand.nextInt(end)
  }

}
