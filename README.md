## CS 441 - Engineering Distributed Objects for Cloud Computing
## Course Project - Cloud Simulation using Cloudsim-Plus

---

### Overview

This project is part of the course curriculum of the CS441 - Cloud Computing objects at the University of Illinois at Chicago.

Cloud computing is the leading technology for delivery of reliable, secure, fault-tolerant, sustainable, and scalable computational services. For assurance of such characteristics in cloud systems under development, it is required timely, repeatable, and controllable methodologies for evaluation of new cloud applications and policies, before actual development of cloud products.

In this project we aim to analyze and build various cloud architectures with multiple datacenters (according to availability region) , the network they use to connect with each other, each of the datacenter having multiple hosts, host network topology, build and allocation of VMs to this hosts, Cloudlet scheduling policies, Dynamic load balancing algorithm and finally the costs associated with execution of two main types of jobs (map reduce and webservice) on this cloud infrastructure. 

### Team members (in alphabetical order)

- Manoj Prabhakar Nallabothula (mnalla2@uic.edu)
- Mayank K Rastogi (mrasto3@uic.edu)
- Mohammed Siddiq (msiddi56@uic.edu)
- Saikrishna Vanamala (svanam2@uic.edu)

### Instructions

#### Docker Image

The docker image for running this project can be dound at [DockerHub](https://hub.docker.com/r/mnalla2/cloudsimulationproviders)

```
docker pull mnalla2/cloudsimulationproviders
```

#### Prerequisites

- [SBT](https://www.scala-sbt.org/) installed on your system

#### Running the project using sbt

1. Clone or download this repository onto your system
2. Open the Command Prompt (if using Windows) or the Terminal (if using Linux/Mac) and browse to the project directory
3. Build the project and generate the jar file using SBT
   
    ```
    sbt clean compile run
    ```

### Architecture of the cloud simulation

### Components of the application

#### Control Flow

1. Driver Program:

   The main entry point to the simulation application is the Driver Program. Each Type of Job (Map-Reduce, Web-Service) is initialized in the Driver Program, each job is associated with a datacenter broker which will handle the execution of this job till start to finish.

   The simulation object reference, broker reference, job reference, datacenter reference, hosts reference is all originated from this Driver Program.

2. Job:

   A Job simulates the type of application which needs to get executed on the cloud infrastructure. Two main types of jobs are designed - Map/Reduce and Web-Service. Each Job is associated with a Datacenter Broker who specializes in execution of a particular job.

   ![1557666731554](images/1557666731554.png)



#### Jobs Architecture:



##### Map-Reduce

![1557660401136](images/1557660401136.png)



We Split the Job into three different tasks: Mapper, Reducer and ResourceManager.

1. Mappers: Defines an individual mapper which will work on a particular file split and executes the map task [CloudletExecutionTask]. After done with execution it will emit/write the map result simulated using the [CloudletSendTask].

2. Reducers: Defines an individual reducer task which will take the output written by the mapper from the intermediate storage, processes it and writes/persists to the output node.

3. Resource Manager: Models a resource manager, which takes in the Job requests allocated/schedules the jobs among the different available nodes as specified by the job conf.

4. Worker Nodes and VMs: Each of the above cloudlets are executed on these worker nodes which are nothing but the VMs allocated to the job by the resource manager.



##### Web Service

![1557657601791](images/1557657601791.png)

A Webservice job simulates the execution of Dynamic User requests in the form of dynamic arrival of cloudlets using a Uniform Distribution prediction. These dynamic User requests are submitted to Data Center Broker, who decides the allocation of these cloudlets on to the VM using the custom min-min and max-min scheduling and load balancing policy.

The execution of these jobs for different cloud architectures has been compared and the results been analyzed to choose an optimum architecture with a greater return on investments.     



#### Load Balancing Algorithms

##### Min-Min

Min-Min start with the set of all unassigned tasks in the make-span. This algorithm work in two phases. First, the minimum expected completion time for all the tasks is calculated. The completion time for all the tasks is calculated on all the machines. In the second phase, the task with the minimum expected completion time from make-span is selected and that tasks assigned to the corresponding resource. Then the task which is completed that is removed from the make-span and this process is repeated until all tasks are completed.

A data center broker that uses Min-min load balancing algorithm to place Cloudlets on the available VMs. The algorithm prioritizes smaller cloudlets (ones with smaller estimated execution time) over larger cloudlets and assigns them to VMs first.

##### Max-Min

Max-Min start with the set of all unassigned tasks in the make-span. This algorithm also works in two phases. First, the maximum expected completion time for all the tasks is calculated. The completion time for all the tasks is calculated on all the machines. In the second phase, the task with the maximum expected completion time from make-span is selected and that tasks assigned to the corresponding resource. Then the task which is completed that is removed from the make-span and this process is repeated until all tasks are completed.

A data center broker that uses Max-min load balancing algorithm to place Cloudlets on the available VMs. The algorithm prioritizes smaller cloudlets (ones with smaller estimated execution time) over larger cloudlets and assigns them to VMs first.



#### VM Allocation Policies

These policies define the allocation of VMs to the Host.

##### Random Allocation

A Random allocation policy defines the random allocation of Vms to its Hosts. The Host list is shuffled and a random Host is picked. This picked Host is first prioritized for allocation of VMs. Once VMs are allocated to this Host and the Host is at maximum utilization, another Host is picked according to the defined Random policy to get it allocated to the future VMs   

##### Nearest Host Allocation

A more sophisticated allocation policy than the Random allocation. The nearest host allocation policy will first allocate the VMs in the Same Host till the maximum utilization, once its utilization capacity is reached, it will try to allocate the VM in the same Rack through scanning the EDGE SWITCH for any under-utilized Host. If it doesn't find any Host in the Edge Switches, it will start scanning the AGGREGATE SWITCH to identify any underutilized Host. Finally, if it is not able to find the Host through the Aggregate Switches also, Then it will try to look into another datacenters with the help of ROOT SWITCH Scan and Datacenter Broker dynamic table list.   

### Expected Simulation Results and Analysis:

We divided our analysis based on different impplementations of the allocation policies. Depending upon the combination of VM Allocation Policy and Load Balancing algorithms we implemented.
We simulated multiple distributed jobs and web services that had myriad of executions and scheduling options. The analysis looks as follows:

We have implemented and done simulations for 3  VM Allocation Policy :

1. First-Fit VM allocation policy
2. Random VM Allocation
3. Nearest Host VM Allocation Policy



We also implemented the following Load Balancer, balancing bursts of loads over the varied jobs Policy:

1. Round-Robin load balancing
2.  Min-Min Load Balancing
3. Max-Min Load Balancing

We were changing one variable at a time. Keeping other factors as constant. Essentially, we ran 3 different map-reduce jobs, 3 different web services at the same time. 
The load on the data center was allocated randomly, with the following set of atomic executions on the machines :

 Architecture: 
 
 96 Hosts per Datacenter

 For Map-Reduce Job:

 No of Mappers = 30 * 3 = 90

 No of Reducers= 30 * 3 = 90
 
 For the web services :
 
 Had 3 services running with random number of clients, and auto scalable servers based on the load. 

Thus, our analysis is divided into 9 parts:

 **Part 1: First Fit VM Allocation and Round Robin Load Balancer** 

  which will compare RoundRobin balancer and First fit VM allocation based on our dynamic job runs. 

**Part 2: First Fit VM Allocation and Min-Min Load balancing**

**Part 3: First Fit VM Allocation and Max-Min Load Balancing** 

**Part 4:Random VM Allocation and Round Robin Load Balancer** 

**Part 5: :Random VM Allocation and Min-Min Load balancing** 

**Part 6: Random VM Allocation and Max-Min Load Balancing** 

**Part 7: Nearest Host VM Allocation Policy and Round Robin Load Balancer** 

**Part 8: Nearest Host VM Allocation Policy and Min-Min Load Balancing** 

 **The expected result analysis would generate the statistical average of the executions, resource usages and the costs over large number of iterations (say 100).**
 
 
The implementation for the above hypothesis was constructed and simulation was ran.

#

The following was the snapshot of the simulation results obtained. 



   Result Snap:

```
================== Simulation finished at time 5370.86 ==================

INFO  Simulation completed.


                                      SIMULATION RESULTS: DatacenterBrokerSimple18ResourceManager

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|CPU Cost|Bandwidth Cost|Total Cost
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|     USD|           USD|       USD
----------------------------------------------------------------------------------------------------------------------------------------
       0|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
       4|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
       8|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
      12|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
      50|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
      90|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
     130|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
       1|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       5|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       9|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      13|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       2|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       6|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      10|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      14|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       3|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
       7|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      11|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      15|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      20|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      60|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     100|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     140|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      30|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      70|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     110|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     150|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      40|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
      80|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     120|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
     160|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
----------------------------------------------------------------------------------------------------------------------------------------
INFO  Statistics for DatacenterBrokerSimple18ResourceManager for iteration 1:

Cloud Architecture     : Cloud Architecture having 1 data center with 96 hosts using First-Fit VM allocation policy and Round-Robin load balancing of cloudlets
Average Cloudlet Length: 69276569.00 MI
Average Execution Time : 5218.95 seconds
Average CPU Cost       : $ 52.19
Average Bandwidth Cost : $ 0.10
Average Total Cost     : $ 52.39
```
   
The simulation kept on running based on the configs structured and generated the log files of about **10 GB**.

And the aggregated statistical results were resembling the above replica like the following result summary :

    ================== Simulation finished at time 5370.86 ==================
    
    INFO  Simulation completed.
    
    
                                          SIMULATION RESULTS: DatacenterBrokerSimple18ResourceManager
    
    Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|CPU Cost|Bandwidth Cost|Total Cost
          ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|     USD|           USD|       USD
    ----------------------------------------------------------------------------------------------------------------------------------------
           0|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
           4|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
           8|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
          12|SUCCESS| 1|   0|        4| 0|        2|   67108864|          2|        0|      4699|    4699| $ 46.99|        $ 0.10|   $ 47.19
          50|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
          90|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
         130|SUCCESS| 1|   0|        4| 0|        2|       3000|          2|        0|      4699|    4700| $ 46.99|        $ 0.10|   $ 47.19
           1|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           5|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           9|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          13|SUCCESS| 1|   0|        4| 1|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           2|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           6|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          10|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          14|SUCCESS| 1|   1|        4| 2|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           3|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
           7|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          11|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          15|SUCCESS| 1|   1|        4| 3|        2|   67108864|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          20|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          60|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         100|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         140|SUCCESS| 1|   0|        4| 1|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          30|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          70|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         110|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         150|SUCCESS| 1|   1|        4| 2|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          40|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
          80|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         120|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
         160|SUCCESS| 1|   1|        4| 3|        2|       3000|          2|        0|      5371|    5371| $ 53.71|        $ 0.10|   $ 53.91
    ----------------------------------------------------------------------------------------------------------------------------------------
    INFO  Statistics for DatacenterBrokerSimple18ResourceManager for iteration 5:
    
    Cloud Architecture     : Cloud Architecture having 1 data center with 96 hosts using 'Nearest Host' VM allocation policy and Round-Robin load balancing of cloudlets
    Average Cloudlet Length: 69276569.00 MI
    Average Execution Time : 5218.95 seconds
    Average CPU Cost       : $ 52.19
    Average Bandwidth Cost : $ 0.10
    Average Total Cost     : $ 52.39
    
  So taking rationale look at it , the following would be the possible :
  
  - The scheduling algorithm was successfully implemented but the jobs had overlapping traits that lead to overlapping result view
  - The individual simulated jobs were implemented appropriately but the scheduling algorithm had overlapping events to schedule.


# Future Work

- We want to evaluate the above hypothesis comparing the simulation results on our defined configurations.

- Extend the job simulations towards iterative jobs like spark and scheduling policies like delayed-scheduling

- Most importantly introspect on what went wrong with such a long and exhaustive simulation.

