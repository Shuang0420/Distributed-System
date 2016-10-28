/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sxu1;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shuang
 */
@WebServlet(name = "NotificationService", urlPatterns = {"/getTemperatures", "/highTemperature", "/lowTemperature", "/getLastTemperature"})
public class NotificationService extends HttpServlet {

    Model model = null;

    @Override
    public void init() {
        model = new Model();
    }

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NotificationService</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NotificationService at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
//        processRequest(request, response);

        if (request.getHeader("Accept") == null) {
            response.setStatus(404);
        }

        String result = null;
        String sensorID = null;
        String timeStamp = null;
        String type = null;
        String temperature = null;
        String signature = null;
        OutputStreamWriter out = new OutputStreamWriter(
                response.getOutputStream());
        // check the url path
        String path = request.getServletPath();
        // get parameters if it's a warning query
        if (path.equals("/highTemperature") || path.equals("/lowTemperature")) {
            sensorID = request.getParameter("sensorID");
            timeStamp = request.getParameter("timeStamp");
            type = request.getParameter("type");
            temperature = request.getParameter("temperature");
            signature = request.getParameter("signature");
        }
        switch (path) {
            case "/getTemperatures":
                response.setStatus(200);
                String temperatures = model.getTemperatures();
//                System.out.println(temperatures);
                out.write(temperatures);
                out.flush();
                break;
            case "/highTemperature":
                response.setStatus(200);
                System.out.println(sensorID + " " + timeStamp + " " + type + " " + temperature + " " + signature);
                String highWarning = model.highTemperature(sensorID, timeStamp, type, temperature, signature);
//                System.out.println(highWarning);
                out.write(highWarning);
                out.flush();
                break;

            case "/lowTemperature":
                response.setStatus(200);
                String lowWarning = model.lowTemperature(sensorID, timeStamp, type, temperature, signature);
//                System.out.println(lowWarning);
                out.write(lowWarning);
                out.flush();
                break;

            case "/getLastTemperature":
                response.setStatus(200);
                sensorID = request.getParameter("sensorID");
                String lastTemperature = model.getLastTemperature(sensorID);
//                System.out.println(lastTemperature);
                out.write(lastTemperature);
                out.flush();
                break;

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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
