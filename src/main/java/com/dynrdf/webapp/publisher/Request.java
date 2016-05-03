package com.dynrdf.webapp.publisher;

import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.jena.query.* ;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

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
     * Represents a request for an object data
     */
    public Response execute(){
        String type = object.getType();
        Model model = ModelFactory.createDefaultModel();

        if(type.equals("PROXY")){
            return executeProxy(model);
        }

        Log.debug("uri: " + uri);
        String filledTemplate = object.getRdfTemplateObject().fillTemplate(uri, uriParameters);

        if(filledTemplate == null){
            return Response.status(404).build();
        }

        switch(type){
            case "SPARQL-CONSTRUCT":
                return executeConstruct(model, filledTemplate);
            case "SPARQL-ENDPOINT":
                return executeSparqlEndpoint(filledTemplate);
            default:
                return executeRDFTemplate(model, filledTemplate);

        }
    }

    private Response executeRDFTemplate(Model model, String filledTemplate){
        Log.debug("Filled template:");
        Log.debug(filledTemplate);
        Log.debug("--------------");
        StringReader sr = new StringReader(filledTemplate);
        model.read(sr, null, object.getType());
        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }

    private Response executeConstruct(Model model, String filledTemplate){
        try (QueryExecution qexec = QueryExecutionFactory.create(filledTemplate, model)) {
            model = qexec.execConstruct();
        }
        catch(Exception e){
            Log.debug("SPARQL exception: " + e.getMessage());

            return Response.status(404).build();
        }

        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }

    private Response executeProxy(Model model){
        String url = object.getUrl();

        // construct request url
        if(url.contains("?")){
            url = url + "&" + object.getProxyParam() + "=" + uri;
        }
        else{

            url = url + "?" + object.getProxyParam() + "=" + uri;
        }

        // validate url
        URL requestUrl;
        try{
            requestUrl = new URL(url);
        }
        catch (MalformedURLException ex){
            Log.debug("Malformed proxy URL, object: " + object.toString());
            return return404();
        }

        Log.debug("Proxy object: Reading from:" + requestUrl.toString());
        try{
            model.read(requestUrl.toString());
        }
        catch(Exception ex){ // service error
            Log.debug("Proxy object: Failed to read from URI:" + ex.getMessage());
            return Response.status(500).build();
        }

        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }

    private Response executeSparqlEndpoint (String filledTemplate){
        Model model;
        try{
            String sparqlEndpoint = object.getUrl();
            String sparqlQuery = filledTemplate;
            Query query = QueryFactory.create(sparqlQuery);
            QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(sparqlEndpoint, query);

            // some sparql endpoint have problem with current ttl version - bad characters
            qexec.setModelContentType("application/ld+json");

            if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(sparqlQuery, "construct")){
                // execute construct
                model = qexec.execConstruct();
            }
            else if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(sparqlQuery, "describe")){
                model = qexec.execDescribe();
            }
            else{
                // neither construct nor describe
                return Response.status(400).build();
            }
        }
        catch( Exception ex){
            Log.error("Error in sparql endpoint - ObjectFullName= " + object.getFullName() +":"+ ex.getMessage());
            return Response.status(500).build();
        }


        StringWriter out = new StringWriter();
        model.write(out, produces);

        return Response.status(200).entity(out.toString()).build();
    }

    private Response return404(){

        return Response.status(404).build();
    }

    public List<String> getUriParameters() {
        return uriParameters;
    }

    public RDFObject getObject() {
        return object;
    }

    public String getUri() {
        return uri;
    }

    public String getProduces() {
        return produces;
    }
}