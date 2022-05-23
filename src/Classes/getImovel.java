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
public class getImovel {
    String mimovel = "";
    public getImovel(ComboBox<pimovelModel> tp_imovel) {
        for (pimovelModel simovel : tp_imovel.getItems()) {
            mimovel += simovel.getTpImovel() + ";";
        }
    }
    
    public String toString() {
        return (mimovel.length() > 0 ? mimovel.substring(0, mimovel.length() - 1) : "");
    }
}
