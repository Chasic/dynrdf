package com.dynrdf.webapp.util;

import com.dynrdf.webapp.exceptions.InitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Base logging class
 * Using log4j Logger
 */
public class Log {

    static Logger logger;


    public static void init() throws InitException{

        logger = Logger.getLogger(Log.class);
        Properties p = new Properties();

        try {
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("log4j.properties"));
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            throw new InitException("Cannot open log4j config file.");
        }

    }

    public static void debug( String msg ){
        if(logger.isDebugEnabled()){
            logger.debug(msg);
        }
    }

    public static void error( String msg ){
        logger.error(msg);
    }

    public static void warning( String msg ){
        logger.warn(msg);
    }

    public static void fatal( String msg ){
        logger.fatal(msg);
    }

    public static void info( String msg ){
        logger.info(msg);
    }

}
