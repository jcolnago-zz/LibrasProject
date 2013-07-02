package learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Worker thread that implements the behavior of a lesson (learn part) 
 * and its components
 * 
 * @author Jessica H. Colnago
 */
public class LearnThread extends Task<Void> {

    /* Class variables */
    private LearnController lc;
    public boolean waitingPlay = true;
    
    
    /**
     * Class constructor
     * 
     * @param lc the lessonController that starts the thread and the lesson it
     * should run.
     */
    LearnThread(LearnController lc) {
        this.lc = lc;
    }
    

    /**
     * Implements the recognition part.
     */
    @Override
    protected Void call() throws Exception {     
        boolean result;
        String recognized;
        int iterations = lc.lessonComponents.size();
        
        Socket clientSocket = new Socket(lc.application.prop.getProperty("server"),
                Integer.parseInt(lc.application.prop.getProperty("port")));
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
               
        while (iterations >= 0) {
        
            /* Waits for the video to be played */
            lc.setActiveCircle(Color.RED);
            synchronized(lc.waitVideo) {
                lc.waitVideo.wait(); 
            }
            
            Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      lc.setRecognized("");
                    }
                });
            lc.setActiveCircle(Color.YELLOW);

            try {
                Thread.sleep((long)lc.getVideoDuration().toMillis());
            } catch (InterruptedException ex) {
                Logger.getLogger(LearnThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            /* Indicate the user can start */
            lc.setActiveCircle(Color.GREEN);
            out.println("START");
            Thread.sleep(3000);  // In order to get the predominant value recognized
            out.println("STOP");

            recognized = in.readLine();
            result = recognized.equalsIgnoreCase(lc.currentComponent);
            if (result) {
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      lc.progress+=1/lc.total;
                      lc.showNextElement();
                      lc.setActiveCircle(Color.RED);
                    }
                });

                iterations--;
            }
            else {
                final String temp = recognized;
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      lc.setRecognized(temp);
                    }
                });            
            }
        }
                
        in.close();
        out.close();
        return null;
    }
        
}

