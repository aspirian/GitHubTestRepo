/**
 *
 */
package nike.feed.gcs.controller

import groovy.json.JsonBuilder

import javax.naming.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import com.mascon.ldap.LDAPAuthenticator

import nike.feed.gcs.model.User

/**
 * Simple controller for logging into GCS using LDAP authentication
 *
 * @author kaarthikeyan.palani
 *
 */
@Controller
class LoginController {

    // Define the logger object for this class
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/handleSuccessLogin", method= RequestMethod.GET)
    @ResponseBody
    public String handleSuccess(HttpServletRequest request, HttpServletResponse response) {

        def displayName = ""
        def loginStatus = ""
        def json

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            if(user){
                displayName = user.displayName
            }
                        
            loginStatus = "success"
            json = new JsonBuilder([loginStatus : loginStatus , displayName: displayName])
            return json.toPrettyString()
        }
        catch (Exception e) {
            LOG.error("Exception occured during login.")
            json = new JsonBuilder([loginStatus : loginStatus , displayName: displayName])
            return json.toPrettyString()
        }
    }
    
    @RequestMapping(value="/handleFailureLogin", method= RequestMethod.GET)
    @ResponseBody
    public String handleFailure(HttpServletRequest request, HttpServletResponse response) {

        def displayName = ""
        def loginStatus = ""
        def json

        try {
            loginStatus = "failure"
            json = new JsonBuilder([loginStatus : loginStatus , displayName: displayName])
            return json.toPrettyString()
        }
        catch (Exception e) {
            LOG.error("Exception occured during login.")
            json = new JsonBuilder([loginStatus : loginStatus , displayName: displayName])
            return json.toPrettyString()
        }
    }
}