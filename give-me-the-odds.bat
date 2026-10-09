@echo off
rem prints the odds of reaching the target in time, as a percentage.
rem
rem   give-me-the-odds <millennium-falcon.json> <empire.json>
rem
rem Build it first with: cd backend ^&^& mvnw package

setlocal

set JAVA_MAJOR=0
for /f "tokens=3 delims=.= " %%v in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /c:"java.specification.version" ^| findstr /v /c:"vm"') do set JAVA_MAJOR=%%v

if %JAVA_MAJOR% LSS 21 (
    echo Java 21 or later is required, found %JAVA_MAJOR%. 1>&2
    exit /b 3
)

set JAR=
for %%f in ("%~dp0backend\target\falcon-*.jar") do set JAR=%%f

if "%JAR%"=="" (
    echo Jar not found. Build it with: cd backend ^&^& mvnw package 1>&2
    exit /b 3
)

java -Dloader.main=com.falcon.falcon.cli.GiveMeTheOdds -jar "%JAR%" %*
