#!/bin/sh

DIRM=`pwd`

DIRM_L="$DIRM/../libs"

SAT4J_DIR="$DIRM_L/org.sat4j.core.jar"

LOGICNG_DIR="$DIRM_L/logicng-2.4.1.jar"

ANTLR_DIR="$DIRM_L/antlr-runtime-4.9.3.jar"

CLASSPATH=".:$CLASSPATH:$DIRM:$DIRM_L:$SAT4J_DIR:$LOGICNG_DIR:$ANTLR_DIR"

export CLASSPATH

javac *.java
args=("P1" "P2" "P3")
args2=("LARGE0" "LARGE1" "LARGE2" "LARGE3" "LARGE4" "LARGE5" "LARGE6" "LARGE7" "LARGE8" "LARGE9")

# Loop through each argument in the list
for agent in "${args[@]}"
do
  for prob in "${args2[@]}"
  do
    java A3main "$agent" "$prob"
  done
done

