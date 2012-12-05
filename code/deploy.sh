#!/bin/bash


#
# Directorio base del Tomcat.
#
#TOMCAT_BASE_DIR=/home/andy/programas/apache-tomcat-6.0.18
TOMCAT_BASE_DIR=/home/agregoir/programas/apache-tomcat-6.0.18

#
# Copiamos las aplicaciones web
#
echo "Realizando deploy de la aplicacion..."
rm -fr $TOMCAT_BASE_DIR/webapps/chatterbot*
cp web/target/chatterbot-web.war $TOMCAT_BASE_DIR/webapps
cp server/target/chatterbot-server.war $TOMCAT_BASE_DIR/webapps
cp dispatcher/target/chatterbot-dispatcher.war $TOMCAT_BASE_DIR/webapps
cp business/target/chatterbot-business.war $TOMCAT_BASE_DIR/webapps

#
# Copiamos los diccionarios de OpenNLP al tmp.
#
echo "Copiando diccionarios de OpenNLP..."
cp -r server/target/classes/models /tmp

#
# Iniciamos el Tomcat
#
JAVA_OPTS='-Xmx2000M -XX:MaxPermSize=512m'
$TOMCAT_BASE_DIR/bin/catalina.sh run


