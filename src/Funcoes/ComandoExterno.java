package Funcoes;

import java.io.IOException;

/**
 * Created by supervisor on 16/03/17.
 */
public class ComandoExterno {

    public static void ComandoExterno(String cmd) {
        Process p;
        try {
            //executar rotina de backup
            if (System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
                p = Runtime.getRuntime().exec(new String[]{ "/bin/bash", "-c",cmd});
            } else {
                p = Runtime.getRuntime().exec(cmd);
            }
            p.waitFor(); // espera pelo processo terminar
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}