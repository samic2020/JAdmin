package Administrador;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class cRegTrib {
    private SimpleIntegerProperty id;
    private SimpleStringProperty descricao;
    private SimpleBooleanProperty simples;
    private SimpleBooleanProperty padrao;

    public cRegTrib(int id, String descricao, boolean simples, boolean padrao) {
        this.id = new SimpleIntegerProperty(id);
        this.descricao = new SimpleStringProperty(descricao);
        this.simples = new SimpleBooleanProperty(simples);
        this.padrao = new SimpleBooleanProperty(padrao);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public boolean isSimples() { return simples.get(); }
    public SimpleBooleanProperty simplesProperty() { return simples; }
    public void setSimples(boolean simples) { this.simples.set(simples); }

    public boolean isPadrao() { return padrao.get(); }
    public SimpleBooleanProperty padraoProperty() { return padrao; }
    public void setPadrao(boolean padrao) { this.padrao.set(padrao); }

    @Override
    public String toString() {
        return descricao.get();
    }
}
