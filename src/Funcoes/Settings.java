/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 *
 * @author supervisor
 */
public class Settings {
    static private String GlobalPropert = System.getProperty("user.dir") + "/" + new NetWork().NetWork(false) + ".conf";
    static private String mFile = "";
    private Properties p;
    FileInputStream propFile;
    
    public Settings() {
        mFile = GlobalPropert;
        if ((new File(mFile)).exists()) {
            try {
                propFile = new FileInputStream(mFile);
                p = new Properties(System.getProperties());
                p.load(propFile);

                System.setProperties(p);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void Save(String propriedade, String Valor) {
        try {
            System.setProperty(propriedade, Valor);
            p.setProperty(propriedade, Valor);
            FileOutputStream outFile = new FileOutputStream(mFile);
            p.save(outFile, new NetWork().NetWork(false) + ".conf");
            outFile.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }

    public void Remove(String propriedade) {
        try {
            p.remove(propriedade);
            FileOutputStream outFile = new FileOutputStream(mFile);
            p.save(outFile, new NetWork().NetWork(false) + ".conf");
            outFile.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }
}
