package Funcoes;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Collections;

public class SettingPwd {
    private static final String dbPassWordSettingPath = System.getProperty("user.dir") + "/jAdmin.aut";

    public SettingPwd() {
        boolean exists = (new File(dbPassWordSettingPath)).exists();
        if (!exists) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Atenção!");
            alerta.setContentText("Programa sem autorização!!!\n\nEntre em contato com a SAMIC Tel.: (21)2701-0261\nou\nCel.: (21)98552-1405");
            alerta.showAndWait();
            System.exit(0);
        }

        Properties p;
        FileInputStream propFile;
        try {
            propFile = new FileInputStream(dbPassWordSettingPath);
            p = new Properties(System.getProperties()){
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            p.load(propFile);

            System.setProperties(p);
        } catch (Exception ex) {ex.printStackTrace();}
    }
}
