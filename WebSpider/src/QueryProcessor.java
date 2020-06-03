import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 		List<String> output = Parser.parse(finalTokens);
 		return output;
	}

	
}
	
