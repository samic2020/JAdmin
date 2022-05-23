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
public class pemailModel {
    private SimpleStringProperty email;
    private SimpleStringProperty senha;
    
    public pemailModel() {}
    
    public pemailModel(String wemail) {
        this.email = new SimpleStringProperty(wemail);
    }
    public pemailModel(String wemail, String wsenha) {
        this.email = new SimpleStringProperty(wemail);
        this.senha = new SimpleStringProperty(wsenha);
    }

    public String getEmail() {return email.get();}
    public void setEmail(String c) {email.set(c);}

    public String getSenha() { return senha.get(); }
    public SimpleStringProperty senhaProperty() { return senha; }
    public void setSenha(String senha) { this.senha.set(senha); }

    @Override
    public String toString() {
        return email.get();
    }
}
