package com.greatdreams.downloader.cxf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author greatdreams
 */
public class DownloaderUtil {

    private static final Logger logger = LogManager.getLogger();

    public static String download(String url, String cookie) {
        logger.info("calling" + DownloaderUtil.class.getName() + ".download(appurl = " + url + ", cookie = " + cookie + ")");

        int status = 1; // app download status flag
        String statusDescription = "-- app resource is unavailable --"; // description for downloading status
        String storagePath = ApplicationProperties.TEMPPATH; // the directory for storage of downloaded android apps

        //  UUID uuid = UUID.randomUUID();
        // String fileName = uuid.toString(); //downloaded app name which is a java UUID value.
        //  String fileName = String.valueOf(url.hashCode()); // use the hashcode of url as the filename
        String fileName = StringToSHA1.toSHA1(url); // use the hashcode of url as the filename

        String headers[] = {
            "User-Agent:Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.4.0",
            "Connection:keep-alive",
            "Cache-Control:max-age=0",
            "Accept-Language:en-US,en;q=0.5",
            "Accept-Encoding:gzip, deflate",
            "Accept:ext/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        };

        List<String> cmd = new ArrayList<>();
        cmd.add("curl");
        for (String header : headers) {
            cmd.add("-H");
            cmd.add(header);
        }
        cmd.add("--cookie");
        cmd.add(cookie);
        cmd.add("-C"); // the download will continue the last task if the last download task is interrupted 
        cmd.add("-");
        cmd.add("--location");
        cmd.add("-o");
        cmd.add(fileName);
        cmd.add(url);

        Process process;
        BufferedReader br;
        int returnValue = 1;

        try {
            //test whether the appurl is available
            process = new ProcessBuilder("curl", "-I", "--location", "-H", "User-Agent:Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.4.0", url).start();
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
                    logger.info("HTTP request final status code is " + statusCode);
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            logger.warn(ex.getMessage());
        }

        if (status == 1) {
            fileName = null;
            storagePath = null;
        }
        String result;/*
         result = "{"
         + "\"appurl\" : \" " + appurl + "\", "
         + "\"status\" : " + status + ",  "
         + "\"status_description\" : \"" + statusDescription + "\","
         + "\"file_name\" : \"" + fileName + "\","
         + "\"storage_path\" : \"" + storagePath + "/" + fileName + "\""
         + "}";
         */

        /*
         result = "{"
         + "\"agentId\" : \"0\", "
         + "\"status\" : " + status + ",  "
         + "\"tmpLocation\" : \"" + storagePath + "/" + fileName + "\""
         + "}";
         */
        //  result = "agentId=0&status=" + (status == 0 ? 4 : 0) + "&tmpLocation=" + storagePath + "/" + fileName;
        result
                = "{"
                + "\"agentId\" : \"f8f1e396c47f153b0b93\", "
                + "\"status\" : \"" + (status == 0 ? 4 : 0) + "\", "
                + "\"tmpLocation\" : \"" + storagePath + "/" + fileName + "\""
                + "}";
        logger.info("download return " + result);
        return result;
    }
}
