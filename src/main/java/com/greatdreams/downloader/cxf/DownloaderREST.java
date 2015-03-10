package com.greatdreams.downloader.cxf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author greatdreams
 */
@Path("/downloader")
public class DownloaderREST {

    private final Logger logger = LogManager.getLogger(DownloaderREST.class.getName());

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String addPlainText() {
        return "<html>"
                + "<head>"
                + "<title>android app downloader</title>"
                + "</head>"
                + "<body>"
                + "<form action='/downloader/' method='post'>"
                + "<label for='appurl'>android app url</label><input type='text' name='appurl' value='https://github.com/Jonovono/c/archive/master.zip'/><br/><br/>"
                + "<label for='cookie'>cookie</label><input type='text' name='cookie'/><br/><br/>"
                + "<input type='submit' value='download'/>"
                + "</form>"
                + "</body>"
                + "</html>";
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String download(@FormParam("appurl") String appurl, @FormParam("cookie") String cookie) {
        logger.info("calling download(appurl = " + appurl + ", cookie = " + cookie + ")");
        int status = 1; // app download status flag
        String statusDescription = "-- app resource is unavailable --"; // description for downloading status
        String storagePath = "/tmp/android_apps"; // the directory for storage of downloaded android apps

        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString(); //downloaded app name which is a java UUID value.

        String headers[] = {
            "User-Agent:Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.4.0",
            "Connection:keep-alive",
            "Cache-Control:max-age=0",
            "Accept-Language:en-US,en;q=0.5",
            "Accept-Encoding:gzip, deflate",
            "Accept:ext/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        };

        String headersOption = "";
        for (String header : headers) {
            headersOption += "-H \"" + header + "\" ";
        }

        // System.out.println(headersOption);
        String cookieOption = " --cookie \"" + cookie + "\" ";

        List<String> cmd = new ArrayList<>();
        cmd.add("curl");
        for (String header : headers) {
            cmd.add("-H \"" + header + "\" ");
        }
        cmd.add("--cookie");
        cmd.add("\"" + cookie + "\"");
        cmd.add("--location");
        cmd.add("-o");
        cmd.add(fileName);
        cmd.add(appurl);

        Process process;
        BufferedReader br;
        int returnValue = 1;

        try {
            //test whether the appurl is available
            process = new ProcessBuilder("curl", "-I", "--location", appurl).start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            returnValue = process.waitFor();

            if (returnValue == 0) {
                while (br.ready()) {
                    String statusLine = br.readLine();
                    int statusCode = Integer.parseInt(statusLine.substring(9, 12));
                    
                    
                    if (statusCode == 200 || statusCode == 301 || statusCode == 302) {

                        process = new ProcessBuilder(cmd).directory(new File(storagePath)).start();
                        returnValue = process.waitFor();
                        if (returnValue == 0) {
                            status = 0;
                            statusDescription = "--app downloading sucess---";
                        }
                    } 
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (status == 1) {
            fileName = null;
            storagePath = null;
        }

        String result;
        result = "{"
                + "\"appurl\" : \" " + appurl + "\", "
                + "\"status\" : " + status + ",  "
                + "\"status_description\" : \"" + statusDescription + "\","
                + "\"file_name\" : \"" + fileName + "\","
                + "\"storage_path\" : \"" + storagePath + "/" + fileName + "\""
                + "}";
        logger.info("return " + result);
        return result;
    }
}
