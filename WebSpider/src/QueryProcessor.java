import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
 		List<String> output = Parser.parse(message);
 		return output;
	}
/*	public static void main(String[] args) throws IOException {
		String message =  SetOriginalQuery() ;
		Parser.loadStopwords();
 		List<String> output = Parser.parse(message);
 		salma bt3mly build ezzay
	}*/
	
}
	
