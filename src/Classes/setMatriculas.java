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
public class setMatriculas {
    List<pmatriculaModel> datamat = new ArrayList<>();
    public setMatriculas(String wmat) {
        if (!wmat.equalsIgnoreCase("")) {
            String[] amat = wmat.split(";");
            if (amat.length > 0) {
                for (String smat : amat) {
                    String[] dmat = smat.split(",");
                    if (dmat.length >= 2) {
                        if (dmat[0] != null && dmat[1] != null) datamat.add(new pmatriculaModel(dmat[0], dmat[1]));
                    }
                }
            }
        }
    }
    
    public List<pmatriculaModel> rString() {
        if (!datamat.isEmpty()) {
            return datamat;
        } else {
            return null;
        }        
    }
    
}
