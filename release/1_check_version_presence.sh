#!/usr/bin/env bash
script_dir=$(dirname $0)
script_location="$script_dir/version_input.txt"
if [ -s "$script_location" ]
then
   echo "Configured version: `cat "$script_location"`"
else
   echo "No version configured in file: $script_location"
   exit 1
fi

