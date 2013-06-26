package learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Worker thread that implements the behavior of a review and its components
 * 
 * @author Jessica H. Colnago
 */
public class ReviewThread extends Task<Void> {

    /* Class variables */
    private ReviewController controller;
    public ArrayList<LessonComponent> reviews;
    public String extraInformation;
    public List<String> images;
    public boolean review;
    
    
    /**
     * Class constructor
     * @params the reviewController that starts the thread and the review it
     * should run.
     */
    ReviewThread(ReviewController controller, boolean review) {
        this.controller = controller;
        this.review = review;
    }
    
    
    /**
     * Implements the logic of the behavior.
     * @return a string informing that it is done
     */
    @Override
    protected Void call() throws Exception {      
        boolean result;
        String recognized;
        PreparedStatement pStatement;
        Statement statement;
        
        int mistakes = 0;
        int iterations = controller.lessonComponents.size();
        
        Socket clientSocket = new Socket(controller.application.prop.getProperty("server"),
                Integer.parseInt(controller.application.prop.getProperty("port")));
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        pStatement = controller.application.getConnection().prepareStatement(
                  "UPDATE review_component SET mistakes=?, last_reviewed='" 
                + controller.getCurrentTimeStamp() + "' WHERE component_id=? AND user_id='"
                + controller.application.getUserName() + "';");
        
        while (iterations >= 0) {
            
            /* Waits for the user to be ready */
            controller.setActiveCircle(Color.RED);
            synchronized(controller.ready) {
                controller.ready.wait(); 
            }

            Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      controller.setRecognized("");
                    }
             });
            
            /* Indicate the user can start */
            controller.setActiveCircle(Color.GREEN);
            out.println("START");
            Thread.sleep(3000);  // In order to get the predominant value recognized
            out.println("STOP");
 
            recognized = in.readLine();
            result = recognized.equalsIgnoreCase(controller.currentComponent);
                      
            if (result) {
                /* Update the review_component with the amout of mistakes made */
                pStatement.setInt(1, mistakes);
                pStatement.setString(2, controller.currentComponent);
                pStatement.executeUpdate();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      controller.progress+=1/controller.total;
                      controller.showNextElement();
                      controller.setActiveCircle(Color.RED);
                    }
                });  
                iterations--;
                mistakes = 0;
            }
            else {
                final String temp = recognized;
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      controller.setRecognized(temp);
                    }
                });
                                
                System.out.println("Ooopsie!");
            }          
        }
        if (!review) {
            statement = controller.application.getConnection().createStatement();
            statement.executeUpdate("UPDATE user_lesson SET complete=true" 
                + " WHERE lesson_id='" + controller.lessonName + "' AND user_id='"
                + controller.application.getUserName() + "';");
        }
        in.close();
        out.close();
        return null;
    }
}