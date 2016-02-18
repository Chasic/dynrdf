package com.dynrdf.webapp;

import com.dynrdf.webapp.model.RDFObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * Represents a request for an object data
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
     * Execute the request
     * @return Response
     */
    public Response execute(){
        String data = object.getTemplateObject().fillTemplate(uri, uriParameters, uriByParameter);

        if(data == null){
            return Response.status(404).build();
        }

        StringReader sr = new StringReader(data);

        Model model = ModelFactory.createDefaultModel();
        model.read(sr, null, object.getRDFType(object.getType()));


        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }
}
