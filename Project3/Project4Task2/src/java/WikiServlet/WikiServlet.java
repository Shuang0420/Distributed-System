/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WikiServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(name = "WikiServlet", urlPatterns = {"/", "/WikiServlet", "/AnalyticsData"})
public class WikiServlet extends HttpServlet {

    WikiModel model = null;
    MongoConn mongoConn = null;

    @Override
    public void init() {
        model = new WikiModel();
        mongoConn = new MongoConn();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(String res, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/XML;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(res);
        }
    }

    
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
        if (request.getServletPath().equals("/WikiServlet")) {
            processRequest(request,response);
        } else if (request.getServletPath().equals("/AnalyticsData")) {
            String result = "Dashboard.jsp";
            request = getResults(request);
            response.setContentType("text/HTML;charset=UTF-8");
            RequestDispatcher result_page = request.getRequestDispatcher(result);
            result_page.forward(request, response);
        }
    }

    /**
     * Helper method for query request and data storage.
     * @param request
     * @param response
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            StringBuilder jsonStr = new StringBuilder("{\"log\":\"true\"");

            // Get user-agent
            String userAgent = request.getHeader("user-agent");
            StringBuilder append = jsonStr.append("\"user-agent\":\"").append(userAgent).append("\"");

            // Get user's search keywords
            String para = request.getParameter("search");
            jsonStr.append(",\"para\":\"").append(para).append("\"");

            // Get timestamp for when request is received
            Date date = new Date();
            Long startFrom = date.getTime();
            jsonStr.append(",\"startFrom\":\"").append(startFrom).append("\"");

            // Request from 3rd part API
            String urlString = "https://en.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=json&srsearch=" + para;//para;
            jsonStr.append(",\"urlString\":\"").append(urlString).append("\"");

            // Reply from 3rd part API
            String replyFromAPI = model.connect(para);
            jsonStr.append(",\"replyFromAPI\":").append(replyFromAPI);

            // Response
            String resXML = model.parse2XML(para, replyFromAPI);
            jsonStr.append(",\"resXML\":\"").append(resXML.replaceAll("\"", "'")).append("\"");

            // Write response
            processRequest(resXML, response);

            // Get timestamp for when response is returned
            date = new Date();
            Long endAt = date.getTime();
            jsonStr.append(",\"endAt\":\"").append(endAt).append("\"");

            jsonStr.append(",\"latency\":").append(endAt - startFrom);

            // Get response in json format for the sake of storage
            String resJSON = model.parse2JSON(para, replyFromAPI);
            jsonStr.append(",\"resJSON\":").append(resJSON);

            jsonStr.append("}}");
            mongoConn.storeData(jsonStr.toString());
        } catch (ServletException | IOException ex) {
            Logger.getLogger(WikiServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Helper method for analytical data.
     * @param request
     * @return Returns HttpServletRequest
     */
    private HttpServletRequest getResults(HttpServletRequest request) {
        request.setAttribute("numOfEntries", mongoConn.getNumOfEntries());
        request.setAttribute("avgLatency", mongoConn.getAvgLatency());
        request.setAttribute("topQuery", mongoConn.getPopular(true, "$para"));
        request.setAttribute("topUserAgent", mongoConn.getPopular(true, "$user-agent"));
        request.setAttribute("peakTime", mongoConn.getPeakTime(true));
        request.setAttribute("logData", mongoConn.getLog());
        request.setAttribute("lastVisit", mongoConn.getLastVisit());
        return request;
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
//        processRequest(request, response);
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
