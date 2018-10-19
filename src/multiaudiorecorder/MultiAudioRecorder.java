package multiaudiorecorder;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import res.Dialog;

/**
 *
 * @author wenkael™
 */
public class MultiAudioRecorder extends Application implements EventHandler<ScrollEvent>, InterfaceTray {
    
    private static Stage               primaryStage;
    private static final Dimension      screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    private static double              initX, initY;
    private static Dialog               dialog_exit;
    public static double              volume = 0.0D;
    public static ArrayList<Node>          listNode;
    private Tray tray = null;
    
    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("res/ic_launcher_web.png"));
        stage.setTitle("MultiAudioRecorder v1.0");
        
        FXMLLoader fxml = new FXMLLoader(getClass().getClassLoader().getResource("res/AudioFrame.fxml"));
        Group group = (Group) fxml.load();
        AudioFrameController.init(this);
        
        Scene scene = new Scene(group, Color.rgb(0, 0, 0, 0.0D));
        stage.setScene(scene);
        stage.show();
        scene.setOnScroll(this);
        
//        listNode = getAllNodes(group);
        primaryStage = stage;
        tray = new Tray(this);
        
        Parameters param = super.getParameters();
        
        AudioFrameController.getInstance().addClient("param - ", Arrays.toString(param.getRaw().toArray()));
        List<String> raw = param.getRaw();
        
        if(raw!=null&&raw.size()>0){
            for(String parameter : raw){
                System.out.println("param - "+parameter);
                if("/hide".equals(parameter)){
                    this.hide();
                }
            }
        }
    }
    
//    public static ArrayList<Node> getAllNodes(Parent root) {
//        ArrayList<Node> nodes = new ArrayList<>();
//        addAllDescendents(root, nodes);
//    return nodes;
//}
//
//    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
//        parent.getChildrenUnmodifiable().stream().map((node) -> {
//            nodes.add(node);
//            return node;
//        }).filter((node) -> (node instanceof Parent)).forEach((node) -> {
//            addAllDescendents((Parent)node, nodes);
//        });
//}
    
    public void selectAudio(int select)
    {
        
    }
    
    public void setVolume(double value)
    {
        volume = value;
    }
    public double getVolume()
    {
        return volume;
    }
    
    public void setPositionFrame(double x, double y, boolean toach)
    {
        if(toach)
        {
            initX = x-primaryStage.getX();
            initY = y-primaryStage.getY();
            return;
        }
        
        double moveX = x-initX;
        double moveY = y-initY;
        
        if(screen_size.width >= moveX+(645) && 0 <= moveX+45)
            primaryStage.setX(moveX);else primaryStage.setX(((moveX+45D)>screen_size.width/2D) ? screen_size.width-645 : -40);
        if(screen_size.height >= moveY+(445) && 0 <= moveY+45)
            primaryStage.setY(moveY); else primaryStage.setY(((y+45D)>screen_size.height/2D) ? screen_size.height-445 : -40);
    }
    
    public static void repaint(){
        
//        primaryStage.requestFocus();
//        hide();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
//        Preferences prefs = Preferences.userRoot().node(System.getProperty("app.preferences.id")).node("JVMUserOptions");
//        prefs.put("-Xmx", "64m");
//        prefs.put("-Xms", "64m");
//        try {
//            prefs.flush();
//        } catch (BackingStoreException ex) {
//            Logger.getLogger(MultiAudioRecorder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        launch(args);
    }

    @Override
    public void handle(ScrollEvent event) {
//        if(event.getDeltaY()>0)
//            AudioFrameController.skroll(5);
//                else AudioFrameController.skroll(-5);
    }
    
    @Override
    public void show() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                primaryStage.show();
            }
        });
    }
    
    @Override
    public void hide() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                primaryStage.close();
            }
        });
    }
    
    @Override
    public void exit(){
        if(dialog_exit==null)
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog_exit = new Dialog();
                dialog_exit.show();
                if(dialog_exit.getStatus()){
                    System.exit(0);
                }
                dialog_exit = null;
            }
        });
        
    }
    
    
}
