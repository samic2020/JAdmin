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
public class getMatriculas {
    String mmat = "";
    public getMatriculas(ComboBox<pmatriculaModel> mat) {
        for (pmatriculaModel smat : mat.getItems()) {
            mmat += smat.getId() + "," + smat.getCod() + ";";
        }
    }
    
    public String toString() {
        return (mmat.length() > 0 ? mmat.substring(0, mmat.length() - 1) : "");        
    }
}
