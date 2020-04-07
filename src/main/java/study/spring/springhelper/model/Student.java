/**
 * @filename    : Student.java
 * @description : 학생 테이블 구조와 매핑되는 Pojo 클래스
 * @author      : 김민선 (akrend0433@gmail.com)
 */
package study.spring.springhelper.model;

import lombok.Data;

@Data
public class Student {
	// 1) 기본 컬럼
	private int studno;
	private String name;
	private String userid;
	private int grade;
	private String idnum;
	private String birthdate;
	private String tel;
	private int height;
	private int weight;
	private int deptno;
	private int profno;
	
	// 2) JOIN 절에 따른 추가 칼럼
	/** 소속학과 이름 (department 테이블과의 join) */// department join
	private String dname;
	/** 담당교수 이름 (professor 테이블과의 join) */// professor join
	private String pname;

	// 3) 페이지 구현을 위한 static 변수
	private static int offset; // LIMIT 절에서 사용할 조회 시작 위치
	private static int listCount; // LIMIT 절에서 사용할 조회할 데이터 수
	
	public static int getOffset() {
		return offset;
	}
	public static void setOffset(int offset) {
		Student.offset = offset;
	}
	public static int getListCount() {
		return listCount;
	}
	public static void setListCount(int listCount) {
		Student.listCount = listCount;
	}
	
}
