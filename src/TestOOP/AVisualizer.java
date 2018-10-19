package TestOOP;

/**
 *
 * @author wenkael™
 */
public abstract class AVisualizer implements InterfaceVisual {
    
    @Override
    public abstract void renderer();
    
    public abstract void start(long period);
    public abstract void stop();
    
    
}