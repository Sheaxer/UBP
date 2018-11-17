<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registration</title>
</head>
<body>
<%
// allow access only if session doesn't exists
if(session.getAttribute("username") != null) {
	response.sendRedirect("encrypt.jsp");
}
%>
<b>${message}</b>
<h1>Registration</h1>
<form action="Registration" method="post">
Username: <input type="text" name="username"><br>
Password: <input type="password" name="password"><br>
repeat password: <input type="password" name="passwordRepeat"><br>
<input type="submit" value="register" />
</form>
<br>
<a href="./index.jsp">back</a>
</body>
</html>