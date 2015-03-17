package com.greatdreams.downloader.cxf;

import java.io.File;
import java.io.IOException;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author greatdreams
 */
public class DownloaderRESTStartUp {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        // load the configuration file
        File configFile = new File("config/config.properties");
        try {
            ApplicationProperties.load(configFile);
        }catch(IOException e) {
            logger.warn("fail to load the applcation configuration file " + configFile.getAbsolutePath());
        }
        
        //android app storage directory will be created if not exits
        ProcessBuilder createDirProcessBuilder = new ProcessBuilder("mkdir", ApplicationProperties.TEMPPATH);
        Process p = null;
        int returnValue = 0;
        try {
            p = createDirProcessBuilder.start();
            int result = p.waitFor(); // 0 is sucess, other is failure
        } catch (InterruptedException | IOException ex) {
            logger.warn(ex.getMessage());
        }
        
        if(returnValue == 0 ) {
            logger.info("successful to create " + "'" + ApplicationProperties.TEMPPATH + "'" + " directory");
        }else {
            logger.warn("'" + ApplicationProperties.TEMPPATH + "'" + "directory exists");
        }        
        
        // to help easily create Server endpoints for JAX-RS
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(DownloaderREST.class);
        sf.setResourceProvider(DownloaderREST.class, new SingletonResourceProvider(new DownloaderREST()));  
        sf.setAddress("http://" + ApplicationProperties.HOSTIP + ":" + ApplicationProperties.HOSTPORT + "/");
        try {
            Server server = sf.create();
            server.start();
        }catch(Exception ex) {
            logger.error(ex.getMessage());
        }
    }    
}
 