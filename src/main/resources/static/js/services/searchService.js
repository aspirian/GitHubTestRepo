(function () {
     
    angular
        .module('microFeedService')
        .factory('SearchService', SearchService);
 
    SearchService.$inject = ['$http'];
    function SearchService($http) {
        var service = {};
        service.search = search;
 
        return service;
 
        /**
         * Search service method to get the data from DB that matches the item numbers. 
         */
        function search(searchItems, callback) {
 
        	var pattern = /('(.*?)'|"(.*?)"|[^\s\"',]+)/g;
        	var itemNumbers = 'items='+searchItems.match(pattern);
        	var response;
        	
        	$http({
        	    method: 'POST',
        	    url: '/products',
        	    data : itemNumbers,
        	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        	}).success(function(data, status, header, config) {
        		response = data;
        		callback(response);
            }).error(function(data, status, headers, config) {
            	//window.alert("failure");
            	response = { status: false, message: 'Error' };
            	callback(response);
            });
 
        }
 
       
    };
 
})();