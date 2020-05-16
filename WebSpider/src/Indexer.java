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
		//Connect to DataBase
		createInvertedFile();
		
	}
	public void stemAndRemoveStopWords(java.util.List<String>Tags,String url,String pos) throws IOException, SQLException {
		
		System.out.print("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD\n"+Tags+"\n");
		//Loop on the sentences 
		for(int i=0;i<Tags.size();i++) {
			System.out.print("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW\n"+Tags.get(i)+"\n");
			//for every sentence get the words in it and remove the stop words.
			java.util.List<String> parsedWords=Parser.parse(Tags.get(i));
			System.out.print("STEMMEDDDDDDDWords"+parsedWords+"\n");
			String currentStr=null;
//			String stemmedWord=null;
			// loop on the words that in parsed words array after removing the stop words
			for(int j=0;j<parsedWords.size();j++) {
				currentStr=parsedWords.get(j);
				System.out.print("CurrenttttWordddddd   "+currentStr+"\n");
//				if(Parser.isNumeric(currentStr)==false) {
//					
//					if(currentStr==" ")
//						continue;
//					//Stem the word
//					stemmedWord=Stemmer.Stemming(currentStr);
//					if(stemmedWord=="null"||stemmedWord==" ")
//						continue;
					System.out.print(currentStr+" Savedd in the database............................\n");
////					DatabaseConnection.addStemmedWord(stemmedWord, url, pos);
//				}
//				else
//				System.out.print(currentStr+" Savedd in the database............................\n");
				DatabaseConnection.addStemmedWord(currentStr, url, pos);
			}
		}
		
	}

	public void createInvertedFile() throws IOException, SQLException {
		//get url hereeeee
		//want to save last url i have indexed 
//		String url = DatabaseConnection.getFirstUnIndexed();
		//load Stop Words
		Parser.loadStopwords();
		String url = DatabaseConnection.getFirstUnIndexed();
//		String url=("https://www.msn.com/en-us/Sports");
		 Document doc;
		 while(url!=null) {
			System.out.print("The current url "+url+"\n");
			 doc = Jsoup.connect(url).get();
			Elements words = doc.select("h1, h2, h3, h4, h5, h6,p");
			System.out.print("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"+words+"\n");
			java.util.List<String> h1Tags = words.select("h1").eachText();
			System.out.print("H1111111111111111111111111111111111111111111"+h1Tags+"\n");
			stemAndRemoveStopWords(h1Tags,url,"h1");
			java.util.List<String> h2Tags = words.select("h2").eachText();
			System.out.print("H2222222222222222222222222222222222222222222222"+h2Tags+"\n");
			stemAndRemoveStopWords(h2Tags,url,"h2");
			java.util.List<String> h3Tags = words.select("h3").eachText();
			System.out.print("H3333333333333333333333333333333333333333333333"+h3Tags+"\n");
			stemAndRemoveStopWords(h3Tags,url,"h3");
			java.util.List<String> h4Tags = words.select("h4").eachText();
			System.out.print("H444444444444444444444444444444444444444444444444444"+h4Tags+"\n");
			stemAndRemoveStopWords(h4Tags,url,"h4");
			java.util.List<String> h5Tags = words.select("h5").eachText();
			System.out.print("H555555555555555555555555555555555555555555555555555555555"+h5Tags+"\n");
			stemAndRemoveStopWords(h5Tags,url,"h5");
			java.util.List<String> h6Tags = words.select("h6").eachText();
			System.out.print("H66666666666666666666666666666666666666666666666666666"+h6Tags+"\n");
			stemAndRemoveStopWords(h6Tags,url,"h6");
			java.util.List<String> p = words.select("p").eachText();
			System.out.print("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP"+p+"\n");
			stemAndRemoveStopWords(p,url,"p");
			//set done indexing to 1
			DatabaseConnection.SetDoneIndexing(url);
			url = DatabaseConnection.getFirstUnIndexed();
		}
		
		
//		 System.out.println("After parsing, Heading : " + doc.getElementsByIndexEquals(0));
	}
    public static void main(String[] args) throws IOException, SQLException {
    	DatabaseConnection.DatabaseConnect();
    	Indexer indexer=new Indexer();
    }

}
