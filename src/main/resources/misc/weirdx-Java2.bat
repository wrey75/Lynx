@echo off

rem set WEIRDX_HOME=\tmp\weirdx\misc
if "%WEIRDX_HOME%" == "" set WEIRDX_HOME=.

rem cd $WEIRDX_HOME

@echo on
java -classpath ".;%WEIRDX_HOME%\weirdx.jar" com.jcraft.weirdx.WeirdX
@echo off
rem cd ..
goto done

:done
