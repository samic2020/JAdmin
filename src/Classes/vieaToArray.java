/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author supervisor
 */
public class vieaToArray {
    private String[] viea;
    
    public vieaToArray(String viea) {
        String rviea = "";
        if (viea != null) {
            for (int i = 0;i<viea.length();i++) {
                rviea += viea.substring(i, i+1) + ";";
            }
            if (!"".equals(rviea)) rviea = rviea.substring(0, rviea.length() - 1);
            this.viea = rviea.split(";");
        } else this.viea = null;
    }
    
    public String[] getViea() { return this.viea; }
}
