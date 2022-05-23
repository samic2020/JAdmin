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
public class panuncioModel {
    private SimpleStringProperty campo;
    private SimpleStringProperty descr;

    public panuncioModel() {}

    public panuncioModel(String wcampo, String wdescr) {
        this.campo = new SimpleStringProperty(wcampo);
        this.descr = new SimpleStringProperty(wdescr);
    }
    
    public String getCampo() {return campo.get();}
    public void setCampo(String p) {campo.set(p);}

    public String getDescr() {return descr.get();}
    public void setDescr(String i) {descr.set(i);}

    @Override
    public String toString() {
        return null;
    }
}

