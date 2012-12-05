REM de ChartCreator para JSF
CALL mvn install:install-file -Dfile=chartcreator-1.2.0.jar -DgroupId=net.sf -DartifactId=chartcreator -Dversion=1.2.0 -Dpackaging=jar -DgeneratePom=true

REM Implementación y API de RI JSF
CALL mvn install:install-file -Dfile=jsf-impl-1.2_09.jar -DgroupId=javax.faces -DartifactId=jsf-impl -Dversion=1.2_09 -Dpackaging=jar -DgeneratePom=true

CALL mvn install:install-file -Dfile=jsf-api-1.2_09.jar -DgroupId=javax.faces -DartifactId=jsf-api -Dversion=1.2_09 -Dpackaging=jar -DgeneratePom=true

REM Para desarrollar plugins para openfire
CALL mvn install:install-file -Dfile=maven-openfire-plugin-1.0.2-SNAPSHOT.jar -DgroupId=com.reucon.maven.plugins -DartifactId=maven-openfire-plugin -Dversion=1.0.2-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

REM Openfire API
CALL mvn install:install-file -Dfile=openfire.jar -DgroupId=org.igniterealtime.openfire -DartifactId=openfire -Dversion=3.6.0 -Dpackaging=jar -DgeneratePom=true 

REM OpenNLP Tools
CALL mvn install:install-file -Dfile=opennlp-tools-1.3.0.jar -DgroupId=opennlp -DartifactId=opennlp-tools -Dversion=1.3.0 -Dpackaging=jar -DgeneratePom=true;

REM OpenNLP Maxent
CALL mvn install:install-file -Dfile=maxent-2.4.0.jar -DgroupId=opennlp -DartifactId=opennlp-maxent -Dversion=2.4.0 -Dpackaging=jar -DgeneratePom=true;

REM OpenNLP Trove (GNU)
mvn install:install-file -Dfile=trove.jar -DgroupId=gnu -DartifactId=trove -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true;

REM JTA Transaction
CALL mvn install:install-file -Dfile=jta-1_0_1B-classes.zip -DgroupId=javax.transaction -DartifactId=jta -Dversion=1.0.1B -Dpackaging=jar -DgeneratePom=true;

