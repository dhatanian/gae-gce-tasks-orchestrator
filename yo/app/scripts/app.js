'use strict';

angular.module('yoApp', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'infinite-scroll'
])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/executions', {
                templateUrl: 'views/main.html',
                controller: 'ExecutionsCtrl',
                resolve: {
                    'ExecutionServiceReady': function (Executionsservice) {
                        // MyServiceData will also be injectable in your controller, if you don't want this you could create a new promise with the $q service
                        return Executionsservice.promise;
                    }
                }
            })
            .otherwise({
                redirectTo: '/executions'
            });
    });
