package com.example;
import java.sql.*;
    public class Main {
        static final String DB_URL = "jdbc:mysql://localhost:3306/emiliadb";
        static final String USER = "root";
        static final String PASS = "mysql2311";

        public static void main(String[] args) {
            Statement stmt = null;
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();

                System.out.println("Below the list of our available pizzas");
                System.out.println();

                ResultSet rs = stmt.executeQuery("SELECT pizza_id, pizza_name FROM Pizzas");
                while (rs.next()) {
                    System.out.print(rs.getInt("pizza_id"));
                    System.out.println(". Pizza: " + rs.getString("pizza_name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }