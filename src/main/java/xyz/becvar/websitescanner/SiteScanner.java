package xyz.becvar.websitescanner;

import xyz.becvar.websitescanner.utils.FileUtils;
import xyz.becvar.websitescanner.utils.SystemUtil;
import xyz.becvar.websitescanner.utils.TimeUtils;
import xyz.becvar.websitescanner.utils.console.ConsoleColors;
import xyz.becvar.websitescanner.utils.console.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SiteScanner {

    //Init main used objects
    public Getter getter = new Getter();
    public Validator validator = new Validator();
    public FileUtils fileUtils = new FileUtils();

    public void scan(String url) {
        //Delete old log if exist
        fileUtils.deleteFile("scanned_logs/" + validator.urlStrip(url) + ".log");

        //Create new log file and write title
        fileUtils.saveMessageLog("Information log: " + url + " : " + TimeUtils.getTime() + "\n", "scanned_logs/" + validator.urlStrip(url) + ".log");

        //Print real ip and save to log file
        realIPScan(url);

        //File system scan
        fileSystemScan(url);
    }


    //Get real site ip by url
    public void realIPScan(String url) {
        //Get real site ip
        String realIP = getter.getIP(url);

        //Print real ip to console
        Logger.log("Real ip of " + url + " is: " + realIP);

        //Save real ip to log file
        fileUtils.saveMessageLog("REAL IP: " + realIP, "scanned_logs/" + validator.urlStrip(url) + ".log");
    }


    //Scan page file system
    public void fileSystemScan(String url) {

        int file = 1;
        int foundFiles = 1;

        url = validator.removeLastSlash(url);

        //Save to log file
        fileUtils.saveMessageLog("\n\n\nFiles and directoryes found on " + url + "\n", "scanned_logs/" + validator.urlStrip(url) + ".log");

        if (fileUtils.ifFileExist("word.list")) {
            try (BufferedReader br = new BufferedReader(new FileReader("word.list"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        HttpURLConnection.setFollowRedirects(false);
                        HttpURLConnection con = (HttpURLConnection) new URL(url + "/" + line).openConnection();

                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

                        con.setRequestMethod("HEAD");

                        con.connect();

                        //Print path and code to console

                        //Succesful
                        if (con.getResponseCode() == 200 || con.getResponseCode() == 202 || con.getResponseCode() == 201) {
                            Logger.log(ConsoleColors.CODES.ANSI_GREEN + file + ":" + url + "/" + line + " code: " + new String(String.valueOf(con.getResponseCode())));

                        //Redirection
                        } else if (con.getResponseCode() == 301 || con.getResponseCode() == 302 || con.getResponseCode() == 303 || con.getResponseCode() == 304 || con.getResponseCode() == 305 || con.getResponseCode() == 308 || con.getResponseCode() == 307) {
                            Logger.log(ConsoleColors.CODES.ANSI_YELLOW + file + ":" + url + "/" + line + " code: " + new String(String.valueOf(con.getResponseCode())));

                        //Client Error
                        } else if (con.getResponseCode() == 400 || con.getResponseCode() == 401 || con.getResponseCode() == 402 || con.getResponseCode() == 403 || con.getResponseCode() == 404 || con.getResponseCode() == 406 || con.getResponseCode() == 409 || con.getResponseCode() == 411 || con.getResponseCode() == 414 || con.getResponseCode() == 423|| con.getResponseCode() == 426 || con.getResponseCode() == 429 || con.getResponseCode() == 451) {
                            Logger.log(ConsoleColors.CODES.ANSI_RED + file + ":" + url + "/" + line + " code: " + new String(String.valueOf(con.getResponseCode())));

                        //Server Error
                        } else if (con.getResponseCode() == 500 || con.getResponseCode() == 501 || con.getResponseCode() == 502 || con.getResponseCode() == 503 || con.getResponseCode() == 504 || con.getResponseCode() == 505 || con.getResponseCode() == 506 || con.getResponseCode() == 507 || con.getResponseCode() == 508 || con.getResponseCode() == 509 || con.getResponseCode() == 510|| con.getResponseCode() == 511) {
                            Logger.log(ConsoleColors.CODES.ANSI_PURPLE + file + ":" + url + "/" + line + " code: " + new String(String.valueOf(con.getResponseCode())));


                        } else {

                            //All others codes
                            Logger.log(file + ":" + url + "/" + line + " code: " + new String(String.valueOf(con.getResponseCode())));
                        }
                        //End of path print




                        file++;


                        //Save to log file if response code not 404, 403, 400
                        if (con.getResponseCode() != 404 && con.getResponseCode() != 400 && con.getResponseCode() != 403 && con.getResponseCode() != 301 && con.getResponseCode() != 302 && con.getResponseCode() != 308 && con.getResponseCode() != 301 && con.getResponseCode() != 429) {
                            foundFiles++;
                            fileUtils.saveMessageLog(url + "/" + line + " - " + new String(String.valueOf(con.getResponseCode())), "scanned_logs/" + validator.urlStrip(url) + ".log");
                        }
                    }
                    catch (Exception e) {
                        Logger.log(e.getMessage());
                    }
                }
            } catch (IOException e) {
                Logger.log(e.getMessage());
            }
            Logger.log("Scanner: exited with " + foundFiles + " found files.");
            Logger.log("Scanner: all scanned data saved to scanned_logs/" + "scanned_logs/" + validator.urlStrip(url) + ".log");
        } else {
            SystemUtil.kill("error word.list not found, please check your file or try reinstall this app");
        }
    }
}
