package learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Worker thread that implements the behavior of a lesson and its components
 * 
 * @author Jessica H. Colnago
 */
public class RecognitionThread extends Task<Void> {

    /* Class variables */
    private LessonsController lc;
    public boolean waitingPlay = true;
    
    
    /**
     * Class constructor
     * 
     * @param lc the lessonController that starts the thread and the lesson it
     * should run.
     */
    RecognitionThread(LessonsController lc) {
        this.lc = lc;
    }
    

    /**
     * Implements the recognition part.
     */
    @Override
    protected Void call() throws Exception {     
        boolean result;
        PreparedStatement reviewStatement, completeStatement;      
        int mistakes = 0;
        int iterations = lc.lessonComponents.size();
        
        Socket clientSocket = new Socket(lc.application.prop.getProperty("server"),
                Integer.parseInt(lc.application.prop.getProperty("port")));
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        reviewStatement = lc.application.getConnection().prepareStatement(
                  "UPDATE review_component SET mistakes=?, last_reviewed='" 
                + lc.getCurrentTimeStamp() + "' WHERE component_id=? AND user_id='"
                + lc.application.getUserName() + "';");
        
        completeStatement = lc.application.getConnection().prepareStatement(
                "UPDATE user_lesson SET complete='true' "
                + "WHERE lesson_id='" + lc.lessonName + "' AND user_id='" 
                + lc.application.getUserName() + "'");
        
        while (iterations >= 0) {
            /* Waits for the video to be played */
            synchronized(lc.waitVideo) {
                lc.waitVideo.wait(); 
            }

            try {
                Thread.sleep((long)lc.getVideoDuration().toMillis());
            } catch (InterruptedException ex) {
                Logger.getLogger(RecognitionThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            /* Indicate the user can start */
            lc.setBorderStrokeColor(Color.GREEN);
            out.println("START");
            Thread.sleep(3000);  // In order to get the predominant value recognized
            out.println("STOP");

            result = in.readLine().equalsIgnoreCase(lc.currentComponent);

            if (result) {
                /* Update the review_component with the amout of mistakes made */
                reviewStatement.setInt(1, mistakes);
                reviewStatement.setString(2, lc.currentComponent);
                reviewStatement.executeUpdate();
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      lc.progress+=1/lc.total;
                      lc.showNextElement();
                      lc.setBorderStrokeColor(Color.BLACK);
                    }
                });

                mistakes = 0;
                iterations--;
            }
            else {
                lc.setBorderStrokeColor(Color.RED);
                Thread.sleep(1000);
                mistakes++;
                lc.setBorderStrokeColor(Color.BLACK);
            }
        }
        
        completeStatement.executeUpdate();
        
        in.close();
        out.close();
        return null;
    }
        
}

