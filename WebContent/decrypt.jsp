<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isThreadSafe="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<b>${message}</b>
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
Decryption key:  <input type="file" name="key" /> <br>
<input type="hidden" name="mode" value="decrypt">
<input type="hidden" name="cipher" value="asymetric">
<input type="submit" value="download" />
</form>
<a href="./index.jsp"> encrypt files</a>
</body>