#!/bin/bash

TEST_HOME=$(dirname $(readlink -e $0))

mvn package

java -Djava.security.auth.login.config=$TEST_HOME/test-emb/src/main/resources/auth.conf \
        -jar test-emb/target/test-emb.jar $TEST_HOME
