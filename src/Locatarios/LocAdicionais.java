package Locatarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LocAdicionais {
    private SimpleIntegerProperty id;
    private SimpleStringProperty cpfcnpj;
    private SimpleStringProperty nome;

    public LocAdicionais(int id, String cpfcnpj, String nome) {
        this.id = new SimpleIntegerProperty(id);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.nome = new SimpleStringProperty(nome);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getCpfcnpj() { return cpfcnpj.get(); }
    public SimpleStringProperty cpfcnpjProperty() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj.set(cpfcnpj); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    @Override
    public String toString() {
        return "LocAdicionais{" +
                "id=" + id +
                ", cpfcnpj=" + cpfcnpj +
                ", nome=" + nome +
                '}';
    }
}
