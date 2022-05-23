package Movimento.SociosExt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class cExtSoc {
    private SimpleIntegerProperty registro;
    private SimpleObjectProperty<Date> data;
    private SimpleStringProperty aut;

    public cExtSoc(int registro, Date data, String aut) {
        this.registro = new SimpleIntegerProperty(registro);
        this.data = new SimpleObjectProperty<>(data);
        this.aut = new SimpleStringProperty(aut);
    }

    public int getRegistro() { return registro.get(); }
    public SimpleIntegerProperty registroProperty() { return registro; }
    public void setRegistro(int registro) { this.registro.set(registro); }

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    public String getAut() { return aut.get(); }
    public SimpleStringProperty autProperty() { return aut; }
    public void setAut(String aut) { this.aut.set(aut); }

    @Override
    public String toString() {
        return "cExtSoc{" +
                "registro=" + registro +
                ", data=" + data +
                ", aut=" + aut +
                '}';
    }
}
