'use strict';

angular.module('yoApp')
    .controller('AdminsCtrl', function ($scope, Executionsservice, dateFilter, $modal) {
        var pageToken = null;
        $scope.admins = [];
        $scope.loading = false;

        $scope.addAdmin = function () {
            Executionsservice.addAdmin({email: $scope.newAdmin});
        }

        $scope.addAdmin = function (admin) {
            Executionsservice.deleteAdmin(admin);
        }

        $scope.infiniteScroll = function () {
            $scope.loading = true;
            Executionsservice.listAdmins(pageToken, 10).then(function (resp) {
                    if (resp.items != null && resp.items != undefined && resp.items != []) {
                        $scope.admins = $scope.admins.concat(resp.items);
                    }
                    if (resp.nextPageToken != undefined) {
                        pageToken = resp.nextPageToken;
                    }
                    $scope.loading = false;
                }
            );
        }

        $scope.infiniteScroll();
    });
