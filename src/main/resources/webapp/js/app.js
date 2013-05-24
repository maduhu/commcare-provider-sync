'use strict';

/* put your routes here */

angular.module('commcare-provider-sync', ['motech-dashboard', 'YourModuleServices', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider
            .when('/welcome', { templateUrl: '../commcare-provider-sync/resources/partials/welcome.html', controller: YourController })
            .otherwise({redirectTo: '/welcome'});
    }]);
