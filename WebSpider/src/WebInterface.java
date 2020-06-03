import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.servlet.*;
import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebInterface extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        String SearchInput = request.getParameter("SearchInput");
        double time = 0;
        if (SearchInput != null) //if action is not null
        {
        	//time = "hi";
            String action = request.getParameter("button");
            if (action.equals("Search")) //add button clicked
            {
            	time = 0.33;
               //do your work here
            	response.setContentType("text/html");
            	//----------------------------------------- getting url content -----------------------------
		          String url = ("https://www.liverpoolfc.com/team/first-team/player/takumi-minamino");
		 			Document doc;
		 			Elements words1 = null;
		 			String title = null;
		 		 try {
		 			doc = Jsoup.connect(url).get();
		 			title = doc.title();
		 			words1 = doc.select("p");
		 		} catch (IOException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
		 		
		 		List<String> tagsinfo = words1.select("p").eachText();
		 	//	StringBuilder d = new StringBuilder();
		 		//for(int i=0; i<tagsinfo.size(); i++) {
		 			//d.append(tagsinfo.get(i));
		 		//	d.append(" ");
		 	//	}
		 		String names[] = {"world", "cup"};
		 		//names[1]= "cup";
		 		List<String>  exist =  new ArrayList<String>();
		 		int count = 0;
		 		for(int i=0; i<tagsinfo.size(); i++) {
		 		    if(Stream.of(names).anyMatch(tagsinfo.get(i).toLowerCase()::contains)) {
		 		    	exist.add(tagsinfo.get(i));
		 		    }
		 		     
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
            	PrintWriter out = response.getWriter();
            	// to edit the title
            	String message = "hi";//,url = "https://www.youtube.com/watch?v=khJpNYlpinY&pbjreload=101";
            	String snippets = exist.get(0)+exist.get(1)+exist.get(2);
                out.println("<!doctype html>\n" + 
                		"<html>\n" + 
                		"\n" + 
                		"<head>\n" + 
                		"    <title>computer engineering mutex concept - Google Search</title>\n" + 
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
            	out.println("	<p id=\"searchresultsnumber\">About 155,000 results (0.56 seconds) </p>");
				/*
				 * out.println("    <div id=\"searchresultsarea\">\n" +
				 * "        <p id=\"searchresultsnumber\">About "+ message +" results (" +time+
				 * " seconds) </p>\n" + "");
				 */
        		rd.include(request, response);
        		for(int i = 0 ; i<3; i++) {
        		out.println("    <div class=\"searchresult\">\n" + 
        				"            <h2>"+title+"</h2>\n" + 
        				"            <a>"+url+"</a> <button> </button>\n" + 
        				"            <p>"+snippets+"</p>\n" + 
        				"        </div>\n"  
        				);
        		}
        		RequestDispatcher rd2 = request.getRequestDispatcher("/WEB-INF/part2.html");
        		rd2.include(request, response);
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
 