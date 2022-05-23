package Movimento.Adiantamento;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

import java.math.BigDecimal;
import java.util.Date;

public class classAdImoveis {
    private SimpleBooleanProperty tag;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty endereco;
    private SimpleObjectProperty<Date> vencimento;
    private SimpleObjectProperty<BigDecimal> valor;

    public classAdImoveis(boolean tag, String rgimv, String contrato, String endereco, Date vencimento, BigDecimal valor) {
        this.tag = new SimpleBooleanProperty(tag);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.endereco = new SimpleStringProperty(endereco);
        this.vencimento = new SimpleObjectProperty<>(vencimento);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public boolean getTag() { return this.tag.get(); }
    public SimpleBooleanProperty tagProperty() { return this.tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
    public ObservableBooleanValue isCheckedTag() { return this.tag; }
    public void setCheckedTag(Boolean tag) { this.tag.set(tag);}

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getEndereco() { return endereco.get(); }
    public SimpleStringProperty enderecoProperty() { return endereco; }
    public void setEndereco(String endereco) { this.endereco.set(endereco); }

    public Date getVencimento() { return vencimento.get(); }
    public SimpleObjectProperty<Date> vencimentoProperty() { return vencimento; }
    public void setVencimento(Date vencimento) { this.vencimento.set(vencimento); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "classAdImoveis{" +
                "tag=" + tag +
                ", rgimv=" + rgimv +
                ", contrato=" + contrato +
                ", endereco=" + endereco +
                ", vencimento=" + vencimento +
                ", valor=" + valor +
                '}';
    }
}
