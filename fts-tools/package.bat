@ECHO OFF

SET "THIS_DIR=%~dp0"
SET CLASSPATH=%THIS_DIR%\build\classes\java\main
SET JAVA_HOME=C:\Program Files\Android\Android Studio\jbr

mkdir build 2>NUL
"%JAVA_HOME%\bin\java" -classpath %CLASSPATH% fts.tools.Merger build
