package login;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import application.LibrasProject;

/**
 * This class is responsible for the control of the login scene.
 * 
 * @author Jessica H. Colnago
 */
public class LoginController extends AnchorPane implements Initializable {

    /* Class variables */
    private LibrasProject application;
    
    /* FXML id for the different ui components */
    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    Label errorMessage;
    
    
    /**
     * Performs the proper actions for when the close button receives an action.
     * 
     * @param event the action event information
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        application.closeConnection();
        Platform.exit();    // finishes the application
    }
    
    
    /**
     * Sets up the application and any other work that needs to be done on start
     * up.
     * 
     * @param application responsible for the scene
     */
    public void setApp(LibrasProject application){
        this.application = application;
    }   
    
    
    /**
     * This function responds to the event of pressing the login button.
     * It verifies if the user exists and, if so, if the password and login pair
     * is a valid one
     * 
     * @param event information about the event
     */
    @FXML
    public void processLogin(ActionEvent event) {
        PreparedStatement pStatement;
        ResultSet rs;
        try {
            pStatement = application.getConnection().prepareStatement("SELECT password " 
                    + "FROM user_login WHERE user_id='" + userId.getText() + "'");
            rs = pStatement.executeQuery();
            if (!rs.next()) {
                errorMessage.setText("Usuario nao cadastrado.");
            }
            else {
                if (!password.getText().equals(rs.getString("password"))){
                    errorMessage.setText("Usuario ou senha incorreta.");
                }
                else {
                    application.setUserName(userId.getText());
                    application.gotoSelection();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * This function responds to the event of pressing the register button.
     * It starts the registration scene.
     * 
     * @param event information about the event
     */
    @FXML
    public void registerUser(ActionEvent event) {
        application.gotoRegistration();
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setText("");
        userId.setPromptText("learningLibras");
        password.setPromptText("learningLibras");       
    } 
}
