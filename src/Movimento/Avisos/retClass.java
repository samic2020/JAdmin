package Movimento.Avisos;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class retClass {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty end;
    private SimpleStringProperty taxa;
    private SimpleObjectProperty<BigDecimal> valor;
    private SimpleStringProperty Recto;
    private SimpleStringProperty Vencto;
    private SimpleBooleanProperty tag;

    public retClass() {
        this.id = new SimpleIntegerProperty(0);
        this.rgimv = new SimpleStringProperty(null);
        this.end = new SimpleStringProperty(null);
        this.taxa = new SimpleStringProperty(null);
        this.valor = new SimpleObjectProperty<>(new BigDecimal("0"));
        this.Recto = new SimpleStringProperty(null);
        this.Vencto = new SimpleStringProperty(null);
        this.tag = new SimpleBooleanProperty(false);
    }

    public retClass(Integer id, String rgimv, String end, String taxa, BigDecimal valor, String Recto, String Vencto, Boolean tag) {
        this.id = new SimpleIntegerProperty(id);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.end = new SimpleStringProperty(end);
        this.taxa = new SimpleStringProperty(taxa);
        this.valor = new SimpleObjectProperty<>(valor);
        this.Recto = new SimpleStringProperty(Recto);
        this.Vencto = new SimpleStringProperty(Vencto);
        this.tag = new SimpleBooleanProperty(tag);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getEnd() { return end.get(); }
    public SimpleStringProperty endProperty() { return end; }
    public void setEnd(String end) { this.end.set(end); }

    public String getTaxa() { return taxa.get(); }
    public SimpleStringProperty taxaProperty() { return taxa; }
    public void setTaxa(String taxa) { this.taxa.set(taxa); }

    public BigDecimal getValor() { return valor.get(); }
    public SimpleObjectProperty<BigDecimal> valorProperty() { return valor; }
    public void setValor(BigDecimal valor) { this.valor.set(valor); }

    public String getRecto() { return Recto.get(); }
    public SimpleStringProperty rectoProperty() { return Recto; }
    public void setRecto(String recto) { this.Recto.set(recto); }

    public String getVencto() { return Vencto.get(); }
    public SimpleStringProperty venctoProperty() { return Vencto; }
    public void setVencto(String vencto) { this.Vencto.set(vencto); }

    public boolean isTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
}
