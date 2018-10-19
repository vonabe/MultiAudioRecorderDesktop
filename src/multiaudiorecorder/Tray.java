package multiaudiorecorder;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;

/**
 *
 * @author wenkael™
 */
public class Tray {

    private static TrayIcon   icon  = null;
    private static SystemTray tray  = null;
    private static BufferedImage image = null;
    private static PopupMenu  popmenu = null;
    public static InterfaceTray interace = null;
    
    public Tray(InterfaceTray in) {
        interace = in;
        if(SystemTray.isSupported()){
            tray = SystemTray.getSystemTray();
            
            image = SwingFXUtils.fromFXImage(new javafx.scene.image.Image("res/ic_launcher_web.png"), null);
            popmenu = new PopupMenu();
            
            MenuItem item1 = new MenuItem("blablabla");
            item1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(item1.getLabel());
                }
            });
            popmenu.add(item1);
            
            MenuItem item = new MenuItem("exit");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exit();
                }
            });
            popmenu.add(item);
            
            
            int trayIconWidth = tray.getTrayIconSize().width;
            icon = new TrayIcon(image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "MultiAudioRecorder v1.0", popmenu);
            
            icon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    show();
                }
            });
            icon.setImageAutoSize(true);
            
            try {
                tray.add(icon);
            } catch (AWTException ex) {
                Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
            }
            message("","Hello "+System.getProperties().getProperty("user.name")+"!",TrayIcon.MessageType.INFO);
            
        }
        
    }
    
    public static void hide(){
        if(interace!=null)interace.hide();
    }
    
    public static void show(){
        if(interace!=null)interace.show();
    }
    
    public static void exit(){
        if(interace!=null)interace.exit();
    }
    
    public static void message(Object title, Object message, TrayIcon.MessageType type){
        try {
            icon.displayMessage(new String(title.toString().getBytes("windows-1252"),"windows-1251"), new String(message.toString().getBytes("windows-1252"),"windows-1251"), type);
        } catch (UnsupportedEncodingException ex) {
            System.err.println("error encoding string");
        }
    }
    
}
