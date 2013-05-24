package learning;

import kinect.ViewerPanel;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ReviewBehaviorThread behavior;
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
    public void setApp(LibrasProject application, String lessonName) throws SQLException {
        this.application = application;
        this.lessonName = lessonName;
        progress = 0;
        
        /* Get the information from these components*/
        lessonComponents = getReviewInformation();
        total = lessonComponents.size();
        
        showNextElement();
        
        vp = new ViewerPanel(this);
        Thread th1 = new Thread(vp);
        th1.setDaemon(true);
        th1.start();   // start updating the panel's image
       
        behavior = new ReviewBehaviorThread(this); 
        Thread th = new Thread(behavior);
        th.setDaemon(true);
        th.start();
    }
    
    
    /**
     * This function creates a list of review components obtaining the values
     * from the database.
     * @param components a list of review components
     * @return an array list of reviews
     */
    public ArrayList<LessonComponent> getReviewInformation() throws SQLException {
        Statement statement = application.getConnection().createStatement();
        ResultSet rs;
        
        /* Creates the temporary list */
        ArrayList<LessonComponent> reviewList = new ArrayList<>();
        
            rs = statement.executeQuery("SELECT c.component_id, mistakes, last_reviewed "
                + "FROM review_component AS rc, component AS c "
                + "WHERE user_id='" + application.getUserName() + "'"
                + "AND lesson_id='" + lessonName + "' "
                + "AND rc.component_id=c.component_id"); 
        
        long daysDiff;
        /* Iterate over every component */
        while(rs.next()) {
            
            daysDiff = (Calendar.getInstance().getTimeInMillis()-rs.getDate("last_reviewed").getTime())/(1000*60*60*24);
            if (rs.getInt("mistakes")*2 + daysDiff  > 6) {
            
                Statement statementImages = application.getConnection().createStatement();
                Statement statementInfo = application.getConnection().createStatement();
                ResultSet rsImages;
                ResultSet rsInfo;
                ArrayList<String> imageList = new ArrayList<>();
                String componentName = rs.getString("component_id");
                
                rsInfo = statementInfo.executeQuery("SELECT extra_information FROM component "
                + "WHERE component_id='" + componentName + "'");
                
                /* There is only going to be one row. No need for while */
                rsInfo.next();
                String extraInfo = rsInfo.getString("extra_information");
                LessonComponent reviewElement;

                rsImages = statementImages.executeQuery("SELECT image_path "
                        + " FROM image AS i, component_image AS ci WHERE i.image_id="
                        + "ci.image_id AND ci.component_id='" + componentName + "'");
                while(rsImages.next()) {
                    imageList.add(rsImages.getString("image_path"));
                }

                /* Creates a review object */
                reviewElement = new LessonComponent(imageList, componentName, extraInfo);
                /* Adds it to the review list to be returned */
                reviewList.add(reviewElement);   
            }
        }
        return reviewList;       
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
            showMessageBox();
        }    
    }
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}