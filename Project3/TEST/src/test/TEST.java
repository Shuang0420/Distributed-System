/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;

/**
 *
 * @author Shuang
 */
public class TEST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ArrayList newHand = new ArrayList();
        newHand.add("hello");
        newHand.add("c");
        for (Object c:newHand) {
            System.out.println((String)c);
        }
    }
    
}
