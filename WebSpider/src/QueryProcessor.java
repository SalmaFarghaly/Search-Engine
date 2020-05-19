import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
	private static List<String> stopWords =null;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String s; 
		System.out.println("Enter a string"); 
		Scanner sc = new Scanner(System.in);
 		s = sc.nextLine();
 		Parser.loadStopwords();
 		List<String> output = Parser.parse(s);
 		
 		
 		for (int i = 0; i <output.size(); i++) {
 			System.out.println("Entered string "+output.get(i)); 
 		}
		
	}
}
	