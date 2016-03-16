/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 * A Model object - represents Requestor from NIKE PRODIGY PRODUCT XML feed
 * 
 * E.g.
 * 
 * <requestor name="JP">
 *     <status>ACTIVE</status>
 *     <startdate>03/28/2015 00:00:00.000000</startdate>
 *     <inventory>false</inventory>
 * </requestor>
 * 
 * 
 * @author murugan.poornachandran
 *
 */
@ToString(includeNames=true)
class Requestor {

	String name
	String status
	String startDate
    String requestType
	boolean inventory
}
