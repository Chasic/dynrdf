package com.dynrdf.webapp.publisher;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by honza on 11/6/15.
 */

@Path("/{uri:.+}")
public class RDFObjectPublisherService {

    @GET
    @Produces("application/rdf")
    public Response handleObjectRequestRDF(@PathParam("uri") String _uri) {

        List<String> uri = parseUriPath(_uri);

        String output = "RDFObjectPublisherService says ";

        for(String part : uri ){
            output = output + "\n " + part;
        }
        return Response.status(200).entity(output).build();
    }

    @GET
    @Produces("text/html")
    public Response handleObjectRequestHtml(@PathParam("uri") String _uri) {

        List<String> uri = new ArrayList<String>(Arrays.asList(_uri.split("/")));

        String output = "RDFObjectPublisherService says LOLOLOLOOOL ";

        for(String part : uri ){
            output = output + "\n " + part;
        }
        return Response.status(200).entity(output).build();
    }


    private List<String> parseUriPath( String uriPath ){
        return new  ArrayList<String>(Arrays.asList(uriPath.split("/")));
    }

}
