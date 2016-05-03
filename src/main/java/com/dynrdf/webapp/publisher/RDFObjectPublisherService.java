package com.dynrdf.webapp.publisher;

import com.dynrdf.webapp.exceptions.RequestException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
            return return4xx(ex.getMessage());
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
            return return4xx(ex.getMessage());
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
            return return4xx(ex.getMessage());
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
            return return4xx(ex.getMessage());
        }

        return objectRequest.execute();
    }

    @GET
    @Produces("text/html")
    public Response handleObjectRequestHtml(@Context HttpServletRequest request) {
        Request objectRequest;
        try{
            objectRequest = buildObjectRequest(request, "HTML");
        }
        catch(RequestException ex){
            return return4xx(ex.getMessage());
        }

        RDFObject o = objectRequest.getObject();
        String html = o.getHtmlTemplateObject().fillTemplate(objectRequest.getUri(), objectRequest.getUriParameters());


        return Response.status(200).entity(html).build();
    }

    private Request buildObjectRequest(HttpServletRequest request, String produces) throws RequestException{
        List<String> uriParameters;

        String requestUri = request.getParameter("url");
        try{
            requestUri = URLDecoder.decode(requestUri, "UTF-8");
        }
        catch(UnsupportedEncodingException ex){
            Log.error("Decoding problem: " + ex.getMessage());
        }
        // url was set as parameter "url" : .com/?url=..
        if(requestUri == null){
            throw new RequestException("URL parameter not set");
        }
        String group = "";
        String groupVal = request.getParameter("group");
        if(groupVal != null){
            group = groupVal;
        }

        uriParameters = parseUriPath(requestUri);

        RDFObject obj = RDFObjectContainer.getInstance().getObjectByUriRegexMatch(requestUri, group);
        // object not found by given uri prefix
        if(obj == null){
            throw new RequestException("Object not found!");
        }

        return new Request(obj, requestUri, uriParameters, produces);
    }


    private List<String> parseUriPath( String uriPath ){
        return new  ArrayList<>(Arrays.asList(uriPath.split("/")));
    }

    public Response return4xx(String msg) {
        int code = 400; // url param not set
        if(msg.equals("Object not found!")){
            code = 404;
        }
        return Response.status(code).entity(msg).build();
    }

}
