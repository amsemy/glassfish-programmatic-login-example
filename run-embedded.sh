#!/bin/bash

TEST_HOME=$(dirname $(readlink -e $0))

mvn package

java -jar test-emb/target/test-emb.jar $TEST_HOME
