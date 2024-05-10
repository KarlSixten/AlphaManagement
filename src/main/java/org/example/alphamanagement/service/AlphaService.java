package org.example.alphamanagement.service;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.model.Task;
import org.example.alphamanagement.repository.AlphaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlphaService {
    private final AlphaRepository alphaRepository;

    public AlphaService(AlphaRepository alphaRepository) {
        this.alphaRepository = alphaRepository;
    }




    public Emp checkValidLogin(String empUsername, String empPassword) {
        return alphaRepository.checkValidLogin(empUsername, empPassword);
    }
    public Project createProject(Project newProject, Emp projectCreatorEmp){
        return alphaRepository.createProject(newProject, projectCreatorEmp);
    }

    public Emp findEmpByUsername(String username) {
        return alphaRepository.findEmpByUsername(username);
    }
    public void deleteEmp(String username){
        alphaRepository.deleteEmp(username);
    }
    public List<Project> getAllProjects(){
        return alphaRepository.getAllProjects();
    }

    public Emp updateEmp(Emp emp, List<String> empSkills){
        return alphaRepository.updateEmp(emp, empSkills);
    }

    public List<Emp>getAllEmp(){
        return alphaRepository.getAllEmp();
    }
    public List<Emp> findByUsernameContaining(String searchString) {
        return alphaRepository.findEmpsContaining(searchString);
    }
    public Project updateProject(Project project){
        return alphaRepository.updateProject(project);
    }
    public Project findProjectByID(int projectID){
        return alphaRepository.findProjectByID(projectID);
    }
    public void deleteProject(int projectID) {
        alphaRepository.deleteProject(projectID);
    }

    public List<String> getSkillsList() {
        return alphaRepository.getSkillsList();
    }
    public List<String> getEmpSkillList(String username) {
        return alphaRepository.getEmpSkillList(username);
    }

    public Project createSubProject(int parentProjectID, Project newProject) {
        return alphaRepository.createSubProject(parentProjectID, newProject);
    }

    public ArrayList<Project> getAllSubProjectsOfProject(int projectID) {
        return alphaRepository.getAllSubProjectsOfProject(projectID);
    }

    public void addEmpToProject(String username, int projectID) {
        alphaRepository.addEmpToProject(username, projectID);
    }

    public Emp createEmpWithSkills(Emp newEmp, ArrayList<String> skills){
        return alphaRepository.createEmpWithSkills(newEmp, skills);}
    public Task createTask(Task newTask, int projectID){
        return alphaRepository.createTask(newTask, projectID);
    }
    public List<Emp> getEmpsOnProject(int projectID) {
        return alphaRepository.getEmpsOnProject(projectID);
    }
    public void deleteTask(int taskID){
        alphaRepository.deleteTask(taskID);
    }
    public ArrayList<Task>getAllTaskOfSubProject(int parrentProjectID){
        return alphaRepository.getAllTaskOfSubProject(parrentProjectID);
    }

    public List<Map<String, Object>> getAllCategories() {
        return alphaRepository.getAllCategories();
    }
    public Task findTaskById(int taskId){
        return alphaRepository.findTaskByTaskId(taskId);
    }
    public Task findTaskByProjectID(int projectID){
        return alphaRepository.findTaskByProjectID(projectID);
    }

    public List<Emp> findByUsernameContainingNotOnProject(String searchString, int projectID) {
        return alphaRepository.findEmpsConatiningNotOnProject(searchString, projectID);
    }

    public void removeEmpFromProject(int projectID, String username) {
        alphaRepository.removeEmpFromProject(projectID, username);
    }
    public int sumOfEstimates(int projectID){
        return alphaRepository.sumOfEstimates(projectID);
    }
    public void toggleIsDone(boolean isDone, int taskID){
        alphaRepository.toggleIsDone(isDone,taskID);
    }
    public Task updateTask(Task task){
        return alphaRepository.updateTask(task);
    }
    public double updatehoursDone(int hoursDoneToday, int taskID){
        return alphaRepository.updatehoursDone(hoursDoneToday,taskID);
    }

    public double getWorkProgressPercentage(int projectID){
        return alphaRepository.getWorkProgressPercentage(projectID);

    }

    public int getRemaningHoursOfWork(int projectID){
        return alphaRepository.getRemaningHoursOfWork(projectID);
    }

    public List<Project> getProjectsForEmp(String username) {
        return alphaRepository.getProjectsForEmp(username);
    }
}
