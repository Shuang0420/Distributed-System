/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sxu1;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Shuang
 */
@WebService(serviceName = "Task2Server")
public class Task2Server {

    final static BigInteger e1 = new BigInteger("65537");
    final static BigInteger n1 = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
    final static BigInteger e2 = new BigInteger("65537");
    final static BigInteger n2 = new BigInteger("3377327302978002291107433340277921174658072226617639935915850494211665206881371542569295544217959391533224838918040006450951267452102275224765075567534720584260948941230043473303755275736138134129921285428767162606432396231528764021925639519");

    static TreeMap<Long, String> temperatureMap = new TreeMap();
    static String curTemperatureSensor1 = null;
    static String curTemperatureSensor2 = null;

    /**
     * Compute a SHA-1 digest of the input
     *
     * @param input
     * @return SHA-1 result for input
     */
    public static String computeHash(String input) {
        byte[] hashed_byte = null;
        byte[] res = null;
        try {
            java.security.MessageDigest alg = java.security.MessageDigest.getInstance("SHA-1");
            alg.update(input.getBytes());
            hashed_byte = alg.digest();
            // set a new byte array and set its 0'th byte zero, then copy bytes from hashed_str to the new array
            res = new byte[hashed_byte.length + 1];
            res[0] = 0;
            for (int i = 1; i < res.length; i++) {
                res[i] = hashed_byte[i - 1];
            }
            // res = javax.xml.bind.DatatypeConverter.printHexBinary(hashed_str);
//            System.out.println("res " + javax.xml.bind.DatatypeConverter.printHexBinary(res));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Exception: " + ex);
        } finally {
            return javax.xml.bind.DatatypeConverter.printHexBinary(res);
        }
    }

    /**
     * Parse xml string to document
     *
     * @param xmlString
     * @return document object of the string
     */
    public static Document parseXML(String xmlString) {

        DocumentBuilder builder;
        Document sensorMessage = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            sensorMessage = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (SAXException ex) {
            Logger.getLogger(Task2Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Task2Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Task2Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sensorMessage;
    }

    /**
     * Check if the signature is valid
     *
     * @param sensorID
     * @param mes
     * @param signature
     * @return true if it's valid else false
     */
    public static boolean checkValid(String sensorID, String mes, String signature) {
        // decrypt the encrypted hash to compute a decrypted hash
        BigInteger m = new BigInteger(signature);

        String decrypted_mes = "";

        if (sensorID.equals("1")) {
            BigInteger clear = m.modPow(e1, n1);
            decrypted_mes = new String(clear.toByteArray());
            //decrypted_mes = clear.toString();
        } else {
            BigInteger clear = m.modPow(e2, n2);
            decrypted_mes = new String(clear.toByteArray());
        }

        // hash the message using SHA-1
        String hashed_mes = computeHash(mes);
        // compare 
        if (decrypted_mes.equals(hashed_mes)) {
            return true;
        }
        return false;
    }

    /**
     * Store temperature into treemap and update current temperature for
     * specific sensor
     *
     * @param sensorID
     * @param timeStamp
     * @param temperature
     */
    public static void storeTemperature(String sensorID, String timeStamp, String temperature) {
        long ts = Long.parseLong(timeStamp);
        temperatureMap.putIfAbsent(ts, temperature);
        if (sensorID.equals("1")) {
            curTemperatureSensor1 = temperature;
        } else {
            curTemperatureSensor2 = temperature;
        }
    }

    /**
     * Report high temperature
     *
     * @param sensorID
     * @param timeStamp
     * @param type
     * @param temperature
     * @param signature
     * @return report
     */
    @WebMethod(operationName = "highTemperature")
    public String highTemperature(@WebParam(name = "xmlString") String xmlString, @WebParam(name = "signature") String signature) {
        // parse xml
        Document doc = parseXML(xmlString);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList list = root.getChildNodes();
        String sensorID = null;
        String timeStamp = null;
        String type = null;
        String temperature = null;
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);

            System.out.println(n.getNodeName() + n.getTextContent());
            switch (n.getNodeName()) {
                case "sensorID":
                    System.out.println("true");
                    sensorID = n.getTextContent();
                case "timeStamp":
                    timeStamp = n.getTextContent();
                case "type":
                    type = n.getTextContent();
                case "temperature":
                    temperature = n.getTextContent();
            }
        }

        String mes = sensorID + timeStamp + type + temperature;

        if (!checkValid(sensorID, mes, signature)) {
            return "Invalid signature method!";
        }

        storeTemperature(sensorID, timeStamp, temperature);
        double temp = Double.parseDouble(temperature);
        if (type.equals("Celsius") && temp > 40) {
            return "Warning: High Temperature!";
        }
        if (type.equals("Fahrenheit") && temp > 40 * 1.8 + 32) {
            return "Warning: High Temperature!";
        }
        return "Success";
    }

    /**
     * Report low temperature
     *
     * @param sensorID
     * @param timeStamp
     * @param type
     * @param temperature
     * @param signature
     * @return report
     */
    @WebMethod(operationName = "lowTemperature")
    public String lowTemperature(@WebParam(name = "xmlString") String xmlString, @WebParam(name = "signature") String signature) {
        Document doc = parseXML(xmlString);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList list = root.getChildNodes();
        String sensorID = null;
        String timeStamp = null;
        String type = null;
        String temperature = null;
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeName()) {
                case "sensorID":
                    sensorID = n.getNodeValue();
                case "timeStamp":
                    timeStamp = n.getNodeValue();
                case "type":
                    type = n.getNodeValue();
                case "temperature":
                    temperature = n.getNodeValue();
            }
        }
        String mes = sensorID + timeStamp + type + temperature;
        if (!checkValid(sensorID, mes, signature)) {
            return "Invalid signature method!";
        }
        storeTemperature(sensorID, timeStamp, temperature);
        double temp = Double.parseDouble(temperature);
        if (type.equals("Celsius") && temp < 10) {
            return "Warning: Low Temperature!";
        }
        if (type.equals("Fahrenheit") && temp < 10 * 1.8 + 32) {
            return "Warning: Low Temperature!";
        }
        return "Success";
    }

    /**
     * Get all temperatures
     *
     * @return String representation of all temperatures
     */
    @WebMethod(operationName = "getTemperatures")
    public String getTemperatures() {
        StringBuffer res = new StringBuffer();
        for (String val : temperatureMap.values()) {
            res.append(val);
            res.append(" ");
        }

        return res.toString();
    }

    /**
     * Get last temperature of a particular sensor (sensor 1 or sensor 2)
     *
     * @param sensorID
     * @return last temperature
     */
    public String getLastTemperature(
            @WebParam(name = "sensorID") String sensorID) {
        if (sensorID.equals("1")) {
            return curTemperatureSensor1;
        } else {
            return curTemperatureSensor2;
        }
    }
}
