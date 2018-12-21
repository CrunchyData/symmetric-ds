#!/usr/bin/env bash

curl -s get.sdkman.io | bash
source "/home/travis/.sdkman/bin/sdkman-init.sh"
sdk install groovy