package Locatarios.Pagamentos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class cPagtos {
    private SimpleIntegerProperty Id;
    private SimpleIntegerProperty Aut;
    private SimpleObjectProperty<Date> Vencto;
    private SimpleObjectProperty<Date> Recto;
    private SimpleObjectProperty<BigDecimal> Valor;
    private SimpleObjectProperty<Date> DataHora;
    private SimpleStringProperty Logado;
    private SimpleStringProperty Lanctos;

    public cPagtos(int id, int aut, Date vencto, Date recto, BigDecimal valor, Date dataHora, String logado, String lanctos) {
        Id = new SimpleIntegerProperty(id);
        Aut = new SimpleIntegerProperty(aut);
        Vencto = new SimpleObjectProperty<>(vencto);
        Recto = new SimpleObjectProperty<>(recto);
        Valor = new SimpleObjectProperty<>(valor);
        DataHora = new SimpleObjectProperty<>(dataHora);
        Logado = new SimpleStringProperty(logado);
        Lanctos = new SimpleStringProperty(lanctos);
    }

    public int getId() { return Id.get(); }
    public SimpleIntegerProperty idProperty() { return Id; }
    public void setId(int id) { this.Id.set(id); }

    public int getAut() { return Aut.get(); }
    public SimpleIntegerProperty autProperty() { return Aut; }
    public void setAut(int aut) { this.Aut.set(aut); }

    public Date getVencto() { return Vencto.get(); }
    public SimpleObjectProperty<Date> venctoProperty() { return Vencto; }
    public void setVencto(Date vencto) { this.Vencto.set(vencto); }

    public Date getRecto() { return Recto.get(); }
    public SimpleObjectProperty<Date> rectoProperty() { return Recto; }
    public void setRecto(Date recto) { this.Recto.set(recto); }

    public String getLanctos() { return Lanctos.get(); }
    public SimpleStringProperty lanctosProperty() { return Lanctos; }
    public void setLanctos(String lanctos) { this.Lanctos.set(lanctos); }

    public BigDecimal getValor() { return Valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return Valor; }
    public void setValor(BigDecimal valor) { this.Valor.set(valor); }

    public Date getDataHora() { return DataHora.get(); }
    public SimpleObjectProperty<Date> dataHoraProperty() { return DataHora; }
    public void setDataHora(Date dataHora) { this.DataHora.set(dataHora); }

    public String getLogado() { return Logado.get(); }
    public SimpleStringProperty logadoProperty() { return Logado; }
    public void setLogado(String logado) { this.Logado.set(logado); }

    @Override
    public String toString() {
        return "cPagtos{" +
                "Id=" + Id +
                ", Aut=" + Aut +
                ", Vencto=" + Vencto +
                ", Recto=" + Recto +
                ", Valor=" + Valor +
                ", DataHora=" + DataHora +
                ", Logado=" + Logado +
                ", Lanctos=" + Lanctos +
                '}';
    }
}
