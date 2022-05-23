package Relatorios.ReajusteAluguel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class cReajAlug {
    private SimpleStringProperty tpimovel;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nomerazao;
    private SimpleObjectProperty<Date> dtinicio;
    private SimpleObjectProperty<Date> dttermino;
    private SimpleObjectProperty<Date> dtaditamento;

    public cReajAlug(String tpimovel, String contrato, String nomerazao, Date dtinicio, Date dttermino, Date dtaditamento) {
        this.tpimovel = new SimpleStringProperty(tpimovel);
        this.contrato = new SimpleStringProperty(contrato);
        this.nomerazao = new SimpleStringProperty(nomerazao);
        this.dtinicio = new SimpleObjectProperty<>(dtinicio);
        this.dttermino = new SimpleObjectProperty<>(dttermino);
        this.dtaditamento = new SimpleObjectProperty<>(dtaditamento);
    }

    public String getTpimovel() { return tpimovel.get(); }
    public SimpleStringProperty tpimovelProperty() { return tpimovel; }
    public void setTpimovel(String tpimovel) { this.tpimovel.set(tpimovel); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrao(String contrato) { this.contrato.set(contrato); }

    public String getNomerazao() { return nomerazao.get(); }
    public SimpleStringProperty nomerazaoProperty() { return nomerazao; }
    public void setNomerazao(String nomerazao) { this.nomerazao.set(nomerazao); }

    public Date getDtinicio() { return dtinicio.get(); }
    public SimpleObjectProperty<Date> dtinicioProperty() { return dtinicio; }
    public void setDtinicio(Date dtinicio) { this.dtinicio.set(dtinicio); }

    public Date getDttermino() { return dttermino.get(); }
    public SimpleObjectProperty<Date> dtterminoProperty() { return dttermino; }
    public void setDttermino(Date dttermino) { this.dttermino.set(dttermino); }

    public Date getDtaditamento() { return dtaditamento.get(); }
    public SimpleObjectProperty<Date> dtaditamentoProperty() { return dtaditamento; }
    public void setDtaditamento(Date dtaditamento) { this.dtaditamento.set(dtaditamento); }
}
