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
<h1>Symetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	<input type="file" name="fileName" />
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="symetric">
	<input type="submit" value="upload" />
</form>


<h1>Asymetric Encryption</h1>
<form  action="Upload" method="post" enctype="multipart/form-data">
	<input type="file" name="fileName" />
	<input type="hidden" name="mode" value="encrypt">
	<input type="hidden" name="cipher" value="asymetric">
	<input type="submit" value="upload" />
</form>

<a href="./decrypt.jsp"> decrypt files</a>
</body>
</html>