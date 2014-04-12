'use strict';

angular.module('yoApp')
    .controller('ScheduledCtrl', function ($scope, Executionsservice, $modal) {
        var pageToken = null;
        $scope.noMorePage=false;
        $scope.executions = [];
        $scope.loading = false;

        $scope.infiniteScroll = function () {
            $scope.loading = true;
            Executionsservice.listScheduledExecutions(pageToken, 10).then(function (resp) {
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

        $scope.deleteExecution = function (execution) {
            Executionsservice.deleteScheduledExecution(execution).then(function(){
                $scope.executions = $scope.executions.filter(function(execution2) {
                    return execution != execution2;
                });
            });
        }

        $scope.showExecutionDetails = function (execution) {
            $modal.open({
                templateUrl: 'scheduledexecutionmodal.html',
                controller: ScheduledExecutionModalCtrl,
                resolve: {
                    execution: function () {
                        return execution;
                    }
                }
            });
        }
    });

var ScheduledExecutionModalCtrl = function ($scope, $modalInstance, execution) {
    $scope.execution = execution;
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
}