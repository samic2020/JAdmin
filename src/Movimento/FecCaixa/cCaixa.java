package Movimento.FecCaixa;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class cCaixa {
    private SimpleStringProperty doc; // AVI;EXT;REC;...
    private SimpleIntegerProperty aut;
    private SimpleObjectProperty<Date> hora;
    private SimpleStringProperty registro; // RGPRP; RGIMV; CONTRATO
    private SimpleStringProperty situacao; // Extorno;''
    private SimpleStringProperty operacao; // CRE;DEB
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty tipo; // DN;CH;CT;BC;...
    private List<cBanco> databanco; // BCO, AG, NCHEQUE, DATA, VALOR

    public cCaixa(String doc, int aut, Date hora, String registro, String situacao, String operacao, BigDecimal valor, String tipo, List<cBanco> databanco) {
        this.doc = new SimpleStringProperty(doc);
        this.aut = new SimpleIntegerProperty(aut);
        this.hora = new SimpleObjectProperty<>(hora);
        this.registro = new SimpleStringProperty(registro);
        this.operacao = new SimpleStringProperty(operacao);
        this.situacao = new SimpleStringProperty(situacao);
        this.valor = new SimpleObjectProperty<>(valor);
        this.tipo = new SimpleStringProperty(tipo);
        this.databanco = databanco;
    }

    public String getDoc() { return doc.get(); }
    public SimpleStringProperty docProperty() { return doc; }
    public void setDoc(String doc) { this.doc.set(doc); }

    public int getAut() { return aut.get(); }
    public SimpleIntegerProperty autProperty() { return aut; }
    public void setAut(int aut) { this.aut.set(aut); }

    public Date getHora() { return hora.get(); }
    public SimpleObjectProperty<Date> horaProperty() { return hora; }
    public void setHora(Date hora) { this.hora.set(hora); }

    public String getRegistro() { return registro.get(); }
    public SimpleStringProperty registroProperty() { return registro; }
    public void setRegistro(String registro) { this.registro.set(registro); }

    public String getOperacao() { return operacao.get(); }
    public SimpleStringProperty operacaoProperty() { return operacao; }
    public void setOperacao(String operacao) { this.operacao.set(operacao); }

    public String getSituacao() { return situacao.get(); }
    public SimpleStringProperty situacaoProperty() { return situacao; }
    public void setSituacao(String situacao) { this.situacao.set(situacao); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public List<cBanco> getDatabanco() { return databanco; }
    public void setDatabanco(List<cBanco> databanco) { this.databanco = databanco; }
}
