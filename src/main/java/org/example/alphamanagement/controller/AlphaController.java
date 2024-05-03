package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.service.AlphaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (userIsLoggedIn()) {
            model.addAttribute("emp", new Emp());
            return "create_emp";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/submit-create-emp")
    public String getSaveNewEmp(@ModelAttribute Emp emp) {
        if (alphaService.createEmp(emp) != null) {
            httpSession.setAttribute("newlyCreatedEmp", emp);
            return "redirect:/user-created-success";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/user-created-success")
    public String getCreateUserSuccess(Model model) {
        model.addAttribute("createdUser", httpSession.getAttribute("newlyCreatedEmp"));
        return "user_created_success";
    }

    @GetMapping("")
    public String getLogin(Model model){
        model.addAttribute("emp", new Emp());
        return "login";
    }

    @PostMapping("/submit-login")
    public String submitLogin(@ModelAttribute Emp emp, HttpSession httpSession) {
        if (alphaService.checkValidLogin(emp) != null) {
            httpSession.setAttribute("empLoggedIn", emp);
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/home")
    public String getHome() {
        if (userIsLoggedIn()) {
            return "front-page";
        } else {
            return "redirect:/";
        }
    }
    @GetMapping("/projects/new")
    public String showCreateProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "createProject";
    }

    //Denne skal måske postmappe til /projects/new/submit
    //Dette kan så videre redirecte til siden for det nye projekt eller Home
    @PostMapping("/projects")
    public String createProject(@ModelAttribute Project project) {
        alphaService.createProject(project);
        return "redirect:/projectView";
    }

    @GetMapping("/find-user")
    public String showEmployees(Model model, @RequestParam(required = false) String searchString) {
        if (searchString == null) {
            searchString = "";
        }
        List<Emp> foundEmps = alphaService.findByUsernameContaining(searchString);
        model.addAttribute("foundEmps", foundEmps);
        return "find_user";
    }

    @PostMapping("")
    public String deleteEmp(@PathVariable String username){
        alphaService.deleteEmp(username);
        return "";
    }

    private boolean userIsLoggedIn() {
        return httpSession.getAttribute("empLoggedIn") != null;
    }
}