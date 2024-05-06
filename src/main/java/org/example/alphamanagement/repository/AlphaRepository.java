package org.example.alphamanagement.repository;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.repository.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Repository
public class AlphaRepository {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;


    public Emp createEmp(Emp newEmp) {
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
            return newEmp;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private String createUsername(String firstName, String lastName) {
        Random random = new Random();
        String username = "";

        do {
            username = firstName.substring(0, 2) + lastName.substring(0, 2) + random.nextInt(10000);
        } while (!usernameIsUnique(username));

        return username;
    }

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

    public Project createProject(Project newProject) {
        String SQL = "INSERT INTO PROJECT(PROJECTNAME, STARTDATE, ENDDATE) values (?,?,?)";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newProject.getProjectName());
            preparedStatement.setDate(2, java.sql.Date.valueOf(newProject.getStartDate()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(newProject.getEndDate()));
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedProjectID = generatedKeys.getInt(1);
                newProject.setProjectID(generatedProjectID);
            } else {
                throw new SQLException("Creating project failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newProject;
    }

    public Emp findEmpByUsername(String username) {
        Emp emp = new Emp();
        String sql = "SELECT * FROM emp WHERE username like (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, username);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setJobType(rs.getInt("jobTypeID"));
            }
            return emp;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEmp(String username) {
        String sql = "DELETE FROM EMP WHERE username = ?";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String SQL = "SELECT * FROM project";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(SQL);
            ResultSet rs = preparedStatement.executeQuery();
            {

                while (rs.next()) {
                    Project project = new Project();
                    project.setProjectID(rs.getInt("projectID"));
                    String projectName = rs.getString("projectName");
                    LocalDate startDate = rs.getDate("startDate").toLocalDate();
                    LocalDate endDate = rs.getDate("endDate").toLocalDate();
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
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

    public Emp updateEmp(Emp emp, List<String> skills) {
        String updateEmpQuery = "UPDATE emp SET firstName = ?, lastName = ?, password = ?, jobType = ?;";
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
            updateEmpStatement.executeUpdate();

            delete.setString(1, emp.getUsername());
            delete.executeUpdate();

            for (String skill : skills) {
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

    public Project updateProject(Project project) {
        String SQL = "UPDATE project SET projectName = ?, startDate = ?, endDate = ? WHERE projectId = ?;";
        Connection con = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(SQL);
            preparedStatement.setString(1, project.getProjectName());
            preparedStatement.setDate(2, java.sql.Date.valueOf(project.getStartDate()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(project.getEndDate()));
            preparedStatement.setInt(4, project.getProjectID());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    private Project createProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setProjectID(rs.getInt("projectId"));
        project.setProjectName(rs.getString("projectName"));
        project.setStartDate(rs.getDate("startDate").toLocalDate());
        project.setEndDate(rs.getDate("endDate").toLocalDate());
        return project;
    }

    public Project findProjectByID(int projectID) {
        String sql = "SELECT * FROM project WHERE projectId = ?;";
        Connection connection = ConnectionManager.getConnection(url, user, password);
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, projectID);
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


}
