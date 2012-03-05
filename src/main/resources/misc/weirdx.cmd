@echo off

rem set SWING_HOME=\swing-1.1
rem set JAVA_HOME=\jdk1.1.6

rem if "%SWING_HOME%" == "" goto nohome_swing

if "%JAVA_HOME%" == "" goto nohome_java
if "%CLASSPATH%" == "" set CLASSPATH=%JAVA_HOME%\lib\classes.zip

rem set WEIRDX_HOME=\tmp\weirdx\misc
if "%WEIRDX_HOME%" == "" set WEIRDX_HOME=.

rem cd $WEIRDX_HOME

@echo on
%JAVA_HOME%\bin\java -classpath ".;%WEIRDX_HOME%\weirdx.jar;%SWING_HOME%;%SWING_HOME%\swing.jar;%CLASSPATH%" com.jcraft.weirdx.WeirdX
@echo off
rem cd ..
goto done

:nohome_swing
echo No SWING_HOME environment variable set.
goto done

:nohome_java
echo No JAVA_HOME environment variable set.
goto done

:nohome_jxpie
echo No WEIRDX_HOME environment variable set.
:done
