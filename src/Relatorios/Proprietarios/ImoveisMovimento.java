package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class ImoveisMovimento {
    // rgimv, contrato, to_char(dtvencimento,'TMMon') as mes, mensal
    private SimpleIntegerProperty rgprp;
    private SimpleIntegerProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty mes;
    private SimpleStringProperty ano;
    private SimpleObjectProperty<BigDecimal> aluguel;

    public ImoveisMovimento(int rgprp, int rgimv, String contrato, String mes, String ano, BigDecimal aluguel) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.mes = new SimpleStringProperty(mes);
        this.ano = new SimpleStringProperty(ano);
        this.aluguel = new SimpleObjectProperty<>(aluguel);
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

    public String getMes() { return mes.get(); }
    public SimpleStringProperty mesProperty() { return mes; }
    public void setMes(String mes) { this.mes.set(mes); }

    public String getAno() { return ano.get(); }
    public SimpleStringProperty anoProperty() { return ano; }
    public void setAno(String ano) { this.ano.set(ano); }

    public BigDecimal getAluguel() { return aluguel.get(); }
    public SimpleObjectProperty<BigDecimal> aluguelProperty() { return aluguel; }
    public void setAluguel(BigDecimal aluguel) { this.aluguel.set(aluguel); }
}
