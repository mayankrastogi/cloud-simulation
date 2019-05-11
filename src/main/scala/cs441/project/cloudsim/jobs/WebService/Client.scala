package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.distributions.ContinuousDistribution
import org.cloudbus.cloudsim.distributions.UniformDistr
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudsimplus.listeners.EventInfo

class Client (broker: DatacenterBroker) {

  //Cloudlet Characteristics to be read from configuration file
  var CLOUDLET_PES:Int = 2
  var CLOUDLET_LENGTH: Int = 10000
  var INITIAL_CLOUDLET_NUMBER: Int = 5  //Number of Cloudlets to be statically created when the simulation starts
  var filesize:Int = 1024
  var outputsize: Int = 1024

  var cloudletList: List[Cloudlet] = (1 to INITIAL_CLOUDLET_NUMBER).map{_ => createCloudlet()}.toList

  var random: ContinuousDistribution = _
  random = new UniformDistr

  /*
    To return List of Cloudlets Created Statistically
  */
  def getCloudletList(): List[Cloudlet]={
    return cloudletList
  }

  /*
    Submit Cloudlets to Broker at Runtime
  */
  def submitRandomCloudelts(evt: EventInfo): Unit ={
    if (random.sample() <= 0.4){
      print("\n Randomly Creating 1 Cloudlet at time %.2f\n", evt.getTime)
      var cloudlet: Cloudlet = createCloudlet()
      cloudletList:::List(cloudlet)
      broker.submitCloudlet(cloudlet)
    }
  }

  /*
    Creation of each Cloudlets
  */
  def createCloudlet(): Cloudlet = {
    val um: UtilizationModel = new UtilizationModelDynamic(0.2)
    return (new CloudletSimple(CLOUDLET_LENGTH,CLOUDLET_PES)
        .setFileSize(filesize)
        .setOutputSize(outputsize)
        .setUtilizationModelCpu(new UtilizationModelFull)
        .setUtilizationModelRam(um)
        .setUtilizationModelBw(um)
      )
  }

}
