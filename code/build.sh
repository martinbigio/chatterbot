#!/bin/bash

#
# Script que genera los .war de las 4 aplicaciones web.
#

mvn clean
cd maven-libs
./install_libs.sh
cd ..
mvn install

