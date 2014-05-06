'use strict';

angular.module('yoApp')
    .controller('AdminsCtrl', function ($scope, Executionsservice, dateFilter, $modal) {
        var pageToken = null;
        $scope.noMorePage = false;
        $scope.admins = [];
        $scope.loading = false;

        $scope.addAdmin = function () {
            Executionsservice.addAdmin({email: $scope.newAdmin}).then(function (admin) {
                $scope.admins.push(admin);
            });
            $scope.newAdmin = "";
        }

        $scope.deleteAdmin = function (admin) {
            Executionsservice.deleteAdmin(admin).then(function () {
                $scope.admins = $scope.admins.filter(function (admin2) {
                    return admin != admin2;
                });
            });
        }

        $scope.infiniteScroll = function () {
            $scope.loading = true;
            Executionsservice.listAdmins(pageToken, 10).then(function (resp) {
                    if (resp.items != null && resp.items != undefined && resp.items != []) {
                        $scope.admins = $scope.admins.concat(resp.items);
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
    });
