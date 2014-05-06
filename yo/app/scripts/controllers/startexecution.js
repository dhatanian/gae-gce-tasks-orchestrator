'use strict';

//5 hours
var DEFAULT_TIMEOUT_MS = 5 * 60 * 60 * 1000;

angular.module('yoApp')
    .controller('StartExecutionCtrl', function ($scope, Executionsservice) {

        function resetRequest() {
            $scope.executionRequest = {
                userScript: {timeoutMs: DEFAULT_TIMEOUT_MS},
                gceConfiguration: {
                    machineType: "n1-highmem-8",
                    zone: "us-central1-a",
                    image: "https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20131120"
                }
            };
        }

        resetRequest();

        $scope.registerExecution = function () {
            Executionsservice.registerExecution($scope.executionRequest).then(function (resp) {
                    toastr.success("Successfully started execution with id : " + resp.id);
                    resetRequest();
                },
                function (resp) {
                    toastr.error("Unable to start this execution : " + resp.message, "Error " + resp.code);
                }
            );
        }
    });
