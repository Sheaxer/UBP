<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Decryption</title>
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
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<b>${message}</b><br>
<p>Welcome <%=user %></p>
<h1>Symmetric decryption</h1>
<form action="Upload" method="post" enctype="multipart/form-data">

File to decrypt: <input type="file" name="fileName" /> <br>
Decryption key: <input type="file" name="key" /> <br>
<input type="hidden" name="mode" value="decrypt">
<input type="hidden" name="cipher" value="symetric">
<input type="submit" class="btn btn-primary" value="download" />
</form>
</div>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<h1>Asymmetric decryption</h1>
<form action="Upload" method="post" enctype="multipart/form-data">

File to decrypt: <input type="file" name="fileName" /> <br>
<!--  Decryption key:  <input type="file" name="key" /> <br> -->
<input type="hidden" name="mode" value="decrypt">
<input type="hidden" name="cipher" value="asymetric">
<input type="submit" class="btn btn-primary" value="download" />
</form>
</div>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<h1>Download asymetric keys</h1>
<form action="Upload" method="post">
<input type="hidden" name="mode" value="download">
<input type="submit" class="btn btn-primary" value="Download keys" />
</form>

<br>
<a href="<%=response.encodeURL("encrypt.jsp") %>" class="btn btn-primary"> encrypt files</a>
<br>
<form action="Logout" method="post">
	<input type="submit" class="btn btn-primary" value="logout" />
</form>
</div>
</body>