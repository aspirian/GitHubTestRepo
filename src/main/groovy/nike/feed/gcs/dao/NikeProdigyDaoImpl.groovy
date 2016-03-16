/**
 * 
 */
package nike.feed.gcs.dao

import groovy.sql.Sql
import nike.feed.gcs.model.Product

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Basic implementation for NikeProdigyDao
 * 
 * @author murugan.poornachandran
 *
 */
@Component
class NikeProdigyDaoImpl implements NikeProdigyDao {

    // Define the logger object for this class
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Sql sql

    @Value('${insert.nike.prodigy.xml}')
    String insertNikeProdigyXMLQuery;

    @Value('${update.nike.prodigy.xml}')
    String updateNikeProdigyXMLQuery;

    @Value('${get.nike.prodigy.xml}')
    String getNikeProdigyXMLQuery;

    @Value('${get.nike.product}')
    String getNikeProductQuery;

    @Value('${insert.nike.prodigy.import.log}')
    String insertNikeProdigyImportLogQuery;

    @Value('${insert.nike.prodigy.import.log.detail}')
    String insertNikeProdigyImportLogDetailQuery;

    @Value('${get.business.unit.name}')
    String getBusinessUnitQuery;
    
    @Value('${get.nike.prodigy.xml.product.status}')
    String getNikeProdigyXMLProductStatusQuery;
    
    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#saveNikeProdigyXml(java.util.List)
     */
    @Override
    public void saveNikeProdigyXml(List<? extends Object> params) {
        sql.executeInsert(insertNikeProdigyXMLQuery,params)
    }

    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#updateNikeProdigyXml(java.lang.Long, java.util.List)
     */
    @Override
    public int updateNikeProdigyXml(String productId, String productXML) {
        return sql.executeUpdate( updateNikeProdigyXMLQuery, [productXML, productId])
    }

    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#getNikeProdigyXml(java.lang.String)
     */
    @Override
    public String getNikeProdigyXml(String productId) {
        log.info("Query - "+ getNikeProdigyXMLQuery);
        def xml =  sql.firstRow(getNikeProdigyXMLQuery, [productId])?.product_xml?.trim();
        return xml
    }

    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#getProduct(java.lang.String)
     */
    @Override
    public Product getProduct(String itemNumber) {
        def xml = sql.firstRow(getNikeProductQuery, [itemNumber])?.product_xml?.trim();
        return Product.createNew(xml);
    }

    @Override
    public List<Product> getProducts(String itemNumbers) {
        List<Product> products = new ArrayList<Product>()
        sql.eachRow("select product_xml from nike_prodigy_xml where item_number in (" + itemNumbers  +")") { row ->
            products << Product.createNew(row.product_xml?.trim());
        }
        return products
    }

    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#saveNikeProdigyXmlImportLog(java.util.List)
     */
    @Override
    public Long saveNikeProdigyXmlImportLog(List<? extends Object> params) {
        def keys = sql.executeInsert(insertNikeProdigyImportLogQuery,params)
        return keys[0][0]
    }

    /* (non-Javadoc)
     * @see nike.feed.gcs.dao.NikeProdigyDao#saveNikeProdigyXmlImportLogDetail(java.util.List)
     */
    @Override
    public Long saveNikeProdigyXmlImportLogDetail(List<? extends Object> params) {
        def keys = sql.executeInsert(insertNikeProdigyImportLogDetailQuery,params)
        return keys[0][0]
    }

    @Override
    public StringBuilder getGroupedNikeProdigyXmls(String productIds) {
        def prodigyXML = new StringBuilder("""<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>
<products xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">""");

        sql.eachRow("select product_xml from nike_prodigy_xml where product_id in (" + productIds  +")") { row ->
            prodigyXML.append(row.product_xml?.substring(row.product_xml.indexOf("<PRODUCT"), row.product_xml.indexOf("</products>")-1).trim())
        }
        prodigyXML.append("</products>")
        return prodigyXML;
    }

    @Override
    public String getBusinessUnitName(String businessUnitCode) {
        def businessUnitName = sql.firstRow(getBusinessUnitQuery, [businessUnitCode])?.business_unit_name?.trim();
        log.info("business name - "+ businessUnitName)
        return businessUnitName
    }
    
    @Override
    public String getNikeProdigyXmlProductStatus(String productId) {
        def xml =  sql.firstRow(getNikeProdigyXMLProductStatusQuery, [productId])?.product_xml?.trim();
        return xml
    }

}
