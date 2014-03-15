'use strict';

var googleLibraryLoaded = false;
var user = "Anonymous user";
var userPicUrl = "/questionmark.png";

function onGoogleLibraryLoaded() {
    var orchestratorLoaded = false;
    var oauthLoaded = false;
    var userAuthenticated = false;

    var globalCallback = function () {
        if (orchestratorLoaded && oauthLoaded && userAuthenticated) {
            googleLibraryLoaded = true;
            //TODO set the username here
            googleLibraryCallback();
        }
    }

    var signin = function (immediate, callback) {
        gapi.auth.authorize({client_id: "102862643449-geb89aoann7dj6tsha4mtkhvos5mk01b.apps.googleusercontent.com", scope: "https://www.googleapis.com/auth/userinfo.email",
            immediate: immediate}, callback);
    }

    var authorizationCallback = function (data) {
        if (data == null) {
            signin(false, authorizationCallback);
        } else {
            userAuthenticated = true;
            globalCallback();
        }
    }

    signin(true, authorizationCallback);

    var ROOT = '/_ah/api';
    gapi.client.load('orchestrator', 'v1', function () {
        orchestratorLoaded = true;
        globalCallback();
    }, ROOT);

    gapi.client.load('oauth2', 'v2', function () {
        console.log("oauth2 loaded")
        oauthLoaded = true;
        globalCallback();
    });
}

var googleLibraryCallback = function () {
}

angular.module('yoApp')
    .service('Executionsservice', function Executionsservice($q) {
        // AngularJS will instantiate a singleton by calling "new" on this function

        var deferred = $q.defer();
        if (googleLibraryLoaded) {
            deferred.resolve();
        } else {
            googleLibraryCallback = function () {
                deferred.resolve();
            }
        }

        return {
            startExecution: function (execution) {
                gapi.client.orchestrator.executions.start(execution).execute(function (resp) {
                    //TODO defer
                    console.log(resp);
                });
            },
            promise: deferred.promise
        }
    });
