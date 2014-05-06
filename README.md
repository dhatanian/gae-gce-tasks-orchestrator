#Google Cloud Platform Task Orchestrator

This Google App Engine application runs user-defined tasks (shell scripts) on Google Compute Engine. You can use it as a replacement for Windows's scheduled tasks, for example.

![Task creation interface](../master/screenshots/createexecution.png)

##Installation and setup
To install the App Engine application :

```
mvn appengine:update -Dapplication.appId=YOURAPPLICATIONID
```

You also need to create a Google Cloud Console project, enable the billing and the following APIs : Google Cloud Storage, Google Compute Engine.

The App Engine application's service account (YOURAPPLICATIONID@appspot.gserviceaccount.com) must be listed as a team member for this project, with WRITE rights.

You must also update the client_id parameter in yo/app/services/executionService.js to use a properly configured client id with the scope https://www.googleapis.com/auth/userinfo.email and YOURAPPLICATIONID.appspot.com as the javascript origin.


##How to use the application

Once the application is configured and deployed, you can start a new execution.

The javascript interface will ask for a project id and for the location of the script/executable that it must run. The executable must be stored in Cloud Storage and you must provide the bucket and the path to the executable.

You must also provide a Cloud Storage bucket where the application will store the result of its execution.

You can also scheduled an execution using CRON expressions. The scheduler runs every 15 minutes so it is not extremely accurate for high frequencies.