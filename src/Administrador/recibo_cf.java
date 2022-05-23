package Administrador;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

/**
 * Created by supervisor on 06/04/17.
 */
public class recibo_cf {
    SimpleIntegerProperty id;
    SimpleStringProperty descr;
    SimpleBooleanProperty tag;

    public recibo_cf(Integer id, String descr, Boolean tag) {
        this.id = new SimpleIntegerProperty(id);
        this.descr = new SimpleStringProperty(descr);
        this.tag = new SimpleBooleanProperty(tag);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getDescr() { return descr.get(); }
    public SimpleStringProperty descrProperty() { return descr; }
    public void setDescr(String descr) { this.descr.set(descr); }

    public boolean isTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public ObservableBooleanValue isCheckedTag() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
}
