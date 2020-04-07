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
	<h1>학생정보</h1>
	<p>학생번호: ${output.studno}</p>
	<p>학생이름: ${output.name}</p>
	<p>아이디: ${output.userid}</p>
	<p>학년: ${output.grade}</p>
	<p>주민번호: ${output.idnum}</p>
	<p>생년월일: ${output.birthdate}</p>
	<p>전화번호: ${output.tel}</p>
	<p>신장: ${output.height}</p>
	<p>몸무게: ${output.weight}</p>
	<p>학과이름: ${output.dname}</p>
	<p>담당교수번호: ${output.profno}</p>
	<hr/>
	<a href="${pageContext.request.contextPath}/student/list.do">[목록보기]</a>
	<a href="${pageContext.request.contextPath}/student/add.do">[학생추가]</a>
	<a href="${pageContext.request.contextPath}/student/edit.do?studno=${output.studno}">[학생정보수정]</a>
	<a href="#" id="deleteStudent" data-studno="${output.studno}" data-name="${output.name}" 
	data-grade="${output.grade}">[학생정보삭제]</a>
	
	<!--Google CDN 서버로부터 jQuery 참조 -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <!-- jQuery Ajax Form plugin CDN -->
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery.form/4.2.2/jquery.form.min.js"></script>
    <!-- jQuery Ajax Setup -->
    <script src="${pageContext.request.contextPath}/assets/plugins/ajax/ajax_helper.js"></script>
    <!-- User Code -->
    <script>
    $(function(){
    	$("#deleteStudent").click(function(e){
    		e.preventDefault(); // 링크 클릭에 대한 페이지 이동 방지
    		
    		let current = $(this);	// 이벤트가 발생한 객체 자신 ==> <a> 태그
    		let studno = current.data('studno'); 	// data-studno 값을 가져옴
    		let name = current.data('name');		// data-name 값을 가져옴
    		let target = studno + " " + name;		// "학번 + 공백 + 이름" 형식의 문자열
    		
    		// 삭제 확인
    		if(!confirm("정말 " + target + "님을(를) 삭제하시겠습니까?")){
    			return false;
    		}
    		// delete 메서드로 Ajax 요청 --> <form> 전송이 아니므로 직접 구현한다.
    		$.delete("${pageContext.request.contextPath}/student",{
    			"studno": studno
    			}, function(json){
    				if (json.rt =="OK"){
    					alert("삭제되었습니다.");
    					// 삭제 완료 후 목록 페이지로 이동
    					window.location = "${pageContext.request.contextPath}/student/list.do";
    				}
    		});
    	});    	
    });
    </script>
</body>
</html>