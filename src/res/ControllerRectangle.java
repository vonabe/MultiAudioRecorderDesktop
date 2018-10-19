package res;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation.Status;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author wenkael™
 */
public class ControllerRectangle implements Initializable {

    @FXML
    private Rectangle rectangle_robot;
    @FXML
    private Label              lblRec;
    @FXML
    private Label             lblSIZE;
    
    private static ControllerRectangle instance;
    public static ControllerRectangle getInstance(){
        return instance;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
    }
    
    public  void setResize(double width, double height){
        rectangle_robot.setWidth(width);
        rectangle_robot.setHeight(height);
        lblSIZE.setText(width+"x"+height);
    }
    
    private SequentialTransition sequential;
    private boolean animation;
    public void animation(){
        if(sequential!=null)if(sequential.getStatus()==Status.RUNNING){animation=false;return;}
        FillTransition transition = new FillTransition(
                Duration.millis(1000),
                rectangle_robot,
                Color.color(1.0D, 1.0D, 1.0D, 1.0D),
                Color.color(0.0D, 0.0D, 0.0D, 0.1D)
        );
        
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setAutoReverse(true);
        sequential = new SequentialTransition(transition);
        sequential.play();
        animation=true;
        sequential.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(animation)sequential.play();
            }
        });
        
    }
    
}
