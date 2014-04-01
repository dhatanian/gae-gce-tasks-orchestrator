'use strict';

angular.module('yoApp')
    .controller('ExecutionsCtrl', function ($scope, Executionsservice) {
        var pageToken = null;
        $scope.executions = [];
        $scope.loading=false;
        $scope.startExecution = function () {
            Executionsservice.startExecution({});
        }

        $scope.infiniteScroll = function() {
            $scope.loading=true;
            Executionsservice.listExecutions(pageToken, 10).then(function (resp) {
                    $scope.executions = $scope.executions.concat(resp.items);
                    pageToken = resp.nextPageToken;
                    $scope.loading=false;
                }
            );
        }

        $scope.infiniteScroll();
    }).controller('NavCtrl', function ($scope, Executionsservice) {
        $scope.username = user;
    });
