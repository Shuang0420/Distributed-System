package project2task1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shuang
 */
public class PasswordHash {

    public static String computeHash(String input) {
        byte[] hashed_str = null;
        String res = null;
        try {
            // get hash
            java.security.MessageDigest alg = java.security.MessageDigest.getInstance("SHA-1");
            alg.update(input.getBytes());
            hashed_str = alg.digest();
            res = javax.xml.bind.DatatypeConverter.printHexBinary(hashed_str);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Exception: " + ex);
        } finally {
            return res;
        }
    }
    /**
    public static void main(String args[]) {
        System.out.println(computeHash("seanlele"));
    }*/

}


