package learning;

import kinect.ViewerPanel;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import application.LibrasProject;

/**
 * This class is responsible for the control of the review scene.
 * 
 * @author Jessica H. Colnago
 */
public class ReviewController extends LearningController implements Initializable{

    /* Class variables */
    private ReviewThread behavior;
    private ViewerPanel vp;
    public final Object ready = new Object();
        
    
    /**
     * Performs the proper action for when the back button receives an action.
     * 
     * @param event the action event information
     */
    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        behavior.cancel();
        vp.closeDown();
        application.gotoSelection();    // returns to the selection scene
    }
    
    
    /**
     * Performs the proper action for when the ready button receives an action.
     * 
     * @param event the action event information
     */
    @FXML
    private void handleReadyButtonAction(ActionEvent event) {   
        /* Notifies the thread that the user is ready */
        synchronized(ready) {
         ready.notify();  
       }
    }

    
    /**
     * Sets up the application and any other work that needs to be done on start
     * up.
     * 
     * @param application responsible for the scene that needs to be loaded
     */
    public void setApp(LibrasProject application, String lessonName, boolean review) throws SQLException {
        this.application = application;
        this.lessonName = lessonName;
        progress = 0;
        
        /* Get the information from these components*/
        if (review) {
           lessonComponents = getReviewInformation(); 
        }
        else {
           lessonComponents = getLessonInformation();
        }
        
        total = lessonComponents.size();
        
        showNextElement();
        
        vp = new ViewerPanel(this);
        Thread th1 = new Thread(vp);
        th1.setDaemon(true);
        th1.start();   // start updating the panel's image
       
        behavior = new ReviewThread(this, review); 
        Thread th = new Thread(behavior);
        th.setDaemon(true);
        th.start();
    }

    
    /**
     * This function sets the value for the new element to be shown and if all
     * elements have already been shown, display the "done" message.
     */
    public void showNextElement() {
        int index;
        Random r = new Random();
        
        Progress.setProgress(progress);
        
        if (!lessonComponents.isEmpty()) {
           /* Defines new random lesson component */
            index = r.nextInt(lessonComponents.size());
            /* Sets up the first lesson components to be shown */
            currentComponent = setUpLessonComponents(index); 
        }
        else {
            showMessageBox("Parabéns!", "Não há mais nada para ser feito!");
        }    
    }
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}