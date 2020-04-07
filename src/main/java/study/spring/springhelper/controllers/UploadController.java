package study.spring.springhelper.controllers;

import java.io.File;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import study.spring.springhelper.helper.UploadItem;
import study.spring.springhelper.helper.WebHelper;

@Controller
public class UploadController {
	@Autowired
	WebHelper webHelper;
	
	/**업로드 폼을 구성하는 페이지*/
	@RequestMapping(value="/upload/upload.do", method=RequestMethod.GET)
	public String upload() {
		return "upload/upload";
	}
	
	/** 업로드 폼에 대한 action 페이지*/
	@RequestMapping(value = "/upload/upload_ok.do", method=RequestMethod.POST)
	public ModelAndView uploadOK(Model model, @RequestParam String subject,
			@RequestParam MultipartFile photo) {
		/**1) 업로드 파일 저장하기 */
		// 업로드 된 파일이 존재하는지 확인한다.
		if(photo.getOriginalFilename().isEmpty()) {
			return webHelper.redirect(null, "업로드 된 파일이 없습니다.");
		}
		
		// 업로드 된 파일이 저장될 경로 정보를 생성한다.
		File targetFile = new File(webHelper.getUploadDir(), photo.getOriginalFilename());
		
		// 업로드 된 파일을 지정된 경로로 복사한다.
		
			try {
				photo.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
				return webHelper.redirect(null, "업로드 된 파일 저장에 실패했습니다.");
			}
		
		/**2) 업로드 경로 정보 처리하기*/
		// 복사된 파일의 절대 경로를 추출한다.
		// -> 운영체제 호환을 위해 역 슬래시를 슬래시로 변환한다.
		String absPath = targetFile.getAbsolutePath().replace("\\","/");
		
		// 절대경로에서 이미 root-context에 지정되어 있는 업로드 폴더 경로를 삭제한다.
		String filePath = absPath.replace(webHelper.getUploadDir(), "");
		
		/**3) 썸네일 이미지 생성하기*/
		// 업로드 결과로부터 썸네일 이미지를 생성한다.
		String thumbnail = null;
		
		try {
			thumbnail= webHelper.createThumbnail(filePath, 320, 320, true);
		} catch (Exception e) {
			
			e.printStackTrace();
			return webHelper.redirect(null, "썸네일 생성에 실패했습니다.");
		}
		/**4) 업로드 결과를 Beans에 저장*/
		UploadItem item = new UploadItem();
		item.setContentType(photo.getContentType());
		item.setFieldName(photo.getName());
		item.setFileSize(photo.getSize());
		item.setOrginName(photo.getOriginalFilename());
		
		// 업로드 경로와 썸네일 경로는 웹 상에서 접근 가능한 경로 문자열로 변환하여 Beans에 추가한다.
		item.setFilePath(webHelper.getUploadPath(filePath));
		item.setThumbnail(webHelper.getUploadPath(thumbnail));
		
		/**5) View 처리*/
		// 텍스트 정보를 View로 전달한다.
		model.addAttribute("subject", subject);
		// 업로드 정보를 View로 전달한다.
		model.addAttribute("item", item);
		
		// 뷰 호출 -> 메서드 리턴 타입이 "ModelAndView"인 경우 View의 호출이 ModelAndView 객체가 된다.
		return new ModelAndView("upload/upload_ok");
	}
	
	/** WebHelper를 활용하는 업로드 처리를 위한 <form>을 구성하는 페이지 */
	@RequestMapping(value = "/upload/use_helper.do", method = RequestMethod.GET)
	public String useHelper() {
		return "upload/use_helper";
	}
	
	/** WebHelper를 활용하는 업로드 처리에 대한 action 페이지 */
	@RequestMapping(value = "/upload/use_helper_ok.do", method = RequestMethod.POST)
	public ModelAndView useHelperOk(Model model, @RequestParam(required=false) MultipartFile photo) {
		
		/** 1) 업로드 처리*/
		UploadItem item = null;
	
		try {
			item = webHelper.saveMultipartFile(photo);
		} catch (NullPointerException e) {			
			e.printStackTrace();
			return webHelper.redirect(null, "업로드 된 파일이 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return webHelper.redirect(null, "업로드에 실패했습니다.");
		}
		
		
		/** 2) 썸네일 생성*/
		String thumbnail = null;
		
		if(item.getContentType().indexOf("image")>-1) {
			try {
				thumbnail = webHelper.createThumbnail(item.getFilePath(), 240, 240, true);
			} catch (Exception e) {
				e.printStackTrace();
				return webHelper.redirect(null, "썸네일 생성에 실패했습니다.");
			}
		}
		/** 3) View 처리*/
		// webHelper에 의해 생성된 업로드 경로와 썸네일 경로는 서버상의 위치일 뿐 웹상에 노출될 수 있는 형태는 아니다.
		// View를 통해 웹 상에 노출하기 위해서는 업로드 위치의 URL PATH를 덧 붙인 형태로 경로를 가공해야 한다.
		String filePath = webHelper.getUploadPath(item.getFilePath());
		String thumbnailPath = webHelper.getUploadPath(thumbnail);
		item.setFilePath(filePath);
		item.setThumbnail(thumbnailPath);
		
		// 업로드 정보를 View로 전달한다.
		model.addAttribute("item", item);
		
		// 뷰 호출 --> 메서드 리턴 타입이 "ModelAndView"인 경우 View의 호출이 ModelAndView 객체가 된다.
		return new ModelAndView("upload/use_helper_ok");		
	}
	
	/** WebHelper를 활용하는 업로드 처리를 위한 <form>을 구성하는 페이지 */
	@RequestMapping(value = "/upload/multiple.do", method = RequestMethod.GET)
	public String mutiple() {
		return "upload/multiple";
	}
	
	/** 다중 업로드 처리에 대한 action 페이지*/
	@RequestMapping(value = "/upload/multiple_ok.do", method = RequestMethod.POST)
	public ModelAndView multipleOk(Model model, @RequestParam(required=false) MultipartFile[] photo) {
		
		/** 1) 업로드 처리*/
		List<UploadItem> list = null;
		
		try {
			list = webHelper.saveMultipartFile(photo);
		} catch (NullPointerException e) {			
			e.printStackTrace();
			return webHelper.redirect(null, "업로드 된 파일이 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return webHelper.redirect(null, "업로드에 실패했습니다.");
		}
		
		/** 2) 업로드 된 항목 수 만큼 반복을 수행하면서 원본 파일 경로와 썸네일 이미지의 경로를 설정한다.*/
		for(int i=0; i<list.size(); i++) {
			UploadItem item = list.get(i);
			String thumbnail = null;
			
			if(item.getContentType().indexOf("image")>-1) {
				try {
					thumbnail = webHelper.createThumbnail(item.getFilePath(), 240, 240, true);
				} catch (Exception e) {
					e.printStackTrace();
					return webHelper.redirect(null, "섬네일 생성에 실패했습니다.");
				}
			}
			
			String filePath = webHelper.getUploadPath(item.getFilePath());
			String thumbnailPath = webHelper.getUploadPath(thumbnail);
			item.setFilePath(filePath);
			item.setThumbnail(thumbnailPath);
			
		}
		
		/** 3) View 처리*/
		// 업로드 정보를 View로 전달한다.
		model.addAttribute("list", list);
		
		// 뷰 호출 --> 메서드 리턴 타입이 "ModelAndView"인 경우 View의 호출이 ModelAndView 객체가 된다.
				return new ModelAndView("upload/multiple_ok");	
	}
	 
}
