package com.jatinst.webapp.quoter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.servlet.GuiceFilter;
import com.jatinst.webapp.quoter.dao.Dao;

@Ignore("Until we figure out how to get mock injection working!")
public class StuffServiceTest {
    static final URI BASE_URI = getBaseURI();
    static HttpServer server;

    @SuppressWarnings("unchecked")
    static Dao<String> mockDao = Mockito.mock(Dao.class);

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(9998).build();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Mockito.when(mockDao.getAll()).thenReturn(Arrays.asList(new String[] { "stuff1", "stuff2", "stuff3" }));
        Mockito.when(mockDao.getById("id1")).thenReturn("stuff1");

        // TODO - figure out how to use the Mock when starting Grizzly with Jersey 2!

        // Create HttpServer
        server = GrizzlyHttpServerFactory.createHttpServer(getBaseURI(), false);

        final WebappContext context = new WebappContext("Guice Webapp sample", "");

        context.addListener(Starter.class);

        // Initialize and register Jersey ServletContainer
        ServletRegistration servletRegistration = context.addServlet("ServletContainer", ServletContainer.class);
        servletRegistration.addMapping("/services/*");
        servletRegistration.setInitParameter("javax.ws.rs.Application", "com.jatinst.webapp.quoter.WebAppGuiceBridge");

        // Initialize and register GuiceFilter - do we even need this?
        /*
        final FilterRegistration registration = context.addFilter("GuiceFilter", GuiceFilter.class);
        registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");
        */

        context.deploy(server);

        server.start();

        /*
         * // Below worked only with Jersey 1.x and the old Grizzly Server Injector injector = Guice.createInjector(new
         * ServletModule() {
         * 
         * @Override protected void configureServlets() { bind(new TypeLiteral<Dao<String>>() { }).toInstance(mockDao);
         * } });
         * 
         * ResourceConfig rc = new PackagesResourceConfig("com.jatinst.webapp.quoter"); IoCComponentProviderFactory ioc
         * = new GuiceComponentProviderFactory(rc, injector); server = GrizzlyServerFactory.createHttpServer(BASE_URI +
         * "services/", rc, ioc);
         */
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testGetAll() throws IOException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget target = client.target(getBaseURI());

        Response resp = target.path("services").path("stuff").request().accept(MediaType.TEXT_HTML).get(Response.class);

        String text = resp.readEntity(String.class);

        assertEquals(200, resp.getStatus());
        assertEquals("<h2>All stuff</h2><ul>" + "<li>stuff1</li>" + "<li>stuff2</li>" + "<li>stuff3</li></ul>", text);

    }

    @Test
    public void testGetById() throws IOException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget target = client.target(getBaseURI());

        Response resp = target.path("services").path("stuff").path("id1").request().accept(MediaType.TEXT_HTML)
                .get(Response.class);

        String text = resp.readEntity(String.class);

        assertEquals(200, resp.getStatus());
        assertEquals("<html><body><div>stuff1</div></body></html>", text);

        String text2 = target.path("services").path("stuff").path("non_existent_id").request()
                .accept(MediaType.TEXT_HTML).get(String.class);

        assertEquals(200, resp.getStatus());
        assertEquals("<html><body><div>Not Found</div></body></html>", text2);

    }

}
