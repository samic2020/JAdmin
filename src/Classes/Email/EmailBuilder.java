/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes.Email;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Samic
 */
public class EmailBuilder {
 private boolean retorno;
    public EmailBuilder(String appPath, String fileName) {
        try {
            Process process = new ProcessBuilder("EmailSend.exe",appPath, fileName).start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line; int pos = 0;
            while ((line = br.readLine()) != null) {
                if (pos == 0) {
                    if (line.toLowerCase().trim().equalsIgnoreCase("true")) {
                        this.retorno = true;
                    } else {
                        this.retorno = false;
                    }
                } else {
                    System.out.println("Error: " + line);
                }
                pos++;
            }
        } catch (Exception e) { 
            this.retorno = false; 
        }
    }

    public boolean isRetorno() { return retorno; }
}
