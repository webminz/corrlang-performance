# Performance Evaluation: CHECK FILE-based

Compares the consistency verification execution time of various model management tools at the  `Families2Persons` use case.

## Folder Layout

```
.
├── bins						# The pre-built Java-binaries 
│ ├── Fam2PersGen.jar 		    # The Generator application for creating random Families/Persons instances,
│ └── corrlang.jar 			    # The CorrLang binary, see the CorrLang repo for more information
├── build.xml 					# The ANT configuration. You can add your tool here, see intructions below
├── data 						# Contains example model files (i.e. Families and Persons instances)
│ ├── examples                  # Contains some demo files representing consistent and inconsistent examples, that you can use for testing you tools
│ └── generated                 # This folder gets populated by the generator application
├── generator-workspace         # An eclipse workspace that containts the Families2Persons generator application
├── globalVars.properties       # Contains central machine-dependent configuration parameters
├── ivy.xml 					# The Ivy dependencies, which are used by ant 
├── ivysettings.xml 			# The Ivy configuration
├── lib 						# Folder that will be created by ivy to store the downloaded dependencies
├── metamodels 					# Contains the ECORE metmodels for the Families2Persons scenario
├── README.md                   # This file
├── runComparison.sh  			# Shell script to run the benchmark
└── solutions 					# Contains the files that are needed for the respective tool
    ├── corrlang
    ├── emoflon
    └── epsilon
```

## Running the benchmark

The benchmark uses `ant` for running the different tools.
Thus, you have to download [Apache Ant](https://ant.apache.org/) if you do not already have and update the value of the `ANT_INSTALL_DIR` in `globalVars.properties` accordingly.
Next, you have to make sure that `JRE_INSTALL_DIR` points to a JDK root directory with Java version 14 or higher.


Afterwards, you should be able to  
```
$> ./runComparison.sh
```
from the command line!


Execution is currently based on a UNIX-shell script. 
Thus you either have to be on a UNIX-like environment (Linux,MacOS,FreeBSD,...), use an emulator environmnet (like CYGWIN) or put the whole thing into a DOCKER container.

## Running emoflon

`eMoflon::Neo` is included in this benchmark. But, since it has several external dependencies that require some set up and configuration, they are not run by default.
If you want to run this tool on you machine, please, follow these steps:

1. Make sure that the `ECLIPSE_P2_PLUGINS_DIR` property in `globalVars.properties` points to the respective P2 pool directory on you machine. 
2. Install [Neo4J](https://neo4j.com/) on your machine.
3. Follow the installation instructions for eMoflon::Neo [here](https://github.com/eMoflon/emoflon-neo).
4. Import the `solutions/emoflon/FamiliesToPersons` project into an Eclipse workspace where the emoflon plugin is installed and compile the sources.
4. Set the `RUN_EMOFLON` property in `globalVars.properties` to `1`!


## Adding your own tool

The benchmark can easily be extended:
The execution is based on _Apache Ant_, which is invoked by the shell script `runComparison.sh`.
To add a custom solution with a tool of you choice, add the required binaries, (meta)models, configuration files, scripts to the respective folders and add an ANT task in `build.xml`. You can also use Ivy for dependency management there.
Finally you have to add the call to you tool in  `runComparison.sh`, which already contains a template of how you add more tools.