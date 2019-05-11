package cs441.project.cloudsim.jobs.WebService

import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.distributions.ContinuousDistribution
import org.cloudbus.cloudsim.distributions.UniformDistr
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudsimplus.listeners.EventInfo
import org.cloudsimplus.listeners.EventListener

class Client (simulation: Simulation) {

  var TIME_TO_TERMINATE_SIMULATION: Int = 30
  var SCHEDULING_INTERVAL: Int = 1

  var CLOUDLET_PES:Int = 2
  var CLOUDLET_LENGTH: Int = 10000

  var INITIAL_CLOUDLET_NUMBER: Int = 5  //Number of Cloudlets to be statically created when the simulation starts
  var random: ContinuousDistribution = _

  //val evt: EventInfo
  //Dynamic Cloudlet Creation Starts Here
  random = new UniformDistr

  simulation.addOnClockTickListener(createRandomCloudelts)

  def createRandomCloudelts(evt: EventInfo): Unit ={
    if (random.sample() <= 0.3){
      print("\n Randomly Creating 1 Cloudlet at time %.2f\n", evt.getTime)
      val cloudlet: Cloudlet = createCloudlet()
    }
  }

}
