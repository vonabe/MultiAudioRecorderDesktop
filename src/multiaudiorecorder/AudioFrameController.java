package multiaudiorecorder;

import Network.Broadcast;
import Network.ServerManager;
import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import visualizator.SpectrumListener;
import visualizator.Visualization;

/**
 * FXML Controller class
 *
 * @author wenkael™
 */
public class AudioFrameController implements Initializable {

    @FXML
    public ImageView imgStream;
    @FXML
    public Slider slider;
    @FXML
    public Pane viewelement;
    @FXML
    public Button btnHide;
    @FXML
    public Button btnEXIT;
    @FXML
    public MenuBar taskpanel;
    @FXML
    public Canvas canvas;
    @FXML
    public Label txtValueSlider;
    @FXML
    public Button btnPlay;
    @FXML
    public Button btnPause;
    @FXML
    public Button btnStop;
    @FXML
    public ProgressBar progress;
    @FXML
    private Button btnUpdateDevice;

    @FXML
    private ChoiceBox choiceboxaudioInput;
    @FXML
    private Button audioINdisconnect;
    @FXML
    private ChoiceBox choiceboxcamera;
    @FXML
    private Button videodisconnect;
    @FXML
    private ChoiceBox choiceboxaudioOutput;
    @FXML
    private Button audioOUTdisconnect;
    @FXML
    private ProgressIndicator progressLoad;
    @FXML
    private Group rootGroup;
    @FXML
    private Label numberConnect;
    @FXML
    private ListView<String> listviewserver;
    @FXML
    private StackPane stackpanelIP;
    @FXML
    private ContextMenu context_play;
    @FXML
    private CheckBox checkBoxPanelSlide;
    @FXML
    private Canvas canvas_spectrum;
    @FXML
    private CheckMenuItem autorun;
    @FXML
    private Text textNameApplication;
    @FXML
    private CheckMenuItem autohide;

    private MediaPlayer mediaPlayer;
    private SpectrumListener spectrumListener;
    private Visualization visualization;
    public double AUDIO_SPECTRUM_INTERVAL = 0.1;

    private static final Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    private static AudioFrameController instance;

    synchronized public static AudioFrameController getInstance() {
        return instance;
    }

    private static ITargetDataLine iTargetDataLine;
    private static ISourceDataLine iSourceDataLine;
    private static Visualizer visualizer;
    private static AudioStream audioStream;
    private static List<Webcam> webCamsArray;
    private static WebCamera webcamera;
    private static CanvasImg canvasImg;
    private static AudioMicrophone microphone;
    private static MultiAudioRecorder AudioRecorder;
    private static Desktop desktop;
    private static InputCameraStream inputcamera;
    private static ServerManager servermanager;
    private static Broadcast broadcast = null;
    @FXML
    private ComboBox<String> boxIPcomputed;

    private static final Port.Info[] infoDevice
            = {
                Port.Info.COMPACT_DISC,
                Port.Info.HEADPHONE,
                Port.Info.LINE_IN,
                Port.Info.LINE_OUT,
                Port.Info.MICROPHONE,
                Port.Info.SPEAKER
            };

    private static final ArrayList<Mixer.Info> portTargetLine = new ArrayList<>();
    private static final ArrayList<Mixer.Info> portSourceLine = new ArrayList<>();

    private static final HashMap<String, String> clientID = new HashMap();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loading(false);
//        addText();

        imgStream.setImage(new Image("res/logo.png"));

//        ImageView view = new ImageView(new Image("res/logo.png"));
//        view.setX(txtX);
//        view.setY(txtY);
        visualizer = new Visualizer(canvas_spectrum);
        createListeners();
        updateDevice();
        instance = this;
        try {
            numberConnect.setText(new String(("#" + 0).getBytes("windows-1252"), "windows-1251"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);
        }

        broadcast = new Broadcast();
        broadcast.bind();

        MotionBlur blur = new MotionBlur(360D, 30D);
        textNameApplication.setEffect(blur);
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double angle = blur.getAngle();
                double radius = blur.getRadius();

                if (angle > 0.0D) {
                    blur.setAngle(angle - 0.5D);
                }
                if (radius > 0.0D) {
                    blur.setRadius(radius - 0.2D);
                }
                if (angle <= 0.0D && radius <= 0.0D) {
                    stop();
                }
            }
        };
        timer.start();

//        GraphicsContext gc =  
//                canvas_spectrum.getGraphicsContext2D();
//        gc.fill();
//        gc.strokeRect(
//                0,              //x of the upper left corner
//                0,              //y of the upper left corner
//                canvas_spectrum.getWidth(),    //width of the rectangle
//                canvas_spectrum.getHeight());  //height of the rectangle
//         
//        gc.setFill(Color.RED);
//        gc.setStroke(Color.BLUE);
//        gc.setLineWidth(1);
    }

    private void addText() {
//        Text text = new Text();
//        text.setId("fancytext");

//        textNameApplication.setX(28);
//        textNameApplication.setY(265);
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(254, 235, 66, 0.3));
        ds.setOffsetX(5);
        ds.setOffsetY(5);
        ds.setRadius(5);
        ds.setSpread(0.2);

        blend.setBottomInput(ds);

        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.web("#f13a00"));
        ds1.setRadius(20);
        ds1.setSpread(0.2);

        Blend blend2 = new Blend();
        blend2.setMode(BlendMode.SRC_OVER);

        InnerShadow is = new InnerShadow();
        is.setColor(Color.web("#feeb42"));
        is.setRadius(9);
        is.setChoke(0.8);
        blend2.setBottomInput(is);

        final InnerShadow is1 = new InnerShadow();
        is1.setColor(Color.web("#f13a00"));
        is1.setRadius(5);
        is1.setChoke(0.4);

        blend2.setTopInput(is1);

        Blend blend1 = new Blend();
        blend1.setMode(BlendMode.SOFT_LIGHT);
        blend1.setBottomInput(ds1);
        blend1.setTopInput(blend2);

        blend.setTopInput(blend1);

        textNameApplication.setStroke(Color.WHITE);
        textNameApplication.setStrokeWidth(1.0D);
        textNameApplication.setFontSmoothingType(FontSmoothingType.LCD);

        textNameApplication.setEffect(blend);
        textNameApplication.setText("MultiAudioRecorder");

    }

    public static void init(MultiAudioRecorder recorder) {
        AudioRecorder = recorder;
        servermanager = new ServerManager();

//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                servermanager = new ServerManager();
//                boolean stat = false;
//                while(stat==false){
//                    stat = servermanager.TCPStart(5555);
//                    if(!stat)servermanager.disposeServer();
//                    try {Thread.sleep(1000);}
//                    catch (InterruptedException ex)
//                    {Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);}
//                }
//                servermanager.TCPAccept();
//            }
//        }).start();
    }

    public void addClient(final String n, final String ip) {
        String name = n;
        if (clientID.containsKey(n)) {
            name += new Random().nextInt(1000);
        }
        clientID.put(name, ip);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList listTarget = FXCollections.observableArrayList(clientID.keySet().toArray());
                listviewserver.setItems(listTarget);
            }
        });
    }

    public void clearClient() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setCountConnect(0);
                listviewserver.getItems().clear();
                clientID.clear();
            }
        });
    }

    public void deleteClient(String key) {
        listviewserver.getItems().remove(key);
        clientID.remove(key);
        System.out.println(clientID.size());
    }

    private void createListeners() {
        choiceboxaudioInput.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue() > 0) {

//                mediaPlayer = new MediaPlayer(
//                         new Media("file:/E:/music/Changes.mp3"));
//                spectrumListener = new SpectrumListener();
//                mediaPlayer.setAudioSpectrumListener(spectrumListener);
//                mediaPlayer.play();
//                
//                AUDIO_SPECTRUM_INTERVAL = 0.06;
//                visualization = new SpectrumBars(100, canvas_spectrum);
////                System.out.println("canvas - "+canvas_spectrum.getWidth()+":"+canvas_spectrum.getHeight());
//                
//                spectrumListener.setVisualization(visualization);
//                mediaPlayer.setAudioSpectrumInterval(AUDIO_SPECTRUM_INTERVAL);
//                mediaPlayer.setAudioSpectrumThreshold((-1)*visualization.getMaxVolume());
//                viewelement.getChildren().add(visualization.getNode());
//                if(visualization!=null)return;
                try {

                    if (audioStream != null) {
                        Mixer.Info in = portTargetLine.get(newValue.intValue() - 2);
                        iTargetDataLine = new ITargetDataLine(ITargetDataLine.DefaultAudioFormat(true), in);
                        if (!iTargetDataLine.start()) {
                            iTargetDataLine.destroy();
                            iTargetDataLine = null;
                            choiceboxaudioInput.getSelectionModel().select(0);
                            return;
                        }
                        audioStream.changeChannel(iTargetDataLine);
                        return;
                    }

                    Mixer.Info in = portTargetLine.get(newValue.intValue() - 2);
                    iTargetDataLine = new ITargetDataLine(ITargetDataLine.DefaultAudioFormat(true), in);
                    if (!iTargetDataLine.start()) {
                        iTargetDataLine.destroy();
                        iTargetDataLine = null;
                        choiceboxaudioInput.getSelectionModel().select(0);
                        return;
                    }

//                        spectrumListener = new SpectrumListener();
//                        visualization = new SpectrumBars(100, canvas_spectrum);
//                        spectrumListener.setVisualization(visualization);
                    audioStream = new AudioStream(iTargetDataLine, visualizer, spectrumListener);

//                        canvas.setOpacity(1.0D);
//                        imgStream.setOpacity(0.1D);
//                        choiceboxaudioInput.setDisable(true);
                    audioINdisconnect.setDisable(false);

                    if (!audioStream.start()) {
                        audioINdisconnect.setDisable(true);
                        canvas.setOpacity(0.1D);
                        imgStream.setOpacity(1.0D);
                        audioStream.destroy();
                        audioStream = null;
                        choiceboxaudioInput.getSelectionModel().select(0);
                    }

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        choiceboxaudioOutput.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue() > 0) {
//                      visualizer = new Visualizer(canvas_spectrum);
//                      visualizer.play();
                iSourceDataLine = new ISourceDataLine(
                        ITargetDataLine.DefaultAudioFormat(false),
                        portSourceLine.get(newValue.intValue() - 2)
                );
                iSourceDataLine.start();
//                      Scanner scanner = new Scanner(System.in);
//                      Mixer.Info in = portTargetLine.get(scanner.nextInt());
//                      System.out.println(in);

                microphone = new AudioMicrophone(iSourceDataLine, visualizer, null);
                if (!microphone.start()) {
                    microphone.destroy();
                    microphone = null;
//                          visualizer.destroy();
//                          visualizer = null;
                    choiceboxaudioOutput.getSelectionModel().select(0);
//                          iSourceDataLine.destroy();
//                          iSourceDataLine = null;
                    return;
                }
                audioOUTdisconnect.setDisable(false);
                choiceboxaudioOutput.setDisable(true);
            }
        });

        choiceboxcamera.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue() > 0) {

                if (webcamera == null) {

                    webcamera = new WebCamera();
                    webcamera.init(webCamsArray.get(newValue.intValue() - 2), imgStream);

                    canvas.setOpacity(0.1D);
                    imgStream.setOpacity(1.0D);

                    choiceboxcamera.setDisable(true);
                    videodisconnect.setDisable(false);

                }
            }
        });

        btnEXIT.setOnAction((ActionEvent event) -> {
            //                Alert alert = new Alert(AlertType.CONFIRMATION);
//                alert.setTitle("Confirmation Dialog");
//                alert.setHeaderText("Look, a Confirmation Dialog");
//                alert.setContentText("Are you ok with this?");
//
//                Optional<ButtonType> result = alert.showAndWait();
//                if (result.get() == ButtonType.OK){
//                    // ... user chose OK
//                } else {
//                    // ... user chose CANCEL or closed the dialog
//                }
            Tray.exit();
        });

        btnHide.setOnAction((ActionEvent event) -> {
            Tray.hide();
        });

        taskpanel.setOnMousePressed((MouseEvent event) -> {
            AudioRecorder.setPositionFrame(event.getScreenX(), event.getScreenY(), true);
        });
        taskpanel.setOnMouseDragged((MouseEvent event) -> {
            AudioRecorder.setPositionFrame(event.getScreenX(), event.getScreenY(), false);
        });

        stackpanelIP.setOnMousePressed((MouseEvent event) -> {
            AudioRecorder.setPositionFrame(event.getScreenX(), event.getScreenY(), true);
        });

        stackpanelIP.setOnMouseDragged((MouseEvent event) -> {
            AudioRecorder.setPositionFrame(event.getScreenX(), event.getScreenY(), false);
        });

        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            txtValueSlider.setText(String.format("delay - %2.0f", newValue.doubleValue()));
            AudioRecorder.setVolume(newValue.doubleValue());
            if (iTargetDataLine != null) {
                iTargetDataLine.setVolume(AudioRecorder.getVolume());
            }
        });

        final RadioMenuItem desktopItem = (RadioMenuItem) context_play.getItems().get(0);
        final RadioMenuItem androidCamStreamItem = (RadioMenuItem) context_play.getItems().get(1);

        btnPlay.setOnAction((ActionEvent event) -> {
            //                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//              if(microphone!=null){
//                  iSourceDataLine = new ISourceDataLine(iTargetDataLine.getFormat(), null);
//                  if(iSourceDataLine.start())
//                      microphone = new AudioMicrophone(iSourceDataLine, visualizer, null);microphone.start();
//              }
if (desktopItem.isSelected()) {
    if (desktop == null) {
        desktop = new Desktop(imgStream);
    }
} else if (androidCamStreamItem.isSelected()) {
    if (inputcamera == null) {
        inputcamera = new InputCameraStream(imgStream, viewelement);
        if (!inputcamera.start()) {
            inputcamera.destroy();
            inputcamera = null;
        }
    }
}

//                    }
//                }).start();
//                if(server == null){
//                    canvasImg = new CanvasImg(imgStream);
//                    server = new Server(canvasImg);
//                    server.play();
//                }
        });
        btnPause.setOnAction((ActionEvent event) -> {
//                if(webcamera!=null)webcamera.pause();
//                if(server != null);
        });
        btnStop.setOnAction((ActionEvent event) -> {
            //                if(server != null){server.stop();server = null;}
            if (inputcamera != null) {
                inputcamera.destroy();
                inputcamera = null;
            }
            if (desktop != null) {
                desktop.close();
                desktop = null;
            }
            if (microphone != null) {
                microphone.destroy();
                microphone = null;
            }
            imgStream.setImage(null);
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        desktopItem.setOnAction((ActionEvent event) -> {
            if (androidCamStreamItem.isSelected()) {
                androidCamStreamItem.setSelected(false);
            }
        });

        androidCamStreamItem.setOnAction((ActionEvent event) -> {
            if (desktopItem.isSelected()) {
                desktopItem.setSelected(false);
            }
        });

//        context_play.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Event - " );
//            }
//        });
        btnUpdateDevice.setOnAction((ActionEvent event) -> {
            btnUpdateDevice.setDisable(true);
            animationRotate(btnUpdateDevice, 500).setOnFinished((ActionEvent event1) -> {
                btnUpdateDevice.setDisable(false);
            });
            updateDevice();
        });

        audioINdisconnect.setOnAction((ActionEvent event) -> {
            //                if(visualizer!=null || iTargetDataLine!=null)
//                {
//                    visualizer.destroy();
//                    iTargetDataLine.destroy();
//                    visualizer=null;
//                    iTargetDataLine=null;
//                }
            if (audioStream != null) {
                audioStream.destroy();
                audioStream = null;
                clearClient();
            }
            choiceboxaudioInput.getSelectionModel().select(0);
//                choiceboxaudioInput.setDisable(false);
            audioINdisconnect.setDisable(true);
        });

        audioOUTdisconnect.setOnAction((ActionEvent event) -> {
            if (microphone != null) {
                microphone.destroy();
            }
            microphone = null;
            choiceboxaudioOutput.getSelectionModel().select(0);
            audioOUTdisconnect.setDisable(true);
            choiceboxaudioOutput.setDisable(false);
        });

        videodisconnect.setOnAction((ActionEvent event) -> {
            if (webcamera != null) {
                webcamera.destroy();
                webcamera = null;
                choiceboxcamera.getSelectionModel().select(0);
            }
            choiceboxcamera.getSelectionModel().select(0);
            videodisconnect.setDisable(true);
            choiceboxcamera.setDisable(false);
        });

        listviewserver.setMaxSize(300, 450);

//        ObservableList names = FXCollections.observableArrayList();
//        names.addAll(
//             "Adam", "Alex", "Alfred", "Albert",
//             "Brenda", "Connie", "Derek", "Donny",
//             "Lynne", "Myrtle", "Rose", "Rudolph",
//             "Tony", "Trudy", "Williams", "Zach"
//        );
//        
//        ObservableList<String> items = FXCollections.observableArrayList (
//            "Single", "Double", "Suite", "Family App");
//        listviewserver.setItems(names);
        listviewserver.setEditable(false);
        listviewserver.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listviewserver.setCellFactory((ListView<String> param) -> {
            ButtonCell cell = new ButtonCell();
            return cell;
        });

        checkBoxPanelSlide.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //                TranslateTransition openNav=new TranslateTransition(new Duration(350), listviewserver);
//                TranslateTransition closeNav=new TranslateTransition(new Duration(350), listviewserver);
//                System.out.println("scaleX - "+listviewserver.getScaleX());
            if (newValue == true) {
                ScaleTransition openNav = new ScaleTransition(new Duration(350), listviewserver);
                openNav.setToX(1);
                openNav.play();
            } else {
                ScaleTransition closeNav = new ScaleTransition(new Duration(350), listviewserver);
                closeNav.setToX(0);
                closeNav.play();
            }
        });

        autorun.setOnAction((ActionEvent event) -> {
            editReg(autorun.isSelected());
        });

        autorun.setSelected(Autorun.isRegistration());

        autohide.setOnAction((ActionEvent event) -> {
            editParamReg(autohide.isSelected());
        });

        autohide.setSelected(Autorun.isParameter());

//        listviewserver.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent click) {
//
//                if (click.getClickCount() == 1) {
////                    System.out.println("selected");
//                   //Use ListView's getSelected Item
////                   currentItemSelected = playList.getSelectionModel().getSelectedItem();
//                   //use this to do whatever you want to. Open Link etc.
//                }
//            }
//        });
//        listviewserver.setCellFactory(ComboBoxListCell.forListView(names));
//        listviewserver.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
//            @Override
//            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue){
//                System.out.println("changed "+newValue.toString());
////                System.out.println("max-size: "+maplistview.size());
////                listviewserver.cellFactoryProperty().get().call(listviewserver);
//            }
//        });
    }

    private void editParamReg(boolean selected) {
        if (selected) {
            boolean reg = Autorun.addParamHide();
            autohide.setSelected(reg);
        } else {
            boolean reg = Autorun.deleteParamHide();
            autohide.setSelected(!reg);
        }
    }

    private void editReg(boolean selected) {
        if (selected) {
            boolean reg = Autorun.registration();
            autorun.setSelected(reg);
        } else {
            boolean unreg = Autorun.unregistration();
            autorun.setSelected(!unreg);
            autohide.setSelected(!unreg);
        }
    }

    class ButtonCell extends ListCell<String> {

        @Override
        public void updateItem(final String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {

                labelNAME.setText(item);
                labelIP.setText(clientID.get(item));

                Pane pane = new Pane();
                HBox hbox = new HBox(labelNAME, pane, labelIP);
                HBox.setHgrow(pane, Priority.ALWAYS);
                setGraphic(hbox);

                ContextMenu menu = new ContextMenu();
                MenuItem itemdelete = new MenuItem("delete");
                menu.getItems().add(itemdelete);
                setContextMenu(menu);
                itemdelete.setOnAction((ActionEvent event) -> {
                    deleteClient(item);
                });

            } else {
                setGraphic(null);
            }
        }

        final Label labelNAME = new Label("NaN");
        final Label labelIP = new Label("NaN");

        public ButtonCell() {
//            labelIP.setText(ip);
        }

    }

    public void setCountConnect(int number) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    numberConnect.setText(new String(("#" + number).getBytes("windows-1252"), "windows-1251"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void loading(final boolean visible) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressLoad.setVisible(visible);
            }
        });
    }

    private SequentialTransition animationScale(Node node, long millis) {
        ScaleTransition rt = new ScaleTransition(Duration.millis(millis), node);
        rt.setToX(1.5D);
        rt.setToY(1.5D);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        ScaleTransition rt2 = new ScaleTransition(Duration.millis(millis), node);
        rt2.setToX(1.0D);
        rt2.setToY(1.0D);
        rt2.setInterpolator(Interpolator.EASE_BOTH);
        SequentialTransition sequential = new SequentialTransition(rt, rt2);

        return sequential;
    }

    public RotateTransition animationRotate(Node node, long millis) {
        RotateTransition rt = new RotateTransition(Duration.millis(millis), node);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setByAngle(360);
        rt.setCycleCount(3);
        rt.setAxis(Rotate.Y_AXIS);
        rt.play();
        return rt;
    }

    public void updateDevice() {
//      *********************************AudioDevice************************************
        portTargetLine.clear();
        portSourceLine.clear();
        Mixer.Info[] mixer = AudioSystem.getMixerInfo();

        DataLine.Info targetLine = new DataLine.Info(TargetDataLine.class, ITargetDataLine.DefaultAudioFormat(true));
        DataLine.Info sourceLine = new DataLine.Info(SourceDataLine.class, targetLine.getFormats()[0]);

        String[] nameDeviceTarget = new String[mixer.length + 1];
        String[] nameDeviceSource = new String[mixer.length + 1];

        int targetC = 1, sourceC = 1;
        for (Mixer.Info in : mixer) {
            if (AudioSystem.getMixer(in).isLineSupported(targetLine)) {
                try {
                    portTargetLine.add(in);
                    nameDeviceTarget[targetC] = new String(in.getName().getBytes("windows-1252"), "windows-1251");
                    targetC++;
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (AudioSystem.getMixer(in).isLineSupported(sourceLine)) {
                try {
                    portSourceLine.add(in);
                    nameDeviceSource[sourceC] = new String(in.getName().getBytes("windows-1252"), "windows-1251");
                    sourceC++;
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AudioFrameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

//          **********************IN**********************
        nameDeviceTarget[0] = "IN - " + (targetC - 1);
        String[] deviceTarget = new String[targetC];
        System.arraycopy(nameDeviceTarget, 0, deviceTarget, 0, targetC);
        ObservableList listTarget = FXCollections.observableArrayList(deviceTarget);
        choiceboxaudioInput.setItems(listTarget);
        choiceboxaudioInput.getItems().add(1, new SeparatorMenuItem());
        choiceboxaudioInput.getSelectionModel().select(0);

//          *********************OUT*********************
        nameDeviceSource[0] = "OUT - " + (sourceC - 1);
        String[] deviceSource = new String[sourceC];
        System.arraycopy(nameDeviceSource, 0, deviceSource, 0, sourceC);
        ObservableList listSource = FXCollections.observableArrayList(deviceSource);
        choiceboxaudioOutput.setItems(listSource);
        choiceboxaudioOutput.getItems().add(1, new SeparatorMenuItem());
        choiceboxaudioOutput.getSelectionModel().select(0);
//      ********************************************************************************

//      *********************************Webcam*****************************************
        webCamsArray = Webcam.getWebcams();
        String cams[] = new String[webCamsArray.size() + 1];
        cams[0] = "Camera - " + (webCamsArray.size());
        int c = 1;
        for (Object obj : webCamsArray) {
            cams[c] = obj.toString();
            c++;
        }
        choiceboxcamera.setItems(FXCollections.observableArrayList(cams));
        choiceboxcamera.getItems().add(1, new SeparatorMenuItem());
        choiceboxcamera.getSelectionModel().select(0);
//      ********************************************************************************
        ObservableList<String> listIP = FXCollections.observableArrayList();
        Object[] arrayip = getIpAddress();

        for (Object obj : arrayip) {
            String ip = obj.toString();

            if (!"127.0.0.1".equals(ip) && !"0.0.0.0".equals(ip)) {
//                lblIPcomputed.setText(ip+":1488");
                listIP.add(ip + ":1488");
                System.out.println(ip);
            }
        }

        boxIPcomputed.setItems(listIP);
        for (Object obj : arrayip) {
            if (obj.toString().startsWith("192")) {
                boxIPcomputed.getSelectionModel().select(obj + ":1488");
//                lblIPcomputed.setText(obj+":1488");
            }
        }

        new Thread(() -> {
            String current_ip = getCurrentIP();
            if (current_ip != null && validate(current_ip)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        boxIPcomputed.getItems().add(current_ip + ":1488");
                    }
                });
            }
        }).start();

    }

    public static Object[] getIpAddress() {
        ArrayList<String> arrayip = new ArrayList<>();
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
//                System.out.println("networkInterface.displayName=" + networkInterface.getDisplayName());
//                System.out.println("networkInterface.name=" + networkInterface.getName());
                Enumeration inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
//                    System.out.println("networkInterface.[" + networkInterface.getName() + "].inetAddress=" + inetAddress);
                    String ip = inetAddress.getHostAddress();
                    if (validate(ip)) {
                        arrayip.add(ip);
                    }
                }
//                System.out.println("-----------------------------------");
            }
        } catch (SocketException ex) {
            return null;
        }
        return arrayip.toArray();
    }

    private static final Pattern PATTERN
            = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern PATTERN_NULL
            = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public static boolean validateNull(final String ip) {
        return PATTERN_NULL.matcher(ip).matches();
    }

    public static String getCurrentIP() {

        /*nslookup myip.opendns.com resolver1.opendns.com*/
        String ipAdressDns = "";
        try {
            String command = "nslookup myip.opendns.com resolver1.opendns.com";
            Process proc = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
                ipAdressDns += s + "\n";
            }
            int lastIndexOf = ipAdressDns.lastIndexOf("Address:") + "Address:".length();
            ipAdressDns = ipAdressDns.substring(lastIndexOf, ipAdressDns.length()).replace(" ", "").replace("\n", "");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ipAdressDns;
//        String result = null;
//        BufferedReader reader = null;
//        try {
//            URL url = new URL("http://myip.by/");
//            InputStream inputStream = null;
//            inputStream = url.openStream();
////                inputStream.wait(1000);
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder allText = new StringBuilder();
//            char[] buff = new char[1024];
//            
//            int count = 0;
//            while ((count = reader.read(buff)) != -1) {
//                allText.append(buff, 0, count);
//            }
//            // Строка содержащая IP имеет следующий вид
//            // <a href="whois.php?127.0.0.1">whois 127.0.0.1</a>
//            Integer indStart = allText.indexOf("\">whois ");
//            Integer indEnd = allText.indexOf("</a>", indStart);
//            
//            String ipAddress = allText.substring(indStart + 8, indEnd);
//            if (ipAddress.split("\\.").length == 4) { // минимальная (неполная) 
//                //проверка что выбранный текст является ip адресом.
//                result = ipAddress;
//            }
//        } catch (MalformedURLException ex) {
//        } catch (IOException ex) {
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException ex) {
//                }
//            }
//        }
//        return result;
    }

}
