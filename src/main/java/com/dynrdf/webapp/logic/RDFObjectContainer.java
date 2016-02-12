package com.dynrdf.webapp.logic;


import com.dynrdf.webapp.exceptions.ContainerException;
import com.dynrdf.webapp.model.RDFObject;
import com.dynrdf.webapp.util.Log;
import javafx.util.Pair;
import org.apache.jena.vocabulary.RDF;
import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class: RDFObjectContainer
 * Singleton: call getInstance()
 *
 * RDFObjectContainer handles all RDF object models.
 */
public class RDFObjectContainer{


    private static SessionFactory factory;

    /**
     * Singleton instance
     */
    private static RDFObjectContainer instance = null;

    /**
     * Map of loaded objects
     */
    private Map<Integer, RDFObject> objects;

    /**
     * Protected constructor
     */
    protected RDFObjectContainer(){
        objects = new HashMap<>();

        // init hibernate
        try {
            factory = new AnnotationConfiguration()
                    .configure().buildSessionFactory();
        } catch (Throwable ex) {
            Log.fatal(ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get Hibernate Session
     * @return Session
     * @throws HibernateException
     */
    public static Session getSession()
            throws HibernateException {
        return factory.openSession();
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
     * Get RDF object from container
     * @param id Objec id
     * @return RDFObject, null if not found
     */
    public RDFObject getObject( int id ) {
        RDFObject found = objects.get(id);

        if( found != null ){
            // return copy of the object
            return new RDFObject(found);
        }

        return null;
    }

    /**
     * Get all objects (copies)
     * @return  List<RDFObject> all loaded objects
     */
    public List<RDFObject> getAll() {
        List<RDFObject> result = new ArrayList<>();
        for( Map.Entry<Integer, RDFObject> e : objects.entrySet() ){
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
        for( Map.Entry<Integer, RDFObject> e : objects.entrySet() ){
            result.add( new RDFObject(e.getValue()).setTemplate(null) );
        }

        return result;
    }

    /**
     * Save given object into the container
     * @param obj Object to save
     * @throws ContainerException when cannot create new object
     */
    public void createObject  ( RDFObject obj ) throws ContainerException{

        validateObject(obj, false);

        Session session = factory.openSession();
        Transaction tx = null;
        Integer id = null;
        boolean err = false;


        try{
            tx = session.beginTransaction();
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            Log.error(e.toString());
            err = true;
        }finally {
            session.close();
            if( !err ){
                obj.setId(id);
                objects.put(id, obj);
                Log.debug("Creating new object: " + obj.toString());
            }
            else{
                throw new ContainerException("Cannot create new object, see log.");
            }
        }

    }

    /**
     * Check object if is valid
     * @param obj
     * @throws ContainerException
     */
    private void validateObject( RDFObject obj, boolean updating ) throws ContainerException{
        // check unique uri path
        if( !checkURiPath(obj.getUri_prefix(), (updating ? obj : null) ) ){
            throw new ContainerException("Invalid URI prefix or already in use.");
        }

        if( obj.getName() == null || obj.getName().length() == 0 ){
            throw new ContainerException("Name is empty");
        }
        else if(  RDFObject.getRDFType(obj.getType()) == null ){
            throw new ContainerException("Unknown RDF serialization number, not supported ...");
        }
        else if( obj.getTemplate() == null ||  obj.getTemplate().length() == 0 ){
            throw new ContainerException("Empty template!");
        }
    }


    /**
     * Remove given object
     * @param id
     * @throws ContainerException if object does not exist or hibernate error occurred
     */
    public void removeObject( int id ) throws ContainerException{
        if(  objects.containsKey(id) ){
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();
            try {
                RDFObject toDel = objects.get(id);
                Log.debug(toDel.toString());
                session.delete(toDel);
            }
            catch( HibernateException ex ){
                throw new ContainerException("An error occurred during removing object from database: " + ex.getMessage());
            }
            finally {
                session.flush();
                tx.commit();
                session.close();
            }

            objects.remove(id);
        }
        else{
            throw new ContainerException("Object does not exist.");
        }
    }

    /**
     * Check if given uri path is free and valid
     * @param uriPrefix String URI prefix to check
     * @param toCompare RDFObject Exclude from check given object(update case)
     * @return boolean true
     */
    private boolean checkURiPath(String uriPrefix, RDFObject toCompare){
        if( uriPrefix == null || uriPrefix.contains(" ") || uriPrefix.contains("/") ){
            return false;
        }

        Session session = factory.openSession();
        RDFObject result = null;
        try{
            Criteria cr = session.createCriteria(RDFObject.class);
            cr.add(Restrictions.eq("uri_prefix", uriPrefix ));
            result = (RDFObject) cr.uniqueResult();

        }catch (HibernateException e) {
            Log.error(e.toString());
            return false;
        }finally {
            session.close();
        }


        if( result == null ) return true;

        if( toCompare != null ){
            if( result.getId() == toCompare.getId() ){
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Update object
     * @param obj Object to update
     * @throws ContainerException
     */
    public void updateObject ( RDFObject obj ) throws ContainerException{
        validateObject(obj, true);

        Session session = factory.openSession();
        Transaction tx = null;
        int id = obj.getId();
        boolean err = false;

        if( !objects.containsKey(id) ){
            throw new ContainerException("Object with given id dose not exist.");
        }


        try{
            tx = session.beginTransaction();
            session.update(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            Log.error(e.toString());
            err = true;
        }finally {
            session.close();
            if( !err ){
                objects.remove(id);
                objects.put(id, obj);
                Log.debug("Updating object, new data: " + obj.toString());
            }
            else{
                throw new ContainerException("Cannot update given object, see log.");
            }
        }
    }

    public static void init(){
        RDFObjectContainer container = RDFObjectContainer.getInstance();
        container.loadObjects();

    }

    /**
     * Loads all objects from DB
     */
    private void loadObjects(){
        Log.info("Loading objects ...");
        Session session = factory.openSession();
        List<RDFObject> results = null;
        try{
            Criteria cr = session.createCriteria(RDFObject.class);
            results = cr.list();

        }catch (HibernateException e) {
            Log.error(e.toString());
            Log.error("Cannot load objects ...");
        }finally {
            session.close();
        }

        for( RDFObject o : results ){
            objects.put(o.getId(), o);
        }

        Log.info("Loaded " + results.size() + " objects.");
    }

    private void reloadObject( RDFObject o ){
        objects.put(o.getId(), o);
    }

}
