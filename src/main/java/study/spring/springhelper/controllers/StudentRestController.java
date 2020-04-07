/**
 * @filename    : StudentRestController.java
 * @description : 클라이언트가 필요한 데이터 요청을 수행하고 결과를 알려주는 컨트롤러
 * @author      : 김민선 (akrend0433@gmail.com)
 */
package study.spring.springhelper.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import study.spring.springhelper.helper.PageData;
import study.spring.springhelper.helper.RegexHelper;
import study.spring.springhelper.helper.WebHelper;
import study.spring.springhelper.model.Student;
import study.spring.springhelper.service.StudentService;

@RestController
public class StudentRestController {
	/** WebHelper 주입*/
	@Autowired
	WebHelper webHelper;
	
	/** RegexHelper 주입*/
	@Autowired RegexHelper regexHelper;
	
	/** Service 패턴 구현체 주입*/
	@Autowired StudentService studentService;
	
	/** 목록페이지 */
	@RequestMapping(value="/student", method=RequestMethod.GET)
	public Map<String, Object> get_list(Model model,
			@RequestParam(value="keyword", defaultValue="", required=false)String keyword,
			@RequestParam(value="page", defaultValue="1", required=false)int nowPage,
			@RequestParam(value="totalCount", defaultValue="0", required=false)int totalCount,
			@RequestParam(value="listCount", defaultValue="10", required=false)int listCount,
			@RequestParam(value="pageCount", defaultValue="5", required=false)int pageCount) {
		/** 1) 필요한 변수 값 생성*/
		
		/** 2) 데이터 조회하기 */
		// 조회에 필요한 조건값(검색어)를 Beans에 담는다.
		Student input = new Student();
		input.setName(keyword);
		
		List<Student> output = null; // 조회결과가 저장될 객체
		PageData pageData = null;		// 페이지 번호를 계산한 결과가 저장될 객체
		
		try {
			// 전체 게시글 수 조회
			totalCount = studentService.getStudentCount(input);
			// 페이지 번호 계산 -> 계산 결과를 로그로 출력할 것이다.
			pageData = new PageData(nowPage, totalCount, listCount, pageCount);
						
			// SQL 의 LIMIT 절에서 사용될 값을 Beans의 static 변수에 저장
			Student.setOffset(pageData.getOffset());
			Student.setListCount(pageData.getListCount());
			// 데이터 조회하기
			output = studentService.getStudentList(input);
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) JSON 출력하기*/
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("keyword", keyword);
		data.put("item", output);	
		data.put("meta", pageData);
		
		return webHelper.getJsonData(data);
	}
	
	/** 상세페이지*/
	@RequestMapping(value="/student/{studno}", method=RequestMethod.GET)
	public Map<String, Object> get_item(@PathVariable("studno") int studno){
		/** 1) 필요한 변수 값 생성*/
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
		if(studno==0) {
			return webHelper.getJsonWarning("학생번호가 없습니다.");
		}
				
		/** 2) 데이터 조회하기 */
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Student input = new Student();
		input.setStudno(studno);
				
		// 조회결과를 저장할 객체 선언
		Student output=null;
				
		try {
			// 데이터 조회
			output = studentService.getStudentItem(input);
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
				
		/** 3) JSON 출력하기 */
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("item", output);
		
		return webHelper.getJsonData(data);		
	}
	
	/** 작성 폼에 대한 action 페이지 */
	@RequestMapping(value="/student", method=RequestMethod.POST)
	public Map<String, Object> post(Model model,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="grade", required=false)int grade,
			@RequestParam(value="idnum", required=false)String  idnum,
			@RequestParam(value="birthdate", required=false)String birthdate,
			@RequestParam(value="tel", required=false)String tel,
			@RequestParam(value="height", required=false)int height,
			@RequestParam(value="weight", required=false)int weight,
			@RequestParam(value="deptno", required=false)int deptno,
			@RequestParam(value="profno", required=false)int profno) {
		
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(name==null) {return webHelper.getJsonWarning("학생 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.getJsonWarning("학생 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.getJsonWarning( "학생 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.getJsonWarning("학생 아이디는 영어와 숫자로만 가능합니다.");}
		if(grade==0) {return webHelper.getJsonWarning( "학년을 입력하세요.");}
		if(idnum==null) {return webHelper.getJsonWarning("주민번호를 입력하세요.");}
		if(!regexHelper.isNum(idnum)) {return webHelper.getJsonWarning("주민번호는 숫자만 입력가능합니다.");}
		if(birthdate==null) {return webHelper.getJsonWarning("생년월일을 입력하세요.");}
		if(height<0) {return webHelper.getJsonWarning("신장은 0 보다 작을 수 없습니다.");}
		if(weight<0) {return webHelper.getJsonWarning("몸무게는 0 보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.getJsonWarning("소속 학과 번호를 입력하세요.");}
		
		/** 2) 데이터 저장하기*/
		// 저장할 값들을 Beans에 담는다.
		Student input = new Student();
		input.setName(name);
		input.setUserid(userid);
		input.setGrade(grade);
		input.setIdnum(idnum);
		input.setBirthdate(birthdate);
		input.setTel(tel);
		input.setHeight(height);
		input.setWeight(weight);
		input.setDeptno(deptno);
		input.setProfno(profno);
		
		// 저장될 결과를 조회하기 위한 객체
		Student output = null;
		
		try {
			// 데이터저장
			// --> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 PK값이 저장된다.
			studentService.addStudent(input);
			
			// 데이터 조회
			output = studentService.getStudentItem(input);
		}catch(Exception e) {
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 JSON 출력*/
		Map<String, Object>map = new HashMap<String, Object>();
		map.put("item", output);
		return webHelper.getJsonData(map);
	}
	
	/** 수정 폼에 대한 action 페이지 */
	@RequestMapping(value="/student", method=RequestMethod.PUT)
	public Map<String, Object> put(Model model,
			@RequestParam(value="studno", required=false)int studno,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="grade", required=false)int grade,
			@RequestParam(value="idnum", required=false)String idnum,
			@RequestParam(value="birthdate", required=false)String birthdate,
			@RequestParam(value="tel", required=false)String tel,
			@RequestParam(value="height", required=false)int height,
			@RequestParam(value="weight", required=false)int weight,
			@RequestParam(value="deptno", required=false)int deptno,
			@RequestParam(value="profno", required=false)int profno) {
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(studno== 0) {return webHelper.getJsonWarning("학생번호가 없습니다.");}
		if(name==null) {return webHelper.getJsonWarning("학생 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.getJsonWarning("학생 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.getJsonWarning( "학생 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.getJsonWarning("학생 아이디는 영어와 숫자로만 가능합니다.");}
		if(grade==0) {return webHelper.getJsonWarning( "학년을 입력하세요.");}
		if(idnum==null) {return webHelper.getJsonWarning("주민번호를 입력하세요.");}
		if(!regexHelper.isNum(idnum)) {return webHelper.getJsonWarning("주민번호는 숫자만 입력가능합니다.");}
		if(birthdate==null) {return webHelper.getJsonWarning("생년월일을 입력하세요.");}
		if(height<0) {return webHelper.getJsonWarning("신장은 0 보다 작을 수 없습니다.");}
		if(weight<0) {return webHelper.getJsonWarning("몸무게는 0 보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.getJsonWarning("소속 학과 번호를 입력하세요.");}
		
		/** 2) 데이터 수정하기*/
		// 수정할 값들을 Beans에 담는다.
		Student input = new Student();
		input.setStudno(studno);
		input.setName(name);
		input.setUserid(userid);
		input.setGrade(grade);
		input.setIdnum(idnum);
		input.setBirthdate(birthdate);
		input.setTel(tel);
		input.setHeight(height);
		input.setWeight(weight);
		input.setDeptno(deptno);
		input.setProfno(profno);
		
		// 수정된 결과를 조회하기 위한 객체
		Student output = null;
		
		try {
			// 데이터수정
			studentService.editStudent(input);
			
			// 데이터 조회
			output = studentService.getStudentItem(input);
		}catch(Exception e) {
			return webHelper.getJsonError( e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 JSON 출력 */
		Map<String, Object>map = new HashMap<String, Object>();
		map.put("item", output);
		return webHelper.getJsonData(map);
	}
	
	/** 삭제 처리*/
	@RequestMapping(value="/student", method=RequestMethod.DELETE)
	public Map<String, Object> delete(@RequestParam(value="studno", defaultValue="0")int studno){ 
		/** 1) 필요한 변수 값 생성*/
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
		if(studno==0) {
			return webHelper.getJsonWarning("학생 번호가 없습니다.");
		}
						
		/** 2) 데이터 조회하기 */
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Student input = new Student();
		input.setStudno(studno);
						
		try {
			studentService.deleteStudent(input); // 데이터 삭제
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
				
		/** 3) 결과를 확인하기 위한 JSNO 출력*/
		// 확인할 대상이 삭제된 상태이므로 OK로 전달
		return webHelper.getJsonData();
	}
}
