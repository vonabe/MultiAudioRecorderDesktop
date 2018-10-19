package Network;

import java.util.ArrayList;

/**
 *
 * @author wenkael™
 */
public class ServerUDP implements Runnable {

    private UDPOld                                      udp;
    private Thread                                   thread;
    private ArrayList<byte[]>buffer_send=new ArrayList<>(20);
    private boolean status = false;
    
    public ServerUDP() {
        udp = new UDPOld(1488);
        udp.setPackageSize(NpS.IMAGE_SIZE_BIG);
    }
    
    public boolean connect(){
        if(!udp.bind())
        {
            thread = null;
            return false;
        }
        thread = new Thread(this);
        thread.start();
        return true;
    }
    
    public void send(byte[]buffer){
        if(status)buffer_send.add(buffer);
    }
    
    public void destroy(){
        status = false;
        buffer_send = null;
        if(udp!=null)
            udp.destroy();
        udp = null;
        if(thread!=null)
            thread.interrupt();
        thread = null;
        System.out.println("serverUDP destroy");
    }
    
    @Override
    public void run() {
        
        boolean connect = udp.transactionAccept();
        if(connect==false){
            status = false;
            System.out.println("connect distruct");
            return;
        }else {status = true;System.out.println("connect Successfull");}
        
        while(status)
        {
            for(int i=0;i<buffer_send.size()-1;i++)
            {
                byte[]buffer = buffer_send.get(i);
                if(buffer==null)break;
                udp.send(buffer);
                buffer_send.remove(i);
                buffer_send.trimToSize();
            }
        }
    }
    
}
