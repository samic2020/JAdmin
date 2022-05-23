package Movimento.Depositos;

import javafx.beans.property.SimpleStringProperty;

public class cBancoDep {
    private SimpleStringProperty banco;
    private SimpleStringProperty descricao;
    private SimpleStringProperty agencia;
    private SimpleStringProperty conta;
    private SimpleStringProperty tipo;

    public cBancoDep(String banco, String descricao, String agencia, String conta, String tipo) {
        this.banco = new SimpleStringProperty(banco);
        this.descricao = new SimpleStringProperty(descricao);
        this.agencia = new SimpleStringProperty(agencia);
        this.conta = new SimpleStringProperty(conta);
        this.tipo = new SimpleStringProperty(tipo);
    }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getConta() { return conta.get(); }
    public SimpleStringProperty contaProperty() { return conta; }
    public void setConta(String conta) { this.conta.set(conta); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    @Override
    public String toString() {
        return banco.get() +
                " - " + descricao.get() +
                ", " + agencia.get() +
                ", " + conta.get() +
                ", " + tipo.get();
    }
}
