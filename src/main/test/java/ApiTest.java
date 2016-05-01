import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.exceptions.InitException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class ApiTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(com.dynrdf.webapp.api.RDFObjectService.class);
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
    @Ignore
    public void test(){
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
        HttpURLConnection connection = null;
        System.out.println(ttl);
        try {
            //Create connection
            URL url = new URL("http://localhost:9998/objects");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "text/turtle");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(ttl.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(ttl);


            //Get Response
            Log.debug("a");
            InputStream is = connection.getInputStream();
            Log.debug("a2");
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            Log.debug("b");
            rd.close();

            wr.close();
            Assert.assertEquals(response.toString(), "a");
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @Ignore
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
        Client client = ClientBuilder.newClient();

        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT,    1000);

        WebTarget target = client.target("http://localhost:9998/objects");
        final Response res = target.request().post(e);
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());
        // get object
        /*final Response resGet = target("objects/tests/testobjttl").request(MediaType.APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());*/

    }

    @Test
    //@Ignore
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
    //@Ignore
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

}