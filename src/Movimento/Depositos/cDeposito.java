package Movimento.Depositos;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

import java.math.BigDecimal;
import java.util.Date;

public class cDeposito {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty s;
    private SimpleObjectProperty<Date> dtlanc;
    private SimpleObjectProperty<Date> dtpre;
    private SimpleStringProperty banco;
    private SimpleStringProperty agencia;
    private SimpleStringProperty numero;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleBooleanProperty tag;

    public cDeposito() { }

    public cDeposito(int id, int s, Date dtlanc, Date dtpre, String banco, String agencia, String numero, BigDecimal valor, boolean tag) {
        this.id = new SimpleIntegerProperty(id);
        this.s = new SimpleIntegerProperty(s);
        this.dtlanc = new SimpleObjectProperty<>(dtlanc);
        this.dtpre = new SimpleObjectProperty<>(dtpre);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.numero = new SimpleStringProperty(numero);
        this.valor = new SimpleObjectProperty<>(valor);
        this.tag = new SimpleBooleanProperty(tag);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getS() { return s.get(); }
    public SimpleIntegerProperty sProperty() { return s; }
    public void setS(int s) { this.s.set(s); }

    public Date getDtlanc() { return dtlanc.get(); }
    public SimpleObjectProperty<Date> dtlancProperty() { return dtlanc; }
    public void setDtlanc(Date dtlanc) { this.dtlanc.set(dtlanc); }

    public Date getDtpre() { return dtpre.get(); }
    public SimpleObjectProperty<Date> dtpreProperty() { return dtpre; }
    public void setDtpre(Date dtpre) { this.dtpre.set(dtpre); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getNumero() { return numero.get(); }
    public SimpleStringProperty numeroProperty() { return numero; }
    public void setNumero(String numero) { this.numero.set(numero); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public boolean getTag() { return this.tag.get(); }
    public SimpleBooleanProperty tagProperty() { return this.tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
    public ObservableBooleanValue isTag() { return this.tag; }
    public void setCheckedTag(Boolean tag) { this.tag.set(tag);}

    @Override
    public String toString() {
        return "cDeposito{" +
                "id=" + id +
                ", s=" + s +
                ", dtlanc=" + dtlanc +
                ", dtpre=" + dtpre +
                ", banco=" + banco +
                ", agencia=" + agencia +
                ", numero=" + numero +
                ", valor=" + valor +
                ", tag=" + tag +
                '}';
    }
}
