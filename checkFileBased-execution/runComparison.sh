#!/usr/bin/env bash

# Adapt this variable to your system
ANT_BINARY='./apache-ant-1.9.15/bin/ant'

# Adapt these variables depending on how big models you want to have
NOOFELEMENTS_START=10000
NOOFELEMENTS_STOP=1000000
NOOFELEMENTS_STEPSIZE=10000

# Taks definition


callGenerator() {
	eval "$ANT_BINARY generateTestData -DnumberOfElements=$1" > /dev/null
	eval "cat data/Stats.properties| grep \"noOfInconsistencies\" | cut -d '=' -f2"
}

callCorrLang() {
	eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runCorrlang | grep \"Target runCorrlang: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 
}

callEpsilon() {
	eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger runEpsilon | grep \"Target runEpsilon: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
}

#callYOUR_TOOL_HERE() {
#	eval "$ANT_BINARY -logger org.apache.tools.ant.listener.ProfileLogger YOUR_TOOL_HERE | grep \"Target YOUR_TOOL_HERE: finished\" | cut -d '(' -f2 | cut -d 'm' -f1" 2> /dev/null
#}
# You could add you own tool to the comparison here by copy pasting on of the above functions, and change it to a respective ANT task you have to create


# Make a fresh statistics file 
if [[ -e ./runtimeComparison.csv ]]; then
	rm ./runtimeComparison.csv
	touch ./runtimeComparison.csv
else
	touch ./runtimeComparison.csv
fi


echo "Elements;Violations;CorrLang runtime (ms);Epsilon runtime (ms)" >> ./runtimeComparison.csv

VIOLATIONS=0
CORRLANG_RUNTIME=0
EPSILON_RUNTIME=0

# Main loop
for ((i = NOOFELEMENTS_START; i <= NOOFELEMENTS_STOP; i = i + NOOFELEMENTS_STEPSIZE )); do
	VIOLATIONS="$(callGenerator $i)"
	CORRLANG_RUNTIME="$(callCorrLang)"
	EPSILON_RUNTIME="$(callEpsilon)"
	# You could add your tool here! You must also add the variable print to the final line of the loop
	#YOUR_TOOL_HERE_RUNTIME="$(callYOUR_TOOL_HERE)"
	echo "$i;${VIOLATIONS};${CORRLANG_RUNTIME};${EPSILON_RUNTIME}" >> ./runtimeComparison.csv
done
