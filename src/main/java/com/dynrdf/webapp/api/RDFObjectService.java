package com.dynrdf.webapp.api;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.model.RDFObject;
import com.google.gson.Gson;
import org.apache.jena.vocabulary.RDF;
import org.hibernate.HibernateException;

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
            JSONObject obj = new JSONObject();

            obj.put("id", o.getId());
            obj.put("name", o.getName());
            obj.put("type", o.getType());
            obj.put("url_prefix", o.getUri_prefix());

            return Response.status(200).entity(obj.toJSONString()).build();
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
    public Response createObject(){
        String output = "create object ";
        return Response.status(200).entity(output).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateObject(@PathParam("id") int id){
        String output = "update object ";
        return Response.status(Response.Status.OK).entity(output).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeObject(@PathParam("id") int id){
        String output = "update object ";
        return Response.status(Response.Status.OK).entity(output).build();
    }
}