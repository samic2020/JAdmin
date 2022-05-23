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
public class urotinasModel {
    private SimpleIntegerProperty id;    
    private SimpleStringProperty desc;
    private SimpleStringProperty icon;
    private SimpleStringProperty look;
    private SimpleStringProperty options;    

    public urotinasModel() {}
    
    public urotinasModel(Integer wid, String wdesc, String wicon, String wlook, String woptions) {
        this.id = new SimpleIntegerProperty(wid);
        this.desc = new SimpleStringProperty(wdesc);
        this.icon = new SimpleStringProperty(wicon);
        this.look = new SimpleStringProperty(wlook);
        this.options = new SimpleStringProperty(woptions);
    }
    
    public Integer getId() {return id.get();}
    public void setId(Integer p) {id.set(p);}

    public String getDesc() {return desc.get();}
    public void setDesc(String i) {desc.set(i);}

    public String getIcon() {return icon.get();}
    public void setIcon(String t) {icon.set(t);}

    public String getLook() {return look.get();}
    public void setLook(String e) {look.set(e);}

    public String getOptions() {return options.get();}
    public void setOptions(String s) {options.set(s);}
    
    @Override
    public String toString() {
        return "";
    }
}
