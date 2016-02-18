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
 * Created by honza on 11/6/15.
 */

@Path("/{uri:.+}")
public class RDFObjectPublisherService {

    @GET
    @Produces({"application/x-turtle", "text/turtle", "application/rdf+turtle",
               "application/turtle"})
    public Response handleObjectRequestTurtle(@Context HttpServletRequest request, @PathParam("uri") String _uri) {

        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(_uri, request, "TURTLE");
        }
        catch(RequestException ex){
            return return404();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces({"application/rdf+n3", "text/n3", "application/n3"})
    public Response handleObjectRequestN3(@Context HttpServletRequest request, @PathParam("uri") String _uri) {

        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(_uri, request, "N-TRIPLES");
        }
        catch(RequestException ex){
            return return404();
        }

        return objectRequest.execute();
    }


    @GET
    @Produces("application/ld+json")
    public Response handleObjectRequestJSONLD(@Context HttpServletRequest request, @PathParam("uri") String _uri) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(_uri, request, "JSON-LD");
        }
        catch(RequestException ex){
            return return404();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces({"application/rdf+xml", "application/xml"})
    public Response handleObjectRequestRDFXML(@Context HttpServletRequest request, @PathParam("uri") String _uri) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(_uri, request, "RDF/XML");
        }
        catch(RequestException ex){
            return return404();
        }

        return objectRequest.execute();
    }

    @GET
    @Produces("text/html")
    public Response handleObjectRequestHtml(@Context HttpServletRequest request, @PathParam("uri") String _uri) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(_uri, request, "JSON-LD");
        }
        catch(RequestException ex){
            return return404();
        }

        return objectRequest.execute();
    }

    private Request buildObjectRequest( String uri, HttpServletRequest request, String produces) throws RequestException{
        List<String> uriParameters = parseUriPath(uri);
        boolean uriByParameter = false;

        // every object has uri prefix
        String objectPrefix = uriParameters.get(0);

        String requestUri = request.getParameter("url");
        // url was set as parameter "url" : .com/?url=..
        if(requestUri != null){
            uriParameters = parseUriPath(requestUri);
            uriByParameter = true;
        }
        else{
            requestUri = request.getRequestURL().toString();
        }

        if(uriParameters.size() == 0 ){
            throw new RequestException("Cannot identify an object!");
        }

        RDFObject obj = RDFObjectContainer.getInstance().getObjectByUriPrefix(objectPrefix);
        // object not found by given uri prefix
        if(obj == null){
            throw new RequestException("Object not found!");
        }

        return new Request(obj, requestUri, uriParameters, produces, uriByParameter);
    }


    private List<String> parseUriPath( String uriPath ){
        return new  ArrayList<String>(Arrays.asList(uriPath.split("/")));
    }

    public Response return404() {

        return Response.status(404).build();
    }

}
