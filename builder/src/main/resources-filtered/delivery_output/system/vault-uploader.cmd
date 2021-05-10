@echo off

title EVS ${pom.version}

REM Uncomment and complete following line to manually set the JAVA_HOME directory in this context
REM set JAVA_HOME=

set EVS_VERSION=${pom.version}

:CHECK_JAVA_HOME
set JAVA = %JAVA_HOME%\bin\java
echo Using JAVA_HOME: %JAVA_HOME%
if not "%JAVA_HOME%" == "" goto CHECK_JAVA_VERSION
echo JAVA_HOME is not set, execution will stop
echo Set JAVA_HOME to the directory of your local JRE/JDK and retry
goto END


:CHECK_JAVA_VERSION
PATH %JAVA_HOME%\bin\;%PATH%
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"
if %jver% LSS 18000 (
  echo java version is too low
  echo current java version: %jver%
  echo at least 1.8 is needed
  goto END
)

:LAUNCH
cd %~dp0
REM the order of the CP property below is important. The connector dependency should be put before the dependency-jars folder dependency, to avoid this exception: 
REM Caused by: be.ehealth.technicalconnector.exception.ConfigurationException: No Valid config. Reason[Configuration could not be validated : Could not find properties. [sessionmanager.samlattribute][sessionmanager.samlattributedesignator]]
java -Dfile.encoding=UTF-8 -Dlog4j.configuration=file:../config/log4j/log4j-uploader.properties -cp "./datagenerator-%EVS_VERSION%.jar;./core-%EVS_VERSION%.jar;./ehconnector-%EVS_VERSION%.jar;./validator-%EVS_VERSION%.jar;./viewer-%EVS_VERSION%.jar;./dependency-jars/*;../config/actors/*" org.imec.ivlab.datagenerator.uploader.UploaderRunner %*
goto END

:END
pause