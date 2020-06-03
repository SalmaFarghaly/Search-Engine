import java.io.*;
<<<<<<< HEAD
=======
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
>>>>>>> 27e107293706f022bffc2cd18c4dc6058c49bc7e
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.servlet.*;
import javax.servlet.http.*;

<<<<<<< HEAD
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

//import marytts.TextToSpeech;
import marytts.signalproc.effects.JetPilotEffect;
import net.sourceforge.javaflacencoder.FLACFileWriter;
=======
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
>>>>>>> 27e107293706f022bffc2cd18c4dc6058c49bc7e

public class WebInterface extends HttpServlet{
	
	private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
	private final GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        String SearchInput = request.getParameter("SearchInput");
        double time = 0;
        if (SearchInput != null) //if action is not null
        {
        	String names[] = SearchInput. split(" ");
        	//time = "hi";
            String action = request.getParameter("button");
            if (action.equals("Search")) //add button clicked
            {
            	//------------------------------------------- adding first part of template with style sheet -------------------------------
               	response.setContentType("text/html");
               PrintWriter out = response.getWriter();
            	// to edit the title
            	String message = "hi";//,url = "https://www.youtube.com/watch?v=khJpNYlpinY&pbjreload=101";
                out.println("<!doctype html>\n" + 
                		"<html>\n" + 
                		"\n" + 
                		"<head>\n" + 
                		"    <title>"+SearchInput+" - Bing Search</title>\n" + 
                		"    <link rel=\"shortcut icon\" type=\"image/ico\" href=\"images/favicon.ico\" />\n" + 
                		"	 <link rel=\"stylesheet\" href=\"css/indexcss3.css\">\n" + 
                		"</head>");
                // to edit search bar
                out.println("\n" + 
                		"<body>\n" + 
                		"    <div id=\"header\">\n" + 
                		"        <div id=\"topbar\">\n" + 
                		"            <img id=\"searchbarimage\" src=\"images/googlelogo_color_92x30dp.png\" />\n" + 
                		"            <div id=\"searchbar\" type=\"text\">\n" + 
                		"                <input id=\"searchbartext\" type=\"text\" value=\""+SearchInput +" \"/>");
            	RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/part1.html");
           		rd.include(request, response);
                
            	//------------------------------ Getting Search Results --------------------------
            	Map<String, Double> output = null;
            	try {
					 output = QueryProcessor.queryProcessor(SearchInput);
				} catch (IOException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	time = 0.33;
               //do your work here
            	  for (Entry<String, Double> entry : output.entrySet())  {
      	        	System.out.println("Key final= " + entry.getKey() + 
      	                    ", Value final= " + entry.getValue()); 
            	//----------------------------------------- getting url content -----------------------------
		          String url =entry.getKey();
		 			Document doc;
		 			Elements words1 = null;
		 			String title = null;
		 		 try {
		 			System.out.println("Connecting to document");
		 			doc = Jsoup.connect(url).timeout(180000).ignoreHttpErrors(true).get();
		 		 	title = doc.title();
		 			words1 = doc.select("p");
		 		} catch (IOException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
		 		System.out.println("Done Connecting");
		 		List<String> tagsinfo = words1.select("p").eachText();
		 		
		 		//String names[] = {"world", "cup"};
		 		List<String>  exist =  new ArrayList<String>();
		 		int count = 0;
		 		for(int i=0; i<tagsinfo.size(); i++) {
		 		    if(Stream.of(names).anyMatch(tagsinfo.get(i).toLowerCase()::contains)) {
		 		    	exist.add(tagsinfo.get(i));
		 		    	count++;
		 		    }
		 		     if(count>2)
		 		    	 break;
		 		}
				/*
				 * for(int i = 0 ; i<exist.size(); i++) { System.out.println(
				 * tagsinfo.get(exist.get(i))); }
				 */
            	  	
            	//------------------------------------------ end here ---------------------------------------
            	
            	//List<String> stopWords1 = Files.readAllLines(Paths.get("C:\\Users\\Lenovo\\Downloads\\apache-tomcat-9.0.34\\webapps\\ROOT\\part1.html"));
            	//List<String> stopWords2 = Files.readAllLines(Paths.get("C:\\Users\\Lenovo\\Downloads\\apache-tomcat-9.0.34\\webapps\\ROOT\\trial2.html"));
                	
            	//response.getWriter().println(stopWords1);
            //	 response.getWriter().println(stopWords2);
          
				/*
				 * out.println("    <div id=\"searchresultsarea\">\n" +
				 * "        <p id=\"searchresultsnumber\">About "+ message +" results (" +time+
				 * " seconds) </p>\n" + "");
				 */
		 		System.out.println("Adding document to html file");
		 		String snippets=null;
		 		for(int j=0; j<count;j++ )
		 			 snippets = exist.get(j);
		 		String replaceString = null;
		 		if(snippets!=null) {
			 		for (int i=0;i<names.length; i++) {
			 			String boldText = "<b>"+names[i]+"</b>";
				 	   	replaceString=snippets.replaceAll(names[i],boldText);
			 		}
		 		}
		 		System.out.println("Adding document to html file");
		 		if(replaceString!=null) {
	        		out.println("    <div class=\"searchresult\">\n" + 
	        				"            <h2>"+title+"</h2>\n" + 
	        				"            <a>"+url+"</a> <button> </button>\n" + 
	        				"            <p>"+replaceString+"</p>\n" + 
	        				"        </div>\n"  
	        				);
		 		}
        		}
            	  System.out.println("Done Adding document to html file");
            	  RequestDispatcher rd2 = request.getRequestDispatcher("/WEB-INF/part2.html");
          		rd2.include(request, response);
          		 System.out.println("Done");
            	  }
            
            try {
            	System.out.print(SearchInput+"\n");
    			DatabaseConnection.saveSearchQuery(SearchInput);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            	
        		
            }
           
        }
		
 
	public static void getSnippets (String url) {
	
		
	}
}
/*	DatabaseConnection.DatabaseConnect();
    
        response.setContentType("text/html");
        //String message = "Your Search Query is " + SearchInput ;
        List<Entry<String, Double>> m = null;
        long startTime = 0, endTime = 0;
        
		try {
			try {
				 startTime = System.nanoTime();
				m = Ranker.ranker(SearchInput);
				endTime = System.nanoTime();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			 
			//List<String> stopWords = Files.readAllLines(Paths.get("C:\\Users\\Lenovo\\Downloads\\apache-tomcat-9.0.34\\webapps\\ROOT\\trial2.html"));
		  	long time = endTime-startTime;
		  	time = time/1000000000;
 * */
 
>>>>>>> 27e107293706f022bffc2cd18c4dc6058c49bc7e
