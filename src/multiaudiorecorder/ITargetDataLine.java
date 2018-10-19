package multiaudiorecorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author wenkael™
 */
public class ITargetDataLine {

//    private FloatControl          control;
    private TargetDataLine       dataLine;
    private AudioFormat            format;
    private Info                     info;
    private Port.Info            portinfo;
    
    public ITargetDataLine(AudioFormat form, Info in) throws LineUnavailableException {
        this.format = form;
        this.info   =   in;
        this.dataLine = AudioSystem.getTargetDataLine(format, info);
        
    }
    
    public ITargetDataLine(AudioFormat form, Port.Info in) throws LineUnavailableException {
        this.format   = form;
        this.portinfo =   in;
        this.dataLine = (TargetDataLine) AudioSystem.getLine(this.portinfo);
    }
    
    public boolean start()
    {
        try {
            this.dataLine.open(format, 8192);
            this.dataLine.start();            
            return true;
        } catch (LineUnavailableException ex) {
            return false;
        }
    }
    
    public TargetDataLine getTargetDataLine(){
        return dataLine;
    }
    
    public void setVolume(double value){
//        this.control.setValue((float) value);
    }
    
    public byte[] get(int size)
    {
        byte[] buffer = new byte[size];
        this.dataLine.read(buffer, 0, size);
        return buffer;
    }
    
    public int available(){
        return this.dataLine.available();
    }
    
    public AudioFormat getFormat()
    {
        return format;
    }
    
    public void destroy()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataLine.flush();
                dataLine.stop();
                dataLine.close();
                dataLine=null;
                format = null;
                info   = null;
            }
        }).start();
    }
    
    public boolean isActive()
    {
        return dataLine.isActive();
    }
    
    public static AudioFormat DefaultAudioFormat(boolean channel)
    {
		    float sampleRate = 44100.0F;
		    //8000,11025,16000,22050,44100
		    int sampleSizeInBits = 16;
		    //8,16
		    int channels = (channel==true) ? 2 : 1;
		    //1,2
		    boolean signed = true;
		    //true,false
		    boolean bigEndian = false;
		    //true,false
		    return new AudioFormat(
		                      sampleRate,
		                      sampleSizeInBits,
		                      channels,
		                      signed,
		                      bigEndian);
		  }//end getAudioFormat
    
}





//                          save file wave
//            byte[]buffer = new byte[1024*1024];
//            dataLine.read(buffer, 0, buffer.length);
//            InputStream in = new ByteArrayInputStream(buffer, 0, buffer.length);
//            
//            try {
//                AudioSystem.write(
//                        new AudioInputStream(in,getFormat(),buffer.length),
//                        AudioFileFormat.Type.WAVE,
//                        new File("C:\\Users\\wenkael™\\Desktop\\wave.wav"));
//            } catch (IOException ex) {
//                Logger.getLogger(Visualizer.class.getName()).log(Level.SEVERE, null, ex);
//            }