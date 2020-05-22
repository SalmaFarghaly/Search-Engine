/*import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebSpider {
	private final Set<URL>links;
	private final long startTime;
	
	private WebSpider(final URL startURl) {
		this.links=new HashSet<>();
		this.startTime=System.currentTimeMillis();
		crawl(initURLS(startURL));
		
	}
	private Set<URL> initURLS(final URL startURL){
		
		return Collections.singleton(startURL);
		
	}
	
	public static void main(String[] args) throws IOException {
		final WebSpider spider=new WebSpider(new URL("http://www.gutenberg.org/"));
	}
}*/
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



public class WebSpider{

//    private final Set<URL> links;
    private final long startTime;
    
    //lock
    public Integer dummy=1;
    
    //maximum links is 5000
    public int count =0;

    //startURL is seed set
    private WebSpider(final ArrayList<URL> list) throws SQLException {
        this.startTime = System.currentTimeMillis();
        count=list.size();
       for(URL url :list){
    	   DatabaseConnection.saveInitials(url.toString());
    	   
    }
       crawl(list); 
    }

    private void crawl(ArrayList<URL> URLS) throws SQLException {
    	int c1=URLS.size();
    	int i=1;
    	System.out.print(URLS);
    	//crawling the seedList
    	for(URL url:URLS) {
    		System.out.print("THread NO "+i+"created \n");
    		new Crawl(url,this.dummy,i).start();
    		i++;
    	}
  }

    public static void main(String[] args) throws IOException, SQLException {
    	BufferedReader reader;
    	ArrayList<URL> list = new ArrayList<URL>();
    	
    	
    	
    	try {
			reader = new BufferedReader(new FileReader("C:\\Users\\Lenovo\\git\\Search-Engine1\\seedlist.txt"));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				list.add(new URL(line));
				line = reader.readLine();
			}
			reader.close();
			System.out.print(list+"\n");
		}
    	catch (IOException e) {
    		System.out.print("ERROR"+e+"\n");
			e.printStackTrace();
		}
    	DatabaseConnection.DatabaseConnect();
    	final WebSpider crawler = new WebSpider(list);
    	
    }
    
    private static class Crawl extends Thread{
    	//crawled link
    	URL link;
    	// the discovered urls
     	ArrayList<URL> newURLS = new ArrayList<URL>();
     	//number of discovered url's in this link.
     	int count=0;
     	//dummy variable for syncronization
     	Integer dummy=1;
     	///thread number
     	int threadNo;
    	
    	public Crawl(URL url,Integer d, int num) {
    		this.link=url;
    		this.dummy=d;
    		this.threadNo=num;
    		newURLS.add(url);
    		System.out.print("Thread # "+this.threadNo+" starts Crawling "+ this.link + "\n");
    	}
    	
    	public void run() {
    	int i=0;
    	URL currentURL=null;
    	   int countURLS=0;
    	while((newURLS.isEmpty()==false||i==0) && countURLS<10000) {
    		
	        currentURL=this.newURLS.get(0);
	        System.out.print("CUREENT URL IS "+currentURL+"\n");
	        Document document=null;
			try {
				document = Jsoup.connect(currentURL.toString()).get();
				
		          final Elements linksOnPage = document.select("a[href]");
		          System.out.print("HYPERRRRRRRRRRRRRRRRRRRRRRCOUNTTTTTTTTTTTTTTTTTTTTTT"+linksOnPage.size()+"\n");
	              String [] parts=null;
		          for (final Element page : linksOnPage) {
		        	synchronized(this.dummy) 
					{
		              try {
		  				countURLS=DatabaseConnection.getCountUrl();
		  	          } 
		  	          catch (SQLException e1) {
		  				e1.printStackTrace();
		  	          }
					}
		 
		        	  try {
		              final String urlText = page.attr("abs:href").trim();
		              if(urlText=="")
		            	  continue;
		              System.out.print("Thread # "+this.threadNo+" DISCOVERED "+urlText+"\n");
		              final URL discoveredURL = new URL(urlText);
//		              System.out.print("Thread # "+this.threadNo+" DISCOVERED "+discoveredURL+"\n");
		              parts=discoveredURL.toString().split("#");
		              
					  if(DatabaseConnection.isLinkExist(parts[0])==false&&countURLS<10000){
						  	this.newURLS.add(new URL(parts[0]));
						  	this.count++;
					  }
					  //those 3 lines need a lock on database
						synchronized(this.dummy) 
						{
					  //check after # relation exist 
			              if(countURLS<10000&&DatabaseConnection.isRelationExist(currentURL.toString(), parts[0])==0) {
			            	  //lock table
			              	DatabaseConnection.incrementInBound(parts[0]);
			              	DatabaseConnection.insertDocument(currentURL.toString(),parts[0]);
			              	DatabaseConnection.incrementOutBound(currentURL.toString());
			              	//unlock table
			              }
			           
			              else if(countURLS>=10000)
			              	break;
			        	  }
		        	  }
		        	 catch (MalformedURLException | SQLException e) {
		        		 System.out.print(parts[0]+" Malformed Exception "+ e +"\n" );
		  				e.printStackTrace();
		  			}
		        	  
		         }
		          newURLS.remove(0);
			} catch (IOException e1) {
				System.out.print("Jsoup Error "+ e1 +"\n" );
				e1.printStackTrace();
				newURLS.remove(0);
			}
	          
	        
    	}
          
    		
    	}
    	
    }

}




