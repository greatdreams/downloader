package com.greatdreams.downloader.cxf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author greatdreams
 */

public class ApplicationProperties {
    private static final Logger logger = LogManager.getLogger();
    
    public static String HOSTIP = "127.0.0.1";
    public static String HOSTPORT = "4500";
    public static String TEMPPATH = "/tmp/android_apps/";
    public static String FEEDBACKURL = "http://127.0.0.1/taskmanager/subtask/4";
    
    public static void  load(File file) throws IOException {        
        logger.debug("load the configuration file " + file.getAbsolutePath());
        Properties pros = new Properties();
        pros.load(new FileInputStream(file));
        Enumeration<Object> keys = pros.keys();
        logger.debug("configuration properties is the following ...");
        while(keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            logger.debug(key + " : " + pros.getProperty(key));
        }
        
        if(pros.getProperty("host_ip") != null) {
            HOSTIP = pros.getProperty("host_ip");
        }
        
        HOSTIP = pros.getProperty("host_ip") != null ? pros.getProperty("host_ip") : HOSTIP;
        HOSTPORT = pros.getProperty("host_port") != null ? pros.getProperty("host_port") : HOSTPORT;
        TEMPPATH = pros.getProperty("temp_path") != null ? pros.getProperty("temp_path") : TEMPPATH;
        FEEDBACKURL = pros.getProperty("feedback_url") != null ? pros.getProperty("feedback_url") : FEEDBACKURL;
        
    }    
}
