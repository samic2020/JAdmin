package Administrador.Abas.ContasAdmin;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 25/01/17.
 */
public class ContasAdm {
    SimpleIntegerProperty id;
    SimpleStringProperty codigo;
    SimpleStringProperty descricao;

    public ContasAdm(Integer id, String codigo, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.codigo = new SimpleStringProperty(codigo);
        this.descricao = new SimpleStringProperty(descricao);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getCodigo() { return codigo.get(); }
    public SimpleStringProperty codigoProperty() { return codigo; }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }
}
