package Movimento.Adiantamento;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class classDebLista {
    private SimpleIntegerProperty id;
    private SimpleStringProperty descricao;
    private SimpleObjectProperty<BigDecimal> valor;

    public classDebLista(int id, String descricao, BigDecimal valor) {
        this.id = new SimpleIntegerProperty(id);
        this.descricao = new SimpleStringProperty(descricao);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "classDebLista{" +
                "id=" + id +
                ", descricao=" + descricao +
                ", valor=" + valor +
                '}';
    }
}
