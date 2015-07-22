package com.jatinst.webapp.quoter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.jatinst.webapp.quoter.dao.Dao;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;

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

        Injector injector = Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(new TypeLiteral<Dao<String>>() {
                }).toInstance(mockDao);
            }
        });

        ResourceConfig rc = new PackagesResourceConfig("com.jatinst.webapp.quoter");
        IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory(rc, injector);
        server = GrizzlyServerFactory.createHttpServer(BASE_URI + "services/", rc, ioc);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
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
