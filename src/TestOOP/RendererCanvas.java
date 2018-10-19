package TestOOP;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author wenkael™
 */
public class RendererCanvas extends AVisualizer {

    private TimerTask                   task = null;
    private Timer                      timer = null;
    private Thread                    thread = null;
    private long                      period;
    private final InterfaceVisual visualizer;
    
    private boolean status = false;
    
    public RendererCanvas(InterfaceVisual visualizer) {
        this.visualizer = visualizer;
    }
    
    @Override
    public void start(long p)
    {
        this.period = p;
        this.renderer();
    }
    
    @Override
    public void stop() 
    {
        if(timer!=null)timer.cancel();
        if(thread!=null)thread.interrupt();
        timer = null;
        thread = null;
        status = false;
    }
    
    public boolean isRenderer()
    {
        return status;
    }
    
    @Override
    public void renderer() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        visualizer.renderer();
                    }
                };
                timer.schedule(task, 0L, period);
            }
        });
        thread.start();
        status = true;
    }

}
