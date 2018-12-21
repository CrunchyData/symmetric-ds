#!/usr/bin/env bash

curl -s get.sdkman.io | bash
source "/home/travis/.sdkman/bin/sdkman-init.sh"
sdk install groovy
mkdir -p /home/travis/.groovy/lib
pushd /home/travis/.groovy/lib
wget https://jdbc.postgresql.org/download/postgresql-42.2.5.jar
popd