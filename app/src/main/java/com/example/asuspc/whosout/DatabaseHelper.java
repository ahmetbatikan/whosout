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
    private final String password = "199495";

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
        File root = new File(Environment.getExternalStorageDirectory(), "Images_Whosout");

        try {
            String sql = "SELECT * FROM Photo WHERE flag='1' AND name='Visitor';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            int index=0;
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                if (!root.exists()) {
                    root.mkdirs(); // this will create folder.
                }
                root.mkdirs();
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
        //set the flag zero nd the next message old images not shown
        try{
            String sql = "UPDATE Photo SET flag='0' WHERE flag='1' AND name='Visitor';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.executeUpdate(sql);
        }
        catch (Exception e){}

    }

    public void getImageAll(){
        File root = new File(Environment.getExternalStorageDirectory(), "Images_Whosout");

        try {
            String sql = "SELECT * FROM Photo";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            int index=0;
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(5);
                if (!root.exists()) {
                    root.mkdirs(); // this will create folder.
                }
                root.mkdirs();
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

    public void getImageSecurity(){
        try {
            String sql = "SELECT * FROM Photo WHERE flag='1' AND name='Security';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            int index=0;
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(5);
                File root = new File(Environment.getExternalStorageDirectory(), "Images_Whosout");
                if (root.exists()) {
                    deleteDirectory(root);//root.mkdirs(); // this will create folder.
                }
                root.mkdirs();
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
        //set the flag zero nd the next message old images not shown
        try{
            String sql = "UPDATE Photo SET flag='0' WHERE flag='1' AND name='Security';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.executeUpdate(sql);
        }
        catch (Exception e){}
    }
    public int[] checkNewSecurity(){
        int[] arr= new int[3];
        int check=0;
        int visitor=0;
        int security=0;

        try {
            String sql = "SELECT * FROM Photo WHERE flag='1';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                check++;
                if(resultSet.getString(2).equals("Visitor")){visitor++;}
                if(resultSet.getString(2).equals("Security")){security++;}
            }
        }
        catch (Exception e){}
        arr[0]=check;
        arr[1]=visitor;
        arr[2]=security;
        return arr;
    }

    public void sendMessages(String msg){
        try {
            String sql = " INSERT INTO Message(source, flag, mesaj)" + " VALUE (?, ?, ?)";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.setString (1, "User");
            stmt.setInt (2, 1);
            stmt.setString   (3, msg);
            stmt.execute();
            connect.close();
        }
        catch (Exception e){}
    }

    public String receiveMessages(){
        String temp_txt="";
        try {
            String sql = "SELECT * FROM Message WHERE flag='1' AND source='Door';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                temp_txt +="\n* " + resultSet.getString(4);
            }
        }
        catch (Exception e){}
        //set the flag zero nd the next message old images not shown
        try{
            String sql = "UPDATE Message SET flag='0' WHERE flag='1' AND source='Door';";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.executeUpdate(sql);
        }
        catch (Exception e){}
        return temp_txt;
    }

    public int checkButton()
    {
        try {
            int lock=0;
            int light=0;
            int check=0;
            String query = "SELECT * FROM Security";
            PreparedStatement stmt = connect.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next())
            {
                lock = resultSet.getInt(2);
                light = resultSet.getInt(3);
                if (lock ==1)
                {
                    check += 1;
                }
                if (light ==1)
                {
                    check += 10;
                }
            }
            return check;
        }
        catch (Exception e){return -10;}
    }

    public boolean updateDoorLock(int lock)
    {
        try {
            String query = "update Security set doorlock='"+lock+"'";
            state = connect.prepareStatement(query);
            state.executeUpdate(query);
        }
        catch (Exception e){return false;}
        return true;
    }
    public boolean updateLamp(int lamp)
    {
        try {

            String query = "update Security set light='"+lamp+"'";
            state = connect.prepareStatement(query);
            state.executeUpdate(query);
            String query2 = "update Security set flag='1'";
            state = connect.prepareStatement(query2);
            state.executeUpdate(query2);

        }
        catch (Exception e){return false;}
        return true;
    }


    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                //    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
}
