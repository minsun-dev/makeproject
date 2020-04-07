<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<meta charset="utf-8"/>
	<title>Home</title>
</head>
<body>
<h1>
	SpringHelper
</h1>

<h2>Helper 이식 테스트</h2>
<ul>
	<li>
		<a href="${pageContext.request.contextPath}/mail/write.do">/mail/write.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/retrofit/daily_box_office_graph.do">/retrofit/daily_box_office_graph.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/upload/upload.do">/upload/upload.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/upload/use_helper.do">/upload/use_helper.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/upload/multiple.do">/upload/multiple.do</a>
	</li>
</ul>

<h2>데이터베이스 연동 테스트</h2>
<ul>
	<li>
		<a href="${pageContext.request.contextPath}/department/list.do">/department/list.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/professor/list.do">/professor/list.do</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/student/list.do">/student/list.do</a>
	</li>
</ul>


</body>
</html>
