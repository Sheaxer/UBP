<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Encryption</title>
<link rel="stylesheet" href="bootstrap.css">
</head>
<body>
<%
// allow access only if session exists
String user = null;
if(session.getAttribute("loginHash") == null) {
	response.sendRedirect("index.jsp");
}
else {
	user = (String) session.getAttribute("username");
}
%>
<br><br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<b>${message}</b><br>
<p>Welcome <%=user %></p>
<h1>Symmetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	File to encrypt: <input type="file" name="fileName" /> <br>
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="symetric">
	<input type="submit" class="btn btn-primary" value="upload" />
</form>
</div>

<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<h1>Asymmetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	File to encrypt: <input type="file" name="fileName" /> <br>
	<!--  Optional public key<input type="file" name="key" /> -->
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="asymetric">
	<input type="submit" class="btn btn-primary" value="upload" />
</form>
</div>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<h1>Download asymetric keys</h1>
<form action="Upload" method="post">
<input type="hidden" name="mode" value="download">
<input type="submit" class="btn btn-primary" value="Download keys" />
</form>
<br>
<a href="<%=response.encodeURL("decrypt.jsp") %>" class="btn btn-primary"> decrypt files</a>
<br>
<form action="Logout" method="post">
	<input type="submit" class="btn btn-primary" value="logout" />
</form>
</div>
</body>
</html>