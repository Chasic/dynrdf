package com.dynrdf.webapp.model;

import com.dynrdf.webapp.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template class for RDF object templates with placeholders
 * Placeholders syntax examples:
 *  [@1, "regex"]
 *  [  @1,      "regex"      ] - whitespaces before and after parameter index and regex
 *  [@1] - no regex
 *  [@0] - full url
 *
 *  Parameters numbers:
 *      0 - full url
 *      1 - first parameter after object identifier
 *      2 - second parameter ...
 *
 *      Example - date entity http://dynrdf.com/data/?url=http://company.com/rdf/date/10-10-2015 :
 *          @0 = http://company.com/rdf/date/10-10-2015
 *          @6 = 10-10-2015
 */
public class Template {

    private String template;
    private List<TemplatePlaceholder> placeholders;


    /**
     * Pattern for:
     * [ @1, "regex"]
     * [@2]
     */
    private static Pattern templatePattern = Pattern.compile("\\[\\s*@(\\d+)\\s*(,\\s*[\\\"|\\\\\"](.*)[\\\"|\\\\\"])?\\s*\\]");


    public Template(String template){
        this.template = template;
        placeholders = new ArrayList<>();
    }

    /**
     * Find every placeholder and prepare for later replacements
     */
    public void preprocess(){
        if(template == null) return; // proxy
        Matcher matcher = templatePattern.matcher(template);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String regex = null;
            int groups = matcher.groupCount();

            // parameter number + regex
            if( groups > 1 ){
                regex = matcher.group(3);
            }
            int uriParameter = Integer.parseInt(matcher.group(1));

            TemplatePlaceholder record = new TemplatePlaceholder(start, end, uriParameter, regex);
            placeholders.add(record);

            Log.debug("Found template record: " + record);
        }
    }


    /**
     * Fill prepared template with parameters
     * @param uri Full URI
     * @param uriParameters List of URI parameters after identifier
     * @return String data from template OR null, if failed to fill
     *         placeholders (all required parameters have to be set),
     *         null if template is not set (proxy case)
     */
    public String fillTemplate(String uri, List<String> uriParameters){
        if(template == null) return null;

        StringBuilder data = new StringBuilder();
        int leftBoundary = 0;
        for( TemplatePlaceholder record : placeholders){
            // check if parameter exists
            if( record.getUriParameterNumber() < 0 || record.getUriParameterNumber() -1 >= uriParameters.size() ){
                return null;
            }

            // fill template text to the left
            data.append(template.substring(leftBoundary, record.getStart()));


            // apply regex if set
            Pattern pattern = null;
            String regex = record.getRegex();
            if (regex != null){
                pattern = Pattern.compile(regex);
            }

            // fill full uri
            if(record.getUriParameterNumber() == 0) {
                if(regex != null){
                    Matcher matcher = pattern.matcher(uri);
                    if (matcher.find()){
                        data.append(matcher.group(1)); // append regex match
                    }
                }
                else{
                    data.append(uri);
                }
            }
            // fill parameter
            else{
                // parameters are indexed from 1
                // "num - 1" is used if uri was set by ?url parameter
                int parameterNum = record.getUriParameterNumber() - 1;
                if(regex != null){
                    Matcher matcher = pattern.matcher(uriParameters.get(parameterNum));
                    if (matcher.find()){
                        data.append(matcher.group(1)); // append regex match
                    }
                }
                else{
                    data.append(uriParameters.get(parameterNum));
                }

            }
            leftBoundary = record.getEnd();
        }

        // fill other template text to the right
        data.append(template.substring(leftBoundary));

        return data.toString();
    }

    public String getTemplate() {
        return template;
    }

    public List<TemplatePlaceholder> getPlaceholders() {
        return placeholders;
    }
}
