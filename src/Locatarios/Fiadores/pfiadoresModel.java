/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios.Fiadores;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author supervisor
 */
public class pfiadoresModel {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty cnpj;
    private SimpleStringProperty nome;

    public pfiadoresModel() {}

    public pfiadoresModel(Integer wid, String wrgprp, String wrgimv, String wcontrato, String wcnpj, String wnome) {
        this.id = new SimpleIntegerProperty(wid);
        this.rgprp = new SimpleStringProperty(wrgprp);
        this.rgimv = new SimpleStringProperty(wrgimv);
        this.contrato = new SimpleStringProperty(wcontrato);
        this.cnpj = new SimpleStringProperty(wcnpj);
        this.nome = new SimpleStringProperty(wnome);
    }

    public Integer getId() {return id.get();}
    public void setId(Integer p) {id.set(p);}

    public String getRgprp() {return rgprp.get();}
    public void setRgprp(String p) {rgprp.set(p);}

    public String getRgimv() {return rgimv.get();}
    public void setRgimv(String i) {rgimv.set(i);}

    public String getContrato() {return contrato.get();}
    public void setContrato(String t) {contrato.set(t);}

    public String getCnpj() {return cnpj.get();}
    public void setCnpj(String e) {cnpj.set(e);}

    public String getNome() {return nome.get();}
    public void setNome(String s) {nome.set(s);}
    
    @Override
    public String toString() {
        return null;
    }
}