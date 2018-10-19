package multiaudiorecorder;

import Network.NpS;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author wenkael™
 */
public class Visualizer {
//    private Thread                           thread;

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;
    private static double[] mPointsX, mPointsY;
//    ******************************************************
    private final float SAMPLE_RATE = 44100;
//    private float                           SCREEN_WIDTH = 250;
    private static final int LINE_BUFF_SIZE = 1024;
    private static final int READ_BUFF_SIZE = LINE_BUFF_SIZE / 2;
    private static final int SAMPLE_SIZE = READ_BUFF_SIZE / 2;
    private final float[] dataBuff = new float[512];
    private float samplesPerPix;
    private int sampSize;
    private boolean status;
//    ******************************************************
//    private final RendererCanvas                      renderer;

//    private ArrayList<byte[]> buffers = new ArrayList<>();
    public Visualizer(Canvas canvas) {
        this.canvas = canvas;
        System.out.println("CreateVisualizer");
//        this.renderer = new RendererCanvas(this);
        this.graphicsContext = canvas.getGraphicsContext2D();

        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.REFLECT, new Stop(0.0, Color.WHITE),
                new Stop(0.5, Color.BLUE),
                new Stop(1.0, Color.RED));
//        this.graphicsContext.setStroke(Color.rgb(255, 0, 0, 1.0D));
        this.graphicsContext.setStroke(lg);

//        this.graphicsContext.setLineCap( StrokeLineCap.ROUND );
//        this.graphicsContext.setLineJoin( StrokeLineJoin.ROUND );
//        this.graphicsContext.setLineWidth( 1 );
//        
//        BoxBlur blur = new BoxBlur();
//        blur.setWidth(1);
//        blur.setHeight(1);
//        blur.setIterations(1);
//        this.graphicsContext.setEffect(blur);
//        this.thread = new Thread(this);
    }

//    public void play()
//    {
//        status = true;
//        this.renderer.start(150L);
//        this.thread.start();
//    }
//    public void destroy(){
////        status = false;
////        if(thread!=null)
////            thread.interrupt();
////        thread = null;
////        if(renderer!=null && renderer.isRenderer())
////                this.renderer.stop();
//        
////        System.out.println("visualizer destroy");
//    }
//    @Override
    public void run() {
        while (status) {
//            for(int f=0;f<buffers.size()-1;f++){
//            System.out.println("buff: "+buff);

            byte[] buffer = null;

            for (int i = 0; i < buffer.length; i += 2) {
                dataBuff[i / 2] = ((buffer[i] & 0xFF) | (buffer[i + 1] << 8)) / 32768.0F;
            }

            writable = new WritableImage((int) canvas.getWidth() * 4, (int) canvas.getHeight() * 4);
            PixelWriter pixel = writable.getPixelWriter();
            double width = 0;
            double inc = (writable.getWidth() / 4 / dataBuff.length);
            for (int i = 0; i < dataBuff.length; i++) {
                double x = width;
                int y = 125 - (int) (dataBuff[i] * 255);
                if (x < 0) {
                    x = -x;
                }
                if (y < 0) {
                    y = -y;
                }
                pixel.setColor((int) x, y, Color.AQUAMARINE);
                pixel.setColor((int) x, y + 1, Color.BLUE);
                pixel.setColor((int) x + 1, y + 1, Color.BLUE);
                pixel.setColor((int) x + 1, y, Color.BLUE);
                width += inc;
            }
            drawScreen();
            writable.cancel();
        }
//                buffers.clear();
//                buffers.trimToSize();
//        }
    }

    public void update(float[] buffer) {

        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double halfHeight = height / 2D;

//            double[] aX = new double[buffer.length];
//            double[] aY = new double[buffer.length];
//            int incX = 0, incY = 0;
        Platform.runLater(() -> {

            graphicsContext.clearRect(0D, 0D, width, height);
//                graphicsContext.setStroke(Color.rgb(255, 0, 0, 1.0D));

            graphicsContext.beginPath();
            for (int i = 0; i < buffer.length; i += 4) {
                double x = (buffer[i] * width);
                double y = (halfHeight - buffer[i + 1] * height);

                double x1 = (buffer[i + 2] * width);
                double y1 = (halfHeight - buffer[i + 3] * height);

//                    graphicsContext.setLineDashes(x,y,x1,y1);
//                    graphicsContext.strokeLine(x, y, x1, y1);
                this.graphicsContext.moveTo(x, y);
                this.graphicsContext.lineTo(x1, y1);

                //                aX[incX]=x; incX++;
                //                aX[incX]=x1;incX++;
                //                
                //                aY[incY]=y; incY++;
                //                aY[incY]=y1;incY++;
            }
            this.graphicsContext.closePath();
            this.graphicsContext.stroke();

        });

//            for(int i=0; i < buffer.length ; i+=4){
//                graphicsContext.strokeLine(
//                        (int)( buffer[i] * width),  (int)( halfHeight - buffer[i+1] * height),
//                        (int)( buffer[i+2] * width),(int)( halfHeight - buffer[i+3] * height)
//                );
//            }
    }

    public void add(byte[] buffer, AudioFormat format) {
//        buffers.add(buffer.clone());

        float[] samples = new float[buffer.length];
        SimpleAudioConversion.unpack(buffer, samples, buffer.length, format);

        if (buffer == null) {
            System.out.println("buffer null");
            return;
        }
        buffer = Arrays.copyOf(buffer, NpS.AUDIO_SIZE_MIN);

        for (int i = 0; i < buffer.length; i += 2) {
            dataBuff[i / 2] = ((buffer[i] & 0xFF) | (buffer[i + 1] << 8)) / 32768.0F;
        }
        writable = new WritableImage((int) canvas.getWidth() * 4, (int) canvas.getHeight() * 3);
        PixelWriter pixel = writable.getPixelWriter();
        double width = 0;
        double inc = (writable.getWidth() / 4 / dataBuff.length);
        for (int i = 0; i < dataBuff.length; i++) {
            double x = width;
            int y = 125 - (int) (dataBuff[i] * 255);
            if (x < 0) {
                x = -x;
            }
            if (y < 0) {
                y = -y;
            }
            pixel.setColor((int) x, y, Color.AQUAMARINE);
            pixel.setColor((int) x, y + 1, Color.BLUE);
            pixel.setColor((int) x + 1, y + 1, Color.BLUE);
            pixel.setColor((int) x + 1, y, Color.BLUE);
            width += inc;
        }
        drawScreen();
        writable.cancel();

    }

//                samplesPerPix = (float)((40e-3 * SAMPLE_RATE)/canvas.getWidth());
//                sampSize = (int)(40e-3 * SAMPLE_RATE);
//                for (int i = 0; i < SAMPLE_SIZE; i++) dataBuff[i] = 0.0f;
//                    byte inBuffer[] = new byte[READ_BUFF_SIZE];
//                    int  buffSize = inBuffer.length;
//                    int bytesAvailable = dataLine.available();
//                    if (bytesAvailable >= READ_BUFF_SIZE)
//                    {
//                        inBuffer = dataLine.get(inBuffer.length);
//                        // Convert  to floating point
//                        for(int i = 0; i < buffSize; i += 2)
//                            dataBuff[i/2] = ((inBuffer[i] & 0xFF)|(inBuffer[i + 1] << 8)) / 32768.0F;
//                        
//                    }
    // Find trigger point
//                        int trig = 0;
//                        float trigLevel = 0.05f;
//                        float upperTrigLevel = trigLevel + 0.02f;
//                        float lowerTrigLevel = trigLevel - 0.005f;
//
//                        for (int i = 1; i < SAMPLE_SIZE - 1; i++)
//                        {
//                            // If signal crosses the trigger point
//                            if ((dataBuff[i] >= lowerTrigLevel) &&
//                                    (dataBuff[i] <= upperTrigLevel))
//                            {
//                                // Check slope
//                                if ((dataBuff[i-1] < dataBuff[i]) &&
//                                        (dataBuff[i+1] > dataBuff[i]))
//                                {
//                                    trig = i;
//                                }
//                            }
//                            if (trig != 0) break;
//                        }
    //                mPointsX = new double[sampSize];
    //                mPointsY = new double[sampSize];
    //                for(int width = 0;width<(int)writable.getWidth();width++){
    //                    for(int height = 0;height<(int)writable.getHeight();height++){
    ////                        pixel.setColor(width, height, new Color(random, random, random, random));
    ////                            mPointsX[width]=width;
    ////                            mPointsY[height]=rand(writable.getHeight(), 0);
    //                    }
    //                }
//                        byte buffer[] = dataLine.get(1024);
    public static double rand(double max, double min) {
        return (Math.random() * (max - min) + min);
    }

    private WritableImage writable;

    private void drawScreen() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                graphicsContext.clearRect(0, 0, writable.getWidth(), writable.getHeight());
                graphicsContext.drawImage(writable, 0.0D, 0.0D);
            }
        });
    }

    public void renderer() {
//        if(buffers.size()<=0)return;

//        int iter=0;
//        while(!buffers.isEmpty()){
//        for(int iter=0;iter<buffers.size()-1;iter++){
//        byte[]buffer = buffers.get(iter);
//        if(buffer==null){System.out.println("buffer null");return;}
//        
//        for(int i = 0; i < buffer.length; i += 2){
//            dataBuff[i/2] = ((buffer[i] & 0xFF)|(buffer[i + 1] << 8)) / 32768.0F;
//        }
//        writable = new WritableImage((int)canvas.getWidth()*4, (int)canvas.getHeight()*3);
//        PixelWriter pixel = writable.getPixelWriter();
//        double width = 0;
//        double inc = (writable.getWidth()/4/dataBuff.length);
//        for (int i = 0; i < dataBuff.length; i++)
//        {
//            double x = width;
//            int y = 125 - (int)(dataBuff[i] * 255);
//            if(x<0)x=-x;if(y<0)y=-y;
//            pixel.setColor((int)x,   y, Color.AQUAMARINE);
//            pixel.setColor((int)x,   y+1,   Color.RED);
//            pixel.setColor((int)x+1, y+1, Color.GREEN);
//            pixel.setColor((int)x+1, y, Color.BLUE);
//            width+=inc;
//        }
//            drawScreen();
//            writable.cancel();
////            buffers.remove(iter);
////            buffers.trimToSize();
//        }
//        
//        buffers.clear();
//        buffers.trimToSize();
//        
//        System.out.println("buffers size: "+buffers.size());
    }

}
