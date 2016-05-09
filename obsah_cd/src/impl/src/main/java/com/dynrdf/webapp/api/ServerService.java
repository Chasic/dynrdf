package com.dynrdf.webapp.api;

import com.dynrdf.webapp.exceptions.InitException;
import com.dynrdf.webapp.logic.RDFObjectContainer;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/server")
public class ServerService {

    @GET
    @Path("/reload")
    public Response reloadDefinitions() {
        try{
            RDFObjectContainer.reload();
        }
        catch(InitException ex){
            Response.status(500).entity(ex.getMessage()).build();
        }
        return Response.status(200).entity("Reloaded").build();
    }
}
