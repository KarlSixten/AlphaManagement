//package org.example.alphamanagement.repository;
//
//import org.example.alphamanagement.repository.util.ConnectionManager;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import org.springframework.beans.factory.annotation.Value;
//
//public class TestDB {
//    @Value("${spring.datasource.url}")
//    private String url;
//    @Value("${spring.datasource.username}")
//    private String user;
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    public void projectTestDB(){
//        try {
//            Connection con = ConnectionManager.getConnection(url,user,password);
//
//            Statement statement = con.createStatement();
//
//            statement.addBatch("");
//        }catch (SQLException e){
//            throw new RuntimeException(e);
//        }
//    }
//}
