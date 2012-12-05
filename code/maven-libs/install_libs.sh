#!/bin/bash

# Tag de ChartCreator para JSF
mvn install:install-file -Dfile=chartcreator-1.2.0.jar -DgroupId=net.sf -DartifactId=chartcreator -Dversion=1.2.0 -Dpackaging=jar -DgeneratePom=true

# Implementación y API de RI JSF
mvn install:install-file -Dfile=jsf-impl-1.2_09.jar -DgroupId=javax.faces -DartifactId=jsf-impl -Dversion=1.2_09 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=jsf-api-1.2_09.jar -DgroupId=javax.faces -DartifactId=jsf-api -Dversion=1.2_09 -Dpackaging=jar -DgeneratePom=true

# Para desarrollar plugins para openfire
mvn install:install-file -Dfile=maven-openfire-plugin-1.0.2-SNAPSHOT.jar -DgroupId=com.reucon.maven.plugins -DartifactId=maven-openfire-plugin -Dversion=1.0.2-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

# Openfire API
mvn install:install-file -Dfile=openfire.jar -DgroupId=org.igniterealtime.openfire -DartifactId=openfire -Dversion=3.6.0 -Dpackaging=jar -DgeneratePom=true 

# OpenNLP Tools
mvn install:install-file -Dfile=opennlp-tools-1.3.0.jar -DgroupId=opennlp -DartifactId=opennlp-tools -Dversion=1.3.0 -Dpackaging=jar -DgeneratePom=true;

# OpenNLP Maxent
mvn install:install-file -Dfile=maxent-2.4.0.jar -DgroupId=opennlp -DartifactId=opennlp-maxent -Dversion=2.4.0 -Dpackaging=jar -DgeneratePom=true;

# OpenNLP Trove (GNU)
mvn install:install-file -Dfile=trove.jar -DgroupId=gnu -DartifactId=trove -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true;

# Driver de Oracle
mvn install:install-file -DgroupId=ojdbc -DartifactId=ojdbc -Dversion=14 -Dpackaging=jar -Dfile=ojdbc14.jar


