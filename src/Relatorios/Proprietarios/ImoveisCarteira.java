package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class ImoveisCarteira {
    // rgimv, dtinicio, dtfim, dtaditamento, to_char(dtinicio, 'TMMonth')
    private SimpleIntegerProperty rgprp;
    private SimpleIntegerProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleObjectProperty<Date> dtinicio;
    private SimpleObjectProperty<Date> dtermino;
    private SimpleObjectProperty<Date> dtadito;
    private SimpleStringProperty reajuste;

    public ImoveisCarteira(int rgprp, int rgimv, String contrato, Date dtinicio, Date dtermino, Date dtadito, String reajuste) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.dtinicio = new SimpleObjectProperty<>(dtinicio);
        this.dtermino = new SimpleObjectProperty<>(dtermino);
        this.dtadito = new SimpleObjectProperty<>(dtadito);
        this.reajuste = new SimpleStringProperty(reajuste);
    }

    public int getRgprp() { return rgprp.get(); }
    public SimpleIntegerProperty rgprpProperty() { return rgprp; }
    public void setRgprp(int rgprp) { this.rgprp.set(rgprp); }

    public int getRgimv() { return rgimv.get(); }
    public SimpleIntegerProperty rgimvProperty() { return rgimv; }
    public void setRgimv(int rgimv) { this.rgimv.set(rgimv); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public Date getDtinicio() { return dtinicio.get(); }
    public SimpleObjectProperty<Date> dtinicioProperty() { return dtinicio; }
    public void setDtinicio(Date dtinicio) { this.dtinicio.set(dtinicio); }

    public Date getDtermino() { return dtermino.get(); }
    public SimpleObjectProperty<Date> dterminoProperty() { return dtermino; }
    public void setDtermino(Date dtermino) { this.dtermino.set(dtermino); }

    public Date getDtadito() { return dtadito.get(); }
    public SimpleObjectProperty<Date> dtaditoProperty() { return dtadito; }
    public void setDtadito(Date dtadito) { this.dtadito.set(dtadito); }

    public String getReajuste() { return reajuste.get(); }
    public SimpleStringProperty reajusteProperty() { return reajuste; }
    public void setReajuste(String reajuste) { this.reajuste.set(reajuste); }
}


