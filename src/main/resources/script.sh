#!/bin/bash
cd /root
mkdir script-exec
cd script-exec

#this tells the bash that all the commands should have their error and standard output redirected to files
exec 1>system.stdout 2>system.stderr

apt-get update
apt-get install -y wget

gsutil -q cp gs://%USER_SCRIPT_BUCKET%/%USER_SCRIPT_PATH% ./userscript.sh
chmod +x ./userscript.sh

#run actual script with parameters and send to stderr.log and stdout.log
timeout %TIMEOUT_HOURS%h ./userscript.sh 1>userscript.stdout 2>userscript.stderr
wget -X POST %RESULT_URL% --post-data='{"executionId":"%EXECUTION_ID%","resultCode":'$?'}'

#copy the whole directory
cd ..
gsutil -q cp -R script-exec gs://%RESULT_BUCKET%/gae-orchestrator-%EXECUTION_ID%
gcutil deleteinstance %INSTANCE_NAME% --zone=%ZONE% --project=%PROJECT_ID% --force --delete_boot_pd