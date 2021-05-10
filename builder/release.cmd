call ../release/1_check_version_presence.cmd || GOTO :EOF
call ../release/2_apply_version_to_poms.cmd || GOTO :EOF
REM all individual modules, INCLUDING the builder project must first be built
call mvn clean package || GOTO :EOF
REM only then the EVS installation can be packaged
call mvn install -f ././../distribution/pom.xml || GOTO :EOF