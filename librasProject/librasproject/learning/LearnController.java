package learning;

import kinect.ViewerPanel;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import application.LibrasProject;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for the control of the Lessons scene.
 * 
 * @author Jessica H. Colnago
 */
public class LessonsController extends LearningController implements Initializable {
    
    /* Class variables */
    private RecognitionThread recognition;
    public final Object waitVideo = new Object();
    private ViewerPanel vp;
    
    /* FXML id for the different ui components */
    @FXML
    Pane Video;
    @FXML
    Button Play;
   
    
    /**
     * Performs the proper action for when the back button receives an action.
     * 
     * @param event the action event information
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        recognition.cancel();
        vp.closeDown();
        application.gotoSelection();    // returns to the selection scene
    }
    
    
    /**
     * Performs the proper action for when the back button receives an action.
     * 
     * @param event the action event information
     */
    @FXML
    private void handlePlayButtonAction(ActionEvent event) {
        stopVideo();    
        playVideo();
    }
    
    
    /**
     * Sets up the application and any other work that needs to be done on start
     * up.
     * 
     * @param application responsible for the scene
     * @param lessonName lesson's name that needs to be loaded
     */
    public void setApp(LibrasProject application, String lessonName) throws SQLException {
        this.application = application;
        this.lessonName = lessonName;
        progress = 0;
        
        /* Get the information from these components*/
        lessonComponents = getLessonInformation();
        total = lessonComponents.size();
        
        showNextElement();
               
        vp = new ViewerPanel(this);
        Thread th1 = new Thread(vp);
        th1.setDaemon(true);
        th1.start();   // start updating the panel's image
        
        recognition = new RecognitionThread(this);
        Thread th = new Thread(recognition);
        th.setDaemon(true);
        th.start();        
    }
      

    /**
     * Sets up the path for the video to be used for the next lesson component.
     * 
     * @param fileLocation a string containing the video path
     */
    public void setVideo(String fileLocation) {
        if (!Video.getChildren().isEmpty()) {
            stopVideo();
            Video.getChildren().remove(0);
        }

        String video;
        try {
            video = (LibrasProject.class.getClassLoader().getResource(fileLocation).toURI().toString());
            Media media = new Media(video);

            // Create the player and set to play automatically.
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            //mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(1);

             // Create the view and add it to the Scene.
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitHeight(274.0);
            mediaView.setFitWidth(300.0);
            mediaView.setPreserveRatio(false);
            Video.getChildren().add(mediaView);
        } catch (URISyntaxException ex) {
            Logger.getLogger(LessonsController.class.getName()).log(Level.SEVERE, null, ex);
        }
   
    }
    
    
    /**
     * Plays the current video.
     */
    public void playVideo() {
       ((MediaView)Video.getChildren().get(0)).getMediaPlayer().play();
       synchronized(waitVideo) {
         waitVideo.notify();  
       }  
    }
    
    
    /**
     * Stops the current video displaying.
     */
    public void stopVideo() {
       if (((MediaView)Video.getChildren().get(0)).getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
           ((MediaView)Video.getChildren().get(0)).getMediaPlayer().stop();
       }
    }
    
    
    /**
     * Gets the duration in milliseconds of the current video
     */
    public Duration getVideoDuration() {
       return ((MediaView)Video.getChildren().get(0)).getMediaPlayer().getMedia().getDuration();
    }
    
    
    /**
     * This function creates a list of lesson components obtaining the its values
     * from the database based on the lessonName.
     * 
     * @param components a list of lesson components
     * @return an array list of lessonComponents
     */
    private ArrayList<LessonComponent> getLessonInformation() throws SQLException {
        Statement statement = application.getConnection().createStatement();
        ResultSet rs;
        
        /* Creates a temporary list */
        ArrayList<LessonComponent> lessonList = new ArrayList<>();
        
        rs = statement.executeQuery("SELECT component_id, "
                + "video_path, extra_information FROM component "
                + "WHERE lesson_id='" + lessonName + "'");
        
        /* Iterate over every component */
        while(rs.next()) {
            Statement statementImages = application.getConnection().createStatement();
            ResultSet rsImages;
            
            ArrayList<String> imageList = new ArrayList<>();
            String videoPath = rs.getString("video_path");
            String componentName = rs.getString("component_id");
            String extraInfo = rs.getString("extra_information");
            LessonComponent lessonElement;
                        
            rsImages = statementImages.executeQuery("SELECT image_path "
                    + " FROM image AS i, component_image AS ci WHERE i.image_id="
                    + "ci.image_id AND component_id='" + componentName + "'");
            while(rsImages.next()) {
                imageList.add(rsImages.getString("image_path"));
            }
            
            /* Creates a lesson object */
            lessonElement = new LessonComponent(imageList, videoPath, componentName, extraInfo);
            /* Adds it to the lesson list to be returned */
            lessonList.add(lessonElement);      
        }
        return lessonList;       
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
            /* In case of the lessonsController set up the video */
            setVideo(lessonComponents.get(index).getVideo());
            /* Sets up the first lesson components to be shown */
            currentComponent = setUpLessonComponents(index);         
        }
        else {
            showMessageBox();
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
}
