package Movimento.Despesas;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class grpDespesas {
    private SimpleIntegerProperty id;
    private SimpleStringProperty descricao;

    public grpDespesas(int id, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.descricao = new SimpleStringProperty(descricao);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    @Override
    public String toString() {
        return id.getValue() + " - " + descricao.getValue();
    }
}
