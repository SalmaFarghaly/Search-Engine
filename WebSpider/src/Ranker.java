import java.io.IOException;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.FileReader;

public class Ranker {
	static int totalDocuments = 0;
	static List<String> URLs = new ArrayList<String>(),URLsMoreWords = new ArrayList<String>();
	static List<Integer> TF = new ArrayList<Integer>(),TFMoreWords = new ArrayList<Integer>(); 
	static List<Double> TFIDFSingleWord = new ArrayList<Double>();
//	static List<DocInfo> Documents = new ArrayList<DocInfo>();
//	static int[] Docs_Contain_term ;
	static Map< String,Double> Docs =  
             new HashMap< String,Double>(); 
	static Connection conn;
	
	 public static void main(String[] args) throws IOException, SQLException {
			//Connect to DataBase
	    	DatabaseConnection.DatabaseConnect();
	    	conn= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root","");
	    	getTotalDocuments();
	    	
	 
	    	List<String> inputString = QueryProcessor.GetParsedQuery();
	//    	Docs_Contain_term = new int[inputString.size()];
	    	for(int i=0; i<inputString.size(); i++) {
	//    		Docs_Contain_term[i]=0;
	    		getFilteredDocuments(inputString.get(i),i);
		   }
	    	
	    	
	    	
	    }

	private static void getFilteredDocuments(String string,int termPosition) throws SQLException {
		// TODO Auto-generated method stub
		int Docs_Contain_term = 0;
				
		//----------------- URLs that contain that word and its frequency Query ---------------------//
		String SQL="SELECT link,(title+h1+h2+h3+h4+h5+h6+p) FROM indexing where word = '"+string+"'";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ResultSet rs = ps.executeQuery();
		   while ( rs.next() ) {
			// get the total number of docs containing this word 
			   Docs_Contain_term++;
	    	   System.out.print("filling... ");  
	    	  String s = rs.getString(1);
	      	  System.out.println(s);
	    	  int t = rs.getInt(2);
	    	  if(!URLs.contains(s)) {
		    	 URLs.add(s);
		    	 TF.add(t);
		    }
	    	  else {
	    		  URLsMoreWords.add(s);
	    		  TFMoreWords.add(t);
	    	  }
	       }
	    //--------------------- End of Query -------------------------------------------------------------	  
	       
	       for(int i=0; i<TFMoreWords.size(); i++) {
	    	   System.out.print("More words...."+URLsMoreWords.get(i));
	    	   System.out.print("\r\n");
	       }
	       for(int i=0; i<URLs.size(); i++) {
	    	   System.out.print("Single word...."+URLs.get(i));
	    	   System.out.print("\r\n");
	       }
	   	 
		//doc.getTermFrequency() * Math.log10(totalDocuments / docListLength)
		 for(int i=0; i<URLs.size(); i++) {
			 // get the number of terms in the single document
			
			 String URL = URLs.get(i);
			 if(Docs.containsKey(URL) & !URLsMoreWords.contains(URL) ) // was calculated before
				continue;	
			 System.out.println("Calculating for URL "+URL);
			  int DocLength = DatabaseConnection.getDocumentslength(URL);
			  System.out.print("Length: "+ DocLength);
			  double IDF = (totalDocuments / Docs_Contain_term);
			  double TFCalc = (float)TF.get(i)/DocLength;
			  System.out.print(" TFcalc "+TFCalc+" TF "+TF.get(i)+" IDF "+Math.log10(IDF));
			  double tfIdf = TFCalc * Math.log10(IDF);	
			  System.out.print("---- total---- "+TFCalc * Math.log10(IDF));
			  System.out.print("\r\n");
			 if(!URLsMoreWords.contains(URL)) {
				Docs.put(URL, tfIdf);
			 }
			 else {
				  System.out.print("trying to get the doubled URL"+URL);
				  System.out.print("\r\n");
			//	 int index = Documents.indexOf(d);
				  Double D = Docs.get(URL);
				  D += tfIdf;
				  Docs.put(URL, D);
				
			 }
			 System.out.print("\r\n");
						
		 }
		 System.out.print("\r\n");
		// Set< Map.Entry< String,Double> > st = Docs.entrySet();    
		
		 Docs.entrySet().stream()
		   .sorted(Map.Entry.comparingByValue())
		   .forEach(System.out::println);
	     double maxValueInMap=(Collections.max(Docs.values()));  // This will return max value in the Hashmap
	     System.out.print("\r\n");
	     System.out.print("Max value:"+maxValueInMap );
	     for (Entry<String, Double> entry : Docs.entrySet()) {  // Itrate through hashmap
	            if (entry.getValue()==maxValueInMap) {
	                System.out.println(entry.getKey());     // Print the key with max value
	            }
	        }
		 /*
	       for (Map.Entry< String,Double> me:st) 
	       { 
	           System.out.print("URL"+me.getKey()+":"); 
	           System.out.println(" TF-IDF: "+me.getValue()); 
	       } 
			*/		
		}
		


	
	public static void getTotalDocuments() throws SQLException {
		totalDocuments= DatabaseConnection.getTotalDocuments();
	}
	
}