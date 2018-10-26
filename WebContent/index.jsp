<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<b> Hello World </b>
<form  action="Upload" method="post" enctype="multipart/form-data">
	<input type="file" name="fileName" />
	Key : <input type= "text" id="fkey" name="key"><br>
	<input type="hidden" name="mode" value="upload">
	<input type="submit" value="upload" />
</form>

<a href="./download.jsp"> download files</a>

</body>
</html>