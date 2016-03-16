/**
 *
 */
package nike.feed.gcs.controller

import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


/**
 * 
 * @author kaarthikeyan.palani
 *
 */
@Controller
class LogoutController {

    // Define the logger object for this class
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/handleSuccessLogout", method= RequestMethod.GET)
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        def logoutStatus = ""
        def logoutMessage = ""
        def json

        try {
            logoutStatus = "success"
            logoutMessage = "You have successfully logged out !!!"
            json = new JsonBuilder([logoutStatus: logoutStatus, logoutMessage : logoutMessage])
            return json.toPrettyString()
        }
        catch (Exception e) {
            LOG.error("Exception occured during logout.")
            json = new JsonBuilder([logoutStatus: "", logoutMessage : ""])
            return json.toPrettyString()
        }
    }
}