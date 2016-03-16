(function () {
     
    angular
        .module('microFeedService')
        .controller('SearchController', SearchController);
 
    SearchController.$inject = ['$scope', 'SearchService', 'FeedProcessService'];
    function SearchController($scope, SearchService, FeedProcessService) {
         $scope.searchControl = true;
         $scope.selectAllCheckBoxFlag = false;
         $scope.showResultMessage=false;
         //$scope.paginationLimit = 2
         var productsListed = [];
         
         $scope.selectProduct = {
        		 values: []
         };
         
         
         var checkAll = function() {
        	 var productsJsonString = JSON.stringify(productsListed);
        	    $scope.selectProduct.values = productsListed;
        	  };
        	  
         var uncheckAll = function() {
        	 $scope.selectProduct.values = [];
        	  };
        	  
         $scope.search = function() {
        	$scope.selectProduct.values = [];
        	$scope.dataSearching = true;
        	SearchService.search($scope.searchItems, function(response){
        		$scope.searchResult  = response;
        		$scope.dataSearching = false; 
        		$scope.searchControl = false;
        		$scope.showResultMessage=false;
        	});
        };
        
        $scope.sort = function(keyname){
            $scope.sortKey = keyname;   //set the sortKey to the param passed
            $scope.reverse = !$scope.reverse; //if true make it false and vice versa
        }
        
        $scope.toggleFormControl = function(){
        	$scope.searchControl = !$scope.searchControl;
        }
        
        $scope.togglecheckboxselection = function(selectAllCheckBoxFlag){
        	$scope.selectAllCheckBoxFlag = !selectAllCheckBoxFlag;
        	if($scope.selectAllCheckBoxFlag){
        		checkAll();
        	} else {
        		uncheckAll();
        	}
        	
        }
        
        $scope.proceed = function() {
        	var itemNumbers = "";
        	$scope.dataSearching = true;
        	$scope.showResultMessage=false;
        	for(var prod in $scope.selectProduct.values){
        		itemNumbers = itemNumbers + $scope.selectProduct.values[prod] + ",";
        	}
        	if(itemNumbers==""){
        			alert("Select the products to be processed from search result.");
        			$scope.dataSearching = false;
        	} else{
        	FeedProcessService.process(itemNumbers, function(response){
        		$scope.dataSearching = false;
        		$scope.showResultMessage=true;
        		if(response == "SUCCESS"){
        			$scope.successResponse = "Request has been successfully sent to GCS system";
        			$scope.failureResponse = null;
        		}
        		else{
        			$scope.successResponse = null;
        			$scope.failureResponse = "Request failed due to some issue! Please contact your administrator";
        		}
        	});
        }
        }
    }
 
})();