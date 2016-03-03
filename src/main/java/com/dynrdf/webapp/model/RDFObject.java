package com.dynrdf.webapp.model;


import com.dynrdf.webapp.Template;
import com.dynrdf.webapp.util.Log;

import javax.persistence.*;
import java.util.regex.Pattern;


@Entity
@Table(name="objects")
public class RDFObject {

    private static String[] supportedTemplateTypes = {"TURTLE", "RDF/XML", "N-TRIPLES", "JSON-LD", "SPARQL"};
    transient private Template templateObject;
    transient private Pattern pattern;

    @Id
    @GeneratedValue
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="uriRegex")
    private String uriRegex;

    @Column(name="type")
    private int type;

    @Column(name="template")
    private String template;

    @Column(name="priority")
    private int priority;


    public RDFObject() {
    }


    public RDFObject( RDFObject another ) {
        this.id = another.id;
        this.name = another.name;
        this.type = another.type;
        this.template = another.template;
        this.uriRegex = another.uriRegex;
        this.templateObject = another.templateObject;
        this.priority = another.priority;
    }

    public RDFObject(String name, String uriRegex, int type, String template, int priority) {
        this.name = name;
        this.uriRegex = uriRegex;
        this.type = type;
        this.template = template;
        this.priority = priority;
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
        return "[id=" + id + ", name=" + name + ", uriRegex=" + uriRegex + ", type=" + type + ", template=" + template + ", priority=" + priority + "]";
    }

    public String getUriRegex() {
        return uriRegex;
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

    public void setUriRegex(String uriRegex) {
        this.uriRegex = uriRegex;
    }

    public Template getTemplateObject(){
        return this.templateObject;
    }

    public void preprocessTemplate(){
        Log.debug("Preprocessing template for object: " + name );
        templateObject = new Template(this.template);
        templateObject.preprocess();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void compilePattern(){
        pattern =  Pattern.compile(uriRegex);
    }

    public Pattern getPattern() {
        return pattern;
    }


}
