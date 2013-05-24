package learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Component class.
 * This class defines the structure for the lessons to be used in the system.
 * It includes its attributes and the methods to get them.
 * 
 * @author Jessica H. Colnago
 */
public class LessonComponent {
    
    /* Class variables */
    private List<String> images;    // Images path
    private String video;           // Video path
    private String componentName;   // Component name
    private String extraInfo;       // Extra information
    
    
    /** 
     * Class constructor
     * 
     * @params a list of image paths, avatar path, name of the lesson and extra 
     * information to be shown 
    */
    LessonComponent (ArrayList<String> images, String video, String lessonName, String extraInfo) {
        this.images = images;
        this.video = video;
        this.componentName = lessonName;
        /* If there is no extra information set it as an empty string */
        if (extraInfo == null || extraInfo.isEmpty()) {
            this.extraInfo = "";
        }
        else {
            this.extraInfo = extraInfo;
        }       
    }
    
    
    /** 
     * Class constructor without the avatar (used for review components)
     * 
     * @params a list of image paths, avatar path, name of the lesson and extra 
     * information to be shown 
    */
    LessonComponent (ArrayList<String> images, String lessonName, String extraInfo) {
        this.images = images;
        this.componentName = lessonName;
        /* If there is no extra information set it as an empty string */
        if (extraInfo == null || extraInfo.isEmpty()) {
            this.extraInfo = "";
        }
        else {
            this.extraInfo = extraInfo;
        }       
    }

    
    /**
     * Getter method for the list of images path
     * 
     * @return the images
     */
    public List<String> getImages() {
        return images;
    }

    
    /**
     * Getter method for the video path
     * 
     * @return the video
     */
    public String getVideo() {
        return video;
    }

    
    /**
     * Getter method for the lesson name
     * 
     * @return the lessonName
     */
    public String getLessonComponentName() {
        return componentName;
    }

    
    /**
     * Getter method for the extra information available
     * 
     * @return the extraInfo
     */
    public String getExtraInfo() {
        return extraInfo;
    }
    
}
