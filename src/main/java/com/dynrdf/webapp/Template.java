package com.dynrdf.webapp;

import com.dynrdf.webapp.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by honza on 2/17/16.
 */
public class Template {

    private String template;
    private List<TemplateRecord> records;


    public Template(String template){
        this.template = template;
    }

    public void preprocess(){
        records = new ArrayList<>();
        Pattern pattern =  Pattern.compile("\\[\\s*@(\\d+)\\s*(,\\s*\\\"(.*)\\\")?\\s*\\]");
        /** Pattern for:
         * [ @1, "regex"]
         * [@2]
         */
        Matcher matcher = pattern.matcher(template);
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

            TemplateRecord record = new TemplateRecord(start, end, uriParameter, regex);
            records.add(record);

            Log.debug("Found template record: " + record);
        }
    }

    public String fillTemplate(String uri, List<String> uriParameters, boolean uriByParameter){
        StringBuffer data = new StringBuffer();
        int leftBoundary = 0;
        for( TemplateRecord record : records ){
            // check if parameter exists
            if( record.getUriParameterNumber() < 0 || record.getUriParameterNumber() >= uriParameters.size() )
                return null;

            // fill template text to the left
            data.append(template.substring(leftBoundary, record.getStart()));


            // apply regex if set
            Pattern pattern = null;
            String regex = record.getRegex();
            if (regex != null){
                pattern = pattern.compile(regex);
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
                int parameterNum = uriByParameter ?  record.getUriParameterNumber() - 1 : record.getUriParameterNumber();
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


}
