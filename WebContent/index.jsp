<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login</title>
</head>
<body>
<%
// allow access only if session doesn't exists
if(session.getAttribute("loginHash") != null) {
	response.sendRedirect("encrypt.jsp");
}
%>
<b>${message}</b>
<h1>Login</h1>
<form action="Login" method="post">
Username: <input type="text" name="username"><br>
Password: <input type="password" name="password"><br>
<input type="submit" value="login" />
</form>
<br>
<a href="./register.jsp">register</a>
</body>
</html>