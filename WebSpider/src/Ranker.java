import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Ranker {
	static int totalDocuments = 0;
	private static List<ArrayList<Integer>> adjList;

	static List<String> URLs = new ArrayList<String>(),URLsMoreWords;
	static Map< String,Double> Docs =  new HashMap< String,Double>(); 
	static Connection conn;
	final static double title_weight = 0.3, h1_weight = 0.15,h2_weight = 0.1,
			h3_weight = 0.1,h4_weight = 0.025,h5_weight = 0.025,h6_weight = 0.025,
			italic_weight = 0.1, bold_weight = 0.1,p_weight = 0.075;
	
	static List<Integer> Italic= new ArrayList<Integer>(),Bold= new ArrayList<Integer>(),
			Title= new ArrayList<Integer>(),h1= new ArrayList<Integer>(),
			h2= new ArrayList<Integer>(),h3= new ArrayList<Integer>(),
			h4= new ArrayList<Integer>(),h5= new ArrayList<Integer>(),
			h6= new ArrayList<Integer>(),p= new ArrayList<Integer>();
	

	private static void CombinationPR_TFIDF() throws SQLException {
		// TODO Auto-generated method stub
		 // using for-each loop for iteration over Map.entrySet() 
        for (Entry<String, Double> entry : Docs.entrySet())  {
        	System.out.println("Key = " + entry.getKey() + 
                    ", Value = " + entry.getValue()); 
        	Double value = entry.getValue();
        	String key =  entry.getKey();
        	double pr = DatabaseConnection.getLinkPageRank(key);
        	double NewRank = 0.75 * value + 0.25 * pr ; 
        	Docs.put(key, NewRank);
        	System.out.println("Key = " + entry.getKey() + 
                    ", Value = " + entry.getValue()); 
        }
            
    } 
		
	private static void PhraseSearching(String s) {
	    String QueryWithoutQuotes =s.replaceAll("^\"+|\"+$", "");
		
	}

    private static void CalcTfIDF (String s) throws SQLException, IOException {
	       List<String> inputString = QueryProcessor.ParsedQuery(s);
	    	for(int i=0; i<inputString.size(); i++) {
	    		getFilteredDocuments(inputString.get(i));
		   }
	     if(!Docs.isEmpty()) {
		   	 double maxValueInMap=(Collections.max(Docs.values()));  // This will return max value in the Hashmap
		     System.out.print("\r\n");
		     System.out.print("Max value: "+maxValueInMap );
		     for (Entry<String, Double> entry : Docs.entrySet()) {  // Iterate through hashmap
		            if (entry.getValue()==maxValueInMap) {
		                System.out.println(" "+entry.getKey());     // Print the key with max value
		            }
		        }
	    }
	     CombinationPR_TFIDF();
	     System.out.print("\r\n");
	
    }

	private static void getFilteredDocuments(String string) throws SQLException {

		// TODO Auto-generated method stub
		int Docs_Contain_term = 0;

		URLsMoreWords = new ArrayList<String>();
		
				
		//----------------- URLs that contain that word and its frequency Query ---------------------//
		String SQL="SELECT link,title,h1,h2,h3,h4,h5,h6,p,italic,bold FROM indexing where word = '"+string+"'";
		PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
		ResultSet rs = ps.executeQuery();
		   while ( rs.next() ) {
			// get the total number of docs containing this word 
			  Docs_Contain_term++;
	    	  String s = rs.getString(1);
	    	  System.out.println("filling... "+s);  
	    //	  int tfCount = rs.getInt(2);
	    	  int title = rs.getInt(2);
	    	  int h11 = rs.getInt(3);
	    	  int h22 = rs.getInt(4);
	    	  int h33 = rs.getInt(5);
	    	  int h44 = rs.getInt(6);
	    	  int h55 = rs.getInt(7);
	    	  int h66 = rs.getInt(8);
	    	  int P = rs.getInt(9);
	    	  int italic = rs.getInt(10);
	    	  int bold = rs.getInt(11);
	    	  if(!URLs.contains(s)) {
		    	 URLs.add(s);
		    	 //TF.add(tfCount);
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

	    		 //TF.add(index,tfCount);
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
	       System.out.println("Documents containing the term"+Docs_Contain_term);
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
			  double CalcTfWeights = (Title.get(i)*title_weight) + (h1.get(i)*h1_weight) + (h2.get(i)*h2_weight) + (h3.get(i)*h3_weight)
					  					+ (h4.get(i)*h4_weight) + (h5.get(i)*h5_weight) + (h6.get(i)*h6_weight) + (p.get(i)*p_weight) 
					  					+ (Italic.get(i)*italic_weight) + (Bold.get(i)*bold_weight);
			  double TFCalc = (float)CalcTfWeights/DocLength;
			  System.out.print(" TFcalc "+TFCalc+" TF "+CalcTfWeights+" IDF "+Math.log10(IDF));
			  double tfIdf = TFCalc * Math.log10(IDF);	
			  System.out.print("---- total---- "+TFCalc * Math.log10(IDF));
			 if(!URLsMoreWords.contains(URL)) {
				Docs.put(URL, tfIdf);
			 }
			 else {

				  
				  System.out.print("\r\n");
			//	 int index = Documents.indexOf(d);
				  Double D = Docs.get(URL);
				  System.out.print("trying to get the doubled URL"+URL+ " tfidf old"+D);
				  D += tfIdf;
				  D = D*2;
				  System.out.print(" tfidf new"+D);

				  Docs.put(URL, D); // check mappp
				
			 }
			 System.out.print("\r\n");
		 }
		 // To be removed
		 System.out.print("-------------------Showing all results----------------------");
		 System.out.print("\r\n");
	     System.out.println(entriesSortedByValues(Docs));
					
		 
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
	
	
	 public static void main(String[] args) throws IOException, SQLException {
			//Connect to DataBase
	    	DatabaseConnection.DatabaseConnect();
	    	conn= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root","");

	    	getTotalDocuments();
	   
	   // check if it is a phrase searching case or normal search
	    String s =  QueryProcessor.SetOriginalQuery();
	    if( s.startsWith("\"") & s.endsWith("\"")) { // it is a phrase searching case
	    	CalcTfIDF (s);
	    	PhraseSearching(s);
	    } 
	    else { // will make normal search
	    	CalcTfIDF (s);
	    }
		 System.out.print("-------------------Showing all results----------------------");
		 System.out.print("\r\n");
	     System.out.println(entriesSortedByValues(Docs));
	
	 }
	

		
	

	
	
	
}





