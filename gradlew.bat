@echo off
setlocal

set DIR=%~dp0

set APP_BASE_NAME=%~n0
set APP_HOME=%DIR%

set JAVA_EXE=java
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_EXE=%JAVA_HOME%\bin\java.exe
    )
)
if "%JAVA_EXE%"=="java" (
    if exist "C:\Program Files\Java\IntelliJ IDEA Community Edition 2025.2.4\jbr\bin\java.exe" (
        set "JAVA_EXE=C:\Program Files\Java\IntelliJ IDEA Community Edition 2025.2.4\jbr\bin\java.exe"
    )
)

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

"%JAVA_EXE%" "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

endlocal


