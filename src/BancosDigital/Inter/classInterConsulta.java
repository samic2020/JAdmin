/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import java.math.BigDecimal;
import java.util.Date;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author desenvolvimento-pc
 */
public class classInterConsulta {
    private SimpleObjectProperty<Date> dataEmissao;
    private SimpleObjectProperty<Date> dataVencimento;
    private SimpleObjectProperty<Date> dataPagamento;
    private SimpleStringProperty seuNumero;
    private SimpleStringProperty nossoNumero;
    private SimpleStringProperty cnpjCpfSacado;
    private SimpleStringProperty nomeSacado;
    private SimpleObjectProperty<BigDecimal> valorMulta;
    private SimpleObjectProperty<BigDecimal> valorJuros;
    private SimpleObjectProperty<BigDecimal> valorNominal;
    private SimpleStringProperty situacao;
    private SimpleBooleanProperty baixado;

    public classInterConsulta(Date dataEmissao, 
                              Date dataVencimento, 
                              Date dataPagamento, 
                              String seuNumero, 
                              String nossoNumero, 
                              String cnpjCpfSacado, 
                              String nomeSacado, 
                              BigDecimal valorMulta, 
                              BigDecimal valorJuros,
                              BigDecimal valorNominal,
                              String situacao,
                              boolean baixado) {
        this.dataEmissao = new SimpleObjectProperty(dataEmissao);
        this.dataVencimento = new SimpleObjectProperty(dataVencimento);
        this.dataPagamento = new SimpleObjectProperty(dataPagamento);
        this.seuNumero = new SimpleStringProperty(seuNumero);
        this.nossoNumero = new SimpleStringProperty(nossoNumero);
        this.cnpjCpfSacado = new SimpleStringProperty(cnpjCpfSacado);
        this.nomeSacado = new SimpleStringProperty(nomeSacado);
        this.valorMulta = new SimpleObjectProperty(valorMulta);
        this.valorJuros = new SimpleObjectProperty(valorJuros);
        this.valorNominal = new SimpleObjectProperty(valorNominal);
        this.situacao = new SimpleStringProperty(situacao);
        this.baixado = new SimpleBooleanProperty(baixado);
    }

    public SimpleObjectProperty<Date> getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(SimpleObjectProperty<Date> dataEmissao) { this.dataEmissao = dataEmissao; }

    public SimpleObjectProperty<Date> getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(SimpleObjectProperty<Date> dataVencimento) { this.dataVencimento = dataVencimento; }

    public SimpleObjectProperty<Date> getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(SimpleObjectProperty<Date> dataPagamento) { this.dataPagamento = dataPagamento; }

    public SimpleStringProperty getSeuNumero() { return seuNumero; }
    public void setSeuNumero(SimpleStringProperty seuNumero) { this.seuNumero = seuNumero; }
    
    public SimpleStringProperty getNossoNumero() { return nossoNumero; }
    public void setNossoNumero(SimpleStringProperty nossoNumero) { this.nossoNumero = nossoNumero; }

    public SimpleStringProperty getCnpjCpfSacado() { return cnpjCpfSacado; }
    public void setCnpjCpfSacado(SimpleStringProperty cnpjCpfSacado) { this.cnpjCpfSacado = cnpjCpfSacado; }

    public SimpleStringProperty getNomeSacado() { return nomeSacado; }
    public void setNomeSacado(SimpleStringProperty nomeSacado) { this.nomeSacado = nomeSacado; }

    public SimpleObjectProperty<BigDecimal> getValorMulta() { return valorMulta; }
    public void setValorMulta(SimpleObjectProperty<BigDecimal> valorMulta) { this.valorMulta = valorMulta; }

    public SimpleObjectProperty<BigDecimal> getValorJuros() { return valorJuros; }
    public void setValorJuros(SimpleObjectProperty<BigDecimal> valorJuros) { this.valorJuros = valorJuros; }

    public SimpleObjectProperty<BigDecimal> getValorNominal() { return valorNominal; }
    public void setValorNominal(SimpleObjectProperty<BigDecimal> valorNominal) { this.valorNominal = valorNominal; }
    
    public SimpleStringProperty getSituacao() { return situacao; }
    public void setSituacao(SimpleStringProperty situacao) { this.situacao = situacao; }

    public SimpleBooleanProperty getBaixado() { return baixado; }
    public void setBaixado(SimpleBooleanProperty baixado) { this.baixado = baixado; }
    
    @Override
    public String toString() {
        return "classInterConsulta{" + "dataEmissao=" + dataEmissao + 
               ", dataVencimento=" + dataVencimento + ", seuNumero=" + seuNumero + 
               ", nossoNumero=" + nossoNumero + ", cnpjCpfSacado=" + cnpjCpfSacado + 
               ", nomeSacado=" + nomeSacado + ", valorMulta=" + valorMulta + 
               ", valorJuros=" + valorJuros + ", valorNominal=" + valorNominal + 
               ", situacao=" + situacao + '}';
    }
    
}
