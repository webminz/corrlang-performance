# Performance Evaluation: CHECK FILE-based

Compares the execution time of checking the consistency of the well-known `Families2Persons` example.
Currently `CorrLang` is compared against an `Epsilon` based solution comprising _Matching_ (ECL), _Merging_ (EML) and _Validation_(EVL).
More tools for comparison can be added by writing a respective Ant task

## Folder Layout

```
.
├── bins						# The binary files that are required to run a tool
│   ├── Fam2PersGen.jar 		# The Generator application for creating random Families/Persons instances, you have built this yourself
│   └── corrlang.jar 			# The CorrLang binary, see the CorrLang repo for building instructions
├── build.xml 					# The ANT configuration. You can add your tool by adding a respective tasks in this file
├── data 						# The Generator applications populates this folder with test data when running the test
├── ivy.xml 					# The Ivy dependencies, which are used by ant 
├── ivysettings.xml 			# The Ivy configuration
├── lib 						# Folder that ivy uses to download dependencies
├── metamodels 					# Contains the ECORE metmodels for the Families/Persons scenario
├── output 						# If the tools produces any output it should be stored here
├── runComparison.sh  			# Shell script to run the benchmark
├── runtimeComparison.csv 		# The statistics of the benchmark execution
└── solutions 					# Contains all the files that are needed for configuring the respective tool for the scenario
    ├── corrlang
    └── epsilon
```

## Running the test

The benchmark requires two binaries for execution, which are not comitted.
Thus, you have to built them yourself:

* The `Fam2PersGen.jar` is created from the `../checkFileBased-files/Families2PersonsGenerator` Project. There is an ANT-build file called `mk_jar.xml` that builds the JAR. Before running it, you should open it once and change the highlighted properties with respect to your machine.
* How to build `corrlang.jar` is described [here](https://github.com/webminz/corr-lang#installation)

After both jar files are built and copied into the  `bins` folder you are able to run the benchmark by calling 
```
$ ./runComparison.sh
```
from the command line!

In that file you can also mofidy the numer of elements you want to run the benchmark with.

Execution is currently based on a UNIX-shell script. 
Thus you either have to be on a UNIX-like environment (Linux,MacOS,FreeBSD,...), use an emulator environmnet (like CYGWIN) or put the whole thing into a DOCKER container.


## Adding your own tool

The benchmark can easily be extended.
The execution is mainly based on _Apache Ant_, which is invoked by the shell script `runComparison.sh`.
To add a custom solution with a tool of you choice, add the required binaries, (meta)models, configuration files, scripts to the respective folders and add an ANT task in `build.xml`. You can also use Ivy for dependency management there.
Finally you have to add the call to you tool in  `runComparison.sh`, which already contains a template of how you add more tools.