import java.io.*;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

public class WebInterface extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        String SearchInput = request.getParameter("SearchInput");

       
        response.setContentType("text/html");
        List<String> inputString = QueryProcessor.ParsedQuery(SearchInput);
        String message = "Your Search Query is " + SearchInput ;

        String page = "<!doctype html> <html> <body> <h1>" + inputString +" </h1> </body></html>";
        response.getWriter().println(page);
    }

}