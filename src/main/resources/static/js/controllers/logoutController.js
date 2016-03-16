(function () {
     
    var microFeedService = angular
        .module('microFeedService')
        .controller('LogoutController', LogoutController);
 
   LogoutController.$inject = ['$location', '$rootScope', 'AuthenticationService'];
    function LogoutController($location, $rootScope, AuthenticationService) {
        var vm = this;
        vm.logout = logout;
 
        function logout() {
        	 AuthenticationService.Logout(function(response){
             	if (response.success) {
             		 AuthenticationService.ClearCredentials();	
             		 vm.message = response.message;
             		 $rootScope.savedLocation = null;
             		 // Notify all registered scope listeners that logout event has been called
             		 $rootScope.$emit('Logout');
             		 $location.path('/');
                 } else {
                 	vm.error = response.message;
                 }
             } );  
        	 
        };		
		return vm;
    } 
})();