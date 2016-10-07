
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shuang
 */
public class Model {
    private String pictureTag; // The search string of the desired picture
    private String pictureURL; // The URL of the picture image
    private String pictureDescription;// The description of the picture url

    /**
     * Arguments.
     *
     * @param searchTag The tag of the photo to be searched for.
     */
    public void doSearch(String searchTag) 
            throws UnsupportedEncodingException  {
        pictureTag = searchTag;
        /*
         * URL encode the searchTag
         */
        searchTag = URLEncoder.encode(searchTag, "UTF-8");
 
        String response = "";

        // Create a URL for the page to be screen scraped
        String searchURL =
                "https://www.cia.gov/library/publications/the-world-factbook/geos/"
                + searchTag;
        
        
        System.out.println("searchURL-----"+searchURL);
        // Fetch the page
        response = fetch(searchURL);

        //System.out.println(response);

        /*
         * Search the page to find the picture URL
         *
         */
        
        System.out.println("searchTag "+searchTag);

        pictureURL="https://www.cia.gov/library/publications/the-world-factbook/graphics/flags/large/"+searchTag.split("\\.")[0]+"-lgflag.gif";
        
        // get picture tag
        int cutLeft=response.indexOf("\"region_name1 countryName \">");
        
        // if cannot get tag, return instrunction message
        if (cutLeft == -1) {
            pictureTag = "Unkown country. Please choose another country!";
            return;
        }        

        cutLeft+="\"region_name1 countryName \">".length();
       
        int cutRight=response.indexOf("</span>",cutLeft);
        
        
        pictureTag=response.substring(cutLeft,cutRight);
        
        pictureTag=pictureTag.charAt(0)+pictureTag.substring(1).toLowerCase();
        
        System.out.println("pictureTag: "+pictureTag);
        
        // get picture description
        cutLeft=response.indexOf("\"flag_description_text\">");
        
        // if cannot get description, return instrunction message
        if (cutLeft == -1) {
            pictureDescription = "<h2>Oops, there's an error. Please choose another country!</h2>";
            return;
        }
        
        cutLeft+="\"flag_description_text\">".length();
       
        cutRight=response.indexOf("</span>",cutLeft);

        pictureDescription="<h3>About this flag: </h3>"+response.substring(cutLeft, cutRight);
        System.out.println("pictureDes: "+pictureDescription);

        System.out.println("pictureURL= " + pictureURL);

    }
    
    


    /*
     * Return the picture tag.  
     * 
     * @return The tag that was used to search for the current picture.
     */
    public String getPictureTag() {
        return (pictureTag);
    }
    
    /**
     * Return the picture URL.  
     * @return The url that was used to search for the current picture.
     */
    public String getPictureURL() {
        return (pictureURL);
    }
    
    /**
     * Return the picture description.
     * @return The description that was used to search for the current picture.
     */
    public String getPictureDescription() {
        if (pictureDescription==null)
            pictureDescription = "<h2>Oops, there's an error. Please choose another country!</h2>";
        return (pictureDescription);
    }


    /*
     * Make an HTTP request to a given URL
     * 
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which 
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
            // Do something reasonable.  This is left for students to do.
        }
        return response;
    }
}
