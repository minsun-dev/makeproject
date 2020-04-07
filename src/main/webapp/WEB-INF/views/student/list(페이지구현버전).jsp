<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Hello JSP</title>
</head>
<body>
	<h1>교수관리</h1>
	<a href="${pageContext.request.contextPath}/student/add.do">[학생추가]</a>
	
	<!-- 검색폼 -->
	<form method="get" action="${pageContext.request.contextPath}/student/list.do">
		<label for="keyword">검색어: </label>
		<input type="search" name="keyword" id="keyword" placeholder="이름검색" value="${keyword}"/>
		<button type="submit">검색</button>
	</form>
	
	<hr/>
	<!-- 조회 결과 목록 -->
	<table border="1">
		<thead>
			<tr>
				<th width="80" align="center">학생번호</th>
                <th width="100" align="center">학생이름</th>
                <th width="100" align="center">아이디</th>
                <th width="80" align="center">학년</th>
                <th width="50" align="center">주민번호</th>
                <th width="100" align="center">생년월일</th>
                <th width="70" align="center">전화번호</th>
                <th width="30" align="center">신장</th>
                <th width="60" align="center">몸무게</th>
                <th width="130" align="center">소속학과이름</th>
                <th width="100" align="center">담당교수이름</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
			<%-- 조회결과가 없는 경우 --%>
			<c:when test="${output == null || fn:length(output) == 0}">
				<tr>
					<td colspan="9" align="center">조회결과가 없습니다.</td>
				</tr>
			</c:when>
			<%-- 조회결과가 있는 경우 --%>
			<c:otherwise>
				<%-- 조회 결과에 따른 반복 처리 --%>
				<c:forEach var="item" items="${output}" varStatus="status">
					<%-- 출력을 위해 준비한 학생이름 변수 --%>
					<c:set var="name" value="${item.name}"/>
					
					<%-- 검색어가 있다면? --%>
					<c:if test="${keyword != '' }">
						<%-- 검색어에 <mark> 태그를 적용하여 형광펜 효과 준비 --%>
						<c:set var="mark" value="<mark>${keyword}</mark>"/>
						<%-- 출력을 위해 준비한 교수이름에서 검색어와 일치하는 단어를 형광펜 효과로 변경 --%>
						<c:set var="name" value="${fn:replace(name, keyword, mark)}"/>
					</c:if>
					
					<%-- 상세페이지로 이동하기 위한 URL --%>
					<c:url value="/student/view.do" var="viewUrl">
						<c:param name="studno" value="${item.studno}"/>					
					</c:url>
					
					<tr>
						<td align="center">${item.studno}</td>
                            <td align="center"><a href="${viewUrl}">${name}</a></td>
                            <td align="center">${item.userid}</td>
                            <td align="center">${item.grade}</td>
                            <td align="center">${item.idnum}</td>
                            <td align="center">${item.birthdate}</td>
                            <td align="center">${item.tel}</td>
                            <td align="center">${item.height}</td>
                            <td align="center">${item.weight}</td>
                            <td align="center">${item.dname}</td>
                            <td align="center">${item.pname}</td>
					</tr>
				</c:forEach>
			</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	
	<!-- 페이지 번호 구현 -->
	<%-- 이전 그룹에 대한 링크 --%>
	<c:choose>
		<%-- 이전 그릅으로 이동 가능하다면? --%>
		<c:when test="${pageData.prevPage > 0}">
			<%-- 이동할 URL 생성 --%>
			<c:url value="/student/list.do" var="prevPageUrl">
				<c:param name="page" value="${pageData.prevPage}"/>
				<c:param name="keyword" value="${keyword}"/>			
			</c:url>
			<a href="${prevPageUrl}">[이전]</a>
		</c:when>
		<c:otherwise>
			[이전]
		</c:otherwise>
	</c:choose>
	<%-- 페이지 번호 (시작 페이지부터 끝 페이지까지 반복)--%>
	<c:forEach var="i" begin="${pageData.startPage}" end="${pageData.endPage}" varStatus="status">
		<%-- 이동할 URL 생성 --%>
		<c:url value="/student/list.do" var="pageUrl">
			<c:param name="page" value="${i}"/>
			<c:param name="keyword" value="${keyword}"/>
		</c:url>
		
		<%-- 페이지 번호 출력 --%>
		<c:choose>
			<%-- 현재 머물고 있는 페이지 번호를 출력할 경우 링크 적용 안함 --%>
			<c:when test="${pageData.nowPage == i }">
				<strong>[${i}]</strong>
			</c:when>
			<%-- 나머지 페이지의 경우 링크 적용함 --%>
			<c:otherwise>
				<a href="${pageUrl}">[${i}]</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<%--다음 그룹에 대한 링크 --%>
	<c:choose>
		<%-- 다음 그릅으로 이동 가능하다면? --%>
		<c:when test="${pageData.nextPage > 0}">
			<%-- 이동할 URL 생성 --%>
			<c:url value="/student/list.do" var="nextPageUrl">
				<c:param name="page" value="${pageData.nextPage}"/>
				<c:param name="keyword" value="${keyword}"/>			
			</c:url>
			<a href="${nextPageUrl}">[다음]</a>
		</c:when>
		<c:otherwise>
			[다음]
		</c:otherwise>
	</c:choose>
</body>
</html>