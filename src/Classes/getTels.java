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
public class getTels {
    String mtel = "";
    public getTels(ComboBox<ptelcontatoModel> tel) {
        mtel = "";
        for (ptelcontatoModel stel : tel.getItems()) {
            mtel += "0" + stel.getDdd() + stel.getTelf() + "," + stel.getTipo() + ";";
        }
    }
    
    public String toString() {
        return mtel.length() > 0 ? mtel.substring(0,mtel.length() - 1) : "";
    }
}
