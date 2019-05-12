package cs441.project.cloudsim.jobs.WebService

import com.typesafe.config.Config
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.distributions.{ContinuousDistribution, UniformDistr}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic, UtilizationModelFull}
import org.cloudsimplus.listeners.EventInfo

import scala.util.Random

class Client(broker: DatacenterBroker, config: Config) {

  var CloudletLength = randomNumber(config.getInt("Cloudlets.CLOUDLET_LENGTH_LB"), config.getInt("Cloudlets.CLOUDLET_LENGTH_HB"))
  var CloudletPes = config.getInt("Cloudlets.CLOUDLET_PES")
  var FileSize = randomNumber(config.getInt("Cloudlets.fileSize_LB"), config.getInt("Cloudlets.fileSize_HB"))
  var OutputSize = randomNumber(config.getInt("Cloudlets.outputSize_LB"), config.getInt("Cloudlets.outputSize_HB"))

  var cloudletList: List[Cloudlet] = (1 to config.getInt("Cloudlets.INITIAL_CLOUDLET_NUMBER")).map { _ => createCloudlet() }.toList
  var random: ContinuousDistribution = _
  random = new UniformDistr

  /*
    To return List of Cloudlets Created Statistically
  */
  def getCloudletList(): List[Cloudlet] = {
    return cloudletList
  }

  /*
    Submit Cloudlets to Broker at Runtime
  */
  def submitRandomCloudelts(evt: EventInfo): Unit = {
    if (random.sample() <= config.getInt("Cloudlets.RAND_SAMPLE")) {
      print("\n Randomly Creating 1 Cloudlet at time %.2f\n", evt.getTime)
      var cloudlet: Cloudlet = createCloudlet()
      cloudletList ::: List(cloudlet)
      broker.submitCloudlet(cloudlet)
    }
  }

  /*
    Creation of each Cloudlets
  */
  def createCloudlet(): Cloudlet = {
    var um: UtilizationModel = new UtilizationModelDynamic(config.getDouble("Cloudlets.INITIAL_UTILISATION_PERCENT"))

    return (new CloudletSimple(CloudletLength, CloudletPes)
      .setFileSize(FileSize)
      .setOutputSize(OutputSize)
      .setUtilizationModelCpu(new UtilizationModelFull)
      .setUtilizationModelRam(um)
      .setUtilizationModelBw(um))
  }

  def randomNumber(start: Int, end: Int): Int = {
    var rand: Random = new Random()
    return (rand.nextInt(end - start) + 1)
  }

}
