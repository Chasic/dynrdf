import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Tests for RDFObject
 */
public class RDFObjectTest {
    @BeforeClass
    public static void init() throws Exception {
        // 1) Init logger
        Log.init();
        // 2) Load config
        Config.init();
        // 3) Init Object container
        RDFObjectContainer.init();
    }

    /**
     * Test if createTTL produces valid RDF turtle
     * Critical test - important for objects administration
     */
    @Test
    public void testCreateTtl(){
        List<RDFObject> objects = RDFObjectContainer.getInstance().getAll();
        // filter objects only to logger test which covers all types
        objects.removeIf(o -> !o.getFullName().contains("dynrdf/logger"));

        for(RDFObject o : objects){
            String ttl = o.createTTL();
            Assert.assertNotEquals("Generated empty turtle definition, object: " + o.getFullName(), "", ttl);

            // validate ttl - read with Jena
            Model model = ModelFactory.createDefaultModel();

            InputStream is = new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8));
            try{
                model.read(is, null, "TURTLE");
            }
            catch (RiotException ex){
                // should not to go inside catch
                Assert.assertTrue("Failed to read turtle definition for object: " + o.getFullName()
                        + ", exception: " + ex.getMessage(), false);
            }

        }
    }
}
