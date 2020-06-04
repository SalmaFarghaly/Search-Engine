import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;

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
	static boolean isPhraseSearching = false;
	static List<String> URLs = new ArrayList<String>(),URLsMoreWords = new ArrayList<String>();;
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

	// public static void main(String[] args) throws IOException, SQLException {
	public static Map<String, Double> ranker (String QueryWord,List<String> ParsedQuery) throws SQLException, IOException	{
	//Connect to DataBase
	    	DatabaseConnection.DatabaseConnect();
	    	conn= DriverManager.getConnection("jdbc:mysql://localhost/SearchEngine?serverTimezone=UTC","root","");
	    	getTotalDocuments();
	   
	   // check if it is a phrase searching case or normal search
	  //  String QueryWord =  QueryProcessor.SetOriginalQuery();
	    if( QueryWord.startsWith("\"") & QueryWord.endsWith("\"")) { // it is a phrase searching case
	    	//CalcTfIDF (QueryWord);
	    	isPhraseSearching = true;
	    	PhraseSearching(QueryWord,ParsedQuery);
	    } 
	    else { // will make normal search
	    	CalcTfIDF (QueryWord,ParsedQuery);
	    }
		// System.out.print("-------------------Showing all results----------------------");
		// System.out.print("\r\n");
	     //System.out.println(entriesSortedByValues(Docs));
	    Map<String,Double> topTen =
	    	    Docs.entrySet().stream()
	    	       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	    	       .limit(200)
	    	       .collect(Collectors.toMap(
	    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	 return topTen;
		// calculatePageRank();
	 }

	private static double CombinationPR_TFIDF(String key,double Oldrank) throws SQLException {
		// TODO Auto-generated method stub
		 // using for-each loop for iteration over Map.entrySet() 
        	System.out.println("Page Rank Calculation Key = " + key + 
                    ", Value = " + Oldrank); 
        	
        	double pr = DatabaseConnection.getLinkPageRank(key);
        	double NewRank = 0.75 * Oldrank + 0.25 * pr ; 
        	
           	System.out.println("Page Rank Calculation Key = " + key + 
                    ", Value = " + Oldrank); 
           	return NewRank;
    } 
		
	private static void PhraseSearching(String s,List<String> ParsedQuery) throws  SQLException {
	    String QueryWithoutQuotes =s.replaceAll("^\"+|\"+$", "");
	    List<String> inputString = ParsedQuery;
		
	    for(int i=0; i<inputString.size(); i++) {
	    
	    	getUrlsWithTerm(inputString.get(i),0);
	   }
	    for (Entry<String, Double> entry : Docs.entrySet())  {
	    	String url =  entry.getKey();
	    	
        	Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
				Elements words = doc.select("h1, h2, h3, h4, h5, h6,p,title,i,b");
	        	List<String> TagsInfo = words.select("i").eachText();
	        	String italicInfo = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h1").eachText();
	        	String h1Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h2").eachText();
	        	String h2Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h3").eachText();
	        	String h3Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h4").eachText();
	        	String h4Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h5").eachText();
	        	String h5Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("h6").eachText();
	        	String h6Info = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("b").eachText();
	        	String boldInfo = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("p").eachText();
	        	String paragraphInfo = getDocumentinfo(TagsInfo);
	        	TagsInfo = null;TagsInfo = words.select("title").eachText();
	        	String titleInfo = getDocumentinfo(TagsInfo);
	        	double titleFreq = 0,paragraphFreq = 0,h1Freq = 0,h2Freq = 0,h3Freq = 0,
	        	h4Freq = 0,h5Freq = 0,h6Freq = 0,boldFreq = 0,italicFreq = 0;
	        	titleFreq =  getFreqREGEX ( QueryWithoutQuotes,titleInfo);
	        	h1Freq =  getFreqREGEX ( QueryWithoutQuotes,h1Info);
	        	h2Freq =  getFreqREGEX ( QueryWithoutQuotes,h2Info);
	        	h3Freq =  getFreqREGEX ( QueryWithoutQuotes,h3Info);
	        	h4Freq =  getFreqREGEX ( QueryWithoutQuotes,h4Info);
	        	h5Freq =  getFreqREGEX ( QueryWithoutQuotes,h5Info);
	        	h6Freq =  getFreqREGEX ( QueryWithoutQuotes,h6Info);
	        	italicFreq =  getFreqREGEX ( QueryWithoutQuotes,italicInfo);
	        	boldFreq =  getFreqREGEX ( QueryWithoutQuotes,boldInfo);
	        	paragraphFreq =  getFreqREGEX ( QueryWithoutQuotes,paragraphInfo);
	        	System.out.println("Frequency for url: "+url+" "+titleFreq+" "+ h1Freq+" "+h2Freq+" "+h3Freq+" "+h4Freq+" "+h5Freq+" "+h6Freq+" "+paragraphFreq+" "+italicFreq);
	        	double total =  (double) ( (title_weight*titleFreq) + (h1_weight*h1Freq) + (h2_weight*h2Freq) + (h3_weight*h3Freq) + (h4_weight*h4Freq) + 
	        			(h5_weight*h5Freq) + (h6_weight*h6Freq) + (italic_weight*italicFreq) + (bold_weight*boldFreq) + (p_weight*paragraphFreq) )   ;
	        	double newRank= 0.0;
	        	if(isPhraseSearching)
	        		newRank = CombinationPR_TFIDF(url,total);
	        	System.out.println("total: "+total);
	        	if(total ==0) {
	        		Docs.put(url, (double) 0);
	        	}
	        	else {
	        		if (isPhraseSearching)
	        			Docs.put(url, newRank);
	        		else {
	        			Double value = entry.getValue();
		        		value = value + total;
		        		Docs.put(url, value);
	        		}
	        		
	        	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
	    Docs.values().removeIf(value -> value == 0.0);
	   }
	  

    private static String getDocumentinfo(List<String> tagsinfo) {
		// TODO Auto-generated method stub
    	StringBuilder d = new StringBuilder();
		for(int i=0; i<tagsinfo.size(); i++) {
			d.append(tagsinfo.get(i));
			d.append(" ");
		}
		String res = d.toString();
    	return res;
	}
    
    private static double getFreqREGEX (String patternToBeMatched,String doc) {
    	Pattern pattern = Pattern.compile(patternToBeMatched);
		Matcher matcher = pattern.matcher(doc);
		double matches = matcher.results().count();
		return matches;
    }

	private static void CalcTfIDF (String s,List<String> ParsedQuery) throws SQLException, IOException {
	       List<String> inputString = ParsedQuery;
	    	for(int i=0; i<inputString.size(); i++) {
	    		getFilteredDocuments(inputString.get(i));
		   }

	     System.out.print("\r\n");
	
    }

	private static void getFilteredDocuments(String string) throws SQLException {

		// TODO Auto-generated method stub
		int Docs_Contain_term = 0;
		Docs_Contain_term = getUrlsWithTerm(string ,Docs_Contain_term);
		/*
		 * for(int i=0; i<URLsMoreWords.size(); i++) {
		 * System.out.print("More words...."+URLsMoreWords.get(i));
		 * System.out.print("\r\n"); } for(int i=0; i<URLs.size(); i++) {
		 * System.out.print("Single word...."+URLs.get(i)); System.out.print("\r\n"); }
		 */
	       System.out.println("Documents containing the term "+Docs_Contain_term);
		//doc.getTermFrequency() * Math.log10(totalDocuments / docListLength)
		 for(int i=0; i<URLs.size(); i++) {
			 // get the number of terms in the single document
			 String URL = URLs.get(i);
			 if(Docs.containsKey(URL) & !URLsMoreWords.contains(URL) ) // was calculated before
				continue;	
			  System.out.println("Calculating for URL "+URL+" For word "+string);
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
				double NewRank = CombinationPR_TFIDF(URL,tfIdf); 
				Docs.put(URL, NewRank);
			 }
			 else {
				  System.out.print("\r\n");
			//	 int index = Documents.indexOf(d);
				  Double D = Docs.get(URL);
				  if(D !=null) {
				  System.out.print("trying to get the doubled URL"+URL+ " tfidf old"+D);
					  D += tfIdf;
					  D = D*10;
					  System.out.print(" tfidf new"+D);
					  Docs.put(URL, D); // check mappp
				  }
			 }
			 System.out.print("\r\n");
		 }
		 // To be removed
		/*
		 * System.out.
		 * print("-------------------Showing all Normal Search results----------------------"
		 * ); System.out.print("\r\n"); System.out.println(entriesSortedByValues(Docs));
		 */
		}
	
	public static int getUrlsWithTerm (String string,int Docs_Contain_term) throws SQLException {
		
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
				    		 if(isPhraseSearching) {
					    		  Docs.put(s, (double) (title+h11+h22+h33+h44+h55+h66+P+italic+bold));
					    	  }
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
			return Docs_Contain_term;
	}
	
public static int getUrlsWithAllTerms (String[] query,int Docs_Contain_term) throws SQLException {
		
		//----------------- URLs that contain that ALL Query words ---------------------//
				int count = query.length;
				String SQL="SELECT link,title,h1,h2,h3,h4,h5,h6,p,italic,bold FROM indexing where ";// word = '"+string+"'";
				for(int i=0; i<query.length; i++) {
					SQL = SQL +"word = "+ "'"+ query[i] +"'";
					count --;
					if (count>0) {
						SQL = SQL + " AND ";
					}
				}
				PreparedStatement ps= conn.prepareStatement( SQL, Statement.RETURN_GENERATED_KEYS );
				ResultSet rs = ps.executeQuery();
				   while ( rs.next() ) {
					 
						//int tfCount = rs.getInt(2);
				       String s = rs.getString(1);
				       System.out.println("filling... "+s);  
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
					// get the total number of docs containing this word 
					   Docs.put(s, (double) (title+h11+h22+h33+h44+h55+h66+P+italic+bold));
				    }
			    	 
			       
			    //--------------------- End of Query -------------------------------------------------------------	  
			return Docs_Contain_term;
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
//-------------------------------------------- PAGE RANK ALGORITHM ------------------------------	
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
	
// For Calculating Max URL
	/*
	 * if(!Docs.isEmpty()) { double maxValueInMap=(Collections.max(Docs.values()));
	 * // This will return max value in the Hashmap System.out.print("\r\n");
	 * System.out.print("Max value: "+maxValueInMap ); for (Entry<String, Double>
	 * entry : Docs.entrySet()) { // Iterate through hashmap if
	 * (entry.getValue()==maxValueInMap) { System.out.println(" "+entry.getKey());
	 * // Print the key with max value } } }
	 */	
	
}





