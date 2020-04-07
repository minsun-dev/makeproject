<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Hello JSP</title>
</head>
<body>
	<h1>WebHelper에 의한 파일 업로드 확인</h1>
	
	<h2>원본 이미지</h2>
	<img src="${item.filePath}" width="240"/>
	
	<h2>썸네일 이미지</h2>
	<img src="${item.thumbnail}" width="64"/>
	
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