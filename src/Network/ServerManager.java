package Network;

import java.util.ArrayList;

/**
 *
 * @author wenkael™
 */
public class ServerManager {
    
    private SOCKET tcpsocket = null;
    private SSLSocket    ssl = null;
    private final ArrayList<SSLClientQuery> arraylistclientquery = new ArrayList<>();
    
    private static ServerManager instance = null;
    public ServerManager() {
        instance = this;
    }
    
    public static ServerManager getInstance(){
        return instance;
    }
    
    public boolean SSLServer(int port){
        ssl = new SSLSocket(port);
        if(ssl.bind()) {return true;}else{ logError("port no bind"); ssl.dispose(); ssl=null; return false;}
    }
    
    public boolean SSLConnect(String ip, int port){
        ssl = new SSLSocket(ip, port);
        if(ssl.connect()){
            SSLCompound compound = ssl.getClient();
            if(null!=compound)
            {
                arraylistclientquery.add(new SSLClientQuery(compound));
                log("add compound size "+arraylistclientquery.size());
            }
            return true;
        }else{ logError("error connect "+ip+":"+port); return false; }
    }
    
    public boolean SSLAccept(){
        if(ssl!=null){
            if(ssl.accept()){
                SSLCompound compound = ssl.getClient();
                if(null!=compound)
                {
                    arraylistclientquery.add(new SSLClientQuery(compound));
                    log("add compound size "+arraylistclientquery.size());
                }
            }else { logError("error accept");return false;}
        }else { logError("error ssl null");return false;}
        return true;
    }
    
    public boolean TCPConnect(String ip, int port){
        tcpsocket = new SOCKET(ip, port);
            SSLCompound c = tcpsocket.connect();
            if(c!=null){
                arraylistclientquery.add(new SSLClientQuery(c));
                log("add compound size "+arraylistclientquery.size());
            }else { logError("error null client");return false;}
        return true;
    }
    
    public boolean TCPStart(int port){
        tcpsocket = new SOCKET(port);
        return tcpsocket.bind();
    }
    
    public boolean TCPAccept(){
        if(tcpsocket!=null){
                SSLCompound c = tcpsocket.accept();
                if(c!=null){
                    arraylistclientquery.add(new SSLClientQuery(c));
                    log("add compound size "+arraylistclientquery.size());
                    return true;
                }else { logError("error null client");return false;}
        }else { logError("error tcp null");return false;}
    }
    
    
    public SSLClientQuery getConnect(){
        return arraylistclientquery.get(arraylistclientquery.size()-1);
    }
    
    public static void logError(Object msg){
        System.err.println(msg);
    }
    
    public static void log(Object msg){
        System.out.println(msg);
    }
    
    public void delete_client(SSLClientQuery query){
        if(arraylistclientquery.remove(query))log("remove client :: size:"+arraylistclientquery.size());else logError("error remove client, not this client");
    }
    
    public void resetConnects(){
        for(SSLClientQuery c : arraylistclientquery)
            c.close();
        arraylistclientquery.clear();
    }
    
    public void disposeServer(){
        if(ssl!=null)ssl.dispose();ssl = null;
        tcpsocket = null;
        
        for(SSLClientQuery c : arraylistclientquery)
            c.close();
        arraylistclientquery.clear();
    }
    
}
