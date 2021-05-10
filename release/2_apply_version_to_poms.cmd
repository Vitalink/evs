set script_dir=%~dp0
set script_location="%script_dir%\version_input.txt"
set /p new_version=<%script_location%

call mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=%new_version% -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && ^
cd ../super &&  call mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=%new_version%  -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && ^
cd ../distribution/ && mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=%new_version%  -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && ^
cd ../builder/ && call mvn versions:commit  && ^
cd ../super && call mvn versions:commit  && ^
cd ../distribution && call mvn versions:commit  && ^
cd ../builder/
