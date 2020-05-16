import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;

public class Indexer {
	
	
	public Indexer() throws IOException, SQLException {
		System.out.print("Indexer Started...................\n");
		createInvertedFile();
		
	}
	public void stemAndRemoveStopWords(java.util.List<String>Tags,String url,String pos) throws IOException, SQLException {
		
		System.out.print("Tags\n"+Tags+"\n");
		//Loop on the sentences 
		for(int i=0;i<Tags.size();i++) {
			System.out.print("Tags\n"+Tags.get(i)+"\n");
			//for every sentence get the words in it and remove the stop words.
			java.util.List<String> parsedWords=Parser.parse(Tags.get(i));
			System.out.print("STEMMEDDDDDDDWords"+parsedWords+"\n");
			String currentStr=null;
			// loop on the words that in parsed words array after removing the stop words
			for(int j=0;j<parsedWords.size();j++) {
				currentStr=parsedWords.get(j);
				System.out.print("CurrenttttWordddddd   "+currentStr+"\n");
					System.out.print(currentStr+" Savedd in the database............................\n");
				DatabaseConnection.addStemmedWord(currentStr, url, pos);
			}
		}
		
	}

	public void createInvertedFile() throws IOException, SQLException {
		//load Stop Words
		Parser.loadStopwords();
		String url = DatabaseConnection.getFirstUnIndexed();
		 Document doc;
		 while(url!=null) {
			System.out.print("The current url "+url+"\n");
			 doc = Jsoup.connect(url).get();
			Elements words = doc.select("h1, h2, h3, h4, h5, h6,p");
			java.util.List<String> h1Tags = words.select("h1").eachText();
			stemAndRemoveStopWords(h1Tags,url,"h1");
			java.util.List<String> h2Tags = words.select("h2").eachText();
			stemAndRemoveStopWords(h2Tags,url,"h2");
			java.util.List<String> h3Tags = words.select("h3").eachText();
			stemAndRemoveStopWords(h3Tags,url,"h3");
			java.util.List<String> h4Tags = words.select("h4").eachText();
			stemAndRemoveStopWords(h4Tags,url,"h4");
			java.util.List<String> h5Tags = words.select("h5").eachText();
			stemAndRemoveStopWords(h5Tags,url,"h5");
			java.util.List<String> h6Tags = words.select("h6").eachText();
			stemAndRemoveStopWords(h6Tags,url,"h6");
			java.util.List<String> p = words.select("p").eachText();
			stemAndRemoveStopWords(p,url,"p");
			//set done indexing to 1
			DatabaseConnection.SetDoneIndexing(url);
			url = DatabaseConnection.getFirstUnIndexed();
		}
		
		
	}
    public static void main(String[] args) throws IOException, SQLException {
		//Connect to DataBase
    	DatabaseConnection.DatabaseConnect();
    	Indexer indexer=new Indexer();
    }

}
