/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 * A Model object - represents Style from NIKE PRODIGY PRODUCT XML feed
 * 
 * <style language="en_US">
 *    <stylename>Nike Roshe One Men's Shoe</stylename>
 *    <colordescription>Black/Sail/Anthracite</colordescription>
 *    <swatchcolor>13161A</swatchcolor>
 *    <productnameline1>Nike Roshe One</productnameline1>
 *    <productnameline2>Men's Shoe</productnameline2>
 *    <productslug>roshe-one-shoe</productslug>
 *  </style>
 *  
 * @author murugan.poornachandran
 *
 */
@ToString(includeNames=true)
class Style {

	String language
	String styleName
	String colorDescription
	String swatchColor
	String productNameLine1
	String productNameLine2
	String productSlug
}
