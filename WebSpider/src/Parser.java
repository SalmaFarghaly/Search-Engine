

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
		
	    stopWords = Files.readAllLines(Paths.get("C:\\Users\\Dell\\git\\Search-Engine\\WebSpider\\english_stopwords.txt"));
	    System.out.print("stopWords\n"+stopWords+"\n");
	}

	public static ArrayList<String> parse(ArrayList<String>tokens) throws IOException {
		ArrayList<String> stemmedTokens=new ArrayList<String>();
		for(String token:tokens) {
			if(token.isBlank()==true||token.isEmpty()==true)
				continue;
			token=token.toLowerCase();
			if(stopWords.contains(token)==true)
				continue;
			//if it is 4 digit number like birth date save it but without stemming
			if(is4DigitNumber(token)==true)
				stemmedTokens.add(token);
			// if it is numeric but not a 4-digit number don't save it or if it is one character word it has no meaning so don't save it
			else if (isNumeric(token)==true||token.length()==1)
				continue;	
			else {
				
				String result = Stemmer.Stemming(token);
				System.out.print("Result"+result+"\n");
				if(result.isEmpty()==false&&result.isBlank()==false&&result!=null&&isNumeric(result)==false)
					stemmedTokens.add(result);
			}
		}
		System.out.print("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+stemmedTokens+"\n");
		return stemmedTokens;
	}
	
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
	
	public static void main(String[] arg) throws IOException
	{
		   BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
	    // Reading data using readLine 
	    String args= consoleReader.readLine(); 
//	    parse(args);
	}

}

