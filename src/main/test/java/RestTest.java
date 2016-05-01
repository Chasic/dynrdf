import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * Class for REST tests - support for @Context HttpServletRequest (with other mvn dep.),
 *                        which is important for testing POST requests of
 *                        ttl definitions in request body
 */
public abstract class RestTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        return new ResourceConfig();
    }

    /**
     * Override this method and return full class name of the rest class to be tested
     * @return String
     */
    abstract protected String getRestClassName();

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new TestContainerFactory() {
            @Override
            public TestContainer create(URI baseUri, DeploymentContext deploymentContext) {
                return new TestContainer() {
                    private HttpServer server;

                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }

                    @Override
                    public URI getBaseUri() {
                        return baseUri;
                    }

                    @Override
                    public void start() {
                        try {
                            this.server = GrizzlyWebContainerFactory.create(
                                    baseUri, Collections.singletonMap(ServerProperties.PROVIDER_CLASSNAMES, getRestClassName())
                            );
                        } catch (ProcessingException | IOException e) {
                            throw new TestContainerException(e);
                        }
                    }

                    @Override
                    public void stop() {
                        this.server.shutdownNow();

                    }
                };
            }
        };
    }
}
