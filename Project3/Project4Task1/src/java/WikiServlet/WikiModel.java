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

//    public static void main(String args[]) {
//        WikiModel m=new WikiModel();
//        m.connect("engines");
//    }
    /**
     * Communicate with server using URL
     *
     * @param urlString
     */
    public String connect(String query) {
        String urlString = "https://en.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=json&srsearch=" + query;
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // SET HEADER
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "text/xml");
//            System.out.println(urlString + con.getResponseCode());
            if (con.getResponseCode() == 200) {// check response code
                // get response from server
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
//                    System.out.println(inputLine);
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
        return parseData(query, res);
    }


    public String parseData(String query, String str) {
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
                xml.append(doc.get("snippet").toString().replaceAll("[(]?<span[^<]*</span>[)]?", "").replaceAll("&quot;","").replaceAll("&", ""));
                xml.append("</snippet></doc>");
            }
            xml.append("</results></query>");

        } catch (ParseException ex) {
            Logger.getLogger(WikiModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xml.toString();
    }

}
