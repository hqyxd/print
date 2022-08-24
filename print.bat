::%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
 
::默认PID，无需修改
set "PID=999999"
::记录当前目录，无需修改
set "CURRENT_PATH=%cd%"
::记录jar包路径，无需修改
set "SERVER_PATH=%cd%"
::指定java_home路径，不能起名JAVA_HOME，会覆盖环境变量
::set "JAVA_HOME_CUSTOM=D:\DevTool\jdk1.8.0_181"、
::指定jre_home
::set "JRE_HOME_CUSTOM=%JAVA_HOME_CUSTOM%\jre"
::指定maven_home，不能起名MAVEN_HOME，会覆盖环境变量
::set "MAVEN_HOME_CUSTOM=D:\DevTool\apache-maven-3.5.4"
 
 
::指定程序工作路径
::set "SERVICE_DIR=D:\xiaowei1\gitlabdep\chanpay-eureka\chanpay-eureka"
::指定程序包名
::set "JARNAME=chanpay-eureka.jar"
::指定程序端口号
set "port=8023"
::指定程序启动日志名
::set "LOG_FILE=chanpay-eureka.log"
 
 
 
::流程控制
::if "%1"=="start" (
call:STOP 
call:START
::) else (
::  if "%1"=="stop" ( 
::    call:STOP 
::  ) else ( 
::    if "%1"=="restart" (
::	  call:RESTART 
::	) else ( 
::	  call:DEFAULT 
::	)
::  )
::)
 
goto:eof
 
::启动jar包
:START
::echo function "start" starting...
::cd /d %SERVICE_DIR%
::call %MAVEN_HOME_CUSTOM%\bin\mvn clean install
::echo "%JRE_HOME_CUSTOM%\bin\java"
::start /b "%JRE_HOME_CUSTOM%\bin\" java.exe -Xms128m -Xmx512m -jar %SERVICE_DIR%\target\%JARNAME% > %SERVICE_DIR%\logs\%LOG_FILE%
cd /d %CURRENT_PATH%\jre\bin
::echo == service start success
java -jar -Djava.awt.headless=false %SERVER_PATH%\print.jar
goto:eof
 
 
::停止java程序运行
:STOP
echo function "stop" starting...
call:findPid
call:shutdown
echo == service stop success
goto:eof

::找到端口对应程序的pid
:findPid
echo function "findPid" start.
for /f "tokens=5" %%i in ('netstat -aon ^| findstr %port%') do (
    set "PID=%%i"
)
if "%PID%"=="999999" ( echo pid not find, skip stop . ) else ( echo pid is %PID%. )
goto:eof
 
::杀死pid对应的程序
:shutdown
if not "%PID%"=="999999" ( taskkill /f /pid %PID% )
goto:eof

::pause