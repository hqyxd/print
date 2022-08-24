::默认PID，无需修改
set "PID=999999"
::记录当前目录，无需修改
set "CURRENT_PATH=%cd%"
::记录jar包路径，无需修改
set "SERVER_PATH=%cd%"

::指定程序端口号
set "port=8023"

call:STOP
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