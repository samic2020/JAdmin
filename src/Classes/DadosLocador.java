package Classes;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 24/05/17.
 */
public class DadosLocador {
    SimpleStringProperty rgprp;
    SimpleStringProperty nomeprop;
    SimpleFloatProperty comissao;

    public DadosLocador(String rgprp, String nomeprop, float comissao) {
        this.rgprp = new SimpleStringProperty(rgprp);
        this.nomeprop = new SimpleStringProperty(nomeprop);
        this.comissao = new SimpleFloatProperty(comissao);
    }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getNomeprop() { return nomeprop.get(); }
    public SimpleStringProperty nomepropProperty() { return nomeprop; }
    public void setNomeprop(String nomeprop) { this.nomeprop.set(nomeprop); }

    public float getComissao() { return comissao.get(); }
    public SimpleFloatProperty comissaoProperty() { return comissao; }
    public void setComissao(float comissao) { this.comissao.set(comissao); }
}
