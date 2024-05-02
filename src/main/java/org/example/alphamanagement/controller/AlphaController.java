package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.service.AlphaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class AlphaController {
    private final AlphaService alphaService;
    private final HttpSession httpSession;

    public AlphaController(AlphaService wishyService, HttpSession httpSession) {
        this.alphaService = wishyService;
        this.httpSession = httpSession;
    }

    @GetMapping("/create-emp")
    public String createEmp (Model model) {
        model.addAttribute("emp", new Emp());
        return "create_emp";
    }

    @PostMapping("/submit-create-emp")
    public String getSaveNewEmp(@ModelAttribute Emp emp) {
        if (alphaService.createEmp(emp) != null) {
            return "redirect:/create-emp";
        } else {
            return "TEST_create_emp_success";
        }
    }

    @GetMapping("")
    public String getLogin(Model model){
        model.addAttribute("emp", new Emp());
        return "login";
    }

    @PostMapping("/submit-login")
    public String submitLogin(@ModelAttribute Emp emp, HttpSession httpSession) {
        if (alphaService.checkValidLogin(emp) != null) {
            httpSession.setAttribute("emp", emp);
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/home")
    public String getHome() {
        if (httpSession.getAttribute("emp") != null) {
            return "front-page";
        } else {
            return "redirect:/";
        }
    }
}