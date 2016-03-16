/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 *  A Model object - represents UPC/SKU Size from NIKE PRODIGY PRODUCT XML feed
 *
 * E.g.
 * <size id="5000014">
 *    <size>6</size>
 *    <sequence>8</sequence>
 *    <upc>00675911191835</upc>
 *    <inventory>false</inventory>
 *  </size>
 *  
 * @author murugan.poornachandran
 *
 */
@ToString(includeNames=true)
class SkuSize {
	
	String id
	String sequence
	String upc
	boolean inventory
	
}
