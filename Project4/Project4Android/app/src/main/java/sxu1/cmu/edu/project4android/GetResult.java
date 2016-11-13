package sxu1.cmu.edu.project4android;

import android.os.AsyncTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.StringReader;
/**
 * Created by Shuang on 16/11/9.
 * This class provides capabilities to search for an document from Heroku Web Service given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView docReady method to do the update.
 */

public class GetResult {

    Wiki ip = null;

    /*
     * search is the public GetResult method.  Its arguments are the search term, and the Wiki object that called it.  This provides a callback
     * path such that the docReady method in that object is called when the doc is available from the search.
     */
    public void search(String searchTerm, Wiki ip) {
        this.ip = ip;
        new AsyncTextSearch().execute(searchTerm);
    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncTextSearch extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return search(urls[0]);
        }

        protected void onPostExecute(String doc) {
            System.out.println("doc " + doc);
            ip.docReady(doc);
        }

        /*
         * Search Heroku Web Service for the searchTerm argument, and return a String that can be put in an TextView
         */
        private String search(String searchTerm) {
            String res = null;
            Document doc = parseXML(connect(searchTerm));

            NodeList nl = doc.getElementsByTagName("doc");

            if (nl.getLength() == 0) {
                return null; // no docs found
            } else {
                int d = new Random().nextInt(nl.getLength()); //choose a random doc
                Element e = (Element) nl.item(d);
                // get title
                Node n1 = e.getFirstChild();
                if (n1 != null && n1.getNodeName().equals("title")) {
                    res = "Title: " + n1.getTextContent();
                }
                // get snippet
                Node n2 = e.getLastChild();
                if (n2 != null && n2.getNodeName().equals("snippet")) {
                    res += "\n" + "Snippet: " + n2.getTextContent();
                }
            }
            return res;
        }


        /**
         * Parse xml string to document
         *
         * @param xmlString
         * @return document object of the string
         */
        public Document parseXML(String xmlString) {

            DocumentBuilder builder;
            Document sensorMessage = null;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                sensorMessage = builder.parse(new InputSource(new StringReader(xmlString)));
            } catch (SAXException ex) {
                Logger.getLogger(GetResult.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GetResult.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(GetResult.class.getName()).log(Level.SEVERE, null, ex);
            }
            return sensorMessage;
        }

        /**
         * Communicate with server using URL
         *
         * @param query
         */
        public String connect(String query) {
            // This is task 1 url.
            String urlString = "https://warm-island-14833.herokuapp.com/WikiServlet?search="+query;
            // This is task 2 url.
//            String urlString = "https://serene-cove-59508.herokuapp.com/WikiServlet?search="+query;
            String res = "";
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // SET HEADER
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "text/xml");
                System.out.println(con.getResponseCode());
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
                Logger.getLogger(GetResult.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GetResult.class.getName()).log(Level.SEVERE, null, ex);
            }
            return res;
        }



    }
}
