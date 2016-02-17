package com.dynrdf.webapp;

public class TemplateRecord {

    /**
     * Number of uri parameter to insert
     */
    private int uriParameterNumber;

    /**
     * True if @0 was given - insert full url
     */
    private boolean fullUrl = false;

    /**
     * Regex to be applied
     */
    private String regex;

    /**
     * Start index of template record
     */
    private int start;

    /**
     * End index of tempalte record
     */
    private int end;

    public TemplateRecord(int start, int end, int uriParameterNumber, String regex){
        this.start = start;
        this.end = end;
        this.uriParameterNumber = uriParameterNumber;
        this.regex = regex;

        if(uriParameterNumber == 0){
            this.fullUrl = true;
        }
    }

    @Override
    public String toString() {
        return "TemplateRecord{" +
                "uriParameterNumber=" + uriParameterNumber +
                ", fullUrl=" + fullUrl +
                ", regex='" + regex + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public int getUriParameterNumber() {
        return uriParameterNumber;
    }

    public boolean isFullUrl() {
        return fullUrl;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public String getRegex() {
        return regex;
    }
}
