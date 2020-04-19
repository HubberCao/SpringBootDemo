package com.sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by admin on 2020/4/2.
 */
//@Controller
public class TempController {

    @RequestMapping
    public ModelAndView test() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("title", "Sample title").addObject("body",
                "Sample body");
        mav.setViewName("template.html");
        return mav;
    }
}
