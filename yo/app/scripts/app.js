'use strict';

angular.module('yoApp', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'infinite-scroll',
    'ui.bootstrap'
])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/executions', {
                templateUrl: 'views/listexecutions.html',
                controller: 'ExecutionsCtrl',
                resolve: {
                    'ExecutionServiceReady': function (Executionsservice) {
                        return Executionsservice.promise;
                    }
                }
            })
            .when('/start', {
                templateUrl: 'views/startexecution.html',
                controller: 'StartExecutionCtrl',
                resolve: {
                    'ExecutionServiceReady': function (Executionsservice) {
                        return Executionsservice.promise;
                    }
                }
            })
            .when('/admins', {
                templateUrl: 'views/admins.html',
                controller: 'AdminsCtrl',
                resolve: {
                    'ExecutionServiceReady': function (Executionsservice) {
                        return Executionsservice.promise;
                    }
                }
            })
            .otherwise({
                redirectTo: '/executions'
            });
    });
