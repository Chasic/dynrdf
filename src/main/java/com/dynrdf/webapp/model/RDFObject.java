package com.dynrdf.webapp.model;


import com.dynrdf.webapp.Template;
import com.dynrdf.webapp.util.Log;

import javax.persistence.*;


@Entity
@Table(name="objects")
public class RDFObject {

    private static String[] supportedTemplateTypes = {"TURTLE", "RDF/XML", "N-TRIPLES", "JSON-LD", "SPARQL"};
    transient private Template templateObject;

    @Id
    @GeneratedValue
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="uri_prefix")
    private String uri_prefix;

    @Column(name="type")
    private int type;

    @Column(name="template")
    private String template;


    public RDFObject() {
    }


    public RDFObject( RDFObject another ) {
        this.id = another.id;
        this.name = another.name;
        this.type = another.type;
        this.template = another.template;
        this.uri_prefix = another.uri_prefix;
        this.templateObject = another.templateObject;
    }

    public RDFObject(String name, String uri_prefix, int type, String template) {
        this.name = name;
        this.uri_prefix = uri_prefix;
        this.type = type;
        this.template = template;
    }

    public RDFObject setId( int id ){
        this.id = id;

        return this;
    }

    public int getId(){
        return id;
    }

    public RDFObject setName( String name ){
        this.name = name;

        return this;
    }

    public String getName(){
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public RDFObject setTemplate(String template) {
        this.template = template;

        return this;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + name + ", uri_prefix=" + uri_prefix + ", type=" + type + ", template=" + template + "]";
    }

    public String getUri_prefix() {
        return uri_prefix;
    }

    public int getType() {
        return type;
    }


    /**
     * Get RDF serialization by id
     * @param typeId
     * @return RDF serialization name | null if not supported
     */
    public static String getRDFType( int typeId ){
        if( supportedTemplateTypes.length - 1 >= typeId && typeId >= 0 ){
            return supportedTemplateTypes[typeId];
        }

        return null;
    }

    public void setUri_prefix(String uri_prefix) {
        this.uri_prefix = uri_prefix;
    }

    public Template getTemplateObject(){
        return this.templateObject;
    }

    public void preprocessTemplate(){
        Log.debug("Preprocessing template for object: " + name );
        templateObject = new Template(this.template);
        templateObject.preprocess();
    }
}
