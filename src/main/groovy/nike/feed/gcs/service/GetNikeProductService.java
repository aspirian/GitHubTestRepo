package nike.feed.gcs.service;

import java.io.IOException;
import java.util.List;

import nike.feed.gcs.model.Product;

public interface GetNikeProductService {

    /**
     * Get the NIKE PRODIGY Product for give Item Number
     * 
     * @param item
     *            Number - NIKE PRODIGY Item Number
     * 
     * @return - Product model for the NIKE PRODIGY PRODUCT XML
     */
    Product getProduct(String itemNumber);

    /**
     * Get the NIKE PRODIGY Products for give Item Numbers
     * 
     * @param item
     *            Numbers - NIKE PRODIGY Item Numbers
     * 
     * @return - List Product model for the give item numbers
     */
    List<Product> getProducts(String itemNumbers);

    /**
    * Generate an xls MBU feed file for single product
    * 
    * @param - Product 
    * 
    * @return - MBU feed file name
     */
    byte[] generateMbuFeedFile(Product product);

    /**
    * Generate an xls MBU feed file for multiple products
    * 
    * @param - List of Products 
    * 
    * @return - MBU feed file name
    */
    byte[] generateMbuFeedFile(List<Product> products) throws IOException;

    /**
         * Get the NIKE PRODIGY XML for given Product
         * 
         * @param productId - NIKE PRODIGY Product Id
         * 
         * @return - NIKE PRODIGY PRODUCT XML string
         */
    String getProdigyXML(String productId);

    /**
     * Get the NIKE PRODIGY XML for given list of Products
     * 
     * @param productId - NIKE PRODIGY Product Ids
     * 
     * @return - Merged NIKE PRODIGY PRODUCT XML as string builder 
     */
    StringBuilder getProdigyXMLs(String productIds);

    /**
     * Generate an GCS XML for multiple products
     * 
     * @param - Prodigy XMl as String 
     * 
     * @return - GCS XML as String
     */
    void generateGCSXML(StringBuilder prodigyXML) throws IOException;
    
    /**
     * Retrieve the Business Unit for the given business unit code
     * 
     * @param - BusinessUnitCode as String 
     * 
     * @return - Business Unit Name as String
     */
    String getBusinessUnit(String businessUnitCode);

}
