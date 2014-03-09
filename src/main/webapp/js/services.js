var ReportServices = angular.module('ReportServices', [ 'ngResource' ]);

ReportServices.factory('Reports', [ '$resource', function($resource) {
	return $resource('/admin/reports/:reportId', {}, {});
} ]);