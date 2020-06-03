import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

//import marytts.TextToSpeech;
import marytts.signalproc.effects.JetPilotEffect;
import net.sourceforge.javaflacencoder.FLACFileWriter;

public class VoiceRecognition extends HttpServlet{
	
	private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
	private final GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        String SearchInput = request.getParameter("SearchInput");

        String button=request.getParameter("button");
        if(button.equals("Search By Voice")) {
        	duplex.setLanguage("en");
    	
    		startSpeechRecognition();
    		
    		duplex.addResponseListener(new GSpeechResponseListener() {
    			String old_text = "";
    			
    			public void onResponse(GoogleResponse googleResponse) {
    				
    				String output = "";
    				//Get the response from Google Cloud
    				
    				output = googleResponse.getResponse();
    				if (!googleResponse.getOtherPossibleResponses().isEmpty()) {
    					System.out.print("3333333333333333333333333\n");
    					output = old_text + " (" + (String) googleResponse.getOtherPossibleResponses().get(0) + ")";
    					old_text=output;
    				}
    				else {
    					mic.close();
    					duplex.stopSpeechRecognition();
    				}
//    				 response.setContentType("text/html");
    				
//    				 String page = "<!doctype html> <html> <body> <h1>" + output +" </h1> </body></html>";

    				
    				System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF\n"+output);
    				
    			}
    			
    		});
        	
        }
       
    }
    public void startSpeechRecognition() {
		//Start a new Thread so our application don't lags
		new Thread(() -> {
			try {
				duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
			} catch (LineUnavailableException | InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

}