package Network;

import Network.ParseTJson.Type;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author wenkael
 */
public class SSLClientQuery implements Runnable {
    
    final public static short maskaudioinsetting  = 0x0001;
    final public static short maskaudiooutsetting = 0x0002;
    final public static short maskdesktopstream   = 0x0004;
    final public static short maskoutcamerastream = 0x0008;
    
    private SSLCompound client = null;
    private String       reply = null;
    private final Thread thread = new Thread(this);
    
    public String name = "united";
    
    public short maskqury = -1;
    private static enum QUERY {
        AUDIOINSETTING,AUDIOOUTSETTING,DESKTOPSTREAM,OUTPUTCAMERASTREAM
    }
    
    public SSLClientQuery(SSLCompound compound) {
        this.client = compound;
        this.thread.start();
    }
    
    @Override
    public void run() {
        client.create();
        if(client.status())ServerManager.log("!CLIENT!");
        
        while(client.status()){
            byte[]buffer = client.receive(1024);
            if(buffer!=null){
                parse(buffer);
                if(!client.send(reply.getBytes()))close();
            }else{ServerManager.log("CLIENT disconnect");close();}
        }
    }
    
    private boolean parse(byte[]b){
        String read = null;
        try {
            read = new String(b).trim();
            JSONParser parse = new JSONParser();
            Object obj0 = parse.parse(read);
            if(ParseTJson.isType(obj0)==ParseTJson.Type.OBJECT){
                JSONObject jobj = ParseTJson.toObject(obj0);
                String namepackage = jobj.keySet().iterator().next().toString();
                switch(namepackage){
                    case "clientname":
                        return p_client_name(jobj);
                    case "audioinstream":
                        break;
                }
                
                Object obj1 = jobj.get(namepackage);
                if(ParseTJson.isType(obj1)==ParseTJson.Type.ARRAY){
                    JSONArray array0 = ParseTJson.toArray(obj1);
                    Object aobj0 = array0.get(0);
                    
                    if(ParseTJson.isType(aobj0)==Type.OBJECT){
                        JSONObject obj2 = ParseTJson.toObject(aobj0);
                        Iterator it = obj2.keySet().iterator();
                        
                        if(obj2.size()==0){
                            ServerManager.log("not elements array");
                            reply = "not elements array";
                        }
                        
                        while(it.hasNext()){
                            
                            Object obj3 = it.next();
                            if(ParseTJson.isType(obj3)==Type.STRING){
///////////////////////////////////////////FINAL/////////////////////////////////////////////////////
                                
                                ServerManager.log("info: name:"+obj3.toString()+";param:"+obj2.get(obj3));
                                reply = "Successfully";
                                
///////////////////////////////////////////FINAL/////////////////////////////////////////////////////
                            }else{ ServerManager.logError("no string type"); reply = "no string type";}
                            
                        }
                        
                    }else {ServerManager.logError("parametr 2 array no object");reply = "parametr 2 array no object";}
                }else {ServerManager.logError("parametr 1 no array");reply = "parametr 1 no array";}
            }else {ServerManager.logError("parametr 0 no object");reply = "parametr 0 no object";}
        } catch (ParseException ex) {
            ServerManager.logError("ERROR Not Parse type package - "+read);
            reply = "ERROR Not Parse type package - "+read+"; "+ex;
            return false;
        }
        return true;
    }
    
    
    private boolean p_client_name(JSONObject obj){
        Object obj1 = obj.get("clientname");
        if(ParseTJson.isType(obj1)==ParseTJson.Type.ARRAY){
            JSONArray array0 = ParseTJson.toArray(obj1);
            Object aobj0 = array0.get(0);
            if(ParseTJson.isType(aobj0)==Type.OBJECT){
                JSONObject obj2 = ParseTJson.toObject(aobj0);
                Iterator it = obj2.keySet().iterator();
                if(obj2.size()==0){
                    ServerManager.log("not elements array");
                    reply           = "not elements array";
                }
                while(it.hasNext()){
                    Object obj3 = it.next();
                    if(ParseTJson.isType(obj3)==Type.STRING){
                        name = obj2.get(obj3).toString();
                    }
                }
            }else {return false;}
        }else{return false;}
        return true;
    }
    
    private boolean p_audioinstream(){
        
        return true;
    }
    
    public void close(){
        ServerManager.getInstance().delete_client(this);
        client.dispose();
        reply = null;
    }
    
}
