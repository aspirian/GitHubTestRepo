package nike.feed.gcs.dao;

import java.util.List;

import nike.feed.gcs.model.Product;

/**
 * DAO 1) To save or update the NIKE PRODIGY XML into database tables 2) Capture
 * the import logs with details like XSD validation message & DIFF, etc
 * 
 * @author murugan.poornachandran
 *
 */
public interface NikeProdigyDao {

	/**
	 * Save the NIKE actual PRODIGY Input XML
	 * 
	 * @param params
	 *            - List [ Product ID and actual Product XML details]
	 * 
	 * @return - Return the PRIMARY KEY ID (Long) for saved Product
	 */
	void saveNikeProdigyXml(List<? extends Object> params);

	/**
	 * Update the NIKE actual PRODIGY Input XML
	 * 
	 * @param productId
	 *            - Product ID for the given Product
	 * @param productXML
	 *            - XML string representation for one NIKE PRODIGY PRODUCT XML
	 * 
	 * @return - Number of affected rows, 0 if nothing changed
	 */
	int updateNikeProdigyXml(String productId, String productXML);

	/**
	 * Get the NIKE PRODIGY Product XML details for give Product ID
	 * 
	 * @param productId
	 *            - NIKE PRODIGY Product ID
	 * 
	 * @return - XML string representation for one NIKE PRODIGY PRODUCT XML
	 */
	String getNikeProdigyXml(String productId);

	/**
         * Get the NIKE PRODIGY Product XML details for give Product IDs
         * 
         * @param productId
         *            - NIKE PRODIGY Product ID with comma seperated value
         * 
         * @return - XML string representation for combined NIKE PRODIGY PRODUCT XML
         */
	StringBuilder getGroupedNikeProdigyXmls(String productIds);

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
	 * Capture the import log with input filename and its size in byte
	 * 
	 * @param params
	 *            - List [ Input Feed Filename, Feed File size]
	 * 
	 * @return - Return the PRIMARY KEY ID (Long) for saved import log
	 */
	Long saveNikeProdigyXmlImportLog(List<? extends Object> params);

	/**
	 * Capture the import log details with XSD validation and DIFF details
	 * 
	 * @param params
	 *            - List [ Import's Log Id, Details like create/update time,
	 *            validation status, XML product changes w.r.t previous XML,
	 *            etc]
	 * 
	 * @return - Return the PRIMARY KEY ID (Long) for saved import log
	 */
	Long saveNikeProdigyXmlImportLogDetail(List<? extends Object> params);
	
	/**
         * Retrieve the Business Unit name from the business unit master table
         * 
         * @param params
         *            - String [Business Unit Code like EU, US, JP, etc]
         * 
         * @return - Return the BusinessUnitName (String) from business unit master
         */
	String getBusinessUnitName(String businessUnitCode);
	
	/**
         * Retrieve the Product status from prodigy import log details
         * 
         * @param params
         *            - String [Product Id - to fetch the details]
         * 
         * @return - Return the latest Product Status (String) from import log details.
         */
	String getNikeProdigyXmlProductStatus(String productId);
}
