/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author supervisor
 */
public class setBancos {
    List<pbancosModel> data = new ArrayList<>();
    public setBancos(String banco) {
        String zbanco = null, zagencia = null, zconta = null, zfavorecido = null;
        if (banco != null && !banco.isEmpty()) {
            String[] abanco = banco.split(";");
            if (abanco.length > 0) {
                String[] aabanco = null;
                for (String stel : abanco) {
                    aabanco = stel.split(",");

                    if (aabanco.length > 0) {
                        try {zbanco = aabanco[0];} catch (Exception e) {}
                        try {zagencia = aabanco[1];} catch (Exception e) {}
                        try {zconta = aabanco[2];} catch (Exception e) {}
                        try {zfavorecido = aabanco[3];} catch (Exception e) {zfavorecido = "";}
                    }
                    data.add(new pbancosModel(zbanco, zagencia, zconta, zfavorecido));
                }
            }
        }
    }
    
    public List<pbancosModel> rString() {
        if (!data.isEmpty()) {
            return data;
        } else {
            return null;
        }        
    }
}
