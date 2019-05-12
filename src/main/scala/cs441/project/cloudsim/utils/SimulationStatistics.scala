package cs441.project.cloudsim.utils

/**
  * Holds a summary of execution results of a cloud simulation.
  *
  * @param jobName               The name of the job.
  * @param cloudArchitecture     The description of the cloud architecture being simulated.
  * @param iteration             The current iteration of the simulation.
  * @param averageCloudletLength Average length of all the cloudlets in millions of instructions.
  * @param averageExecutionTime  Average execution time of all the cloudlets in seconds.
  * @param averageCPUCost        Average cost of CPU usage for all the cloudlets.
  * @param averageBandwidthCost  Average cost of bandwidth usage for all the cloudlets.
  * @param averageTotalCost      Average total cost of CPU + bandwidth usage for all the cloudlets.
  * @param totalIterations       The total number of iterations the simulation was run for.
  */
case class SimulationStatistics(jobName: String,
                                cloudArchitecture: String,
                                iteration: Int,
                                averageCloudletLength: Double,
                                averageExecutionTime: Double,
                                averageCPUCost: Double,
                                averageBandwidthCost: Double,
                                averageTotalCost: Double,
                                totalIterations: Int) {

  override def toString: String = {
    """
      |Cloud Architecture     : %s
      |Average Cloudlet Length: %.2f MI
      |Average Execution Time : %.2f seconds
      |Average CPU Cost       : $ %.2f
      |Average Bandwidth Cost : $ %.2f
      |Average Total Cost     : $ %.2f
    """
      .stripMargin
      .format(
        cloudArchitecture,
        averageCloudletLength,
        averageExecutionTime,
        averageCPUCost,
        averageBandwidthCost,
        averageTotalCost
      )
  }
}