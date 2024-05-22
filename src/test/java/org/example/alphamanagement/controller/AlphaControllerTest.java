package org.example.alphamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.service.AlphaService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static java.lang.reflect.Array.get;
import static org.junit.jupiter.api.Assertions.*;

import org.example.alphamanagement.service.AlphaService;
import org.example.alphamanagement.model.Emp;  // Ensure you import your Emp model
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AlphaController.class)
class AlphaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlphaService alphaService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockHttpSession mockSession;
    private Emp empLoggedIn;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockSession = new MockHttpSession();
        empLoggedIn = new Emp();

        mockSession.setAttribute("empLoggedIn", empLoggedIn);
    }

    @Test
    void getLogin() throws Exception {
        mockMvc.perform(get("")).andExpect(status().isOk()).andExpect(view().name("login"));

    }


    @Test
    void showCreateProjectFormForProjectManager() throws Exception {

        empLoggedIn.setJobType(2);
        mockMvc.perform(get("/projects/new").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("create_project"));


    }

@Test
void showCreateProjectFormForSystemAdmin() throws Exception{
    empLoggedIn.setJobType(3);
    mockMvc.perform(get("/projects/new").session(mockSession))
            .andExpect(status().isOk())
            .andExpect(view().name("incorrect_jobtype_error"));
}

    @Test
    void showEmployees() throws Exception {

        empLoggedIn.setJobType(3);
        mockMvc.perform(get("/edit-emps").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_emps"));
    }


    @Test
    void updateEmpForm() throws Exception {
        empLoggedIn.setJobType(3);
        given(alphaService.findEmpByUsername(ArgumentMatchers.any()))
                .willReturn(new Emp("Test", "Test", "Test", "test", 1));

        mockMvc.perform(MockMvcRequestBuilders.get("/edit-emps/{username}/update-emp", "test").session(mockSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("update_emp"));
    }

    @Test
    void showCreateSubprojectForProjectManager() throws Exception {
        empLoggedIn.setJobType(2);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/{projectID}/create-subproject", 1).session(mockSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("create_subproject"));



    }
    @Test
    void showCreateSubprojectForEmployee() throws Exception{
        empLoggedIn.setJobType(1);
        mockMvc.perform(MockMvcRequestBuilders.get("/projects/{projectID}/create-subproject", 1).session(mockSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("incorrect_jobtype_error"));

    }


}

