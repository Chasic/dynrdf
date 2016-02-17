package com.dynrdf.webapp;

import com.dynrdf.webapp.model.RDFObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by honza on 2/17/16.
 */
public class Request {
    private List<String> uriParameters;
    private RDFObject object;
    private String uri;
    private String produces;
    private boolean uriByParameter;

    public Request(RDFObject obj, String uri, List<String> uriParameters, String produces, boolean uriByParameter){
        object = obj;
        this.uriParameters = uriParameters;
        this.uri = uri;
        this.produces = produces;
        this.uriByParameter = uriByParameter;
    }

    /**
     *
     * @return
     */
    public Response execute(){
        String data = object.getTemplateObject().fillTemplate(uri, uriParameters, uriByParameter);

        if(data == null){
            return Response.status(404).build();
        }

        return Response.status(200).entity(data).build();
    }
}
