#!/bin/bash

echo -n -e "\033]0;EVS - Exporter ${pom.version}\007"

: '
Uncomment and complete following line to manually set the JAVA_HOME directory in this context
JAVA_HOME=
'

EVS_VERSION=${pom.version}

: '
The order of the CP property below is important. The connector dependency should be put before the dependency-jars folder dependency, to avoid this exception:
Caused by: be.ehealth.technicalconnector.exception.ConfigurationException: No Valid config. Reason[Configuration could not be validated : Could not find properties. [sessionmanager.samlattribute][sessionmanager.samlattributedesignator]]
'

cd "$(dirname "$0")"

java -Dfile.encoding=UTF-8 -Dlog4j.configuration=file:../config/log4j/log4j-exporter.properties -cp "./datagenerator-$EVS_VERSION.jar:./core-$EVS_VERSION.jar:./ehconnector-$EVS_VERSION.jar:./validator-$EVS_VERSION.jar:./viewer-$EVS_VERSION.jar:./dependency-jars/*:../config/actors/*" org.imec.ivlab.datagenerator.exporter.VaultExporterRunner "$@"


$SHELL
