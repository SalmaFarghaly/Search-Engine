import java.io.IOException;
import java.io.InputStreamReader;
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
	
	static int x = 0;
	static int totalDocuments;
	static Integer dummy=0;
	public Indexer() throws IOException, SQLException {
		System.out.print("Indexer Started...................\n");
		totalDocuments= DatabaseConnection.getTotalDocuments();
		//create 5 threads
		int ThreadNo=1;
		Thread index1=new Index(ThreadNo,dummy);
		index1.start();
		Thread index2=new Index(++ThreadNo,dummy);
		index2.start();
		Thread index3=new Index(++ThreadNo,dummy);
		index3.start();
		Thread index4=new Index(++ThreadNo,dummy);
		index4.start();
		Thread index5=new Index(++ThreadNo,dummy);
		index5.start();
	}


    public static void main(String[] args) throws IOException, SQLException {
		//Connect to DataBase
    	DatabaseConnection.DatabaseConnect();

    	System.out.println("Enter You want to re-index or index: (1) index (2) re-index");
    	 BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
        // Reading data using readLine 
        String type = 	"1";//consoleReader.readLine(); 
        //check if he wants to re-index the content or not
        if(Integer.parseInt(type)==2) {
        	//drop all the table
        	
        	DatabaseConnection.dropIndexingTable();
        	//reset start indexing of all urls 
        	DatabaseConnection.resetDoneAndStartIndexing();
        	
        }
        else{
        	DatabaseConnection.deleteNonDoneIndexingWords();
        }
        Parser.loadStopwords();
    	Indexer indexer=new Indexer();
    	
    }
    private static class Index extends Thread{
    	
    	int ThreadNo;
    	Integer dummy;
    	public Index(int ThreadNum,Integer d) {
    		this.ThreadNo=ThreadNum;
    		this.dummy=d;
    	}
    
		public void run(){
			//load Stop Words
			
			//create 5 threads
			String url = null ;
			while(url==null) {
				
				synchronized(this.dummy) {
					try {
						url=DatabaseConnection.getFirstUnIndexed(this.ThreadNo);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			 Document doc=null;
				 while(url!=null) {
					System.out.print(this.ThreadNo +"   The current urllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll\n "+url+"\n");
					System.out.print("["+x+"/"+totalDocuments+"] The current url "+url+"\n");
					 try {
						doc = Jsoup.connect(url).timeout(180000).ignoreHttpErrors(true).get();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(doc!=null) {
						synchronized(this.dummy) {
							try {
								DatabaseConnection.setStartIndexing(url.toString());
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Elements words = doc.select("h1, h2, h3, h4, h5, h6,p,title,i,b");
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
						java.util.List<String> titleTags = words.select("title").eachText();
						stemAndRemoveStopWords(titleTags,url,"title");
						java.util.List<String> italicTags = words.select("i").eachText();
						stemAndRemoveStopWords(italicTags,url,"i");
						java.util.List<String> boldTags = words.select("b").eachText();
						stemAndRemoveStopWords(boldTags,url,"b");
					}
					
					//set done indexing to 1
					synchronized(this.dummy) {
						try {
							if(doc!=null)
							DatabaseConnection.SetDoneIndexing(url);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							url = DatabaseConnection.getFirstUnIndexed(this.ThreadNo);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}
			public void stemAndRemoveStopWords(java.util.List<String>Tags,String url,String pos){
				
				System.out.print("Tags\n"+Tags+"\n");
				//Loop on the sentences 
				for(int i=0;i<Tags.size();i++) {
					System.out.print("Tags\n"+Tags.get(i)+"\n");
					//for every sentence get the words in it and remove the stop words.
					java.util.List<String> parsedWords=null;
					try {
						parsedWords = Parser.parse(Tags.get(i));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.print("STEMMEDDDDDDDWords"+parsedWords+"\n");
					String currentStr=null;
					// loop on the words that in parsed words array after removing the stop words
					for(int j=0;j<parsedWords.size();j++) {
						currentStr=parsedWords.get(j);
						System.out.print("CurrenttttWordddddd   "+currentStr+"\n");
							System.out.print(currentStr+" Savedd in the database............................\n");
						if(!currentStr.isEmpty()&&!currentStr.isBlank()) {
							try {
								DatabaseConnection.addStemmedWord(currentStr, url, pos);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				
			}

			
			
		
    }

}
