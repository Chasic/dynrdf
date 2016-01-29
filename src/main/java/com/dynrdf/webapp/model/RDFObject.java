package com.dynrdf.webapp.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="objects")
public class RDFObject {

    private static String[] supportedRDFTypes = {"turtle", "sparql"};

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
        this.uri_prefix = another.uri_prefix;
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

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + name + ", uri_prefix=" + uri_prefix + ", type=" + type + "]";
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
        if( supportedRDFTypes.length - 1 >= typeId && typeId >= 0 ){
            return supportedRDFTypes[typeId];
        }

        return null;
    }

    public void setUri_prefix(String uri_prefix) {
        this.uri_prefix = uri_prefix;
    }
}
