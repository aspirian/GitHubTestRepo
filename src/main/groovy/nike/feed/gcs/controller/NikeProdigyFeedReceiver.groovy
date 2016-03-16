/**
 * 
 */
package nike.feed.gcs.controller

import static nike.feed.gcs.controller.NikeProdigyFeedResponseUtil.*
import nike.feed.gcs.service.AcceptNikeProdigyFeedService

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

/**
 * Simple controller to accept NIKE PRODIGY Feed and stages into GCS staging tables
 *  
 * @author murugan.poornachandran
 *
 */
@Controller
@RequestMapping("/feed")
class NikeProdigyFeedReceiver {

	// Define the logger object for this class
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AcceptNikeProdigyFeedService feedService

	/**
	 * Accept the NIKE PRODIGY XML filename from MPX and send the GCS's FEED XML response
	 * 
	 * @param name - Input XML Feed file name (from MPX)
	 * 
	 * @param out - Response's Output writer object 
	 */
	@RequestMapping(method=[
		RequestMethod.GET,
		RequestMethod.POST
	], produces = "text/xml;charset=UTF-8")
	void receiveFeed(@RequestParam(value="name", required=false, defaultValue="") String name,
			Writer out) {
		try{
			Long logId = feedService.acceptFeed(name)
			out.write(getResponseXML(name, logId))
		}
		catch(FileNotFoundException nfe) {
			log.error("Input feed file ${name} not found to proceed further - ${nfe.getMessage()}")
			out.write(getResponseXML(name, nfe.getMessage()))
		}
		catch(Exception e) {
			out.write(getResponseXML(name, e.getMessage()))
			log.error("Error while processing feed ${name} - ${e.getMessage()}")
			e.printStackTrace()
		}
		finally {
			out.flush()
			out.close()
		}
	}
}
