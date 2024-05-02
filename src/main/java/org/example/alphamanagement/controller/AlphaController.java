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

    @GetMapping("")
    public String getLogin(Model model){
        model.addAttribute("emp", new Emp());
        return "login";
    }

    @PostMapping("/submit-login")
    public String submitLogin(@ModelAttribute Emp emp, HttpSession httpSession) {
        if (alphaService.checkValidLogin(emp) != null) {
            httpSession.setAttribute("emp", emp);
            return "front-page";
        } else {
            return "redirect:/";
        }
    }
}