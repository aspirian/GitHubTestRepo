/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 * A Model object - represents Attribute from NIKE PRODIGY PRODUCT XML feed
 * 
 * E.g.
 * <attribute type="gender">
 *    <code>Men</code>
 *  </attribute>
 *  
 * @author murugan.poornachandran
 *
 */
@ToString(includeNames=true)
class Attribute {

	String type
	String code	
}
