(function () {
     
    angular
        .module('microFeedService')
        .factory('AuthenticationService', AuthenticationService);
 
    AuthenticationService.$inject = ['$http', '$cookieStore', '$rootScope', '$timeout'];
    function AuthenticationService($http, $cookieStore, $rootScope, $timeout, UserService) {
        var service = {};
 
        service.Login = Login;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;
        service.Logout = Logout;
 
        return service;
 
        /**
         * Login method to interact with backend server
         */
        function Login(username, password, callback) {
 
        	var credentials = 'username=' + username + '&password=' + password;
        	var response = { success: false, message: 'Username or password is incorrect' };
        	
        	$http({
        	    method: 'POST',
        	    url: '/authenticate',
        	    data : credentials,
        	    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        	}).success(function(data, status, headers, config) {
        		if(typeof data != 'undefined' && data != null){
        			angular.forEach(data, function(value, key){ 
        				if(key == "loginStatus" && value != "failure"){
        					response = { success: true, displayName : "guest" };
        				} else if (key == "displayName" && value != ""){
        					response = { success: true, displayName : value };
        				}
        			});
        		}  
        		callback(response);
            }).error(function(data, status, headers, config) {
            	callback(response);
            });
        }
        
        /**
         * Logout method to interact with backend server
         */
        function Logout(callback) {
        	var response = { success: false};
        	$http({
        	    method: 'POST',
        	    url: '/logout'
        	}).success(function(data, status, headers, config) {
        		if(typeof data != 'undefined' && data != null){
        			angular.forEach(data, function(value, key){ 
        				if(key == "logoutStatus" && value != "failure"){
        					response = { success: true, message : "" };
        				} else if (key == "logoutMessage" && value != ""){
        					response = { success: true, message : value };
        				}
        			});
        		}  
        		callback(response);
            }).error(function(data, status, headers, config) {
            	callback(response);
            });
        }
 
        /**
         * Encode and set credentials in cookie for session management
         */
        function SetCredentials(username, password, displayName) {
            var authdata = Base64.encode(username + ':' + password);
 
            $rootScope.globals = {
                currentUser: {
                    username: username,
                    authdata: authdata,
                    displayName: displayName
                }
            };
 
            $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata; 
            $cookieStore.put('globals', $rootScope.globals);
        }
 
        /**
         * Clear credentials
         */
        function ClearCredentials() {
            $rootScope.globals = {};
            $cookieStore.remove('globals');
            $http.defaults.headers.common.Authorization = 'Basic ';
        }
    }
 
    // Base64 encoding service used by AuthenticationService
    var Base64 = {
 
        keyStr: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=',
 
        encode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;
 
            do {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
 
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
 
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
 
                output = output +
                    this.keyStr.charAt(enc1) +
                    this.keyStr.charAt(enc2) +
                    this.keyStr.charAt(enc3) +
                    this.keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            } while (i < input.length);
 
            return output;
        },
 
        decode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;
 
            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            var base64test = /[^A-Za-z0-9\+\/\=]/g;
            if (base64test.exec(input)) {
                window.alert("There were invalid base64 characters in the input text.\n" +
                    "Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
                    "Expect errors in decoding.");
            }
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
            do {
                enc1 = this.keyStr.indexOf(input.charAt(i++));
                enc2 = this.keyStr.indexOf(input.charAt(i++));
                enc3 = this.keyStr.indexOf(input.charAt(i++));
                enc4 = this.keyStr.indexOf(input.charAt(i++));
 
                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;
 
                output = output + String.fromCharCode(chr1);
 
                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }
 
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
 
            } while (i < input.length);
 
            return output;
        }
    };
 
})();