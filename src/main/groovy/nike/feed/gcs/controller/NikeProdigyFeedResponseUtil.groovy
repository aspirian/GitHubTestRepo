package nike.feed.gcs.controller

/**
 * A Simple Utility class to construct MPX's XML response
 * 
 * @author murugan.poornachandran
 *
 */
class NikeProdigyFeedResponseUtil {

	static def XMLNS = """<?xml version="1.0" encoding="UTF-8"?>
<GcsFeedResponse xmlns="https://gcs.retail.schawk.com/schema/GcsFeedSchema" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="https://gcs.retail.schawk.com/schema/GcsFeedSchema 
https://gcs.retail.schawk.com/schema/GcsFeedSchema1.0.xsd ">
	"""
	/**
	 * Generate the XML response as per GCS's Feed schema for MPX
	 * 
	 * @param filename
	 * @return
	 */
	static def getResponseXML(String fileName, Long feedId){

		def xml = XMLNS

		if(fileName != "") {
			xml += "<ResponseCode>GCS-Feed-0000</ResponseCode>"
			xml += "<Message>Feed-${fileName} Saved in staging tables"
			xml += " with feed id-${feedId}</Message>"
			xml += "<ResponseDateTime>${new Date()}</ResponseDateTime>"
		} else {
			xml += "<ResponseCode>GCS-Feed-1111</ResponseCode>"
			xml += "<Message>Error! No feed file found in the request!"
			xml += " Please pass ?name=feed-file-name.xml in the URL</Message>"
			xml += "<ResponseDateTime>${new Date()}</ResponseDateTime>"
		}
		xml += "</GcsFeedResponse>"

		return xml
	}

	static def getResponseXML(String fileName, String errorMessage){

		def xml = XMLNS

		if(errorMessage) {
			xml += "<ResponseCode>GCS-Feed-1111</ResponseCode>"
			xml += "<Message>Error while staging Feed-${fileName} : ${errorMessage} </Message>"
			xml += "<ResponseDateTime>${new Date()}</ResponseDateTime>"
		}

		xml += "</GcsFeedResponse>"

		return xml
	}
}
