package org.example.alphamanagement.service;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.repository.AlphaRepository;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {
    private final AlphaRepository alphaRepository;

    public AlphaService(AlphaRepository alphaRepository) {
        this.alphaRepository = alphaRepository;
    }



    public Emp createEmp(Emp emp){
        return alphaRepository.createEmp(emp);
    }

    public Emp checkValidLogin(Emp empToCheck) {
        return alphaRepository.checkValidLogin(empToCheck);
    }
    public Project createProject(Project newProject){
        return alphaRepository.createProject(newProject);
    }

    public Object findEmpByUsername(String username) {
        return alphaRepository.findEmpByUsername(username);
    }
    public void deleteEmp(String username){
        alphaRepository.deleteEmp(username);
    }

}
