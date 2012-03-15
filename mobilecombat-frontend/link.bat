@echo off
mklink /D "%~dp0/frontend-wp7\mobilecombat-phonegap\www" "..\..\phonegap-src"
mklink /D "%~dp0/frontend-android\MobileCombatAndroid\assets\www" "..\..\..\phonegap-src"
PAUSE
