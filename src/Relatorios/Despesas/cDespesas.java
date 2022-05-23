package Relatorios.Despesas;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class cDespesas {
    private SimpleStringProperty id;
    private SimpleStringProperty descricao;
    private SimpleStringProperty texto;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty aut;
    private SimpleObjectProperty<Date> data;
    private SimpleStringProperty logado;

    public cDespesas(String id, String descricao, String texto, BigDecimal valor, String aut, Date data, String logado) {
        this.id = new SimpleStringProperty(id);
        this.descricao = new SimpleStringProperty(descricao);
        this.texto = new SimpleStringProperty(texto);
        this.valor = new SimpleObjectProperty<>(valor);
        this.aut = new SimpleStringProperty(aut);
        this.data = new SimpleObjectProperty<>(data);
        this.logado = new SimpleStringProperty(logado);
    }

    public String getId() { return id.get(); }
    public SimpleStringProperty idProperty() { return id; }
    public void setId(String id) { this.id.set(id); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public String getTexto() { return texto.get(); }
    public SimpleStringProperty textoProperty() { return texto; }
    public void setTexto(String texto) { this.texto.set(texto); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getAut() { return aut.get(); }
    public SimpleStringProperty autProperty() { return aut; }
    public void setAut(String aut) { this.aut.set(aut); }

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    public String getLogado() { return logado.get(); }
    public SimpleStringProperty logadoProperty() { return logado; }
    public void setLogado(String logado) { this.logado.set(logado); }

    @Override
    public String toString() {
        return "cDespesas{" +
                "id=" + id +
                ", descricao=" + descricao +
                ", texto=" + texto +
                ", valor=" + valor +
                ", aut=" + aut +
                ", data=" + data +
                ", logado=" + logado +
                '}';
    }
}
