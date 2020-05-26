

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
	private static List<Character> punctuationMarks=new ArrayList<Character>();
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
		System.out.print("TOKENNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN\n");
		System.out.print(Arrays.toString(tokens));
		String loweredCaseToken=null;
		for(String token:tokens) {
			//remove numbers
			if(is4DigitNumber(token)==true||isNumeric(token)==false) {
			loweredCaseToken=token.toLowerCase();
			if(!loweredCaseToken.isBlank()&&!loweredCaseToken.isEmpty())
			tokenList.add(loweredCaseToken);
			}
			
			
		}
		//remove stop word
		ArrayList<String> WordsAfterStemming=new ArrayList<String>();
		removeStopWords(tokenList);
		for(String token:tokenList) {
			if(is4DigitNumber(token)==true) {
				WordsAfterStemming.add(token);
			}
			else {
				ArrayList<String> stemmedWords=Stemmer.Stemming(token);
				for(String word:stemmedWords) {
					if(!word.isEmpty()&&!word.isBlank())
						WordsAfterStemming.add(word);	
				}
			}
				
		}
		return WordsAfterStemming;
	}
	//check if this string can be parsed to string
	
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		}
		catch(NumberFormatException e){
			return false;
		}
	}
	public static boolean is4DigitNumber(String str) {
		 try {  
			 	//number after parsing it to integer
			    int n=Integer.parseInt(str);  
			    int d=n/1000;
			    if(d>=1&&d<=9)
			    	return true;
			    else
			    	return false;
			  } catch(NumberFormatException e){  
			    return false;  
			  }  
		
	}

	// removing the stop words from the tokens
	public static void removeStopWords(List<String> inputList) {
		inputList.removeIf(ip -> stopWords.contains(ip));
	}

}