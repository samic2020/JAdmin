package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class ImoveisBaixa {
    // l_rgimv, l_dtbaixa, l_baixamotivo
    private SimpleIntegerProperty rgimv;
    private SimpleObjectProperty<Date> dtbaixa;
    private SimpleStringProperty bxmotivo;

    public ImoveisBaixa(int rgimv, Date dtbaixa, String bxmotivo) {
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.dtbaixa = new SimpleObjectProperty<>(dtbaixa);
        this.bxmotivo = new SimpleStringProperty(bxmotivo);
    }

    public int getRgimv() { return rgimv.get(); }
    public SimpleIntegerProperty rgimvProperty() { return rgimv; }
    public void setRgimv(int rgimv) { this.rgimv.set(rgimv); }

    public Date getDtbaixa() { return dtbaixa.get(); }
    public SimpleObjectProperty<Date> dtbaixaProperty() { return dtbaixa; }
    public void setDtbaixa(Date dtbaixa) { this.dtbaixa.set(dtbaixa); }

    public String getBxmotivo() { return bxmotivo.get(); }
    public SimpleStringProperty bxmotivoProperty() { return bxmotivo; }
    public void setBxmotivo(String bxmotivo) { this.bxmotivo.set(bxmotivo); }
}
