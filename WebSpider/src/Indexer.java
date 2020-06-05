import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import marytts.util.math.ArrayUtils;

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
	}


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
    	//Connect to DataBase
    	DatabaseConnection.DatabaseConnect();
//
//    	System.out.println("Enter You want to re-index or index: (1) index (2) re-index");
//    	 BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
//        // Reading data using readLine 
//
//    	 String type = consoleReader.readLine(); 
//        
//        //check if he wants to re-index the content or not
//        if(Integer.parseInt(type)==2) {
//        	//drop all the table
//        	
//        	DatabaseConnection.dropIndexingTable();
//        	//reset start indexing of all urls 
//        	DatabaseConnection.resetDoneAndStartIndexing();
//        	
//        }
//        else{
//        	DatabaseConnection.deleteNonDoneIndexingWords();
//        }
//        consoleReader.close();
//        Parser.loadStopwords();
//    	Indexer indexer=new Indexer();
    	
    	Ranker.calculatePageRank();
    	
    }
    private static class Index extends Thread{
    	
    	int ThreadNo;
    	Integer dummy;
    	public Index(int ThreadNum,Integer d) {
    		this.ThreadNo=ThreadNum;
    		this.dummy=d;
    	}
    
		public void run(){
		
			
			//create 5 threads
			String url = null ;
			int i=0;
			while(i==0||url!=null) {
				i++;
				synchronized(this.dummy) {
					try {
						url=DatabaseConnection.getFirstUnIndexed(this.ThreadNo);
						if(url!=null)
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
						Elements words = doc.select("h1, h2, h3, h4, h5, h6,p,title,i,b");
						
						ArrayList<Word> tokens=new ArrayList<Word>();
						Integer wordCount=0;
						try {
						Elements h1Tags = words.select("h1");
						System.out.print("H1Tagssssssssssssssssssssssssssssssssssss\n");
						///String textH1=h1Tags.text();
						java.util.List<String> h1List = words.select("h1").eachText();
						System.out.println(Arrays.toString(h1List.toArray()));
						System.out.print("=============================================\n");
						if(!h1List.isEmpty()){
							
							ArrayList<String>tokensH1=new ArrayList();
							for(int j=0;j<h1List.size();j++) {
								String[] tempTokens=h1List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH1.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH1+"\n");
							}
						
							wordCount+=tokensH1.size();
							stemAndRemoveStopWords(tokensH1,url,"h1",tokens);
							
							}
						Elements h2Tags = words.select("h2");
						System.out.print("H2Tagssssssssssssssssssssssssssssssssssss\n");
						String textH2=h1Tags.text();
						java.util.List<String> h2List = words.select("h2").eachText();
						System.out.println(Arrays.toString(h2List.toArray()));
						System.out.print("=============================================\n");
						if(!h2List.isEmpty()){
							
							ArrayList<String>tokensH2=new ArrayList();
							for(int j=0;j<h2List.size();j++) {
								String[] tempTokens=h2List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH2.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH2+"\n");
							}
							wordCount+=tokensH2.size();
							stemAndRemoveStopWords(tokensH2,url,"h2",tokens);
							
							}

						Elements h3Tags = words.select("h3");
						System.out.print("H3Tagssssssssssssssssssssssssssssssssssss\n");
						String textH3=h3Tags.text();
						java.util.List<String> h3List = words.select("h3").eachText();
						System.out.println(Arrays.toString(h3List.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("H3Tagssssssssssssssssssssssssssssssssssss\n"+textH3+"\n");
						if(!h3List.isEmpty()){
							
							ArrayList<String>tokensH3=new ArrayList();
							for(int j=0;j<h3List.size();j++) {
								String[] tempTokens=h3List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH3.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH3+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensH3.size();
							stemAndRemoveStopWords(tokensH3,url,"h3",tokens);
							
							}
						Elements h4Tags = words.select("h4");
						System.out.print("H4Tagssssssssssssssssssssssssssssssssssss\n");
						String textH4=h4Tags.text();
						java.util.List<String> h4List = words.select("h4").eachText();
						System.out.println(Arrays.toString(h4List.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("H4Tagssssssssssssssssssssssssssssssssssss\n"+textH4+"\n");
						if(!h4List.isEmpty()){
							
							ArrayList<String>tokensH4=new ArrayList();
							for(int j=0;j<h4List.size();j++) {
								String[] tempTokens=h4List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH4.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH4+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensH4.size();
							stemAndRemoveStopWords(tokensH4,url,"h4",tokens);
							
							}
						Elements h5Tags = words.select("h5");
						System.out.print("H5Tagssssssssssssssssssssssssssssssssssss\n");
						String textH5=h5Tags.text();
						java.util.List<String> h5List = words.select("h5").eachText();
						System.out.println(Arrays.toString(h5List.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("H5Tagssssssssssssssssssssssssssssssssssss\n"+textH5+"\n");
						if(!h5List.isEmpty()){
							
							ArrayList<String>tokensH5=new ArrayList();
							for(int j=0;j<h5List.size();j++) {
								String[] tempTokens=h5List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH5.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH5+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensH5.size();
							stemAndRemoveStopWords(tokensH5,url,"h5",tokens);
							
							}
						Elements h6Tags = words.select("h6");
						System.out.print("H6Tagssssssssssssssssssssssssssssssssssss\n");
						String textH6=h6Tags.text();
						java.util.List<String> h6List = words.select("h6").eachText();
						System.out.println(Arrays.toString(h6List.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("H6Tagssssssssssssssssssssssssssssssssssss\n"+textH6+"\n");
						if(!h6List.isEmpty()){
							
							ArrayList<String>tokensH6=new ArrayList();
							for(int j=0;j<h6List.size();j++) {
								String[] tempTokens=h6List.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensH6.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensH6+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensH6.size();
							stemAndRemoveStopWords(tokensH6,url,"h6",tokens);
							
							}
						Elements p = words.select("p");
						System.out.print("ppTagssssssssssssssssssssssssssssssssssss\n");
						String textp=p.text();
						java.util.List<String> pList = words.select("p").eachText();
						System.out.println(Arrays.toString(pList.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("ppppppppppppppppppppppppppppppppppppppppppppp\n"+textp+"\n");
						if(!pList.isEmpty()){
							
							ArrayList<String>tokensp=new ArrayList();
							for(int j=0;j<pList.size();j++) {
								String[] tempTokens=pList.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensp.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensp+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensp.size();
							stemAndRemoveStopWords(tokensp,url,"p",tokens);
							
							}
						Elements titleTags = words.select("title");
						System.out.print("titleTagssssssssssssssssssssssssssssssssssss\n");
						String textTitle=titleTags.text();
						java.util.List<String> titleList = words.select("title").eachText();
						System.out.println(Arrays.toString(titleList.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("textTitlleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n"+textTitle+"\n");
						if(!titleList.isEmpty()){
							
							ArrayList<String>tokensTitle=new ArrayList();
							for(int j=0;j<titleList.size();j++) {
								String[] tempTokens=titleList.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensTitle.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensTitle+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensTitle.size();
							stemAndRemoveStopWords(tokensTitle,url,"title",tokens);
							
							}
						Elements italicTags = words.select("i");
						System.out.print("italicTagssssssssssssssssssssssssssssssssssss\n");
						String textItalic=italicTags.text();
						java.util.List<String> iList = words.select("i").eachText();
						System.out.println(Arrays.toString(iList.toArray()));
						System.out.print("=============================================\n");
//						System.out.print("textItaliccccccccccccccccccccccccccccccccccccccc\n"+textItalic+"\n");
						if(!iList.isEmpty()){
							
							ArrayList<String>tokensI=new ArrayList();
							for(int j=0;j<iList.size();j++) {
								String[] tempTokens=iList.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensI.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensI+"\n");
							}
							//display bothhhhhhhhhhhhhhhhhhhhhhhhhh
							wordCount+=tokensI.size();
							stemAndRemoveStopWords(tokensI,url,"italic",tokens);
							
							}
						Elements boldTags = words.select("b");
						System.out.print("boldTagssssssssssssssssssssssssssssssssssss\n");
						String textBold=boldTags.text();
						java.util.List<String> bList = words.select("b").eachText();
						System.out.println(Arrays.toString(bList.toArray()));
						System.out.print("=============================================\n");
						if(!bList.isEmpty()){
							
							ArrayList<String>tokensBold=new ArrayList();
							for(int j=0;j<bList.size();j++) {
								String[] tempTokens=bList.get(j).split("[^a-zA-Z0-9'-]");
								System.out.println(Arrays.toString(tempTokens)+"\n");
								tokensBold.addAll(Arrays.asList(tempTokens));
								System.out.println(tokensBold+"\n");
							}
						
							wordCount+=tokensBold.size();
							stemAndRemoveStopWords(tokensBold,url,"bold",tokens);
							
							}
						
						}
						catch(Exception e) {
							
						}
						
						try {
							if(tokens.isEmpty()==false) {
								System.out.print("WORD COUNTTTTTTTTTTTTTTTTTTTTTTTTT "+wordCount+"\n");
								for(int k=0;k<tokens.size();k++) {
									tokens.get(k).print();
								}
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
