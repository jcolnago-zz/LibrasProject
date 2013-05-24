package lesson2DB;

import application.LibrasProject;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Lesson2DB class.
 * This class is responsible for adding the information found in the resources
 * file to the database. 
 * 
 * @author Jessica H. Colnago
 */
public class Lesson2DB {
   
    private LibrasProject application; 
  
    
    /**
     * Adds the lessons to the database as well as the components
     * 
     * @param list a list of the lessons
     */
    public void add2BD(NodeList list) throws SQLException {
        Statement statement = application.getConnection().createStatement(); // Variable for the SQL statement
        NodeList components;
        
        for (int i = 0; i < list.getLength(); i++) {
            Element lesson = (Element)list.item(i);  
            
            /* Check if row already exists before inserting 
               Used because postgresql doesn't offer INSERT OR UPDATE */
            if (statement.executeUpdate("UPDATE lesson SET lesson_id='"
                    + lesson.getAttribute("name") + "' WHERE lesson_id='" 
                    + lesson.getAttribute("name") + "'") == 0 ) {
                statement.executeUpdate("INSERT INTO lesson(lesson_id) VALUES ('"
                        + lesson.getAttribute("name") + "')");
            }
            
            components = lesson.getElementsByTagName("LessonComponent");
            addComponents2DB(components, lesson.getAttribute("name"));
	} 
    }
    
    
    /**
     * Given a list of components and the lesson that owns them, this function 
     * adds to the database the necessary values 
     * 
     * @param components a list of lesson components
     * @param lessonId the lesson name
     */
    private void addComponents2DB(NodeList components, String lessonId) throws SQLException {   
        Statement statement = application.getConnection().createStatement(); // Variable for the SQL statement
        Statement statementImage = application.getConnection().createStatement(); // Variable for the SQL statement
        Statement statementCI = application.getConnection().createStatement(); // Variable for the SQL statement
        
        /* Iterate over every component in the node list */
        /* Obtains the image paths, avatar path and informations from the 
           components of the lesson */
        for (int i=0; i<components.getLength(); i++) {
            Node node = components.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String imageId;
                String imagePath;
                String videoPath = 
                        element.getElementsByTagName("Video").item(0).getTextContent();
                String componentId = element.getAttribute("name");
                String extraInfo = 
                        element.getElementsByTagName("ExtraInformation").item(0).getTextContent();
           
                /* Check if row already exists before inserting 
                   Used because postgresql doesn't offer INSERT OR UPDATE */
                if (statement.executeUpdate("UPDATE component SET "
                        + "component_id='" + componentId + "', "
                        + "video_path='" + videoPath + "', "
                        + "extra_information='" + extraInfo + "', "
                        + "lesson_id='" + lessonId + "' WHERE component_id='" 
                        + componentId + "'") == 0 ) {
                     statement.executeUpdate("INSERT INTO component(component_id, "
                             + "video_path, extra_information, lesson_id) VALUES ('"
                             + componentId + "','" + videoPath + "','" 
                             + extraInfo + "','" + lessonId + "')");
                }         
            
                for (int j=0; j<4; j++) {
                    imageId = 
                            ((Element)element.getElementsByTagName("Image").item(j)).getAttribute("name");
                    imagePath = 
                            element.getElementsByTagName("Image").item(j).getTextContent();

                    /* Check if row already exists before inserting 
                       Used because postgresql doesn't offer INSERT OR UPDATE */
                    if (statementImage.executeUpdate("UPDATE image SET "
                        + "image_id='" + imageId + "', "
                        + "image_path='" + imagePath + "' WHERE image_id='" 
                        + imageId + "'" ) == 0 ) {
                            statementImage.executeUpdate("INSERT INTO image(image_id,"
                                    + " image_path) VALUES ('" + imageId + "','" + 
                                    imagePath + "')");
                    }

                    /* Check if row already exists before inserting 
                       Used because postgresql doesn't offer INSERT OR UPDATE */
                    if (statementCI.executeUpdate("UPDATE component_image SET "
                        + "component_id='" + componentId + "', "
                        + "image_id='" + imageId + "' WHERE component_id='" 
                        + componentId + "' AND image_id='" + imageId + "'") == 0 ) {
                            statementCI.executeUpdate("INSERT INTO component_image "
                                    + "(component_id, image_id) VALUES ('" 
                                    + componentId + "','" + imageId + "')");
                    }
                }
            }
        }
    }
    
    
    /**
     * Obtains the values that need to be inserted into the database from the
     * resources.xml and adds them
     * 
     * @param lp the application
     */
    public void insertIntoDB(LibrasProject lp)  {

        NodeList lessonsList = null;   // A list of all the lessons available
        
        application = lp;
        application.establishConnection();
        
        /* Extracts from the resource file the information about the lessons */
        try {                     
            
            InputStream fXmlFile = LibrasProject.class.getClassLoader().getResourceAsStream("lessonResources/resources.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            
            doc.getDocumentElement().normalize();
            
            lessonsList = (NodeList)doc.getElementsByTagName("Lesson");
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Lesson2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            add2BD(lessonsList);
        } catch (SQLException ex) {
            Logger.getLogger(Lesson2DB.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
}
