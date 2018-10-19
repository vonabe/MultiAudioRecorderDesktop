package oldClass;

import Network.SOCKET;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import multiaudiorecorder.CanvasImg;

/**
 *
 * @author wenkael™
 */
public class Server implements Runnable {

//    private final UDP            udp;
    private final SOCKET      socket;
    private final Thread      thread;
    private boolean           status;
    private final CanvasImg renderer;
    
    public Server(CanvasImg img) {
//        udp = new UDP(1488);
        socket = new SOCKET(1488);
        thread = new Thread(this);
        renderer = img;
    }
    
    public void play()
    {
        status = true;
        thread.start();
    }
    
    public void stop()
    {
        status = false;
        thread.interrupt();
//        udp.destroy();
        
    }
    
    @Override
    public void run() {
        System.out.println("server wait...");
        socket.accept();
        System.out.println("connect: ");
        
        while(status)
        {
             int size = 11000;
             String inSize = null;
             try{
//                 inSize = new String(socket.receive(18)).trim();
                 inSize = inSize.substring(inSize.indexOf("[")+1, inSize.lastIndexOf("]"));
                 size = Integer.parseInt(inSize);
             }catch(Exception ex){System.out.println("debug: damaged file, size: "+inSize);}
             
             System.out.println("size: "+size);
             
//             byte[]data = socket.receive(size);
             byte[]data = null;
//             int[] rgb = new int[data.length];
//             decodeYUV420SP(rgb, data, 640, 480);
//             System.out.println("RGB: "+Arrays.toString(rgb));
                try
                {
                    BufferedImage imgBuffer = ImageIO.read(new ByteArrayInputStream(data));
                    renderer.paint(SwingFXUtils.toFXImage(imgBuffer, new WritableImage(300, 300)));
                } catch (Exception ex) {
//                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
    }
    
}
