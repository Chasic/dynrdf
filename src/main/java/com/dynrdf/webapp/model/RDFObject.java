package com.dynrdf.webapp.model;


import javax.persistence.*;



@Entity
@Table(name="objects")
public class RDFObject {

    @Id
    @GeneratedValue
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="uri_prefix")
    private String uri_prefix;

    @Column(name="type")
    private int type;

    public RDFObject() {
    }


    public RDFObject( RDFObject another ) {
        this.id = another.id;
        this.name = another.name;
        this.type = another.type;
        this.uri_prefix = another.uri_prefix;
    }

    public RDFObject(String name, String uri_prefix, int type) {
        this.name = name;
        this.uri_prefix = uri_prefix;
        this.type = type;
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
}
