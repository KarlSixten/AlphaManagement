package org.example.alphamanagement.repository;


import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.model.Task;
import org.example.alphamanagement.repository.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class AlphaRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    //---------------------------------------------------------------------------------------------------------------
    // LOGIN
    //---------------------------------------------------------------------------------------------------------------

    public Emp checkValidLogin(String empUsername, String empPassword) {
        String sql = "SELECT * FROM emp WHERE username LIKE (?) AND password LIKE (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, empUsername);
            pstmt.setString(2, empPassword);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createEmpFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //---------------------------------------------------------------------------------------------------------------
    // GET PROJECT
    //---------------------------------------------------------------------------------------------------------------

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE parentProjectID IS NULL ORDER BY startDate ASC";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(createProjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

    public List<Project> getProjectsForEmp(String username) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT project.* " +
                "FROM project " +
                "JOIN project_emp ON project.projectID = project_emp.projectID " +
                "WHERE project_emp.username = ? " +
                "AND project.parentProjectID IS NULL " +
                "ORDER BY startDate ASC;";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(createProjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

    //---------------------------------------------------------------------------------------------------------------
    // FIND PROJECT
    //---------------------------------------------------------------------------------------------------------------

    public Project findProjectByID(int projectID) {
        String sql = "SELECT * FROM project WHERE projectId = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createProjectFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find project by ID", e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    // CREATE PROJECT
    //---------------------------------------------------------------------------------------------------------------
    public Project createProject(Project newProject, Emp projectCreatorEmp) {
        String projectSQL = "INSERT INTO PROJECT(PROJECTNAME, STARTDATE, ENDDATE) values (?,?,?)";
        String projectEmpSQL = "INSERT INTO project_emp (projectID, username) VALUES (?, ?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement projectPreparedStatement = connection.prepareStatement(projectSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            projectPreparedStatement.setString(1, newProject.getProjectName());
            projectPreparedStatement.setDate(2, Date.valueOf(newProject.getStartDate()));
            projectPreparedStatement.setDate(3, Date.valueOf(newProject.getEndDate()));
            projectPreparedStatement.executeUpdate();

            ResultSet generatedKeys = projectPreparedStatement.getGeneratedKeys();



            if (generatedKeys.next()) {
                int generatedProjectID = generatedKeys.getInt(1);
                newProject.setProjectID(generatedProjectID);
            } else {
                throw new SQLException("Creating project failed, no ID obtained.");
            }

            PreparedStatement projectEmpPreparedStatement = connection.prepareStatement(projectEmpSQL);
            projectEmpPreparedStatement.setInt(1, generatedKeys.getInt(1));
            projectEmpPreparedStatement.setString(2, projectCreatorEmp.getUsername());
            projectEmpPreparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newProject;
    }


    //---------------------------------------------------------------------------------------------------------------
    // UPDATE PROJECT
    //---------------------------------------------------------------------------------------------------------------
    public Project updateProject(Project project) {
        String SQL = "UPDATE project SET projectName = ?, startDate = ?, endDate = ? WHERE projectId = ?;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(SQL);
            preparedStatement.setString(1, project.getProjectName());
            preparedStatement.setDate(2, Date.valueOf(project.getStartDate()));
            preparedStatement.setDate(3, Date.valueOf(project.getEndDate()));
            preparedStatement.setInt(4, project.getProjectID());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    //---------------------------------------------------------------------------------------------------------------
    // DELETE PROJECT
    //---------------------------------------------------------------------------------------------------------------

    public void deleteProject(int projectID) {
        String empTaskSql = "DELETE FROM emp_task WHERE taskID IN (SELECT taskID FROM task WHERE projectID IN (SELECT projectID FROM project WHERE parentProjectID = ? OR projectID = ?));";
        String taskSql = "DELETE FROM task WHERE projectID IN (SELECT projectID FROM project WHERE parentProjectID = ? OR projectID = ?);";
        String subprojectEmpSql = "DELETE FROM project_emp WHERE projectID IN (SELECT projectID FROM project WHERE parentProjectID = ?);";
        String projectEmpSql = "DELETE FROM project_emp WHERE projectID = ?;";
        String subProjectSql = "DELETE FROM project WHERE parentProjectID = ?;";
        String projectSql = "DELETE FROM project WHERE projectID = ?;";

        Connection connection = ConnectionManager.getConnection(url, user, password);

        try {
            connection.setAutoCommit(false);

            PreparedStatement taskEmpPstmt = connection.prepareStatement(empTaskSql);
            taskEmpPstmt.setInt(1, projectID);
            taskEmpPstmt.setInt(2, projectID);
            taskEmpPstmt.executeUpdate();

            PreparedStatement taskPstmt = connection.prepareStatement(taskSql);
            taskPstmt.setInt(1, projectID);
            taskPstmt.setInt(2, projectID);
            taskPstmt.executeUpdate();

            PreparedStatement subprojectEmpPstmt = connection.prepareStatement(subprojectEmpSql);
            subprojectEmpPstmt.setInt(1, projectID);
            subprojectEmpPstmt.executeUpdate();

            PreparedStatement projectEmpPstmt = connection.prepareStatement(projectEmpSql);
            projectEmpPstmt.setInt(1, projectID);
            projectEmpPstmt.executeUpdate();

            PreparedStatement subProjectPstmt = connection.prepareStatement(subProjectSql);
            subProjectPstmt.setInt(1, projectID);
            subProjectPstmt.executeUpdate();

            PreparedStatement projectPstmt = connection.prepareStatement(projectSql);
            projectPstmt.setInt(1, projectID);
            projectPstmt.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to roll back transaction", ex);
            }
            throw new RuntimeException("Failed to delete project", e);
        }
    }




    //---------------------------------------------------------------------------------------------------------------
    // CREATE SUBPROJECT
    //---------------------------------------------------------------------------------------------------------------
    public Project createSubProject(int parentProjectID, Project newProject) {
        newProject.setParentProjectID(parentProjectID);
        String SQL = "INSERT INTO project(projectName, startDate, endDate, parentProjectID) values (?,?,?,?);";

        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(SQL)) {
            pstmt.setString(1, newProject.getProjectName());
            pstmt.setDate(2, Date.valueOf(newProject.getStartDate()));
            pstmt.setDate(3, Date.valueOf(newProject.getEndDate()));
            pstmt.setInt(4, parentProjectID);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newProject;
    }
    //---------------------------------------------------------------------------------------------------------------
    // GET SUBPROJECT
    //---------------------------------------------------------------------------------------------------------------
    public ArrayList<Project> getAllSubProjectsOfProject(int projectID) {
        ArrayList<Project> subProjects = new ArrayList<>();
        String SQL = "SELECT * from project where parentProjectID = ?;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(SQL)) {
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            Project currentProject;
            while (rs.next()) {
                currentProject = createProjectFromResultSet(rs);
                subProjects.add(currentProject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subProjects;
    }

    //---------------------------------------------------------------------------------------------------------------
    // CREATE EMP
    //---------------------------------------------------------------------------------------------------------------
    public Emp createEmpWithSkills(Emp newEmp, ArrayList<String> skills) {
        newEmp.setUsername(createUsername(newEmp.getFirstName(), newEmp.getLastName()));
        String sql = "INSERT INTO emp(firstName, lastName, username, password, jobTypeID) VALUES (?, ?, ?, ?, ?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newEmp.getFirstName());
            pstmt.setString(2, newEmp.getLastName());
            pstmt.setString(3, newEmp.getUsername());
            pstmt.setString(4, newEmp.getPassword());
            pstmt.setInt(5, newEmp.getJobType());
            pstmt.executeUpdate();
            if (skills != null) {
                saveSkills(newEmp.getUsername(), skills);
            }
            return newEmp;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    // ADD EMP
    //---------------------------------------------------------------------------------------------------------------

    public void addEmpToTask(String username, int taskID){
        String SQL = "INSERT INTO EMP_TASK (USERNAME, TASKID) VALUES (?,?)";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, taskID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addEmpToProject(String username, int projectID) {
        String sql = "INSERT INTO PROJECT_EMP (USERNAME, PROJECTID) VALUES (?, ?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, projectID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------------------------
    // GET EMP
    //---------------------------------------------------------------------------------------------------------------


    public List<Emp> getAllEmp() {
        List<Emp> allEmp = new ArrayList<>();
        String sql = "SELECT * FROM Emp;";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                allEmp.add(createEmpFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allEmp;
    }

    public List<Emp> getEmpsOnProject(int projectID) {
        List<Emp> empList = new ArrayList<>();

        String sql = "SELECT emp.username, emp.password, emp.firstName, emp.lastName, emp.jobTypeID FROM emp JOIN project_emp ON emp.username = project_emp.username WHERE projectID = (?) ORDER BY projectID;";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                empList.add(createEmpFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empList;
    }

    public List<Emp> getEmpsOnTask(int taskID) {
        List<Emp> empList = new ArrayList<>();

        String sql = "SELECT emp.username, emp.password, emp.firstName, emp.lastName, emp.jobTypeID " +
                "FROM emp " +
                "JOIN emp_task ON emp.username = emp_task.username " +
                "WHERE taskID = ? " +
                "ORDER BY emp.username;";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                empList.add(createEmpFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empList;
    }

    public List<Emp> getEmpsNotOnTask(int taskID, int projectID) {
        List<Emp> empList = new ArrayList<>();
        String sql = "SELECT emp.* " +
                "FROM emp " +
                "JOIN project_emp ON emp.username = project_emp.username " +
                "LEFT JOIN emp_task ON emp.username = emp_task.username AND emp_task.taskID = ? " +
                "WHERE project_emp.projectID = ? " +
                "AND emp_task.username IS NULL " +
                "ORDER BY emp.username;";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskID);
            pstmt.setInt(2, projectID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                empList.add(createEmpFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empList;
    }

    //---------------------------------------------------------------------------------------------------------------
    // FIND EMP
    //---------------------------------------------------------------------------------------------------------------


    public Emp findEmpByUsername(String username) {
        Emp emp = new Emp();
        String sql = "SELECT * FROM emp WHERE username like (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, username);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                emp = createEmpFromResultSet(rs);
            }
            return emp;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Emp> findEmpsContaining(String searchQuery) {
        List<Emp> searchResults = new ArrayList<>();
        String sql = "SELECT * FROM emp WHERE username LIKE (?) OR firstName LIKE (?) OR lastName LIKE (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, "%" + searchQuery + "%");
            psmt.setString(2, "%" + searchQuery + "%");
            psmt.setString(3, "%" + searchQuery + "%");
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                searchResults.add(createEmpFromResultSet(rs));
            }
            return searchResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Emp> findEmpsContainingInParentProject(String searchQuery, int projectID){
        List<Emp> searchResults = new ArrayList<>();
        String sql = "SELECT e.* " +
                "FROM emp e " +
                "JOIN project_emp pe ON e.username = pe.username " +
                "WHERE (e.username LIKE ? OR e.firstName LIKE ? OR e.lastName LIKE ?) " +
                "AND pe.projectID = ? " +
                "AND e.username NOT IN (" +
                "    SELECT username " +
                "    FROM project_emp " +
                "    WHERE projectID = ?" +
                ");";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, "%" + searchQuery + "%");
            psmt.setString(2, "%" + searchQuery + "%");
            psmt.setString(3, "%" + searchQuery + "%");
            psmt.setInt(4, findProjectByID(projectID).getParentProjectID());
            psmt.setInt(5, projectID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                searchResults.add(createEmpFromResultSet(rs));
            }
            return searchResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Emp> findEmpsContainingNotOnProject(String searchQuery, int projectID) {
        List<Emp> searchResults = new ArrayList<>();
        String sql = "SELECT emp.* FROM emp LEFT JOIN project_emp ON emp.username = project_emp.username AND project_emp.projectID = (?) WHERE project_emp.username IS NULL AND (emp.username LIKE (?) OR emp.firstName LIKE (?) OR emp.lastName LIKE (?));";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectID);
            pstmt.setString(2, "%" + searchQuery + "%");
            pstmt.setString(3, "%" + searchQuery + "%");
            pstmt.setString(4, "%" + searchQuery + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                searchResults.add(createEmpFromResultSet(rs));
            }
            return searchResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    //---------------------------------------------------------------------------------------------------------------
    // UPDATE EMP
    //---------------------------------------------------------------------------------------------------------------
    public Emp updateEmp(Emp emp, List<String> empSkills) {
        String updateEmpQuery = "UPDATE emp SET firstName = ?, lastName = ?, password = ?, jobTypeID = ? WHERE username = ?;";
        String deleteEmpSkillsQuery = "DELETE FROM emp_skill WHERE username = ?;";
        String insertEmpSkillsQuery = "INSERT INTO emp_skill (username, skillID) VALUES (?, (SELECT skillID FROM skill WHERE skillName = ?));";

        Connection con = ConnectionManager.getConnection(url, user, password);
        Emp updatedEmp = null;

        try {
            PreparedStatement updateEmpStatement = con.prepareStatement(updateEmpQuery);
            PreparedStatement delete = con.prepareStatement(deleteEmpSkillsQuery);
            PreparedStatement insert = con.prepareStatement(insertEmpSkillsQuery);

            updateEmpStatement.setString(1, emp.getFirstName());
            updateEmpStatement.setString(2, emp.getLastName());
            updateEmpStatement.setString(3, emp.getPassword());
            updateEmpStatement.setInt(4, emp.getJobType());
            updateEmpStatement.setString(5, emp.getUsername());
            updateEmpStatement.executeUpdate();

            delete.setString(1, emp.getUsername());
            delete.executeUpdate();

            for (String skill : empSkills) {
                insert.setString(1, emp.getUsername());
                insert.setString(2, skill);
                insert.executeUpdate();
            }

            updatedEmp = findEmpByUsername(emp.getUsername());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedEmp;
    }


    //---------------------------------------------------------------------------------------------------------------
    // DELETE EMP
    //---------------------------------------------------------------------------------------------------------------

    public void deleteEmp(String username) {
        String deleteFromEmp = "DELETE FROM EMP WHERE username = ?";
        String deleteFromProject_Emp = "DELETE FROM PROJECT_EMP WHERE username = ?";
        String deleteFromEmp_Task = "DELETE FROM EMP_TASK WHERE username =?";
        String deleteFromEmp_Skill = "DELETE FROM EMP_SKILL WHERE username =?";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            connection.setAutoCommit(false);
            PreparedStatement deleteFromProject_EmpPstmt = connection.prepareStatement(deleteFromProject_Emp);
            deleteFromProject_EmpPstmt.setString(1, username);
            deleteFromProject_EmpPstmt.executeUpdate();

            PreparedStatement deleteFromEmp_TaskPstmt = connection.prepareStatement(deleteFromEmp_Task);
            deleteFromEmp_TaskPstmt.setString(1, username);
            deleteFromEmp_TaskPstmt.executeUpdate();

            PreparedStatement deleteFromEmp_SkillPstmt = connection.prepareStatement(deleteFromEmp_Skill);
            deleteFromEmp_SkillPstmt.setString(1, username);
            deleteFromEmp_SkillPstmt.executeUpdate();

            PreparedStatement deleteFromEmpPstmt = connection.prepareStatement(deleteFromEmp);
            deleteFromEmpPstmt.setString(1, username);
            deleteFromEmpPstmt.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to roll back transaction", ex);
            }
            throw new RuntimeException("Failed to delete emp", e);
        }
    }

    public void removeEmpFromProject(int projectID, String username) {
        if (findProjectByID(projectID).getParentProjectID() > 0) {
            removeEmpFromSubProject(projectID, username);
        } else {
            removeEmpFromParentProject(projectID, username);
        }
    }

    public void removeEmpFromTask(int taskID, String username){
        String sql;
        Connection connection = ConnectionManager.getConnection(url, user, password);
        sql = "DELETE FROM EMP_TASK WHERE TASKID = ? AND USERNAME = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    //---------------------------------------------------------------------------------------------------------------
    // CREATE TASK
    //---------------------------------------------------------------------------------------------------------------
    public Task createTask(Task newTask, int projectID) {
        String SQL = "INSERT INTO TASK(TASKNAME, PROJECTID, CATEGORYID, DESCRIPTION, ESTIMATE, STARTDATE, ENDDATE) values (?,?,?,?,?,?,?)";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try {
            newTask.setProjectID(projectID);
            PreparedStatement preparedStatement = con.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newTask.getTaskName());
            preparedStatement.setInt(2, newTask.getProjectID());
            preparedStatement.setInt(3, newTask.getCategoryID());
            preparedStatement.setString(4, newTask.getDescription());
            preparedStatement.setInt(5, newTask.getEstimate());
            preparedStatement.setDate(6, Date.valueOf(newTask.getStartDate()));
            preparedStatement.setDate(7, Date.valueOf(newTask.getEndDate()));
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedTaskID = generatedKeys.getInt(1);
                newTask.setTaskID(generatedTaskID);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newTask;
    }
    //---------------------------------------------------------------------------------------------------------------
    // GET TASK
    //---------------------------------------------------------------------------------------------------------------
    public ArrayList<Task> getAllTaskOfSubProject(int projectID) {
        ArrayList<Task> tasks = new ArrayList<>();
        String SQL = "SELECT * FROM TASK WHERE ProjectID = ?;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(SQL)) {
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task currentTask = createTaskFromResultSet(rs);
                tasks.add(currentTask);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    public List<Task> getTasksForEmp(String username) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT task.* FROM task JOIN emp_task ON task.taskID = emp_task.taskID WHERE emp_task.username = (?) ORDER BY task.endDate ASC;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(createTaskFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    //---------------------------------------------------------------------------------------------------------------
    // FIND TASK
    //---------------------------------------------------------------------------------------------------------------
    public Task findTaskByTaskId(int taskId) {
        String sql = "SELECT * FROM task WHERE taskID = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createTaskFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find task by ID", e);
        }
    }

    public Task findTaskByProjectID(int projectID) {
        String sql = "SELECT * FROM task WHERE projectID = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createTaskFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find task by ID", e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    // UPDATE TASK
    //---------------------------------------------------------------------------------------------------------------
    public Task updateTask(Task task) {
        String SQL = "UPDATE TASK SET taskName = ?, categoryID = ?, description = ? , estimate = ?, startDate = ?, endDate = ? WHERE taskID =?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setInt(2, task.getCategoryID());
            preparedStatement.setString(3, task.getDescription());
            preparedStatement.setInt(4, task.getEstimate());
            preparedStatement.setDate(5, java.sql.Date.valueOf(task.getStartDate()));
            preparedStatement.setDate(6, java.sql.Date.valueOf(task.getEndDate()));
            preparedStatement.setInt(7, task.getTaskID());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("ups");
        }
        return task;
    }
    //---------------------------------------------------------------------------------------------------------------
    // DELETE TASK
    //---------------------------------------------------------------------------------------------------------------

    public void deleteTask(int taskID) {
        String taskEmpSql = "DELETE FROM emp_task WHERE taskID = (?);";
        String taskSql = "DELETE FROM TASK WHERE TASKID = (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement taskEmpPstmt = connection.prepareStatement(taskEmpSql);
            taskEmpPstmt.setInt(1, taskID);
            taskEmpPstmt.executeUpdate();

            PreparedStatement taskPstmt = connection.prepareStatement(taskSql);
            taskPstmt.setInt(1, taskID);
            taskPstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------------------------
    // GET SKILLS
    //---------------------------------------------------------------------------------------------------------------
    public List<String> getSkillsList() {
        List<String> skillsList = new ArrayList<>();
        String SQL = "SELECT SKILLNAME FROM SKILL;";
        Connection con = ConnectionManager.getConnection(url, user, password);

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                String skillName = rs.getString("skillName");
                skillsList.add(skillName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return skillsList;
    }

    public List<String> getEmpSkillList(String username) {
        List<String> empSkillList = new ArrayList<>();
        String sql = "SELECT username, skillName FROM emp_skill LEFT JOIN skill ON emp_skill.skillID = skill.skillID WHERE username LIKE (?);";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                empSkillList.add(rs.getString("skillName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empSkillList;
    }
    // ---------------------------------------------------------------------------------------------------------------
    // CATEGORY METHODS
    //---------------------------------------------------------------------------------------------------------------
    public List<Map<String, Object>> getAllCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        String SQL = "SELECT categoryID, categoryName FROM category;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                category.put("categoryID", rs.getInt("categoryID"));
                category.put("categoryName", rs.getString("categoryName"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load categories", e);
        }
        return categories;
    }


    // ---------------------------------------------------------------------------------------------------------------
    // CALCULATION RELATED METHODS
    //---------------------------------------------------------------------------------------------------------------

    //TODO: Flytte

    public double hoursPrDayCalculator(int projectID){
            int sumOfEstimates = getAllEstimatesInSubProject(projectID);
            int getLengthOfSubproject = getLengthOfSubProject(projectID);
            int getNoOfEmpsOnSubproject = getNoOfEmpsOnSubproject(projectID);

        return (double)sumOfEstimates/getLengthOfSubproject/getNoOfEmpsOnSubproject;
    }

    public int getAllEstimatesInSubProject(int projectID){
        String SQL = "SELECT SUM(estimate) AS total_estimate FROM task WHERE projectID = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        int sumOfEstimates = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)){
            pstmt.setInt(1, projectID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                sumOfEstimates = rs.getInt("total_estimate");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sumOfEstimates;
    }

    public int getLengthOfSubProject(int subProjectID) {
        String SQL = "SELECT DATEDIFF('day', startDate, endDate) " +
                "       - (DATEDIFF('week', startDate, endDate) * 2)" +
                "       - CASE WHEN DAY_OF_WEEK(startDate) = 1 THEN 1 ELSE 0 END" +
                "       + CASE WHEN DAY_OF_WEEK(endDate) = 1 THEN 1 ELSE 0 END AS dateDifference FROM project WHERE projectID = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        int lengthOfSubProject = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setInt(1, subProjectID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                lengthOfSubProject = rs.getInt("dateDifference");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return lengthOfSubProject;
    }

    public int sumOfEstimates(int projectID) {
        ArrayList<Task> tasks = getAllTaskOfSubProject(projectID);
        int sumOfEstimates = 0;
        for (Task task : tasks) {
            sumOfEstimates += task.getEstimate();
        }

        return sumOfEstimates;
    }

    public void toggleIsDone(boolean isDone, int taskID) {
        String sql = "UPDATE TASK SET isDone = ? WHERE taskID = ?;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setBoolean(1, !isDone);
            pstmt.setInt(2, taskID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double updatehoursDone(double hoursDoneToday, int taskID) {
        hoursDoneToday += findTaskByTaskId(taskID).getHoursDone();
        String SQL;
        if (hoursDoneToday >= findTaskByTaskId(taskID).getEstimate()) {
            SQL = "update task set hoursDone = ?, isDone = true where TaskID = ?";
        } else {
            SQL = "update task set hoursDone = ? where TaskID = ?;";
        }
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setDouble(1, hoursDoneToday);
            pstmt.setInt(2, taskID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hoursDoneToday;
    }

    public int getWorkProgressPercentage(int projectID) {
        ArrayList<Task> tasksInProject = getAllTaskOfSubProject(projectID);
        int sumOfEstimates = 0;
        double sumOfHoursDone = 0;

        for (Task task : tasksInProject) {
            sumOfEstimates += task.getEstimate();
            if (task.getEstimate()>= task.getHoursDone()) {
                sumOfHoursDone += task.getHoursDone();
            } else {
                sumOfHoursDone += task.getEstimate();
            }
        }
        if (sumOfEstimates != 0) {
            double percentage = (sumOfHoursDone / sumOfEstimates) * 100;
            return (int)Math.min(percentage, 100);
        } else {

            return 0;
        }
    }

    public int getRemainingHoursOfWork(int projectID){
        ArrayList<Task> tasksInProject = getAllTaskOfSubProject(projectID);
        int sumOfEstimates = 0;
        double sumOfHoursDone = 0;
        for (Task task : tasksInProject) {
            sumOfEstimates += task.getEstimate();
            if (task.getEstimate()>= task.getHoursDone()) {
                sumOfHoursDone += task.getHoursDone();
            } else {
                sumOfHoursDone += task.getEstimate();
            }
        }

        return sumOfEstimates - (int)sumOfHoursDone;


    }



    //---------------------------------------------------------------------------------------------------------------
    //HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER HJÆLPEMETODER
    //---------------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------------
    //LOGIN
    //---------------------------------------------------------------------------------------------------------------
    private String createUsername(String firstName, String lastName) {
        Random random = new Random();
        String username = "";

        do {
            username = firstName.substring(0, 2) + lastName.substring(0, 2) + random.nextInt(10000);
        } while (!usernameIsUnique(username));

        return username;
    }

    private boolean usernameIsUnique(String username) {
        boolean nameIsUnique = false;

        String sql = "SELECT COUNT(*) FROM emp WHERE username like (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("COUNT(*)");
                nameIsUnique = (count == 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return nameIsUnique;
    }
    //---------------------------------------------------------------------------------------------------------------
    //PROJECT
    //---------------------------------------------------------------------------------------------------------------
    private Project createProjectFromResultSet(ResultSet rs) {
        Project project = new Project();

        try {
            project.setProjectID(rs.getInt("projectId"));
            project.setProjectName(rs.getString("projectName"));
            project.setStartDate(rs.getDate("startDate").toLocalDate());
            project.setEndDate(rs.getDate("endDate").toLocalDate());
            project.setParentProjectID(rs.getInt("parentProjectID"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }
    //---------------------------------------------------------------------------------------------------------------
    //EMP
    //---------------------------------------------------------------------------------------------------------------
    private Emp createEmpFromResultSet(ResultSet resultSet) {
        Emp emp = new Emp();

        try {
            emp.setFirstName(resultSet.getString("firstName"));
            emp.setLastName(resultSet.getString("lastName"));
            emp.setUsername(resultSet.getString("username"));
            emp.setPassword(resultSet.getString("password"));
            emp.setJobType(resultSet.getInt("jobTypeID"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emp;
    }

    private void removeEmpFromSubProject(int projectID, String username) {

        removeEmpFromSubProjectTasks(projectID, username);
        String sql = "DELETE FROM project_emp WHERE projectID = ? AND username = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void removeEmpFromParentProject(int parentProjectID, String username) {
        String sql = "DELETE FROM project_emp " +
                "WHERE (projectID = ? OR projectID IN (" +
                "           SELECT projectID " +
                "           FROM project " +
                "           WHERE parentProjectID = ?" +
                "      ))" +
                "AND username = ?;";

        removeEmpFromParentProjectTasks(parentProjectID, username);

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, parentProjectID);
            pstmt.setInt(2, parentProjectID);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void removeEmpFromParentProjectTasks(int parentProjectID, String username) {
        String sql = "DELETE FROM emp_task " +
                "WHERE taskID IN (" +
                "           SELECT taskID " +
                "           FROM task " +
                "           WHERE projectID IN (" +
                "                   SELECT projectID " +
                "                   FROM project " +
                "                   WHERE parentProjectID = ?" +
                "           )" +
                ") " +
                "AND username = ?;";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, parentProjectID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeEmpFromSubProjectTasks(int subProjectID, String username) {
        String sql = "DELETE FROM emp_task " +
                "WHERE taskID IN (" +
                "           SELECT taskID " +
                "           FROM task " +
                "           WHERE projectID = ?" +
                ") " +
                "AND username = ?;";

        Connection connection = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, subProjectID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getNoOfEmpsOnSubproject(int projectID){
        return getEmpsOnProject(projectID).size();
    }
    //---------------------------------------------------------------------------------------------------------------
    //TASK
    //---------------------------------------------------------------------------------------------------------------
    private Task createTaskFromResultSet(ResultSet rs) {
        Task task = new Task();

        try {
            task.setTaskID(rs.getInt("taskID"));
            task.setTaskName(rs.getString("taskName"));
            task.setProjectID(rs.getInt("projectID"));
            task.setCategoryID(rs.getInt("categoryID"));
            task.setDescription(rs.getString("description"));
            task.setEstimate(rs.getInt("estimate"));
            task.setStartDate(rs.getDate("startDate").toLocalDate());
            task.setEndDate(rs.getDate("endDate").toLocalDate());
            task.setDone(rs.getBoolean("isDone"));
            task.setHoursDone(rs.getDouble("hoursDone"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return task;

    }
    //---------------------------------------------------------------------------------------------------------------
    //OTHERS
    //---------------------------------------------------------------------------------------------------------------

    private void saveSkills(String username, ArrayList<String> skills) {
        String sql = "INSERT INTO emp_skill (username, skillID) VALUES (?, (SELECT skillID FROM skill WHERE skillName = ?));";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (String skill : skills) {
                pstmt.setString(1, username);
                pstmt.setString(2, skill);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



}
