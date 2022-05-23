package BancosDigital.Inter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConsultaInter {
    private SimpleObjectProperty<LocalDate> emissao;
    private SimpleObjectProperty<LocalDate> vencimento;
    private SimpleObjectProperty<LocalDate> pagamento;
    private SimpleStringProperty seunumero;
    private SimpleStringProperty nossonumero;
    private SimpleStringProperty cpfcnpj;
    private SimpleStringProperty sacado;
    private SimpleObjectProperty<BigDecimal> multa;
    private SimpleObjectProperty<BigDecimal> juros;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty situacao;
    private SimpleStringProperty baixa;

    public ConsultaInter(
            LocalDate emissao,
            LocalDate vencimento,
            LocalDate pagamento,
            String seunumero,
            String nossonumero,
            String cpfcnpj,
            String sacado,
            BigDecimal multa,
            BigDecimal juros,
            BigDecimal valor,
            String situacao,
            String baixa) {
        this.emissao = new SimpleObjectProperty<>(emissao);
        this.vencimento = new SimpleObjectProperty<>(vencimento);
        this.pagamento = new SimpleObjectProperty<>(pagamento);
        this.seunumero = new SimpleStringProperty(seunumero);
        this.nossonumero = new SimpleStringProperty(nossonumero);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.sacado = new SimpleStringProperty(sacado);
        this.multa = new SimpleObjectProperty<>(multa);
        this.juros = new SimpleObjectProperty<>(juros);
        this.valor = new SimpleObjectProperty<>(valor);
        this.situacao = new SimpleStringProperty(situacao);
        this.baixa = new SimpleStringProperty(baixa);
    }

    public LocalDate getEmissao() {return emissao.get();}
    public SimpleObjectProperty<LocalDate> emissaoProperty() {return emissao;}
    public void setEmissao(LocalDate emissao) {this.emissao.set(emissao);}

    public LocalDate getVencimento() {return vencimento.get();}
    public SimpleObjectProperty<LocalDate> vencimentoProperty() {return vencimento;}
    public void setVencimento(LocalDate vencimento) {this.vencimento.set(vencimento);}

    public LocalDate getPagamento() {return pagamento.get();}
    public SimpleObjectProperty<LocalDate> pagamentoProperty() {return pagamento;}
    public void setPagamento(LocalDate pagamento) {this.pagamento.set(pagamento);}

    public String getSeunumero() {return seunumero.get();}
    public SimpleStringProperty seunumeroProperty() {return seunumero;}
    public void setSeunumero(String seunumero) {this.seunumero.set(seunumero);}

    public String getNossonumero() {return nossonumero.get();}
    public SimpleStringProperty nossonumeroProperty() {return nossonumero;}
    public void setNossonumero(String nossonumero) {this.nossonumero.set(nossonumero);}

    public String getCpfcnpj() {return cpfcnpj.get();}
    public SimpleStringProperty cpfcnpjProperty() {return cpfcnpj;}
    public void setCpfcnpj(String cpfcnpj) {this.cpfcnpj.set(cpfcnpj);}

    public String getSacado() {return sacado.get();}
    public SimpleStringProperty sacadoProperty() {return sacado;}
    public void setSacado(String sacado) {this.sacado.set(sacado);}

    public BigDecimal getMulta() {return multa.get();}
    public SimpleObjectProperty<BigDecimal> multaProperty() {return multa;}
    public void setMulta(BigDecimal multa) {this.multa.set(multa);}

    public BigDecimal getJuros() {return juros.get();}
    public SimpleObjectProperty<BigDecimal> jurosProperty() {return juros;}
    public void setJuros(BigDecimal juros) {this.juros.set(juros);}

    public BigDecimal getValor() {return valor.get();}
    public SimpleObjectProperty<BigDecimal> valorProperty() {return valor;}
    public void setValor(BigDecimal valor) {this.valor.set(valor);}

    public String getSituacao() {return situacao.get();}
    public SimpleStringProperty situacaoProperty() {return situacao;}
    public void setSituacao(String situacao) {this.situacao.set(situacao);}

    public String getBaixa() {return baixa.get();}
    public SimpleStringProperty baixaProperty() {return baixa;}
    public void setBaixa(String baixa) {this.baixa.set(baixa);}

    @Override
    public String toString() {
        return "ConsultaInter{" +
                "emissao=" + emissao +
                ", vencimento=" + vencimento +
                ", pagamento=" + pagamento +
                ", seunumero=" + seunumero +
                ", nossonumero=" + nossonumero +
                ", cpfcnpj=" + cpfcnpj +
                ", sacado=" + sacado +
                ", multa=" + multa +
                ", juros=" + juros +
                ", valor=" + valor +
                ", situacao=" + situacao +
                ", baixa=" + baixa +
                '}';
    }
}
