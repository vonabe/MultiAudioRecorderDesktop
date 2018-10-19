package multiaudiorecorder;

import Network.SOCKET;
import Network.SSLCompound;
import Network.ServerUDP;
import Network.UDPOld;
import TestOOP.InterfaceVisual;
import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import oldClass.UDP;

/**
 *
 * @author wenkael™
 */
public class WebCamera implements InterfaceVisual, Runnable {
    
    private boolean               pause = false;
    private Webcam               webcam = null;
    private BufferedImage  grabbedImage = null;
    private ImageView              view = null;
    private Thread               thread = null;
    private EncodingVideoRecord  record = null;
    private ServerUDP            server = null;
    
    private UDP                    udp0 = null;
    private UDPOld                  udp = null;
    private SOCKET               socket = null;
    private SSLCompound        compound = null;
    
    
    public void init(final Webcam cam, final ImageView image)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    AudioFrameController.getInstance().loading(true);
                    webcam = cam;
                    webcam.setViewSize(webcam.getViewSizes()[2]);
                    webcam.open();
                    view = image;
                    
//                    udp0 = new UDP("192.168.0.101", 1488);
                    
//                    udp = new UDPOld("192.168.0.101", 1488);
//                    socket = new SOCKET("192.168.0.100", 1488);
                    socket = new SOCKET(1488);
                    socket.bind();
                    compound = socket.accept();
//                    compound = socket.connect();
                    if(compound==null){destroy();System.out.println("no connect!");return;}
                    
//                    server = new ServerUDP();
//                    if(!server.connect()){server=null;}
                    thread.start();
                    AudioFrameController.getInstance().loading(false);
                }catch(Exception ex){AudioFrameController.getInstance().loading(false);}
            }
        }).start();
        thread = new Thread(this);
    }
    
    public String[] getDevice()
    {
        return (String[])Webcam.getWebcams().toArray();
    }
    
//                    if(record!=null && renderer.isRenderer())record.setBuffer(grabbedImage);
//                    if(renderer==null)view.setImage(null);
    
    @Override
    public void run() {
        while(webcam!=null){
         if ((grabbedImage = webcam.getImage()) != null && !pause) {
            if(grabbedImage!=null){
                final WritableImage img = SwingFXUtils.toFXImage(grabbedImage, null);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.setImage(img);
                    }
                });
//            if(server!=null||socket.isConnect()){
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ImageIO.write(grabbedImage, "jpg", output);
                    send(output.toByteArray());
                } catch (IOException ex) {
                    Logger.getLogger(WebCamera.class.getName()).log(Level.SEVERE, null, ex);
                }
//            }
            grabbedImage.flush();
            }
         }
        }
    }
    
    private void send(byte[]buffer){
        if(udp0!=null){
            udp0.sendFree(buffer);
        }else{
            compound.send(buffer);
        }
    }
    
    @Override
    public void renderer() {
        
        if ((grabbedImage = webcam.getImage()) != null && !pause) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    view.setImage(SwingFXUtils.toFXImage(grabbedImage, null));
//                    if(record!=null && renderer.isRenderer())record.setBuffer(grabbedImage);
//                    if(renderer==null)view.setImage(null);
                    if(server!=null){
                        try {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            ImageIO.write(grabbedImage, "jpg", output);
                            server.send(output.toByteArray());
                        } catch (IOException ex) {
                            Logger.getLogger(WebCamera.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            grabbedImage.flush();
        }
    }
    
    public void unpause()
    {
        pause = false;
    }
    
    public void pause()
    {
        pause = true;
    }
    
    public void destroy(){
        try{
            if(udp!=null){udp.destroy();udp=null;}
            if(webcam.isOpen()){webcam.close();webcam = null;}
            if(record!=null){record.stop();record=null;}
            if(server!=null){server.destroy();server=null;}
            System.out.println("destroy webcamera");
        }catch(Exception ex){System.out.println("warning destroy WebCamera.class: "+ex);}
    }
    
}
