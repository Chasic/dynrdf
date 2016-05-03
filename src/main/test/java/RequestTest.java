import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.model.Template;
import com.dynrdf.webapp.publisher.Request;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.*;
import sun.misc.IOUtils;

import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for Request
 */
public class RequestTest {

    @BeforeClass
    public static void init() throws Exception {
        // 1) Init logger
        Log.init();
        // 2) Load config
        Config.init();
        // 3) Init Object container
        RDFObjectContainer.init();
    }

    // ## test request execute

    // rdf serializations
    @Test
    public void testRequestExecuteTurtleType() {
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerTurtle");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerTurtle/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "TURTLE");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "TURTLE");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerTurtle#42", res);
    }

    @Test
    public void testRequestExecuteRDFXMLType() {
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerXML");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerXML/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "RDF/XML");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "RDF/XML");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerXML#42", res);
    }

    @Test
    public void testRequestExecuteNTriplesType() {
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerNTriples");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerNTriples/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "N-TRIPLES");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "N-TRIPLES");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerNTriples#42", res);
    }

    @Test
    public void testRequestExecuteJSONLD() {
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerJSONLD");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerJSONLD/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "JSON-LD");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "JSON-LD");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerJSONLD#42", res);
    }



    // sparql construct

    @Test
    public void testRequestExecuteSparqlConstruct(){
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerConstruct");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerConstruct/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "TURTLE");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();
        System.out.println(result);

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "TURTLE");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerConstruct#42", res);
    }

    /**
     * Sparql endpoint test requires internet connection and running
     * endpoint on http://linkedgeodata.org/sparql
     */
    @Test
    public void testRequestExecuteSparqlEndpoint(){
        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerEndpoint");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerEndpoint/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "TURTLE");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();
        System.out.println(result);

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "TURTLE");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerEndpoint#42", res);
    }

    @Test
    public void testRequestExecuteProxy(){

        RDFObject o = RDFObjectContainer.getInstance().getObject("dynrdf/loggerProxy");
        Assert.assertNotNull(o);

        String uri = "http://logservice.com/data/loggerProxy/42/2016-05-01/10:00:23/org.dynrdf.Request/some exception msg";
        List<String> params = new ArrayList<String>(Arrays.asList(uri.split("/")));
        Request request = new Request(o, uri, params, "TURTLE");
        Response r = request.execute();

        Assert.assertEquals(200, r.getStatus());
        String result = (String)r.getEntity();
        System.out.println(result);

        StringReader sr = new StringReader(result);
        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, "TURTLE");

        String res = selectresource(model);
        Assert.assertEquals("http://logservice.com/data/loggerProxy#42", res);
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
