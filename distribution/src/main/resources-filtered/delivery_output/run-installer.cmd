@echo off

REM Uncomment and complete following line to manually set the JAVA_HOME directory in this context
REM set JAVA_HOME=

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

java -jar EVS-${pom.version}.jar