import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    
    public int count =0;
    //maximum number of links saved in database
    public int threshold=8500;

    //startURL is seed set
    private WebSpider(final ArrayList<URL> list,int type) throws SQLException, MalformedURLException {
        this.startTime = System.currentTimeMillis();
        count=list.size();
        //check that it was first time to run crawler
        if(type==1&&DatabaseConnection.isThreadStateEmpty()==true) {
        	System.out.print("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG\n");
	       for(URL url :list){
	    	   DatabaseConnection.saveInitials(url.toString());
	       }
       }
       crawl(list); 
    }

    private void crawl(ArrayList<URL> URLS) throws SQLException, MalformedURLException {
    	int c1=URLS.size();
    	int i=1;
    	System.out.print(URLS);
    	//crawling the seedList
    	int urlCnt=DatabaseConnection.getCountUrl();
    	//first time to run crawler , database is empty (i.e No links)
    	if(URLS.isEmpty()==false) {
	    	for(URL url:URLS) {
	    		System.out.print("THread NO "+i+"created \n");
	    		new Crawl(url,this.dummy,i).start();
	    		i++;
	    	}
    	}
    	// the crawler was interrupted , and we retrieve the state of each thread and start it
    	else if(urlCnt<threshold) {
    		String url=DatabaseConnection.getThreadUrl(i);
    		while(url!=null) {
    			System.out.print("THread NO "+i+"created \n");
    			new Crawl(new URL(url),this.dummy,i).start();
    			i++;
    			url=DatabaseConnection.getThreadUrl(i);
    		}
    		
    	}
    	//else means the db is filled with it's threshold , so we have to terminate the program
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
     	//max number of links
        public int threshold=6500;
    	
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
    	while(newURLS.isEmpty()==false&& countURLS<threshold) {

    		
	        currentURL=this.newURLS.get(0);
	        try {
	        	//save the current URL that thread state table that it is the current url that is being crawled
	        	//to use it later in case an interrupt has occurred
				DatabaseConnection.saveThreadState(this.threadNo, currentURL.toString());
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
	        System.out.print("CUREENT URL IS "+currentURL+"\n");
	        Document document=null;
			try {
				//infinite timeOut
				document = Jsoup.connect(currentURL.toString()).timeout(0).get();
			
				
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
		        	   	if(countURLS>=threshold)
		        	   		break;
		              final String urlText = page.attr("abs:href").trim();
		              if(urlText=="")
		            	  continue;
		              System.out.print("Thread # "+this.threadNo+" DISCOVERED "+urlText+"\n");
		              final URL discoveredURL = new URL(urlText);
//		              System.out.print("Thread # "+this.threadNo+" DISCOVERED "+discoveredURL+"\n");
		              parts=discoveredURL.toString().split("#");
		              //check if this hyper link is image,xml
		              if(parts[0].matches("(.*).jpg")==true||parts[0].matches("(.*)/pdf")==true||parts[0].matches("(.*).pdf")==true||parts[0].matches("(.*).png")==true
		            		  ||parts[0].matches("(.*).asp")==true||parts[0].matches("(.*)/image(.*)")==true||parts[0].matches("(.*)login(.*)")==true)
		            	  continue;
		            //check if used protocol is http or https
		              if(parts[0].matches("http://(.*)")==false && parts[0].matches("https://(.*)")==false) 
		            	  continue;
		            	  //check if it is allowed to enter link
		              if(robotSafe(new URL(parts[0]))==false )
		            	  continue;

		        	
		 
		        	  try {
		      
		              
					  if(DatabaseConnection.isLinkExist(parts[0])==false&&countURLS<threshold){
						  	this.newURLS.add(new URL(parts[0]));
						  	this.count++;
					  }
					  //those 3 lines need a lock on database
						synchronized(this.dummy) 
						{

			              if(countURLS<threshold&&DatabaseConnection.isRelationExist(currentURL.toString(), parts[0])==0) {
			            	  //lock table
			              	DatabaseConnection.incrementInBound(parts[0]);
			              	DatabaseConnection.insertDocument(currentURL.toString(),parts[0]);
			              	DatabaseConnection.incrementOutBound(currentURL.toString(),1);
			              	//unlock table
			              }
			           
			              else if(countURLS>=threshold)
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
    	System.out.print(this.threadNo+"  FINISHEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD\n");
          
    		
    	}
    	
    }
    
    public static boolean robotSafe(URL url) 
    {
        String strHost = url.getHost();
        
//        System.out.print("strHostttt"+ strHost+"\n");
        String strRobot = "https://" + strHost + "/robots.txt";
        URL urlRobot;
//        System.out.print("strRobottttttttt"+ strRobot+"\n");
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // something weird is happening, so don't trust it
            return true;
        }

        String strCommands;
        ArrayList<RobotRule> robotRules = new ArrayList<>();
        try 
        {
            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
            try {
            strCommands = new String(b, 0, numRead);
            }
            catch(Exception e) {
            	return true;
            }
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1) 
                {
                        String newCommands = new String(b, 0, numRead);
                        strCommands += newCommands;
                }
            }
           urlRobotStream.close();
        } 
        catch (IOException e) 
        {
            return true; // if there is no robots.txt file, it is OK to search
        }

        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
        {
            String[] split = strCommands.split("\n");
            String mostRecentUserAgent = null;
            for (int i = 0; i < split.length; i++) 
            {
                String line = split[i].trim();
                if (line.toLowerCase().startsWith("user-agent")) 
                {
                	
                    int start = line.indexOf(":") + 1;
                    int end   = line.length();
                    mostRecentUserAgent = line.substring(start, end).trim();
                }
                else if (line.startsWith("Disallow")) {
                    if (mostRecentUserAgent != null) {
                        RobotRule r = new RobotRule();
                        r.userAgent = mostRecentUserAgent;
                        int start = line.indexOf(":") + 1;
                        int end   = line.length();
                        r.rule = line.substring(start, end).trim();
                        robotRules.add(r);
                    }
                }
            }
            String path = url.getPath();
            for (RobotRule robotRule : robotRules)
            {
               
                if (robotRule.rule.length() == 0) return true; // allows everything if BLANK
                if (robotRule.rule == "/") return false;       // allows nothing if /

                if (robotRule.rule.length() <= path.length())
                { 
                    String pathCompare = path.substring(0, robotRule.rule.length());
                    if (pathCompare.equals(robotRule.rule)) return false;
                }
            }
        }
        return true;
    }
    public static void main(String[] args) throws IOException, SQLException {
    	BufferedReader reader;
    	ArrayList<URL> list = new ArrayList<URL>();
    	//connect to Database
    	DatabaseConnection.DatabaseConnect();
    	
    	System.out.println("Enter You want to recrawl or crawl: (1) crawl (2) recrawl");
    	 BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
        // Reading data using readLine 
        String type = consoleReader.readLine(); 
        //first check the thread state table to see if the threads were ran before 
        //or we will read the seed list
        //first time to run crawler or need to recrawl
        //if ThreadState is saved and he want to crawl "1" that means interrupt has occurred
    	if(DatabaseConnection.isThreadStateEmpty()==true||Integer.parseInt(type)==2) {
	    	try {
				reader = new BufferedReader(new FileReader("C:\\Users\\Dell\\Desktop\\Apt project\\seedlist trial.txt"));
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
    	}
    
    	final WebSpider crawler = new WebSpider(list,Integer.parseInt(type));
    	
    }

}
