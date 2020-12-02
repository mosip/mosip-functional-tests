## Packet Generation Utility
This folder contains the codes of utility to generate packets from an available sample packet.  
It can be executed from inside an IDE or from terminal by converting it to a jar.  
All the resources by the utility are kept in src/main/resources, which get to run time resources directory via the class loader.<br/>
<br/>
config.properties file contains the properties needed at run time. Properties such as base url of an environment, number of threads, system path to generate packets are mentioned in this property file.<br/>
This file also contains the properties mentioning path of the pre-existing packets inside the project. These pre-existing packets are referred by the utility to generate new packets of similar file structures. <br/>
It works in various modes which are discussed below. The application accepts a command-line argument which decides the mode in which the utility executes:<br/>
