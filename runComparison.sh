#!/usr/bin/env bash


source globalVars.properties


export JAVACMD="$JRE_INSTALL_DIR/Contents/Home/bin/java"

# setting location of the Ant binary
ANT_BINARY="$ANT_INSTALL_DIR/bin/ant"


# Taks definition
callGenerator() {
	eval "$ANT_BINARY runGenerator -DnumberOfElements=$1 -DinconsistencyProbability=$2"  > /dev/null
	eval "cat data/generated/Stats.properties| grep \"noOfInconsistencies\" | cut -d '=' -f2"
}

callCorrLang() {
	eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runCorrlang | grep \"Target runCorrlang: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 
}

EPSILON_CUTOFF=1000 # Seemed to struggle with much more elements than 1000
callEpsilon() {
	if (( $1 > $EPSILON_CUTOFF )); then
		echo "n/a"
	else
		eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runEpsilon | grep \"Target runEpsilon: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
	fi
}


EPSILON_ALT_CUTOFF=20000  # Running with 20,000 elements took 47 minutes in a test, 30,000 took 109 minutes, which is a bit tooo long for my taste thus 20,000
callEpsilonAlt() {
	if (( $1 > $EPSILON_ALT_CUTOFF )); then
		echo "n/a"
	else
		eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runEpsilonAlt | grep \"Target runEpsilonAlt: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
	fi
}


EMOFLON_CUTOFF=300 # Going over 500 results in spurious deadlock within Neo4j
callEmoflonPrep() {
	if (( $1 > $EMOFLON_CUTOFF )); then
		echo "n/a"
	else
		eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger emoflonImport -Dp2PluginPool=$ECLIPSE_P2_PLUGINS_DIR | grep \"Target emoflonImport: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
	fi
}

callEmoflon() {
	if (( $RUN_EMOFLON = 1 )); then
		if (( $1 > $EMOFLON_CUTOFF )); then
			echo "n/a"
		else
			eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runEmoflon -Dp2PluginPool=$ECLIPSE_P2_PLUGINS_DIR | grep \"Target runEmoflon: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
		fi
	else 
			echo "n/a"
	fi
}


# You could add you own tool to the comparison here by copy pasting the following functions, and change it to a respective ANT task you have to create in `build.xml`

#YOUR_TOOL_HERE_RUNTIME=0 
#YOUR_TOOL_HERE_CUTOFF=1000000 (optional, you can define a cutoff if you about a certain number of elements where the consistency verification does not run reliably anymore)
#callYOUR_TOOL_HERE() {
#	eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger YOUR_TOOL_HERE | grep \"Target YOUR_TOOL_HERE: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
#}


# Header of the CSV result file
makeHeader() {
	# Add the name of your tool to the header if you want
	echo "No of elements;No of consistency violating elements;CorrLang runtime (ms);Epsilon (Merge) runtime (ms);Epsilon (NoMerge) runtime (ms);Emoflon (Database Import) runtime (ms);Emoflon (Consistency check) runtime (ms)"
}


# Main loop
INCONSISTENCY_PROBABILITY=( 0.3 0.5 0.7 0.9 0.1 )
# Doing 10 repetitions of the whole thing to avoid anomalies
for ((reps = 0; reps < 10; reps = reps + 1 )); do
	# doing it for various degrees of inconsistencies
	for prob in "${INCONSISTENCY_PROBABILITY[@]}"; do
		RESULT_FILE="results/$(date +"%Y_%m_%d_%H_%M_%s")_$prob.csv"
		touch $RESULT_FILE
		echo "$(makeHeader)" >> $RESULT_FILE
		# Running from 0 - 100, 100 - 1000, 1000 - 10.000, 10.000 - 100.000, 100.000 - 1.000.000
		NO_OF_ELEMENTS=10
		for ((j = 0; j < 5; j = j + 1)); do
			STARTSIZE=$NO_OF_ELEMENTS
			STEPSIZE=$NO_OF_ELEMENTS
			(( ENDSIZE=$STARTSIZE * 10 ))
			for ((i = $STARTSIZE; i <= $ENDSIZE; i = i + $STEPSIZE)); do
				NO_OF_ELEMENTS=$i
				VIOLATIONS="$(callGenerator $NO_OF_ELEMENTS $prob)"
				CORRLANG_RUNTIME="$(callCorrLang)"
				EPSILON_RUNTIME="$(callEpsilon $NO_OF_ELEMENTS)"
				EPSILON_ALT_RUNTIME="$(callEpsilonAlt $NO_OF_ELEMENTS)"
				EMOFLON_SETUPTIME="$(callEmoflonPrep $NO_OF_ELEMENTS)"
				EMOFLON_RUNTIME="$(callEmoflon $NO_OF_ELEMENTS)"
				# You could add your tool here! You must also add the variable print to the final line of the loop
				#YOUR_TOOL_HERE_RUNTIME="$(callYOUR_TOOL_HERE $NO_OF_ELEMENTS)"
				echo "${NO_OF_ELEMENTS};${VIOLATIONS};${CORRLANG_RUNTIME};${EPSILON_RUNTIME};${EPSILON_ALT_RUNTIME};${EMOFLON_SETUPTIME};${EMOFLON_RUNTIME}" >> $RESULT_FILE
			done
		done
	done
done



