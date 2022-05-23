package Movimento;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

/**
 * Created by supervisor on 01/12/16.
 */
public class tbvAltera {
    private SimpleStringProperty  onde;
    private SimpleStringProperty  variavel;
    private SimpleIntegerProperty id;
    private SimpleBooleanProperty tag;
    private SimpleStringProperty  tipo;
    private SimpleStringProperty  desc;
    private SimpleStringProperty  cota;
    private SimpleStringProperty valor;
    private SimpleBooleanProperty rt;

    public tbvAltera(String onde, String variavel, int id, boolean tag, String tipo, String desc, String cota, String valor, boolean ret) {
        this.onde = new SimpleStringProperty(onde);
        this.variavel = new SimpleStringProperty(variavel);
        this.id = new SimpleIntegerProperty(id);
        this.tag = new SimpleBooleanProperty(tag);
        this.tipo = new SimpleStringProperty(tipo);
        this.desc = new SimpleStringProperty(desc);
        this.cota = new SimpleStringProperty(cota);
        this.valor = new SimpleStringProperty(valor);
        this.rt = new SimpleBooleanProperty(ret);
    }

    public String getVariavel() { return variavel.get(); }
    public SimpleStringProperty variavelProperty() { return variavel; }
    public void setVariavel(String variavel) { this.variavel.set(variavel); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getOnde() { return onde.get(); }
    public SimpleStringProperty ondeProperty() { return onde; }
    public void setOnde(String onde) { this.onde.set(onde); }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public boolean getTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }
    public ObservableBooleanValue isCheckedTag() { return tag; }
    public void setCheckedTag(Boolean tag) { this.tag.set(tag);}

    public String getDesc() { return desc.get(); }
    public SimpleStringProperty descProperty() { return desc; }
    public void setDesc(String desc) { this.desc.set(desc); }

    public String getCota() { return cota.get(); }
    public SimpleStringProperty cotaProperty() { return cota; }
    public void setCota(String cota) { this.cota.set(cota); }

    public String getValor() { return valor.get(); }
    public SimpleStringProperty valorProperty() { return valor; }
    public void setValor(String valor) { this.valor.set(valor); }

    public boolean isRt() { return rt.get(); }
    public SimpleBooleanProperty rtProperty() { return rt; }
    public void setRt(boolean rt) { this.rt.set(rt); }
    public ObservableBooleanValue isCheckedRt() { return rt; }
    public void setCheckedRet(Boolean ret) { this.rt.set(ret);}

    @Override
    public String toString() {
        return "tbvAltera{" +
                "onde=" + onde +
                ", id=" + id +
                ", tag=" + tag +
                ", desc=" + desc +
                ", cota=" + cota +
                ", valor=" + valor +
                ", reterncao=" + rt +
                '}';
    }
}
