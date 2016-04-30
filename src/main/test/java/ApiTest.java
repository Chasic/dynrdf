import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.exceptions.InitException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.util.Log;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


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

    @Test
    public void test() {
        final Response res = target("objects/test").request().get(Response.class);
        System.out.println(res.getStatusInfo().getReasonPhrase());
        Assert.assertEquals(200, res.getStatusInfo().getStatusCode());
    }

}