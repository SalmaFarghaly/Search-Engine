

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;



public class Parser {
	private static List<String> stopWords =null;
	//list of documents you want to index
	//this is stored in our database
	private static List<File> webPageFilesList = new ArrayList<File>();

	public static void loadStopwords() throws IOException {
		
	    stopWords = Files.readAllLines(Paths.get("C:\\Users\\Dell\\git\\repository\\WebSpider\\english_stopwords.txt"));
	    System.out.print("stopWords\n"+stopWords+"\n");
	}

	public static List<String> parse(String doc) throws IOException {
		List<String> tokenList = new ArrayList<String>();
		String[] tokens = doc.toString().split("[^a-zA-Z0-9'-]");
		String currentStr=null;
		for (String token : tokens){
			if(Parser.isNumeric(token)==false) {
				if(token.isEmpty())
					continue;
				currentStr=Stemmer.Stemming(token.toLowerCase());
				if(currentStr==null||currentStr.isEmpty())
					continue;
				tokenList.add(currentStr);
			}
			else
				tokenList.add(token);
				
		}
		removeStopWords(tokenList);
		return tokenList;
	}
	//check if this string can be parsed to string
	public static boolean isNumeric(String str) {
		 try {  
			    Integer.parseInt(str);  
			    return true;
			  } catch(NumberFormatException e){  
			    return false;  
			  }  
		
	}

	// removing the stop words from the tokens
	public static void removeStopWords(List<String> inputList) {
		inputList.removeIf(ip -> stopWords.contains(ip));
	}

}