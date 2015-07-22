package com.jatinst.webapp.quoter;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.jatinst.webapp.quoter.dao.Dao;

// this class will handle any request similar to "server.com/stuff"
@Path("stuff")
public class StuffService {

    private Dao<String> dao;

    // let Guice inject the DAO
    @Inject
    public StuffService(Dao<String> dao) {
        this.dao = dao;
    }

    // this method will handle GET requests for text/html
    @GET
    @Produces(TEXT_HTML)
    public String getAll() {
        String html = "<h2>All stuff</h2><ul>";
        for (String stuff : dao.getAll()) {
            html += "<li>" + stuff + "</li>";
        }
        html += "</ul>";
        return html;
    }

    // requests which include a sub-path (expected to be the ID) are handle here
    @GET
    @Path("{id}")
    @Produces(TEXT_HTML)
    public String getById(@PathParam("id") String id) {
        String stuff = dao.getById(id);
        if (stuff == null)
            return notFound();
        else
            return "<html><body><div>" + stuff + "</div></body></html>";
    }

    // when an ID is not found, do this
    private String notFound() {
        return "<html><body><div>Not Found</div></body></html>";
    }

}