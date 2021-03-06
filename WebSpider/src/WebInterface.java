
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

//import marytts.TextToSpeech;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class WebInterface extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String SearchInput = request.getParameter("SearchInput");
		System.out.print("SEARCH Input : -----------"+SearchInput+"\n");
		double time = 0;
		long start = 0;
		if (SearchInput != null && SearchInput != "") // if action is not null
		{
			int PagesNum = 0;
		
			 start = System.currentTimeMillis();

				// ------------------------------------------- adding first part of template
				// with style sheet -------------------------------
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				AddingFirstHTMLPart(out,SearchInput);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/part1.html");
				rd.include(request, response);

				// ------------------------------ Getting Search Results
				// --------------------------
				ArrayList<String> output = new ArrayList();
				try {
					System.out.print("SEARCHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"+SearchInput+"\n");
					output = QueryProcessor.queryProcessor(SearchInput,1);
					List<String> parsedQuery = QueryProcessor.ParsedQuery(SearchInput);
					PagesNum = DatabaseConnection.getQueryLengthResult(parsedQuery);
//					System.out.print("WEBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB  \n");
					System.out.print(output);
				} catch (IOException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				PagesNum = (int) Math.ceil((float) PagesNum/10);
				AddingResults( out, output, SearchInput);
				PagesNum++;
				 AddingPagebar( out, PagesNum, SearchInput);
				
						RequestDispatcher rd2 = request.getRequestDispatcher("/WEB-INF/part2.html");
						rd2.include(request, response);
						out.flush();
						out.close();
						
				}
				
				System.out.println("Done");
			

			try {
				System.out.print(SearchInput + "\n");
				DatabaseConnection.saveSearchQuery(SearchInput);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 long end = System.currentTimeMillis();
		      //finding the time difference and converting it into seconds
		      float sec = (end - start) / 1000F; System.out.println("Time of WebInterface: "+ sec + " seconds");


		}

	

	public static void AddingFirstHTMLPart(PrintWriter out,String SearchInput) {
		// to edit the title
	    if( SearchInput.startsWith("\"") & SearchInput.endsWith("\"")) {
	    	  SearchInput =SearchInput.replaceAll("^\"+|\"+$", "");
	    	 //SearchInput = "\""+QueryWithoutQuotes+"\"";
	    }
		out.println("<!doctype html>\n" + "<html>\n" + "\n" + "<head>\n" + "    <title>" + SearchInput
				+ " - Bing Search</title>\n"
				+ "    <link rel=\"shortcut icon\" type=\"image/ico\" href=\"images/favicon.ico\" />\n"
				+ "	 <link rel=\"stylesheet\" href=\"css/indexcss3.css\">\n" + "</head>");
		// to edit search bar
		out.println("\n" + "<body>\n" + "    <div id=\"header\">\n" + "        <div id=\"topbar\">\n"
				+ "             <img id=\"searchbarimage\" src=\"img/bing.png\" />\n"
				+ "            <div id=\"searchbar\" type=\"text\">\n"
				+ "                <input id=\"searchbartext\" type=\"text\" value=\"" + SearchInput + " \"/>");

	}
	public static void AddingPagebar(PrintWriter out,int PagesNum,String SearchInput) {
		  if( SearchInput.startsWith("\"") & SearchInput.endsWith("\"")) {
	    	  SearchInput =SearchInput.replaceAll("^\"+|\"+$", "");
	    	 //SearchInput = "\""+QueryWithoutQuotes+"\"";
	    }
		System.out.println("Done Adding document to html file");
		out.println("<body>     \n" + 
				"		<br>\n" + 
				"		\n" + 
				"        <div class=\"pagebar\">\n" + 
				"            <ul class=\"pagelist\">\n" );
		
		for(int i=1; i<PagesNum; i++) {
		out.println(
				"                <li class=\"pagelistfirst\"><a href=\"ViewServlet?page="+i+"&SearchInput="+SearchInput+"\">"+i+"</a></li>\n" 
			);
		}
		out.println(
				"            </ul>\n" + 
				"        </div>\n" + 
				"    </div>\n" + 
				"");
	}
	
	public static void AddingResults(PrintWriter out,List<String> output,String SearchInput) {
		int PageList = 0;
		   if( SearchInput.startsWith("\"") & SearchInput.endsWith("\"")) {
		    	  SearchInput =SearchInput.replaceAll("^\"+|\"+$", "");
		    	 //SearchInput = "\""+QueryWithoutQuotes+"\"";
		    }
		String names[] = SearchInput.split(" ");
		String CapitalizedInput[] = new String[names.length];
		for(int i=0;i<names.length; i++)
			CapitalizedInput[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1);
		String namesLowered[]= new String[names.length] ; 
    	for(int i =0;i<names.length;i++) {
    		namesLowered[i] = names[i].toLowerCase(); 
    	}
		for (int p=0;p<output.size();p++){
			if(PageList == 10)
				break;
//			System.out.println("Key final= " + entry.getKey() + ", Value final= " + entry.getValue());
			// ----------------------------------------- getting url content
			// -----------------------------
			String url = output.get(p);
			System.out.println("Number of search results: "+output.size());
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
			List<String> exist = new ArrayList<String>();
			int count = 0;
			for (int i = 0; i < tagsinfo.size(); i++) {
				if (Stream.of(names).anyMatch(tagsinfo.get(i).toLowerCase()::contains)||Stream.of(namesLowered).anyMatch(tagsinfo.get(i).toLowerCase()::contains)) {
					exist.add(tagsinfo.get(i));
					count++;
				}
				if (count > 2)
					break;
			}
			// ------------------------------------------ end here
			// ---------------------------------------

			String snippets = null;
			for (int j = 0; j < count; j++)
				snippets = exist.get(j);
			String replaceString = null;
			//Making the search query input bold in snipets
		if (snippets != null) {
				for (int i = 0; i < names.length; i++) {
					String boldText = "<b>" + names[i] + "</b>";
					if(replaceString!= null) {
						replaceString = replaceString.replaceAll(names[i], boldText);
						boldText = "<b>" + namesLowered[i] + "</b>";
						replaceString = replaceString.replaceAll(namesLowered[i], boldText);
						boldText = "<b>" + CapitalizedInput[i] + "</b>";
						replaceString = replaceString.replaceAll(CapitalizedInput[i], boldText);
					}else {
					replaceString =  snippets.replaceAll(names[i], boldText);
					boldText = "<b>" + namesLowered[i] + "</b>";
					replaceString = replaceString.replaceAll(namesLowered[i], boldText);
					boldText = "<b>" + CapitalizedInput[i] + "</b>";
					replaceString = replaceString.replaceAll(CapitalizedInput[i], boldText);
					}
				}
			}
			
			if (replaceString != null && PageList <10) {
				System.out.println("Adding document to html file");
				out.println("    <div class=\"searchresult\">\n" + "            <h2><a href=\"" + url + "\">"
						+ title + "</a></h2>\n" + "            <h3>" + url + "</h3> \n" + "            <p>"
						+ replaceString + "</p>\n" + "        </div>\n");
			}
			if(replaceString==null && PageList<10)	{
				System.out.println("Adding document to html file WITHOUT SNIPPET");
				out.println("    <div class=\"searchresult\">\n" + "            <h2><a href=\"" + url + "\">"
						+ title + "</a></h2>\n" + "            <h3>" + url + "</h3> \n" + "            <p>"
						+ "" + "</p>\n" + "        </div>\n");
			}
			PageList++;
			}

	}
}
