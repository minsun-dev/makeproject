package study.spring.springhelper.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import study.spring.springhelper.helper.MailHelper;
import study.spring.springhelper.helper.RegexHelper;
import study.spring.springhelper.helper.RetrofitHelper;
import study.spring.springhelper.helper.WebHelper;

@Controller
public class HomeController {

    // --> import study.spring.springhelper.helper.WebHelper;
    @Autowired
    WebHelper webHelper;
    
    // --> import study.spring.springhelper.helper.RegexHelper;
    @Autowired
    RegexHelper regexHelper;
    
    // --> import study.spring.springhelper.helper.MailHelper;
    @Autowired
    MailHelper mailHelper;
    
    // --> import study.spring.springhelper.helper.RetrofitHelper;
    @Autowired
    RetrofitHelper retrofitHelper;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        return "home";
    }

}
