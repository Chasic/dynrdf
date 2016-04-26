package com.dynrdf.webapp.model;


import com.dynrdf.webapp.Config;
import com.dynrdf.webapp.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RDFObject {

    public static List<String> supportedTemplateTypes = Arrays.asList("TURTLE", "RDF/XML", "N-TRIPLES", "JSON-LD",
            "SPARQL-CONSTRUCT", "SPARQL-ENDPOINT", "PROXY");
    // rdf types, same indexes
    public static List<String> supportedTemplateTypesRdf = Arrays.asList("Turtle", "RDFXML", "NTriples", "JSONLD",
            "SPARQLConstruct", "SPARQLEndpoint", "Proxy");
    transient private Template rdfTemplateObject;
    transient private Template htmlTemplateObject;
    transient private Pattern pattern;
    transient private String rdfType;

    private String name;

    private String uriRegex;

    private String type;

    private String template;

    private int priority;

    private String url;

    private String proxyParam;

    private String htmlTemplate;

    private String group;

    private String fullName;

    private String definitionTTL;

    // absolute path to definition
    private String filePath;


    public RDFObject() {
    }


    public RDFObject( RDFObject another ) {
        this.name = another.name;
        this.type = another.type;
        this.template = another.template;
        this.uriRegex = another.uriRegex;
        this.rdfTemplateObject = another.rdfTemplateObject;
        this.htmlTemplateObject = another.htmlTemplateObject;
        this.priority = another.priority;
        this.proxyParam = another.proxyParam;
        this.url = another.url;
        this.htmlTemplate = another.htmlTemplate;
        this.definitionTTL = another.definitionTTL;
        this.fullName = another.fullName;
        this.group = another.group;
        this.filePath = another.filePath;
    }

    public RDFObject(String name, String uriRegex, String type, String template, int priority,
                     String url, String proxyParam, String htmlTemplate, String definitionTTL) {
        this.name = name;
        this.uriRegex = uriRegex;
        this.type = type;
        this.template = template;
        this.priority = priority;
        this.proxyParam = proxyParam;
        this.url = url;
        this.htmlTemplate = htmlTemplate;
        this.definitionTTL = definitionTTL;
    }


    public RDFObject setName( String name ){
        this.name = name;

        setFullName();
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

    public void setType(String type) throws Exception{
        if(isValidObjectType(type)){
            this.type = type;
        }
        else throw new Exception("Unknown object type");
    }

    @Override
    public String toString() {
        return "RDFObject{" +
                "rdfTemplateObject=" + rdfTemplateObject +
                ", htmlTemplateObject=" + htmlTemplateObject +
                ", pattern=" + pattern +
                ", name='" + name + '\'' +
                ", uriRegex='" + uriRegex + '\'' +
                ", type='" + type + '\'' +
                ", template='" + template + '\'' +
                ", priority=" + priority +
                ", url='" + url + '\'' +
                ", proxyParam='" + proxyParam + '\'' +
                ", htmlTemplate='" + htmlTemplate + '\'' +
                ", group='" + group + '\'' +
                ", fullName='" + fullName + '\'' +
                ", definitionTTL='" + definitionTTL + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getUriRegex() {
        return uriRegex;
    }

    public String getType() {
        return type;
    }


    /**
     * Check if is valid object type
     * @param type String
     * @return boolean true if valid | false if invalid
     */
    public static boolean isValidObjectType( String type ){
        return supportedTemplateTypes.contains(type);
    }

    public void setUriRegex(String uriRegex) {
        this.uriRegex = uriRegex;
    }

    public Template getRdfTemplateObject(){
        return this.rdfTemplateObject;
    }

    public Template getHtmlTemplateObject() {
        return htmlTemplateObject;
    }


    public void preprocessTemplate(){
        // RDF template
        Log.debug("Preprocessing RDF template for object: " + name );
        rdfTemplateObject = new Template(this.template);
        rdfTemplateObject.preprocess();

        // HTML template
        Log.debug("Preprocessing HTML template for object: " + name );
        htmlTemplateObject = new Template(this.htmlTemplate);
        htmlTemplateObject.preprocess();
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

    public String getProxyParam() {
        return proxyParam;
    }

    public void setProxyParam(String proxyParam) {
        this.proxyParam = proxyParam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String proxyUrl) {
        this.url = proxyUrl;
    }

    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public RDFObject setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;

        return this;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
        setFullName();
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(){
        fullName = group;
        fullName += "/" + name;
    }

    public String getDefinitionTTL() {
        return definitionTTL;
    }

    public RDFObject setDefinitionTTL(String definitionTTL) {
        this.definitionTTL = definitionTTL;

        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String createTTL(){
        if(uriRegex == null) uriRegex = "";
        if(template == null) template = "";
        if(htmlTemplate == null) htmlTemplate = "";
        if(fullName == null) fullName = "";
        if(group == null) group = "";
        if(url == null) url = "";
        if(proxyParam == null) proxyParam = "";

        definitionTTL = "@prefix dynrdf: <"+Config.ObjectRDFS+"> .\n" +
                "@prefix def: <"+Config.ObjectBaseUrl+"> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix dcterms: <http://purl.org/dc/terms/> .\n" +
                "\n" +
                "def:" + fullName.replace("/", "_").replace(" ", "_") + "\n" +
                "    a               dynrdf:"+rdfType+" ;\n" +
                "    dcterms:title     \""+name+"\"^^xsd:string ;\n" +
                (group.length() > 0 ?
                "    dynrdf:group   \""+ group +"\"^^xsd:string ;\n"
                : "") +
                "    dynrdf:regex    \""+uriRegex.replace("\\/", "\\\\/")+"\"^^xsd:string ;\n" +
                "    dynrdf:priority \""+priority+"\"^^xsd:integer ;\n" +
                "    dynrdf:objectTemplate \"\"\""+template.replace("\\/", "\\\\/")+"\"\"\"^^xsd:string ;\n" +
                "    dynrdf:htmlTemplate \""+htmlTemplate.replace("\\/", "\\\\/")+"\"^^xsd:string ;\n" +
                (url.length() > 0 ?
                "    dynrdf:url <"+url+"> ;\n"
                : "") +
                (proxyParam.length() > 0 ?
                "    dynrdf:proxyParam \""+proxyParam+"\"^^xsd:string .\n"
                : "") +
                "\n";

        Log.debug("------------");
        Log.debug(definitionTTL);
        Log.debug("------------");

        return definitionTTL;
    }

    public String getRdfType() {
        return rdfType;
    }

    public void setRdfType(String rdfType) {
        this.rdfType = rdfType;
    }
}
