#!/usr/bin/env bash
script_dir=$(dirname $0)
script_location="$script_dir/version_input.txt"
export newVersion=`cat "$script_location"`  && \
mvn versions:set -DnewVersion=$newVersion -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && \
cd ../super &&  mvn versions:set -DnewVersion=$newVersion  -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && \
cd ../distribution/ && mvn versions:set -DnewVersion=$newVersion  -DartifactId='*' -DgroupId='*' -DprocessAllModules=true && \
cd ../builder/ && mvn versions:commit  && \
cd ../super && mvn versions:commit  && \
cd ../distribution && mvn versions:commit  && \
cd ../builder/