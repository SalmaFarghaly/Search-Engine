import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
	static String s = null; 
	public static List<String> GetParsedQuery() throws IOException {
		// TODO Auto-generated method stub
		
		System.out.println("Enter a string"); 
		Scanner sc = new Scanner(System.in);
 		s = sc.nextLine();
 		Parser.loadStopwords();
 		List<String> output = Parser.parse(s);
 		return output;
 		
 		/*for (int i = 0; i <output.size(); i++) {
 			System.out.println("Entered string "+output.get(i)); 
 		}*/
		
	}
	public static String[] GetOriginalQuery() {
		 String[] tokens = s.split("\\S");
		return tokens;
	}
	public static List<String> ParsedQuery(String message) throws IOException {
		Parser.loadStopwords();
 		List<String> output = Parser.parse(message);
 		return output;
	}
	
}
	
