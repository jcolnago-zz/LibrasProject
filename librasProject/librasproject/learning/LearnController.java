package learning;

import kinect.ViewerPanel;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * This class is responsible for the control of the Lessons scene.
 * 
 * @author Jessica H. Colnago
 */
public class LearnController extends LearningController implements Initializable {
    
    /* Class variables */
    private LearnThread learn;
    private MemorizeThread memorize;
    public final Object waitVideo = new Object();
    public final Object waitVerification = new Object();
    public boolean isCorrect = false;
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
        learn.cancel();
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
     * Checks if the element clicked is the correct one.
     * 
     * @param event the action event information
     */
    @FXML
    private void checkIfCorrect(MouseEvent event) {
        ImageView image = (ImageView)event.getSource(); 
        isCorrect = image.getId().equals(currentComponent);
        synchronized(waitVerification) {
            waitVerification.notify();  
       } 
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
        
        learn = new LearnThread(this);
        Thread th = new Thread(learn);
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
            Logger.getLogger(LearnController.class.getName()).log(Level.SEVERE, null, ex);
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
            /* Part one has finished */
            application.gotoMemorize(lessonName);
        }
    }
    
    
    /**
     * This function sets the value for the new element to be shown and if all
     * elements have already been shown, display the "done" message.
     */
    public void showNextElementMemorize() {
        int index;
        Random r = new Random();
        
        Progress.setProgress(progress);
        
        if (!lessonComponents.isEmpty()) {
           /* Defines new random lesson component */
            index = r.nextInt(lessonComponents.size());
            /* In case of the lessonsController set up the video */
            setVideo(lessonComponents.get(index).getVideo());
            currentComponent = lessonComponents.get(index).getLessonComponentName();
            lessonComponents.remove(index); 
        }
        else {
            /* Part one has finished */
            application.gotoReview(lessonName, false);
        }
    }
    
        
    /**
     * Sets up the memorize part of the lesson.
     * 
     * @param application responsible for the scene
     * @param lessonName lesson's name that needs to be loaded
     */
    public void setMemorize(LibrasProject application, String lessonName) throws SQLException {       
        this.application = application;
        this.lessonName = lessonName;
        progress = 0;
        
        /* Get the information from these components*/
        lessonComponents = getLessonInformation();
        total = lessonComponents.size();      
                
        Image1.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(lessonComponents.get(0).getImages().get(0))));
        Image1.setId(lessonComponents.get(0).getLessonComponentName());
        Image2.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(lessonComponents.get(1).getImages().get(0))));
        Image2.setId(lessonComponents.get(1).getLessonComponentName());
        Image3.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(lessonComponents.get(2).getImages().get(0))));
        Image3.setId(lessonComponents.get(2).getLessonComponentName());
        Image4.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(lessonComponents.get(3).getImages().get(0))));
        Image4.setId(lessonComponents.get(3).getLessonComponentName());
        Image5.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(lessonComponents.get(4).getImages().get(0))));
        Image5.setId(lessonComponents.get(4).getLessonComponentName());
        
        showNextElementMemorize();
               
        memorize = new MemorizeThread(this);
        Thread th = new Thread(memorize);
        th.setDaemon(true);
        th.start();
        
    }
      
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
}
