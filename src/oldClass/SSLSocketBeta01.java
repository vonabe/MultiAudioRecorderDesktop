package oldClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author wenkael™
 */
public class SSLSocketBeta01 {

    private boolean SSLContextStatus               = false;
    private SSLServerSocketFactory sslServerSocketFactory = null;
    private SSLSocketFactory       sslSocketFactory = null;
    private SSLServerSocket         sslserversocket = null;
    private Socket                           socket = null;
    
    private final int                   PORT;
    private String                        IP;
    
    private InputStream         input = null;
    private OutputStream       output = null;
    
    private SSLSocketBeta01     Instance;
    public SSLSocketBeta01 getInstance(){
        return Instance;
    }
    
//  *********************SERVER*********************
    public SSLSocketBeta01(int port) {
        this.PORT = port;
        createSSLContextServer();
        Instance = this;
    }
    
    private void createSSLContextServer(){
        try {
            // Пол�?чить экземпляр хранилища ключей.
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File file = new File("./src/JKS/ServerSSLStore");
            
            FileInputStream fis = new FileInputStream(file);
            keyStore.load(fis, "nFnmzb9R2H".toCharArray());
            
            // Пол�?чить ди�?петчеры ключей базовой реализации для заданного хранилища ключей.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "8u7LAMdR".toCharArray());
            KeyManager [] keyManagers = keyManagerFactory.getKeyManagers();
            
            // Пол�?чить доверенные ди�?петчеры базовой реализации.
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager [] trustManagers = trustManagerFactory.getTrustManagers();
            
            // Пол�?чить защищенное �?л�?чайное чи�?ло.
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            
            // Создание SSL контек�?та
            SSLContext sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(keyManagers, trustManagers, secureRandom);
            
            // Создание фабрики SSL �?окетов.
            sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLContextStatus = true;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | NoSuchProviderException | KeyManagementException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (IOException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        }
    }
    
    public void setTimeout(int time){
        try {
            sslserversocket.setSoTimeout(time);
        } catch (SocketException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean bind(){
        if(!SSLContextStatus)return false;
        try {
            sslserversocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
            sslserversocket.setNeedClientAuth(true);
//            sslserversocket.setNeedClientAuth(true);
        } catch (IOException ex) {
            System.err.println("Error PORT BUSY");
            return false;
        }
        return true;
    }
    
    public boolean accept(){
        if(!SSLContextStatus)return false;
        try {
            System.out.println("wait..."+PORT);
            socket = sslserversocket.accept();
            input = socket.getInputStream();
            output = socket.getOutputStream();
            System.out.println("SV:Connect! " + socket.getInetAddress());
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
//  *********************SERVER*********************

//  *********************CLIENT*********************
    public SSLSocketBeta01(String ip, int port){
        this.IP = ip;this.PORT = port;
        createSSLContextClient();
        Instance = this;
    }
    
    private void createSSLContextClient(){
        try {
            // Пол�?чить экземпляр хранилища ключей.
            KeyStore keyStore = KeyStore.getInstance("JKS");
            
            File file = new File(getClass().getClassLoader().getResource("./JKS/ClientSSLStore").toURI());
            FileInputStream fis = new FileInputStream(file);
            keyStore.load(fis, "nFnmzb9R2H".toCharArray());
            
            // Пол�?чить ди�?петчеры ключей базовой реализации для заданного хранилища ключей.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "8u7LAMdR".toCharArray());
            KeyManager [] keyManagers = keyManagerFactory.getKeyManagers();
            
            // Пол�?чить доверенные ди�?петчеры базовой реализации.
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager [] trustManagers = trustManagerFactory.getTrustManagers();
            
            // Пол�?чить защищенное �?л�?чайное чи�?ло.
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            
            // Создание SSL контек�?та
            SSLContext sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(keyManagers, trustManagers, secureRandom);
            
            // Создание фабрики SSL �?окетов.
            sslSocketFactory = sslContext.getSocketFactory();
            SSLContextStatus = true;
            
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | NoSuchProviderException | KeyManagementException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (IOException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (URISyntaxException ex) {
            Logger.getLogger(SSLSocketBeta01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean connect(){
        if(!SSLContextStatus)return false;
        try {
            socket = sslSocketFactory.createSocket(IP, PORT);
            input = socket.getInputStream();
            output = socket.getOutputStream();
            System.out.println("CL:Connect! "+socket.getInetAddress());
        return true;
        } catch (IOException ex) {
            return false;
        }
    }
//  *********************CLIENT*********************

    public boolean send(byte[]buffer){
        try {
            output.write(buffer, 0, buffer.length);
            output.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public byte[] receive(int size){
        byte[]buffer = new byte[size];
        try {
            input.read(buffer, 0, size);
            return buffer;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public void dispose(){
        try {
            SSLContextStatus = false;
            if(sslServerSocketFactory!=null)sslServerSocketFactory = null;
            if(sslSocketFactory!=null)sslSocketFactory = null;
            if(input!=null)input.close();
            if(output!=null)output.close();
            if(socket!=null)socket.close();
            if(sslserversocket!=null)sslserversocket.close();
        } catch (IOException ex){
        }finally{
            input = null;
            output = null;
            socket = null;
            sslserversocket = null;
        }
        
    }
    
}
