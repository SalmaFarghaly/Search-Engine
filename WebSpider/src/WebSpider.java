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
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;



public class WebSpider{

    private final Set<URL> links;
    private final long startTime;

    //startURL is seed set
    private WebSpider(final ArrayList<URL> startURL) {
        this.links = new HashSet<>();
        this.startTime = System.currentTimeMillis();
        for(int i=0;i<startURL.size();i++){
        	System.out.println(startURL.get(i));
        		crawl(initURLS(startURL.get(i)));
        }
    }

    private static Set<URL> initURLS(final URL startURL) {
        final Set<URL> startURLS = new HashSet<>();
        startURLS.add(startURL);
        return startURLS;
    }

    private void crawl(final Set<URL> URLS) {
        URLS.removeAll(this.links);
        if (!URLS.isEmpty()) {
            final Set<URL> newURLS = new HashSet<>();
            try {
                this.links.addAll(URLS);
                for (final URL url : URLS) {
                    System.out.println("time = " + (System.currentTimeMillis() - this.startTime) +
                            " connect to : " + url);
                    final Document document = Jsoup.connect(url.toString()).get();
                    final Elements linksOnPage = document.select("a[href]");
                    for (final Element page : linksOnPage) {
                        final String urlText = page.attr("abs:href").trim();
                        final URL discoveredURL = new URL(urlText);
                        newURLS.add(discoveredURL);
                        ///System.out.print("GHASGSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                       DatabaseConnection.incrementInBound(discoveredURL.toString());
                        ///System.out.print("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                        //	writeResults(discoveredURL.toString());
                    }
                }
            } 
            catch (final Exception | Error ignored) {
            }
            crawl(newURLS);
        }
   }

    private void writeResults(String discoveredLink) throws IOException {
    	DatabaseConnection.insert(discoveredLink);
    }

    public static void main(String[] args) throws IOException {
    	BufferedReader reader;
    	ArrayList<URL> list = new ArrayList<URL>();
    	
    	
    	
    	try {
			reader = new BufferedReader(new FileReader("C:\\Users\\Dell\\Desktop\\Apt project\\URLS.txt"));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				list.add(new URL(line));
				line = reader.readLine();
			}
			reader.close();
		}
    	catch (IOException e) {
			e.printStackTrace();
		}
    	DatabaseConnection.DatabaseConnect();
    	final WebSpider crawler = new WebSpider(list);
    	
        //crawler.writeResults();
    }

}
