package multiaudiorecorder;

import TestOOP.Close;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import res.ControllerRectangle;

/**
 *
 * @author wenkael™
 */
public class WindowDialog implements Close, EventHandler<ScrollEvent>{
    
    private Stage        dialog;
    private Close         exits;
    private double initX, initY;
    private static final Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    
    public WindowDialog(Close ex) {
        exits = ex;
    }
    
    public void show(){
                
                dialog = new Stage();
                dialog.initStyle(StageStyle.TRANSPARENT);
                FXMLLoader fxml = new FXMLLoader(getClass().getClassLoader().getResource("res/rectangle_robot.fxml"));
                Parent parent = null;
                try {
                    parent = fxml.load();
                } catch (IOException ex) {
                    Logger.getLogger(WindowDialog.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                dialog.setTitle("MultiAudioRecorder v1.0 - DesktopStream");
                dialog.getIcons().add(new Image("res/ic_launcher_web.png"));
                
//                dialog.initModality(Modality.NONE);
//                dialog.initStyle(StageStyle.UTILITY);
//                dialog.setWidth(600);
//                dialog.setHeight(400);
//                dialog.setResizable(false);
                
                Scene scene = new Scene(parent, Color.rgb(0, 0, 0, 0.0D));
                scene.setOnScroll(this);
                dialog.setScene(scene);
                dialog.show();
                
                dialog.setOnCloseRequest((WindowEvent event) -> {
                    close();
//                        System.out.println("close WindowDialog");
                });
                
                scene.setOnMousePressed((MouseEvent event) -> {
                    setPositionFrame(event.getScreenX(), event.getScreenY(), true);
                });
                
                scene.setOnMouseDragged((MouseEvent event) -> {
                    setPositionFrame(event.getScreenX(), event.getScreenY(), false);
                });
                
                ControllerRectangle.getInstance().animation();
//                animationRotate(dialog.getScene().getRoot(), 2000);
    }
    
    public Rectangle getRectangle(){
        return new Rectangle((int)dialog.getX(), (int)dialog.getY(), (int)dialog.getWidth(), (int)dialog.getHeight());
    }
    
    public void setResize(double scaling)
    {
        if(scaling>0.0F){
            dialog.setWidth (dialog.getWidth()  +scaling);
            dialog.setHeight(dialog.getHeight() +scaling);
            ControllerRectangle.getInstance().setResize(dialog.getWidth(), dialog.getHeight());
        }else{
            if(dialog.getWidth()>300){
                dialog.setWidth (dialog.getWidth()  +scaling);
                dialog.setHeight(dialog.getHeight() +scaling);
                ControllerRectangle.getInstance().setResize(dialog.getWidth(), dialog.getHeight());
            }
        }
        
    }
    
    public void setPositionFrame(double x, double y, boolean toach){
        if(toach)
        {
            initX = x-dialog.getX();
            initY = y-dialog.getY();
            return;
        }
        
        double moveX = x-initX;
        double moveY = y-initY;
        
        if(screen_size.width >= moveX+(dialog.getWidth()) && 0 <= moveX+10)
            dialog.setX(moveX);
        else {
            dialog.setX( ((dialog.getWidth()/2+dialog.getX())>screen_size.width/2D) ? screen_size.width-dialog.getWidth() : -0 );
        }
        
        if(screen_size.height >= moveY+(dialog.getHeight()) && 0 <= moveY+10)
            dialog.setY(moveY);
        else 
            dialog.setY( ((dialog.getHeight()/2+dialog.getY())>screen_size.height/2D) ? screen_size.height-dialog.getHeight() : -0 );
        
    }
    
    
    public RotateTransition animationRotate(Node node, long millis){
        RotateTransition rt = new RotateTransition(Duration.millis(millis), node);
//                rt.setInterpolator(Interpolator.LINEAR);
//                rt.setByAngle(360);
//                rt.setCycleCount(3);
//                rt.setAxis(Rotate.Y_AXIS);
//                rt.play();
          
         final Timeline timeline = new Timeline();
            timeline.setCycleCount(2);
            timeline.setAutoReverse(true);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
              new KeyValue (node.translateXProperty(), 0)));
            timeline.play();
        
//        PathTransition transition = newPathTransitionTo(node, 460, 320);
//        transition.play();

        
//        TranslateTransition transition = new TranslateTransition(Duration.millis(millis), node);
//        transition.setInterpolator(Interpolator.LINEAR);
//        transition.setFromX(5);
//        transition.setFromY(5);
//        transition.setToX(node.getTranslateX());
//        transition.setToY(node.getTranslateY());
//        transition.play();
        return rt;
    }
    
    
    @Override
    public void close(){
        exits.close();
        dialog.close();
    }
    
    @Override
    public void handle(ScrollEvent event) {
        if(event.getDeltaY()>0)
            setResize(5);
                else setResize(-5);
    }
    
    
}
