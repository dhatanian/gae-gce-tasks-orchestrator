#! /bin/bash
cd /root
apt-get update
apt-get install -y wget

mkdir script-exec
cd script-exec

gsutil cp gs://%USER_SCRIPT_BUCKET%/%USER_SCRIPT_PATH% ./userscript.sh"

#run actual script with parameters and send to stderr.log and stdout.log
timeout %TIMEOUT_HOURS%h userscript.sh 1>stdout.log 2>stderr.log
wget -X POST %RESULT_URL% --post-data='{"executionId":"%EXECUTION_ID%","resultCode":'$?'}'

#copy the whole directory
cd..
gsutil cp -R script-exec gs://%RESULT_BUCKET%/gae-orchestrator-%EXECUTION_ID%
gcutil deleteinstance %INSTANCE_NAME% --zone=%ZONE% --project=%PROJECT_ID% --force --nodelete_boot_pd