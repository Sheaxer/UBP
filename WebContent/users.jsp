<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="script.js"></script>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="style.css">
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
<p>Welcome <span id="myName"><%=user %></span></p>
<section id="userSection"></section>
<section id="filesFromMe"></section>
<form id="decryptForm" enctype="multipart/form-data"></form>
<section id="filesForMe"></section>

<section id="commentSection"></section>
<button id="getCommentsButton">Get comments for last clicked file</button>
<form id="addCommentForm">
<textarea name="message" rows="20" cols="50"></textarea>
<input type="submit" value="Add Comment to the last clicked file" />
</form>
<section id="encryptSection">
<form id="encryptForm">
File <input type="file" name="fileName" /> <br>
<input type="submit" value="Encrypt" />
</form>
</section>
<form id="decryptForm">
<input type="submit" value="Decrypt last selected file for you" />
</form>
</body>
</html>