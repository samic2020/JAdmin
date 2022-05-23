package Movimento.Avisos;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class TableRetencao {
    private SimpleIntegerProperty id;
    private SimpleStringProperty tipo; // T - Taxas / S - Seguros
    private SimpleStringProperty rgimv;
    private SimpleStringProperty ender;
    private SimpleStringProperty taxa; // Agua / Luz / Telefone / ...
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty dtrecebto;
    private SimpleStringProperty dtvencto;
    private SimpleBooleanProperty tag;

    public TableRetencao(int id, String tipo, String rgimv, String ender, String taxa, BigDecimal valor, String dtrecebto, String dtvencto, boolean tag) {
        this.id = new SimpleIntegerProperty(id);
        this.tipo = new SimpleStringProperty(tipo);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.ender = new SimpleStringProperty(ender);
        this.taxa = new SimpleStringProperty(taxa);
        this.valor = new SimpleObjectProperty<BigDecimal>(valor);
        this.dtrecebto = new SimpleStringProperty(dtrecebto);
        this.dtvencto = new SimpleStringProperty(dtvencto);
        this.tag = new SimpleBooleanProperty(tag);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getEnder() { return ender.get(); }
    public SimpleStringProperty enderProperty() { return ender; }
    public void setEnder(String ender) { this.ender.set(ender); }

    public String getTaxa() { return taxa.get(); }
    public SimpleStringProperty taxaProperty() { return taxa; }
    public void setTaxa(String taxa) { this.taxa.set(taxa); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getDtrecebto() { return dtrecebto.get(); }
    public SimpleStringProperty dtrecebtoProperty() { return dtrecebto; }
    public void setDtrecebto(String dtrecebto) { this.dtrecebto.set(dtrecebto); }

    public String getDtvencto() { return dtvencto.get(); }
    public SimpleStringProperty dtvenctoProperty() { return dtvencto; }
    public void setDtvencto(String dtvencto) { this.dtvencto.set(dtvencto); }

    public boolean isTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
}
