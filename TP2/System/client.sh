#!/bin/bash

pushd $(dirname $0) > /dev/null
basepath=$(pwd)
popd > /dev/null

clear

java -cp "$basepath"/dispatcher.jar:"$basepath"/lib/gson-2.8.2.jar:"$basepath"/shared.jar \
  -Djava.rmi.server.codebase=file:"$basepath"/shared.jar \
  -Djava.security.policy="$basepath"/policy \
  ca.polymtl.inf8480.tp2.dispatcher.Client $*
