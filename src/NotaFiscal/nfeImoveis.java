package NotaFiscal;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class nfeImoveis {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty endereco;
    private SimpleObjectProperty<Date> vencto;
    private SimpleObjectProperty<BigDecimal> aluguel;
    private SimpleIntegerProperty ntfiscal;
    private SimpleIntegerProperty comissao;

    public nfeImoveis(int id, String rgimv, String contrato, String endereco, Date vencto, BigDecimal aluguel, int ntfiscal, int comissao) {
        this.id = new SimpleIntegerProperty(id);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.endereco = new SimpleStringProperty(endereco);
        this.vencto = new SimpleObjectProperty<>(vencto);
        this.aluguel = new SimpleObjectProperty<>(aluguel);
        this.ntfiscal = new SimpleIntegerProperty(ntfiscal);
        this.comissao = new SimpleIntegerProperty(comissao);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getEndereco() { return endereco.get(); }
    public SimpleStringProperty enderecoProperty() { return endereco; }
    public void setEndereco(String endereco) { this.endereco.set(endereco); }

    public Date getVencto() { return vencto.get(); }
    public SimpleObjectProperty<Date> venctoProperty() { return vencto; }
    public void setVencto(Date vencto) { this.vencto.set(vencto); }

    public BigDecimal getAluguel() { return aluguel.get(); }
    public SimpleObjectProperty<BigDecimal> aluguelProperty() { return aluguel; }
    public void setAluguel(BigDecimal aluguel) { this.aluguel.set(aluguel); }

    public int getNtfiscal() { return ntfiscal.get(); }
    public SimpleIntegerProperty ntfiscalProperty() { return ntfiscal; }
    public void setNtfiscal(int ntfiscal) { this.ntfiscal.set(ntfiscal); }

    public int getComissao() { return comissao.get(); }
    public SimpleIntegerProperty comissaoProperty() { return comissao; }
    public void setComissao(int comissao) { this.comissao.set(comissao); }
}
