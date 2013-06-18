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
import javafx.stage.Stage;
import application.LibrasProject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javafx.scene.shape.Circle;

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
    Label ExtraInformation, Recognized;
    @FXML
    ImageView Image1, Image2, Image3, Image4, Image5, Feed;
    @FXML
    Circle RedCircle, GreenCircle, YellowCircle;
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
    public void setActiveCircle(Color color) {
        if (color == Color.GREEN) {
            GreenCircle.setOpacity(1.0);
            YellowCircle.setOpacity(0.3);
            RedCircle.setOpacity(0.3);
        }
        else if (color == Color.YELLOW) {
            YellowCircle.setOpacity(1.0);
            GreenCircle.setOpacity(0.3);
            RedCircle.setOpacity(0.3);
        }
        else {
            RedCircle.setOpacity(1.0);
            YellowCircle.setOpacity(0.3);
            GreenCircle.setOpacity(0.3);
        }
        
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
     * Displays a message box.
     */
    public void showMessageBox(String title, String message){
        try {
            Stage stage = new Stage(); 
            AnchorPane page = (AnchorPane) FXMLLoader.load(getClass().getResource("MessageBox.fxml"));
            for (int i=0; i < page.getChildren().size(); i++) { 
                if (page.getChildren().get(i).getClass() == Label.class) {
                    if ("text".equals(((Label)page.getChildren().get(i)).getId())) {
                        ((Label)page.getChildren().get(i)).setText(message);
                    }
                    else {
                       ((Label)page.getChildren().get(i)).setText(title); 
                    }
                        
                }
            }
            stage.setScene(new Scene(page));
            stage.setTitle("Learning Libras");
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LearnController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    
    /**
     * This function creates a list of lesson components obtaining the its values
     * from the database based on the lessonName.
     * 
     * @param components a list of lesson components
     * @return an array list of lessonComponents
     */
    protected ArrayList<LessonComponent> getLessonInformation() throws SQLException {
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
     * This function creates a list of review components obtaining the values
     * from the database.
     * @param components a list of review components
     * @return an array list of reviews
     */
    protected ArrayList<LessonComponent> getReviewInformation() throws SQLException {
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
}
