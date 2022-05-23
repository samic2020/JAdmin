/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author supervisor
 */
public class StreamFile {
    String defaultPath = new java.io.File("").getAbsolutePath();
    BufferedWriter os;
    BufferedWriter ps;

    public StreamFile () {
        try {
            os = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(defaultPath), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public StreamFile (String[] args) {
        if (args.length > 0) {
            defaultPath = args[0];
        }
        try {
            os = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(defaultPath, true), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean  Open() {
        boolean sucesso = false;

        try {
            sucesso = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            sucesso = false;
        }

        return sucesso;
    }

    public void Print(String output) {
        try {
            os.write(output);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void Close() {
        try {
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
