<%-- 
    Document   : index
    Created on : 2016-9-21, 19:40:02
    Author     : Shuang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>      
        <h1>Hello! Welcome to the hash world!</h1>       
        <form action="ComputeHashes" method="GET">
          <font size="12px"></font>Input: <input type="text" name="input">
          <font size="12px"></font><br><br>Hash type: <br>
          <input type="radio" name="method" value="MD5" checked> MD5<br>
           <input type="radio" name="method" value="SHA-1"> SHA-1<br><br>
           <input type="submit">
        </form>                
    </body>
</html>
