package Movimento.Adiantamento;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

import java.math.BigDecimal;

public class classAdRecibo {
    private SimpleIntegerProperty id;
    private SimpleStringProperty tabela;
    private SimpleBooleanProperty tag;
    private SimpleStringProperty descricao;
    private SimpleStringProperty cotaparc;
    private SimpleObjectProperty<BigDecimal> valor;

    public classAdRecibo(int id, String tabela, boolean tag, String descricao, String cotaparc, BigDecimal valor) {
        this.id = new SimpleIntegerProperty(id);
        this.tabela = new SimpleStringProperty(tabela);
        this.tag = new SimpleBooleanProperty(tag);
        this.descricao = new SimpleStringProperty(descricao);
        this.cotaparc = new SimpleStringProperty(cotaparc);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getTabela() { return tabela.get(); }
    public SimpleStringProperty tabelaProperty() { return tabela; }
    public void setTabela(String tabela) { this.tabela.set(tabela); }

    public boolean getTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
    public ObservableBooleanValue isCheckedTag() { return this.tag; }
    public void setCheckedTag(Boolean tag) { this.tag.set(tag);}

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public String getCotaparc() { return cotaparc.get(); }
    public SimpleStringProperty cotaparcProperty() { return cotaparc; }
    public void setCotaparc(String cotaparc) { this.cotaparc.set(cotaparc); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "classAdRecibo{" +
                "id=" + id +
                ", tabela=" + tabela +
                ", tag=" + tag +
                ", descricao=" + descricao +
                ", cotaparc=" + cotaparc +
                ", valor=" + valor +
                '}';
    }
}
