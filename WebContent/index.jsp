<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login</title>
<link rel="stylesheet" href="bootstrap.css">
</head>
<body>
<%
// allow access only if session doesn't exists
if(session.getAttribute("loginHash") != null) {
	response.sendRedirect("encrypt.jsp");
}
%>
<br><br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<b>${message}</b>
<h1>Login</h1>
<form action="Login" method="post">
Username: <input type="text" class="form-control" name="username"><br>
Password: <input type="password" class="form-control" name="password"><br>
<input type="submit" class="btn btn-primary" value="login" />
<a href="./register.jsp" class="btn btn-primary">register</a>
</form>
</div>
</body>
</html>