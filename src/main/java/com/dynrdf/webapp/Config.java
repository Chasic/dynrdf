package com.dynrdf.webapp;


import com.dynrdf.webapp.exceptions.InitException;
import com.dynrdf.webapp.util.Log;
import com.sun.org.apache.xml.internal.security.Init;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;

public class Config {

    public static String objectsPath;
    public static String ObjectRDFS;
    public static String ObjectBaseUrl;

    public static void init() throws InitException{

        Log.info( "Initializing config ..." );
        Properties p = new Properties();
        try {
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            Log.fatal("Cannot open config file!");
            throw new InitException("Cannot open config file.");
        }

        objectsPath = p.getProperty("ObjectsPath");

        if(objectsPath == null){
            Log.error("Missing parameter 'ObjectsPath' in config!");
            throw new InitException("Missing parameter 'ObjectsPath' in config!");
        }

        ObjectRDFS = p.getProperty("ObjectRDFS");

        if(ObjectRDFS == null){
            Log.error("Missing parameter 'ObjectRDFS' in config!");
            throw new InitException("Missing parameter 'ObjectRDFS' in config!");
        }

        ObjectBaseUrl = p.getProperty("ObjectBaseUrl");

        if(ObjectBaseUrl == null){
            Log.error("Missing parameter 'ObjectBaseUrl' in config!");
            throw new InitException("Missing parameter 'ObjectBaseUrl' in config!");
        }

        Log.info("Config initialized!");
    }
}
