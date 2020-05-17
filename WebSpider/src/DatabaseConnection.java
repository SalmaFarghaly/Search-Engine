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
	//url points to discoveredURL
	static public void incrementInBound(String discoveredURL) throws SQLException {
		String SQL="SELECT count(*) FROM url WHERE Link=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, discoveredURL);
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	     }
	     if (n>0) {
		    	String SQL2 = "UPDATE url set inBound=inBound+1 WHERE Link=?";
		    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, discoveredURL);
		    	ps.executeUpdate();
	     }
	     else {
	    	 String SQL2 = "INSERT INTO url (Link,inBound,outBound,doneIndexing) VALUES (?,?,?,?)";
		    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString( 1, discoveredURL);
		    	ps.setInt(2, 1);
		    	ps.setInt(3,0);
		    	ps.setInt(4,0);
		    	ps.executeUpdate();
	     }
	
	}
	
	
	static public void insertDocument(String url,String discoveredURL) throws SQLException {
	     String SQL3="INSERT INTO bounds (url,discoveredURL) VALUES (?,?)";
	     PreparedStatement ps= conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
	    	ps.setString( 1, url);
	    	ps.setString(2, discoveredURL);
	    	ps.executeUpdate();
		
	}
	static public void incrementOutBound(String url) throws SQLException {
		String SQL="UPDATE url set outBound=outBound+1 WHERE Link=?";
		PreparedStatement ps;
		ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
    	ps.setString(1,url);
    	ps.executeUpdate();
		
		
	}
	static public boolean isLinkExist(String url) throws SQLException {
		String SQL="SELECT count(*) FROM url WHERE Link=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, url);
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	    	    if(n>0)
	    	    	return true;
	    	    else 
	    	    	return false;
	     }
	     return false;
		
	}
	static public int getCountUrl() throws SQLException {
		String SQL="SELECT count(*) FROM url";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	    	    return n;
		
	}
	     return 0;
}
	
	static public void saveInitials(String url) throws SQLException {
    	 String SQL2 = "INSERT INTO url (Link,inBound,outBound,doneIndexing) VALUES (?,?,?,?)";
    		PreparedStatement ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
	    	ps.setString( 1, url);
	    	ps.setInt(2, 0);
	    	ps.setInt(3,0);
	    	ps.setInt(4,0);
	    	ps.executeUpdate();
	}
	
	static public  int isRelationExist(String url,String discoveredURL) throws SQLException {
		String SQL="SELECT count(*) FROM bounds WHERE url = ? and discoveredURL=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString( 1, url);
		ps.setString( 2, discoveredURL);
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	    	    return n;
		
	}
	     return 0;
		
	}
	
	static public void addStemmedWord(String word,String url,String pos) throws SQLException {
		String SQL="SELECT count(*) FROM indexing WHERE link=? and word=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ps.setString( 1, url);
	     ps.setString( 2, word);
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	     }
	     if (n>0) {
	    	 String SQL2=null;
	    	 if(pos=="h1") 
		    	SQL2 = "UPDATE indexing set h1=h1+1 WHERE link=? and word=?";
	    	 else if(pos=="h2") 
	    		 SQL2 = "UPDATE indexing set h2=h2+1 WHERE link=? and word=?";
	    	 else if(pos=="h3") 
	    		 SQL2 = "UPDATE indexing set h3=h3+1 WHERE link=? and word=?";
	    	 else if(pos=="h4") 
	    		 SQL2 = "UPDATE indexing set h4=h4+1 WHERE link=? and word=?";
	    	 else if(pos=="h5") 
	    		 SQL2 = "UPDATE indexing set h5=h5+1 WHERE link=? and word=?";
	    	 else if(pos=="h6") 
	    		 SQL2 = "UPDATE indexing set h6=h6+1 WHERE link=? and word=?";
	    	 else if(pos=="p") 
	    		 SQL2 = "UPDATE indexing set p=p+1 WHERE link=? and word=?";
	      	 else if(pos=="title") 
	    		 SQL2 = "UPDATE indexing set title=title+1 WHERE link=? and word=?";
			    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
			    	ps.setString(1, url);
			    	ps.setString(2, word);
			    	ps.executeUpdate();
	     }
	     else
	     {
	    	 String SQL3=null;
	    	 if(pos=="h1") {
	    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
			    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
			    	ps.setString(1, url);
			    	ps.setString(2, word);
			    	ps.setInt(3, 1);
			    	ps.setInt(4, 0);
			    	ps.setInt(5, 0);
			    	ps.setInt(6, 0);
			    	ps.setInt(7, 0);
			    	ps.setInt(8, 0);
			    	ps.setInt(9, 0);
			    	ps.setInt(10, 0);
			    	ps.executeUpdate();
	    		 
	    	 }
	    	 else if(pos=="h2") {
	    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
			    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
			    	ps.setString(1, url);
			    	ps.setString(2, word);
			    	ps.setInt(3, 0);
			    	ps.setInt(4, 1);
			    	ps.setInt(5, 0);
			    	ps.setInt(6, 0);
			    	ps.setInt(7, 0);
			    	ps.setInt(8, 0);
			    	ps.setInt(9, 0);
			    	ps.setInt(10, 0);
				    	ps.executeUpdate();
	    	 }
	    	 else if(pos=="h3") {
	    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
			    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
			    	ps.setString(1, url);
			    	ps.setString(2, word);
			    	ps.setInt(3, 0);
			    	ps.setInt(4, 0);
			    	ps.setInt(5, 1);
			    	ps.setInt(6, 0);
			    	ps.setInt(7, 0);
			    	ps.setInt(8, 0);
			    	ps.setInt(9, 0);
			    	ps.setInt(10, 0);
				    	ps.executeUpdate();
	    		 
	    	 }
			else if(pos=="h4") {
    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
		    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, url);
		    	ps.setString(2, word);
		    	ps.setInt(3, 0);
		    	ps.setInt(4, 0);
		    	ps.setInt(5, 0);
		    	ps.setInt(6, 1);
		    	ps.setInt(7, 0);
		    	ps.setInt(8, 0);
		    	ps.setInt(9, 0);
		    	ps.setInt(10, 0);
			    	ps.executeUpdate();
				    		 
			}
			else if(pos=="h5") {
    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
		    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, url);
		    	ps.setString(2, word);
		    	ps.setInt(3, 0);
		    	ps.setInt(4, 0);
		    	ps.setInt(5, 0);
		    	ps.setInt(6, 0);
		    	ps.setInt(7, 1);
		    	ps.setInt(8, 0);
		    	ps.setInt(9, 0);
		    	ps.setInt(10, 0);
			    	ps.executeUpdate();
				 
			}
			else if(pos=="h6") {
	    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
			    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
			    	ps.setString(1, url);
			    	ps.setString(2, word);
			    	ps.setInt(3, 0);
			    	ps.setInt(4, 0);
			    	ps.setInt(5, 0);
			    	ps.setInt(6, 0);
			    	ps.setInt(7, 0);
			    	ps.setInt(8, 1);
			    	ps.setInt(9, 0);
			    	ps.setInt(10, 0);
			    	ps.executeUpdate();
				 
			}
			else if(pos=="p") {
    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
		    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, url);
		    	ps.setString(2, word);
		    	ps.setInt(3, 0);
		    	ps.setInt(4, 0);
		    	ps.setInt(5, 0);
		    	ps.setInt(6, 0);
		    	ps.setInt(7, 0);
		    	ps.setInt(8, 0);
		    	ps.setInt(9, 1);
		    	ps.setInt(10, 0);
			    	ps.executeUpdate();
				
			}
			else if(pos=="title") {
    		 	SQL3 = "INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title) VALUES (?,?,?,?,?,?,?,?,?,?)";
		    	ps = conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString(1, url);
		    	ps.setString(2, word);
		    	ps.setInt(3, 0);
		    	ps.setInt(4, 0);
		    	ps.setInt(5, 0);
		    	ps.setInt(6, 0);
		    	ps.setInt(7, 0);
		    	ps.setInt(8, 0);
		    	ps.setInt(9, 0);
		    	ps.setInt(10, 1);
			    	ps.executeUpdate();
				
			}
	     }
		
	}
	static public String getFirstUnIndexed() throws SQLException {
		String SQL="SELECT Link FROM url WHERE doneIndexing =0 LIMIT 1";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ResultSet rs = ps.executeQuery();
	     String  url=null;
	     if ( rs.next() ) {
	    	    url= rs.getString(1);
	}
		    return url;
	}
	static public void SetDoneIndexing(String url) throws SQLException {
		String SQL="UPDATE url set doneIndexing=1 WHERE link=?";
		PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
    	ps.setString(1, url);
    	ps.executeUpdate();
	}
}
