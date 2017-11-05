package tray;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import form.org.*;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.LogManager;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
public class Tray implements NativeKeyListener {
        
        static String input="********************";
        static setting setting;
        boolean find(String f){
            String fileName = "data/bl.ls";
            String line = null;
            try {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                for (int i = 0;(line = bufferedReader.readLine()) != null; i++) {
                    if(f.contains(line.toUpperCase())){
                        String a="";
                        for (int j = 0; j < line.length(); j++) {
                            a=a+"*";
                        }
                        setting.writeLog("warning! >>> " + input);
                        input=input.replace(line.toUpperCase(), a);
                        f=input;
                        return true;
                    }
                }
                bufferedReader.close(); 
            }
            catch(FileNotFoundException ex) {
                System.out.println("Unable to open file '" + fileName + "'");                
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        String number2String(String a){
            if (a.equals("0")) a="O";
            else if (a.equals("9")) a="G";
            else if (a.equals("8")) a="B";
            else if (a.equals("7")) a="J";
            else if (a.equals("6")) a="G";
            else if (a.equals("5")) a="S";
            else if (a.equals("4")) a="A";
            else if (a.equals("3")) a="E";
            else if (a.equals("2")) a="S";
            else if (a.equals("1")) a="I";
            else if (a.equals("@")) a="A";
            else if (a.equals("$")) a="S";
            
            return a;
        }
	public void nativeKeyPressed(NativeKeyEvent e) {
            String key=NativeKeyEvent.getKeyText(e.getKeyCode());
            key=number2String(key);
            if (key.equalsIgnoreCase("Backspace")) {
                key="";
                input="***" + input.substring(1, input.length()-1) ;
            }
            else if(key.equalsIgnoreCase("SPACE")) key =" ";
            else if(key.length()>1) key ="*";
            System.out.println("Key Pressed: " + key);
            input=input.substring(1)+key;            
            System.out.println(input);
            if (input.contains(setting.password.getText().toUpperCase())) {
                String pass=setting.password.getText();
                setting.setVisible(true);
                String a="";
                for (int j = 0; j < pass.length(); j++) {
                    a=a+"*";
                }                        
                input=input.replace(pass.toUpperCase(), a);
            }
            if (find(input) && setting.enable.isSelected() && !setting.isVisible() ) {     
                
                sleep();
                shutDown();
                warning();
                webcam();
                
            }
            
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		//System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		//System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	}
        void sleep(){
            if (setting.Sleep.isSelected()) {
                try {
                    Runtime.getRuntime().exec("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
                } catch (IOException ex) {
                    Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        void shutDown(){
            if (setting.Shut.isSelected()) {
                try {
                    Runtime.getRuntime().exec("shutdown.exe -s -t 0");
                } catch (IOException ex) {
                    Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        void warning(){
            if (setting.Warning.isSelected()) {
                JOptionPane.showMessageDialog(new JFrame(), setting.WarningText.getText());
            }
        }
        void webcam(){
            if (setting.Webcam.isSelected()) {
                System.out.println("b");
                Webcam e = Webcam.getDefault();
                
                System.out.println("jh");
                e.setCustomViewSizes(new Dimension[]{WebcamResolution.VGA.getSize()});
                e.open();
                System.out.println("hj");
                BufferedImage Img = e.getImage();
                try {
                    System.out.println("df");
                    ImageIO.write(Img, "png", new File("test.png"));
                } catch (IOException ex) {
                    Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ImageIcon imageIcon = new ImageIcon("test.png");
                Image image = imageIcon.getImage();
                image = image.getScaledInstance(456, 373, 4);

                
                JLabel a = new JLabel();
                a.setIcon(new ImageIcon(image)); // NOI18N
                JOptionPane.showMessageDialog(null,a);
            }
        }
        
	public static void main(String[] args) throws IOException {
            setting = new setting();
                
            try {
                    GlobalScreen.registerNativeHook();
                }
            catch (NativeHookException ex) {
                    //ascscsmmmssSystem.err.println("There was a problem registering the native hook.");
                    //System.err.println(ex.getMessage());
                   System.exit(1);
                }
            GlobalScreen.addNativeKeyListener(new Tray());
            
            //remove log
            LogManager.getLogManager().reset();
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            
	}
}