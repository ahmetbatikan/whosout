package com.example.asuspc.whosout;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by AsusPc on 1.10.2017.
 */

public class DatabaseHelper {
    private final String DB_URL = "jdbc:mysql://35.195.221.163/whosoutdb";

    private final String username = "root";
    private final String password = "123456";

    private Connection connect;
    private Statement state;

    public DatabaseHelper(){
        connect = null;
        state = null;
    }

    public void connectDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error in JDBC creation");
        }
        try{
            connect = DriverManager.getConnection(DB_URL, username, password);
            state = connect.createStatement();

            state.executeUpdate("USE whosoutdb");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getImageNotSeen(){
        try {
            String sql = "SELECT * FROM Photo WHERE flag='1';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            int index=0;
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                File root = new File(Environment.getExternalStorageDirectory(), "Images_Whosout");
                if (!root.exists()) {
                    root.mkdirs(); // this will create folder.
                }
                File image = new File(root,"image_test"+(++index)+".jpg");
                FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = resultSet.getBinaryStream(3);
                while (is.read(buffer) > 0) {
                    fos.write(buffer);
                }
                fos.close();
            }
        }
        catch (Exception e){}
    }

    public void getImageDay(){

    }
}