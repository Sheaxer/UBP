<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Decryption</title>
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
<b>${message}</b><br>
<p>Welcome <%=user %></p>
<h1>Symmetric decryption</h1>
<form action="Upload" method="post" enctype="multipart/form-data">

File to decrypt: <input type="file" name="fileName" /> <br>
Decryption key:  <input type="file" name="key" /> <br>
<input type="hidden" name="mode" value="decrypt">
<input type="hidden" name="cipher" value="symetric">
<input type="submit" value="download" />
</form>
<h1>Asymmetric decryption</h1>
<form action="Upload" method="post" enctype="multipart/form-data">

File to decrypt: <input type="file" name="fileName" /> <br>
<!--  Decryption key:  <input type="file" name="key" /> <br> -->
<input type="hidden" name="mode" value="decrypt">
<input type="hidden" name="cipher" value="asymetric">
<input type="submit" value="download" />
</form>
<h1>Download asymetric keys</h1>
<form action="Upload">
<input type="hidden" name="mode" value="download">
<input type="submit" value="Download keys" />
</form>

<br>
<a href="<%=response.encodeURL("encrypt.jsp") %>"> encrypt files</a>
<br>
<form action="Logout" method="post">
	<input type="submit" value="logout" />
</form>
</body>