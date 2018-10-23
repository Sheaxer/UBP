<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h> Hello World </h>
<form action="upload" method="post"
enctype="multipart/form−data">
	<input type="file" name="file" />
	Key : <input type= "text" id="fname" name="fname"><br>
	<input type="submit" value="upload" />
</form>

</body>
</html>