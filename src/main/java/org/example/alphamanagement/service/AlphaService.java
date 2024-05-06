package org.example.alphamanagement.service;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.repository.AlphaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlphaService {
    private final AlphaRepository alphaRepository;

    public AlphaService(AlphaRepository alphaRepository) {
        this.alphaRepository = alphaRepository;
    }



    public Emp createEmp(Emp emp){
        return alphaRepository.createEmp(emp);
    }

    public Emp checkValidLogin(String empUsername, String empPassword) {
        return alphaRepository.checkValidLogin(empUsername, empPassword);
    }
    public Project createProject(Project newProject){
        return alphaRepository.createProject(newProject);
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
    public List<String> getSkillsList(){
        return alphaRepository.getSkillsList();
    }
    public List<String> getEmpSkillList(String username) {
        return alphaRepository.getEmpSkillList(username);
    }

    }
