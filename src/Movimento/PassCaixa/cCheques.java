package Movimento.PassCaixa;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class cCheques {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty s;
    private SimpleStringProperty banco;
    private SimpleStringProperty agencia;
    private SimpleStringProperty ncheque;
    private SimpleObjectProperty<BigDecimal> valor;

    public cCheques(int id, int s, String banco, String agencia, String ncheque, BigDecimal valor) {
        this.id = new SimpleIntegerProperty(id);
        this.s = new SimpleIntegerProperty(s);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.ncheque = new SimpleStringProperty(ncheque);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getS() { return s.get(); }
    public SimpleIntegerProperty sProperty() { return s; }
    public void setS(int s) { this.s.set(s); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getNcheque() { return ncheque.get(); }
    public SimpleStringProperty nchequeProperty() { return ncheque; }
    public void setNcheque(String ncheque) { this.ncheque.set(ncheque); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "banco=" + banco.get() +
               ", agencia=" + agencia.get() +
               ", ncheque=" + ncheque.get() +
               ", valor=" + new DecimalFormat("#,##0.00").format(valor.get());
    }
}
