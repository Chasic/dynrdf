import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.util.Log;
import javax.ws.rs.core.Response;
import org.junit.*;

/**
 * Tests server api requests.
 * Test cases:
 *  1) Reload: load test objects -> remove from container
 *             -> reload -> get list of objects from container
 *
 */
public class ServerApiTest extends RestTest {


    @Override
    protected String getRestClassName() {
        return "com.dynrdf.webapp.api.ServerService";
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
    public void testReload() {
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // remove loaded objects
        container.removeAll(false);
        Assert.assertEquals(0, container.getAll().size());

        // call reload
        final Response resGet = target("server/reload" ).request().get();
        Assert.assertEquals(200, resGet.getStatusInfo().getStatusCode());

        // there should be reloaded test objects in container
        Assert.assertTrue(container.getAll().size() >= 2);
        Assert.assertNotNull(container.getObject("tests/reload1"));
        Assert.assertNotNull(container.getObject("tests/reload2"));
    }
}
