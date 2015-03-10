
package com.greatdreams.downloader.cxf;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

/**
 *
 * @author greatdreams
 */
public class DownloaderRESTStartUp {
    public static void main(String[] args) {
        
        //android app storage directory will be created if not exits
        ProcessBuilder createDirProcessBuilder = new ProcessBuilder("mkdir", "/tmp/android_apps/");
        Process p = null;
        int returnValue = 0;
        try {
            p = createDirProcessBuilder.start();
            int result = p.waitFor(); // 0 is sucess, other is failure
        } catch (InterruptedException ex) {
            Logger.getLogger(DownloaderRESTStartUp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DownloaderRESTStartUp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(returnValue == 0 )
        {
         System.out.println("successful to create '/tmp/android_apps/' directory");
        }else {
            System.out.println("'/tmp/android_apps/' directory exists");
        }        
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(DownloaderREST.class);
        sf.setResourceProvider(DownloaderREST.class, new SingletonResourceProvider(new DownloaderREST()));    
        sf.setAddress("http://localhost:9999/");
        Server server = sf.create();
        server.start();
    }    
}
 