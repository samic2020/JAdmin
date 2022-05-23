package Movimento.Razao;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class AvisoADMContas {
    private SimpleStringProperty registro;
    private SimpleObjectProperty<BigDecimal> Credito;
    private SimpleObjectProperty<BigDecimal> Debito;

    public AvisoADMContas(String registro, BigDecimal credito, BigDecimal debito) {
        this.registro = new SimpleStringProperty(registro);
        Credito = new SimpleObjectProperty<>(credito);
        Debito = new SimpleObjectProperty<>(debito);
    }

    public String getRegistro() { return registro.get(); }
    public SimpleStringProperty registroProperty() { return registro; }
    public void setRegistro(String registro) { this.registro.set(registro); }

    public BigDecimal getCredito() { return Credito.get(); }
    public SimpleObjectProperty<BigDecimal> creditoProperty() { return Credito; }
    public void setCredito(BigDecimal credito) { this.Credito.set(credito); }

    public BigDecimal getDebito() { return Debito.get(); }
    public SimpleObjectProperty<BigDecimal> debitoProperty() { return Debito; }
    public void setDebito(BigDecimal debito) { this.Debito.set(debito); }

    @Override
    public String toString() {
        return "AvisoADMContas{" +
                "registro=" + registro +
                ", Credito=" + Credito +
                ", Debito=" + Debito +
                '}';
    }
}
