'use strict';

angular.module('yoApp')
    .controller('NavCtrl', function ($scope, Executionsservice,$location) {
        $scope.username = "Checking access rights...";
        Executionsservice.promise.then(function () {
            Executionsservice.checkLogin().then(function (user) {
                $scope.username = user.email;
                if(!user.admin){
                    //window.uri = "#/denied"
                    $location.path("/denied");
                }
            });
        });
    });