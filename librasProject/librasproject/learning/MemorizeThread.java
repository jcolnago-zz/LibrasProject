package learning;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Worker thread that implements the behavior of a lesson (memorize part)
 * and its components
 * 
 * @author Jessica H. Colnago
 */
public class MemorizeThread extends Task<Void> {

    /* Class variables */
    private LearnController lc;
    public boolean waitingPlay = true;
    
    
    /**
     * Class constructor
     * 
     * @param lc the lessonController that starts the thread and the lesson it
     * should run.
     */
    MemorizeThread(LearnController lc) {
        this.lc = lc;
    }
    

    /**
     * Implements the recognition part.
     */
    @Override
    protected Void call() throws Exception {     
        int iterations = lc.lessonComponents.size();
        
        while (iterations >= 0) {
            /* Waits for the video to be played */
            lc.setActiveCircle(Color.RED);
            synchronized(lc.waitVideo) {
                lc.waitVideo.wait(); 
            }
            
            lc.setActiveCircle(Color.YELLOW);
            
            try {
                Thread.sleep((long)lc.getVideoDuration().toMillis());
            } catch (InterruptedException ex) {
                Logger.getLogger(LearnThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            lc.setActiveCircle(Color.GREEN);
            
            /* Waits for the user to select an image */
            synchronized(lc.waitVerification) {
                lc.waitVerification.wait(); 
            }

            if (lc.isCorrect) {   
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      lc.progress+=1/lc.total;
                      lc.showNextElementMemorize();
                      lc.setActiveCircle(Color.RED);
                    }
                });

                iterations--;
            }
            else {
                lc.setActiveCircle(Color.RED);
                Thread.sleep(1000);
            }
        }
        
        return null;
    }
        
}

