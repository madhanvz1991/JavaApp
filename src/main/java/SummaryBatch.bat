@echo off

java -cp %CLASSPATH%"SummaryBatch.jar;ojdbc6.jar;jdom-2.0.5.jar;" com.cts.uw.uwd.SummaryBatch
pause
