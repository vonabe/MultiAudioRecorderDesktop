package Network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPOld {
    
    private DatagramSocket      socket = null;
    private DatagramPacket	sendpacket = null;
    private DatagramPacket	recivepacket = null;
    private InetSocketAddress   socketAddress=null;
    private int                 PORT;
    private String                IP;
    final private int MAX_SIZE = 512;
    private ByteBuffer byteBuffer = null;
    private byte[] receiveAcceptBuffer = null;
    
    private static UDPOld instance;
    public static UDPOld getInstance()
    {
        return instance;
    }
    
    public UDPOld() {
    }
    
    
    public UDPOld(int port) {
        PORT = port;
        instance = this;
    }
    public boolean bind(){
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException ex) {
            System.err.println("ERROR PORT BUSY");
            return false;
        }
        return true;
    }
    
    public UDPOld(String ip, int port) {
        try {
            IP = ip;PORT = port;
            socketAddress = new InetSocketAddress(IP, PORT);
//            address = InetAddress.getByName(IP);

            socket = new DatagramSocket();
        } catch (SocketException e) {}
        instance = this;
    }
    
    public UDPOld setPackageSize(int size)
    {
        byteBuffer = ByteBuffer.allocate(size);
        return this;
    }
    
    public void add(String ip, int port)
    {
        try {
            IP=ip;PORT=port;
            socketAddress = new InetSocketAddress(ip, port);
            socket = new DatagramSocket();
            
//		try {
//                    address = InetAddress.getByName(IP);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
        } catch (SocketException ex) {
            Logger.getLogger(UDPOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
    public UDPOld addAddress(InetSocketAddress add){
        socketAddress = new InetSocketAddress(add.getAddress(), add.getPort());
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(UDPOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }
    
	public void setSoTimeOut(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
	}
        
        public InetSocketAddress getAddress(){
            return socketAddress;
        }
        
        public boolean send(byte[] buffer)
        {
            if(socketAddress==null)return false;
            try {
                if(buffer==null)return false;
                byte[] maxBuffer = new byte[MAX_SIZE];
                
                byte[] newBuffer = null;
                if(buffer.length<byteBuffer.limit()){
                    newBuffer = new byte[byteBuffer.limit()];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                }
                
                ByteArrayInputStream buffers = new ByteArrayInputStream((newBuffer==null)?buffer:newBuffer);
                int i;
                while((i = buffers.read(maxBuffer))!=-1){
                    sendpacket = new DatagramPacket(maxBuffer, i, socketAddress);
                    socket.send(sendpacket);
//                    try {
//                        Thread.sleep((long) MultiAudioRecorder.volume);
//                    } catch (InterruptedException ex) {Logger.getLogger(AudioStream.class.getName()).log(Level.SEVERE, null, ex);}
                
                }
                buffers.close();
//                sendpacket = new DatagramPacket(buffer, buffer.length, socketAddress);
//                socket.send(sendpacket);
//                System.out.println("packet_size: "+buffer.length);
                return true;
            } catch (IOException e) {return false;}
	}

    public byte[]gerReceiveBuff(){
        return receiveAcceptBuffer;
    }
        
    public boolean transactionAccept(){
        byte[] buffer = new byte[512];
        try{
            recivepacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(recivepacket);
            socketAddress = (InetSocketAddress) recivepacket.getSocketAddress();
            receiveAcceptBuffer = buffer;
            return true;
        } catch (IOException e) {return false;}
    }

    public boolean transactionConnect(){
        try {
            byte[]buffer = new byte[124];
            sendpacket = new DatagramPacket(buffer, buffer.length, socketAddress);
            socket.send(sendpacket);
            return true;
        } catch (IOException e) {return false;}
    }
    
    public InputStream getInputStream(){
        return new ByteArrayInputStream(receive());
    }
    
    public byte[] receive()
    {
        try {
            byte[] buffer = new byte[MAX_SIZE];
            while(byteBuffer.hasRemaining()){
                recivepacket = new DatagramPacket(buffer, MAX_SIZE);
                socket.receive(recivepacket);
                byteBuffer.put(buffer, 0, MAX_SIZE);
            }
            byte[] pac = byteBuffer.array();
            byteBuffer.clear();
            return pac;
        } catch (IOException e) {
            return null;
        }
    }
    public void destroy()
    {
        try{
            socket.close();
            socket = null;
            socketAddress = null;
            recivepacket = null;
            sendpacket = null;
        }catch(Exception ex){System.out.println("destroy udp error");}
    }
        
}
