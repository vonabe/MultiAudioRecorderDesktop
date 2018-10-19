package testClassNetQury;

import Network.UDPOld;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author wenkael™
 */
public class audioin implements Runnable{
    
    final private Thread thread = new Thread(this);
    final private static ArrayList<UDPOld> list = new ArrayList();
    public static boolean status = false;
    
    public int sizeclients(){
        return list.size();
    }
    
    public audioin() {
        status = true;
        thread.start();
    }
    
    public void close(){
        status = false;
        list.clear();
    }
    
    public void addClient(UDPOld old){
        list.add(old);
    }
    
    @Override
    public void run() {
        while(status){
            Iterator<UDPOld> it = list.iterator();
            while(it.hasNext()){
                it.next().send(new byte[]{127,127,127,127,127,127,127,127,127});
            }
        }
    }
    
    
    
    
}
