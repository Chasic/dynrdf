package com.dynrdf.webapp.logic;


import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.exceptions.InitException;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class: RDFObjectContainer
 * Singleton: call getInstance()
 *
 * RDFObjectContainer handles all RDF object models.
 */
public class RDFObjectContainer{

    /**
     * Singleton instance
     */
    private static RDFObjectContainer instance = null;

    /**
     * Map of loaded objects - by full name (vendor/name)
     */
    private Map<String, RDFObject> objects;

    /**
     * List of objects' regexp
     */
    private List<String> usedRegex;

    /**
     * Map of objects by priority in groups
     * Performance optimization
     */
    private Map<String, LinkedList<RDFObject>> groupPriority;

    /**
     * Protected constructor
     */
    protected RDFObjectContainer(){
        objects = new HashMap<>();
        groupPriority = new HashMap<>();
        usedRegex = new ArrayList<>();
    }

    /**
     * Get RDFObjectContainer instance
     * @return RDFObjectContainer
     */
    public static RDFObjectContainer getInstance(){
        if( instance == null ){
            instance = new RDFObjectContainer();
        }

        return instance;
    }

    /**
     * Get RDF object from container by full name
     * @param fullName Object full name
     * @return RDFObject, null if not found
     */
    public RDFObject getObject( String fullName ) {
        RDFObject found = objects.get(fullName);

        if( found != null ){
            // return copy of the object
            return new RDFObject(found);
        }

        return null;
    }

    /**
     * Get first RDF object from container by regex match
     * @param uri String
     * @param group String
     * @return RDFObject, null if not found
     */
    public RDFObject getObjectByUriRegexMatch(String uri, String group){
        if(groupPriority.get(group) == null){
            // unknown group
            return null;
        }
        for( RDFObject o : groupPriority.get(group) ){
            Log.debug("Trying to match: uri="+uri+", pattern="+o.getUriRegex()+", objectFullName="+o.getFullName());

            Pattern pattern = o.getPattern();
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                return new RDFObject(o); // return copy
            }
        }

        return null;
    }

    /**
     * Get all objects (copies)
     * @return  List<RDFObject> all loaded objects
     */
    public List<RDFObject> getAll() {
        List<RDFObject> result = new ArrayList<>();
        for( Map.Entry<String, RDFObject> e : objects.entrySet() ){
            result.add( new RDFObject(e.getValue()) );
        }

        return result;
    }
    /**
     * Get all objects (copies) without templates
     * @return  List<RDFObject> all loaded objects
     */
    public List<RDFObject> getAllWithoutTemplate() {
        List<RDFObject> result = new ArrayList<>();
        for( Map.Entry<String, RDFObject> e : objects.entrySet() ){
            result.add( new RDFObject(e.getValue()).setTemplate(null).setDefinitionTTL(null).setHtmlTemplate(null) );
        }

        return result;
    }

    /**
     * Check object if is valid
     * @param obj RDFObject
     * @param updating Boolean True if updating(PUT)
     * @throws ContainerException
     */
    public void validateObject( RDFObject obj, boolean updating, RDFObject updatingObj ) throws ContainerException{
        if( obj.getName() == null || obj.getName().length() == 0 ){
            throw new ContainerException("Name is empty");
        }
        if( obj.getGroup() == null || obj.getGroup().length() == 0 ){
            throw new ContainerException("Group is empty");
        }
        if( obj.getUriRegex() == null || obj.getUriRegex().length() == 0 ){
            throw new ContainerException("Uri regex is empty");
        }
        if( !RDFObject.isValidObjectType(obj.getType()) ){
            throw new ContainerException("Invalid definition type");
        }
        if( (obj.getTemplate() == null ||  obj.getTemplate().length() == 0)
                    && !obj.getType().equals("PROXY")  // proxy has no template
                 ){
            throw new ContainerException("Empty template!");
        }

        RDFObject found = objects.get(obj.getFullName());

        // check for unique regex
        boolean contains = usedRegex.contains(obj.getUriRegex());

        if( updating ){
            if( contains && !updatingObj.getUriRegex().equals(obj.getUriRegex()) ){
                throw new ContainerException("An object with given regex already exists.");
            }

            if(!updatingObj.getFullName().equals(obj.getFullName()) && found != null ){
                throw new ContainerException("Duplicate full name");
            }
        }
        else{
            if( contains ){
                throw new ContainerException("An object with given regex already exists.");
            }
            if( found != null ){
                throw new ContainerException("Duplicate full name");
            }
        }

        // validate proxy data
        if(obj.getType().equals("PROXY")){
            if(obj.getProxyParam() == null || obj.getUrl() == null){
                throw new ContainerException("Missing PROXY mandatory parameters.");
            }

            if(!obj.getProxyParam().matches("[a-zA-Z_0-9]+")){
                throw new ContainerException("Bad parameter name format, expected: [a-zA-Z_0-9]+");
            }
            // validate url
            try{
                URL url = new URL(obj.getUrl());
            }
            catch (MalformedURLException ex){
                throw new ContainerException("Malformed url.");
            }
        }
        // endpoint validate
        if(obj.getType().equals("SPARQL-ENDPOINT")){
            if( obj.getUrl() == null){
                throw new ContainerException("Missing endpoint url for SPARQL-ENDPOINT type.");
            }
            // validate url
            try{
                URL url = new URL(obj.getUrl());
            }
            catch (MalformedURLException ex){
                throw new ContainerException("Malformed url.");
            }
        }
    }

    /**
     * Removes all objects
     * @param removeTTL Boolean true if you want to remove TTL files
     */
    public void removeAll(boolean removeTTL){
        List<RDFObject> toRemove = new ArrayList<>(objects.values());
        for( RDFObject obj : toRemove ){
            try{
                removeObject(obj.getFullName(), removeTTL);
            }
            catch(ContainerException ex){
                // do nothing
            }
        }
        objects = new HashMap<>();
        groupPriority = new HashMap<>();
    }


    /**
     * Remove given object
     * @param fullName String
     * @param removeTTLFile True if you want to remove ttl definition file
     * @throws ContainerException if object does not exist
     */
    public void removeObject( String fullName, boolean removeTTLFile) throws ContainerException{
        if(objects.containsKey(fullName)){
            RDFObject removed = objects.get(fullName);
            objects.remove(fullName);
            groupPriority.get("").remove(removed);
            groupPriority.get(removed.getGroup()).remove(removed);

            usedRegex.remove(removed.getUriRegex());
            // remove file
            if(removeTTLFile){
                if(removed.getFilePath().contains("/.tests_")){
                    Log.debug("Remove TTL def: skipping test file " + removed.getFilePath() + "  ...");
                    return;
                }
                try{
                    File file = new File(removed.getFilePath());
                    if(!file.delete()){
                        Log.info("Failed to remove " + file.getAbsolutePath());
                    }
                }
                catch(Exception e){
                    Log.info("Failed to remove " + removed.getUriRegex() + ", Exception: " + e.getMessage());
                }
            }
        }
        else{
            throw new ContainerException("Object does not exist.");
        }
    }

    /**
     * Update object, given TTL in request body
     * @param request Client request
     * @param originalFilePath String file path of the definition of the updating object
     * @param updatingObj object to be updated
     * @throws ContainerException
     */
    public void updateObject (HttpServletRequest request, String originalFilePath, RDFObject updatingObj ) throws Exception{

        Model model = ModelFactory.createDefaultModel();
        InputStream body = request.getInputStream();
        model.read(body, null, "TTL");

        updateObjectFromModel(model, originalFilePath, updatingObj);
    }

    /**
     * Create object from turtle
     * @param request Client request
     * @throws Exception
     */
    public void createObject (HttpServletRequest request) throws Exception{
        Model model = ModelFactory.createDefaultModel();
        InputStream body = request.getInputStream();
        model.read(body, null, "TTL");

        createObjectFromModel(model);
    }

    /**
     * Register RDFObject
     * @param o An object to register and sav into the container
     */
    public void createObject(RDFObject o)throws Exception{
        o.setFullName(); // jackson build does not set generated fullName
        // set RDF type from txpe before generating ttl
        int index = RDFObject.supportedTemplateTypes.indexOf(o.getType());
        if(index == -1){
            throw new Exception("Unknown type");
        }

        o.setRdfType(RDFObject.supportedTemplateTypesRdf.get(index));
        String objTTL = o.  createTTL();

        InputStream is = new ByteArrayInputStream(objTTL.getBytes());
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null, "TTL");


        createObjectFromModel(model);
    }

    /**
     * Update given object
     * Create TTL def and then updateObjectFromModel()
     * @param o Object in request body
     * @param originalFilePath File path of found object
     * @param updatingObj RDFObject found in container
     * @throws Exception
     */
    public void updateObject (RDFObject o, String originalFilePath, RDFObject updatingObj ) throws Exception{
        // set RDF type from txpe before generating ttl
        int index = RDFObject.supportedTemplateTypes.indexOf(o.getType());
        if(index == -1){
            throw new Exception("Unknown type");
        }
        o.setRdfType(RDFObject.supportedTemplateTypesRdf.get(index));

        String objTTL = o.createTTL();

        Log.debug(objTTL);

        InputStream is = new ByteArrayInputStream(objTTL.getBytes());
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null, "TTL");

        updateObjectFromModel(model, originalFilePath, updatingObj);
    }


    /**
     * Update RDFObject from TTL model
     * @param model Model
     * @param originalFilePath Definition file of the updating object
     * @param updatingObj Definition to update, found in the container before
     * @throws Exception
     */
    private void updateObjectFromModel(Model model, String originalFilePath, RDFObject updatingObj) throws Exception{
        RDFLoader loader = new RDFLoader(model);
        RDFObject o = loader.createObject(true, updatingObj);

        o.setFilePath(originalFilePath);

        // save def into file
        File file = new File(originalFilePath);
        FileWriter objWriter = new FileWriter(file, false);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TTL") ;
        String content = new String(os.toByteArray());

        objWriter.write(content);
        objWriter.close();

        reloadObject(o);
        if(!o.getFullName().equals(updatingObj.getFullName())){
            removeObject(updatingObj.getFullName(), false);
        }
    }

    /**
     * Create RDFObject from TTL model
     * @param model Model
     * @throws Exception
     */
    private void createObjectFromModel(Model model) throws Exception{
        RDFLoader loader = new RDFLoader(model);
        RDFObject o = loader.createObject(false, null);

        String filename = o.getFullName().replace("/", "_");
        String filePath = Config.objectsPath + "/" + filename + ".ttl";
        o.setFilePath(filePath);

        // save def into file
        File file = new File(filePath);
        FileWriter objWriter = new FileWriter(file, false);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TTL") ;
        String content = new String(os.toByteArray());

        objWriter.write(content);
        objWriter.close();

        reloadObject(o);
    }

    /**
     * Initialize container
     * Load stored definitions
     * @throws InitException
     */
    public static void init() throws InitException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        List<File> definitions = container.findObjectsDefinitionFiles();
        container.loadObjects(definitions);
    }

    /**
     * Reload container
     * Reload definitions
     * @throws InitException
     */
    public static void reload() throws InitException{
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        container.removeAll(false);
        List<File> definitions = container.findObjectsDefinitionFiles();
        container.loadObjects(definitions);
    }

    private List<File> findObjectsDefinitionFiles() throws InitException {
        File currentDir = new File(Config.objectsPath);
        List<File> definitions;
        try{
            definitions = RDFLoader.findObjectsDefinitionsRec(currentDir);
        }
        catch (IOException ex){
            throw new InitException(ex.getMessage());
        }
        return definitions;
    }



    /**
     * Loads all objects from DB
     */
    private void loadObjects(List<File> files){
        Log.info("Loading objects ...");
        Model model;
        for(File file : files ){

            model = ModelFactory.createDefaultModel();
            try{

                model.read(new FileInputStream(file.getAbsolutePath()), null, "TTL");
                RDFLoader loader = new RDFLoader(model, file.getAbsolutePath());
                RDFObject o = loader.createObject(false, null);
                reloadObject(o);
            }
            catch(Exception ignored){

                // try to load other objects
            }
        }
    }

    /**
     * Reload object
     * Create/Update case
     * @param o An object to reload
     */
    private void reloadObject( RDFObject o ){
        RDFObject removed = objects.get(o.getFullName());
        if(groupPriority.get("") == null){
            groupPriority.put("", new LinkedList<>());
        }
        if(groupPriority.get(o.getGroup()) == null){
            groupPriority.put(o.getGroup(), new LinkedList<>());
        }
        if(removed != null){
            objects.remove(o.getFullName());
            groupPriority.get("").remove(removed);
            groupPriority.get(removed.getGroup()).remove(removed);
            usedRegex.remove(removed.getUriRegex());
        }
        objects.put(o.getFullName(), o);

        // relocate the object in priority list for regex
        int pos = 0;
        for( RDFObject obj : groupPriority.get("") ){
            if(obj.getPriority() > o.getPriority())
                break;

            pos++;
        }
        groupPriority.get("").add(pos, o);

        // relocate in nogroup
        if(o.getGroup().length() > 0){
            pos = 0;
            for( RDFObject obj : groupPriority.get(o.getGroup()) ){
                if(obj.getPriority() > o.getPriority())
                    break;

                pos++;
            }
            groupPriority.get(o.getGroup()).add(pos, o);
        }

        usedRegex.add(o.getUriRegex());

        o.compilePattern();
        o.preprocessTemplate();
    }
}
