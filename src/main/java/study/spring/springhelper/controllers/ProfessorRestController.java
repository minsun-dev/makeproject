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
import study.spring.springhelper.model.Professor;
import study.spring.springhelper.service.DepartmentService;
import study.spring.springhelper.service.ProfessorService;

@RestController
public class ProfessorRestController {
	/** WebHelper 주입*/
	@Autowired
	WebHelper webHelper;
	
	/** RegexHelper 주입*/
	@Autowired RegexHelper regexHelper;
	
	/** Service 패턴 구현체 주입*/
	@Autowired ProfessorService professorService;
	
	// --> import study.spring.springhelper.service.DepartmentService;
	@Autowired DepartmentService departmentService;
	
	/** 목록 페이지*/
	@RequestMapping(value = "/professor", method= RequestMethod.GET)
	public Map<String, Object> get_list(Model model,
			@RequestParam(value="keyword", defaultValue="", required=false)String keyword,
			@RequestParam(value="page", defaultValue="1", required=false)int nowPage,
			@RequestParam(value="totalCount", defaultValue="0", required=false)int totalCount,
			@RequestParam(value="listCount", defaultValue="10", required=false)int listCount,
			@RequestParam(value="pageCount", defaultValue="5", required=false)int pageCount) {
		/** 1) 필요한 변수 값 생성*/
		
		/** 2) 데이터 조회하기 */
		// 조회에 필요한 조건값(검색어)를 Beans에 담는다.
		Professor input = new Professor();
		input.setName(keyword);
		
		List<Professor> output = null; // 조회결과가 저장될 객체
		PageData pageData = null;		// 페이지 번호를 계산한 결과가 저장될 객체
		
		try {
			// 전체 게시글 수 조회
			totalCount = professorService.getProfessorCount(input);
			// 페이지 번호 계산 -> 계산 결과를 로그로 출력할 것이다.
			pageData = new PageData(nowPage, totalCount, listCount, pageCount);
						
			// SQL 의 LIMIT 절에서 사용될 값을 Beans의 static 변수에 저장
			Professor.setOffset(pageData.getOffset());
			Professor.setListCount(pageData.getListCount());
			// 데이터 조회하기
			output = professorService.getProfessorList(input);
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) JSON 출력하기*/
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("item", output);
		data.put("keyword", keyword);
		data.put("meta", pageData);
		
		return webHelper.getJsonData(data);
	}
	
	/** 상세 페이지*/
	@RequestMapping(value = "/professor/{profno}", method= RequestMethod.GET)
	public Map<String, Object> get_item(@PathVariable("profno")int profno){
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
		if(profno==0) {
			return webHelper.getJsonWarning("교수번호가 없습니다.");
		}
				
		/** 2) 데이터 조회하기 */
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Professor input = new Professor();
		input.setProfno(profno);
				
		// 조회결과를 저장할 객체 선언
		Professor output=null;
				
		try {
			// 데이터 조회
			output = professorService.getProfessorItem(input);
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
				
		/** 3) JSON 출력하기 */
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("item", output);
				
		return webHelper.getJsonData(data);
	}
	
	/** 작성 폼에 대한 action 페이지 */
	@RequestMapping(value = "/professor", method= RequestMethod.POST)
	public Map<String, Object> post(Model model,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="position", required=false)String position,
			@RequestParam(value="sal", required=false)int sal,
			@RequestParam(value="hiredate", required=false)String hiredate,
			@RequestParam(value="comm", required=false)int comm,
			@RequestParam(value="deptno", required=false)int deptno) {
		
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(name==null) {return webHelper.getJsonWarning("교수 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.getJsonWarning("교수 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.getJsonWarning( "교수 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.getJsonWarning("교수 아이디는 영어와 숫자로만 가능합니다.");}
		if(position==null) {return webHelper.getJsonWarning( "직급을 입력하세요.");}
		if(sal==0) {return webHelper.getJsonWarning("급여를 입력하세요.");}
		if(sal<0) {return webHelper.getJsonWarning("급여는 0보다 작을 수 없습니다.");}
		if(hiredate==null) {return webHelper.getJsonWarning("입사일을 입력하세요.");}
		if(comm<0) {return webHelper.getJsonWarning("보직 수당은 0보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.getJsonWarning("소속 교수 번호를 입력하세요.");}
		
		/** 2) 데이터 저장하기*/
		// 저장할 값들을 Beans에 담는다.
		Professor input = new Professor();
		input.setName(name);
		input.setUserid(userid);
		input.setPosition(position);
		input.setSal(sal);
		input.setHiredate(hiredate);
		input.setComm(comm);
		input.setDeptno(deptno);
		
		// 저장된 결과를 조회하기 위한 객체
		Professor output = null;
		
		try {
			// 데이터저장
			// --> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 PK값이 저장된다.
			professorService.addProfessor(input);
			
			// 데이터 조회
			output = professorService.getProfessorItem(input);
		}catch(Exception e) {
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 JSON 출력*/
		// 저장결과를 확인하기 위해서 데이터 저장시 생성된 PK 값을 상세 페이지로 전달해야 한다.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("item", output);
		return webHelper.getJsonData(map);		
	}
	
	/** 수정 폼에 대한 action 페이지 */
	@RequestMapping(value = "/professor", method= RequestMethod.PUT)
	public Map<String, Object> put(Model model,
			@RequestParam(value="profno", defaultValue="0", required=false)int profno,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="position", required=false)String position,
			@RequestParam(value="sal", required=false)int sal,
			@RequestParam(value="hiredate", required=false)String hiredate,
			@RequestParam(value="comm", required=false)int comm,
			@RequestParam(value="deptno", required=false)int deptno) {
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(profno==0) {return webHelper.getJsonWarning("교수번호가 없습니다.");}
		if(name==null) {return webHelper.getJsonWarning( "교수 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.getJsonWarning("교수 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.getJsonWarning("교수 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.getJsonWarning("교수 아이디는 영어와 숫자로만 가능합니다.");}
		if(position==null) {return webHelper.getJsonWarning("직급을 입력하세요.");}
		if(sal==0) {return webHelper.getJsonWarning( "급여를 입력하세요.");}
		if(sal<0) {return webHelper.getJsonWarning("급여는 0보다 작을 수 없습니다.");}
		if(hiredate==null) {return webHelper.getJsonWarning( "입사일을 입력하세요.");}
		if(comm<0) {return webHelper.getJsonWarning( "보직 수당은 0보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.getJsonWarning("소속 교수 번호를 입력하세요.");}
		
		/** 2) 데이터 수정하기*/
		// 수정할 값들을 Beans에 담는다.
		Professor input = new Professor();
		input.setProfno(profno);
		input.setName(name);
		input.setUserid(userid);
		input.setPosition(position);
		input.setSal(sal);
		input.setHiredate(hiredate);
		input.setComm(comm);
		input.setDeptno(deptno);
		
		// 수정된 결과를 조회하기 위한 객체
		Professor output = null;
		
		try {
			// 데이터수정
			// --> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 PK값이 저장된다.
			professorService.editProfessor(input);
			
			// 데이터 조회
			output = professorService.getProfessorItem(input);
		}catch(Exception e) {
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 페이지 이동*/
		// 수정한 대상을 상세페이지에 알려주기 위해서 PK값을 수정해야 한다.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("item", output);
		return webHelper.getJsonData(map);
	}
	
	/** 삭제 처리*/
	@RequestMapping(value = "/professor", method= RequestMethod.DELETE)
	public Map<String, Object> delete(Model model,
			@RequestParam(value="profno", defaultValue="0")int profno) {
		/** 1) 필요한 변수값 생성*/
		// 이 값이 존재하지 않는다면 데이터 삭제가 불가능하므로 반드시 ㅍ
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
			if(profno==0) {
				return webHelper.getJsonWarning("교수번호가 없습니다.");
			}
				
		/** 2) 데이터 조회하기 */
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Professor input = new Professor();
		input.setProfno(profno);
				
		try {
			// 데이터 삭제
			professorService.deleteProfessor(input); // 데이터 삭제
		} catch (Exception e) {	
			return webHelper.getJsonError(e.getLocalizedMessage());
		}
		
		/** 3) 페이지 이동*/
		// 확인할 대상이 삭제된 상태이므로 목록 페이지로 이동
		return webHelper.getJsonData();
	}
}
