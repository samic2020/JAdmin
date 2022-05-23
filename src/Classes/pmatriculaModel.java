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
public class pmatriculaModel {
    private SimpleStringProperty id;
    private SimpleStringProperty cod;
    
    public pmatriculaModel() {}
    
    public pmatriculaModel(String wid, String wcod) {
        this.id = new SimpleStringProperty(wid);
        this.cod = new SimpleStringProperty(wcod);
    }
    
    public String getId() {return id.get();}
    public void setId(String c) {id.set(c);}
    
    public String getCod() {return cod.get();}
    public void setCod(String c) {cod.set(c);}
    
    @Override
    public String toString() {
        return id.get() + "," + cod.get();
    }
}
