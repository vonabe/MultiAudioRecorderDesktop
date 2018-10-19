package res;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import multiaudiorecorder.WindowDialog;

/**
 * FXML Controller class
 *
 * @author wenkael™
 */
public class Dialog {
    
    private Stage stage;
    private Scene scene;
    private boolean status = false;
    
    
    private Group         group;
    private Rectangle rectangle;
    @FXML
    private Button OK;
    @FXML
    private Button NO;
    @FXML
    private Label lblSQUARY;
    
    /**
     * Initializes the controller class.
     */
    
    public Dialog() {
        
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("res/ic_launcher_web.png"));
        stage.setTitle("MultiAudioRecorder v1.0 - Exit");
        
        FXMLLoader fxml = new FXMLLoader(getClass().getClassLoader().getResource("res/dialog.fxml"));
//        Group parent = null;
        try {
            group = fxml.load();
        } catch (IOException ex) {
            Logger.getLogger(WindowDialog.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        Object[]array = group.getChildrenUnmodifiable().toArray();
        for(Object o:array){
            if(o instanceof Button){
                Button btn = (Button)o;
                if(btn.getId().equals("OK"))OK = btn;
                else NO = btn;
            }else if(o instanceof Rectangle){
                rectangle = (Rectangle) o;
            }
        }
        
        OK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                status = true;
                close();
            }
        });
        NO.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                status = false;
                close();
            }
        });
        
        scene = new Scene(group, Color.rgb(0, 0, 0, 0.0D));
        stage.setScene(scene);
        
    }
    
    public RotateTransition animationRotate(Node node, long millis){
        RotateTransition rt = new RotateTransition(Duration.millis(millis), node);
                rt.setInterpolator(Interpolator.LINEAR);
                rt.setByAngle(360);
                rt.setCycleCount(3);
                rt.setAxis(Rotate.Y_AXIS);
                rt.play();
            return rt;
    }
    
    public void show_animation(){
        ScaleTransition transition_height = new ScaleTransition(Duration.millis(250), group);
            transition_height.setFromX(0.0F);
            transition_height.setToX(1.0F);
            transition_height.setFromY(0.0F);
            transition_height.setToY(1.0F);
        transition_height.play();
    }
    
    public ScaleTransition hide_animation(){
        ScaleTransition transition = new ScaleTransition(Duration.millis(250), group);
            transition.setToX(0.0D);
            transition.setToY(0.0D);
        transition.play();
        return transition;
    }
    
    public boolean getStatus(){
        return status;
    }
    
    public void show(){
        show_animation();
        stage.showAndWait();
    }
    public void close(){
        hide_animation().setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {    
                stage.close();
            }
        });
    }
    
}
