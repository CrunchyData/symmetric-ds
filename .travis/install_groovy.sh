#!/usr/bin/env bash

curl -s get.sdkman.io | bash
source "/home/travis/.sdkman/bin/sdkman-init.sh"
sdk install groovy
mkdir -p .groovy/lib
pushd .groovy/lib
wget https://jdbc.postgresql.org/download/postgresql-42.2.5.jar
popd