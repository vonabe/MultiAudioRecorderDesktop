package multiaudiorecorder;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author wenkael™
 */
public class CanvasImg {
//Aharoni
//Andalus
//Angsana New
//AngsanaUPC
//Aparajita
//Arabic Typesetting
//Arial
//Arial Black
//Batang
//BatangChe
//Browallia New
//BrowalliaUPC
//Calibri
//Calibri Light
//Cambria
//Cambria Math
//Candara
//Comic Sans MS
//Consolas
//Constantia
//Corbel
//Cordia New
//CordiaUPC
//Courier New
//DFKai-SB
//DaunPenh
//David
//DilleniaUPC
//DokChampa
//Dotum
//DotumChe
//Ebrima
//Estrangelo Edessa
//EucrosiaUPC
//Euphemia
//FZLanTingHeiS-UL-GB
//FangSong
//FrankRuehl
//Franklin Gothic Medium
//FreesiaUPC
//Gabriola
//Gautami
//Georgia
//Gisha
//Gulim
//GulimChe
//Gungsuh
//GungsuhChe
//Impact
//IrisUPC
//Iskoola Pota
//JasmineUPC
//KaiTi
//Kalinga
//Kartika
//Khmer UI
//KodchiangUPC
//Kokila
//Lao UI
//Latha
//Leelawadee
//Levenim MT
//LilyUPC
//Lucida Bright
//Lucida Console
//Lucida Sans
//Lucida Sans Typewriter
//Lucida Sans Unicode
//MS Gothic
//MS Mincho
//MS PGothic
//MS PMincho
//MS UI Gothic
//MV Boli
//Malgun Gothic
//Mangal
//Marlett
//Meiryo
//Meiryo UI
//Microsoft Himalaya
//Microsoft JhengHei
//Microsoft New Tai Lue
//Microsoft PhagsPa
//Microsoft Sans Serif
//Microsoft Tai Le
//Microsoft Uighur
//Microsoft YaHei
//Microsoft Yi Baiti
//MingLiU
//MingLiU-ExtB
//MingLiU_HKSCS
//MingLiU_HKSCS-ExtB
//Miriam
//Miriam Fixed
//Mongolian Baiti
//Monospaced
//MoolBoran
//NI7SEG
//NSimSun
//Narkisim
//Nyala
//PMingLiU
//PMingLiU-ExtB
//Palatino Linotype
//Plantagenet Cherokee
//Raavi
//Rod
//SWGamekeys MT
//Sakkal Majalla
//SansSerif
//Segoe Print
//Segoe Script
//Segoe UI
//Segoe UI Light
//Segoe UI Semibold
//Segoe UI Symbol
//Serif
//Shonar Bangla
//Shruti
//SimHei
//SimSun
//SimSun-ExtB
//Simplified Arabic
//Simplified Arabic Fixed
//Sylfaen
//Symbol
//System
//Tahoma
//TeamViewer10
//Times New Roman
//Traditional Arabic
//Trebuchet MS
//Tunga
//Utsaah
//Vani
//Verdana
//Vijaya
//Vrinda
//Webdings
//Wingdings
    private final ImageView    image;
    private final Text          text;
    
    public CanvasImg(ImageView img, Pane pane) {
        this.image = img;
        
        this.text = new Text("wenkael");
        this.text.setFont(Font.font ("Symbol", 14));
//        List<String> families = Font.getFamilies();
//        Iterator<String> it = families.iterator();
//        while(it.hasNext()){
//            String famil = it.next();
//            System.out.println(famil);
//        }
        this.text.setFill(Color.RED);
        pane.getChildren().add(this.text);
    }
    
    public void setFPS (int FPS){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.setText("FPS - "+FPS);
            }
        });
    }
    
    public void paint(final WritableImage img)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                image.setImage(img);
            }
        });
    }
    
    
}
