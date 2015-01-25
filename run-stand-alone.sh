#!/bin/bash

TEST_HOME=$(dirname $(readlink -e $0))
DOMAIN_NAME=gfpl-example

mvn package

asadmin create-domain \
        --user admin \
        --passwordfile $TEST_HOME/test-app/src/main/resources/password.txt \
        $DOMAIN_NAME
asadmin start-domain $DOMAIN_NAME
asadmin create-file-user \
        --groups user_group \
        --passwordfile $TEST_HOME/test-app/src/main/resources/password.txt \
        user
asadmin deploy $TEST_HOME/test-ear/target/test-ear.ear

java -jar $TEST_HOME/test-app/target/test-app.jar $TEST_HOME

asadmin undeploy test-ear
asadmin stop-domain $DOMAIN_NAME
asadmin delete-domain $DOMAIN_NAME
