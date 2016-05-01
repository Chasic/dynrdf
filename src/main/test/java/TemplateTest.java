import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.model.Template;
import com.dynrdf.webapp.util.Log;
import org.junit.*;

import java.util.Arrays;

/**
 * Tests for Template
 */
public class TemplateTest {

    @BeforeClass
    public static void initApi() throws Exception{
        // 1) Init logger
        Log.init();
        // 2) Load config
        Config.init();
    }


    @Test
    public void testTemplateRecordsFound(){
        String plainTemplate = "[@0]_[@1]_[@2]";
        Template template = new Template(plainTemplate);
        template.preprocess();
        Assert.assertEquals(3, template.getRecords().size());
    }

    @Test
    public void testFillTemplate(){
        String plainTemplate = "[@0]_[@1]_[@2]_[@3]_[@4]_[@5]_[@6]";
        Template template = new Template(plainTemplate);
        template.preprocess();
        Assert.assertEquals(7, template.getRecords().size());
        String testUrl = "http://test.com/a/b/c";
        String filledTemplate = template.fillTemplate(testUrl, Arrays.asList(testUrl.split("/")));

        String expected = testUrl + "_" + testUrl.replaceAll("/", "_");
        Assert.assertEquals(expected, filledTemplate);
    }

    @Test
    public void testRegexTemplate(){
        String plainTemplate = "[@0, \"http://(.*)\"]";
        Template template = new Template(plainTemplate);
        template.preprocess();
        Assert.assertEquals(1, template.getRecords().size());
        String testUrl = "http://test.com/a/b/c";
        String filledTemplate = template.fillTemplate(testUrl, Arrays.asList(testUrl.split("/")));

        String expected = "test.com/a/b/c";
        Assert.assertEquals(expected, filledTemplate);
    }
}
