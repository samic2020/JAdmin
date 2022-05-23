/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author supervisor
 */
public class pimovelModel {
    private SimpleStringProperty tpimovel;

    public pimovelModel() {}

    public pimovelModel(String wtpimovel) {
        this.tpimovel = new SimpleStringProperty(wtpimovel);
    }
    
    public String getTpImovel() {return tpimovel.get();}
    public void setTpImovel(String c) {tpimovel.set(c);}
    
    @Override public String toString() {return tpimovel.get();}
}
