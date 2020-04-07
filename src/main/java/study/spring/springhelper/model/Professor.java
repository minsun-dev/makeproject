package study.spring.springhelper.model;

import lombok.Data;

@Data
public class Professor {
	// 1) 기본 컬럼
	private int profno;
	private String name;
	private String userid;
	private String position;
	private int sal;
	private String hiredate;
	private Integer comm;
	private int deptno;
	
	// 2) JOIN 절에 따른 추가 칼럼
	/** 소속학과 이름 (department 테이블과의 join) */
	private String dname;
	/** 소속학과 위치 (department 테이블과의 join) */
	private String loc;
	
	// 3) 페이지 구현을 위한 static 변수
	/** LIMIT 절에서 사용할 조회 시작 위치*/
	private static int offset;
	
	/** LIMIT 절에서 사용할 조회할 데이터 수 */
	private static int listCount;

	public static int getOffset() {
		return offset;
	}

	public static void setOffset(int offset) {
		Professor.offset = offset;
	}

	public static int getListCount() {
		return listCount;
	}

	public static void setListCount(int listCount) {
		Professor.listCount = listCount;
	}
	
}
