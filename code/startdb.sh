#!/bin/bash
cd web
mvn sql:execute
mvn -e exec:java
cd ..
