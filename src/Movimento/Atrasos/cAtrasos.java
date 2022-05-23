package Movimento.Atrasos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class cAtrasos {
    private SimpleIntegerProperty id;
    private SimpleStringProperty tipo;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleObjectProperty<Date> vencimento;
    private SimpleStringProperty telefones;
    private SimpleStringProperty emails;

    public cAtrasos(int id, String tipo, String contrato, String nome, Date vencimento, String telefones, String emails) {
        this.id = new SimpleIntegerProperty(id);
        this.tipo = new SimpleStringProperty(tipo);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.vencimento = new SimpleObjectProperty<>(vencimento);
        this.telefones = new SimpleStringProperty(telefones);
        this.emails = new SimpleStringProperty(emails);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public Date getVencimento() { return vencimento.get(); }
    public SimpleObjectProperty<Date> vencimentoProperty() { return vencimento; }
    public void setVencimento(Date vencimento) { this.vencimento.set(vencimento); }

    public String getTelefones() { return telefones.get(); }
    public SimpleStringProperty telefonesProperty() { return telefones; }
    public void setTelefones(String telefones) { this.telefones.set(telefones); }

    public String getEmails() { return emails.get(); }
    public SimpleStringProperty emailsProperty() { return emails; }
    public void setEmails(String emails) { this.emails.set(emails); }

    @Override
    public String toString() {
        return "cAtrasos{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", contrato=" + contrato +
                ", nome=" + nome +
                ", vencimento=" + vencimento +
                ", telefones=" + telefones +
                ", emails=" + emails +
                '}';
    }
}
