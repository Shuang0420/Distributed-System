/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task2client;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sxu1.Task2Server;

/**
 *
 * @author Shuang
 */
public class Project3Task2Client {

    final static BigInteger d1 = new BigInteger("339177647280468990599683753475404338964037287357290649639740920420195763493261892674937712727426153831055473238029100340967145378283022484846784794546119352371446685199413453480215164979267671668216248690393620864946715883011485526549108913");
    final static BigInteger n1 = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
    final static BigInteger d2 = new BigInteger("3056791181023637973993616177812006199813736824485077865613630525735894915491742310306893873634385114173311225263612601468357849028784296549037885481727436873247487416385339479139844441975358720061511138956514526329810536684170025186041253009");
    final static BigInteger n2 = new BigInteger("3377327302978002291107433340277921174658072226617639935915850494211665206881371542569295544217959391533224838918040006450951267452102275224765075567534720584260948941230043473303755275736138134129921285428767162606432396231528764021925639519");

    /**
     * Compute signature for the message
     *
     * @param sensorID
     * @param mes
     * @return string of signature
     */
    public static String getSignature(String sensorID, String mes) {
//        System.out.print("message: " + mes);
        String hashed_str = computeHash(mes);

        // create a biginteger from the byte array
        BigInteger m = new BigInteger(hashed_str.getBytes());
        // encrypt biginteger with RSA e and n
        byte[] key_mes = null;
        BigInteger c = null;
        if (sensorID.equals("1")) {
            c = m.modPow(d1, n1);
        } else {
            c = m.modPow(d2, n2);
        }

        //String signature = c.toString();
        return c.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String signature = null;
        String[] mes = null;
        String xmlString = null;
        while (true) {
            // call the highTemperature method for sensor 1 
            mes = simulate().split("\t");
            xmlString = "<data><sensorID>1</sensorID>" + mes[1];
            signature = getSignature("1", "1" + mes[0]);
            System.out.println(highTemperature(xmlString, signature));

            // call on the lowTemperature method for sensor 2
            mes = simulate().split("\t");
            xmlString = "<data><sensorID>2</sensorID>" + mes[1];
            signature = getSignature("2", "2" + mes[0]);
            System.out.println(highTemperature(xmlString, signature));

            // call the highTemperature method for sensor 1
            mes = simulate().split("\t");
            xmlString = "<data><sensorID>1</sensorID>" + mes[1];
            signature = getSignature("1", "1" + mes[0]);

            System.out.println(highTemperature(xmlString, signature));

            // call the highTemperature method for sensor 1, but with an invalid signature.
            mes = simulate().split("\t");
            xmlString = "<data><sensorID>1</sensorID>" + mes[1];
            signature = getSignature("1", "1" + mes[0]);
            System.out.println(highTemperature(xmlString, signature));

            // get all temperatures 
            System.out.println(getTemperatures());

            // request the last temperature recorded for sensor 1
            System.out.println(getLastTemperature("1"));
        }

    }

    /**
     * Simulate timestamp, type, and temperature
     *
     * @return xml for timestamp, type, and temperature
     */
    public static String simulate() {
        String timeStamp = null;
        String temperature = null;
        String type = null;
        // get timestamp
        Date date = new Date();
        timeStamp = String.valueOf(date.getTime());
        if (Math.random() < 0.5) {
            type = "Celsius";
            temperature = String.valueOf(Math.random() * 100);
        } else {
            type = "Fahrenheit";
            temperature = String.valueOf(Math.random() * 100 * 1.8 + 32);
        }
        String mes = timeStamp + type + temperature;
        String xmlString = "<timeStamp>" + timeStamp + "</timeStamp><type>" + type + "</type><temperature>" + temperature + "</temperature></data>";
        return mes + "\t" + xmlString;
    }

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

    private static String getTemperatures() {
        sxu1.Task2Server_Service service = new sxu1.Task2Server_Service();
        sxu1.Task2Server port = service.getTask2ServerPort();
        return port.getTemperatures();
    }

    private static String getLastTemperature(java.lang.String sensorID) {
        sxu1.Task2Server_Service service = new sxu1.Task2Server_Service();
        sxu1.Task2Server port = service.getTask2ServerPort();
        return port.getLastTemperature(sensorID);
    }

    private static String highTemperature(java.lang.String xmlString, java.lang.String signature) {
        sxu1.Task2Server_Service service = new sxu1.Task2Server_Service();
        sxu1.Task2Server port = service.getTask2ServerPort();
        return port.highTemperature(xmlString, signature);
    }

    private static String lowTemperature(java.lang.String xmlString, java.lang.String signature) {
        sxu1.Task2Server_Service service = new sxu1.Task2Server_Service();
        sxu1.Task2Server port = service.getTask2ServerPort();
        return port.lowTemperature(xmlString, signature);
    }

}
