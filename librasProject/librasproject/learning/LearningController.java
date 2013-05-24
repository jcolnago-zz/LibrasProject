package learning;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import application.LibrasProject;

/**
 * This class is father class of the controllers related to learning (Lessons
 * and Review). 
 * It provides the basic functionalities used by both to display the components
 * necessary.
 * 
 * @author jcolnago
 */
public class LearningController extends AnchorPane {
    
    /* Class variables */
    public LibrasProject application;
    public String lessonName, currentComponent;          
    public ArrayList<LessonComponent> lessonComponents;
    public double total, progress;
    
    /* FXML id for the different ui components */
    @FXML
    Label ExtraInformation;
    @FXML
    ImageView Image1, Image2, Image3, Image4, Feed;
    @FXML
    Rectangle border;
    @FXML
    ProgressBar Progress;
    
    
    /**
     * Sets up the extra information to be shown for the next lesson component.
     * 
     * @param extraInformation a string containing the extra information
     */
    public void setExtraInformation(String extraInformation) {
        ExtraInformation.setText(extraInformation);
    }

    
    /**
     * Sets up the new images to be shown for the next lesson component.
     * 
     * @param fileLocation list of paths for the images to be shown
     */
    public void setImages(List<String> fileLocation) {
        
        Image1.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(fileLocation.get(0))));
        Image2.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(fileLocation.get(1))));
        Image3.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(fileLocation.get(2))));
        Image4.setImage(new Image(LibrasProject.class.getClassLoader().getResourceAsStream(fileLocation.get(3))));
    }

    
    /**
     * Sets up the new image obtained from the Kinect. Simulates a video.
     * 
     * @param bi contains the buffered image obtained from the Kinect.
     */
    public void setFeed(BufferedImage bi) {
        Feed.setImage((Image)SwingFXUtils.toFXImage(bi, null));
    }

    
    /**
     * Sets the stroke color for the border component.
     * 
     * @param color a {@link Color} that defines the new color the border should
     * adopt.
     */
    public void setBorderStrokeColor(Color color) {
        border.setStroke(color);
    }
    
    
    /**
     * Sets up the information necessary to show a lesson component based on a 
     * given index.
     * 
     * @param index random value between 0 and lessonComponents.size();
     * @return the name of the current component
     */
    public String setUpLessonComponents(int index) {
        String componentName = lessonComponents.get(index).getLessonComponentName(); 
        
        /* Setting up the information necessary for the component to be shown */
        setExtraInformation(componentName+lessonComponents.get(index).getExtraInfo());    
        setImages(lessonComponents.get(index).getImages());
        
        lessonComponents.remove(index);       
        
        return componentName;
    }
    
    
    /**
     * Gets the current date
     * 
     * @return strDate the current date.
     */
    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    
    
    /**
     * Displays the "Done" message box.
     */
    public void showMessageBox(){
        try {
            Stage stage = new Stage(); 
            AnchorPane page = (AnchorPane) FXMLLoader.load(getClass().getResource("MessageBox.fxml"));
            stage.setScene(new Scene(page));
            stage.setTitle("Learning Libras");
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LessonsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
