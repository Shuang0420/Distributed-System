/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task3client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shuang
 */
public class Communication {

    /**
     * Communicate with server using URL
     *
     * @param urlString
     */
    public static void connect(String urlString) {
        String res = null;
        try {
//            System.out.println(urlString);
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
                    System.out.println(inputLine);
                }
                in.close();
            } else {
                System.out.println("Bad Request!");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Project3Task3Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Project3Task3Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
