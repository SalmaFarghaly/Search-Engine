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
		index1.join();
		index2.join();
		index3.join();
		index4.join();
		index5.join();
//		Thread index6=new Index(++ThreadNo,dummy);
//		index6.start();
//		Thread index7=new Index(++ThreadNo,dummy);
//		index7.start();
//		Thread index8=new Index(++ThreadNo,dummy);
//		index8.start();
//		Thread index9=new Index(++ThreadNo,dummy);
//		index9.start();
//		Thread index10=new Index(++ThreadNo,dummy);
//		index10.start();
//		Thread index11=new Index(++ThreadNo,dummy);
//		index11.start();
	}


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
		//clear cache
    	java.util.ResourceBundle.clearCache(); 
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
        	
        }
        else{
        	DatabaseConnection.deleteNonDoneIndexingWords();
        }
        consoleReader.close();
        Parser.loadStopwords();
    	Indexer indexer=new Indexer();
//    	DatabaseConnection.updateIDIncrementally();
    	
    //	Ranker.calculatePageRank();
    	
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
						doc = Jsoup.connect(url).timeout(180000).ignoreHttpErrors(true).get();
					} catch (IOException e) {
//						try {
////							DatabaseConnection.SetDoneIndexing(url);
//						} catch (SQLException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(doc!=null) {
						Elements words = doc.select("h1, h2, h3, h4, h5, h6,p,title,i,b");
						
						ArrayList<Word> tokens=new ArrayList<Word>();
						Integer wordCount=0;
						try {
						Elements h1Tags = words.select("h1");
						String textH1=h1Tags.text();
//						System.out.print("H1Tagssssssssssssssssssssssssssssssssssss\n"+textH1+"\n");
						if(!textH1.isEmpty()&&!textH1.isBlank()){
							String[] tokensH1 = textH1.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH1.length;
							stemAndRemoveStopWords(textH1,url,"h1",tokens);
							
							}
						Elements h2Tags = words.select("h2");
						String textH2=h1Tags.text();
//						System.out.print("H2Tagssssssssssssssssssssssssssssssssssss\n"+textH2+"\n");
						if(!textH2.isEmpty()&&!textH2.isBlank()) {
							String[] tokensH2 = textH2.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH2.length;
							stemAndRemoveStopWords(textH2,url,"h2",tokens);
						}
						Elements h3Tags = words.select("h3");
						String textH3=h3Tags.text();
//						System.out.print("H3Tagssssssssssssssssssssssssssssssssssss\n"+textH3+"\n");
						if(!textH3.isEmpty()&&!textH3.isBlank()) {
							String[] tokensH3 = textH3.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH3.length;
							stemAndRemoveStopWords(textH3,url,"h3",tokens);
							}
						Elements h4Tags = words.select("h4");
						String textH4=h4Tags.text();
//						System.out.print("H4Tagssssssssssssssssssssssssssssssssssss\n"+textH4+"\n");
						if(!textH4.isEmpty()&&!textH4.isBlank()) {
							String[] tokensH4 = textH4.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH4.length;
							stemAndRemoveStopWords(textH4,url,"h4",tokens);
						}
						Elements h5Tags = words.select("h5");
						String textH5=h5Tags.text();
//						System.out.print("H5Tagssssssssssssssssssssssssssssssssssss\n"+textH5+"\n");
						if(!textH5.isEmpty()&&!textH5.isBlank()) {
							String[] tokensH5 = textH5.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH5.length;
							stemAndRemoveStopWords(textH5,url,"h5",tokens);
						}
						Elements h6Tags = words.select("h6");
						String textH6=h6Tags.text();
//						System.out.print("H6Tagssssssssssssssssssssssssssssssssssss\n"+textH6+"\n");
						if(!textH6.isEmpty()&&!textH6.isBlank()) {
							String[] tokensH6 = textH6.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensH6.length;
							stemAndRemoveStopWords(textH6,url,"h6",tokens);
						}
						Elements p = words.select("p");
						String textp=p.text();
//						System.out.print("ppppppppppppppppppppppppppppppppppppppppppppp\n"+textp+"\n");
						if(!textp.isEmpty()&&!textp.isBlank()) {
							String[] tokensp = textp.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensp.length;
							stemAndRemoveStopWords(textp,url,"p",tokens);
						}
						Elements titleTags = words.select("title");
						String textTitle=titleTags.text();
						String title = doc.title();
						textTitle += " ";
						textTitle += title;
//						System.out.print("textTitlleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n"+textTitle+"\n");
						if(!textTitle.isEmpty()&&!textTitle.isBlank()) {
							String[] tokensTitle = textTitle.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensTitle.length;
							stemAndRemoveStopWords(textTitle,url,"title",tokens);
						}
						Elements italicTags = words.select("i");
						String textItalic=italicTags.text();
//						System.out.print("textItaliccccccccccccccccccccccccccccccccccccccc\n"+textItalic+"\n");
						if(!textItalic.isEmpty()&&!textItalic.isBlank()) {
							String[] tokensI = textItalic.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensI.length;
							stemAndRemoveStopWords(textItalic,url,"i",tokens);
						}
						Elements boldTags = words.select("b");
						String textBold=boldTags.text();
//						System.out.print("textBolldddddddddddddddddddddddddddddddddddddddddddd\n"+textBold+"\n");
						if(!textBold.isEmpty()&&!textBold.isBlank()) {
							String[] tokensBold = textBold.split("[^a-zA-Z0-9'-]");
							wordCount+=tokensBold.length;
							stemAndRemoveStopWords(textBold,url,"b",tokens);
						}
						}
						catch(Exception e) {
							
						}
						
						try {
							if(tokens.isEmpty()==false) {
								System.out.print("WORD COUNTTTTTTTTTTTTTTTTTTTTTTTTT "+wordCount+"\n");
								for(int i=0; i<tokens.size(); i++) {
									System.out.print(tokens.get(i));
									System.out.print("\r\n");
									

									 
								}
								
							DatabaseConnection.addWords(tokens, url,wordCount);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//document.title
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
			public void stemAndRemoveStopWords(String Tags,String url,String pos,ArrayList<Word> tokens) throws IOException, SQLException{
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
