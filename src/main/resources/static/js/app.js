var microFeedService = angular
	.module('microFeedService', ['ngRoute', 'ngCookies', 'angularUtils.directives.dirPagination', 'checklist-model'])
	.run(run);
 
microFeedService.config(function($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl : 'views/login.html',
			isLogin : true
		})
		.when('/login', {
			templateUrl : 'views/login.html',
			isLogin : true
		})
		.when('/search', {
			templateUrl : 'views/search.html'
		})
		.otherwise({
			redirectTo : '/'
		});
});

run.$inject = ['$rootScope','$location', '$cookieStore', '$http'];

function run($rootScope, $location, $cookieStore, $http) {
    // keep user logged in after page refresh
    $rootScope.globals = $cookieStore.get('globals') || {};

    if ($rootScope.globals.currentUser) {
    	$rootScope.isLoggedIn=true
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; 
    }
    
    $rootScope.$on('$routeChangeStart', function (event, next) {
        var userAuthenticated = $rootScope.globals.currentUser; /* Check if the user is logged in */

        if (!userAuthenticated && !next.isLogin) {
            // You can save the user's location to take him back to the same page after he has logged-in 
            $rootScope.savedLocation = $location.url();
            $location.path('/');
        }
        
    });    
	
    $rootScope.$on('Login',function(){
	// To identify whether a user has logged in or not so that a welcome message can be displayed
		$rootScope.isLoggedIn=true
	});

    $rootScope.$on('Logout',function(){
		$rootScope.isLoggedIn=false
	});
}