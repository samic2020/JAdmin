package Gerencia;

import javafx.beans.property.SimpleStringProperty;

public class divSec {
    private SimpleStringProperty rgprp_dv;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty divisao;

    public String getRgprp_dv() { return rgprp_dv.get(); }
    public SimpleStringProperty rgprp_dvProperty() { return rgprp_dv; }
    public void setRgprp_dv(String rgprp_dv) { this.rgprp_dv.set(rgprp_dv); }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getDivisao() { return divisao.get(); }
    public SimpleStringProperty divisaoProperty() { return divisao; }
    public void setDivisao(String divisao) { this.divisao.set(divisao); }

    public divSec(String rgprp_dv, String rgprp, String rgimv, String divisao) {
        this.rgprp_dv = new SimpleStringProperty(rgprp_dv);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.divisao = new SimpleStringProperty(divisao);
    }

    @Override
    public String toString() {
        return "divSec{" +
                "rgprp_dv=" + rgprp_dv +
                ", rgprp=" + rgprp +
                ", rgimv=" + rgimv +
                ", divisao=" + divisao +
                '}';
    }
}
