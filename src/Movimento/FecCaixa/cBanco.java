package Movimento.FecCaixa;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class cBanco {
    private SimpleStringProperty banco;
    private SimpleStringProperty agencia;
    private SimpleStringProperty ncheque;
    private SimpleObjectProperty<Date> data;
    private SimpleObjectProperty<BigDecimal> valor;

    public cBanco(String banco, String agencia, String ncheque, Date data, BigDecimal valor) {
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.ncheque = new SimpleStringProperty(ncheque);
        this.data = new SimpleObjectProperty<>(data);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getNcheque() { return ncheque.get(); }
    public SimpleStringProperty nchequeProperty() { return ncheque; }
    public void setNcheque(String ncheque) { this.ncheque.set(ncheque); }

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "cBanco{" +
                "banco=" + banco +
                ", agencia=" + agencia +
                ", ncheque=" + ncheque +
                ", data=" + data +
                ", valor=" + valor +
                '}';
    }
}
