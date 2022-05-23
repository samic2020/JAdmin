package Movimento.PreAviso;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class TablePreAviso {
    private SimpleIntegerProperty id;
    private SimpleStringProperty conta;
    private SimpleStringProperty codigo;
    private SimpleStringProperty tipo;
    private SimpleStringProperty texto;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty usuario;
    private SimpleObjectProperty<Date> datalanc;
    private SimpleObjectProperty<Date> data;
    private SimpleStringProperty obs;
    private SimpleIntegerProperty aut;

    public TablePreAviso() {
        this.id = new SimpleIntegerProperty(0);
        this.conta = new SimpleStringProperty(null);
        this.codigo = new SimpleStringProperty(null);
        this.tipo = new SimpleStringProperty(null);
        this.texto = new SimpleStringProperty(null);
        this.valor = new SimpleObjectProperty<BigDecimal>(new BigDecimal("0"));
        this.usuario = new SimpleStringProperty(null);
        this.datalanc = new SimpleObjectProperty<Date>(null);
        this.data = new SimpleObjectProperty<Date>(null);
        this.obs = new SimpleStringProperty(null);
        this.aut = new SimpleIntegerProperty(0);
    }

    public TablePreAviso(int id, String conta, String codigo, String tipo, String texto, BigDecimal valor, String usuario, Date datalanc, Date data) {
        this.id = new SimpleIntegerProperty(id);
        this.conta = new SimpleStringProperty(conta);
        this.codigo = new SimpleStringProperty(codigo);
        this.tipo = new SimpleStringProperty(tipo);
        this.texto = new SimpleStringProperty(texto);
        this.valor = new SimpleObjectProperty<BigDecimal>(valor);
        this.usuario = new SimpleStringProperty(usuario);
        this.datalanc = new SimpleObjectProperty<Date>(datalanc);
        this.data = new SimpleObjectProperty<Date>(data);
    }

    public TablePreAviso(int id, String conta, String codigo, String tipo, String texto, BigDecimal valor, String usuario, Date datalanc, String obs) {
        this.id = new SimpleIntegerProperty(id);
        this.conta = new SimpleStringProperty(conta);
        this.codigo = new SimpleStringProperty(codigo);
        this.tipo = new SimpleStringProperty(tipo);
        this.texto = new SimpleStringProperty(texto);
        this.valor = new SimpleObjectProperty<BigDecimal>(valor);
        this.usuario = new SimpleStringProperty(usuario);
        this.datalanc = new SimpleObjectProperty<Date>(datalanc);
        this.obs = new SimpleStringProperty(obs);
    }

    public TablePreAviso(int id, String conta, String codigo, String tipo, String texto, BigDecimal valor, String usuario, Date datalanc, String obs, int aut) {
        this.id = new SimpleIntegerProperty(id);
        this.conta = new SimpleStringProperty(conta);
        this.codigo = new SimpleStringProperty(codigo);
        this.tipo = new SimpleStringProperty(tipo);
        this.texto = new SimpleStringProperty(texto);
        this.valor = new SimpleObjectProperty<BigDecimal>(valor);
        this.usuario = new SimpleStringProperty(usuario);
        this.datalanc = new SimpleObjectProperty<Date>(datalanc);
        this.obs = new SimpleStringProperty(obs);
        this.aut = new SimpleIntegerProperty(aut);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getConta() { return conta.get(); }
    public SimpleStringProperty contaProperty() { return conta; }
    public void setConta(String conta) { this.conta.set(conta); }

    public String getCodigo() { return codigo.get(); }
    public SimpleStringProperty codigoProperty() { return codigo; }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getTexto() { return texto.get(); }
    public SimpleStringProperty textoProperty() { return texto; }
    public void setTexto(String texto) { this.texto.set(texto); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getUsuario() { return usuario.get(); }
    public SimpleStringProperty usuarioProperty() { return usuario; }
    public void setUsuario(String usuario) { this.usuario.set(usuario); }

    public Date getDatalanc() { return datalanc.get(); }
    public SimpleObjectProperty<Date> datalancProperty() { return datalanc; }
    public void setDatalanc(Date datalanc) { this.datalanc.set(datalanc); }

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    public String getObs() { return obs.get(); }
    public SimpleStringProperty obsProperty() { return obs; }
    public void setObs(String obs) { this.obs.set(obs); }

    public int getAut() { return aut.get(); }
    public SimpleIntegerProperty autProperty() { return aut; }
    public void setAut(int aut) { this.aut.set(aut); }

    @Override
    public String toString() {
        return "TablePreAviso{" +
                "id=" + id +
                ", conta=" + conta +
                ", codigo=" + codigo +
                ", tipo=" + tipo +
                ", texto=" + texto +
                ", valor=" + valor +
                ", usuario=" + usuario +
                ", datalanc=" + datalanc +
                ", data=" + data +
                ", obs=" + obs +
                ", aut=" + aut +
                '}';
    }
}
