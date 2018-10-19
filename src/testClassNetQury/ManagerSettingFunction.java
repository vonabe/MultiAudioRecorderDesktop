package testClassNetQury;

import Network.UDPOld;
import java.util.ArrayList;

/**
 *
 * @author wenkael™
 */
public class ManagerSettingFunction {
    
    public static enum Type {
        listaudioin,listaudioout,listdesktopstream
    }
    
    final private ArrayList<audioout>      listaudioout      = new ArrayList();
    final private ArrayList<audioin>       listaudioin       = new ArrayList();
    final private ArrayList<desktopstream> listdesktopstream = new ArrayList();
    
    public ManagerSettingFunction() {}
    
    public int size(Type T){
        int ret = -1;
        switch(T){
            case listaudioin:
                ret = listaudioin.get(0).sizeclients();
                break;
            case listaudioout:
                ret = listaudioout.size();
                break;
            case listdesktopstream:
                ret = listdesktopstream.get(0).clientsize();
                break;
        }
        return ret;
    }
    
    public boolean remove(Type T, UDPOld old){
        switch(T){
            case listaudioin:
                return listaudioin.remove(old);
            case listaudioout:
                return listaudioout.remove(old);
            case listdesktopstream:
                return listdesktopstream.remove(old);
        }
        return false;
    }
    
    public boolean addAudioOut(UDPOld old){
        listaudioout.add(new audioout(old));
        return true;
    }
    
    public boolean addAudioIn(UDPOld old){
        if(listaudioin.size()>0){
            listaudioin.get(0).addClient(old);
            return true;
        }else if(listaudioin.size()<1){
            listaudioin.add(new audioin());
            listaudioin.get(0).addClient(old);
            return true;
        }else return false;
    }
    
    public boolean addDesktopStream(UDPOld old){
        if(listdesktopstream.size()>0){
            listdesktopstream.get(0).addClient(old);
            return true;
        }else if(listdesktopstream.size()<1){
            listdesktopstream.add(new desktopstream());
            listdesktopstream.get(0).addClient(old);
            return true;
        }else return false;
    }
    
}
