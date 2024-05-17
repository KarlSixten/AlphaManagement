//package org.example.alphamanagement.controller;
//
//import org.example.alphamanagement.service.AlphaService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentMatchers;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static java.lang.reflect.Array.get;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.example.alphamanagement.service.AlphaService;
//import org.example.alphamanagement.model.Emp;  // Ensure you import your Emp model
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.hamcrest.Matchers.hasKey;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//@WebMvcTest(AlphaController.class)
//class AlphaControllerTest {
//
//@Autowired
//private MockMvc mockMvc;
//
//@MockBean
//private AlphaService alphaService;
//
//
//
//    @Test
//    void createEmp() throws Exception{
//        mockMvc.perform(get("/create-emp")).andExpect(status().isOk()).andExpect(view().name("create-emp"));
//
//    }
//
//    @Test
//    void getSaveNewEmp() {
//
//    }
//
//    @Test
//    void getCreateUserSuccess() throws Exception {
//        mockMvc.perform(get("/user-created-success")).andExpect(status().isOk()).andExpect(view().name("user_created_success"));
//
//    }
//
//    @Test
//    void getLogin() throws Exception {
//        mockMvc.perform(get("")).andExpect(status().isOk()).andExpect(view().name("login"));
//
//    }
//
//    @Test
//    void submitLogin() {
//    }
////virker sku ikke
//    @Test
//    void logout() throws Exception{
//        mockMvc.perform(get("/logout")).andExpect(status().isOk()).andExpect(view().name("/login"));
//
//    }
//
//    @Test
//    void getHome() throws Exception{
//        mockMvc.perform(get("/home")).andExpect(status().isOk()).andExpect(view().name("front-page"));
//
//    }
//
//    @Test
//    void showCreateProjectForm() throws Exception {
//        mockMvc.perform(get("/projects/new")).andExpect(status().isOk()).andExpect(view().name("createProject"));
//    }
//
//    @Test
//    void createProject()  {
//
//
//    }
//
//    @Test
//    void showUpdateProjectForm() {
//    }
//
//    @Test
//    void updateProject() {
//    }
//
//    @Test
//    void deleteProject() {
//    }
//
//    @Test
//    void showEmployees() {
//    }
//
//    @Test
//    void deleteEmp() {
//    }
//
//    @Test
//    void updateEmpForm() {
//    }
//
//    @Test
//    void updateEmp() {
//    }
//
//    @Test
//    void showCreateSubproject() {
//    }
//
//    @Test
//    void createSubProject() {
//    }
//
//    @Test
//    void getSubProjects() {
//    }
//
//    @Test
//    void showAddEmpToProjectForm() {
//    }
//
//    @Test
//    void addEmpToProject() {
//    }
//
//    @Test
//    void removeEmpfromProject() {
//    }
//
//    @Test
//    void showCreateTaskForm() {
//    }
//
//    @Test
//    void createTask() {
//    }
//
//    @Test
//    void viewTask() {
//    }
//
//    @Test
//    void getAllTaskOfSubProject() {
//    }
//
//    @Test
//    void deleteTask() {
//    }
//
//    @Test
//    void toggleIsDone() {
//    }
//
//    @Test
//    void showUpdateTaskForm() throws Exception{
//        mockMvc.perform(get("/projects/{projectID}/{taskID}/update")).andExpect(status().isOk()).andExpect(view().name("update-task"));
//
//    }
//
//    @Test
//    void hoursDoneInput() {
//    }
//
//    @Test
//    void updateTask() {
//    }
//}