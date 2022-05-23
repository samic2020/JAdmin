package Movimento.Bloqueio;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

public class BloqClass {
    private SimpleIntegerProperty id;
    private SimpleBooleanProperty tag;
    private SimpleStringProperty tipo;
    private SimpleStringProperty nome;
    private SimpleStringProperty vecto;
    private SimpleStringProperty valor;

    public BloqClass(Integer id, Boolean tag, String tipo, String nome, String vecto, String valor) {
        this.id = new SimpleIntegerProperty(id);
        this.tag = new SimpleBooleanProperty(tag);
        this.tipo = new SimpleStringProperty(tipo);
        this.nome = new SimpleStringProperty(nome);
        this.vecto = new SimpleStringProperty(vecto);
        this.valor = new SimpleStringProperty(valor);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public boolean isTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
    public ObservableBooleanValue isCheckedTag() { return tag; }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getVecto() { return vecto.get(); }
    public SimpleStringProperty vectoProperty() { return vecto; }
    public void setVecto(String vecto) { this.vecto.set(vecto); }

    public String getValor() { return valor.get(); }
    public SimpleStringProperty valorProperty() { return valor; }
    public void setValor(String valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "BloqClass{" +
                "id=" + id +
                ", tag=" + tag +
                ", tipo=" + tipo +
                ", nome=" + nome +
                ", vecto=" + vecto +
                ", valor=" + valor +
                '}';
    }
}
