echo off

set script_dir=%~dp0
set script_location="%script_dir%\version_input.txt"

for /f %%i in (%script_location%) do set size=%%~zi
set /p new_version=<%script_location%

if %size% GTR 0 echo Configured version: %new_version%
if %size% LEQ 0 echo No version configured in file: %script_location% && exit /b 1


