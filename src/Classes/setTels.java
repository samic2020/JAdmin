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
public class setTels {
    List<ptelcontatoModel> data = new ArrayList<>();
    public setTels(String tel) {
        String wddd = ""; String wtel = ""; String wtipo = "";
        String zddd = null, ztel = null, ztip = null;
        if (tel != null && !tel.isEmpty()) {
            String[] atel = tel.split(";");
            if (atel.length > 0) {
                String[] aatel = null;
                for (String stel : atel) {
                    aatel = stel.split(",");

                    if (aatel.length > 0) {
                        zddd = aatel[0].substring(1, 3);
                        ztel = aatel[0].substring(3);
                        ztip = aatel[1];
                    }
                    data.add(new ptelcontatoModel(zddd, ztel, ztip));
                }
            }
        }
    }
    
    public List<ptelcontatoModel> rString() {
        if (!data.isEmpty()) {
            return data;
        } else {
            return null;
        }        
    }
}
