package Movimento.Razao;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Date;

public class cRazao {
    private SimpleObjectProperty<Date> data;

    public cRazao() {}
    public cRazao(Date data) {this.data = new SimpleObjectProperty<>(data);}

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    @Override
    public String toString() {
        return "cRazao{" +
                "data=" + data +
                '}';
    }
}
