package Administrador.admSocios;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class admSociosClass {
    SimpleIntegerProperty id;
    SimpleStringProperty nome;
    SimpleStringProperty banco;
    SimpleStringProperty agencia;
    SimpleStringProperty conta;
    SimpleDoubleProperty perc;

    public admSociosClass(int id, String nome, String banco, String agencia, String conta, double perc) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.conta = new SimpleStringProperty(conta);
        this.perc = new SimpleDoubleProperty(perc);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getConta() { return conta.get(); }
    public SimpleStringProperty contaProperty() { return conta; }
    public void setConta(String conta) { this.conta.set(conta); }

    public double getPerc() { return perc.get(); }
    public SimpleDoubleProperty percProperty() { return perc; }
    public void setPerc(double perc) { this.perc.set(perc); }

    @Override
    public String toString() {
        return "admSociosClass{" +
                "id=" + id +
                ", nome=" + nome +
                ", banco=" + banco +
                ", agencia=" + agencia +
                ", conta=" + conta +
                ", perc=" + perc +
                '}';
    }
}
