@echo off
setlocal enabledelayedexpansion
for /f %%a in ('powershell -command "Get-Date -Format 'yyyy-MM-dd HH:mm:ss'"') do set buildDate=%%a

echo version=1.0.0 > build.properties
echo build=!buildDate! >> build.properties

echo Updated build.properties with timestamp: !buildDate!