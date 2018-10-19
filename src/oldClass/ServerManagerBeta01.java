package oldClass;

/**
 *
 * @author wenkael™
 */
public class ServerManagerBeta01 {
    
    private static SSLSocketBeta01 sslserver, sslclient;
    
    private static ServerManagerBeta01 instance = null;
    public ServerManagerBeta01() {
        instance = this;
    }
    
    public ServerManagerBeta01 getInstance(){
        return instance;
    }
    
    public boolean serverSSL(int port){
        sslserver = new SSLSocketBeta01(port);
        if(!sslserver.bind()){SSLServerDispose();return false;}else {return true;}
    }
    
    public boolean clientSSL(String ip, int port){
        if(isClient())SSLClientDispose();
        sslclient = new SSLSocketBeta01(ip, port);
        sslclient.setTimeout(5000);
        if(!sslclient.connect()){SSLClientDispose();return false;}    
        return true;
    }
    
    public void SSLServerDispose(){
        if(isServer())
            sslserver.dispose();
        sslserver = null;
    }
    
    public void SSLClientDispose(){
        if(isClient())
            sslclient.dispose();
        sslclient = null;
    }
    
    public boolean isClient(){
        if(sslclient!=null)return true;else return false;
    }
    
    public boolean isServer(){
        if(sslserver!=null)return true;else return false;
    }
    
    
public static boolean server = false, clien = false;
public static class ServerStream implements Runnable {
    
        @Override
        public void run() {
            while(server){
                sslserver.accept();
            }
        }
        
        
        class receive implements Runnable{
            
            @Override
            public void run() {
                while(server){
                    
                }
            }
        }
        
        class send implements Runnable{
            
            @Override
            public void run() {
                while(server){
                    
                }
            }
        }
        
}

}
