'use strict';

angular.module('yoApp')
    .controller('ExecutionsCtrl', function ($scope, Executionsservice, dateFilter, $modal) {
        var pageToken = null;
        $scope.noMorePage=false;
        $scope.executions = [];
        $scope.loading = false;
        var today = new Date();
        $scope.todayDate = today;
        $scope.toDate = $scope.todayDate;

        var oneDayAgo = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 1);
        $scope.fromDate = oneDayAgo;

        $scope.infiniteScroll = function () {
            $scope.loading = true;
            Executionsservice.listExecutions($scope.fromDate, $scope.toDate, pageToken, 10).then(function (resp) {
                    if (resp.items != null && resp.items != undefined && resp.items != []) {
                        $scope.executions = $scope.executions.concat(resp.items);
                    }
                    if (resp.nextPageToken != undefined) {
                        pageToken = resp.nextPageToken;
                    }else{
                        $scope.noMorePage = true;
                    }
                    $scope.loading = false;
                }
            );
        }

        $scope.showExecutionDetails = function (execution) {
            $modal.open({
                templateUrl: 'executionmodal.html',
                controller: ExecutionModalCtrl,
                resolve: {
                    execution: function () {
                        return execution;
                    }
                }
            });
        }

        $scope.getClassForExecution = function (execution) {
            if (execution.backendResult == null || execution.backendResult == undefined || execution.backendResult == {}) {
                return "";
            } else {
                if (execution.backendResult.resultCode == 0) {
                    return "success";
                } else {
                    return "warning";
                }
            }
        }

        $scope.$watch("fromDate", function () {
            pageToken = null;
            $scope.executions = [];
            $scope.infiniteScroll();
        });

        $scope.$watch("toDate", function () {
            pageToken = null;
            $scope.executions = [];
            $scope.infiniteScroll();
        })
    });

var ExecutionModalCtrl = function ($scope, $modalInstance, execution) {
    $scope.execution = execution;
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
}