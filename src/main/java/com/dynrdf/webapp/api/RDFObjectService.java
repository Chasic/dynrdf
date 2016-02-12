package com.dynrdf.webapp.api;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

import java.util.List;



@Path("/objects")
public class RDFObjectService {


    @GET
    @Path("/{id}")
    public Response getObject(@PathParam("id") int id) {
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        RDFObject o = container.getObject(id);
        if(  o != null ){

            String json = new Gson().toJson( o );

            return Response.status(200).entity(json).build();
        }

        return Response.status(404).build();
    }

    @GET
    public Response getAllObjects() {
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<RDFObject> oobjects = container.getAllWithoutTemplate();
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
        catch( ContainerException ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Object created!");
    }

    @PUT
    @Consumes("application/json")
    @Path("/{id}")
    public Response updateObject(RDFObject o){
        try{
            RDFObjectContainer container = RDFObjectContainer.getInstance();
            container.updateObject(o);
        }
        catch( ContainerException ex ){
            return returnError(ex.getMessage());
        }

        return returnOK("Updated.");
    }

    @DELETE
    @Path("/{id}")
    public Response removeObject(@PathParam("id") int id){
        try{
            RDFObjectContainer.getInstance().removeObject(id);
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