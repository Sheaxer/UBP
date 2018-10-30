<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="Upload" method="post" enctype="multipart/form-data">

File to decrypt: <input type="file" name="fileName" /> <br>
Decryption key:  <input type="file" name="key" /> <br>
<input type="hidden" name="mode" value="decrypt">
<input type="submit" value="download" />
</form>
</body>