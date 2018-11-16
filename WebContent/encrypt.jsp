<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Encryption</title>
</head>
<body>
<%
// allow access only if session exists
String user = null;
if(session.getAttribute("username") == null) {
	response.sendRedirect("index.jsp");
}
else {
	user = (String) session.getAttribute("username");
}
%>
<b>${message}</b><br>
<p>Welcome <%=user %></p>
<h1>Symmetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	<input type="file" name="fileName" />
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="symetric">
	<input type="submit" value="upload" />
</form>


<h1>Asymmetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	File to encrypt<input type="file" name="fileName" /> <br>
	Optional public key<input type="file" name="key" />
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="asymetric">
	<input type="submit" value="upload" />
</form>
<br>
<a href="<%=response.encodeURL("decrypt.jsp") %>"> decrypt files</a>
<br>
<form action="Logout" method="post">
	<input type="submit" value="logout" />
</form>
</body>
</html>