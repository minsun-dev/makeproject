<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Hello JSP</title>
</head>
<body>
	<h1>${subject}</h1>
	
	<h2>원본 이미지</h2>
	<img src="${item.filePath }"/>
	
	<h2>썸네일 이미지</h2>
	<img src="${item.thumbnail}"/>
	
	<ul>
		<li>fieldName: ${item.fieldName}</li>
		<li>orginName: ${item.orginName}</li>
		<li>filePath: ${item.filePath}</li>
		<li>contentType: ${item.contentType}</li>
		<li>fileSize: ${item.fileSize}</li>
		<li>thumbnail: ${item.thumbnail}</li>
	</ul>
</body>
</html>