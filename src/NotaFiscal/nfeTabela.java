package NotaFiscal;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class nfeTabela {
    private SimpleObjectProperty<Date> geracao;
    private SimpleIntegerProperty ntfiscal;
    private SimpleStringProperty status;
    private SimpleObjectProperty<Date> envio;
    private SimpleStringProperty nmxml;
    private SimpleStringProperty xml;

    public nfeTabela(Date geracao, Integer ntfiscal, String status, Date envio, String nmxml, String xml) {
        this.geracao = new SimpleObjectProperty<>(geracao);
        this.ntfiscal = new SimpleIntegerProperty(ntfiscal);
        this.status = new SimpleStringProperty(status);
        this.envio = new SimpleObjectProperty<>(envio);
        this.nmxml = new SimpleStringProperty(nmxml);
        this.xml = new SimpleStringProperty(xml);
    }

    public Date getGeracao() { return geracao.get(); }
    public SimpleObjectProperty<Date> geracaoProperty() { return geracao; }
    public void setGeracao(Date geracao) { this.geracao.set(geracao); }

    public int getNtfiscal() { return ntfiscal.get(); }
    public SimpleIntegerProperty ntfiscalProperty() { return ntfiscal; }
    public void setNtfiscal(int ntfiscal) { this.ntfiscal.set(ntfiscal); }

    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }

    public Date getEnvio() { return envio.get(); }
    public SimpleObjectProperty<Date> envioProperty() { return envio; }
    public void setEnvio(Date envio) { this.envio.set(envio); }

    public String getNmxml() { return nmxml.get(); }
    public SimpleStringProperty nmxmlProperty() { return nmxml; }
    public void setNmxml(String nmxml) { this.nmxml.set(nmxml); }

    public String getXml() { return xml.get(); }
    public SimpleStringProperty xmlProperty() { return xml; }
    public void setXml(String xml) { this.xml.set(xml); }
}
