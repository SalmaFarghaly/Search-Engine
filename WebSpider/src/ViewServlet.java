import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewServlet")  
public class ViewServlet extends HttpServlet {  
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException   
	          {  
		String SearchInput = request.getParameter("SearchInput");
	        response.setContentType("text/html");  
	        PrintWriter out=response.getWriter();  
	          
	        String spageid=request.getParameter("page");  
	        int pageid=Integer.parseInt(spageid);  
	        int total=5;  
	          out.print("<h1>Page No: "+pageid+"& Search Input"+SearchInput+"</h1>");  
	        out.print("<table border='1' cellpadding='4' width='60%'>");  
	        out.print("<tr><th>Id</th><th>Name</th><th>Salary</th>");  
	       
	          
	        out.print("<a href='ViewServlet?page=1'>1</a> ");  
	        out.print("<a href='ViewServlet?page=2'>2</a> ");  
	        out.print("<a href='ViewServlet?page=3'>3</a> ");  
	          
	        out.close();  
	    }  
	
}
