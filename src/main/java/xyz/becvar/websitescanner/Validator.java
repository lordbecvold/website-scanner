package xyz.becvar.websitescanner;

public class Validator {

    //Function for check if url is valid adress
    public boolean isURL(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) { }
        return false;
    }

    //Function for strip url
    public String urlStrip(String url) {
        //Remove protocol form url
        url = url.replace("https://", "").replace("http://", "");

        //Remove last char /
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
