import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.junit.*;

/**
 * Tests container.
 * Test cases:
 *      1) Singleton instance test
 *      2) Create object
 *      3) Remove object
 *      4) Update object
 *
 */
public class RDFObjectContainerTest {
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
    public void testSingleton(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        Assert.assertTrue(container == RDFObjectContainer.getInstance());
    }

    @Test
    public void testCreate(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("createTestContainer", "tests", "createTestContainer", "TURTLE", "empty template", 1,
                "", "", "html", null);
        boolean ok = true;
        try{
            container.createObject(o);
        }
        catch(Exception ex){
            ok = false;
        }
        Assert.assertTrue(ok);
        Assert.assertNotNull(container.getObject(o.getFullName()));
    }

    @Test
    public void testRemove(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // create
        RDFObject o = new RDFObject("removeTestContainer", "tests", "removeTestContainer", "TURTLE", "empty template", 1,
                "", "", "html", null);
        boolean ok = true;
        try{
            container.createObject(o);
        }
        catch(Exception ex){
            ok = false;
        }
        Assert.assertTrue(ok);
        Assert.assertNotNull(container.getObject(o.getFullName()));

        // remove phase
        ok = true;
        try{
            container.removeObject(o.getFullName(), true);
        }
        catch(ContainerException ex){
            ok = false;
        }

        Assert.assertTrue(ok);
        Assert.assertNull(container.getObject(o.getFullName()));
    }

    @Test
    public void testUpdate(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // create
        RDFObject o = new RDFObject("updateTestContainer", "tests", "updateTestContainer", "TURTLE", "empty template", 1,
                "", "", "html", null);
        boolean ok = true;
        try{
            container.createObject(o);
        }
        catch(Exception ex){
            ok = false;
        }
        Assert.assertTrue(ok);
        o = container.getObject(o.getFullName());
        Assert.assertNotNull(o);

        // update phase
        ok = true;
        RDFObject toUpdate = new RDFObject(o);
        // some update
        toUpdate.setTemplate("update").setGroup("testUpdates").setName("changedName").setPriority(55);
        try{
            container.updateObject(toUpdate, o.getFilePath(), o);
        }
        catch(Exception e){
            ok = false;
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(ok);
        Assert.assertEquals(toUpdate, container.getObject(toUpdate.getFullName()));
    }

}
