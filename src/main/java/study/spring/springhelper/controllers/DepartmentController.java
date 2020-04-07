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
import study.spring.springhelper.service.DepartmentService;

@Controller
public class DepartmentController {
	/** WebHelper 주입*/
	@Autowired
	WebHelper webHelper;
	
	/** RegexHelper 주입*/
	@Autowired RegexHelper regexHelper;
	
	/** Service 패턴 구현체 주입*/
	@Autowired DepartmentService departmentService;
	
	/** "/프로젝트이름"에 해당하는 ContextPath 변수 주입*/
	// -> import org.springframework.beans.factory.annotation.Value;
	@Value("#{servletContext.contextPath}")
	String contextPath;
	
	/** 목록 페이지*/
	@RequestMapping(value="/department/list.do", method=RequestMethod.GET)
	public ModelAndView list(Model model,
			@RequestParam(value="keyword", defaultValue="", required=false)String keyword,
			@RequestParam(value="page", defaultValue="1", required=false)int nowPage,
			@RequestParam(value="totalCount", defaultValue="0", required=false)int totalCount,
			@RequestParam(value="listCount", defaultValue="10", required=false)int listCount,
			@RequestParam(value="pageCount", defaultValue="5", required=false)int pageCount) {
		/** 1) 필요한 변수 값 생성*/
		
		/** 2) 데이터 조회하기*/
		// 조회에 필요한 조건값(검색어)를 Beans에 담는다.
		Department input = new Department();
		input.setDname(keyword);
		input.setLoc(keyword);
		
		List<Department> output = null; // 조회결과가 저장될 객체
		PageData pageData = null; 		// 페이지 번호를 계산한 결과가 저장될 객체
		
		try {
			// 전체 게시글 수 조회
			totalCount = departmentService.getDepartmentCount(input);
			// 페이지 번호 계산 -> 계산 결과를 로그로 출력할 것이다.
			pageData = new PageData(nowPage, totalCount, listCount, pageCount);
			
			// SQL 의 LIMIT 절에서 사용될 값을 Beans의 static 변수에 저장
			Department.setOffset(pageData.getOffset());
			Department.setListCount(pageData.getListCount());
			
			// 데이터 조회하기
			output = departmentService.getDepartmentList(input);
		} catch (Exception e) {
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) Veiw 처리*/
		model.addAttribute("keyword", keyword);
		model.addAttribute("output", output);
		model.addAttribute("pageData", pageData);
		
		String viewPath = "department/list";
		return new ModelAndView(viewPath);
	}
	
	/** 상세페이지*/
	@RequestMapping(value="/department/view.do", method=RequestMethod.GET)
	public ModelAndView view(Model model, 
			@RequestParam(value="deptno", defaultValue="0", required=false)int deptno) {
		/** 1) 필요한 변수 값 생성*/
		// 조회할 대상에 대한 PK 값  @RequestParam(value="deptno", defaultValue="0", required=false)int deptno
		
		// 이 값이 존재하지 않는다면 데이터 조회가 불가능하므로 반드시 필수 값으로 처리해야 한다.
		if(deptno==0) {
			return webHelper.redirect(null, "학과번호가 없습니다.");
		}
		
		/** 2) 데이터 조회하기*/
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Department input = new Department();
		input.setDeptno(deptno);
		
		// 조회결과를 저장할 객체 선언
		Department output = null;
		
		try {
			// 데이터 조회
			output = departmentService.getDepartmentItem(input);
		} catch (Exception e) {

			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) View 처리*/
		model.addAttribute("output", output);
		return new ModelAndView("department/view");
	}
	
	/** 작성 폼 페이지 */
	@RequestMapping(value="/department/add.do", method=RequestMethod.GET)
	public ModelAndView add(Model model) {
		return new ModelAndView("department/add");
	}
	
	/** 작성 폼에 대한 action 페이지*/
	@RequestMapping(value="/department/add_ok.do", method=RequestMethod.POST)
	public ModelAndView add_ok(Model model, 
			@RequestParam(value="dname", required=false)String dname,
			@RequestParam(value="loc", required=false)String loc ) {
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사 */
		
		// 학과 이름은 필수 항목이므로 입력 여부를 검사
		// 위치는 미필수(null) 허용 이므로 입력여부를 검사하지 않는다.
		if(dname==null) {
			return webHelper.redirect(null, "학과이름을 입력하세요.");
		}
		
		if(!regexHelper.isKor(dname)) {
			return webHelper.redirect(null, "학과 이름은 한글만 가능합니다.");
		}
		/** 2) 데이터 저장하기*/
		Department input = new Department();
		input.setDname(dname);
		input.setLoc(loc);
		
		try {
			// 데이터 저장
			// -> 데이터 저장에 성공하면 파라미터로 전달하는 input 객체에 pk 값이 적용된다.
			departmentService.addDepartment(input);
		} catch (Exception e) {
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		/** 3) 결과를 확인하기 위한 페이지 이동*/
		// 저장결과를 확인하기 위해서 데이터 저장시 생성된 pK값을 상세 페이지로 전달해야 한다.
		String redirectUrl = contextPath+"/department/view.do?deptno=" + input.getDeptno();
		return webHelper.redirect(redirectUrl, "저장되었습니다.");
	}
	
	/** 수정 폼 페이지*/
	@RequestMapping(value="/department/edit.do", method=RequestMethod.GET)
	public ModelAndView edit(Model model,
			@RequestParam(value="deptno", required=false)int deptno) {
		/** 1) 필요한 변수 값 생성*/
		
		// 이 값이 존재하지 않는다면 데이터 조회가 불가능 하므로 반드시 필수값으로 처리해야 한다.
		if(deptno==0) {
			return webHelper.redirect(null, "학과번호가 없습니다.");
		}
		
		/** 2) 데이터 조회하기*/
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Department input = new Department();
		input.setDeptno(deptno);
		
		// 조회결과를 저장할 객체 선언
		Department output=null;
		
			try {
				output = departmentService.getDepartmentItem(input);
			} catch (Exception e) {				
				return webHelper.redirect(null, e.getLocalizedMessage());
			}
			
		/** 3) Veiw 처리*/
		model.addAttribute("output", output);
		return new ModelAndView("department/edit");
		
	}
	
	/** 수정 폼에 대한 action 페이지*/
	@RequestMapping(value="/department/edit_ok.do", method=RequestMethod.POST)
	public ModelAndView edit_ok(Model model,
			@RequestParam(value="deptno", defaultValue="0", required=false)int deptno,
			@RequestParam(value="dname", defaultValue="", required=false)String dname,
			@RequestParam(value="loc", defaultValue="", required=false)String loc) {
		/** 1) 사용자가 입력한 파라미터 수신 및 유효성 검사*/
		if(deptno ==0){
			return webHelper.redirect(null,  "학과번호가 없습니다.");
		}
		
		if(dname ==null){
			return webHelper.redirect(null, "학과이름을 입력하세요."); 				
		}
		
		/** 2) 데이터 수정하기*/
		// 수정할 값들을 Beans에 담는다.
		Department input = new Department();
		input.setDeptno(deptno);
		input.setDname(dname);
		input.setLoc(loc);
		
		try {
			// 데이터 수정
			departmentService.editDepartment(input);
		} catch (Exception e) {
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) 결과를 확인하기 위한 페이지 이동*/
		// 수정한 대상을 상세페이지에 알려주기 위해서 PK값을 전달해야 한다.
		String redirectUrl = contextPath + "/department/view.do?deptno="+input.getDeptno();
		return webHelper.redirect(redirectUrl, "수정되었습니다.");
		
	}
	
	@RequestMapping(value="/department/delete_ok.do", method=RequestMethod.GET)
	public ModelAndView delete_ok(Model model,
			@RequestParam(name="deptno", defaultValue="0", required=false)int deptno) {
		/** 1) 필요한 변수값 생성*/
		
		// 이 값이 존재하지 않는다면 데이터 조회가 불가능 하므로 반드시 필수값으로 처리해야 한다.
		if(deptno==0) {
			return webHelper.redirect(null, "학과번호가 없습니다.");
		}
				
		/** 2) 데이터 조회하기*/
		// 데이터 조회에 필요한 조건값을 Beans에 저장하기
		Department input = new Department();
		input.setDeptno(deptno);
				
				
		try {
			departmentService.deleteDepartment(input);
		} catch (Exception e) {				
			return webHelper.redirect(null, e.getLocalizedMessage());
		}
		
		/** 3) 페이지 이동*/
		// 확인할 대상이 삭제된 상태이므로 목록 페이지로 이동
		return webHelper.redirect(contextPath + "/department/list.do", "삭제되었습니다.");
		
	}
}
