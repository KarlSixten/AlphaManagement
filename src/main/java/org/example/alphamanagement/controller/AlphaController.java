package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.model.Task;
import org.example.alphamanagement.service.AlphaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
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

    //---------------------------------------------------------------------------------------------------------------
    //LOGIN + LOGOUT
    //---------------------------------------------------------------------------------------------------------------

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
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        httpSession.invalidate();
        return "redirect:/";
    }

    //---------------------------------------------------------------------------------------------------------------
    //Home
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/home")
    public String getHome(Model model) {
        if (userIsLoggedIn()) {
            Emp loggedInEmp = (Emp) httpSession.getAttribute("empLoggedIn");
            model.addAttribute("jobType", loggedInEmp.getJobType());
            List<Project> projects = alphaService.getProjectsForEmp(loggedInEmp.getUsername());
            model.addAttribute("projects", projects);
            List<Task> tasks = alphaService.getTasksForEmp(loggedInEmp.getUsername());
            model.addAttribute("tasks", tasks);
            return "front_page";
        } else {
            return "redirect:/";
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    //CREATE EMP
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/create-emp")
    public String createEmp(Model model) {
        if (userIsLoggedIn()) {
            model.addAttribute("emp", new Emp());
            model.addAttribute("skillsList", alphaService.getSkillsList());
            return "create_emp";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/create-emp/submit")
    public String getSaveNewEmp(@ModelAttribute Emp emp, @RequestParam(value = "skills", required = false) ArrayList<String> skills) {
        if (alphaService.createEmpWithSkills(emp, skills) != null) {
            httpSession.setAttribute("newlyCreatedEmp", emp);
            return "redirect:/create-emp/submit/user-created-success";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/create-emp/submit/user-created-success")
    public String getCreateUserSuccess(Model model) {
        model.addAttribute("createdUser", httpSession.getAttribute("newlyCreatedEmp"));
        return "user_created_success";
    }

    //---------------------------------------------------------------------------------------------------------------
    //CREATE PROJECT
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/new")
    public String showCreateProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "create_project";
    }


    @PostMapping("/projects/new/submit")
    public String createProject(@ModelAttribute Project project) {
        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            alphaService.createProject(project, (Emp) httpSession.getAttribute("empLoggedIn"));
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    //UPDATE PROJECT
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/{projectID}/update")
    public String showUpdateProjectForm(@PathVariable int projectID, Model model) {
        Project project = alphaService.findProjectByID(projectID);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedStartDate = project.getStartDate().format(formatter);
        String formattedEndDate = project.getEndDate().format(formatter);

        model.addAttribute("formattedStartDate", formattedStartDate);
        model.addAttribute("formattedEndDate", formattedEndDate);

        if (userIsLoggedIn() && (userHasRole(2) || userHasRole(3))) {
            model.addAttribute("project", project);
            return "update_project";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/projects/{projectID}/update/submit")
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

    //---------------------------------------------------------------------------------------------------------------
    //DELETE PROJECT
    //---------------------------------------------------------------------------------------------------------------

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

    //---------------------------------------------------------------------------------------------------------------
    //EDIT EMPS
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/edit-emps")
    public String showEmployees(Model model, @RequestParam(required = false) String searchString) {
        if (searchString == null) {
            searchString = "";
        }
        List<Emp> foundEmps = alphaService.findByUsernameContaining(searchString);
        model.addAttribute("foundEmps", foundEmps);
        return "edit_emps";
    }

    @GetMapping("edit-emps/{username}/update-emp")
    public String updateEmpForm(@PathVariable("username") String username, Model model) {
        Emp emp = alphaService.findEmpByUsername(username);
        List<String> empSkills = alphaService.getEmpSkillList(username);
        List<String> allSkills = alphaService.getSkillsList();
        model.addAttribute("emp", emp);
        model.addAttribute("empSkills", empSkills);
        model.addAttribute("allSkills", allSkills);
        return "update_emp";
    }

    @PostMapping("edit-emps/{username}/update-emp/submit")
    public String updateEmp(@ModelAttribute Emp emp, @RequestParam(required = false, defaultValue = "") List<String> empSkills) {
        if (empSkills.isEmpty()) {
            alphaService.updateEmp(emp, new ArrayList<>());
        } else {
            alphaService.updateEmp(emp, empSkills);
        }
        return "redirect:/edit-emps";
    }

    @PostMapping("edit-emps/{username}/delete-emp")
    public String deleteEmp(@PathVariable("username") String username) {
        alphaService.deleteEmp(username);
        return "redirect:/edit-emps";
    }

    //---------------------------------------------------------------------------------------------------------------
    //CREATE SUBPROJECT
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("projects/{projectID}/create-subproject")
    public String showCreateSubproject(@PathVariable("projectID") int parentProjectID, Model model) {
        model.addAttribute("parentProjectID", parentProjectID);
        model.addAttribute("subProject", new Project());
        return "create_subproject";
    }

    @PostMapping("projects/{projectID}/create-subproject/submit")
    public String createSubProject(@PathVariable("projectID") int parentProjectID, @ModelAttribute Project subProject) {
        alphaService.createSubProject(parentProjectID, subProject);
        return "redirect:/projects/" + parentProjectID + "/subprojects";
    }

    //---------------------------------------------------------------------------------------------------------------
    //VIEW SUBPROJECTS
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("projects/{projectID}/subprojects")
    public String getSubProjects(@PathVariable("projectID") int parentProjectID, Model model) {
        Project parentProject = alphaService.findProjectByID(parentProjectID);
        model.addAttribute("parentProject", parentProject);
        model.addAttribute("subProjects", alphaService.getAllSubProjectsOfProject(parentProjectID));
        return "view_subproject";
    }

    //---------------------------------------------------------------------------------------------------------------
    //UPDATE EMPS FOR PROJECT
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/{projectID}/update-emps")
    public String showAddEmpToProjectForm(@PathVariable("projectID") int projectID, @RequestParam(required = false) String searchString, Model model) {
        if (searchString == null) {
            searchString = "";
        }
        List<Emp> empsOnProject = alphaService.getEmpsOnProject(projectID);
        List<Emp> empsToAdd = null;
        if (alphaService.findProjectByID(projectID).getParentProjectID() != 0) {
            empsToAdd = alphaService.findEmpsContainingInParentProject(searchString,projectID);
            model.addAttribute("empsOnProject", empsOnProject);
            model.addAttribute("empsToAdd", empsToAdd);
            model.addAttribute("projectID", projectID);
            model.addAttribute("project", alphaService.findProjectByID(projectID));
            model.addAttribute("parentProjectID", alphaService.findProjectByID(projectID).getParentProjectID());
            return "add_emp_to_subproject";
        }
        else {
            empsToAdd = alphaService.findByUsernameContainingNotOnProject(searchString, projectID);

            model.addAttribute("empsOnProject", empsOnProject);
            model.addAttribute("empsToAdd", empsToAdd);
            model.addAttribute("projectID", projectID);
            model.addAttribute("project", alphaService.findProjectByID(projectID));
            return "update_project_emps";
        }
    }

    @PostMapping("/projects/{projectID}/update-emps/add/{username}")
    public String addEmpToProject(@PathVariable("projectID") int projectID,
                                  @PathVariable("username") String username){
        alphaService.addEmpToProject(username, projectID);
        return "redirect:/projects/" + projectID + "/update-emps";
    }

    @PostMapping("/projects/{projectID}/update-emps/remove/{username}")
    public String removeEmpfromProject(@PathVariable("projectID") int projectID,
                                       @PathVariable("username") String username){
        alphaService.removeEmpFromProject(projectID, username);
        return "redirect:/projects/" + projectID + "/update-emps";
    }

    //---------------------------------------------------------------------------------------------------------------
    // VIEW ALL TASKS FOR SUBPROJECT
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/{projectID}/view-tasks")
    public String getAllTaskOfSubProject(@PathVariable("projectID") int projectID, Model model){
        Project project = alphaService.findProjectByID(projectID);
        model.addAttribute("project", project);
        model.addAttribute("tasks", alphaService.getAllTaskOfSubProject(projectID));
        model.addAttribute("totalEstimate", alphaService.sumOfEstimates(projectID));
        model.addAttribute("workProgressPercentage", alphaService.getWorkProgressPercentage(projectID));
        model.addAttribute("workRemainingHours", alphaService.getRemaningHoursOfWork(projectID));
        model.addAttribute("hoursPrDay", alphaService.hoursPrDayCalculator(projectID));
        return "view_all_tasks.html";
    }

    //---------------------------------------------------------------------------------------------------------------
    // CREATE TASK
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/{projectID}/view-tasks/new")
    public String showCreateTaskForm(@PathVariable("projectID") int projectID, Model model) {
        model.addAttribute("task", new Task());
            List<Project> subProjects = alphaService.getAllSubProjectsOfProject(projectID);
            model.addAttribute("subProjects", subProjects);
            model.addAttribute("categories", alphaService.getAllCategories());
            model.addAttribute("projectID", projectID);
            return "create_task.html";
        }

    @PostMapping("/projects/{projectID}/view-tasks/new/submit")
    public String createTask(@ModelAttribute("task") Task task, @PathVariable("projectID") int projectID){
        if (userIsLoggedIn()){
            Task savedTask = alphaService.createTask(task, projectID);
            return "redirect:/projects/" + projectID + "/view-tasks";
        } else {
            return "redirect:/";
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    // VIEW SINGLE TASK
    //---------------------------------------------------------------------------------------------------------------

    @GetMapping("/projects/{projectID}/view-tasks/{taskId}")
    public String viewTask(@PathVariable("taskId") int taskId,@PathVariable("projectID") int projectID, Model model){

        Task task = alphaService.findTaskById(taskId);
        List<Emp> empsOnTask = alphaService.getEmpsOnTask(taskId);
        List<Emp> empsToAdd = alphaService.getEmpsNotOnTask(taskId,projectID);
       model.addAttribute("task", task);
       model.addAttribute("projectID", projectID);
       model.addAttribute("empsOnTask", empsOnTask);
       model.addAttribute("empsToAdd", empsToAdd);

       return "view_task.html";
    }

    //---------------------------------------------------------------------------------------------------------------
    // DELETE TASK
    //---------------------------------------------------------------------------------------------------------------

    @PostMapping("/delete-task")
    public String deleteTask(@RequestParam("taskID") int taskID, @RequestParam("projectID") int projectID){
        alphaService.deleteTask(taskID);
        return "redirect:/projects/" + projectID + "/view-tasks";
    }



    @PostMapping("tasks/view/{projectID}/{taskId}/add/{username}")
    public String addEmpToTask(@PathVariable("projectID") int projectID,
                               @PathVariable("username") String username,
                               @PathVariable("taskId") int taskId){
        alphaService.addEmpToTask(username, taskId);
        return "redirect:/tasks/view/" + projectID + "/" + taskId;
    }

    @PostMapping("tasks/view/{projectID}/{taskId}/remove/{username}")
    public String removeEmpFromTask(@PathVariable("projectID") int projectID,
                                    @PathVariable("username") String username,
                                    @PathVariable("taskId") int taskId){

        alphaService.removeEmpFromTask(taskId, username);
        return "redirect:/tasks/view/" + projectID + "/" + taskId;
    }






    @PostMapping("tasks/view/{projectID}/{taskID}")
    public String toggleIsDone(@PathVariable ("projectID") int projectID, @PathVariable("taskID") int taskID){
        alphaService.toggleIsDone(alphaService.findTaskById(taskID).isDone(), taskID);
        return "redirect:/tasks/view/" + projectID + "/" + taskID;
    }


    @GetMapping("/projects/{projectID}/{taskID}/update")
    public String showUpdateTaskForm(@PathVariable("projectID") int projectID, @PathVariable("taskID") int taskID, Model model){
        Task task = alphaService.findTaskById(taskID);
        model.addAttribute("projectID", projectID);
        model.addAttribute("taskID", taskID);
        model.addAttribute("task", task);
        model.addAttribute("categories", alphaService.getAllCategories());
        return "update_task";
    }

    @PostMapping("/projects/{projectID}/{taskID}/progressmade")
    public String hoursDoneInput(@PathVariable("projectID") int projectID,
                                 @PathVariable("taskID") int taskID,
                                 @ModelAttribute("hoursDone") int hoursDone){
        alphaService.updatehoursDone(hoursDone, taskID);
        return "redirect:/tasks/view/" + projectID + "/" + taskID;
    }


    @PostMapping("/projects/{projectID}/{taskID}/update")
    public String updateTask(@ModelAttribute ("task") Task task, @PathVariable("projectID") int projectID, @PathVariable("taskID") int taskID){
        task.setTaskID(taskID);
        alphaService.updateTask(task);
        return "redirect:/all-task/view/" + projectID;
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

    private boolean userHasRole(int jobType) {
        Emp emp = (Emp) httpSession.getAttribute("empLoggedIn");
        return emp != null && emp.getJobType() == jobType;
    }
}