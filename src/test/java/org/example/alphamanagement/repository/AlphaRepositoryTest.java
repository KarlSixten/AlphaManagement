package org.example.alphamanagement.repository;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("dev")
class AlphaRepositoryTest {

@Autowired
AlphaRepository alphaRepository;




    @Test
    void createEmpWithSkillsTest() {
        Emp emp = new Emp("Navn", "efternavn","username1231", "kode", 3);
        ArrayList<String> skilz = new ArrayList<>(List.of("Java"));
        alphaRepository.createEmpWithSkills(emp,skilz);

        int actual = 5;
        int expected = alphaRepository.getAllEmp().size();
        assertEquals(actual,expected);
    }

   @Test
   void findProjectByID() {
       String actual = alphaRepository.findProjectByID(2).getProjectName();
       String expected = "Hesteprojekt";
       assertEquals(actual,expected);
   }



    @Test
    void createProject() {
        LocalDate startDate = LocalDate.of(2013, 11, 24);
        LocalDate endDate = LocalDate.of(2014, 12, 24);
        Project project = new Project("Navn", startDate, endDate);
        Emp emp = new Emp("test", "test", "test", "test", 3);
        alphaRepository.createProject(project,emp);
        int expected = 3;
        int actual = alphaRepository.getAllProjects().size();
        assertEquals(expected,actual);
    }

    @Test
    void checkValidLogin() {
            Emp result = alphaRepository.checkValidLogin("test", "test");
            assertNotNull(result);
            assertEquals("test", result.getFirstName());
            assertEquals("test", result.getLastName());
        }



    @Test
    void findEmpByUsername() {
            String username = "test";
            Emp emp = alphaRepository.findEmpByUsername(username);
            assertNotNull(emp);
            assertEquals(username, emp.getUsername());
    }

    @Test
    void getAllProjects() {
        int expected = 3;
        int actual = alphaRepository.getAllProjects().size();
        assertEquals(actual,expected);
    }

    @Test
    void getProjectsForEmp() {
       int actual = alphaRepository.getProjectsForEmp("test").size();
       int expected = 2;
       assertEquals(actual,expected);
    }

    @Test
    void findEmpsContaining() {
        String searchQuery = "kabj";
        List<Emp> searchResults = alphaRepository.findEmpsContaining(searchQuery);

        assertFalse(searchResults.isEmpty());
        for (Emp emp : searchResults) {
            assertTrue(emp.getUsername().contains(searchQuery) ||
                    emp.getFirstName().contains(searchQuery) ||
                    emp.getLastName().contains(searchQuery));
        }
    }
    @Test
    void findEmpsContainingNoResults() {
        String searchQuery = "nonexistent";
        List<Emp> searchResults = alphaRepository.findEmpsContaining(searchQuery);

        assertTrue(searchResults.isEmpty());
    }


    @Test
    void findEmpsContainingNotOnProject() {
        String searchQuery = "test";
        int projectID = 1;
        List<Emp> searchResults = alphaRepository.findEmpsContainingNotOnProject(searchQuery, projectID);

        assertFalse(searchResults.isEmpty());
        for (Emp emp : searchResults) {
            assertTrue(emp.getUsername().contains(searchQuery) ||
                            emp.getFirstName().contains(searchQuery) ||
                            emp.getLastName().contains(searchQuery));
        }
    }


    @Test
    void getSkillsList() {
        List<String> actual = alphaRepository.getSkillsList();


        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(actual.contains("Java"));
        assertTrue(actual.contains("SQL"));
    }

    @Test
    void getEmpSkillList() {
        List<String> actual = alphaRepository.getEmpSkillList("kabj0000");
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(actual.contains("Java"));
        assertTrue(actual.contains("SQL"));
        assertFalse(actual.contains("HTML"));

    }


    @Test
    void getAllEmp() {
        int actual = alphaRepository.getAllEmp().size();
        int expected = 4;
        assertEquals(actual, expected);
    }




    @Test
    void addEmpToProject() {
        alphaRepository.addEmpToProject("kabj0000", 1);
      int actual = alphaRepository.getEmpsOnProject(1).size();
      int expected = 3;
      assertEquals(actual,expected);
    }

    @Test
    void createSubProject() {
        LocalDate startDate = LocalDate.of(2013, 11, 24);
        LocalDate endDate = LocalDate.of(2014, 12, 24);
        Project project = new Project("Navnetest", startDate, endDate);
        alphaRepository.createSubProject(2,project);
        int actual = alphaRepository.getAllSubProjectsOfProject(2).size();
        int expected = 1;
        assertEquals(expected,actual);
    }

    @Test
    void getAllSubProjectsOfProject() {
        int actual = alphaRepository.getAllSubProjectsOfProject(3).size();
        int expected = 1;
        assertEquals(actual,expected);
    }

    @Test
    void getEmpsOnProject() {
       int actual = alphaRepository.getEmpsOnProject(1).size();
       int expected = 2;
       assertEquals(actual,expected);
    }

    @Test
    void createTask() {
        LocalDate startDate = LocalDate.of(2013, 11, 24);
        LocalDate endDate = LocalDate.of(2014, 12, 24);
        Task task = new Task("Taskname",1,"taskd",2, startDate,endDate);
       alphaRepository.createTask(task,4);
      int actual = alphaRepository.getAllTaskOfSubProject(4).size();
      int expected = 3;
      assertEquals(actual,expected);
    }


    @Test
    void GetAllCategories() {
        List<Map<String, Object>> categories = alphaRepository.getAllCategories();
        assertNotNull(categories);
        assertEquals(2, categories.size());

        Map<String, Object> firstCategory = categories.get(0);
        assertEquals(1, firstCategory.get("categoryID"));
        assertEquals("User story", firstCategory.get("categoryName"));

        Map<String, Object> secondCategory = categories.get(1);
        assertEquals(2, secondCategory.get("categoryID"));
        assertEquals("Technical story", secondCategory.get("categoryName"));
    }

    @Test
    void findTaskByTaskId() {
       String actual = alphaRepository.findTaskByTaskId(1).getTaskName();
       String expected = "TASK 1";
       assertEquals(actual,expected);

    }

    @Test
    void findTaskByProjectID() {
       String actual = alphaRepository.findTaskByProjectID(4).getTaskName();
       String expected = "TASK 1";
       assertEquals(actual,expected);
    }


    @Test
    void getAllTaskOfSubProject() {
       int actual = alphaRepository.getAllTaskOfSubProject(4).size();
       int expected = 2;
       assertEquals(actual,expected);
    }

    @Test
    void sumOfEstimates() {
      int actual = alphaRepository.sumOfEstimates(4);
      int expected = 13;
      assertEquals(actual,expected);
    }


    @Test
    void updatehoursDone() {
       alphaRepository.updatehoursDone(2,1);
       double actual = alphaRepository.findTaskByTaskId(1).getHoursDone();
       double expected = 2;
       assertEquals(actual,expected);
    }


    @Test
    void getRemaningHoursOfWork() {
        alphaRepository.updatehoursDone(3,2);
        double actual = alphaRepository.findTaskByTaskId(1).getEstimate() - alphaRepository.findTaskByTaskId(1).getHoursDone();
        double expected = 2;
        assertEquals(actual,expected);
   }



    @Test
    void deleteEmp() {
       alphaRepository.deleteEmp("kabj0000");
      int actual = alphaRepository.getAllEmp().size();
      int expected = 4;
      assertEquals(actual,expected);
    }

    @Test
    void deleteTask() {
       alphaRepository.deleteTask(1);
       int actual = alphaRepository.getAllTaskOfSubProject(4).size();
       int expected = 2;
       assertEquals(actual,expected);

    }

    @Test
    void removeEmpFromProject() {
       alphaRepository.removeEmpFromProject(1,"test");
       int expected = alphaRepository.getEmpsOnProject(1).size();
       int actual = 2;
       assertEquals(actual,expected);


    }

    @Test
    void deleteProject() {
        alphaRepository.deleteProject(1);
        int actual = alphaRepository.getAllProjects().size();
        int expected = 2;
        assertEquals(actual,expected);
    }
}