/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author supervisor
 */
public class pusuariosModel {
    private SimpleIntegerProperty id;    
    private SimpleStringProperty nome;
    
    public pusuariosModel() {}
    
    public pusuariosModel(int wid, String wnome) {
        this.id = new SimpleIntegerProperty(wid);
        this.nome = new SimpleStringProperty(wnome);
    }
    
    public int getId() {return id.get();}
    public void setId(int p) {id.set(p);}

    public String getNome() {return nome.get();}
    public void setNome(String i) {nome.set(i);}

    @Override
    public String toString() {
        return null;
    }
}

