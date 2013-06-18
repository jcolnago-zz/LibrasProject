package application;

import learning.ReviewController;
import selection.SelectionController;
import registration.RegistrationController;
import learning.LearnController;
import login.LoginController;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lesson2DB.Lesson2DB;

/**
 * Project's main class
 * 
 * @author Jessica H. Colnago
 */
public class LibrasProject extends Application {
    
    /* Class variables */
    private Stage stage;
    private String userName;
    private Connection connection = null;
    public Properties prop = new Properties();
    
    
    /**
     * Function responsible for establishing a connection to the desired 
     * database.
     */
    public void establishConnection() {
        
        // If there is a connection already, return.
        if (connection != null){ 
            return;
        }
            
        // Database url that determines the database to be connected to.
        try {
            Class.forName("org.postgresql.Driver");
               
            // Must determine database, user and password
            connection = DriverManager.getConnection(prop.getProperty("dbURL"),
                    prop.getProperty("dbUser"), prop.getProperty("dbPassword"));
               
            if (connection != null) {
                System.out.println("Connecting to database...");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Function responsible for closing the connection previously established. 
     */
    public void closeConnection() {
        try {
            connection.close();
        }
        catch(Exception e) {
            System.out.println("Problem to close the connection to the database");
        }
    }
    
    
    /**
     * The main entry point for all JavaFX applications.
     * 
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        /* load the properties file */
        
        prop.load(LibrasProject.class.getClassLoader().getResourceAsStream("lessonResources/config.properties"));
        establishConnection();
        
        (new Lesson2DB()).insertIntoDB(this);
               
        /* Initializes the application state and its properties */
        stage = primaryStage;
        stage.setTitle("Learning Libras");
        stage.setResizable(false);
        stage.centerOnScreen();
        
        gotoLogin();                // starts first Scene
        primaryStage.show();        // shows the stage
    }

    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       launch(args);
    }
    
    
    /**
     * Responsible for loading the new scene based on the .fxml file
     * 
     * @param fxml the fxml related to the scene that should be loaded
     * @return returns the controller for that scene
     */
    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = LibrasProject.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(LibrasProject.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        } 
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
    
    
    /**
     * Loads the controller for the selection scene based on the .fxml file.
     */
    public void gotoSelection() {
        try {
            SelectionController selection = (SelectionController) replaceSceneContent("/selection/Selection.fxml");
            selection.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Loads the controller for the Lessons scene based on the .fxml file.
     * 
     * @param lessonName the lesson name of the lesson to be initialized
     */
    public void gotoLessons(String lessonName) {
        try {
            LearnController lessons = (LearnController) replaceSceneContent("/learning/Learn.fxml");
            lessons.setApp(this, lessonName);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Loads the controller for the Lessons scene based on the .fxml file.
     * 
     * @param lessonName the lesson name of the lesson to be initialized
     */
    public void gotoMemorize(String lessonName) {
        try {
            LearnController lessons = (LearnController) replaceSceneContent("/learning/Memorize.fxml");
            lessons.setMemorize(this, lessonName);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Loads the controller for the review scene based on the .fxml file. 
     */
    public void gotoReview(String lessonName, boolean isReview) {
        try {
           ReviewController review = (ReviewController) replaceSceneContent("/learning/Review.fxml");
           review.setApp(this, lessonName, isReview);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Loads the controller for the login scene based on the .fxml file.
     */
    public void gotoLogin() {
        try {
            LoginController login = (LoginController) replaceSceneContent("/login/Login.fxml");
            login.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Loads the controller for the registration scene based on the .fxml file.
     */
    public void gotoRegistration() {
        try {
            RegistrationController registration = (RegistrationController) replaceSceneContent("/registration/Registration.fxml");
            registration.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(LibrasProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * Gets the current user logged in.
     * 
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    
    /**
     * Sets the current user logged in.
     * 
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    
    /**
     * Returns the connection to the database
     * 
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }
}