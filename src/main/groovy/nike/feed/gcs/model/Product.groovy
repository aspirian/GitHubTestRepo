/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 * A Model object - represents NIKE PRODIGY PRODUCT
 * 
 * @author murugan.poornachandran
 *
 */
@ToString(includeNames=true)
class Product {

    // Primary Key
    String productId
    List<Requestor> requestors = new ArrayList<Requestor>()
    String styleNumber
    boolean recommend
    String productGroupId
    String nikeType // Product Type
    String partNumber
    String colorNumber
    String pdpUrl
    String productType // but e.g. value is "1"
    String materialNumber
    boolean mainColorway
    boolean enUsCopy
    List<Style> styleDetail = new ArrayList<Style>()
    List<SkuSize> sizes = new ArrayList<SkuSize>()
    List<Attribute> attributes = new ArrayList<Attribute>()

    static final def requestedType = ["SOON", "INACTIVE", "HOLD"]
    static final def cancelledType = [
        "CANCEL",
        "CLOSEOUT",
        "ACTIVE"
    ]

    static Product createNew(String xml) {

        def products = new XmlParser().parseText(xml)
        def xmlProd = products.PRODUCT[0]
        Product product = new Product()
        product.with {
            productId = xmlProd.@productid
            styleNumber = xmlProd.stylenumber?.text()?.trim()
            recommend = xmlProd.recommend?.text()?.trim().equalsIgnoreCase('true') ? true : false
            productGroupId = xmlProd.productgroupid?.text()?.trim()
            nikeType = xmlProd.niketype?.text()?.trim()
            partNumber = xmlProd.partnumber?.text()?.trim()
            colorNumber = xmlProd.colornumber?.text()?.trim()
            pdpUrl = xmlProd.pdpurl?.text()?.trim()
            productType = xmlProd.producttype?.text()?.trim()
            mainColorway = xmlProd.maincolorway?.text()?.trim().equalsIgnoreCase('true') ? true : false
            enUsCopy = xmlProd.en_uscopy?.text()?.trim().equalsIgnoreCase('true') ? true : false
            materialNumber = styleNumber + "_" + colorNumber
        }
        //requestors
        xmlProd.requestors.requestor.each { req->
            Requestor requestor = new Requestor()
            requestor.name = req.@name
            requestor.status = req.status?.text()?.trim()
            requestor.startDate = req.startdate?.text()?.trim()
            requestor.inventory = req.inventory?.text()?.trim().equalsIgnoreCase('true') ? true : false

            // Calculate requestType based on requestor status
            if(requestedType.contains(requestor.status)){
                requestor.requestType = "REQUESTED"
            } else if(cancelledType.contains(requestor.status)){
                requestor.requestType = "CANCELLED"
            }

            product.requestors << requestor
        }
        //styleDetail
        xmlProd.styledetail.style.each { styl ->
            Style style = new Style()
            style.language = styl.@language
            style.styleName = styl.stylename?.text()?.trim()
            style.colorDescription = styl.colordescription?.text()?.trim()
            style.swatchColor = styl.swatchcolor?.text()?.trim()
            style.productNameLine1 = styl.productnameline1?.text()?.trim()
            style.productNameLine2 = styl.productnameline2?.text()?.trim()
            style.productSlug = styl.productslug?.text()?.trim()

            product.styleDetail << style
        }
        //sizes
        //attributes
        xmlProd.attributes.attribute.each { attr ->
            Attribute attribute = new Attribute()
            attribute.type = attr.@type
            attribute.code = attr.code?.text().trim()

            product.attributes << attribute
        }

        return product
    }
}
