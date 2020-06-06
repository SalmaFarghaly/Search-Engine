import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@WebServlet("/ViewServlet")  
public class ViewServlet extends HttpServlet {  
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException   
	          {  
		String SearchInput = request.getParameter("SearchInput");
		long start = 0;
	        String spageid=request.getParameter("page");  
	        int pageid=Integer.parseInt(spageid);  
	        System.out.print("\n"+"PAGEEEEEEEEEEEEEEID"+pageid+"\n");

        	// ------------------------------------------- adding first part of template
			// with style sheet -------------------------------
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			WebInterface.AddingFirstHTMLPart(out,SearchInput);
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/part1.html");
			rd.include(request, response);
	    
			//result of query search 
	        ArrayList<String> output=new ArrayList<String>();
	        int PagesNum = 0;
	        try {
				output=QueryProcessor.queryProcessor(SearchInput, pageid);
				System.out.print("\nOutput"+output+"\n");
				List<String> parsedQuery = QueryProcessor.ParsedQuery(SearchInput);
				PagesNum = DatabaseConnection.getQueryLengthResult(parsedQuery);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	        WebInterface.AddingResults( out, output, SearchInput);			
	        PagesNum = (int) Math.ceil((float) PagesNum/10);
			PagesNum++;	
			System.out.println("Done Adding document to html file");
			WebInterface.AddingPagebar( out, PagesNum, SearchInput);
			
			RequestDispatcher rd2 = request.getRequestDispatcher("/WEB-INF/part2.html");
			rd2.include(request, response);
			out.flush();
			out.close();
			 long end = System.currentTimeMillis();
		      //finding the time difference and converting it into seconds
		      float sec = (end - start) / 1000F; System.out.println("Time of WebInterface: "+ sec + " seconds");
			}
}
