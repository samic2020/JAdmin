package Gerencia;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class divItens {
    private SimpleIntegerProperty id;
    private SimpleStringProperty cod;
    private SimpleStringProperty descr;
    private SimpleObjectProperty<BigDecimal> perc;

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getCod() { return cod.get(); }
    public SimpleStringProperty codProperty() { return cod; }
    public void setCod(String cod) { this.cod.set(cod); }

    public String getDescr() { return descr.get(); }
    public SimpleStringProperty descrProperty() { return descr; }
    public void setDescr(String descr) { this.descr.set(descr); }

    public BigDecimal getPerc() { return perc.get(); }
    public SimpleObjectProperty<BigDecimal> percProperty() { return perc; }
    public void setPerc(BigDecimal perc) { this.perc.set(perc); }

    public divItens(int id, String cod, String descr, BigDecimal perc) {
        this.id = new SimpleIntegerProperty(id);
        this.cod = new SimpleStringProperty(cod);
        this.descr = new SimpleStringProperty(descr);
        this.perc = new SimpleObjectProperty<>(perc);
    }

    @Override
    public String toString() {
        return "divItens{" +
                "id=" + id +
                ", cod=" + cod +
                ", descr=" + descr +
                ", perc=" + perc +
                '}';
    }
}
