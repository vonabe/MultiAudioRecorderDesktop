package multiaudiorecorder;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author wenkael™
 */
public class Compressor {
    
    final private float MIN = 0.0F, NORMAL = 0.5F, MAX = 1.0F;
    public static enum compression {
        MIN,NORAML,MAX
    }
    private float  compression = NORMAL;
    private ImageWriteParam parametr = null;
    private ImageWriter         writer = null;
    
    public Compressor() {
        Iterator it = ImageIO.getImageWritersByFormatName("jpeg");
        if (!it.hasNext())
            throw new IllegalStateException("No writers for jpeg?!");
        
        writer = (ImageWriter) it.next();
        parametr = writer.getDefaultWriteParam();
        parametr.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Needed see javadoc
        parametr.setCompressionQuality( compression ); // Highest quality
        
    }
    
    public float getCompressor(){
        return compression;
    }
    
    public void compress(float power){
        if(power>1.0F)power=1.0F;else if(power<0.0F)power=0.0F;
        compression = power;
        parametr.setCompressionQuality( compression ); // Highest quality
    }
    
    public void compress(compression c){
        switch(c){
            case MIN:
                compression = MIN;
                break;
            case NORAML:
                compression = NORMAL;
                break;
            case MAX:
                compression = MAX;
                break;
        }
        parametr.setCompressionQuality( compression ); // Highest quality
    }
    
    public void getCompress(RenderedImage rendered, ByteArrayOutputStream out){
        List thumbNails = null;
        IIOImage iioImage = new IIOImage(rendered, thumbNails, (IIOMetadata) null);
        ImageOutputStream imgout = null;
        try {
            imgout = ImageIO.createImageOutputStream(out);
            writer.setOutput(imgout);
            writer.write( (IIOMetadata) null, iioImage, parametr );
            imgout.flush();
            imgout.close();
        } catch (IOException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getCompress(RenderedImage rendered, ByteArrayOutputStream out, float copression){
        compress(copression);
        List thumbNails = null;
        IIOImage iioImage = new IIOImage(rendered, thumbNails, (IIOMetadata) null);
        ImageOutputStream imgout = null;
        try {
            imgout = ImageIO.createImageOutputStream(out);
            writer.setOutput(imgout);
            writer.write( (IIOMetadata) null, iioImage, parametr );
            imgout.flush();
            imgout.close();
        } catch (IOException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void dispose(){
        writer.dispose();
        writer = null;
        parametr = null;
        
    }
    
}
