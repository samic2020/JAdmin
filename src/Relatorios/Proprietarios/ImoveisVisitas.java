package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class ImoveisVisitas {
    // v_nome, v_documento, to_char(to_date(v_dthrsaida,'DD/MM/YYYY'),'DD/MM/YYYY') v_data, v_historico
    private SimpleIntegerProperty rgimv;
    private SimpleStringProperty nome;
    private SimpleStringProperty documento;
    private SimpleObjectProperty<Date> data;
    private SimpleStringProperty historico;

    public ImoveisVisitas(int rgimv, String nome, String documento, Date data, String historico) {
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.nome = new SimpleStringProperty(nome);
        this.documento = new SimpleStringProperty(documento);
        this.data = new SimpleObjectProperty<>(data);
        this.historico = new SimpleStringProperty(historico);
    }

    public int getRgimv() { return rgimv.get(); }
    public SimpleIntegerProperty rgimvProperty() { return rgimv; }
    public void setRgimv(int rgimv) { this.rgimv.set(rgimv); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getDocumento() { return documento.get(); }
    public SimpleStringProperty documentoProperty() { return documento; }
    public void setDocumento(String documento) { this.documento.set(documento); }

    public Date getData() { return data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return data; }
    public void setData(Date data) { this.data.set(data); }

    public String getHistorico() { return historico.get(); }
    public SimpleStringProperty historicoProperty() { return historico; }
    public void setHistorico(String historico) { this.historico.set(historico); }
}
