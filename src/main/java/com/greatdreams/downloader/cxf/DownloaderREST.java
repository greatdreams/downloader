package com.greatdreams.downloader.cxf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
@Path("/subtask/4")
public class DownloaderREST {

    private final Logger logger = LogManager.getLogger(DownloaderREST.class.getName());

    // the method has been invalid and don't use it any more.
    @GET
    @Path("/invalid")
    @Produces(MediaType.TEXT_HTML)
    public String addPlainText() {
        return "<html>"
                + "<head>"
                + "<title>android app downloader</title>"
                + "</head>"
                + "<body>"
                + "<form action='/subtask/4' method='post'>"
                + "<label for='appurl'>android app url</label><input type='text' name='url' value='https://github.com/Jonovono/c/archive/master.zip'/><br/><br/>"
                + "<label for='cookie'>cookie</label><input type='text' name='cookie'/><br/><br/>"
                + "<input type='submit' value='download'/>"
                + "</form>"
                + "</body>"
                + "</html>";
    }

    // the method has been invalid and don't use it any more.
    @POST
    @Path("/invalid")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String startTaskTemp(@FormParam("url") final String appurl, @FormParam("cookie") final String cookie) {
        logger.debug("handle the downloader request (appurl : " + appurl + ", " + "cookie : " + cookie + ")");

        Runnable asynchDownloaderTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String taskResult = DownloaderUtil.download(appurl, cookie);

                    List<String> cmd = new ArrayList<>();
                    cmd.add("curl");
                    cmd.add("-d");
                    cmd.add("\"" + taskResult + "\"");
                    cmd.add(ApplicationProperties.FEEDBACKURL);

                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    Process ps = pb.start();

                    int returnValue = ps.waitFor();
                    if (returnValue == 0) {
                        logger.info("successful : feedback the message of downloading '" + "' to " + ApplicationProperties.FEEDBACKURL);
                    } else {
                        logger.warn("warn : feedback the message of downloading '" + "' to " + ApplicationProperties.FEEDBACKURL);
                    }
                } catch (IOException | InterruptedException ex) {
                    logger.warn(DownloaderREST.class.getName() + " : " + ex.getMessage());
                }
            }
        };

        String code = "received";
        String message = "the request for downloading have been received, the downloader is starting ....";

        try {
            Thread asynchDownloaderTaskThread = new Thread(asynchDownloaderTask);
            asynchDownloaderTaskThread.start();
        } catch (Exception ex) {
            logger.warn(DownloaderREST.class.getName() + ".startTask " + ex.getMessage());
            code = "failed";
            message = "due to internal error, the downloader can not handle your request for downloading, Please try again";
        }

        String result
                = "{"
                + "\"code\" : \"" + code + "\","
                + "\"msg\" : \"" + message + "\""
                + "}";
        return result;
    }

    /**
     * the following method request method : post data format (json) :
     * {"agentId" : "0", "url" : "http://www.example.com/", "cookie" : "the is a
     * cookie data string"} respone format : {"code":"received/failed", "msg":
     * "description for the code parameter"}
     *
     * @param requestJsonString
     * @return
     * @throws java.io.IOException
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String startTask(String requestJsonString) {
        String code = "received";
        String message = "the request for downloading have been received, the downloader is starting ....";
        String result
                = "{"
                + "\"code\" : \"" + code + "\","
                + "\"msg\" : \"" + message + "\""
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> parametersMapper;
        try {
            parametersMapper = mapper.readValue(requestJsonString, Map.class);
        } catch (IOException ex) {
            logger.error("the request json data format is invalid, please checkout and try again");
            code = "failed";
            message = "the request json data format is invalid, please checkout and try again";
            result =
                    "{"
                    + "\"code\" : \"" + code + "\","
                    + "\"msg\" : \"" + message + "\""
                    + "}";
            return result;
        }
        
        final String agentId = parametersMapper.get("agentId");
        final String appurl = parametersMapper.get("url");
        final String cookie = parametersMapper.get("cookie");

        logger.debug("handle the downloader request (appurl : " + appurl + ", " + "cookie : " + cookie + ")");

        Runnable asynchDownloaderTask = new Runnable() {
            @Override
            public void run() {
                try {
                    String taskResult = DownloaderUtil.download(appurl, cookie);

                    List<String> cmd = new ArrayList<>();
                    cmd.add("curl");
                    cmd.add("-d");
                    cmd.add("\"" + taskResult + "\"");
                    cmd.add(ApplicationProperties.FEEDBACKURL);

                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    Process ps = pb.start();

                    int returnValue = ps.waitFor();
                    if (returnValue == 0) {
                        logger.info("feedback the message of downloading '" + "' to " + ApplicationProperties.FEEDBACKURL);
                    } else {
                        logger.warn("feedback the message of downloading '" + "' to " + ApplicationProperties.FEEDBACKURL);
                    }
                } catch (IOException | InterruptedException ex) {
                    logger.warn(ex.getMessage());
                }
            }
        };
        
        // asynchronously start to the downloading task
        Thread asynchDownloaderTaskThread = new Thread(asynchDownloaderTask);
        asynchDownloaderTaskThread.start();

        return result;
    }
}
