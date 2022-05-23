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
public class pbancosModel {
    private SimpleStringProperty banco;
    private SimpleStringProperty agencia;
    private SimpleStringProperty conta;
    private SimpleStringProperty favorecido;
    
    public pbancosModel() {}
    
    public pbancosModel(String wbanco, String wagencia, String wconta, String wfavorecido) {
        this.banco = new SimpleStringProperty(wbanco);
        this.agencia = new SimpleStringProperty(wagencia);
        this.conta = new SimpleStringProperty(wconta);        
        this.favorecido = new SimpleStringProperty(wfavorecido);        
    }
    
    public String getBanco() {return banco.get();}
    public void setBanco(String c) {banco.set(c);}
    
    public String getAgencia() {return agencia.get();}
    public void setAgencia(String c) {agencia.set(c);}
    
    public String getConta() {return conta.get();}
    public void setConta(String c) {conta.set(c);}    
    
    public String getFavorecido() {return favorecido.get();}
    public void setFavorecido(String c) {favorecido.set(c);}    
    
    @Override
    public String toString() {
        return "Banco:" + banco.get() + ", Agência:" + agencia.get() + ", Conta:" + conta.get() + ", Fav.:" + (favorecido.get() != null && !favorecido.get().equalsIgnoreCase("null") ? favorecido.get() : "");
    }
}
