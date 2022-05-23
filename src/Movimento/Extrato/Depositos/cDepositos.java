package Movimento.Extrato.Depositos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class cDepositos {
    private SimpleStringProperty rgprp;
    private SimpleStringProperty nome;
    private SimpleObjectProperty<BigDecimal> valor;

    public cDepositos(String rgprp, String nome, BigDecimal valor) {
        this.rgprp = new SimpleStringProperty(rgprp);
        this.nome = new SimpleStringProperty(nome);
        this.valor = new SimpleObjectProperty<>(valor);
    }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    @Override
    public String toString() {
        return "cDepositos{" +
                "rgprp=" + rgprp +
                ", nome=" + nome +
                ", valor=" + valor +
                '}';
    }
}
