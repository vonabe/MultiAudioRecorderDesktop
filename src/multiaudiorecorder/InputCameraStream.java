package multiaudiorecorder;

import Network.SOCKET;
import Network.SSLCompound;
import oldClass.UDP;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.embed.swing.SwingFXUtils.toFXImage;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

/**
 *
 * @author wenkael™
 */
public class InputCameraStream implements Runnable {
    
    private UDP              udp;
    private SOCKET        socket;
//    private UDPOld           udp;
    private Thread        thread;
    private final CanvasImg  img;
    private boolean       status;
    private WritableImage writable;
    
    public InputCameraStream(ImageView view, Pane pane) {
        socket = new SOCKET(1488);
//        udp = new UDP(1488);
        img = new CanvasImg(view, pane);
        writable = new WritableImage(640, 480);
    }
    
    public boolean start(){
        if(socket.bind()){
//            if(udp.transactionAccept()){
//                udp.setPackageSize(NpS.IMAGE_SIZE_NORMAL);
//                udp.receive(124);
                status = true;
                thread = new Thread(this);
                thread.start();
                return true;
//            }
//            return false;
        }else{System.out.println("socket no bind");return false;}
    }
    
    public void destroy()
    {
        status = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(InputCameraStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        stop();
    }
    
    private void stop(){
        if(udp!=null)udp.destroy();
        udp = null;
        if(socket!=null)socket.dispose();
        socket=null;
        if(thread!=null)thread.interrupt();
        thread = null;
        System.out.println("STOP");
    }
    
    private int fps = 0;
    @Override
    public void run() {
        
        TimerTask updateFPS = new TimerTask() {
            @Override
            public void run() {
                img.setFPS(fps);
                fps = 0;
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(updateFPS, 1000, 1000);
        
        SSLCompound compound = socket.accept();
        while(status)
        {
            try {
//                byte[] buffer = udp.receive(50000);
                byte[] buffer = compound.receive(50000);
                
                if(buffer!=null){
                    
                    BufferedImage bufferedIMG = ImageIO.read(new ByteArrayInputStream(buffer));
                    
                    writable = new WritableImage(640, 480);
                    toFXImage(bufferedIMG, writable);
                    img.paint(writable);
//                    array(buffer);
                    fps++;
                }else System.out.println("image buffer null");
            } catch (Exception ex) {
//                Logger.getLogger(InputCameraStream.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error image: "+ex+", size: ");
            }
            
        }
        timer.cancel();
        compound.dispose();
    }
    
    private void array(byte[]array){
        int i = 0;
        for(byte b : array)
            System.out.println(i+++": "+b);
        
//        int   inc = 0;
//        byte[] a   = new byte[100];
//        
//        for(int i = 0; i < array.length/100; i++){
//            System.arraycopy( array, (i==0)?0:i+100, a, 0, a.length );
//            System.out.println(Arrays.toString(a));
//        }
        
    }
    
    private void isZero(byte[] array){
        
        int zero = 0;
        int oneZeroPosition = 0;
        int inc = 0;
        
        for(byte b : array){
            if(b==0)zero++;
            if( zero > 0 && oneZeroPosition == 0 )oneZeroPosition=inc;
            inc++;
        }
        System.out.println("count zero - "+zero+"\n onezeropos - "+oneZeroPosition+"\nsize - "+array.length);
        
    }
    
}
