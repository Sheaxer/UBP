<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="script.js"></script>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="bootstrap.css">
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
<br><br>
<div class="container" style="background-color: rgb(229, 229, 255); border-radius:5px">
<p>Welcome <span id="myName"><%=user %></span></p>
<section id="userSection" class="container"></section>
<section id="filesFromMe" class="container"></section>

<section id="filesForMe" class="container"></section>
<form id="decryptForm" method="POST" action="./userSection">
<input type="submit" value="Decrypt last selected file for you" class="btn btn-primary" /></form>
<form id="downloadForm" method="POST" action="./userSection">
<input type="submit" value="Download Last selected file for you still encrypted" class="btn btn-primary" /></form>
<section id="commentSection" class="container"></section>
<button id="getCommentsButton" class="btn btn-primary">Get comments for last clicked file</button>
<form id="addCommentForm">
<textarea name="message" rows="20" cols="50" class="form-control"></textarea>
<input type="submit" class="btn btn-primary" value="Add Comment to the last clicked file" />
</form>
<section id="encryptSection" class="container"">
<form id="encryptForm">
File <input type="file" name="fileName" /> <br>
<input type="submit" class="btn btn-primary" value="Encrypt" />
</form>
</section>
<form action="Logout" method="post">
	<input type="submit" class="btn btn-primary" value="logout" />
</form>
</div>
</body>
</html>