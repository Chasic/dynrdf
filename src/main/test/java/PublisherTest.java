import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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
        return "com.dynrdf.webapp.publisher.RDFObjectPublisher";
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




}

