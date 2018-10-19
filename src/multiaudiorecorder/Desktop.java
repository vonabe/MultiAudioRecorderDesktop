package multiaudiorecorder;

import Network.SOCKET;
import Network.SSLCompound;
import Network.UDPOld;
import TestOOP.Close;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import oldClass.UDP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import res.ControllerRectangle;

/**
 *
 * @author wenkael™
 */
public class Desktop implements Runnable, Close {

//    private RendererCanvas renderer;
    private WindowDialog     dialog;
    private Thread           thread, thread_receive;
    private ImageView         image;
    private Robot             robot;
    private Rectangle          rect;
    private BufferedImage    buffer;
    private int            zoom = 1;
    
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private Compressor compressor = new Compressor();
    
    private boolean              status;
    private static SSLCompound compound;
    private static SOCKET        socket;
    
    private static UDP             udp;
    private static UDPOld       udpold;
    
    private static final Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    
    public Desktop(ImageView img) {
        
        rect = new Rectangle(screen_size);
        thread = new Thread(this);
        
        dialog = new WindowDialog(this);
        dialog.show();
        image = img;
        
        try {
            status = true;
            robot = new Robot();
            thread.start();
            
        } catch (AWTException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void setPosition(int x, int y)
    {
        rect.setLocation(x, y);
    }
    
    
    @Override
    public void run() {
        
        
//        File fileHTML = new File("C:\\Users\\wenkael™\\Desktop\\imageUpdate.html");
//        FileInputStream html = null;
//        try {
//            html = new FileInputStream(fileHTML);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
//        }
//             
//            byte[] bit = new byte[(int)fileHTML.length()];
//            try {
//                html.read(bit,0,bit.length);
//            } catch (IOException ex) {
//                Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
//            }
        
        
//        ***********************************************************************************************************
//        status = false;
//        int port0 = 1488, port1 = 13011;
//        UDPOld udp = new UDPOld(port0);
//        
//        while(!status)
//        {
////            System.out.println("wait");
//            byte[]b = udp.receive(26048);
//            try {
//                buffer = ImageIO.read(new ByteArrayInputStream(b));
//            } catch (IOException ex) {
//                Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
//                buffer = null;
//            }
//             Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(buffer!=null)image.setImage(SwingFXUtils.toFXImage(buffer, null));
//                    }
//             });
////            System.out.println("buffer: "+buffer.length);
//        }
//        ***********************************************************************************************************
        
        
//        socket = new SOCKET("192.168.0.101",13011);
//        if(!socket.connect()){System.out.println("no connect ");status = false;}
//        socket.accept();
//*****************************************************************************************
        socket = new SOCKET(1488);
        if(socket.bind()){
                compound = socket.accept(); 
        }else {System.err.println("desktop - error socket");close();return;}
//*****************************************************************************************
//*****************************************************************************************
//        udp = new UDP(1488);
//        udp.bind();
//        udp.receive(100);
//        udpold = new UDPOld("localhost", 8888);
//        udpold.setPackageSize(999_936);
//*****************************************************************************************
        
        
//        if(!socket.accept()){System.err.println("desktop - error socket");return;}
        
//        udp = new UDPOld(1488);
//        if(!udp.bind()){ System.out.println("bind null"); return; }
//        udp.setPackageSize(NpS.IMAGE_SIZE_BIG);
//        if(!udp.transactionAccept()){System.out.println("transaction null");return;}
        
        status = true;
        
        ControllerRectangle.getInstance().animation();
        receive();
        
//        if(!udp.bind()){status=false;return;}
//        byte[]b = udp.receive(512);
//        System.out.println("connect: "+new String(b));
        
        while(status){
            
//            byte[] responce = socket.receive(1024);
//            String strResponce = new String(responce).trim();
//            System.out.println("responce: \n"+strResponce);
            
//            socket.send(bit);
//            socket.dispose();
//            String request = new String(
//                    "HTTP/1.1 200 OK\r\n" +
//                    "Content-Type	images/jpg\r\n" +
//                    "Content-Length	%1$s\r\n"+
//                    "\r\n");
////            
//            String format = String.format(request, 1024);
//            System.out.println("packeg: \r\n"+format);
              
              
//            while(status){
                
                rect.setBounds(dialog.getRectangle());
                buffer = robot.createScreenCapture(rect);
                
                Platform.runLater(() -> {
                    if(dialog != null)image.setImage(SwingFXUtils.toFXImage(buffer, null));
                });
                
                if(MultiAudioRecorder.volume != compressor.getCompressor())
                    compressor.compress((float) MultiAudioRecorder.volume);
                compressor.getCompress(buffer, output);
                
                try {
                    send(output.toByteArray());
//                    System.out.println("length: "+output.toByteArray().length);
//                    compound.send(output.toByteArray());
//                    udpold.send(output.toByteArray());
                    output.flush();
                    output.reset();
                } catch (IOException ex) {
                    System.out.println("error image parse byte array");
                }
                if(buffer!=null)buffer.flush();
                
            }
    }
    
    private void send(byte[]array){
        if(udp!=null){
            udp.send(array);
        }else{
            boolean ret = compound.send(array);
            if(!ret)close();
        }
    }
    
    public void receive(){
        
        this.thread_receive = new Thread(new Runnable(){
            
            @Override
            public void run() {
                
                while(status){
                    
                byte[] buffer = compound.receive(124);
                if(buffer==null)return;
                String receive = new String(buffer);
                
                if(receive != null){
                    
                    JSONArray  array = null;
                    JSONObject    a1 = null;
                    Object     value = null;
                    try{
                        value = JSONValue.parse(receive.trim());
                        array = (JSONArray) value;
                        a1    = (JSONObject)array.get(1);
                    }catch(Exception e){System.err.println("parse error");}
                    
                    if(array != null && a1 != null)
                        if(array.get(0).equals("move")){
                    
                    float x = 0, y = 0;
                    try{
                        String sX = String.valueOf(a1.get("x"));
                        String sY = String.valueOf(a1.get("y"));
                        
                        sX = sX.substring(0, (sX.length()>=5)?5:3);
                        sY = sY.substring(0, (sY.length()>=5)?5:3);
                        
                        x = Float.parseFloat(sX);
                        y = Float.parseFloat(sY);
                    }catch(Exception e){}
                    
                    dialog.setPositionFrame(dialog.getRectangle().x, dialog.getRectangle().y, true);
                    float targetX = dialog.getRectangle().x, targetY = dialog.getRectangle().y;
                    float fx = x, fy = y;
                    
                        Animation animation = new Transition() {
                        {
                            setCycleDuration(Duration.millis(1000));
                        }
                        @Override
                        protected void interpolate(double frac) {
                            dialog.setPositionFrame(
                                targetX+(fx*frac),
                                targetY+(fy*frac),
                            false);
                        }
                    };
                    animation.play();
                    
                }else if(array.get(0).equals("zoom")){
                    String Sfactor = a1.get("factor").toString();
                    float factor = Float.parseFloat(Sfactor);
                    Animation animation = new Transition() {
                        {
                            setCycleDuration(Duration.millis(100));
                        }
                        @Override
                        protected void interpolate(double frac) {
//                            sX = sX.substring(0, (frac.length()>=5)?5:3);
                            frac = new BigDecimal(frac).setScale(2, RoundingMode.UP).doubleValue();
                            dialog.setResize( ((factor>1.0F)?1:-1) * (float) frac);
                        }
                    };
                    animation.play();
//                    dialog.setResize(factor);
                    
                }
                
                }
                
//                    System.out.println("\n---------------------------");
//                    
//                    if(a0 instanceof JSONArray)System.out.println("JSONArray");
//                    else if(a0 instanceof JSONObject)System.out.println("JSONObject");
//                    else if(a0 instanceof JSONValue)System.out.println("JSONValue");
//                    else if(a0 instanceof JSONParser)System.out.println("JSONParser");
//                    else if(a0 instanceof JSONAware)System.out.println("JSONAware");
//                    else System.out.println("null");
//                    
//                    System.out.println("---------------------------");
//                    
//                    if(a1 instanceof JSONArray)System.out.println("JSONArray");
//                    else if(a1 instanceof JSONObject)System.out.println("JSONObject");
//                    else if(a1 instanceof JSONValue)System.out.println("JSONValue");
//                    else if(a1 instanceof JSONParser)System.out.println("JSONParser");
//                    else if(a1 instanceof JSONAware)System.out.println("JSONAware");
//                    else System.out.println("null");
//                    
//                    System.out.println("\n---------------------------");
//                    System.out.println(a0);
//                    System.out.println(((JSONObject)a1).get("x")+";"+((JSONObject)a1).get("y"));
//                    System.out.println("---------------------------");
                }
            }
        });
        this.thread_receive.start();
    }
    
    @Override
    public void close() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                status = false;
                if(thread_receive!=null)thread_receive.stop();
                if(thread!=null)thread.stop();
                if(dialog!=null)dialog.close();
                try {
                    if(output!=null)output.close();
                    if(compressor!=null)compressor.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(udp!=null)udp.destroy();
                if(udpold!=null)udpold.destroy();
                if(compound!=null)compound.dispose();
                if(socket!=null)socket.dispose();
                
                socket   = null;
                udp      = null;
                udpold   = null;
                compound = null;
                
                dialog = null;
                robot  = null;
                image  = null;
                buffer = null;
                compressor = null;
                output = null;
                
                thread = null;
                thread_receive = null;
                
                MultiAudioRecorder.repaint();
                System.out.println("STOP DESKTOP");
            }
        });
    }
    
}