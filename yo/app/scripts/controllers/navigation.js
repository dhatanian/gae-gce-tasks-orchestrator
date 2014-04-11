'use strict';

angular.module('yoApp')
    .controller('NavCtrl', function ($scope, Executionsservice, $location, $modal) {
        $scope.username = "Checking access rights...";
        $scope.login = Executionsservice.openLoginWindow;

        Executionsservice.promise.then(function () {
            Executionsservice.checkLogin().then(function (user) {
                $scope.username = user.email;
                if (!user.admin) {
                    //window.uri = "#/denied"
                    $location.path("/denied");
                }
            });
        });

        Executionsservice.needsLogin.then(function () {
            $modal.open(
                {
                    template: '<div class="modal-body"><p>Click here to be authenticated</p></div><div class="modal-footer"> <a class="btn btn-primary" ng-click="login();$close()">Log in</a></div>',
                    scope: $scope
                }
            );
        });
    });