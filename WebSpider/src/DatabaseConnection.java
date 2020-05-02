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
	         ///DriverManager.se
	         System.out.print("Database Connected\n");
	     } catch (Exception e) {
	         System.out.print("Database Not Connected"+e);
	     }
	  }
	static public void insert(String query){
	    try{
	     String SQL = "INSERT INTO url (Link)"+" VALUES (?)";
         PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, query );
	     ps.executeUpdate();
	     System.out.print("Inserted\n");
	    } 
	    catch(Exception e){
	        System.out.print("Not Inserted"+e);
	    }
	}
	
	static public void incrementInBound(String discoveredURL) throws SQLException {
//		System.out.print("Came Here\n");
		String SQL="SELECT count(*) FROM url WHERE Link=?";
		 PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, discoveredURL);
	     System.out.print("Came Here\n");
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     System.out.print("get Hereeeee\n");
	   ///  System.out.print(rs+"\n");
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	     }
	     if (n>0) {
	    	/// final int count = rs.getInt(1);
	    	/// System.out.print(count+"\n");
	    	 
	         // Quest already completed
		    	String SQL2 = "UPDATE url set outBound=outBound+1 WHERE Link=?";
		    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, discoveredURL);
		    	ps.executeUpdate();
	     }
	     else {
	    	 System.out.print("NOT EXIST\n");
	    	 String SQL2 = "INSERT INTO url (Link,outBound) VALUES (?,?)";
		    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString( 1, discoveredURL);
		    	ps.setInt(2, 0);
		    	ps.executeUpdate();
		    	///insert(discoveredURL);
	         // Quest not completed yet
	     }
	
	     
	    //// System.out.print("ENDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
	     
		
	}

}
