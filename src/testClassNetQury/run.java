package testClassNetQury;

import Network.SOCKET;
import Network.SSLCompound;
import Network.ServerManager;
import java.util.Arrays;

/**
 *
 * @author wenkael™
 */
public class run {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
//        ServerManager servermanager = new ServerManager();
//        ManagerSettingFunction managerfunction = new ManagerSettingFunction();
//        
//        if(servermanager.TCPStart(6666)){
//            while(true){
//                servermanager.TCPAccept();
//            }
//        }
        
        /*23756,23757*/
        
//        ThreadGroup group = new ThreadGroup("serveandclient");
        Thread [] list = new Thread [2];
        
        list[0] = new Thread(new Runnable(){
            
            @Override
            public void run() {
                SOCKET socket = new SOCKET(5555);
                socket.bind();
                SSLCompound compound = socket.accept();
                compound.setTimeout(8000);
                byte[] receive = compound.receive(512);
                if(receive==null){
                    compound.dispose();
                    ServerManager.log("server dispose");
                }else{
                    System.out.println(Arrays.toString(receive));
                }
            }
        });
        
        list[1] = new Thread(new Runnable(){
            @Override
            public void run() {
                SOCKET socket = new SOCKET("localhost",5555);
                SSLCompound compound = socket.connect();
                compound.setTimeout(5000);
                byte[]send = new byte[512];
                Arrays.fill(send, (byte)512);
                compound.dispose();
            }
        });
        
        for(Thread t:list)t.start();
        
//        String [] iparray = new String[]{};
//        
//        UDPOld old0 = new UDPOld("7.111.80.36", 7707);
//        old0.setPackageSize(512);
////        UDPOld old1 = new UDPOld("7.58.45.55", 23757);
////        old1.setPackageSize(512);
//        
//        byte[] buffer = "$$??\n'PC?>´".getBytes();
////        Arrays.fill(buffer, (byte)0);
//        
//        while(true){
//            try {Thread.sleep(10);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(run.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            old0.send(buffer);
////            old1.send(buffer);
//        }
//        Broadcast broadcast = new Broadcast();
//        System.out.println(Arrays.toString(broadcast.search()));
//        System.out.println(broadcast.search());
        
    }
    
}
