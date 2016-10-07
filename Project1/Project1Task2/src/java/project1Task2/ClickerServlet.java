package project1Task2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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
@WebServlet(name = "ClickerServlet", urlPatterns = {"/", "/submit", "/getResults"})
public class ClickerServlet extends HttpServlet {

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
        // check the user-agent and give the correct view for Android or iPhone or desktop
        // a default doctype is desktop 
        response.setContentType("text/html;charset=UTF-8");
        String ua = request.getHeader("User-Agent");
        System.out.println(ua);
        boolean mobile;
        String doctype = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.contains("Android")) || (ua.contains("iPhone")))) {
            mobile = true;
            doctype = "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">";

        }
        request.setAttribute("doctype", doctype);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nextView;
        processRequest(request, response);
        // get user's option
        String option = request.getParameter("option");

        // set current option and count
        //System.out.print("[option] = " + option);
        if (option != null) {
            model.setOption(option);
            request.setAttribute("lastOption", model.getOption());
            nextView = "next.jsp";

        } else {
            nextView = "index.jsp";
        }

        // transfer control over the corrent "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = null;
        processRequest(request, response);
        // check the url path
        if (request.getServletPath().equals("/getResults")) {
            HashMap<String, Integer> option_count = model.getOptionCount();
            StringBuffer str = new StringBuffer();

            Iterator it = option_count.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();

                String key = (String) entry.getKey();
                int value = (int) entry.getValue();
                if (value == 0) {
                    it.remove();
                } else {
                    str.append(key + ":" + value + "<br><br>");
                }
            }

            if (str.length() < 1) {
                request.setAttribute("option_count", "There are currently no results");
            } else {
                str.append("These results have now been cleared.");
                request.setAttribute("option_count", "The results from the survey are as follows:<br><br>" + str.toString());
            }
            // clear data
            model.clear();
            result = "result.jsp";
            
        // if the homepage
        } else if (request.getServletPath().equals("/")) {
            result = "index.jsp";
        }
        // transfer control over the corrent "view"
        RequestDispatcher result_page = request.getRequestDispatcher(result);
        result_page.forward(request, response);
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
