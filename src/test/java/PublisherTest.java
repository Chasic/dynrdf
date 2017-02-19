import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.util.Log;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RiotException;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Tests basic api requests.
 * Test cases:
 *  1) Create with JSON request: Create object & get it
 *  2) Create with TTL definition: Create object & get it
 *  3) Delete: Create object & get object & delete object & get object again (404 failed)
 *  4) Update with JSON: Create & update & get & compare
 *  5) Update with TTL definition: Create & update & get & compare
 *  6) Test get objects using content negotiation
 *
 */
public class PublisherTest extends RestTest {


    @Override
    protected String getRestClassName() {
        return "com.dynrdf.webapp.publisher.RDFObjectPublisherService";
    }

    @BeforeClass
    public static void initApi() throws Exception{
        // 1) Init logger
        Log.init();
        // 2) Load config
        Config.init();
        // 3) Init Object container
        RDFObjectContainer.init();
    }

    @AfterClass
    public static void after() throws Exception{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // remove all test definitions
        container.removeAll(true);
    }

    /**
     * Test requests for all definition types and all supported serializations
     */
    @Test
    public void testContentNegotiation() {
        // suffixes in the names of the logger examples
        // like loggerConstruct, loggerJSONLD etc.
        List<String> suffixes =  Arrays.asList("Turtle", "JSONLD", "Construct", "Endpoint",
                "NTriples", "Proxy", "XML");
        // list of serializations to test requests
        List<String> serializations =  Arrays.asList("TURTLE", "RDF/XML", "N-TRIPLES", "JSON-LD");
        // list of the content types
        List<String> contentTypes =  Arrays.asList("text/turtle", "application/rdf+xml",
                "application/n-triples", "application/ld+json");

        Response res;
        int ctr = 0;
        String uriPrefix = "http://logservice.com/data/logger";
        String uriSuffix = "/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        for(String suffix : suffixes){
            int serializationIndex = 0;
            String uri = uriPrefix + suffix + uriSuffix;
            try{
                uri = URLEncoder.encode(uri, "UTF-8");
            }
            catch(Exception ignored){Assert.assertEquals("Should not happen", 1, 0);}

            for(String serialization : serializations){

                System.out.println("Testing: " + suffix + " - " + serialization);

                String contentType = contentTypes.get(serializationIndex);
                res =  target("").queryParam("url", uri).request(MediaType.valueOf(contentType))
                        .get();

                Assert.assertEquals("Failed to GET object for type=" + suffix + ", serialization=" + serialization,
                        200, res.getStatus());

                String response = res.readEntity(String.class);
                Model model = ModelFactory.createDefaultModel();

                InputStream is = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
                boolean ok = true;
                try{
                    model.read(is, null, serialization);
                }
                catch (RiotException ex){
                    ok = false;
                }
                Assert.assertEquals("Failed to READ object for type=" + suffix + ", serialization=" + serialization,
                        ok, true);

                String resCheck = selectresource(model);
                Assert.assertEquals("Failed to CHECK object for type=" + suffix + ", serialization=" + serialization,
                        "http://logservice.com/data/logger" + suffix + "#42", resCheck);


                serializationIndex++;
                ctr++;
            }
        }
        System.out.println("PublisherTest:testContentNegotiation() tested " + ctr + " requests!");
    }

    @Test
    public void testHtmlRequest(){
        // suffixes in the names of the logger examples
        // like loggerConstruct, loggerJSONLD etc.
        List<String> suffixes =  Arrays.asList("Turtle", "JSONLD", "Construct", "Endpoint",
                "NTriples", "Proxy", "XML");

        Response res;
        String uriPrefix = "http://logservice.com/data/logger";
        String uriSuffix = "/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        for(String suffix : suffixes){
            String uri = uriPrefix + suffix + uriSuffix;
            try{
                uri = URLEncoder.encode(uri, "UTF-8");
            }
            catch(Exception ignored){Assert.assertEquals("Should not happen", 1, 0);}

            res =  target("").queryParam("url", uri).request(MediaType.valueOf("text/html"))
                    .get();

            Assert.assertEquals("Failed to GET object for type=" + suffix + ", serialization=HTML",
                    200, res.getStatus());

            String response = res.readEntity(String.class);
            System.out.println(response);
            Assert.assertTrue(response.contains("Log ID: #42"));

        }
    }

    // helpers

    /**
     * Select resource from model
     * @param model Model
     * @return String
     */
    private String selectresource(Model model) {

        String queryString =
                "SELECT ?res   \n" +
                        "WHERE \n" +
                        "  { ?res a ?type  }";

        Query query = QueryFactory.create(queryString);
        String resource = "";
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext(); ) {
                QuerySolution soln = results.nextSolution();
                Resource r = soln.getResource("res"); // Get a result variable - must be a resource
                resource = r.toString();

            }
        }
        return resource;
    }




}

