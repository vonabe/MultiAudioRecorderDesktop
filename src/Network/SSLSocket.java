package Network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
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
public class SSLSocket {

    private boolean SSLContextStatus               = false;
    private SSLServerSocketFactory sslServerSocketFactory = null;
    private SSLSocketFactory       sslSocketFactory = null;
    private SSLServerSocket         sslserversocket = null;
    private SSLCompound             compound = null;
    private final int                   PORT;
    private String                        IP;
    
    private SSLSocket Instance = null;
    public SSLSocket getInstance(){
        return Instance;
    }
//                          |
//                          |
//                          |
//                          |
//  *********************SERVER*********************
    public SSLSocket(int port) {
        this.PORT = port;
        createSSLContextServer();
        Instance = this;
    }
    
    private void createSSLContextServer(){
        try {
            // Починить экземпляр хранилища ключей.
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File file = new File("./src/JKS/ServerSSLStore");
//            URI uri = null;
//            try {
//                uri = getClass().getClassLoader().getResource("./JKS/ClientSSLStore").toURI();
//            } catch (Exception ex) {
//                System.out.println("error file ssl");
//                return;
//            }
//            System.err.println("File***************** "+uri);
            
//            File file = new File("./JKS/ClientSSLStore");
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
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (IOException ex) {
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        }
    }
    
    public boolean bind(){
        if(!SSLContextStatus)return false;
        try {
            sslserversocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
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
            Socket socket = sslserversocket.accept();
            compound = new SSLCompound(socket);
            if(compound.create()){
                System.out.println("SV:Connect! " + socket.getInetAddress());
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
//  *********************SERVER*********************
//                          |
//                          |
//                          |
//                          |
    public SSLCompound getClient(){
        return compound;
    }
//                          |
//                          |
//                          |
//                          |
//  *********************CLIENT*********************
    public SSLSocket(String ip, int port){
        this.IP = ip;this.PORT = port;
        createSSLContextClient();
        Instance = this;
    }
    
    private void createSSLContextClient(){
        try {
            // Пол�?чить экземпляр хранилища ключей.
            KeyStore keyStore = KeyStore.getInstance("JKS");
            
            File file = new File("./src/JKS/ClientSSLStore");
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
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        } catch (IOException ex) {
            Logger.getLogger(SSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            SSLContextStatus = false;
        }
    }
    
    public boolean connect(){
        if(!SSLContextStatus)return false;
        try {
            Socket socket = sslSocketFactory.createSocket(IP, PORT);
            compound = new SSLCompound(socket);
            if(compound.create()){
                System.out.println("CL:Connect! "+socket.getInetAddress());
                return true;
            } else return false;
        } catch (IOException ex) {
            return false;
        }
    }
//  *********************CLIENT*********************
//                          |
//                          |
//                          |
//                          |
    public void dispose(){
        try {
            SSLContextStatus = false;
            if(sslServerSocketFactory!=null)sslServerSocketFactory = null;
            if(sslSocketFactory!=null)sslSocketFactory = null;
            if(sslserversocket!=null)sslserversocket.close();
        } catch (IOException ex){
        }finally{
            sslserversocket = null;
        }
        
    }
    
}
