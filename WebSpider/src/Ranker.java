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
	private static List<ArrayList<Integer>> adjList;
	static List<String> URLs = new ArrayList<String>(),URLsMoreWords = new ArrayList<String>();
	static List<Integer> TF = new ArrayList<Integer>();
	
	static List<Integer> Italic= new ArrayList<Integer>(),Bold= new ArrayList<Integer>(),
			Title= new ArrayList<Integer>(),h1= new ArrayList<Integer>(),
			h2= new ArrayList<Integer>(),h3= new ArrayList<Integer>(),
			h4= new ArrayList<Integer>(),h5= new ArrayList<Integer>(),
			h6= new ArrayList<Integer>(),p= new ArrayList<Integer>();
	static Map< String,Double> Docs =  new HashMap< String,Double>(); 
	static Connection conn;
	
	 public static void main(String[] args) throws IOException, SQLException {
			//Connect to DataBase
	    	DatabaseConnection.DatabaseConnect();
	    	conn= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root","");
//	    	getTotalDocuments();
//	 
//	    	List<String> inputString = QueryProcessor.GetParsedQuery();
//	    	for(int i=0; i<inputString.size(); i++) {
//	    		getFilteredDocuments(inputString.get(i));
//		   }
//	   	 double maxValueInMap=(Collections.max(Docs.values()));  // This will return max value in the Hashmap
//	     System.out.print("\r\n");
//	     System.out.print("Max value:"+maxValueInMap );
//	     for (Entry<String, Double> entry : Docs.entrySet()) {  // Iterate through hashmap
//	            if (entry.getValue()==maxValueInMap) {
//	                System.out.println(entry.getKey());     // Print the key with max value
//	            }
//	        }
//	     System.out.print("\r\n");
//		 System.out.print("-------------------Showing all results----------------------");
//		 System.out.print("\r\n");
//	     System.out.println(entriesSortedByValues(Docs));
	    	calculatePageRank();
	 }

	
	
	private static void getFilteredDocuments(String string) throws SQLException {

		// TODO Auto-generated method stub
		int Docs_Contain_term = 0;
				
		//----------------- URLs that contain that word and its frequency Query ---------------------//
		String SQL="SELECT link,(title+h1+h2+h3+h4+h5+h6+p+italic+bold),title,h1,h2,h3,h4,h5,h6,p,italic,bold FROM indexing where word = '"+string+"'";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ResultSet rs = ps.executeQuery();
		   while ( rs.next() ) {
			// get the total number of docs containing this word 
			  Docs_Contain_term++;
	    	  String s = rs.getString(1);
	    	  System.out.println("filling... "+s);  
	    	  int tfCount = rs.getInt(2);
	    	  int title = rs.getInt(3);
	    	  int h11 = rs.getInt(4);
	    	  int h22 = rs.getInt(5);
	    	  int h33 = rs.getInt(6);
	    	  int h44 = rs.getInt(7);
	    	  int h55 = rs.getInt(8);
	    	  int h66 = rs.getInt(9);
	    	  int P = rs.getInt(10);
	    	  int italic = rs.getInt(11);
	    	  int bold = rs.getInt(12);
	    	  if(!URLs.contains(s)) {
		    	 URLs.add(s);
		    	 TF.add(tfCount);
		    	 Title.add(title);
		    	 h1.add(h11);
		    	 h2.add(h22);
		    	 h3.add(h33);
		    	 h4.add(h44);
		    	 h5.add(h55);
		    	 h6.add(h66);
		    	 p.add(P);
		    	 Italic.add(italic);
		    	 Bold.add(bold);
		    }
	    	  else {
	    		 URLsMoreWords.add(s);
	    		 int index = URLs.indexOf(s);///---- continue here
	    		 TF.add(index,tfCount);
	    		 Title.add(index,title);
			     h1.add(index,h11);
			     h2.add(index,h22);
			     h3.add(index,h33);
			     h4.add(index,h44);
			     h5.add(index,h55);
			     h6.add(index,h66);
			     p.add(index,P);
			     Italic.add(index,italic);
			     Bold.add(index,bold);
	    	  }
	       }
	    //--------------------- End of Query -------------------------------------------------------------	  
	       
	       for(int i=0; i<URLsMoreWords.size(); i++) {
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
			 if(!URLsMoreWords.contains(URL)) {
				Docs.put(URL, tfIdf);
			 }
			 else {
				  System.out.print("trying to get the doubled URL"+URL);
				  System.out.print("\r\n");
			//	 int index = Documents.indexOf(d);
				  Double D = Docs.get(URL);
				  D += tfIdf;
				  Docs.put(URL, D); // check mappp
				
			 }
			 System.out.print("\r\n");
						
		 }
		 System.out.print("\r\n");
		// Set< Map.Entry< String,Double> > st = Docs.entrySet();    
		
	
		
		}
	//Return map in descending order
	static <K,V extends Comparable<? super V>>List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		
		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e2.getValue().compareTo(e1.getValue());
		        }
		    }
		);
		
		return sortedEntries;
	}
	public static boolean didConverge(int iterations,double[]pageRank,double[]old_pageRank) {
		double multiplicationFactor = 0;
		if (iterations == 0) {
			multiplicationFactor = 100000;
		}
		else  {
			multiplicationFactor = Math.pow(10, (iterations * -1));
		}
		
		for (int i = 0; i < pageRank.length; i++) {
			if ((int)Math.floor(pageRank[i]*multiplicationFactor) != (int)Math.floor(old_pageRank[i]*multiplicationFactor)) {
				return false;
			}
		}
		return true;
	}
	//create adjacency list
	public static void createAdjList(int nodeCount) throws SQLException {
		adjList = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<nodeCount;i++) {
			adjList.add(new ArrayList<>());
		}
		//get all edges to construct graph
		
		for(int i=0;i<nodeCount;i++) {
			List<Integer>outBounds=DatabaseConnection.getOutBoundLinks(i);
			for(int j=0;j<outBounds.size();j++) {
				adjList.get(i).add(outBounds.get(j));
			}
			
		}
	}
	public static void calculatePageRank() throws SQLException {
		getTotalDocuments();
		int nodeCount=totalDocuments;
		//create adjacnency list
		createAdjList(nodeCount);
		
		//initialize some variables (init,damping factor ,offset,contribution and PageRank matrix)
		double initProbability=1/(float)nodeCount;
		double [] pageRank = new double[nodeCount];
		double [] contribution = new double[nodeCount];
		double [] old_pageRank= new double[nodeCount];
		double dampingFactor=0.85;
		double offset = (1 - dampingFactor)/(float)nodeCount;
		
		//fill Contribution Array
		for(int i=0;i<nodeCount;i++) {
			double curOutbounds=(double)DatabaseConnection.getOutboundCount(i);
			if(curOutbounds!=0)
				contribution[i]=DatabaseConnection.getOutboundCount(i);
			else
				contribution[i]=initProbability;
		}
		
		//fill pageRank Array
		for(int i=0;i<nodeCount;i++) {
			pageRank[i]=initProbability;
		}
		
		//loop until convergences (calculate Page Rank && didConverge)
		int iteration=-1;
		do {
			double[] newPageRankArray = new double[nodeCount]; 
			double intermediateCalculation;
			for (int i = 0; i < pageRank.length; i++) {
				intermediateCalculation = 0;
				for (int j = 0; j < adjList.size(); j++) {
					if (adjList.get(j).contains(i)) {
						intermediateCalculation += pageRank[j] / contribution[j];
					}
				}
				newPageRankArray[i] = offset + dampingFactor * intermediateCalculation;
			}
			
			old_pageRank = pageRank;
			pageRank = newPageRankArray;
			iteration++;
		}while(!didConverge(iteration,pageRank,old_pageRank));
//		for(int i=0;i<nodeCount;i++)
//			System.out.print(pageRank[i]+"\n");
		
		//save page Rank in database
		for(int i=0;i<nodeCount;i++) {
			DatabaseConnection.savePageRank(i, pageRank[i]);
			System.out.print(i+"   "+pageRank[i]+"\n");
		}
			System.out.print("TERMINATEDDD\n");
	}
	public static void getTotalDocuments() throws SQLException {
		totalDocuments= DatabaseConnection.getTotalDocuments();
	}
	

		
	

	
	
	
}





