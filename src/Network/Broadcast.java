package Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author wenkael
 */
public class Broadcast implements Runnable {
    
    final private Thread thread = new Thread(this);
    
    private DatagramSocket    socket;
    private DatagramPacket    sendpacket, receiverpacket;
    private InetSocketAddress address;
    private ArrayList<String> searchip = new ArrayList<>();
    
    private final int[] poolport = new int[]{5555,1333,1555,9999,1919};
    private int    PORT = -1;
    
    public Broadcast() {
    }
    
    public boolean bind(){
        for(int port : poolport){
            try {
                socket = new DatagramSocket(port);
                thread.start();
                return true;
            } catch (SocketException ex) {System.err.println("port bussy");}
        }
        return false;
    }
    
    public boolean push(){
        return true;
    }
    
    public Object[] search(){
        
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(1500);
        } catch (SocketException ex) {System.err.println("error search");}
        
        byte[]buffer = new byte[56];
        Arrays.fill(buffer, (byte)0);
        
        Iterator<Integer> it = Arrays.stream(poolport).iterator();
        while(it.hasNext()){
            int port = it.next();
            address = new InetSocketAddress("255.255.255.255", port);
            
            sendpacket = new DatagramPacket(buffer, buffer.length, address);
            try { socket.send(sendpacket); } catch (IOException ex) {System.err.println("error send");}
            
            receiverpacket = new DatagramPacket(buffer, buffer.length);
            try { socket.receive(receiverpacket); } catch (IOException ex) {System.err.println("broadcast search timeout");receiverpacket = null;}
            if(receiverpacket!=null){
                String ip = receiverpacket.getAddress().getHostName();
            if(ip!=null)
                if(!searchip.contains(ip))searchip.add(ip);
                System.out.println("my ip:port: "+socket.getLocalPort()+" > ip --- "+ip);
            }
        }
        return searchip.toArray();
    }
    
    @Override
    public void run() {
        while(true){
            try {
                
                byte[] receive = new byte[56];
                receiverpacket = new DatagramPacket(receive, receive.length);
                socket.receive(receiverpacket);
                
                System.out.println("ping --- "+receiverpacket.getAddress().getHostName());
                
                sendpacket = new DatagramPacket(receive, receive.length, receiverpacket.getSocketAddress());
                for(int i=0;i<3;i++)socket.send(sendpacket);
                
                searchip.add(receiverpacket.getAddress().getHostName());
            } catch (IOException ex) {
                System.err.println("broatcast server error receive "+ex);
            }
        }
    }
    
}