@echo off
mklink /D "%~dp0/frontend-windows\frontend-windows\www" "..\..\phonegap-src"
mklink /D "%~dp0/frontend-android\MobileCombatAndroid\assets\www" "..\..\..\phonegap-src"
PAUSE
