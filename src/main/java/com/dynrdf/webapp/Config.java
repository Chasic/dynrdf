package com.dynrdf.webapp;


import com.dynrdf.webapp.util.Log;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;

public class Config {

    public static String DBLocation;
    public static String DBName;
    public static String ObjectTemplatePath;

    public static void init(){

        Log.info( "Initializing config ..." );
        Properties p = new Properties();
        try {
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            Log.fatal("Cannot open config file!");
            throw new ExceptionInInitializerError("Cannot open config file.");
        }

        /*DBLocation = p.getProperty("DBLocation");
        DBName = p.getProperty("DBName");
        ObjectTemplatePath = p.getProperty("ObjectTemplatePath");*/

        Log.info("Config initialized!");
    }


}
