package testClassNetQury;

import Network.UDPOld;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author wenkael™
 */
public class desktopstream implements Runnable{

    final private Thread thread = new Thread(this);
    final private ArrayList<UDPOld> list = new ArrayList<>();
    private boolean status = false;
    
    public int clientsize(){
        return list.size();
    }
    
    public desktopstream() {status = true;thread.start();}
    
    public void close(){
        status = false;
        list.clear();
    }
    
    public void addClient(UDPOld old){
        list.add(old);
    }
    
    public void removeClient(UDPOld old){
        list.remove(old);
    }
    
    @Override
    public void run() {
        while(status){
            Iterator<UDPOld> it = list.iterator();
            while(it.hasNext()){
                it.next().send(new byte[]{127,127,127,127,127,127,127,127,127,127});
            }
        }
    }
    
}