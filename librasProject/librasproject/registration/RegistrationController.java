package registration;

import login.LoginController;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import application.LibrasProject;
import javafx.scene.control.Label;
import validation.InputMaskValidation;

/**
 * This class is responsible for the control of the registration scene.
 * 
 * @author Jessica H. Colnago
 */
public class RegistrationController extends AnchorPane implements Initializable {

    /* Class variables */
    private LibrasProject application;
    private BooleanBinding binding;
    private ToggleGroup sex;
    private ToggleGroup type;
    
    /* FXML id for the different ui components */
    @FXML
    TextField userId, age;
    @FXML
    PasswordField passw;
    @FXML
    RadioButton female, male, deaf, notDeaf;
    @FXML
    Button register;
    @FXML
    Label message;

    
    /**
     * Sets up the application and any other work that needs to be done on start
     * up.
     * 
     * @param application that's responsible for the scene
     */
    public void setApp(LibrasProject application){
        this.application = application;      
        final InputMaskValidation listener1 = new InputMaskValidation(InputMaskValidation.TEXTONLY, 15, userId);
        final InputMaskValidation listener2 = new InputMaskValidation(InputMaskValidation.PASSWORD, 20, passw);
        final InputMaskValidation listener3 = new InputMaskValidation(InputMaskValidation.NUMBERONLY, 3, age);

        userId.textProperty().addListener(listener1);
        passw.textProperty().addListener(listener2);
        age.textProperty().addListener(listener3);
        
        binding = new BooleanBinding() {
            {
            super.bind(listener1.erroneous, listener2.erroneous, listener3.erroneous);
            }

            @Override
            protected boolean computeValue() {
                return (listener1.erroneous.get() || listener2.erroneous.get() 
                     || listener3.erroneous.get());
            }
        };

        register.disableProperty().bind(binding);
        
        sex = new ToggleGroup();
        female.setToggleGroup(sex);
        female.setSelected(true);
        male.setToggleGroup(sex);
        
        type = new ToggleGroup();
        notDeaf.setToggleGroup(type);
        notDeaf.setSelected(true);
        deaf.setToggleGroup(type);
    }  
    
    
    /**
     * This function responds to the event of pressing the back button.
     * It returns to the login scene.
     * 
     * @param event information about the event
     */
    @FXML
    public void back2Login(ActionEvent event) {
        application.gotoLogin();
    }
    
    
    /**
     * This function responds to the event of pressing the register button.
     * It will add the user to the database as well as insert the values for this
     * user into other user related tables. 
     * 
     * @param event information about the event
     */
    @FXML
    public void addUser(ActionEvent event) {
        Statement statement;
        char gender;
        String type;

        /* Defines which options where selected */
        gender = female.isSelected() ? 'F' : 'M';
        type = deaf.isSelected() ? "surdo" : "ouvinte";   

        /* Insert user data into database */
        try {
            statement = application.getConnection().createStatement();
            statement.executeUpdate("INSERT INTO user_login"
                    + "(user_id, password, age, gender, type) VALUES ('" 
                    + userId.getText() + "','" + passw.getText() + "','"
                    + Integer.parseInt(age.getText()) + "','" + gender + "','"
                    + type + "')");
            
            /* Insert related information into database */
            insertUserLesson(userId.getText());
            insertReviewComponents(userId.getText());
            /* After it is done, return to the login page */
            application.gotoLogin();
            
        } catch (SQLException ex) {
            message.setText("Usuario ja cadastrado.");
            //Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }


    
    /**
     * This function inserts into the user_lesson table the necessary fields.
     * 
     * @param text contains the user_id
     */
    private void insertUserLesson(String text) {
        Statement statementSelect;
        Statement statement;
        ResultSet rs;
        
        try {
            statementSelect = application.getConnection().createStatement();
            statement = application.getConnection().createStatement();
            /* Obtain all lessons' ids */
            rs = statementSelect.executeQuery("SELECT lesson_id FROM lesson");
            while(rs.next()) {
               /* Insert a pair (lesson_id, user_id) for every lesson */
               statement.executeUpdate("INSERT INTO user_lesson"
                    + "(user_id, lesson_id) VALUES ('" 
                    + text + "','" + rs.getString("lesson_id") + "')"); 
            }
            
        } catch (SQLException ex) {
            message.setText("Problema na insercao dos dados do usuario. Tente novamente.");
            //Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * This function inserts into the review_component table the necessary fields.
     * 
     * @param text contains the user_id
     */
    private void insertReviewComponents(String text) {
        Statement statementSelect;
        Statement statement;
        ResultSet rs;
        try {
            statementSelect = application.getConnection().createStatement();
            statement = application.getConnection().createStatement();
            /* Obtain all components' ids */
            rs = statementSelect.executeQuery("SELECT component_id FROM component");
            while(rs.next()) {
                /* Insert a pair (component_id, user_id) for every component */
               statement.executeUpdate("INSERT INTO review_component"
                    + "(user_id, component_id) VALUES ('" 
                    + text + "','" + rs.getString("component_id") + "')"); 
            }
            
        } catch (SQLException ex) {
            message.setText("Problema na insercao dos dados do usuario. Tente novamente.");
            //Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }  
}
