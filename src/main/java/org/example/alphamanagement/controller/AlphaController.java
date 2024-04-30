package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.service.AlphaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class AlphaController {
    private final AlphaService alphaService;
    private final HttpSession httpSession;

    public AlphaController(AlphaService wishyService, HttpSession httpSession) {
        this.alphaService = wishyService;
        this.httpSession = httpSession;
    }
}