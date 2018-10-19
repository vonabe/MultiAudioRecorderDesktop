package testClassNetQury;

import Network.UDPOld;

/**
 *
 * @author wenkael™
 */
public class audioout implements Runnable{
    
    private UDPOld list = null;
    final private Thread thread = new Thread(this);
    private boolean status = false;
    
    public audioout(UDPOld old) {list = old;status = true;thread.start();}
    
    public void close(){
        status = false;
        list.destroy();
    }
    
    @Override
    public void run() {
        while(status){
            byte[]buffer = list.receive();
            System.out.println("audioout: "+buffer);
        }
    }
    
    
}