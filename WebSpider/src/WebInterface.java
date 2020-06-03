import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

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

public class WebInterface extends HttpServlet{
	
	private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
	private final GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        String SearchInput = request.getParameter("SearchInput");
        //save Search Input to Database
        
        try {
        	System.out.print(SearchInput+"\n");
			DatabaseConnection.saveSearchQuery(SearchInput);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	

    }
    

}