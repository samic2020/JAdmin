/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author supervisor
 */
public class pimoveisModel {
    private SimpleStringProperty id;
    private SimpleStringProperty rgprp;    
    private SimpleStringProperty rgimv;
    private SimpleStringProperty tipo;
    private SimpleStringProperty ender;
    private SimpleStringProperty situacao;    
    private SimpleBooleanProperty div;
    private SimpleStringProperty fusao;

    public pimoveisModel() {}
    
    public pimoveisModel(String wrgprp, String wrgimv, String wtipo, String wender, String wsituacao) {
        this.rgprp = new SimpleStringProperty(wrgprp);
        this.rgimv = new SimpleStringProperty(wrgimv);
        this.tipo = new SimpleStringProperty(wtipo);
        this.ender = new SimpleStringProperty(wender);
        this.situacao = new SimpleStringProperty(wsituacao);
    }

    public pimoveisModel(String wid, String wrgprp, String wrgimv, String wtipo, String wender, String wsituacao) {
        this.id = new SimpleStringProperty((wid));
        this.rgprp = new SimpleStringProperty(wrgprp);
        this.rgimv = new SimpleStringProperty(wrgimv);
        this.tipo = new SimpleStringProperty(wtipo);
        this.ender = new SimpleStringProperty(wender);
        this.situacao = new SimpleStringProperty(wsituacao);
    }

    public pimoveisModel(String wrgprp, String wrgimv, String wtipo, String wender, String wsituacao, boolean wdiv) {
        this.rgprp = new SimpleStringProperty(wrgprp);
        this.rgimv = new SimpleStringProperty(wrgimv);
        this.tipo = new SimpleStringProperty(wtipo);
        this.ender = new SimpleStringProperty(wender);
        this.situacao = new SimpleStringProperty(wsituacao);
        this.div = new SimpleBooleanProperty(wdiv);
    }

    public pimoveisModel(String id, String rgprp, String rgimv, String tipo, String ender, String situacao, Boolean div, String fusao) {
        this.id = new SimpleStringProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.tipo = new SimpleStringProperty(tipo);
        this.ender = new SimpleStringProperty(ender);
        this.situacao = new SimpleStringProperty(situacao);
        this.div = new SimpleBooleanProperty(div);
        this.fusao = new SimpleStringProperty(fusao);
    }

    public String getId() {return id.get();}
    public void setId(String p) {id.set(p);}

    public String getRgprp() {return rgprp.get();}
    public void setRgprp(String p) {rgprp.set(p);}

    public String getRgimv() {return rgimv.get();}
    public void setRgimv(String i) {rgimv.set(i);}

    public String getTipo() {return tipo.get();}
    public void setTipo(String t) {tipo.set(t);}

    public String getEnder() {return ender.get();}
    public void setEnder(String e) {ender.set(e);}
    public SimpleStringProperty enderProperty() {return ender;}

    public String getSituacao() {return situacao.get();}
    public void setSituacao(String s) {situacao.set(s);}
    public SimpleStringProperty situacaoProperty() {return situacao;}

    public boolean getDiv() {return div.get();}
    public void setDiv(boolean s) {div.set(s);}

    public String getFusao() {return fusao.get();}
    public void setFusao(String fusao) {this.fusao.set(fusao);}
    public SimpleStringProperty fusaoProperty() {return fusao;}

    @Override
    public String toString() {
        return null;
    }
}

