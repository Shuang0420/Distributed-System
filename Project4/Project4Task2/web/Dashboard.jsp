<%-- 
    Document   : Dashboard
    Created on : 2016-11-10, 22:55:52
    Author     : Shuang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Log Analytics Page</title>
        <style>
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 5px;
            }
            th {
                text-align: left;
            }
        </style>
    </head>
    <body>
        <h1>Analytical Data:</h1>
        <h3>Number of queries: <%= request.getAttribute("numOfEntries")%></h3>
        <h3>Last visit time: <%= request.getAttribute("lastVisit")%></h3>
        <h3>Average latency: <%= request.getAttribute("avgLatency")%></h3>
        <h3>Peak time: <%= request.getAttribute("peakTime")%></h3>
        <table style="width:100%">
            <tr>
                <th>Top queries</th>
                <th>Top user-agents</th>
            </tr>
            <tr>
                <td><%= request.getAttribute("topQuery")%></td> 
                <td><%= request.getAttribute("topUserAgent")%></td>
            </tr>
        </table>
        <br /><br />
        <h1>Log Data: </h1>
        <table style="width:100%">
            <tr>
                <th>User-agent</th>
                <th>Parameter</th> 
                <th> URL String</th>
                <th> Reply from API</th>
                <th> JSON response</th>
                <th> Request timestamp</th>
                <th>Response timestamp</th> 
                <th> Latency</th>
            </tr>
            <%= request.getAttribute("logData")%>
        </table>
    </body>
</html>

