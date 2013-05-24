package selection;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import application.LibrasProject;
import learning.LearningController;

/**
 * This class is responsible for the control of the Selection scene
 * 
 * @author Jessica H. Colnago
 */
public class SelectionController extends AnchorPane implements Initializable {
    
    /* Class variables */
    private LibrasProject application; // relates to the responsible application
    
    /* FXML id for the different ui components */
    @FXML
    public AnchorPane anchorPane;
     
    
    /**
     * Performs the proper actions for when the close button receives an action.
     * @param event the action event information
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        application.setUserName("");
        application.gotoLogin();
    }
    
    
    /**
     * Performs the proper actions for when the close button receives an action.
     * @param event the action event information
     */
    @FXML
    private void handleReview(String lessonName) {
        boolean inReview = false;
        try {
            Statement statement = application.getConnection().createStatement();
            ResultSet rs;
        
            rs = statement.executeQuery("SELECT c.component_id, mistakes, last_reviewed "
                + "FROM review_component AS rc, component AS c "
                + "WHERE user_id='" + application.getUserName() + "'"
                + "AND lesson_id='" + lessonName + "' "
                + "AND rc.component_id=c.component_id"); 

            long daysDiff;
            /* Iterate over every component */
            while(rs.next()) {
                daysDiff = (Calendar.getInstance().getTimeInMillis()-rs.getDate("last_reviewed").getTime())/(1000*60*60*24);
                if (rs.getInt("mistakes")*2 + daysDiff  > 6 && !inReview) {
                    application.gotoReview(lessonName);
                    inReview = true;
                }
            }
            if(!inReview) {
                (new LearningController()).showMessageBox();
            }            
        } catch (SQLException ex) {
            Logger.getLogger(SelectionController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    /**
     * Performs the proper actions for when the different options of selection
     * receive mouse events
     * @param event the action event information
     */
    @FXML
    private void handleSelectionMouseAction(InputEvent event) {
        Statement statement;
        ResultSet rs;
        boolean complete = false;
        Group group = (Group)event.getSource();        
        
         /* If the mouse pointer clicks the group area */
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            try {
                statement = application.getConnection().createStatement();
                rs = statement.executeQuery("SELECT complete "
                        + "FROM user_lesson WHERE user_id='" + application.getUserName()
                        + "' AND lesson_id='" + group.getId() + "'");
                while(rs.next()) {
                    complete = rs.getBoolean("complete");
                }     
            } catch (SQLException ex) {
                Logger.getLogger(SelectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (complete) {
                handleReview(group.getId());  // initiates the review
            }
            else {
                application.gotoLessons(group.getId());  // initiates the lesson with
                                        // the id of the group clicked  
            }
            
        }
       
        /* If the mouse pointer enters the group area */
        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            group.setOpacity(1.0);   // set the group opacity to fully visible          
        }
        /* If the mouse pointer exits the group area */
        else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            group.setOpacity(0.5);   // set the group opacity to half visible    
        } 
    }
    
    
    /**
     * Sets up the application and any other work that needs to be done on start
     * up.
     * @param application application that's responsible for the scene
     */
    public void setApp(LibrasProject application){
        this.application = application;
        
        try {
            PreparedStatement pStatement = application.getConnection().prepareStatement("SELECT complete " 
                                        + "FROM user_lesson WHERE lesson_id=? AND user_id='"
                                        + this.application.getUserName()+"'");
            ResultSet rs;
            ObservableList<Node> children = anchorPane.getChildren();
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i).getClass() == Group.class) {
                    pStatement.setString(1, children.get(i).getId());
                    rs = pStatement.executeQuery();
                    while (rs.next()) {
                       if (rs.getBoolean("complete")){
                            ObservableList<Node> groupChildren = ((Group)children.get(i)).getChildren();
                            for (int j = 0; j < groupChildren.size(); j++) {
                               if ("done".equals(groupChildren.get(j).getId())) {
                                   groupChildren.get(j).setVisible(true);
                               }
                            }
                        }  
                    }
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SelectionController.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
