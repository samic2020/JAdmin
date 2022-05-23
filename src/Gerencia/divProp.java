package Gerencia;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class divProp {
    private SimpleIntegerProperty id;
    private SimpleStringProperty registro;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty descricao;

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getRegistro() { return registro.get(); }
    public SimpleStringProperty registroProperty() { return registro; }
    public void setRegistro(String registro) { this.registro.set(registro); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public divProp(int id, String registro, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.registro = new SimpleStringProperty(registro);
        this.descricao = new SimpleStringProperty(descricao);
    }

    public divProp(int id, String registro, String rgimv, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.registro = new SimpleStringProperty(registro);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.descricao = new SimpleStringProperty(descricao);
    }

    @Override
    public String toString() {
        return "divProp{" +
                "id=" + id +
                ", registro=" + registro +
                ", rgimv=" + rgimv +
                ", descricao=" + descricao +
                '}';
    }
}
