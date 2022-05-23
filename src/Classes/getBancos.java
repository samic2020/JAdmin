/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.scene.control.ComboBox;

/**
 *
 * @author supervisor
 */
public class getBancos {
    String mbanco = "";
    public getBancos(ComboBox<pbancosModel> bancos) {
        mbanco = "";
        for (pbancosModel sbanco : bancos.getItems()) {
            mbanco += sbanco.getBanco() + ","+ sbanco.getAgencia() + "," + sbanco.getConta() + "," + sbanco.getFavorecido() + ";";
        }
    }
    
    public String toString() {
        return mbanco.length() > 0 ? mbanco.substring(0,mbanco.length() - 1) : "";
    }
}
