package dBLinkPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleDBLink {

    public static ResultSet execQuery() throws SQLException, ClassNotFoundException{
        //Load the database driver
        Class.forName("oracle.jdbc.OracleDriver");

        //Create connection to the database
        Connection myConnection = DriverManager.getConnection("jdbc:oracle:thin:@//192.168.1.105:1521/XEPDB1","SYSTEM","Filenet8");

        //Create a statement link to the database for running queries
        Statement myQuery = myConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

        //Create a resultSet to hold the returned query information				
        ResultSet myQueryResults = myQuery.executeQuery("select * from HR.HRMS@'DBMS_CLRDBLINK'");       

        return myQueryResults;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			ResultSet myQueryResults = execQuery();
			System.out.println( myQueryResults.getMetaData().getColumnCount() );
			System.out.println("Done");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//main method

}// class
