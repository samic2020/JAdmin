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
public class ptelcontatoModel {
    private SimpleStringProperty ddd;
    private SimpleStringProperty telf;
    private SimpleStringProperty tipo;
    
    public ptelcontatoModel() {}
    
    public ptelcontatoModel(String wddd, String wtelf, String wtipo) {
        this.ddd = new SimpleStringProperty(wddd);
        this.telf = new SimpleStringProperty(wtelf);
        this.tipo = new SimpleStringProperty(wtipo);        
    }
    
    public String getDdd() {return ddd.get();}
    public void setDdd(String c) {ddd.set(c);}
    
    public String getTelf() {return telf.get();}
    public void setTelf(String c) {telf.set(c);}
    
    public String getTipo() {return tipo.get();}
    public void setTipo(String c) {tipo.set(c);}    
    
    @Override
    public String toString() {
        return "(" + ddd.get() + ") " + telf.get() + " - " + tipo.get();
    }
}
