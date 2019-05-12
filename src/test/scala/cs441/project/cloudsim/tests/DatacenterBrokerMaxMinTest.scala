package cs441.project.cloudsim.tests

import cs441.project.cloudsim.policies.loadbalancing.{DatacenterBrokerMaxMin, DatacenterBrokerMinMin}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.scalatest.{FunSuite, Matchers}

class DatacenterBrokerMaxMinTest extends FunSuite with Matchers {

  val broker = new DatacenterBrokerMaxMin(new CloudSim)

  test("estimateExecutionTimeAndGetMin should return minimum execution time") {
    val vms = createVMs(3)
    val cloudlet = new CloudletSimple(10, 1)
    val minTime = broker.estimateExecutionTimeAndGetMin(cloudlet, vms)

    minTime shouldBe 0.083 +- 0.01
  }

  test("mapCloudletsMaxMin should have the cloudlet lists sorted by their execution times") {
    val vms = createVMs(3)
    val cloudlets = createCloudlets(3)
    val sortedCloudlets = broker.mapCloudletsMaxMin(cloudlets, vms)

    sortedCloudlets.map(_.getId).reverse shouldBe sorted
  }

  def createVMs(number: Int): List[Vm] = {
    (1 to number)
      .toList
      .map(i => new VmSimple(20 * i, 2))
  }

  def createCloudlets(number: Int): List[Cloudlet] = {
    (1 to number)
      .toList
      .map(i =>{
        val cloudlet = new CloudletSimple(10 * i, 2)
        cloudlet.setId(i)
        cloudlet
      })
  }
}
