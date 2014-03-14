'use strict';

angular.module('yoApp')
  .controller('MainCtrl', function ($scope,Executionsservice) {
        $scope.startExecution = function(){
            Executionsservice.startExecution({});
        }
        $scope.awesomeThings = [
          'HTML5 Boilerplate',
          'AngularJS',
          'Karma'
        ];
  });
