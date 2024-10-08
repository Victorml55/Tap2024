package com.example.tap2024.models;

import java.sql.DriverManager;
import java.sql.Connection;

public class Conexion {

    static private String DB    ="spotify";
    static private String USER  ="admin";
    static private String PWD  ="12345678";
    static private String HOST  ="localhost";
    static private String PORT  ="3306";
    public static Connection connection;

    public static void CrearConexion() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection=DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/"+DB,USER,PWD );
            System.out.println("Conexion establecida a la Base De Datos");
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}