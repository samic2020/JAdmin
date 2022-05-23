package Relatorios.Saldos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class ReportClass {
    private SimpleStringProperty registro;
    private SimpleStringProperty descricao;
    private SimpleObjectProperty<BigDecimal> saldo;

    public ReportClass(String registro, String descricao, BigDecimal saldo) {
        this.registro = new SimpleStringProperty(registro);
        this.descricao = new SimpleStringProperty(descricao);
        this.saldo = new SimpleObjectProperty<>(saldo);
    }

    public String getRegistro() { return registro.get(); }
    public SimpleStringProperty registroProperty() { return registro; }
    public void setRegistro(String registro) { this.registro.set(registro); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public BigDecimal getSaldo() { return saldo.get(); }
    public SimpleObjectProperty<BigDecimal> saldoProperty() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo.set(saldo); }

    @Override
    public String toString() {
        return "ReportClass{" +
                "registro=" + registro +
                ", descricao=" + descricao +
                ", total=" + saldo +
                '}';
    }
}
