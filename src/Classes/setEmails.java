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
public class setEmails {
    List<pemailModel> dataemail = new ArrayList<>();
/*
    public setEmails(String wemail) {
        if (wemail != null) {
            String[] aemail = wemail.split(";");
            if (aemail.length > 0) {
                for (String semail : aemail) {
                    dataemail.add(new pemailModel(semail ));
                }
            }
        }
    }
*/

    public setEmails(String wemail, boolean comsenha) {
        if (wemail != null) {
            if (wemail.indexOf(",;") > -1) wemail = wemail.substring(0,wemail.length() - 2);
            String[] aemail = wemail.split(";");
            if (aemail.length > 0) {
                for (String semail : aemail) {
                    if (comsenha) {
                        String[] aemailsenha = semail.split(",");
                        String qemail = aemailsenha[0];
                        String qsenha = null;
                        try {if (aemail.length > 1) qsenha = aemailsenha[1];} catch (Exception e) {}
                        dataemail.add(new pemailModel(qemail, qsenha));
                    } else {
                        dataemail.add(new pemailModel(semail));
                    }
                }
            }
        }
    }

    public List<pemailModel> rString() {
        if (!dataemail.isEmpty()) {
            return dataemail;
        } else {
            return null;
        }        
    }
    
}
