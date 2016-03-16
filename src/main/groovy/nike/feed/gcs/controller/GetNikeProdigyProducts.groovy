/**
 * 
 */
package nike.feed.gcs.controller

import groovy.json.JsonBuilder
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import nike.feed.gcs.model.Product
import nike.feed.gcs.service.GetNikeProductService
import nike.feed.gcs.util.DateUtil

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Simple controller to fetch NIKE PRODIGY Feed from GCS staging tables and process it to GCS system
 *  
 * @author murugan.poornachandran
 *
 */
@Controller
class GetNikeProdigyProducts {

    // Define the logger object for this class
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GetNikeProductService getProductService

    //Controller for Index/Home page
    @RequestMapping(value="/",method=RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getHome(HttpServletResponse response, Model model) {
        return "index";
    }

    //Controller for product search
    @RequestMapping(value="/products",method=RequestMethod.POST)
    @ResponseBody
    public String getProducts(@RequestParam(value="items", required=true) String items) {

        List<String> listItem = new ArrayList<String>(Arrays.asList(items.split(",")));

        // wrap the single quote to pass into the SQL IN clause
        items = items?.split(",").collect { "'${it}'" }.join(",")

        List<Product> products
        def mbuFeedFileContent
        def matchedItems = ""
        def unMatchedItems = ""

        products = getProductService.getProducts(items)

        log.info("product Size"+ products.size())

        //Remove the found materials from the initial items to get the unmatched items list
        products.each { product ->

            matchedItems += product.materialNumber + ",";
            listItem.remove(product.materialNumber.toString());

        }
        
        //Generating the product search result as JSON 
        def json = new groovy.json.JsonBuilder(products : products, unmatched: listItem)
        return json.toPrettyString()
    }

    //Controller for MBU feed generation
    @RequestMapping(value="/generateMBU",method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void generateMBU(@RequestParam(value="products", required=true) String items, HttpServletResponse response) {

        // wrap the single quote to pass into the SQL IN clause
        items = items?.split(",").collect { "'${it}'" }.join(",")
        log.info("Items" + items);
        def mbuFeedFileContent
        List<Product> products
        InputStream inputStream = null
        ServletOutputStream outputStream = null
        try{
            products = getProductService.getProducts(items)

            log.info("Products" + products);
            // Create an xls MBU feed file
            mbuFeedFileContent = getProductService.generateMbuFeedFile(products)

            // Link the created xls file to the response object for attachment download
            def fileName = "Nike_MBU_Template_" + DateUtil.getCurrentDateAsString("yyyyMMddHHmmssSSS") + ".xls";
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"")

            inputStream = new ByteArrayInputStream(mbuFeedFileContent)
            outputStream = response.getOutputStream();
            byte[] outputByte = new byte[4096];

            //copy binary content to output stream
            while(inputStream.read(outputByte, 0, 4096) != -1){
                outputStream.write(outputByte, 0, 4096);
            }

        }
        catch(Exception e) {
            log.error("Couldn't get the products detail for items {items} - ${e.getMessage()}")
        }
        finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(outputStream != null){
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    //Controller to convert the prodigy xml to GCS xml and process it to GCS system
    @RequestMapping(value="/convertXML",method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String convertXML(@RequestParam(value="products", required=true) String productIds, HttpServletResponse response) {
        def json
        try{
        // wrap the single quote to pass into the SQL IN clause
        productIds = productIds?.split(",").collect { "${it}" }.join(",")
        log.info("Items" + productIds);
        
        //Get the prodigy xml for the selected product Ids from DB
        def prodigyXML = getProductService.getProdigyXMLs(productIds)
        
        //Transform prodigy xml to gcs xml and process it to GCS system
        getProductService.generateGCSXML(prodigyXML)
        json = new JsonBuilder([statusResponse: "SUCCESS"])
        
        } catch(IOException e){
            
            json = new JsonBuilder([statusResponse: "FAILED"])
        }
        
        return json.toPrettyString()
    }
}
