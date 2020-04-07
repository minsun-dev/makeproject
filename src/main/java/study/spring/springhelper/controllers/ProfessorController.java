package study.spring.springhelper.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import study.spring.springhelper.helper.PageData;
import study.spring.springhelper.helper.RegexHelper;
import study.spring.springhelper.helper.WebHelper;
import study.spring.springhelper.model.Department;
import study.spring.springhelper.model.Professor;
import study.spring.springhelper.service.DepartmentService;
import study.spring.springhelper.service.ProfessorService;

@Controller
public class ProfessorController {
	/** WebHelper 주입*/
	@Autowired
	WebHelper webHelper;
	
	/** RegexHelper 주입*/
	@Autowired RegexHelper regexHelper;
	
	/** Service 패턴 구현체 주입*/
	@Autowired ProfessorService professorService;
	
	// --> import study.spring.springhelper.service.DepartmentService;
	@Autowired DepartmentService departmentService;
	
	/** "/프로젝트이름"에 해당하는 ContextPath 변수 주입*/
	// --> import org.springframework.beans.factory.annotation.Value;
	@Value("#{servletContext.contextPath}")
	String contextPath;
	
	/** 목록 페이지*/
	@RequestMapping(value = "/professor/list.do", method = RequestMethod.GET)
	public ModelAndView list(Model model,
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
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) View 처리*/
		model.addAttribute("output", output);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageData", pageData);
		
		return new ModelAndView("professor/list");
	}
	
	/** 상세 페이지*/
	@RequestMapping(value = "/professor/view.do", method = RequestMethod.GET)
	public ModelAndView view(Model model,
			@RequestParam(value="profno", defaultValue="0")int profno) {
		/** 1) 필요한 변수 값 생성*/
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
		if(profno==0) {
			return webHelper.redirect(null,"교수번호가 없습니다.");
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
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) View 처리 */
		model.addAttribute("output", output);
		return new ModelAndView("professor/view");
	}
	
	/** 작성 폼 페이지*/
	@RequestMapping(value = "/professor/add.do", method = RequestMethod.GET)
	public ModelAndView add(Model model) {
		
		/** 학과 목록 조회하기*/
		// 조회결과를 저장할 객체 선언
		List<Department> output= null;
		
		try {
			// 데이터 조회 --> 검색조건 없이 모든 학과 조회
			output = departmentService.getDepartmentList(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// View에 추가
		model.addAttribute("output", output);
		
		return new ModelAndView("professor/add");
	}
	
	/** 작성 폼에 대한 action 페이지*/
	@RequestMapping(value = "/professor/add_ok.do", method = RequestMethod.POST)
	public ModelAndView add_ok(Model model,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="position", required=false)String position,
			@RequestParam(value="sal", required=false)int sal,
			@RequestParam(value="hiredate", required=false)String hiredate,
			@RequestParam(value="comm", required=false)int comm,
			@RequestParam(value="deptno", required=false)int deptno) {
		
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(name==null) {return webHelper.redirect(null, "교수 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.redirect(null, "교수 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.redirect(null, "교수 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.redirect(null, "교수 아이디는 영어와 숫자로만 가능합니다.");}
		if(position==null) {return webHelper.redirect(null, "직급을 입력하세요.");}
		if(sal==0) {return webHelper.redirect(null, "급여를 입력하세요.");}
		if(sal<0) {return webHelper.redirect(null, "급여는 0보다 작을 수 없습니다.");}
		if(hiredate==null) {return webHelper.redirect(null, "입사일을 입력하세요.");}
		if(comm<0) {return webHelper.redirect(null, "보직 수당은 0보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.redirect(null, "소속 교수 번호를 입력하세요.");}
		
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
		
		try {
			// 데이터저장
			// --> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 PK값이 저장된다.
			professorService.addProfessor(input);
		}catch(Exception e) {
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 페이지 이동*/
		// 저장결과를 확인하기 위해서 데이터 저장시 생성된 PK 값을 상세 페이지로 전달해야 한다.
		String redirectUrl = contextPath + "/professor/view.do?profno=" + input.getProfno();
		return webHelper.redirect(redirectUrl, "저장되었습니다.");
		
	}
	
	/** 수정 폼 페이지*/
	@RequestMapping(value = "/professor/edit.do", method = RequestMethod.GET)
	public ModelAndView edit(Model model,
			@RequestParam(value="profno", defaultValue="0")int profno) {
		/** 1) 필요한 변수값 생성*/
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
			if(profno==0) {
				return webHelper.redirect(null,"교수번호가 없습니다.");
			}
		/** 2) 데이터 조회하기*/
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Professor input = new Professor();
		input.setProfno(profno);
		
		// 교수 조회결과를 저장할 객체 선언
		Professor output = null;
		
		// 학과목록을 저정할 객체 선언
		List<Department> deptList = null;
		
		try {
			// 교수 기본 정보 조회
			output = professorService.getProfessorItem(input); 
			// 드롭다운을 위한 학과목록 조회
			deptList = departmentService.getDepartmentList(null);
			} catch (Exception e) {	
				return webHelper.redirect(null, e.getLocalizedMessage());
			}		
		/** 3) View 처리*/
		model.addAttribute("output", output);
		model.addAttribute("deptList", deptList);
		return new ModelAndView("professor/edit");
		
	}
	

	/** 수정 폼에 대한 action 페이지*/
	@RequestMapping(value = "/professor/edit_ok.do", method = RequestMethod.POST)
	public ModelAndView edit_ok(Model model,
			@RequestParam(value="profno", defaultValue="0", required=false)int profno,
			@RequestParam(value="name", required=false)String name,
			@RequestParam(value="userid", required=false)String userid,
			@RequestParam(value="position", required=false)String position,
			@RequestParam(value="sal", required=false)int sal,
			@RequestParam(value="hiredate", required=false)String hiredate,
			@RequestParam(value="comm", required=false)int comm,
			@RequestParam(value="deptno", required=false)int deptno) {
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(profno==0) {return webHelper.redirect(null, "교수번호가 없습니다.");}
		if(name==null) {return webHelper.redirect(null, "교수 이름을 입력하세요.");}
		if(!regexHelper.isKor(name)) {return webHelper.redirect(null, "교수 이름은 한글만 가능합니다.");}
		if(userid==null) {return webHelper.redirect(null, "교수 아이디를 입력하세요");}
		if(!regexHelper.isEngNum(userid)) {return webHelper.redirect(null, "교수 아이디는 영어와 숫자로만 가능합니다.");}
		if(position==null) {return webHelper.redirect(null, "직급을 입력하세요.");}
		if(sal==0) {return webHelper.redirect(null, "급여를 입력하세요.");}
		if(sal<0) {return webHelper.redirect(null, "급여는 0보다 작을 수 없습니다.");}
		if(hiredate==null) {return webHelper.redirect(null, "입사일을 입력하세요.");}
		if(comm<0) {return webHelper.redirect(null, "보직 수당은 0보다 작을 수 없습니다.");}
		if(deptno==0) {return webHelper.redirect(null, "소속 교수 번호를 입력하세요.");}
		
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
		
		try {
			// 데이터수정
			// --> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 PK값이 저장된다.
			professorService.editProfessor(input);
		}catch(Exception e) {
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 페이지 이동*/
		// 수정한 대상을 상세페이지에 알려주기 위해서 PK값을 수정해야 한다.
		String rediectUrl = contextPath + "/professor/view.do?profno=" + input.getProfno();
		return webHelper.redirect(rediectUrl, "수정되었습니다.");
		
	}
	
	/** 삭제 처리*/
	@RequestMapping(value = "/professor/delete_ok.do", method = RequestMethod.GET)
	public ModelAndView delete_ok(Model model,
			@RequestParam(value="profno", defaultValue="0")int profno) {
		/** 1) 필요한 변수값 생성*/
		// 이 값이 존재하지 않는다면 데이터 삭제가 불가능하므로 반드시 ㅍ
		
		// 이 값이 존재하지 않는다면 조회가 불가능하므로 반드시 필수값으로 처리해야한다.
			if(profno==0) {
				return webHelper.redirect(null,"교수번호가 없습니다.");
			}
				
		/** 2) 데이터 조회하기 */
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Professor input = new Professor();
		input.setProfno(profno);
				
		try {
			// 데이터 삭제
			professorService.deleteProfessor(input); // 데이터 삭제
		} catch (Exception e) {	
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) 페이지 이동*/
		// 확인할 대상이 삭제된 상태이므로 목록 페이지로 이동
		return webHelper.redirect(contextPath + "/professor/list.do", "삭제되었습니다.");
	}
}
