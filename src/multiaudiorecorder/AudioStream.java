package multiaudiorecorder;

import Network.UDPOld;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import visualizator.SpectrumListener;

/**
 *
 * @author wenkael™
 */
public class AudioStream implements Runnable, MyOscilloscope.OscilloscopeEventHandler{

    private Thread          thread;
    private Visualizer  visualizer;
//    private SpectrumListener listener;
    private UDPOld             udp;
    private final ArrayList<UDPOld> list_udp = new ArrayList<>();
    private ITargetDataLine   line;
    private boolean status = false;
    private int            NpSSize, overlap;
    
//    private SourceDataLine           x = null;
    private JVMAudioInputStream    jvm = null;
    private AudioInputStream    stream = null;
    private AudioDispatcher dispatcher = null;
    
    public AudioStream(ITargetDataLine line, Visualizer visualizer, SpectrumListener list) {
        
//            this.listener = list;
//            this.NpSSize = NpS.AUDIO_SIZE_NORMAL;
            this.NpSSize = 2048;
            this.overlap = 0;
            this.visualizer = visualizer;
            this.line = line;
            
            this.thread = new Thread(this);
            this.udp = new UDPOld(1488);
            this.udp.setPackageSize(NpSSize);
            
            this.stream = new AudioInputStream(line.getTargetDataLine());
            this.jvm = new JVMAudioInputStream(stream);
            
            this.dispatcher = new AudioDispatcher(jvm, NpSSize, overlap);
            this.dispatcher.addAudioProcessor(new MyOscilloscope(this));
//            this.dispatcher.setStepSizeAndOverlap(NpSSize, 1024);
            
                    
            Thread thread0 = new Thread(this.dispatcher, "AudioDispatcher");
            thread0.setPriority(10);
            thread0.setDaemon(true);
            thread0.start();
            
//            final AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
////            final DataLine.Info dataLineInfo = new DataLine.Info(
////					SourceDataLine.class, format);
//            try {
//                x = (SourceDataLine) AudioSystem.getSourceDataLine(this.line.getFormat());
//                x.open(this.line.getFormat());
//                x.start();
//            } catch (LineUnavailableException ex) {
//                Logger.getLogger(AudioStream.class.getName()).log(Level.SEVERE, null, ex);
//            }
    }
    
    public boolean changeChannel(ITargetDataLine line){
        try {
            this.line.destroy();
            this.line = line;
            
            this.dispatcher.stop();
            this.stream.close();
            this.jvm.close();
            
            this.stream = new AudioInputStream(this.line.getTargetDataLine());
            this.jvm = new JVMAudioInputStream(stream);
            this.dispatcher = new AudioDispatcher(jvm, NpSSize, overlap);
            this.dispatcher.addAudioProcessor(new MyOscilloscope(this));
            
            Thread thread0 = new Thread(this.dispatcher, "AudioDispatcher");
            thread0.setPriority(10);
            thread0.setDaemon(true);
            thread0.start();
            
            return true;
        } catch (IOException ex) {
            Logger.getLogger(AudioStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean start(){
        this.status = true;
        if(this.udp.bind());
//            this.thread.start();
        else return false;
        
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(status){
                    
                    if(udp.transactionAccept()){
                        if(list_udp.size()>1){
                            for(UDPOld old : list_udp)
                                old.destroy();
                            list_udp.clear();
                        }
                        
                        byte[]bName = udp.gerReceiveBuff();
                        String name = parseName(bName);
                        
                        list_udp.add( new UDPOld().addAddress( udp.getAddress() ).setPackageSize(NpSSize) );
                        
                        AudioFrameController.getInstance().setCountConnect(list_udp.size());
                        AudioFrameController.getInstance().addClient(name, udp.getAddress().toString());
                    }
                }
            }
        }).start();
        
        return true;
    }
    
    public void destroy(){
        try {
            this.status = false;
            if(this.thread!=null)
                this.thread.interrupt();
            this.thread = null;
            if(this.line!=null)
                this.line.destroy();
            this.line = null;
//            if(this.visualizer!=null)
//                this.visualizer.destroy();
//            this.visualizer = null;
            if(this.dispatcher!=null)dispatcher.stop();
            dispatcher=null;
            if(this.udp!=null)
                udp.destroy();
            this.udp=null;
            list_udp.stream().forEach((old) -> {
                old.destroy();
            });
            this.list_udp.clear();
            if(jvm!=null)
                jvm.close();
            jvm=null;
            if(stream!=null)
                stream.close();
            stream=null;
            System.out.println("AudioStream destroy");
        } catch (IOException ex) {
            Logger.getLogger(AudioStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void handleEvent(float[] data, AudioEvent event) {
        
        visualizer.update(data);
        
        byte[] sendbuffer = event.getSourceDate();
//        System.out.println((sendbuffer==null)?sendbuffer:sendbuffer.length);
        
//        if(sendbuffer!=null)
//            x.write(sendbuffer, 0, sendbuffer.length);
        
//        System.out.println(Arrays.toString(sendbuffer));
        if(sendbuffer!=null && list_udp.size()>0)
            list_udp.stream().forEach((old) -> {
                old.send(sendbuffer);
            });
        
//        try{
//            Thread.sleep((long) MultiAudioRecorder.volume);
//        } catch (InterruptedException ex) {
//            System.err.println("AudioStream - error sleep thread");
//        }
        
    }
    
    @Override
    public void run() {
        
        while(status){
            
            try{
                
//                this.dispatcher.run();
                
                byte[] audiobuffer = new byte[this.NpSSize];
//                this.stream.read(audiobuffer);
//                  byte[] audiobuffer = this.line.get();
                this.jvm.read(audiobuffer, 0, audiobuffer.length);
                
                if(audiobuffer!=null && list_udp.size()>0)
                    list_udp.stream().forEach((old) -> {
                        old.send(audiobuffer);
                    });
                
//                System.out.println(audiobuffer);
//                x.write(audiobuffer, 0, audiobuffer.length);
                
//                x.flush();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    visualizer.add(audiobuffer, line.getFormat());
////                    listener.update(audiobuffer, line.getFormat());
//                }
//            }).start();
                
                try{
                    Thread.sleep((long) MultiAudioRecorder.volume);
                } catch (InterruptedException ex) {
                    System.err.println("AudioStream - error sleep thread");
                }
                
            } catch (IOException ex) {
                Logger.getLogger(AudioStream.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
//                if(this.visualizer!=null)this.visualizer.add(audiobuffer);
    
    private String parseName(byte[]buffer){
        JSONParser parse = new JSONParser();
        try {
            String message = new String(buffer).trim();
            System.out.println("message - "+message);
            JSONObject obj = (JSONObject)parse.parse(message);
            if(obj.containsKey("name")){
                return obj.get("name").toString();
            }
        } catch (ParseException ex) {return null;}
        return null;
    }
    
}
