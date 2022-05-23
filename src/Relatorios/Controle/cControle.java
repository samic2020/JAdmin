package Relatorios.Controle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.sql.Date;

public class cControle {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty endereco;
    private SimpleStringProperty bairro;
    private SimpleStringProperty precampo;
    private SimpleStringProperty descricao;
    private SimpleStringProperty poscampo;
    private SimpleStringProperty cota;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleObjectProperty<Date> dtvencimento;
    private SimpleObjectProperty<Date> dtrecebimento;
    private SimpleStringProperty referencia;
    private SimpleStringProperty tipo;

    public cControle(int id, String rgprp, String rgimv, String endereco, String bairro, String precampo,
                     String descricao, String poscampo, String cota, BigDecimal valor, Date dtvencimento,
                     Date dtrecebimento, String referencia, String tipo) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.endereco = new SimpleStringProperty(endereco);
        this.bairro = new SimpleStringProperty(bairro);
        this.precampo = new SimpleStringProperty(precampo);
        this.descricao = new SimpleStringProperty(descricao);
        this.poscampo = new SimpleStringProperty(poscampo);
        this.cota = new SimpleStringProperty(cota);
        this.valor = new SimpleObjectProperty<>(valor);
        this.dtvencimento = new SimpleObjectProperty<>(dtvencimento);
        this.dtrecebimento = new SimpleObjectProperty<>(dtrecebimento);
        this.referencia = new SimpleStringProperty(referencia);
        this.tipo = new SimpleStringProperty(tipo);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getEndereco() { return endereco.get(); }
    public SimpleStringProperty enderecoProperty() { return endereco; }
    public void setEndereco(String endereco) { this.endereco.set(endereco); }

    public String getBairro() { return bairro.get(); }
    public SimpleStringProperty bairroProperty() { return bairro; }
    public void setBairro(String bairro) { this.bairro.set(bairro); }

    public String getPrecampo() { return precampo.get(); }
    public SimpleStringProperty precampoProperty() { return precampo; }
    public void setPrecampo(String precampo) { this.precampo.set(precampo); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public String getPoscampo() { return poscampo.get(); }
    public SimpleStringProperty poscampoProperty() { return poscampo; }
    public void setPoscampo(String poscampo) { this.poscampo.set(poscampo); }

    public String getCota() { return cota.get(); }
    public SimpleStringProperty cotaProperty() { return cota; }
    public void setCota(String cota) { this.cota.set(cota); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public Date getDtvencimento() { return dtvencimento.get(); }
    public SimpleObjectProperty<Date> dtvencimentoProperty() { return dtvencimento; }
    public void setDtvencimento(Date dtvencimento) { this.dtvencimento.set(dtvencimento); }

    public Date getDtrecebimento() { return dtrecebimento.get(); }
    public SimpleObjectProperty<Date> dtrecebimentoProperty() { return dtrecebimento; }
    public void setDtrecebimento(Date dtrecebimento) { this.dtrecebimento.set(dtrecebimento); }

    public String getReferencia() { return referencia.get(); }
    public SimpleStringProperty referenciaProperty() { return referencia; }
    public void setReferencia(String referencia) { this.referencia.set(referencia); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    @Override
    public String toString() {
        return "cControle{" +
                "id=" + id +
                ", rgprp=" + rgprp +
                ", rgimv=" + rgimv +
                ", endereco=" + endereco +
                ", bairro=" + bairro +
                ", precampo=" + precampo +
                ", descricao=" + descricao +
                ", poscampo=" + poscampo +
                ", cota=" + cota +
                ", valor=" + valor +
                ", dtvencimento=" + dtvencimento +
                ", dtrecebimento=" + dtrecebimento +
                ", referencia=" + referencia +
                ", tipo=" + tipo +
                '}';
    }
}
