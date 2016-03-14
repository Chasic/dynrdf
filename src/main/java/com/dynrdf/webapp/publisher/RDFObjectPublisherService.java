package com.dynrdf.webapp.publisher;

import com.dynrdf.webapp.Request;
import com.dynrdf.webapp.exceptions.RequestException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */

@Path("/")
public class RDFObjectPublisherService {

    @GET
    @Produces("text/turtle")
    public Response handleObjectRequestTurtle(@Context HttpServletRequest request) {

        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "TURTLE");
        }
        catch(RequestException ex){
            return return400();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces("application/n-triples")
    public Response handleObjectRequestN3(@Context HttpServletRequest request) {

        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "N-TRIPLES");
        }
        catch(RequestException ex){
            return return400();
        }

        return objectRequest.execute();
    }


    @GET
    @Produces("application/ld+json")
    public Response handleObjectRequestJSONLD(@Context HttpServletRequest request) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "JSON-LD");
        }
        catch(RequestException ex){
            return return400();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces({"application/rdf+xml", "application/xml"})
    public Response handleObjectRequestRDFXML(@Context HttpServletRequest request) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "RDF/XML");
        }
        catch(RequestException ex){
            return return400();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces("text/html")
    public Response handleObjectRequestHtml(@Context HttpServletRequest request) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "JSON-LD");
        }
        catch(RequestException ex){
            return return400();
        }

        return objectRequest.execute();
    }

    private Request buildObjectRequest(HttpServletRequest request, String produces) throws RequestException{
        List<String> uriParameters;
        boolean uriByParameter = false;

        String requestUri = request.getParameter("url");
        // url was set as parameter "url" : .com/?url=..
        if(requestUri == null){
            throw new RequestException("URL parameter not set");
        }

        uriParameters = parseUriPath(requestUri);

        RDFObject obj = RDFObjectContainer.getInstance().getObjectByUriRegexMatch(requestUri);
        // object not found by given uri prefix
        if(obj == null){
            throw new RequestException("Object not found!");
        }

        return new Request(obj, requestUri, uriParameters, produces);
    }


    private List<String> parseUriPath( String uriPath ){
        return new  ArrayList<String>(Arrays.asList(uriPath.split("/")));
    }

    public Response return400() {

        return Response.status(400).build();
    }

}
