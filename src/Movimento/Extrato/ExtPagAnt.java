package Movimento.Extrato;

import Funcoes.Dates;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class ExtPagAnt {
    private SimpleObjectProperty<Date> datarec;
    private SimpleStringProperty valor;
    private SimpleFloatProperty aut;
    private SimpleStringProperty logado;

    public ExtPagAnt(Date datarec, String valor, float aut, String logado) {
        this.datarec = new SimpleObjectProperty<>(datarec);
        this.valor = new SimpleStringProperty(valor);
        this.aut = new SimpleFloatProperty(aut);
        this.logado = new SimpleStringProperty(logado);
    }

    public Date getDatarec() { return datarec.get(); }
    public SimpleObjectProperty<Date> datarecProperty() { return datarec; }
    public void setDatarec(Date datarec) { this.datarec.set(datarec); }

    public String getValor() { return valor.get(); }
    public SimpleStringProperty valorProperty() { return valor; }
    public void setValor(String valor) { this.valor.set(valor); }

    public float getAut() { return aut.get(); }
    public SimpleFloatProperty autProperty() { return aut; }
    public void setAut(float aut) { this.aut.set(aut); }

    public String getLogado() { return logado.get(); }
    public SimpleStringProperty logadoProperty() { return logado; }
    public void setLogado(String logado) { this.logado.set(logado); }

    @Override
    public String toString() {
        return Dates.DateFormata("dd-MM-yyyy", datarec.getValue()) + " - " + valor.getValue() + " - " + String.valueOf(aut.getValue()).replace(".0", "") + " : " + logado.getValue();
    }
}
