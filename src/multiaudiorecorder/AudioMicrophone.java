package multiaudiorecorder;

import Network.UDPOld;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Mixer.Info;

/**
 *
 * @author wenkael™
 */
public class AudioMicrophone implements Runnable, MyOscilloscope.OscilloscopeEventHandler{

    private UDPOld                     udp;
    private Visualizer          visualizer;
    private ISourceDataLine sourceDataLine;
    private ITargetDataLine targetDataLine;
    
    private AudioInputStream stream;
    private JVMAudioInputStream jvm;
    private AudioDispatcher dispatcher;
    static private InputStream input;
    private MyOscilloscope oscilloscope;
    
    private Thread thread = new Thread(this);
    private boolean status = false;
    
    public AudioMicrophone(ISourceDataLine sourceDataLine, Visualizer visualizer, Info info) {
        this.sourceDataLine = sourceDataLine;
//        try {
//            this.targetDataLine = new ITargetDataLine(ITargetDataLine.DefaultAudioFormat(false), info);
//            this.targetDataLine.start();
//        } catch (LineUnavailableException ex) {
//            Logger.getLogger(AudioMicrophone.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        this.visualizer = visualizer;
        this.udp = new UDPOld(1488);
        this.udp.setPackageSize(1024);
        
//        this.oscilloscope = new MyOscilloscope(this);
        
    }
    
    public boolean start(){
        status = true;
        if(udp.bind()){
            this.thread.start();
            return true;
        }
        else
        {
            status = false;
            return false;
        }
    }
    
    public void destroy(){
        status = false;
        if(this.thread!=null)this.thread.interrupt();
        this.thread = null;
        if(this.sourceDataLine!=null)this.sourceDataLine.destroy();
        this.sourceDataLine = null;
//        if(this.visualizer!=null)this.visualizer.destroy();
//        this.visualizer = null;
        if(this.udp!=null)this.udp.destroy();
        this.udp = null;
        if(targetDataLine!=null)targetDataLine.destroy();
        targetDataLine = null;
        System.out.println("AudioMicrophone destroy");
    }
    
    @Override
    public void handleEvent(float[] data, AudioEvent event) {
//        if(this.visualizer!=null)this.visualizer.update(data);
        byte[] source = event.getSourceDate();
        if(source!=null)this.sourceDataLine.set(source);
//        System.out.println("handler - "+source+" : "+data);
        
    }
    
    @Override
    public void run() {
//        byte[] connect = this.udp.receive();

        System.out.println("accept audiomicrophone");
        boolean connect = this.udp.transactionAccept();
        if(!connect){System.out.println("accept destroy");return;}
        
//        this.input = new ByteArrayInputStream(this.udp.receive());

//        ByteArrayBuffer buf = new ByteArrayBuffer(1024);
//        buf.write(this.udp.receive(), 0, 1024);
        
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
////                input = new ByteArrayInputStream(udp.receive());
////                stream = new AudioInputStream(input, sourceDataLine.getFormat(), 1024L);
////                jvm = new JVMAudioInputStream(stream);
////                dispatcher = new AudioDispatcher(jvm, 1024, 0);
////                dispatcher.addAudioProcessor(oscilloscope);
//        
////                Thread thread0 = new Thread(dispatcher, "dispatcher");
////                thread0.setPriority(10);
////                thread0.setDaemon(true);
////                thread0.start();
//            }
//        });
        
//        System.out.println(("connect: "+connect));
        while(status){
//            this.input = new ByteArrayInputStream(this.udp.receive());
//              byte[] array = this.udp.receive();
//              this.input = new ByteArrayInputStream(array);
//              System.out.println("read");
            byte[]buffer = this.udp.receive();
            if(buffer!=null)this.sourceDataLine.set(buffer);
//            this.sourceDataLine.set(this.targetDataLine.get(1024));
//            float[]samples = new float[1024];
//            int i = SimpleAudioConversion.unpack(buffer, samples, samples.length, this.sourceDataLine.getFormat());
//            System.out.println(i);
//            if(this.visualizer!=null)this.visualizer.update(samples);
        }
        
    }
    
}
