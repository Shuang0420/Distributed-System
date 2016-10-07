<%-- 
    Document   : result
    Created on : 2016-9-21, 21:18:13
    Author     : Shuang
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Distributed Systems Class Clicker</h1>
        <%=request.getAttribute("option_count")%>
        <br><br>
        <form action="submit" method="POST"><input type="submit" value="Homepage"></form>
    </body>
</html>
