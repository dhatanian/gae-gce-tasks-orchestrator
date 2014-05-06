'use strict';

angular.module('yoApp')
    .controller('ScheduledCtrl', function ($scope, Executionsservice, $modal) {
        var pageToken = null;
        $scope.noMorePage = false;
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
                    } else {
                        $scope.noMorePage = true;
                    }
                    $scope.loading = false;
                }
            );
        }

        $scope.deleteExecution = function (execution, $event) {
            Executionsservice.deleteScheduledExecution(execution).then(function () {
                $scope.executions = $scope.executions.filter(function (execution2) {
                    return execution != execution2;
                });
            }, function (error) {
                toaster.error("Unable to delete this scheduled execution : " + resp.message, "Error " + resp.code);
            });

            // Prevent bubbling to showItem.
            // On recent browsers, only $event.stopPropagation() is needed
            if ($event.stopPropagation) $event.stopPropagation();
            if ($event.preventDefault) $event.preventDefault();
            $event.cancelBubble = true;
            $event.returnValue = false;
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