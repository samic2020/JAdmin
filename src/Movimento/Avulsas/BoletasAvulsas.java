package Movimento.Avulsas;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class BoletasAvulsas {
    private SimpleIntegerProperty id;
    private SimpleStringProperty banco;
    private SimpleStringProperty nnumero;
    private SimpleStringProperty nome;
    private SimpleObjectProperty<Date> vencimento;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty descricao;

    public BoletasAvulsas(int id, String banco, String nnumero, String nome, Date vencimento, BigDecimal valor, String descricao) {
        this.id = new SimpleIntegerProperty(id);
        this.banco = new SimpleStringProperty(banco);
        this.nnumero = new SimpleStringProperty(nnumero);
        this.nome = new SimpleStringProperty(nome);
        this.vencimento = new SimpleObjectProperty<>(vencimento);
        this.valor = new SimpleObjectProperty<>(valor);
        this.descricao = new SimpleStringProperty(descricao);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getNnumero() { return nnumero.get(); }
    public SimpleStringProperty nnumeroProperty() { return nnumero; }
    public void setNnumero(String nnumero) { this.nnumero.set(nnumero); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public Date getVencimento() { return vencimento.get(); }
    public SimpleObjectProperty<Date> vencimentoProperty() { return vencimento; }
    public void setVencimento(Date vencimento) { this.vencimento.set(vencimento); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getDescricao() { return descricao.get(); }
    public SimpleStringProperty descricaoProperty() { return descricao; }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    @Override
    public String toString() {
        return "BoletasAvulsas{" +
                "id=" + id +
                ", banco=" + banco +
                ", nnumero=" + nnumero +
                ", nome=" + nome +
                ", vencimento=" + vencimento +
                ", valor=" + valor +
                ", descricao=" + descricao +
                '}';
    }
}
