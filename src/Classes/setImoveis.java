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
public class setImoveis {
    List<pimovelModel> dataimoveis = new ArrayList<>();
    public setImoveis(String wimoveis) {
        if (wimoveis != null) {
            String[] aimoveis = wimoveis.split(";");
            if (aimoveis.length > 0) {
                for (String simovel : aimoveis) {
                    dataimoveis.add(new pimovelModel(simovel));
                }
            }
        }
    }
    
    public List<pimovelModel> rString() {
        if (!dataimoveis.isEmpty()) {
            return dataimoveis;
        } else {
            return null;
        }        
    }
    
}
