package TESTTCPIP;

import Network.SOCKET;
import Network.SSLCompound;
import java.util.Scanner;

/**
 *
 * @author wenkael™
 */
public class mp4 {
    
    
    
    public static void main(String[] args) {
        String path = "192.168.0.1";
        int port = 80;
        
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("PORT - ");
//        port = scanner.nextInt();
        
        SOCKET socket = null;
        SSLCompound connected = null;
        try{
        socket = new SOCKET(path, port);
        connected = socket.connect();
        
        String GET1 = 
                "Host: 192.168.0.1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:47.0) Gecko/20100101 Firefox/47.0\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n";


        String GET =
                "GET HTTP/1.1 User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:41.0) Gecko/20100101 Firefox/41.0 Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8 Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3 Accept-Encoding: gzip, deflate Connection: keep-alive";
        
        
        connected.send(GET1.getBytes());
        
        
        byte[] buffer = connected.receive(1024);
        String message = new String(buffer);
        System.out.println("receive - "+message);
        
        } catch (Exception ex){
            System.err.println("error - "+ex.getMessage());
            if(socket!=null)socket.dispose();
            if(connected!=null)connected.dispose();
        }
//        System.out.println("connected: "+connected);

//        int strok;
//        byte[]array = socket.receive(1024);
//        String text = new String(array).trim();
        
//        for(int i=0; i<text.length(); i++)
//        {
//            System.out.print(text.substring(i, i+1));
//            if(i%100==0)System.out.println("");
//        }

        
    }
    
}
