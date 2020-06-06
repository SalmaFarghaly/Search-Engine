import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Scanner;

public class QueryProcessor {
	static String s = null; 
	
	public static void SetinputQuery() throws IOException{
		System.out.println("Enter a string"); 
		Scanner sc = new Scanner(System.in);
 		s = sc.nextLine();
	}

	public static String[] GetOriginalQueryArrayed() {
		String[] tokens = s.split("\\S");
		return tokens;
	}
	public static String SetOriginalQuery() throws IOException {
		SetinputQuery();
		return s;
	}
	public static List<String> ParsedQuery(String message) throws IOException {
		Parser.loadStopwords();
		String[] tokens = message.split("[^a-zA-Z0-9'-]");
		ArrayList<String> finalTokens=new ArrayList();
		finalTokens.addAll(Arrays.asList(tokens));
		System.out.print("HJUUUU"+finalTokens+"\n");
 		List<String> output = Parser.parse(finalTokens);
 		System.out.print("HJUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU"+output+"\n");
 		return output;
	}
	public static void main(String[] args) throws IOException, SQLException {
		SetinputQuery();
		List<String> parsedQuery=ParsedQuery(s);
		System.out.print("parsed "+parsedQuery);
		String names[] = s. split(" ");
		long start = System.currentTimeMillis();
		ArrayList<String> output = Ranker.ranker(s,parsedQuery);
		DatabaseConnection.saveRankerResults(parsedQuery, output);
		System.out.print("-------------------Showing all results----------------------\n");
//		  for (Entry<String, Double> entry : output.entrySet())  {
//	        	System.out.println("Key final= " + entry.getKey() + 
//	                    ", Value final= " + entry.getValue()); 
//		  }
		  long end = System.currentTimeMillis();
	      //finding the time difference and converting it into seconds
	      float sec = (end - start) / 1000F; System.out.println("Time of Ranker: "+ sec + " seconds");

		
	}
	public static ArrayList<String> queryProcessor(String input,int pid) throws IOException, SQLException {
		DatabaseConnection.DatabaseConnect();
		List<String> parsedQuery=ParsedQuery(input);
		ArrayList<String> m =new ArrayList();
		//this search query hasn't been mad before
		if(DatabaseConnection.isSearchQueryExist(parsedQuery)==false){
			System.out.print("\n"+"QUERYYYYY DOESNT EXIST\n");
					m = Ranker.ranker(input,parsedQuery);
					DatabaseConnection.saveRankerResults(parsedQuery, m);
		}
		else
				m=DatabaseConnection.getQueryResult(parsedQuery, pid);
		//save  result of ranker to the db
		return m;
	}
	
}
	
