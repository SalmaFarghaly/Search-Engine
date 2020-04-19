import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DatabaseConnection {
	
	
	static Statement statement;
	static Connection conn;

	//function to connect to the xampp server      
	static public void DatabaseConnect(){
	     try {
	    	 Class.forName("com.mysql.cj.jdbc.Driver");
	    	
	        conn= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root",""); 
	         statement = conn.createStatement();
	         System.out.print("Database Connected\n");
	     } catch (Exception e) {
	         System.out.print("Database Not Connected"+e);
	     }
	  }
	static public void insert(String query){
	    try{
	     String SQL = "INSERT INTO URLS (Link)"+" VALUES (?)";
         PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, query );
	     ps.executeUpdate();
	     System.out.print("Inserted\n");
	    } 
	    catch(Exception e){
	        System.out.print("Not Inserted"+e);
	    }
	}

}
