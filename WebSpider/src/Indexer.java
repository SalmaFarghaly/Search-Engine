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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;

public class Indexer {
	
	static int x = 0;
	static int totalDocuments;
	static Integer dummy=0;
	public Indexer() throws IOException, SQLException, InterruptedException {
		System.out.print("Indexer Started...................\n");
		totalDocuments= DatabaseConnection.getTotalDocuments();
		//create 5 threads
		int ThreadNo=1;
		Thread index1=new Index(ThreadNo,dummy);
		index1.setName("Thread1");
		index1.start();
		Thread index2=new Index(++ThreadNo,dummy);
		index2.setName("Thread2");
		index2.start();
		Thread index3=new Index(++ThreadNo,dummy);
		index3.setName("Thread3");
		index3.start();
		Thread index4=new Index(++ThreadNo,dummy);
		index4.setName("Thread4");
		index4.start();
		Thread index5=new Index(++ThreadNo,dummy);
		index5.setName("Thread5");
		index5.start();
		// After the 5 threads finish indexing the whole urls , call calculatePageRank function
		index1.join();
		index2.join();
		index3.join();
		index4.join();
		index5.join();
		// updating field id to database as it will be easy to deal with integers(as an index) when creating Adjacency list 
		// for the page rank algorithm
		DatabaseConnection.updateIDIncrementally();
		Ranker.calculatePageRank();
	}


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
    	//Connect to DataBase
    	DatabaseConnection.DatabaseConnect();

    	System.out.println("Enter You want to re-index or index: (1) index (2) re-index");
    	 BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
        // Reading data using readLine 

    	 String type = consoleReader.readLine(); 
        
        //check if he wants to re-index the content or not
        if(Integer.parseInt(type)==2) {
        	//drop all the table
        	
        	DatabaseConnection.dropIndexingTable();
        	//reset start indexing of all urls 
        	DatabaseConnection.resetDoneAndStartIndexing();
    		DatabaseConnection.deleteRankResults();
        	
        }
        else{
        	//retrieve links which has started indexing and not finished because of interrupt
        	//and set their start indexing to zero
        	DatabaseConnection.deleteNonDoneIndexingWords();
        }
        consoleReader.close();
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
	
			String url = null ; // current url that thread is indexing
			int i=0;
			while(i==0||url!=null) {
				i++;
				synchronized(this.dummy) {
					try {
						//get first un-indexed url from database 
						url=DatabaseConnection.getFirstUnIndexed(this.ThreadNo);
						if(url!=null) // if all urls are indexed then the thread finished 
							//and start indexing it 
						DatabaseConnection.setStartIndexing(url.toString());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				 while(url!=null) {
					 Document doc=null;
					 System.out.print(this.ThreadNo +"   The current urllllllllllllllllllllllll  "+url+"\n");
					 try {
						doc = Jsoup.connect(url).timeout(0).get();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(doc!=null) {
						//for each document , ArrayList named tokens is created to store in it the words in document after stemming and parsing 
						// at the end of indexing i store each word in array in the database with the url and the it's count of occurrence in each Tag(h1->h6,bold,italic,title,p)
						Elements words = doc.select("h1, h2, h3, h4, h5, h6,p,title,i,b");
						
						ArrayList<Word> tokens=new ArrayList<Word>();
						
						Integer wordCount=0;	//total count of words in the document
						try {
						//check that the tag is not empty and select strings in each tag ,then split this strings to tokens and stem and remove stop words in this tag 
						// we repeat this process for each tag (h1->h6,bold,italic,title,p)
						Elements h1Tags = words.select("h1");
						java.util.List<String> h1List = words.select("h1").eachText();
						if(!h1List.isEmpty()){
							
							ArrayList<String>tokensH1=new ArrayList();
							for(int j=0;j<h1List.size();j++) {
								String[] tempTokens=h1List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH1.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH1.size();
							stemAndRemoveStopWords(tokensH1,url,"h1",tokens);
							
						}
						Elements h2Tags = words.select("h2");
						String textH2=h1Tags.text();
						java.util.List<String> h2List = words.select("h2").eachText();
						if(!h2List.isEmpty()){
							
							ArrayList<String>tokensH2=new ArrayList();
							for(int j=0;j<h2List.size();j++) {
								String[] tempTokens=h2List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH2.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH2.size();
							stemAndRemoveStopWords(tokensH2,url,"h2",tokens);
							
						}
						Elements h3Tags = words.select("h3");
						String textH3=h3Tags.text();
						java.util.List<String> h3List = words.select("h3").eachText();
						if(!h3List.isEmpty()){
							
							ArrayList<String>tokensH3=new ArrayList();
							for(int j=0;j<h3List.size();j++) {
								String[] tempTokens=h3List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH3.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH3.size();
							stemAndRemoveStopWords(tokensH3,url,"h3",tokens);
							
						}
						Elements h4Tags = words.select("h4");
						String textH4=h4Tags.text();
						java.util.List<String> h4List = words.select("h4").eachText();
						if(!h4List.isEmpty()){
							
							ArrayList<String>tokensH4=new ArrayList();
							for(int j=0;j<h4List.size();j++) {
								String[] tempTokens=h4List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH4.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH4.size();
							stemAndRemoveStopWords(tokensH4,url,"h4",tokens);
							
						}
						Elements h5Tags = words.select("h5");
						String textH5=h5Tags.text();
						java.util.List<String> h5List = words.select("h5").eachText();
						if(!h5List.isEmpty()){
							
							ArrayList<String>tokensH5=new ArrayList();
							for(int j=0;j<h5List.size();j++) {
								String[] tempTokens=h5List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH5.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH5.size();
							stemAndRemoveStopWords(tokensH5,url,"h5",tokens);
							
						}
						Elements h6Tags = words.select("h6");
						String textH6=h6Tags.text();
						java.util.List<String> h6List = words.select("h6").eachText();
						if(!h6List.isEmpty()){
							
							ArrayList<String>tokensH6=new ArrayList();
							for(int j=0;j<h6List.size();j++) {
								String[] tempTokens=h6List.get(j).split("[^a-zA-Z0-9'-]");
								tokensH6.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensH6.size();
							stemAndRemoveStopWords(tokensH6,url,"h6",tokens);
							
						}
						Elements p = words.select("p");
						String textp=p.text();
						java.util.List<String> pList = words.select("p").eachText();
						if(!pList.isEmpty()){
							
							ArrayList<String>tokensp=new ArrayList();
							for(int j=0;j<pList.size();j++) {
								String[] tempTokens=pList.get(j).split("[^a-zA-Z0-9'-]");
								tokensp.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensp.size();
							stemAndRemoveStopWords(tokensp,url,"p",tokens);
							
						}
						Elements titleTags = words.select("title");
						String textTitle=titleTags.text();
						java.util.List<String> titleList = words.select("title").eachText();
						if(!titleList.isEmpty()){
							
							ArrayList<String>tokensTitle=new ArrayList();
							for(int j=0;j<titleList.size();j++) {
								String[] tempTokens=titleList.get(j).split("[^a-zA-Z0-9'-]");
								tokensTitle.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensTitle.size();
							stemAndRemoveStopWords(tokensTitle,url,"title",tokens);
							
						}
						Elements italicTags = words.select("i");
						String textItalic=italicTags.text();
						java.util.List<String> iList = words.select("i").eachText();
						if(!iList.isEmpty()){
							
							ArrayList<String>tokensI=new ArrayList();
							for(int j=0;j<iList.size();j++) {
								String[] tempTokens=iList.get(j).split("[^a-zA-Z0-9'-]");
								tokensI.addAll(Arrays.asList(tempTokens));
							}
							wordCount+=tokensI.size();
							stemAndRemoveStopWords(tokensI,url,"italic",tokens);
							
						}
						Elements boldTags = words.select("b");
						String textBold=boldTags.text();
						java.util.List<String> bList = words.select("b").eachText();
						if(!bList.isEmpty()){
							
							ArrayList<String>tokensBold=new ArrayList();
							for(int j=0;j<bList.size();j++) {
								String[] tempTokens=bList.get(j).split("[^a-zA-Z0-9'-]");
								tokensBold.addAll(Arrays.asList(tempTokens));
							}
						
							wordCount+=tokensBold.size();
							stemAndRemoveStopWords(tokensBold,url,"bold",tokens);
							
						}
						
						}
						catch(Exception e) {
							
						}
						
						try {
							if(tokens.isEmpty()==false) {
								// save array of tokens after stemming them in the database
								// and also save the count of words in each url
							DatabaseConnection.addWords(tokens, url,wordCount);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
			
					}
					
					//set done indexing to 1
					
						try {
							if(doc!=null)
							DatabaseConnection.SetDoneIndexing(url);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//get first un-indexed url to index it
						synchronized(this.dummy) {
						try {
							url = DatabaseConnection.getFirstUnIndexed(this.ThreadNo);
							if(url!=null)
							DatabaseConnection.setStartIndexing(url.toString());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		// 
			public void stemAndRemoveStopWords(ArrayList<String>Tags,String url,String pos,ArrayList<Word> tokens) throws IOException, SQLException{
				ArrayList<String> stemmedWords=Parser.parse(Tags);
				int exist=0;
				for(int i=0;i<stemmedWords.size();i++) {
					for(int j=0;j<tokens.size();j++) {
						if(stemmedWords.get(i).equals(tokens.get(j).word)==true) {
							tokens.get(j).incrementOccurence(pos);
							exist=1;
							break;
						}
						
					}
					if(exist==0) {
						Word token=new Word(stemmedWords.get(i));
						token.incrementOccurence(pos);
						tokens.add(token);
					}
					exist=0;
				}
				
		}
}
}
