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
                var deferred = $q.defer();
                gapi.client.orchestrator.executions.start(execution).execute(function (resp) {
                    if (resp) {
                        deferred.resolve(resp)
                    } else {
                        deferred.reject(resp);
                    }
                });
                return deferred.promise;
            },
            listExecutions: function (fromDate, toDate, cursor, limit) {
                var deferred = $q.defer();
                var options = {"cursor": cursor, "limit": limit, "fromDate": fromDate, "toDate": toDate};
                gapi.client.orchestrator.executions.list(options).execute(function (resp) {
                    if (resp) {
                        deferred.resolve(resp)
                    } else {
                        deferred.reject(resp);
                    }
                });
                return deferred.promise;
            },
            addAdmin: function (admin) {
                var deferred = $q.defer();
                gapi.client.orchestrator.admins.add(admin).execute(function (resp) {
                    if (resp) {
                        deferred.resolve(resp)
                    } else {
                        deferred.reject(resp);
                    }
                });
                return deferred.promise;
            },
            deleteAdmin: function (admin) {
                var deferred = $q.defer();
                gapi.client.orchestrator.admins.delete(admin).execute(function (resp) {
                    if (resp==undefined) {
                        deferred.resolve(resp)
                    } else {
                        deferred.reject(resp);
                    }
                });
                return deferred.promise;
            },
            listAdmins: function (cursor, limit) {
                var deferred = $q.defer();
                var options = {"cursor": cursor, "limit": limit};
                gapi.client.orchestrator.admins.list(options).execute(function (resp) {
                    if (resp) {
                        deferred.resolve(resp)
                    } else {
                        deferred.reject(resp);
                    }
                });
                return deferred.promise;
            },
            promise: deferred.promise
        }
    });