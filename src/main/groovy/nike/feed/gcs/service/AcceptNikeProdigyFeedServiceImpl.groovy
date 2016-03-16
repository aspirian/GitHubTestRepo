package nike.feed.gcs.service

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI
import groovy.xml.XmlUtil

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import name.fraser.neil.plaintext.diff_match_patch
import nike.feed.gcs.dao.NikeProdigyDao

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.xml.sax.ErrorHandler
/**
 * @author murugan.poornachandran
 *
 */
@Service
class AcceptNikeProdigyFeedServiceImpl implements AcceptNikeProdigyFeedService {

	// Define the logger object for this class
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value('${nike.prodigy.input.path}')
	String inputFeedPath;

	@Value('${nike.prodigy.xsd.url}')
	String xsdUrl

	@Autowired
	NikeProdigyDao dao;

	/* (non-Javadoc)
	 * @see nike.feed.gcs.service.AcceptNikeProdigyFeedService#acceptFeed(java.lang.String)
	 */
	@Override
	public Long acceptFeed(String fileName) {
		log.info("File name ${fileName}")
		// Get the given input feed file
		File feedFile = new File(inputFeedPath, fileName)
		Long feedId = 0
		log.info("File path ${feedFile.getAbsolutePath()}")
		// Fail if the input file doesn't exist
		if( "".equals(fileName) || !feedFile.exists() ) {
			throw new FileNotFoundException("Couldn't find input feed file - ${fileName}")
		}

		// Stage the Feed file and its content
		feedId = stageNikeProdigyXml(feedFile)

		return feedId;
	}

	/**
	 * @param feedFile
	 * @return
	 */
	private Long stageNikeProdigyXml(File feedFile) {
		// Save the filename & its size in the import log
		Long logId = dao.saveNikeProdigyXmlImportLog([
			feedFile.getName(),
			feedFile.length()
		])

		parseAndValidateNikeProdigyXml(logId, feedFile)

		return logId
	}

	/**
	 * Parse the NIKE PRODIGY Input XML feed and stage and 
	 * validate each product as per their Schema XSD file
	 * 
	 * @param feedFile - NIKE PRODIGY Input feed file
	 */
	private void parseAndValidateNikeProdigyXml( Long logId, File feedFile) {

		String productId, productXml, oldProductXml, itemNumber
		String deltaType, validationMessage, diffHtml

		// Text - DIFF utility
		def diff = new diff_match_patch()
		// Parse the Actual XML from NIKE
		def products = new XmlParser().parse(feedFile)

		products.PRODUCT.each { product ->
			deltaType = null
			diffHtml = null
			// Get the product ID and its actual XML as string
			productId = product.@productid
			itemNumber = "${product.stylenumber.text()?.trim()}_${product.colornumber.text()?.trim()}"
			productXml = XmlUtil.serialize(product)
			productXml = addProductsRootTag(productXml)

			// Get the existing Product XML to compare and validate
			oldProductXml = dao.getNikeProdigyXml(productId)

			if( !oldProductXml ) {
				// If we are receiving the product for very first time let's save it
				dao.saveNikeProdigyXml([
					productId,
					productXml,
					itemNumber
				])
				deltaType = "NEW" // i.e. It's New product

			} else if ( !productXml.equals(oldProductXml) ) {
				// If there are any updates to the existing product, let update the changes
				dao.updateNikeProdigyXml(productId, productXml)
				deltaType = "UPDATE" // i.e. It's existing product but with modifications

				// Get the DIFF in HTML format for easy understanding
				diffHtml = diff.diff_prettyHtml(diff.diff_main(oldProductXml, productXml));
			}

			// If "no delta" then it's the same feed without any changes,
			// so let's ignore it to update or to re-process in GCS
			if( deltaType != null ) {
				def exceptions = validate( productXml )
				validationMessage = exceptions.size() == 0 ? "Success" : exceptions.toString()

				// Lets save the import details with XSD validation status
				dao.saveNikeProdigyXmlImportLogDetail( [
					logId,
					productId,
					validationMessage,
					deltaType,
					diffHtml]
				)
			}
		}
	}

	/** 
	 * To match each product as per the defined XSD, 
	 * extracted the product and add enclose &lt;products&gt; tag
	 * 
	 * @param productXml - XML string representation for one NIKE PRODIGY PRODUCT XML
	 */
	private String addProductsRootTag(String productXml) {
		// Remove the existing <?xml ...> declaration
		productXml = productXml.substring( productXml.indexOf("<PRODUCT") ).trim();
		productXml = """<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>
<products xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">
${productXml}
</products>"""
		return productXml
	}

	/**
	 * Validate the given XML string against NIKE PRODIGY XSD
	 * and returns the schema errors if any
	 *
	 * @param xml - XML string representation for one NIKE PRODIGY PRODUCT XML
	 *
	 * @return - Empty List if no error otherwise list of schema errors
	 */
	private List<String> validate(String xml) {
		Resource resource = new ClassPathResource("schema/gdc_schawk_prodfeed_new.xsd");

		// Get the validator and validate the given XML
		SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI )
				.newSchema( resource.getURL() )
				.newValidator().with { validator ->
					List<String> exceptions = []
					Closure<Void> handler = { ex ->  exceptions << "Error @ line ${ex.lineNumber}, col ${ex.columnNumber} : ${ex.message}"  }
					errorHandler = [ warning: handler, fatalError: handler, error: handler ] as ErrorHandler
					validate( new StreamSource( new StringReader(xml) ) )

					exceptions
				}
	}
}
