'use strict';

var googleLibraryLoaded = false;

function onGoogleLibraryLoaded() {
    var ROOT = '/_ah/api';
    gapi.client.load('orchestrator', 'v1', function () {
        googleLibraryLoaded = true;
        googleLibraryCallback();
    }, ROOT);
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
