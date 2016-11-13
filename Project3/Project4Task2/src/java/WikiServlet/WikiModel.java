/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WikiServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Shuang
 */
public class WikiModel {
    
    /**
     * Communicate with server using URL and returns json response.
     * @param para
     * @return Returns json string.
     */
    public String connect(String para) {
        String res = "";
        String urlString = "https://en.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=json&srsearch=" + para;//para;
        System.out.println("urlString " + urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // SET HEADER
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "text/xml");
            if (con.getResponseCode() == 200) {// check response code
                // get response from server
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    res += inputLine;
                }
                in.close();
            } else {
                System.out.println("Bad Request!");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(WikiModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikiModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * Parses JSON string to XML
     * @param query
     * @param str
     * @return Returns XML string.
     */
    public String parse2XML(String query, String str) {
        if (str == null) {
            return null;
        }
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><query><searchinfo>" + query + "</searchinfo><results>");
        try {

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(str);
            JSONObject jsObject = (JSONObject) obj;

            JSONArray queryArray = (JSONArray) ((JSONObject) jsObject.get("query")).get("search");
            Iterator iter = queryArray.iterator();
            // get text of title
            while (iter.hasNext()) {
                JSONObject doc = (JSONObject) iter.next();
                xml.append("<doc><title>");
                xml.append(doc.get("title").toString().replaceAll("&", ""));
                xml.append("</title>");
                xml.append("<snippet>");
                xml.append(doc.get("snippet").toString().replaceAll("[(]?<span[^<]*</span>[)]?", "").replaceAll("&quot;", "").replaceAll("&", ""));
                xml.append("</snippet></doc>");
            }
            xml.append("</results></query>");

        } catch (ParseException ex) {
            Logger.getLogger(WikiModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xml.toString();
    }

    /**
     * Returns JSON format result.
     * @param query
     * @param str
     * @return Returns JSON string result
     */
    public String parse2JSON(String query, String str) {
        if (str == null) {
            return null;
        }
        StringBuilder json = new StringBuilder("{\"entry\":");
        try {

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(str);
            JSONObject jsObject = (JSONObject) obj;
            Long totalHits = (Long) ((JSONObject)((JSONObject)jsObject.get("query")).get("searchinfo")).get("totalhits");
            json.append("{\"totoalHits\":"+totalHits+"},\"results\":[");
            JSONArray queryArray = (JSONArray) ((JSONObject) jsObject.get("query")).get("search");
            Iterator iter = queryArray.iterator();
            boolean isFirst = true;
            while (iter.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    json.append(",");
                }
                JSONObject doc = (JSONObject) iter.next();
                json.append("{\"title\":\"");
                json.append(doc.get("title").toString().replaceAll("&", ""));
                json.append("\",\"snippet\":\"");
                json.append(doc.get("snippet").toString().replaceAll("[(]?<span[^<]*</span>[)]?", "").replaceAll("&quot;", "").replaceAll("&", ""));
                json.append("\"}");
            }
            json.append("]}}");

        } catch (ParseException ex) {
            Logger.getLogger(WikiModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json.toString();
    }

}
