package org.example.alphamanagement.repository;

import org.example.alphamanagement.model.Emp;
import org.example.alphamanagement.model.Project;
import org.example.alphamanagement.repository.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
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
        if (createUserID(newEmp.getUsername()) != null) {
            String sql = "INSERT INTO emp(username, password, jobTypeID) VALUES (?, ?, ?);";
            Connection connection = ConnectionManager.getConnection(url, user, password);

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                newEmp.setUsername(createUserID(newEmp.getUsername()));
                pstmt.setString(1, newEmp.getUsername());
                pstmt.setString(2, newEmp.getPassword());
                pstmt.setInt(3, newEmp.getJobType());
                pstmt.executeUpdate();
                return newEmp;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }


    public boolean checkUniqueUsername(String username) {
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

    public String createUserID(String fullName) {
        String userID;
        String[] names = fullName.split("\\s+");
        String userIDLetters = names[0].substring(0, 2).toLowerCase() +
                names[names.length - 1].substring(0, 2).toLowerCase();
        Random random = new Random();
        int numbers = random.nextInt(10000);
        userID = String.format("%s%04d", userIDLetters, numbers);
        if (checkUniqueUsername(userID)) {
            return userID;
        }
        return null;
    }

    public Emp checkValidLogin(Emp empToCheck) {
        String sql = "SELECT * FROM emp WHERE username LIKE (?) AND password LIKE (?);";
        Connection connection = ConnectionManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, empToCheck.getUsername());
            pstmt.setString(2, empToCheck.getPassword());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return empToCheck;
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
            PreparedStatement preparedStatement = con.prepareStatement(SQL);
            preparedStatement.setString(1, newProject.getProjectName());
            preparedStatement.setDate(2, java.sql.Date.valueOf(newProject.getStartDate()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(newProject.getEndDate()));
            preparedStatement.executeUpdate();
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
        String SQL = "SELECT projectname, startdate, enddate FROM project";
        Connection con = ConnectionManager.getConnection(url,user,password);
        try {
             PreparedStatement preparedStatement = con.prepareStatement(SQL);
             ResultSet rs = preparedStatement.executeQuery(); {

            while (rs.next()) {
                String projectName = rs.getString("projectName");
                LocalDate startDate = rs.getDate("startDate").toLocalDate();
                LocalDate endDate = rs.getDate("endDate").toLocalDate();
                projects.add(new Project(projectName, startDate, endDate));
            }}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }
}
