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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shuang
 */
@WebServlet(name = "ComputeHashes", urlPatterns = {"/ComputeHashes"})
public class ComputeHashes extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // get parameters
        String input = request.getParameter("input");
        String hash_type = request.getParameter("method");
        response.setContentType("text/html");
        
        byte[] hashed_str = null;
        try { 
            // get hash
            java.security.MessageDigest alg=java.security.MessageDigest.getInstance(hash_type);
            alg.update(input.getBytes());
            hashed_str=alg.digest();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ComputeHashes.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (PrintWriter out = response.getWriter()) {
            // print results
            out.println("<h1>Hash Result Page </h1>");
            out.println("<h3><strong> The input is: </strong>"+input+"</h3>");
            out.println("<h3><strong> Hash function is: </strong>"+hash_type+"</h3>");
            out.println("<h3><strong> Base64: </strong>"+javax.xml.bind.DatatypeConverter.printBase64Binary(hashed_str)+"</h3>");
            out.println("<h3><strong> Hex: </strong>"+javax.xml.bind.DatatypeConverter.printHexBinary(hashed_str)+"</h3>");
            
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


}
