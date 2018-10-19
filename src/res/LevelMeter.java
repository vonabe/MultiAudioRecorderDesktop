package res;

/**
 *
 * @author wenkael™
 */
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.border.EmptyBorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import multiaudiorecorder.SimpleAudioConversion;

public class LevelMeter extends JComponent {
    
    private int meterWidth = 10;
    
    private byte[] mBuffer = new byte[2048];
    private final float amp  [] = new float[1024];
    private final float peak [] = new float[1024];
    
    
    public void setAmplitude(float amp, int i) {
        this.amp[i] = Math.abs(amp);
//        repaint();
    }

    public void setPeak(float peak, int i) {
        this.peak[i] = Math.abs(peak);
//        repaint();
    }

    public void setMeterWidth(int meterWidth) {
        this.meterWidth = meterWidth;
    }
    
    public void repeat(){
        super.repaint();
    }
    
    private float[]   mPoints=null;
    private Rectangle mRect = new Rectangle();
    
    @Override
    protected void paintComponent(Graphics g) {
//        int w = Math.min(meterWidth, getWidth());
//        int h = getHeight();
//        int x = w / 2;
//        int y = 0;
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (mPoints == null || mPoints.length < mBuffer.length * 4) {
            mPoints = new float[mBuffer.length * 4];
        }
        
        mRect.setBounds(0, 0, getWidth(), getHeight());
        
        for (int i = 0; i <mBuffer.length - 1; i++) {
            
            float x = mPoints[i * 4] = (float) (mRect.getWidth() * i / (mBuffer.length - 1));
            float y = mPoints[i * 4 + 1] = (float) (mRect.getHeight()/ 2
                    + ((byte) (mBuffer[i] + 128)) * (mRect.getHeight() / 2) / 128);
            
            float x1 = mPoints[i * 4 + 2] = (float) (mRect.getWidth() * (i + 1) / (mBuffer.length - 1));
            float y1 = mPoints[i * 4 + 3] = (float) (mRect.getHeight() / 2
                    + ((byte) (mBuffer[i + 1] + 128)) * (mRect.getHeight() / 2) / 128);
            
            g.setColor(Color.RED);
            g.drawLine(
                    (int)x,(int)y,
                    (int)x1,(int)y1
            );
            
//            System.out.println(x+","+y+":"+x1+","+y1);
        }
        
        for(int i=0;i<1024;i++);
        
//        for(int i=0;i<1024;i++){
//            g.setColor(Color.WHITE);
//            g.fillRect(x, y, w, h);
//
//            g.setColor(Color.BLACK);
//            g.drawRect(x, y, w - 1, h - 1);
//
//            int a = Math.round(amp[i] * (h - 2));
//            g.setColor(Color.PINK);
//            g.fillRect(x + 1, y + h - 1 - a, w - 2, a);
//
//            int p = Math.round(peak[i] * (h - 2));
//            g.setColor(Color.RED);
//            g.drawLine(x + 1, y + h - 1 - p, x + w - 1, y + h - 1 - p);
//            x+=10;
//        }
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension min = super.getMinimumSize();
        if(min.width < meterWidth)
            min.width = meterWidth;
        if(min.height < meterWidth)
            min.height = meterWidth;
        return min;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        pref.width = meterWidth;
        return pref;
    }

    @Override
    public void setPreferredSize(Dimension pref) {
        super.setPreferredSize(pref);
        setMeterWidth(pref.width);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Meter");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel content = new JPanel(new BorderLayout());
                content.setBorder(new EmptyBorder(25, 50, 25, 50));

                LevelMeter meter = new LevelMeter();
                meter.setPreferredSize(new Dimension(9, 100));
                content.add(meter, BorderLayout.CENTER);

                frame.setContentPane(content);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                new Thread(new Recorder(meter)).start();
            }
        });
    }

    static class Recorder implements Runnable {
        final LevelMeter meter;

        Recorder(final LevelMeter meter) {
            this.meter = meter;
        }

        @Override
        public void run() {
            AudioFormat fmt = new AudioFormat(44100f, 16, 2, true, false);
            final int bufferByteSize = 2048;
            Mixer.Info info[] = AudioSystem.getMixerInfo();
            
            TargetDataLine line;
            int sel = 6;
            try {
                System.out.println(info[sel]);
                line = AudioSystem.getTargetDataLine(fmt, info[sel]);
                line.open(fmt, bufferByteSize);
                
            } catch(LineUnavailableException e) {
                System.err.println(e);
                return;
            }

            byte[]  buf = new byte[bufferByteSize];
            float[] samples = new float[bufferByteSize / 2];

            float lastPeak = 0f;

            line.start();
            for(int b; (b = line.read(buf, 0, buf.length)) > -1;) {
                meter.mBuffer = new byte[bufferByteSize];
                
                float[] sampless = new float[2048];
                SimpleAudioConversion.unpack(buf, sampless, buf.length/8, line.getFormat());
                
                // convert bytes to samples here
                for(int i = 0, s = 0; i < b;) {
                    int sample = 0;

                    sample |= buf[i++] & 0xFF; // (reverse these two lines
                    sample |= buf[i++] << 8;   //  if the format is big endian)
                    
                    // normalize to range of +/-1.0f
                    samples[s] = sample / 32768f;
                    s++;
                }

                int inc = 0, xInc = 0;
                float rms = 0f;
                float peak = 0f;
                for(float sample : samples) {

                    float abs = Math.abs(sample);
                    if(abs > peak) {
                        peak = abs;
                    }

                    rms += sample * sample;
                    rms = (float)Math.sqrt(rms / samples.length);
                
                    if(lastPeak > peak) {
                        peak = lastPeak * 0.875f;
                    }

                    lastPeak = peak;
                    
                    meter.mBuffer[xInc++]=(byte) rms;
                    meter.mBuffer[xInc++]=(byte) peak;
                    
                    setMeterOnEDT(rms, peak, inc);
                    inc++;
                }
                meter.repeat();
//                System.out.println("inc - "+inc);
                
            }
        }
        
        void setMeterOnEDT(final float rms, final float peak, int i) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    meter.setAmplitude(rms, i);
                    meter.setPeak(peak, i);
                }
            });
        }
        
    }
}