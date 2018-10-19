package multiaudiorecorder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author wenkael™
 */
public class ISourceDataLine {
    
    private SourceDataLine dataLine = null;
    private AudioFormat    format   = null;
    private Info           info     = null;
    
    public ISourceDataLine(AudioFormat form, Info in)
    {
        try {
            this.format = form;
            this.info   =   in;
            this.dataLine = AudioSystem.getSourceDataLine(format, info);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(ISourceDataLine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean set(byte[] input)
    {
        if(input!=null){
            dataLine.write(input, 0, input.length);
            
        }else return false;
        return true;
    }
    
    public boolean start()
    {
        try {
            this.dataLine.open(format);
            this.dataLine.start();
            return true;
        } catch (LineUnavailableException ex) {
            Logger.getLogger(ISourceDataLine.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void destroy()
    {
        dataLine.stop();
        dataLine.close();
        dataLine = null;
        info   = null;
        format = null;
    }

    public AudioFormat getFormat() {
        return this.format;
    }
    
    
}
