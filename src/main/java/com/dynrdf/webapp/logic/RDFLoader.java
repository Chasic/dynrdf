package com.dynrdf.webapp.logic;

import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.JenaException;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * RDFLoader creates a RDFObject from Model (RDF TTL -> RDFObject mapper)
 */
public class RDFLoader {

    private Model model;
    private String filePath;
    private RDFObject obj;
    private String resource;

    public RDFLoader(Model model, String filePath) {
        this.model = model;
        this.filePath = filePath;
    }

    public RDFLoader(Model model) {
        this.model = model;
        this.filePath = ""; // will be set (POST request)
    }

    public RDFObject createObject() throws Exception{
        Log.debug("Creating object");
        obj = new RDFObject();
        try{
            prepare();
            getDefinition();

            // check data
            RDFObjectContainer container = RDFObjectContainer.getInstance();
            container.validateObject(obj, false);
        }
        catch( Exception ex ){
            throw new Exception("Cannot load mandatory data for " + filePath + ", msg: " + ex.getMessage());
        }

        // set file path and TTL def to object
        obj.setFilePath(filePath);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TTL") ;
        String modelString = new String(os.toByteArray());
        obj.setDefinitionTTL(modelString);

        return obj;
    }

    /**
     * Check if exist an object (only one)
     * @throws Exception
     */
    private void prepare() throws Exception{
        String queryString = "PREFIX dynrdf: <" + Config.ObjectRDFS + ">\n" +
                             "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                             "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                             "PREFIX owl:  <http://www.w3.org/2002/07/owl#>" +
                             "SELECT ?res  \n" +
                             "WHERE \n" +
                             "  { ?res a dynrdf:Object }" ;
        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            boolean resSet = false;
            for ( ; results.hasNext() ; ) {
                if(resSet){
                    throw new Exception("Def. file has to contain definition for only ONE object!");
                }
                QuerySolution soln = results.nextSolution() ;
                Resource r = soln.getResource("res") ; // Get a result variable - must be a resource

                resource = r.toString();
                resSet = true;
            }
        }

    }

    /**
     * Load definition from Model
     * @throws Exception
     */
    private void getDefinition() throws Exception{
        String queryString = "PREFIX dynrdf: <" + Config.ObjectRDFS + ">\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>" +
                "SELECT ?type ?value  \n" +
                "WHERE \n" +
                "  { <" + resource + "> ?type ?value }" ;
        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            for ( ; results.hasNext() ; ) {
                QuerySolution soln = results.nextSolution() ;

                RDFNode n = soln.get("value") ; // all values are not resources
                if ( n.isResource() ) continue; // skip resources ( dynrdf:Object etc. )

                String val = ((Literal)n).getLexicalForm();
                Resource typeRes = soln.getResource("type") ;
                String type = typeRes.toString();

                Log.debug("Found: type=" + type + ", val=" + val);
                try{
                    setParam(type, val);
                }
                catch(Exception ex){
                    throw new Exception("Exception during reading and setting parameters of the RDFObject: " + ex.getMessage());
                }
            }

            /*ByteArrayOutputStream os = new ByteArrayOutputStream();
            ResultSetFormatter.out(os, results, query) ;
            String res = new String(os.toByteArray(),"UTF-8");
            Log.debug(res);*/
        }
    }

    /**
     * Set param of RDFObject obj
     * @param param
     * @param val
     * @throws Exception
     */
    private void setParam(String param, String val) throws Exception{
        String objParam = param.substring(param.indexOf("#")+1);
        Log.debug("Setting: param=" + objParam + ", val=" + val);
        switch(objParam){
            case "name":
                obj.setName(val);
                break;
            case "type":
                try{
                    obj.setType(val);
                }
                catch (Exception ex){
                    throw ex;
                }
                break;
            case "vendor":
                obj.setVendor(val);
                break;
            case "regex":
                obj.setUriRegex(val);
                break;
            case "priority":
                obj.setPriority(Integer.parseInt(val));
                break;
            case "objectTemplate":
                obj.setTemplate(val);
                break;
            case "htmlTemplate":
                obj.setHtmlTemplate(val);
                break;
            case "url":
                obj.setUrl(val);
                break;
            case "proxyParam":
                obj.setProxyParam(val);
                break;
            default:
                break; // other user-defined types we dont care
        }

    }


}
