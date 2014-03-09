var ReportApp = angular.module('ReportApp', [ 'ReportServices' ]);

ReportApp.controller('ReportController', function($scope, Reports) {
	$scope.adminUser = "";
	$scope.submitDisabled = false;
	$scope.startReportCreation = function() {
		$scope.alertEnabled = false;
		$scope.submitDisabled = true;
		var adminUser = $scope.adminUser;
		if (adminUser != undefined && adminUser != null && adminUser != "") {
			Reports.save({
				"adminUser" : adminUser
			}, function(data) {
				$scope.submitDisabled = false;
				$scope.alertEnabled = false;
				$scope.adminUser = "";
				console.log("Job id : "+data.id);
				// TODO go to "follow the report creation" screen
			}, function(data) {
				$scope.submitDisabled = false;
				$scope.alertEnabled = true;
			});
		}
	};
});