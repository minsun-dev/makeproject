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
	<h1>학생관리</h1>
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
                <th width="40" align="center">신장</th>
                <th width="60" align="center">몸무게</th>
                <th width="100" align="center">소속학과번호</th>
                <th width="130" align="center">소속학과이름</th>
                <th width="100" align="center">담당교수번호</th>
                <th width="100" align="center">담당교수이름</th>
			</tr>
		</thead>
		<tbody id="list">
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
                            <td align="center">${item.deptno}</td>
                            <td align="center">${item.dname}</td>
                            <td align="center">${item.profno}</td>
                            <td align="center">${item.pname}</td>
					</tr>
				</c:forEach>
			</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	
	<!-- 전체 페이지 수가 2페이지 이상인 경우 "더보기"버튼 노출 -->
    <c:if test="${pageData.totalPage > 1}">
    <button id="btnMore">더보기</button>
    </c:if>
    
    <!-- Handlebar 탬플릿 코드 -->
    <script id="stud-list-tmpl" type="text/x-handlebars-template">
		{{#each item}}
		<tr>
			<td align="center">{{studno}}</td>
			<td align="center">
                <a href="${pageContext.request.contextPath}/student/view.do?studno={{studno}}">{{name}}</a>
            </td>
			<td align="center">{{userid}}</td>
			<td align="center">{{grade}}</td>
			<td align="center">{{idnum}}</td>
			<td align="center">{{birthdate}}</td>
			<td align="center">{{tel}}</td>
			<td align="center">{{height}}</td>
			<td align="center">{{weight}}</td>
			<td align="center">{{deptno}}</td>
            <td align="center">{{dname}}</td>
            <td align="center">{{profno}}</td>
            <td align="center">{{pname}}</td>
		</tr>
		{{/each}}
	</script>
    
    
    <!--Google CDN 서버로부터 jQuery 참조 -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <!-- Handlebar CDN 참조 -->
    <script src="//cdnjs.cloudflare.com/ajax/libs/handlebars.js/4.4.2/handlebars.min.js"></script>
    
    <!-- User code -->
    <script>
    	let nowPage = 1;    // 현재 페이지의 기본값
    	
    	$(function() {
            /** 더 보기 버튼에 대한 이벤트 정의 */
    		$("#btnMore").click(function() {
                // 다음 페이지를 요청하기 위해 페이지 변수 1 증가
    			nowPage++;
                
                // Restful API에 GET 방식 요청
    			$.get("${pageContext.request.contextPath}/student", {
    				"page": nowPage     // 페이지 번호는 GET 파라미터로 전송한다.
    			}, function(json) {
    				var source = $("#stud-list-tmpl").html();   // 템플릿 코드 가져오기
    				var template = Handlebars.compile(source);  // 템플릿 코드 컴파일
    				var result = template(json);    // 템플릿 컴파일 결과물에 json 전달
    				$("#list").append(result);      // 최종 결과물을 #list 요소에 추가한다.
                    
                    // 현재 페이지 번호가 전체 페이지 수에 도달했다면 더 보기 버튼을 숨긴다.
    				if (json.meta.totalPage <= nowPage) {
    					$("#btnMore").hide();
    				}
    			});
    		});
    	});
    </script>
</body>
</html>