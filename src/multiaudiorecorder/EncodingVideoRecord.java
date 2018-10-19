package multiaudiorecorder;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcodec.api.awt.SequenceEncoder;

/**
 *
 * @author wenkael™
 */
public class EncodingVideoRecord implements Runnable{

    private SequenceEncoder     encoder;
    private Thread               thread;
    private boolean            status_encoder;
    private ArrayList<BufferedImage>   buffer;
    
    public EncodingVideoRecord() {
        thread = new Thread(this);
        buffer = new ArrayList<>(10);
    }
    
    public void setBuffer(BufferedImage  buffered)
    {
        if(buffered != null){buffer.add(buffered);}
    }
    
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    @Override
    public void run() {
        while(status_encoder)
        {
            for(int i=0;i<buffer.size()-1;i++){
                try {
                    BufferedImage img = buffer.get(i);
                    if(img!=null){
                        encoder.encodeImage(buffer.get(i));
                        buffer.remove(i);
                    }
                } catch (IOException ex) {System.out.println("Error bufferedImage Encoding ->>\n"+encoder);}
            }
        }
    }
    
    public boolean start(File file)
    {
        try {
            encoder = new SequenceEncoder(file);
            status_encoder=true;
            thread.start();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(EncodingVideoRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void stop()
    {
        try {
            status_encoder=false;
            if(buffer!=null) buffer.clear();     buffer = null;
            if(thread!=null) thread.interrupt(); thread = null;
            if(encoder!=null)encoder.finish();  encoder = null;
        } catch (IOException ex) {
            Logger.getLogger(EncodingVideoRecord.class.getName()).log(Level.SEVERE, null, ex);
            encoder = null;
        }
        
    }
    
}
