import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
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
 		List<String> output = Parser.parse(message);
 		return output;
	}
	public static void main(String[] args) throws IOException, SQLException {
		SetinputQuery();
		List<String> parsedQuery=ParsedQuery(s);
		System.out.print("parsed "+parsedQuery);
		String names[] = s. split(" ");
		Map<String, Double> output = Ranker.ranker(s,parsedQuery);
		System.out.print("-------------------Showing all results----------------------");
		  for (Entry<String, Double> entry : output.entrySet())  {
	        	System.out.println("Key final= " + entry.getKey() + 
	                    ", Value final= " + entry.getValue()); 
		  }
		/*
		 * //----------------------------------------- getting url content
		 * ----------------------------- String url =entry.getKey(); Document doc;
		 * Elements words1 = null; String title = null; try {
		 * System.out.println("Connecting to document"); doc =
		 * Jsoup.connect(url).timeout(180000).ignoreHttpErrors(true).get(); title =
		 * doc.title(); words1 = doc.select("p"); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * System.out.println("Done Connecting"); List<String> tagsinfo =
		 * words1.select("p").eachText(); List<String> exist = new ArrayList<String>();
		 * int count = 0; for(int i=0; i<tagsinfo.size(); i++) {
		 * if(Stream.of(names).anyMatch(tagsinfo.get(i).toLowerCase()::contains)) {
		 * exist.add(tagsinfo.get(i)); count++; } if(count>2) break; } String
		 * snippets=null; for(int j=0; j<count;j++ ) snippets = exist.get(j); String
		 * replaceString = null; if(snippets!=null) { for (int i=0;i<names.length; i++)
		 * { String boldText = "<b>"+names[i]+"</b>";
		 * replaceString=snippets.replaceAll(names[i],boldText); } }
		 */
		 // }
		
	}
	public static Map<String, Double> queryProcessor(String input) throws IOException, SQLException {
		List<String> parsedQuery=ParsedQuery(input);
		Map<String, Double> m = Ranker.ranker(input,parsedQuery);
		return m;
	}
	
}
	
