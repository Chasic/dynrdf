package com.dynrdf.webapp;

import com.dynrdf.webapp.logic.RDFObjectContainer;
import com.dynrdf.webapp.util.Log;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * Servlet context listener
 * Handles init/destroy events
 */
public class ServletContextHandler implements ServletContextListener{


    public void contextInitialized(ServletContextEvent contextEvent) {
        // 1) Init logger
        Log.init();

        Log.info( "################" );
        Log.info( "Initializing ..." );

        // 2) Load config
        Config.init();

        // 3) Init Object container
        RDFObjectContainer.init();


        Log.info("Initialized!");
        Log.info( "################" );
    }


    public void contextDestroyed(ServletContextEvent contextEvent) {
        // do nothing now
    }
}
