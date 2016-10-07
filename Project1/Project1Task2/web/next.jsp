<%-- 
    Document   : submit
    Created on : 2016-9-21, 21:47:44
    Author     : Shuang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype")%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Distributed Systems Class Clicker</h1>
        Your "<%= request.getAttribute("lastOption")%>" response has been registered <br><br>
        <form action="submit" method="POST">
            Submit your answer to the current question:<br><br>
            <input type="radio" name="option" value="A"> A<br>
            <input type="radio" name="option" value="B"> B<br>
            <input type="radio" name="option" value="C"> C<br>
            <input type="radio" name="option" value="D"> D<br>
            <br><br>
            <input type="submit" value="Submit">
            <br>
        </form>
        <br>
    </body>
</html>
