<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registration</title>
<link rel="stylesheet" href="bootstrap.css">
</head>
<body>
<%
// allow access only if session doesn't exists
if(session.getAttribute("username") != null) {
	response.sendRedirect("encrypt.jsp");
}
%>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<b>${message}</b>
<h1>Registration</h1>
<form action="Registration" method="post">
Username: <input type="text" class="form-control" name="username"><br>
Password: <input type="password" class="form-control" name="password"><br>
repeat password: <input type="password" class="form-control" name="passwordRepeat"><br>
<input type="submit" class="btn btn-primary" value="register" />
</form>
<br>
<a href="./index.jsp" class="btn btn-primary">back</a>
</div>
</body>
</html>