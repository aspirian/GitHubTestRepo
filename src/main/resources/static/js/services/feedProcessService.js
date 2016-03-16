(function () {
     
    angular
        .module('microFeedService')
        .factory('FeedProcessService', FeedProcessService);
 
    FeedProcessService.$inject = ['$http'];
    function FeedProcessService($http) {
        var service = {};
        service.process = process;
 
        /**
         * Search service method to get the data from DB that matches the item numbers. 
         */
        function process(feedItems, callback) {
 
        	var pattern = /('(.*?)'|"(.*?)"|[^\s\"',]+)/g;
        	var itemNumbers = 'products='+feedItems.match(pattern);
        	var response;
        	
        	$http({
        	    method: 'POST',
        	    url: '/convertXML',
        	    data : itemNumbers,
        	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        	}).success(function(data, status, header, config) {
        		response = data.statusResponse;
        		callback(response);
            }).error(function(data, status, headers, config) {
            	//window.alert("failure");
            	response = "FAILED";
            	callback(response);
            });
 
        }
 
        return service;
    };
 
})();