package com.dynrdf.webapp.api;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.json.simple.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;



@Path("/objects")
public class RDFObjectService {

    @GET
    @Path("test")
    public Response hello(){
        return Response.status(200).build();
    }

    @GET
    @Path("/{fullName:.+}")
    @Produces("application/json")
    public Response getObjectJson(@PathParam("fullName") String fullName) {
        fullName = fullName.replace("%2F", "/");
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(fullName);
        if(  o != null ){

            String json = new Gson().toJson( o );

            return Response.status(200).entity(json).build();
        }

        return Response.status(404).build();
    }

    @GET
    @Produces("application/json")
    public Response getAllObjectsJson() {
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<RDFObject> oobjects = container.getAll();
        String json = new Gson().toJson( oobjects );

        return Response.status(200).entity(json).build();
    }

    @POST
    @Consumes("application/json")
    public Response createObject(RDFObject o){
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();

            container.createObject(o);
        }
        catch( Exception ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Created.");
    }

    @POST
    @Consumes("text/turtle")
    public Response createObjectTurtle(@Context HttpServletRequest request){
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();

            container.createObject(request);
        }
        catch( Exception ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Created.");
    }

    @PUT
    @Path("/{fullName:.+}")
    @Consumes("application/json")
    public Response updateObjectJson(RDFObject obj, @Context HttpServletRequest request, @PathParam("fullName") String fullName){
        fullName = fullName.replace("%2F", "/");
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();
            RDFObject o = container.getObject(fullName);
            if(o == null){
                return returnError("Object not found.");
            }
            container.updateObject(obj, o.getFilePath(), o);
        }
        catch( Exception ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Updated.");
    }

    @PUT
    @Path("/{fullName:.+}")
    @Consumes("text/turtle")
    public Response updateObjectTurtle(@Context HttpServletRequest request, @PathParam("fullName") String fullName){
        fullName = fullName.replace("%2F", "/");
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();
            RDFObject o = container.getObject(fullName);
            if(o == null){
                return returnError("Object not found.");
            }
            container.updateObject(request, o.getFilePath(), o);
        }
        catch( Exception ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Updated.");
    }

    @DELETE
    @Path("/{fullName:.+}")
    public Response removeObject(@PathParam("fullName") String fullName){
        fullName = fullName.replace("%2F", "/");
        try{
            RDFObjectContainer.getInstance().removeObject(fullName, true);
        }catch( ContainerException ex ){
            return returnError(ex.getMessage());
        }
        return returnOK("Object removed.");
    }


    /**
     * Return 400 error with given message
     * @param msg
     * @return Response
     */
    private Response returnError(String msg){
        JSONObject obj = new JSONObject();

        obj.put("status", "error");
        obj.put("msg", msg);

        return Response.status(400).entity(obj.toJSONString()).build();
    }

    /**
     * Return 400 error with given message
     * @param msg
     * @return Response
     */
    private Response returnOK(String msg){
        JSONObject obj = new JSONObject();

        obj.put("status", "ok");
        obj.put("msg", msg);

        return Response.status(200).entity(obj.toJSONString()).build();
    }

    // ####### get objects by content-negotiation
    @GET
    @Path("/{fullName:.+}")
    // @Produces("text/turtle")
    public Response getObjectTurtle(@PathParam("fullName") String fullName) {
        fullName = fullName.replace("%2F", "/");
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(fullName);
        if(  o != null ){
            return Response.status(200).entity(o.getDefinitionTTL()).build();
        }

        return Response.status(404).build();
    }

    @GET
    @Path("/{fullName:.+}")
    @Produces("application/n-triples")
    public Response getObjectNTriples(@PathParam("fullName") String fullName) {
        fullName = fullName.replace("%2F", "/");
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(fullName);
        if(  o != null ){
            Model model = ModelFactory.createDefaultModel();
            StringReader sr = new StringReader(o.getDefinitionTTL());
            model.read(sr, null, "TURTLE");
            StringWriter out = new StringWriter();
            model.write(out, "N-TRIPLES");

            return Response.status(200).entity(out.toString()).build();
        }

        return Response.status(404).build();
    }

    @GET
    @Path("/{fullName:.+}")
    @Produces("application/ld+json")
    public Response getObjectLDJson(@PathParam("fullName") String fullName) {
        fullName = fullName.replace("%2F", "/");
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(fullName);
        if(  o != null ){
            Model model = ModelFactory.createDefaultModel();
            StringReader sr = new StringReader(o.getDefinitionTTL());
            model.read(sr, null, "TURTLE");
            StringWriter out = new StringWriter();
            model.write(out, "JSON-LD");

            return Response.status(200).entity(out.toString()).build();
        }

        return Response.status(404).build();
    }

    @GET
    @Path("/{fullName:.+}")
    @Produces({"application/rdf+xml", "application/xml"})
    public Response getObjectXML(@PathParam("fullName") String fullName) {
        fullName = fullName.replace("%2F", "/");
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(fullName);
        if(  o != null ){
            Model model = ModelFactory.createDefaultModel();
            StringReader sr = new StringReader(o.getDefinitionTTL());
            model.read(sr, null, "TURTLE");
            StringWriter out = new StringWriter();
            model.write(out, "RDF/XML");

            return Response.status(200).entity(out.toString()).build();
        }

        return Response.status(404).build();
    }

    // ####### get ALL objects by content-negotiation
    @GET
    // default turtle @Produces("text/turtle")
    public Response getAllObjectsTurtle() {
        Model model = ModelFactory.createDefaultModel();
        setModelFromAllObjects(model);

        StringWriter out = new StringWriter();
        model.write(out, "TURTLE");

        return Response.status(200).entity(out.toString()).build();
    }

    @GET
    @Produces({"application/rdf+xml", "application/xml"})
    public Response getAllObjectsXML() {
        Model model = ModelFactory.createDefaultModel();
        setModelFromAllObjects(model);

        StringWriter out = new StringWriter();
        model.write(out, "RDF/XML");

        return Response.status(200).entity(out.toString()).build();
    }

    @GET
    @Produces("application/ld+json")
    public Response getAllObjectsLDJson() {
        Model model = ModelFactory.createDefaultModel();
        setModelFromAllObjects(model);

        StringWriter out = new StringWriter();
        model.write(out, "JSON-LD");

        return Response.status(200).entity(out.toString()).build();
    }

    @GET
    @Produces("application/n-triples")
    public Response getAllObjectsNTriples() {
        Model model = ModelFactory.createDefaultModel();
        setModelFromAllObjects(model);

        StringWriter out = new StringWriter();
        model.write(out, "N-TRIPLES");

        return Response.status(200).entity(out.toString()).build();
    }



    /**
     * Create model from all TTL Definitions
     * @param model
     */
    private void setModelFromAllObjects(Model model){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<RDFObject> oobjects = container.getAll();
        String ttl = "";
        for(RDFObject o : oobjects){
            ttl += "\n" + o.getDefinitionTTL();
        }
        StringReader sr = new StringReader(ttl);
        model.read(sr, null, "TURTLE");
    }
}