@echo off
echo Starting VideoWorker...
echo Classpath: build\classes;src\main\webapp\WEB-INF\lib\*
java -cp "build\classes;src\main\webapp\WEB-INF\lib\*" worker.VideoWorker %*
if %ERRORLEVEL% NEQ 0 (
    echo Error running VideoWorker. Ensure the project is built in Eclipse (Project > Build All).
)
pause
