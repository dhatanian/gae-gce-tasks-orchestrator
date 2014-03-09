#Google Cloud Platform Task Orchestrator

This Google App Engine application runs user-defined tasks (shell scripts) on Google Compute Engine. You can use it as a replacement for Windows's scheduled tasks, for example.

##How to use the application
TODO

##Installation and setup
To install the App Engine application :

```
mvn appengine:update -Dapplication.appId=YOURAPPLICATIONID
```

You also need to create a Google Cloud Console project, enable the billing and the following APIs : Google Cloud Storage, Google Compute Engine.

The App Engine application's service account (YOURAPPLICATIONID@appspot.gserviceaccount.com) must be listed as a team member for this project, with WRITE rights.