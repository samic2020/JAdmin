package Administrador.Mensalao;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.util.Date;

public class MensalaoClass {
    SimpleStringProperty rgprp;
    SimpleStringProperty nome;
    SimpleStringProperty cpfcnpj;
    SimpleObjectProperty<Date> perApuracao;
    SimpleStringProperty codReceita;
    SimpleObjectProperty<Date> dataVenc;
    SimpleObjectProperty<BigDecimal> fisicaAL;
    SimpleObjectProperty<BigDecimal> juridicaAL;
    SimpleObjectProperty<BigDecimal> rendLiquido;
    SimpleObjectProperty<BigDecimal> irrfApurado;
    SimpleObjectProperty<BigDecimal> recFacult;

    public MensalaoClass(String rgprp, String nome, String cpfcnpj, Date perApuracao, String codReceita,
                         Date dataVenc, BigDecimal fisicaAL, BigDecimal juridicaAL, BigDecimal rendLiquido,
                         BigDecimal irrfApurado, BigDecimal recFacult) {
        this.rgprp = new SimpleStringProperty(rgprp);
        this.nome = new SimpleStringProperty(nome);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.perApuracao = new SimpleObjectProperty<>(perApuracao);
        this.codReceita = new SimpleStringProperty(codReceita);
        this.dataVenc = new SimpleObjectProperty<>(dataVenc);
        this.fisicaAL = new SimpleObjectProperty<>(fisicaAL);
        this.juridicaAL = new SimpleObjectProperty<>(juridicaAL);
        this.rendLiquido = new SimpleObjectProperty<>(rendLiquido);
        this.irrfApurado = new SimpleObjectProperty<>(irrfApurado);
        this.recFacult = new SimpleObjectProperty<>(recFacult);
    }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getCpfcnpj() { return cpfcnpj.get(); }
    public SimpleStringProperty cpfcnpjProperty() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj.set(cpfcnpj); }

    public Date getPerApuracao() { return perApuracao.get(); }
    public SimpleObjectProperty<Date> perApuracaoProperty() { return perApuracao; }
    public void setPerApuracao(Date perApuracao) { this.perApuracao.set(perApuracao); }

    public String getCodReceita() { return codReceita.get(); }
    public SimpleStringProperty codReceitaProperty() { return codReceita; }
    public void setCodReceita(String codReceita) { this.codReceita.set(codReceita); }

    public Date getDataVenc() { return dataVenc.get(); }
    public SimpleObjectProperty<Date> dataVencProperty() { return dataVenc; }
    public void setDataVenc(Date dataVenc) { this.dataVenc.set(dataVenc); }

    public BigDecimal getFisicaAL() { return fisicaAL.get(); }
    public SimpleObjectProperty<BigDecimal> fisicaALProperty() { return fisicaAL; }
    public void setFisicaAL(BigDecimal fisicaAL) { this.fisicaAL.set(fisicaAL); }

    public BigDecimal getJuridicaAL() { return juridicaAL.get(); }
    public SimpleObjectProperty<BigDecimal> juridicaALProperty() { return juridicaAL; }
    public void setJuridicaAL(BigDecimal juridicaAL) { this.juridicaAL.set(juridicaAL); }

    public BigDecimal getRendLiquido() { return rendLiquido.get(); }
    public SimpleObjectProperty<BigDecimal> rendLiquidoProperty() { return rendLiquido; }
    public void setRendLiquido(BigDecimal rendLiquido) { this.rendLiquido.set(rendLiquido); }

    public BigDecimal getIrrfApurado() { return irrfApurado.get(); }
    public SimpleObjectProperty<BigDecimal> irrfApuradoProperty() { return irrfApurado; }
    public void setIrrfApurado(BigDecimal irrfApurado) { this.irrfApurado.set(irrfApurado); }

    public BigDecimal getRecFacult() { return recFacult.get(); }
    public SimpleObjectProperty<BigDecimal> recFacultProperty() { return recFacult; }
    public void setRecFacult(BigDecimal recFacult) { this.recFacult.set(recFacult); }

    @Override
    public String toString() {
        return "MensalaoClass{" +
                "rgprp=" + rgprp +
                ", nome=" + nome +
                ", cpfcnpj=" + cpfcnpj +
                ", perApuracao=" + perApuracao +
                ", codReceita=" + codReceita +
                ", dataVenc=" + dataVenc +
                ", fisicaAL=" + fisicaAL +
                ", juridicaAL=" + juridicaAL +
                ", rendLiquido=" + rendLiquido +
                ", irrfApurado=" + irrfApurado +
                ", recFacult=" + recFacult +
                '}';
    }
}
