<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="script.js"></script>
<meta charset="ISO-8859-1">
<title>User section</title>
<link rel="stylesheet" href="bootstrap.css">
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
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px"><p><h1>Welcome <span id="myName"><%=user %></span></h1></p></div>
<br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px"><section id="userSection"></section></div>
<br> <div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">  <section id="filesFromMe"></section> </div>
<br> <div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<section id="filesForMe"></section> </div> <br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<form id="decryptForm" method="POST" action="./userSection">
<input type="submit" value="Decrypt last selected file for you" class="btn btn-primary"/></form> </div> <br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<form id="downloadForm" method="POST" action="./userSection">
<input type="submit" value="Download Last selected file for you still encrypted" class="btn btn-primary"/></form> </div>
<br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px"><section id="commentSection"></section>
<button id="getCommentsButton" class="btn btn-primary">Get comments for last clicked file</button> </div> <br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px"><form id="addCommentForm">
<textarea name="message" rows="20" cols="50"></textarea> <br>
<input type="submit" value="Add Comment to the last clicked file" class="btn btn-primary"/>
</form> </div> <br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<section id="encryptSection">
<form id="encryptForm">
File <input type="file" name="fileName" /> <br>
<input type="submit" value="Encrypt" class="btn btn-primary"/>
</form>
</section> </div> <br>
<div class="container" style="background-color:rgb(200, 255, 255); border-radius:5px">
<form action="Logout" method="post">
	<input type="submit" value="logout" class="btn btn-primary"/>
</form>
</div>
</body>
</html>