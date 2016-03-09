package com.dynrdf.webapp;

import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.jena.query.* ;

/**
 * Represents a request for an object data
 */
public class Request {
    private List<String> uriParameters;
    private RDFObject object;
    private String uri;
    private String produces;

    public Request(RDFObject obj, String uri, List<String> uriParameters, String produces){
        object = obj;
        this.uriParameters = uriParameters;
        this.uri = uri;
        this.produces = produces;
    }

    /**
     * Execute the request
     * @return Response
     */
    public Response execute(){
        String filledTemplate = object.getTemplateObject().fillTemplate(uri, uriParameters);

        if(filledTemplate == null){
            return Response.status(404).build();
        }

        Model model = ModelFactory.createDefaultModel();

        // execute SPARQL query
        if(object.getRDFType(object.getType()).equals("SPARQL")){
            try (QueryExecution qexec = QueryExecutionFactory.create(filledTemplate, model)) {
                model = qexec.execConstruct();
            }
            catch(Exception e){
                Log.debug("SPARQL exception: " + e.getMessage());

                return Response.status(404).build();
            }

        }
        else{
            StringReader sr = new StringReader(filledTemplate);
            model.read(sr, null, object.getRDFType(object.getType()));
        }




        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }
}
