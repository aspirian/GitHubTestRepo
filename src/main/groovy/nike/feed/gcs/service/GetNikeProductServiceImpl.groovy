package nike.feed.gcs.service

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI

import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


import nike.feed.gcs.dao.NikeProdigyDao
import nike.feed.gcs.model.Product
import nike.feed.gcs.model.Style
import nike.feed.gcs.util.DateUtil
import nike.feed.gcs.util.ExcelFileCreator

import org.apache.poi.ss.usermodel.Sheet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * @author murugan.poornachandran
 *
 */
@Service
class GetNikeProductServiceImpl implements GetNikeProductService {

    // Define the logger object for this class
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NikeProdigyDao dao;

    @Value('${template.file.name}')
    String baseFileName;

    @Value('${xslt.file.name}')
    String xslFile;

    @Value('${gcs.output.xml.file.path}')
    String xmlOutputFilePath;

    @Value('${gcs.feed.url}')
    String feedUrl;
	
	@Value('${gcs.feed.xml.user-name}')
	String feedUserName;
	
	@Value('${gcs.feed.xml.password}')
	String feedPassword;
    
    @Override
    public Product getProduct(String itemNumber) {
        return dao.getProduct(itemNumber)
    }

    @Override
    public List<Product> getProducts(String itemNumbers) {
        return dao.getProducts(itemNumbers)
    }

    /**
     * Generate MBU feed for single product
     */
    @Override
    public byte[] generateMbuFeedFile(Product product){
        def mbuFeed = null

        if(product){
            List<Product> products = new ArrayList<Product>()
            products << product

            mbuFeed = generateMbuFeedFile(products)
        }

        return mbuFeed
    }

    /**
     * Generate MBU feed for multiple products
     */
    @Override
    public byte[] generateMbuFeedFile(List<Product> products) throws IOException{

        def fileName = "Nike_MBU_Template_" + DateUtil.getCurrentDateAsString("yyyyMMddHHmmssSSS") + ".xls"
        def sheetName = "Sheet1"

        // Create Excel file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator(baseFileName)

        // Create sheet
        Sheet sheet = excelFileCreator.getSheet(sheetName)



        def rowCount = 1

        // Create row contents
        products.each { product ->

            // Mapping product POGO with MBU Feed columns
            def businessUnit = ""
            def materialNumber = product.materialNumber
            def requestType = ""
            def requestedDate = ""
            def productType = product.nikeType
            def season = ""
            def priority = ""
            def keyMarketing = ""
            def brand = ""
            def category = ""
            def gender = ""
            def sourceImageName = ""
            def alternateCopyName = ""
            def copyCode = ""
            def newVersion = ""
            def premiumRequest = ""
            def confidentialRequest = ""
            def onbodyRequest = ""

            Style style = product.styleDetail[0]
            def productName = style.styleName
            def colorDescription = style.colorDescription

            product.attributes.each { attribute ->
                if(attribute.type == "primarysport"){
                    brand = attribute.code
                }

                if(attribute.type == "gender"){
                    gender = attribute.code
                }
            }


            String[] rowContent = [
                businessUnit,
                materialNumber,
                requestType,
                requestedDate,
                productType,
                productName,
                season,
                priority,
                keyMarketing,
                colorDescription,
                brand,
                category,
                gender,
                sourceImageName,
                alternateCopyName,
                copyCode,
                newVersion,
                premiumRequest,
                confidentialRequest,
                onbodyRequest
            ]
            excelFileCreator.createRow(sheet, rowCount, rowContent)
            rowCount++
        }

        excelFileCreator.autoSizeColumn(sheet);

        log.error("Before creating the Excel")

        // Create file content from WorkBooK
        byte[] mbuFeed = excelFileCreator.createExcelFile();

        log.error("After creating the Excel")

        return mbuFeed
    }

    @Override
    public String getProdigyXML(String productId) {
        return dao.getNikeProdigyXml(productId)
    }

    @Override
    public StringBuilder getProdigyXMLs(String productIds) {

        return dao.getGroupedNikeProdigyXmls(productIds)

    }

    @Override
    public String getBusinessUnit(String businessUnitCode) {

        return dao.getBusinessUnitName(businessUnitCode)

    }

    /**
     * Transform prodigy xml to GCS feed xml
     */
    @Override
    public void generateGCSXML(StringBuilder prodigyXML) throws IOException {
        def outputXml
        def businessUnitCode
        try{

            // Load XSLT
            def xslt= new File(xslFile).getText()
            
            //Create Output filename
            def outputFileName= xmlOutputFilePath+"output_gcs_feed_" + DateUtil.getCurrentDateAsString("yyyyMMddHHmmssSSS") + ".xml"
            outputXml = new FileOutputStream(outputFileName)

            //Replace the BU code with name from DB
            def products = new XmlParser().parseText(prodigyXML.toString())

            log.info("Request - " + groovy.xml.XmlUtil.serialize(products))
            products.PRODUCT.each { product ->
                product.requestors.requestor.each { requestor ->
                    businessUnitCode = requestor.@name
                    log.info("BU - " + businessUnitCode)
                    requestor.@name = dao.getBusinessUnitName(businessUnitCode)
                }
            }

            String prodigyStr = groovy.xml.XmlUtil.serialize(products)

            log.info("Starting transfromation...")

            //set the indent-number into the transformerfactory
            TransformerFactory transformerFactory = TransformerFactory.newInstance()
            transformerFactory.setAttribute("indent-number", new Integer(3))

            //enable the indent in the transformer
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xslt)))
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")

            //Setting the parameter values in the XSL file
            transformer.setParameter("current-date", DateUtil.getCurrentDateAsString("yyyy-MM-dd'T'HH:mm:ss'Z'"))
            transformer.setParameter("user-name", feedUserName)
            transformer.setParameter("password", feedPassword)

            //transform the input XML with XSLT to output XML
            StreamResult xmlOutput = new StreamResult(new StringWriter())
            transformer.transform(new StreamSource(new StringReader(prodigyStr)), xmlOutput)
            String result = xmlOutput.getWriter().toString()

            //Write output xml to the file
            outputXml.write(result.bytes)

            //Send the transfered XML to GCS system
            sendRequestToGCS(result)

            log.info("End transfromation...\n")
        }
        catch(IOException e) {
            log.error("Couldn't get the product detail for item {item} - ${e.getMessage()}")
            throw e
        }
        finally {
            if(outputXml != null){
                outputXml.flush()
                outputXml.close()
            }
        }
        return null;
    }
    
    /*
     * Send the GCS xml to GCS system
     */
    private void sendRequestToGCS(String xmlFeed) {

        def url = feedUrl
        def uriPath = "/feed-service/processFeedV01.htm"
        def http = new HTTPBuilder(url)


        http.post(path:uriPath, body:xmlFeed, requestContentType:ContentType.TEXT, contentType:ContentType.URLENC) { resp, reader ->
            log.info(reader.text)
            //log.info(resp.statusLine)
            assert resp.statusLine.statusCode == 200
        }
    }

}
