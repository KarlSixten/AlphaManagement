package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.service.AlphaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public String createEmp(Model model) {
        if (userIsLoggedIn()) {
            model.addAttribute("emp", new Emp());
            model.addAttribute("skillsList", alphaService.getSkillsList()); // Add the skills list to the model
            return "create_emp";
        } else {
            return "redirect:/";
        }
    }


    @PostMapping("/submit-create-emp")
    public String getSaveNewEmp(@ModelAttribute Emp emp, @RequestParam(value = "skills", required = false) ArrayList<String> skills) {
        if (alphaService.createEmpWithSkills(emp, skills) != null) {
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
    public String getLogin(Model model) {
        model.addAttribute("empUsername", new String());
        model.addAttribute("empPassword", new String());
        return "login";
    }

    @PostMapping("/submit-login")
    public String submitLogin(@ModelAttribute("empUsername") String empUsername, @ModelAttribute("empPassword") String empPassword, HttpSession httpSession) {
        Emp empFromLogin = alphaService.checkValidLogin(empUsername, empPassword);
        if (empFromLogin != null) {
            httpSession.setAttribute("empLoggedIn", empFromLogin);
            Emp empLoggedIn = (Emp) httpSession.getAttribute("empLoggedIn");
            System.out.println(empLoggedIn.getJobType());
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/home")
    public String getHome(Model model) {
        if (userIsLoggedIn()) {
            Emp loggedInEmp = (Emp) httpSession.getAttribute("empLoggedIn");
            model.addAttribute("jobType", loggedInEmp.getJobType());
            List<Project> projects = alphaService.getAllProjects();
            model.addAttribute("projects", projects);
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


    @PostMapping("/projects/new/submit")
    public String createProject(@ModelAttribute Project project) {
        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            alphaService.createProject(project);
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/projects/{projectID}/update")
    public String showUpdateProjectForm(@PathVariable int projectID, Model model) {
        Project project = alphaService.findProjectByID(projectID);
        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            model.addAttribute("project", project);
            return "update-project";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/projects/{projectID}/update")
    public String updateProject(@ModelAttribute("project") Project project, @PathVariable int projectID) {
        project.setProjectID(projectID);
        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            alphaService.updateProject(project);
            if (project.getParentProjectID() > 0) {
                return "redirect:/projects/" + project.getParentProjectID() + "/subprojects";
            } else {
                return "redirect:/home";
            }
        } else {
            return "redirect:/home";
        }
    }



    @GetMapping("/projects/{projectID}/delete")
    public String deleteProject(@PathVariable int projectID) {
        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            Project project = alphaService.findProjectByID(projectID);
            if (project == null) {
                return "redirect:/";
            }
            alphaService.deleteProject(projectID);
            if (project.getParentProjectID() > 0) {
                return "redirect:/projects/" + project.getParentProjectID() + "/subprojects";
            } else {
                return "redirect:/home";
            }
        } else {
            return "redirect:/";
        }
    }


    private boolean userHasRole(int jobType) {
        Emp emp = (Emp) httpSession.getAttribute("empLoggedIn");
        return emp != null && emp.getJobType() == jobType;
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

    @PostMapping("find-user/{username}/delete-emp")
    public String deleteEmp(@PathVariable("username") String username) {
        alphaService.deleteEmp(username);
        return "redirect:/find-user";
    }

    @GetMapping("find-user/{username}/update-emp")
    public String updateEmpForm(@PathVariable("username") String username, Model model) {
        Emp emp = alphaService.findEmpByUsername(username);
        List<String> empSkills = alphaService.getEmpSkillList(username);
        List<String> allSkills = alphaService.getSkillsList();
        model.addAttribute("emp", emp);
        model.addAttribute("empSkills", empSkills);
        model.addAttribute("allSkills", allSkills);
        return "update-emp";
    }

    @PostMapping("/updateEmp")
    public String updateEmp(@ModelAttribute Emp emp, @RequestParam(required = false, defaultValue = "") List<String> empSkills) {
        if (empSkills.isEmpty()) {
            alphaService.updateEmp(emp, new ArrayList<>());
        } else {
            alphaService.updateEmp(emp, empSkills);
        }
        return "redirect:/find-user";
    }

    @GetMapping("projects/{projectID}/create-subproject")
    public String showCreateSubproject(@PathVariable("projectID") int parentProjectID, Model model) {
        model.addAttribute("parentProjectID", parentProjectID);
        model.addAttribute("subProject", new Project());
        return "create_subProject";
    }

    @PostMapping("projects/{projectID}/create-subproject")
    public String createSubProject(@PathVariable("projectID") int parentProjectID, @ModelAttribute Project subProject) {
        alphaService.createSubProject(parentProjectID, subProject);
        return "redirect:/projects/" + parentProjectID + "/subprojects"; // Redirect to the specific subproject view
    }


    @GetMapping("projects/{projectID}/subprojects")
    public String getSubProjects(@PathVariable("projectID") int parentProjectID, Model model) {
        Project parentProject = alphaService.findProjectByID(parentProjectID);
        model.addAttribute("parentProjectID", parentProjectID);
        model.addAttribute("parentProjectName", parentProject.getProjectName());
        model.addAttribute("subProjects", alphaService.getAllSubProjectsOfProject(parentProjectID));
        return "subProject-view-page";
    }

    @GetMapping("/{projectID}/add")
    public String showAddEmpToProjectForm(@PathVariable("projectID") int projectID, @RequestParam(required = false) String searchString, Model model) {
        if (searchString == null) {
            searchString = "";
        }
        List<Emp> foundEmps = alphaService.findByUsernameContaining(searchString);
        model.addAttribute("foundEmps", foundEmps);
        model.addAttribute("projectID", projectID);
        return "addEmpToProject";
    }

    @PostMapping("/{projectID}/add/{username}")
    public String addEmpToProject(@PathVariable("projectID") int projectID,
                                  @PathVariable("username") String username){
        alphaService.addEmpToProject(username, projectID);
        return "redirect:/home";
    }

    //---------------------------------------------------------------------------------------------------------------
    //HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER
    //---------------------------------------------------------------------------------------------------------------

    private boolean userIsLoggedIn() {
        return httpSession.getAttribute("empLoggedIn") != null;
    }

    private boolean userIsProjectManager() {
        Emp empLoggedIn = (Emp) httpSession.getAttribute("empLoggedIn");
        return empLoggedIn.getJobType() == 2;
    }

    private boolean userIsSystemAdmin() {
        Emp empLoggedIn = (Emp) httpSession.getAttribute("empLoggedIn");
        return empLoggedIn.getJobType() == 3;
    }
}