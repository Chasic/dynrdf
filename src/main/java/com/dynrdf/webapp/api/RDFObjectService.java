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
import org.json.simple.JSONObject;

import java.util.List;



@Path("/objects")
public class RDFObjectService {


    @GET
    @Path("/{fullName:.+}")
    public Response getObject(@PathParam("fullName") String fullName) {
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
    public Response getAllObjects() {
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<RDFObject> oobjects = container.getAll();
        String json = new Gson().toJson( oobjects );

        return Response.status(200).entity(json).build();
    }

    @POST
    @Consumes("application/json")
    public Response createObject(RDFObject o, @Context HttpServletRequest request){
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();

            o.createTurtleDefinition(request.getRequestURL().toString());
            container.createObject(o, request);
        }
        catch( ContainerException ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Object created!");
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
}