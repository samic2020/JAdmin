package SegundaVia.Extratos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class cRectos {
    private SimpleIntegerProperty Id;
    private SimpleIntegerProperty Aut;
    private SimpleObjectProperty<Date> DataHora;
    private SimpleObjectProperty<BigDecimal> Valor;
    private SimpleStringProperty Logado;
    private SimpleStringProperty Lanctos;

    public cRectos(int id, int aut, Date dataHora, BigDecimal valor, String logado, String lanctos) {
        Id = new SimpleIntegerProperty(id);
        Aut = new SimpleIntegerProperty(aut);
        DataHora = new SimpleObjectProperty<>(dataHora);
        Valor = new SimpleObjectProperty<>(valor);
        Logado = new SimpleStringProperty(logado);
        Lanctos = new SimpleStringProperty(lanctos);
    }

    public int getId() { return Id.get(); }
    public SimpleIntegerProperty idProperty() { return Id; }
    public void setId(int id) { this.Id.set(id); }

    public int getAut() { return Aut.get(); }
    public SimpleIntegerProperty autProperty() { return Aut; }
    public void setAut(int aut) { this.Aut.set(aut); }

    public Date getDataHora() { return DataHora.get(); }
    public SimpleObjectProperty<Date> dataHoraProperty() { return DataHora; }
    public void setDataHora(Date dataHora) { this.DataHora.set(dataHora); }

    public BigDecimal getValor() { return Valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return Valor; }
    public void setValor(BigDecimal valor) { this.Valor.set(valor); }

    public String getLogado() { return Logado.get(); }
    public SimpleStringProperty logadoProperty() { return Logado; }
    public void setLogado(String logado) { this.Logado.set(logado); }

    public String getLanctos() { return Lanctos.get(); }
    public SimpleStringProperty lanctosProperty() { return Lanctos; }
    public void setLanctos(String lanctos) { this.Lanctos.set(lanctos); }

    @Override
    public String toString() {
        return "cRectos{" +
                "Id=" + Id +
                ", Aut=" + Aut +
                ", DataHora=" + DataHora +
                ", Valor=" + Valor +
                ", Logado=" + Logado +
                ", Lanctos=" + Lanctos +
                '}';
    }
}
