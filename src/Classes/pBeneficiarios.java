package Classes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 12/04/16.
 */
public class pBeneficiarios {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty rgprp;
    private SimpleStringProperty cpf;
    private SimpleStringProperty nome;
    private SimpleObjectProperty nasc;
    private SimpleStringProperty banco;
    private SimpleStringProperty agencia;
    private SimpleStringProperty operacao;
    private SimpleStringProperty conta;

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getRgprp() {
        return rgprp.get();
    }

    public void setRgprp(int rgprp) {
        this.rgprp.set(rgprp);
    }

    public String getCpf() {
        return cpf.get();
    }

    public void setCpf(String cpf) {
        this.cpf.set(cpf);
    }

    public String getNome() {
        return nome.get();
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public Object getNasc() {
        return nasc.get();
    }

    public void setNasc(Object nasc) {
        this.nasc.set(nasc);
    }

    public String getBanco() {
        return banco.get();
    }

    public void setBanco(String banco) {
        this.banco.set(banco);
    }

    public String getAgencia() {
        return agencia.get();
    }

    public void setAgencia(String agencia) {
        this.agencia.set(agencia);
    }

    public String getOperacao() {
        return operacao.get();
    }

    public void setOperacao(String operacao) {
        this.operacao.set(operacao);
    }

    public String getConta() {
        return conta.get();
    }

    public void setConta(String conta) {
        this.conta.set(conta);
    }

    public pBeneficiarios(Integer id, Integer rgprp, String cpf, String nome, Object nasc, String banco, String agencia,
                          String operacao, String conta) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.cpf = new SimpleStringProperty(cpf);
        this.nome = new SimpleStringProperty(nome);
        this.nasc = new SimpleObjectProperty(nasc);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.operacao = new SimpleStringProperty(operacao);
        this.conta = new SimpleStringProperty(conta);
    }

    public pBeneficiarios(String cpf, String nome, Object nasc, String banco, String agencia,
                          String operacao, String conta) {
        this.cpf = new SimpleStringProperty(cpf);
        this.nome = new SimpleStringProperty(nome);
        this.nasc = new SimpleObjectProperty(nasc);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.operacao = new SimpleStringProperty(operacao);
        this.conta = new SimpleStringProperty(conta);
    }
}
