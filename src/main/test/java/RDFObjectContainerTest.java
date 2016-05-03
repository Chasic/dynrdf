import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests container.
 * Test cases:
 *      1) Singleton instance test
 *      2) Create object
 *      3) Remove object
 *      4) Update object
 *      5) Object validations
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

    // ### Validations

    @Test(expected = ContainerException.class)
    public void testEmptyName() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("", "tests", "validations", "TURTLE", "empty template", 1,
                "", "", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void testEmptyGroup() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("name", "", "validations", "TURTLE", "empty template", 1,
                "", "", "html", null);

        container.validateObject(o, false, null);
    }

    @Test
    public void testEmptyTemplate(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<String> types = new ArrayList<>(RDFObject.supportedTemplateTypes);
        //types.addAll(Arrays.asList("TURTLEE", "asd", "RDFFXML", "JSON", "HMM", "TTL"))

        // remove types without template
        types.remove("PROXY");

        for(String type : RDFObject.supportedTemplateTypes){
            RDFObject o = new RDFObject("name", "a", "validations", type, ""/*empty template*/, 1,
                    "http://dummy.com", "url", "html", null);

            try{
                container.validateObject(o, false, null);
            }
            catch(ContainerException ex){
                types.remove(type);
            }
        }

        String notRemoved = "";
        for(String rec : types){
            notRemoved += rec + ", ";
        }

        Assert.assertEquals("Failed for types: " + notRemoved, types.size(), 0);
    }

    @Test
    public void testInvalidTypes(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<String> types = new ArrayList<>(RDFObject.supportedTemplateTypes);

        // add some invalid types
        List<String> invalidTypes = Arrays.asList("TURTLEE", "asd", "RDFFXML", "JSON", "HMM", "TTL");
        types.addAll(invalidTypes);
        List<String> toRemove = new ArrayList<>();

        for(String type : types){
            RDFObject o = new RDFObject("name", "a", "validations", type, "l", 1,
                    "http://dummy.com", "url", "html", null);

            try{
                container.validateObject(o, false, null);
            }
            catch(ContainerException ex){
                toRemove.add(type);
            }
        }

        String allowed = "";
        // remove valid, then should contains invalid types
        types.removeAll(RDFObject.supportedTemplateTypes);
        types.removeAll(toRemove);
        for(String rec : types){
            allowed += rec + ", ";
        }

        Assert.assertEquals("Failed for types: " + allowed, types.size(), 0);
    }

    @Test(expected = ContainerException.class)
    public void validateProxyNoUrlParam() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("name", "tests", "validations", "PROXY", "empty template", 1,
                "http://dummy.com", "", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateProxyInvalidUrlParam() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("name", "tests", "validations", "PROXY", "empty template", 1,
                "http://dummy.com", "a d", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateProxyNoUrl() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("name", "tests", "validations", "PROXY", "empty template", 1,
                "", "param", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateProxyMalformedUrl() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // missing protocol - URL validator is set to check protocol
        RDFObject o = new RDFObject("name", "tests", "validations", "PROXY", "empty template", 1,
                "test.com", "param", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateEndpointMalformedUrl() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // missing protocol - URL validator is set to check protocol
        RDFObject o = new RDFObject("name", "tests", "validations", "SPARQL-ENDPOINT", "empty template", 1,
                "test.com", "param", "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateEndpointEmptyUrl() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = new RDFObject("name", "tests", "validations", "SPARQL-ENDPOINT", "empty template", 1,
                null, null, "html", null);

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateDuplicateName() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // dynrdf/loggerTurtle is in test objects
        RDFObject o = new RDFObject("loggerTurtle", "dynrdf", "validations", "TURTLE", "empty template", 1,
                null, null, "html", null);
        System.out.println(o.getFullName());

        container.validateObject(o, false, null);
    }

    @Test(expected = ContainerException.class)
    public void validateDuplicateRegex() throws ContainerException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        // dynrdf/loggerTurtle is in test objects
        RDFObject o = new RDFObject("loggerTurtle", "testGroup", "loggerTurtle", "TURTLE", "empty template", 1,
                null, null, "html", null);
        System.out.println(o.getFullName());

        container.validateObject(o, false, null);
    }

    @Test
    public void validateDuplicateThingsAllowedInUpdate(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject dummy = container.getObject("tests/dummy"); // in test objects
        if(dummy == null){
            Assert.assertEquals("Missing test definitions ..", 1, 0);
            return;
        }
        // dynrdf/loggerTurtle is in test objects
        RDFObject dummyCopy = new RDFObject(dummy);
        try{
            container.validateObject(dummyCopy, true, dummy);
        }
        catch(ContainerException ex){

            Assert.assertEquals("Validation the same definition for update failed", 1, 0);
        }
    }
}
