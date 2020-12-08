package com.foxconn.fii.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
// @RequestMapping("/pl")
public class ViewController {

    @Value("${server.domain}")
    private String domain;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("path", "");
        model.addAttribute("title", "Home");
        return "application";
    }
}
