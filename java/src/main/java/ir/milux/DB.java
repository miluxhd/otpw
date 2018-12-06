package ir.milux;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DB {
    String store ="insert into password values(?,?,?,?,?)";
    String getPassword = "select phone,password from password where user=? and uuid=? and challenge=?";
    String getPhone = "select phone from password where user=? and uuid=?";
    String purge = "delete from password where user=? and uuid=?";
    Connection con = null;

    public DB() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Properties.getProperty ("master.db.name"),
                Properties.getProperty ("master.db.user"),Properties.getProperty ("master.db.password"));
    }
    public void store(  String user , String phone, String uuid,HashMap<String,String> password) throws ClassNotFoundException, SQLException {

        PreparedStatement statement =con.prepareStatement(store);
        statement.setString (1,uuid);
        statement.setString (2,user);
        for (Map.Entry<String, String> s :
                password.entrySet ()) {
            statement.setString (3,s.getKey ());
            statement.setString (4,s.getValue ());
            statement.setString (5,phone);
            statement.execute ();
        }
    }
    public String getPassword(String user, String uuid, String challenge) throws SQLException {
        PreparedStatement statement = con.prepareStatement (getPassword);
        statement.setString (1,user);
        statement.setString (2,uuid);
        statement.setString (3,challenge);
        ResultSet results = statement.executeQuery ();
        while (results.next ()){
           return results.getString ("password");
        }
        return  null;
    }
    public String getPhoneNumber(String user,String uuid) throws SQLException {
        PreparedStatement statement = con.prepareStatement (getPhone);
        statement.setString (1,user);
        statement.setString (2,uuid);
        ResultSet results = statement.executeQuery ();
        while (results.next ()){
            return results.getString ("phone");
        }
        return  null;
    }
    public boolean flushUser(String user,String uuid) throws SQLException {
        PreparedStatement statement = con.prepareStatement (purge);
        statement.setString (1,user);
        statement.setString (2,uuid);
        return statement.execute ();
    }
}
