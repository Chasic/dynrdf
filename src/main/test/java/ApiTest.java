import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.*;

/**
 * Tests basic api requests.
 * Test cases:
 *  1) Create with JSON request: Create object & get it
 *  2) Create with TTL definition: Create object & get it
 *  3) Delete: Create object & get object & delete object & get object again (404 failed)
 *  4) Update with JSON: Create & update & get & compare
 *  5) Update with TTL definition: Create & update & get & compare
 *
 */
public class ApiTest extends RestTest {


    @Override
    protected String getRestClassName() {
        return "com.dynrdf.webapp.api.RDFObjectService";
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


    @Test
    public void testCreateTtl(){
        String ttl = "@prefix def:   <http://dynrdf.com/objects#> .\n" +
                "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix dcterms: <http://purl.org/dc/terms/> .\n" +
                "@prefix dynrdf: <http://dynrdf.com/objects.rdfs#> .\n" +
                "\n" +
                "def:tests_testobjttl  a          dynrdf:Turtle ;\n" +
                "        dynrdf:group           \"tests\" ;\n" +
                "        dynrdf:htmlTemplate    \"html\" ;\n" +
                "        dynrdf:objectTemplate  \"empty template\" ;\n" +
                "        dynrdf:priority        1 ;\n" +
                "        dynrdf:regex           \"testobjttl\" ;\n" +
                "        dcterms:title          \"testobjttl\" .\n";

        Entity<String> e = Entity.entity(ttl, MediaType.valueOf("text/turtle"));

        final Response res = target("objects").request().post(e);
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());
        // get object
        final Response resGet = target("objects/tests/testobjttl").request(MediaType.APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());
    }

    @Test
    public void testCreateJson() {
        RDFObject o = new RDFObject("testobj1", "tests", "testobj1", "TURTLE", "empty template", 1,
                "", "", "html", null);

        final Response res = target("objects").request().post(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());
        // get object
        final Response resGet = target("objects/" + o.getFullName()).request(MediaType.APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());
    }

    @Test
    public void testCreateDelete() {
        RDFObject o = new RDFObject("testobj2", "tests", "testobj2", "TURTLE", "empty template", 1,
                "", "", "html", null);

        final Response res = target("objects").request().post(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());
        // get object
        final Response resGet = target("objects/" + o.getFullName()).request(MediaType.APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());
        // remove object
        final Response resRemove = target("objects/" + o.getFullName()).request().delete();
        Assert.assertEquals(200, resRemove.getStatusInfo().getStatusCode());
        // check object
        final Response resReqRemove = target("objects/" + o.getFullName()).request(MediaType.APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(404, resReqRemove.getStatusInfo().getStatusCode());
    }

    @Test
    public void testUpdateJson(){
        RDFObject o = new RDFObject("testUpdateObj", "tests", "testUpdateObj", "TURTLE", "empty template", 1,
                "", "", "html", null);

        // create
        Response res = target("objects").request().post(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());

        // update
        String templateContentUpdated = "updated template";
        o.setTemplate(templateContentUpdated);
        res = target("objects/" + o.getFullName()).request().put(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());

        // get object
        final Response resGet = target("objects/" + o.getFullName()).request(MediaType.APPLICATION_JSON_TYPE).get();
        RDFObject oUpdated = resGet.readEntity(RDFObject.class);
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());
        Assert.assertEquals(templateContentUpdated, oUpdated.getTemplate());
    }

    @Test
    public void testUpdateTtl(){
        String baseTtl = "@prefix def:   <http://dynrdf.com/objects#> .\n" +
                "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix dcterms: <http://purl.org/dc/terms/> .\n" +
                "@prefix dynrdf: <http://dynrdf.com/objects.rdfs#> .\n" +
                "\n" +
                "def:tests_testobjttlUpdate  a          dynrdf:Turtle ;\n" +
                "        dynrdf:group           \"tests\" ;\n" +
                "        dynrdf:htmlTemplate    \"html\" ;\n" +
                "        dynrdf:objectTemplate  \"empty template\" ;\n" +
                "        dynrdf:priority        1 ;\n" +
                "        dcterms:title          \"testobjttlUpdate\" ;\n";

        String createTtl = baseTtl +
                "        dynrdf:regex           \"testobjttlUpdate\" .\n";
        // update regex
        String updateTtl = baseTtl +
                "        dynrdf:regex           \"testobjttlUpdateRegexx\" .\n";

        Entity<String> e = Entity.entity(createTtl, MediaType.valueOf("text/turtle"));

        // create
        Response res = target("objects").request().post(e);
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());

        Entity<String> eUpdate = Entity.entity(updateTtl, MediaType.valueOf("text/turtle"));
        // update object
        res = target("objects/tests/testobjttlUpdate").request().put(eUpdate);
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());

        // get object
        final Response resGet = target("objects/tests/testobjttlUpdate").request(MediaType.APPLICATION_JSON_TYPE).get();
        RDFObject oUpdated = resGet.readEntity(RDFObject.class);
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());
        Assert.assertEquals("testobjttlUpdateRegexx", oUpdated.getUriRegex());
    }
}
