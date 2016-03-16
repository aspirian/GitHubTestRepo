(function () {
     
    var microFeedService = angular
        .module('microFeedService')
        .controller('LoginController', LoginController);
 
   LoginController.$inject = ['$location','$rootScope','AuthenticationService'];
    function LoginController($location, $rootScope, AuthenticationService) {
        var vm = this;
        vm.login = login;
		
        (function initController() {			
            // reset login status
            AuthenticationService.ClearCredentials();
            $rootScope.$emit('Logout');
        })();
 
        function login() {
            AuthenticationService.Login(vm.username, vm.password, function(response){
            	if (response.success) {
                    AuthenticationService.SetCredentials(vm.username, vm.password, response.displayName);
					// Notify all registered scope listeners that login event has been successful
                    $rootScope.$emit('Login');
					// Redirect the user to any page requested before login
                    if($rootScope.savedLocation){
                    	$location.path($rootScope.savedLocation);
                    } else {
						// redirect to search page
                    	$location.path('/search');
                    }					
                } else {
                	vm.error = response.message;
                }
            } );                
            
        };		
		return vm;
	}
 
})();