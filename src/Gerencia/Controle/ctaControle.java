package Gerencia.Controle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ctaControle {
    private SimpleIntegerProperty id;
    private SimpleStringProperty registro;
    private SimpleStringProperty descreg;
    private SimpleStringProperty data;
    private SimpleStringProperty descr;
    private SimpleObjectProperty<BigDecimal> cr;
    private SimpleObjectProperty<BigDecimal> db;
    private SimpleObjectProperty<BigInteger> aut;

    public ctaControle() {
        this.id = new SimpleIntegerProperty(0);
        this.registro = new SimpleStringProperty(null);
        this.descreg = new SimpleStringProperty(null);
        this.data = new SimpleStringProperty(null);
        this.descr = new SimpleStringProperty(null);
        this.cr = new SimpleObjectProperty<>(new BigDecimal("0"));
        this.db = new SimpleObjectProperty<>(new BigDecimal("0"));
        this.aut = new SimpleObjectProperty<>(new BigInteger("0"));
    }

    public ctaControle(int id, String registro, String descreg, String data, String descr, BigDecimal cr, BigDecimal db, BigInteger aut) {
        this.id = new SimpleIntegerProperty(id);
        this.registro = new SimpleStringProperty(registro);
        this.descreg = new SimpleStringProperty(descreg);
        this.data = new SimpleStringProperty(data);
        this.descr = new SimpleStringProperty(descr);
        this.cr = new SimpleObjectProperty<>(cr);
        this.db = new SimpleObjectProperty<>(db);
        this.aut = new SimpleObjectProperty<>(aut);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getRegistro() { return registro.get(); }
    public SimpleStringProperty registroProperty() { return registro; }
    public void setRegistro(String registro) { this.registro.set(registro); }

    public String getDescreg() { return descreg.get(); }
    public SimpleStringProperty descregProperty() { return descreg; }
    public void setDescreg(String descreg) { this.descreg.set(descreg); }

    public String getData() { return data.get(); }
    public SimpleStringProperty dataProperty() { return data; }
    public void setData(String data) { this.data.set(data); }

    public String getDescr() { return descr.get(); }
    public SimpleStringProperty descrProperty() { return descr; }
    public void setDescr(String descr) { this.descr.set(descr); }

    public BigDecimal getCr() { return cr.get(); }
    public SimpleObjectProperty<BigDecimal> crProperty() { return cr; }
    public void setCr(BigDecimal cr) { this.cr.set(cr); }

    public BigDecimal getDb() { return db.get(); }
    public SimpleObjectProperty<BigDecimal> dbProperty() { return db; }
    public void setDb(BigDecimal db) { this.db.set(db); }

    public BigInteger getAut() { return aut.get(); }
    public SimpleObjectProperty<BigInteger> autProperty() { return aut; }
    public void setAut(BigInteger aut) { this.aut.set(aut); }

    @Override
    public String toString() {
        return "ctaControle{" +
                "id=" + id +
                ", registro=" + registro +
                ", data=" + data +
                ", descr=" + descr +
                ", cr=" + cr +
                ", db=" + db +
                ", aut=" + aut +
                '}';
    }
}
