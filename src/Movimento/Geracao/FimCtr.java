package Movimento.Geracao;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class FimCtr {
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleObjectProperty<Date> dtinicio;
    private SimpleObjectProperty<Date> dtfim;
    private SimpleObjectProperty<Date> dtaditamento;
    private SimpleObjectProperty<BigDecimal> mensal;

    public FimCtr(String contrato, String nome, Date dtinicio, Date dttermino, Date dtaditamento, BigDecimal qaluguel) {
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.dtinicio = new SimpleObjectProperty<>(dtinicio);
        this.dtfim = new SimpleObjectProperty<>(dttermino);
        this.dtaditamento = new SimpleObjectProperty<>(dtaditamento);
        this.mensal = new SimpleObjectProperty<>(qaluguel);
    }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public Date getDtinicio() { return dtinicio.get(); }
    public SimpleObjectProperty<Date> dtinicioProperty() { return dtinicio; }
    public void setDtinicio(Date dtinicio) { this.dtinicio.set(dtinicio); }

    public Date getDtfim() { return dtfim.get(); }
    public SimpleObjectProperty<Date> dtfimProperty() { return dtfim; }
    public void setDtfim(Date dtfim) { this.dtfim.set(dtfim); }

    public Date getDtaditamento() { return dtaditamento.get(); }
    public SimpleObjectProperty<Date> dtaditamentoProperty() { return dtaditamento; }
    public void setDtaditamento(Date dtaditamento) { this.dtaditamento.set(dtaditamento); }

    public BigDecimal getMensal() { return mensal.get(); }
    public SimpleObjectProperty<BigDecimal> MensalProperty() { return mensal; }
    public void setMensal(BigDecimal mensal) { this.mensal.set(mensal); }
}
