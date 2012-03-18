@echo off
mklink /D "%~dp0/mobilecombat-phonegap1.5\mobilecombat-phonegap1.5\www" "..\..\phonegap-src"
mklink /D "%~dp0/frontend-android\MobileCombatAndroid\assets\www" "..\..\..\phonegap-src"
PAUSE
