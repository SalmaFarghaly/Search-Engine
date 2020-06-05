import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//=================This class contains all the database queries used in all modules=======================//
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
	    	 String SQL2 = "INSERT INTO url (Link,inBound,outBound,doneIndexing,startIndexing,id,rank,count) VALUES (?,?,?,?,?,?,?,?)";
		    	ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
		    	ps.setString( 1, discoveredURL);
		    	ps.setInt(2, 1);
		    	ps.setInt(3,0);
		    	ps.setInt(4,0);
		    	ps.setInt(5,0);
		    	ps.setInt(6,0);
		    	ps.setInt(7,0);
		    	ps.setInt(8,0);
		    	ps.executeUpdate();
	     }
	
	}
	static public ArrayList<String> GetOutBoundLinks(String url) throws SQLException {
		ArrayList<String> outBoundLinks= new ArrayList<String>();
		int i=0;
		String link=null;
		String SQL="SELECT discoveredURL FROM bounds WHERE url= ? ";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, url);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			outBoundLinks.add(rs.getString(1));
		}		
		return outBoundLinks;
	}
	
	
	static public void insertDocument(String url,String discoveredURL) throws SQLException {
	     String SQL3="INSERT INTO bounds (url,discoveredURL) VALUES (?,?)";
	     PreparedStatement ps= conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
	    	ps.setString( 1, url);
	    	ps.setString(2, discoveredURL);
	    	ps.executeUpdate();
		
	}
	static public void insertDocuments(String url,ArrayList<String>discoveredURLS) throws SQLException {
		  String SQL3="INSERT INTO bounds (url,discoveredURL) VALUES ";//(?,?)";
		  int c=discoveredURLS.size()*2;
		  for(int i=0;i<discoveredURLS.size();i++) {
			  SQL3+="(?,?)";
			  if(i!=discoveredURLS.size()-1)
				  SQL3+=",";
		  }
		  PreparedStatement ps= conn.prepareStatement( SQL3, Statement.RETURN_GENERATED_KEYS );
		  int k=0;
		  for(int i=1;i<=c;i++) {
			  if(i%2==1)
		    	ps.setString(i, url);
			  else if(i%2==0) {
		    	ps.setString(i,discoveredURLS.get(k));
		    	k++;
		    	}
		  }
		    	ps.executeUpdate();
	}
	static public void incrementOutBound(String url,int t) throws SQLException {
		String SQL="UPDATE url set outBound=outBound+? WHERE Link=?";
		PreparedStatement ps;
		ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setInt(1, t);
    	ps.setString(2,url);
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
    	 String SQL2 = "INSERT INTO url (Link,inBound,outBound,doneIndexing,startIndexing,id,rank,count) VALUES (?,?,?,?,?,?,?,?)";
    		PreparedStatement ps = conn.prepareStatement( SQL2, Statement.RETURN_GENERATED_KEYS );
	    	ps.setString( 1, url);
	    	ps.setInt(2, 0);
	    	ps.setInt(3,0);
	    	ps.setInt(4,0);
	    	ps.setInt(5,0);
	    	ps.setInt(6,0);
	    	ps.setInt(7,0);
	    	ps.setInt(8,0);
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
	     return n;
		
	}
	static public void deleteLink(String url) throws SQLException {
		String SQL="DELETE FROM url WHERE link=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1,url);
		ps.executeUpdate();
		
	}
	
	static public void addWords(ArrayList<Word> tokens,String url,Integer count) throws SQLException {
	String SQL="INSERT INTO indexing (link,word,h1,h2,h3,h4,h5,h6,p,title,italic,bold) VALUES ";//(?,?,?,?,?,?,?,?,?,?,?,?)";
	for(int i=0;i<tokens.size();i++) {
		SQL=SQL+"(?,?,?,?,?,?,?,?,?,?,?,?)";
		if(i!=tokens.size()-1)
			SQL=SQL+",";
	}
	PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	int c=tokens.size()*12;
	int k=0;
	for(int i=1;i<=c;i++) {
		if(i%12==1)
			ps.setString(i,url);
		else if(i%12==2)
			ps.setString(i, tokens.get(k).word);
		else if(i%12==3)
			ps.setInt(i, tokens.get(k).h1);
		else if(i%12==4)
			ps.setInt(i, tokens.get(k).h2);
		else if(i%12==5)
			ps.setInt(i, tokens.get(k).h3);
		else if(i%12==6)
			ps.setInt(i, tokens.get(k).h4);
		else if(i%12==7)
			ps.setInt(i, tokens.get(k).h5);
		else if(i%12==8)
			ps.setInt(i, tokens.get(k).h6);
		else if(i%12==9)
			ps.setInt(i, tokens.get(k).p);
		else if(i%12==10)
			ps.setInt(i, tokens.get(k).title);
		else if(i%12==11)
			ps.setInt(i, tokens.get(k).italic);
		else if(i%12==0)
			{
			ps.setInt(i, tokens.get(k).bold);
			k++;}
		
	}
	ps.executeUpdate();
	
	SQL="UPDATE url SET count =? WHERE link=? ";
	ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	ps.setInt(1, count);
	ps.setString(2,url);
	ps.executeUpdate();
	
}
	static public int getCountUnindexed() {
		String SQL="SELECT 1 FROM indexing WHERE startIndexing=0 and doneIndexing LIMIT 1";
		  int n=0;
		try {
			PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
			ResultSet rs = ps.executeQuery();
		   
		     if ( rs.next() ) {
		    	    n = rs.getInt(1);
		     }
		}
		catch(Exception e) {
			
		}
	     return n;
	}
	static public String getFirstUnIndexed(int num) throws SQLException {
		String SQL="SELECT Link FROM url WHERE doneIndexing =0 and startIndexing=0 LIMIT 1";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
//		ps.setInt(1, num-1);
	     ResultSet rs = ps.executeQuery();
	     String  url=null;
	     if ( rs.next() ) {
	    	    url= rs.getString(1);
	}
		    return url;
	}
	static public void setStartIndexing(String url)  throws SQLException {
		String SQL="UPDATE url set startIndexing=1 WHERE link=?";
		PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
    	ps.setString(1, url);
    	ps.executeUpdate();
		
	}
	static public void SetDoneIndexing(String url) throws SQLException {
		String SQL="UPDATE url set doneIndexing=1 WHERE link=?";
		PreparedStatement ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
    	ps.setString(1, url);
    	ps.executeUpdate();
	}
	static public boolean isThreadStateEmpty()throws SQLException{
		String SQL="SELECT count(*) FROM threadstate ";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	     ResultSet rs = ps.executeQuery();
	     int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	    	    if(n>0)
	    	    	return false;
	    	    else 
	    	    	return true;
	     }
	     return true;
		
	}
	static public String getThreadUrl(int ThreadNo) throws SQLException{
		String SQL ="SELECT link FROM threadstate WHERE ThreadNo=? ";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setInt(1, ThreadNo);
		ResultSet rs=ps.executeQuery();
		String  url=null;
	     if ( rs.next() ) {
	    	    url= rs.getString(1);
	}
	     return url;
	}
	static public void saveThreadState(int ThreadNo,String url) throws SQLException {
		String SQL="SELECT count(*) FROM threadstate WHERE ThreadNo=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setInt(1, ThreadNo);
		ResultSet rs=ps.executeQuery();
		 int n=0;
	     if ( rs.next() ) {
	    	    n = rs.getInt(1);
	    	    //UPDATE the state of the thread
	    	    if(n>0) {
	    	    	SQL="UPDATE threadstate set Link=? WHERE ThreadNo=?";
	    			ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	    	    	ps.setString(1, url);
	    	    	ps.setInt(2, ThreadNo);
	    	    	ps.executeUpdate();
	    	    }
	    	    //Save the initial state of thread
	    	    else {
	    	    	// 	 String SQL2 = "INSERT INTO url (Link,inBound,outBound,doneIndexing) VALUES (?,?,?,?)";
	    	    	SQL="INSERT INTO threadstate (ThreadNo,Link) VALUES (?,?)";
	    			ps = conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
	    	    	ps.setInt(1, ThreadNo);
	    	    	ps.setString(2, url);
	    	    	ps.executeUpdate();
	    	    	
	    	    }
	    	    	
	     }
	    
	}
	//delete all words in indexing table that there links aren't done indexing 
	static public void deleteNonDoneIndexingWords() throws SQLException {
		String SQL="SELECT Link FROM url WHERE doneIndexing =0 and startIndexing=1 LIMIT 5";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		 ResultSet rs = ps.executeQuery();
		     String  urls=null;
		     if ( rs.next() ) {
		    	    urls= rs.getString(1);
		}
		if(urls==null)
			return;
		SQL="DELETE FROM indexing WHERE link IN (?)";
		ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, urls);
		ps.executeUpdate();
		//set all the start indexing to 0
		SQL="UPDATE url set startIndexing=0 WHERE doneIndexing=0 and startIndexing=1";
		ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.executeUpdate();
	}
	static public void dropIndexingTable() throws SQLException {
		//drop table
		String SQL="DELETE FROM indexing";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.executeUpdate();
		
	}
	static public void resetDoneAndStartIndexing() throws SQLException {
		String SQL ="UPDATE url set doneIndexing=0,startIndexing=0";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.executeUpdate();
	}
	static public void updateIDIncrementally() throws SQLException{
		//String SQL ="select @i := -1;update url set id = (select @i := @i + 1);";
		//"ALTER TABLE tbl AUTO_INCREMENT = -1";
		String SQL="SET @a = -1;UPDATE url SET id = @a:=@a+1;";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		//ps.setInt(1, -1);
		ps.executeUpdate();
		
	}

	//--------------------------------- Ranking Queries -----------------------------//
	static public int getTotalDocuments() throws SQLException {
		String SQL="SELECT count(*) FROM url";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		 ResultSet rs = ps.executeQuery();
	       int total=0;
	     if ( rs.next() ) {
	    	    total= rs.getInt(1);
	}
		    return total;
	}

	//URLs length
	static public int getDocumentslength (String url) throws SQLException{
		String SQL="SELECT count FROM url WHERE Link= ?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1,url);
		ResultSet rs = ps.executeQuery();
		int n =-1;
		String x = null;
	     if ( rs.next() ) {
	    	  n=rs.getInt(1);
	     }
		return n;
		
	}
	
	//URL that contain the word and their TF
	static public Map<Integer,String> getDocumentsinfo (String term) throws SQLException{
		String SQL="SELECT link,(title+h1+h2+h3+h4+h5+h6+p) FROM indexing where word = '"+term+"'";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		 ResultSet rs = ps.executeQuery();
		 System.out.print("Created... ");
		 Map<Integer,String> DocLengthmap=new HashMap<Integer,String>(); 
		 int i=0;
	       while ( rs.next() ) {
	    	   System.out.print("filling... ");  
	    	  String s = rs.getString(1);
	    	  int t = rs.getInt(2);
	    	  DocLengthmap.put(t,s);
	    	  
	    	  
	     }
	      return DocLengthmap;
	}
	static public int getOutboundCount(int id) throws SQLException {
		String SQL="SELECT outBound FROM url WHERE id= ?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		int n =-1;
		String x = null;
	     if ( rs.next() ) {
	    	  n=rs.getInt(1);
	     }
		return n;
		
	}
	static public List<Integer> getOutBoundLinks(int id) throws SQLException {
		List<Integer> outBoundLinks= new ArrayList<Integer>();
		int i=0;
		String link=null;
		String SQL="SELECT link FROM url WHERE id= ? ";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			link=rs.getString(1);
		}
		SQL="SELECT discoveredURL FROM bounds WHERE url= ?";
		ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, link);
		rs = ps.executeQuery();
		List<String> urls=new ArrayList<String>();
		while(rs.next()) {
			urls.add(rs.getString(1));
			i++;
		}
		SQL="SELECT id FROM url WHERE link=";
		for(int j=0;j<urls.size();j++) {
			SQL+="?";
			ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
			ps.setString(1, urls.get(j));
			rs = ps.executeQuery();
			if(rs.next()) {
				outBoundLinks.add(rs.getInt(1));
			}
			SQL="SELECT id FROM url WHERE link=";
		}
		return outBoundLinks;
	}
	//get document id given it's url
	static public int getDocumentID (String url) throws SQLException {
		String SQL="SELECT id from url WHERE link=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, url);
		int n=-1;
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			n=rs.getInt(1);
		}
		return n;
	}
	static public void savePageRank(int id,double pageRank) throws SQLException {
		String SQL="UPDATE url set rank=? WHERE id=?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setDouble(1, pageRank);
		ps.setInt(2, id);
		ps.executeUpdate();
	}     
		
	public static double getLinkPageRank(String key) throws SQLException {
		// TODO Auto-generated method stub
		String SQL="Select rank FROM url WHERE Link= ?";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, key);
		ResultSet rs = ps.executeQuery();
		double d = 0;
		if(rs.next()) {
			d=rs.getDouble(1);
		}
		return d;
	}
	public static void saveSearchQuery(String q) throws SQLException {
		String SQL="SELECT count(*) FROM searchqueries WHERE query=?";
		System.out.print(SQL+"\n");
		Connection conn2= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root","");
		PreparedStatement ps= conn2.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, q);
		ResultSet rs = ps.executeQuery();
		int d = 0;
		if(rs.next()) {
			d=rs.getInt(1);
		}
		if(d>0) {
			//update count
			SQL="UPDATE searchqueries set count=count+1 WHERE query=?";
			ps= conn2.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
			ps.setString(1, q);
			ps.executeUpdate();
		}
		else {
			SQL="INSERT INTO searchqueries(query,count) VALUES(?,?)";
			ps= conn2.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
			ps.setString(1, q);
			ps.setInt(2, 1);
			ps.executeUpdate();
			
			
		}
		
	}
	public static void saveRankerResults(List<String>parsedQuery,ArrayList<String> m ) {
		String q=parsedQuery.get(0);
		for(int i=1;i<parsedQuery.size();i++)
			q=q+" "+parsedQuery.get(i);
		String SQL="INSERT INTO rankerresult(query,result) VALUES";
		for(int j=0;j<m.size();j++) {
				SQL=SQL+"(?,?)";
				if(j!=m.size()-1)
					SQL=SQL+",";
		}
		try {
			int c=2*m.size();
			System.out.print("ccccc\n"+c+"\n");
			PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
			int k=0;
			for(int i=1;i<=c;i++) {
				if(i%2==1)
					ps.setString(i, q);
				else {
					ps.setString(i,m.get(k));
					k++;
				}
					
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static boolean isSearchQueryExist(List<String> query) throws SQLException {
		String q=query.get(0);
		for(int k=1;k<query.size();k++)
			q=q+" "+query.get(k);
		String SQL="SELECT query FROM rankerresult WHERE query=? LIMIT 1";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, q);
		ResultSet rs = ps.executeQuery();
		String d="";
		if(rs.next()) {
			d=rs.getString(1);
		}
		if(d.isEmpty())
			return false;
		else
			return true;
		
	}
	public static  ArrayList<String> getQueryResult(List<String> query,int pid) throws SQLException{
		ArrayList<String>Urls=new ArrayList();
		String q=query.get(0);
		for(int k=1;k<query.size();k++)
			q=q+" "+query.get(k);
		int p=10*(pid-1);
		String SQL ="SELECT result FROM rankerresult WHERE query=? LIMIT ?,10";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, q);
		ps.setInt(2, p);
		ResultSet rs = ps.executeQuery();
		String d="";
		int k=0;
		while(rs.next()) {
			Urls.add(rs.getString(1));
		}
		return Urls;
	}
	public static  int getQueryLengthResult(List<String> query) throws SQLException{
		String q=query.get(0);
		for(int k=1;k<query.size();k++)
			q=q+" "+query.get(k);
		String SQL ="SELECT count(*) FROM rankerresult WHERE query=? ";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ps.setString(1, q);
		ResultSet rs = ps.executeQuery();
		int result = 0;
		if(rs.next()) {
			result = rs.getInt(1);
		}
		return result;
	}
}



