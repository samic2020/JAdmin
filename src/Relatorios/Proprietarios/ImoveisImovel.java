package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ImoveisImovel {
    // i_rgprp, i_rgimv, i_tipo, Trim(i_end) || ', ' || i_num || ' - ' || i_cplto ender, i_bairro, i_cidade, i_estado, i_cep, i_situacao
    private SimpleIntegerProperty rgprp;
    private SimpleIntegerProperty rgimv;
    private SimpleStringProperty ender;
    private SimpleStringProperty bairro;
    private SimpleStringProperty cidade;
    private SimpleStringProperty estado;
    private SimpleStringProperty cep;
    private SimpleStringProperty situacao; // Vazio; Ocupado; Açaõ; Etc...

    // Vazio e Etc...
    private ImoveisBaixa baixa;
    private ImoveisVisitas[] visitas;

    // Ocupado
    private  ImoveisCarteira carteira;
    private ImoveisMovimento[] movimentos;

    public ImoveisImovel(int rgprp, int rgimv, String ender, String bairro, String cidade, String estado, String cep, String situacao) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.ender = new SimpleStringProperty(ender);
        this.bairro = new SimpleStringProperty(bairro);
        this.cidade = new SimpleStringProperty(cidade);
        this.estado = new SimpleStringProperty(estado);
        this.cep = new SimpleStringProperty(cep);
        this.situacao = new SimpleStringProperty(situacao);
    }

    public ImoveisImovel(int rgprp, int rgimv, String ender, String bairro, String cidade, String estado, String cep, String situacao, ImoveisBaixa baixa, ImoveisVisitas[] visitas, ImoveisCarteira carteira, ImoveisMovimento[] movimentos) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.rgimv = new SimpleIntegerProperty(rgimv);
        this.ender = new SimpleStringProperty(ender);
        this.bairro = new SimpleStringProperty(bairro);
        this.cidade = new SimpleStringProperty(cidade);
        this.estado = new SimpleStringProperty(estado);
        this.cep = new SimpleStringProperty(cep);
        this.situacao = new SimpleStringProperty(situacao);
        this.baixa = baixa;
        this.visitas = visitas;
        this.carteira = carteira;
        this.movimentos = movimentos;
    }

    public int getRgprp() { return rgprp.get(); }
    public SimpleIntegerProperty rgprpProperty() { return rgprp; }
    public void setRgprp(int rgprp) { this.rgprp.set(rgprp); }

    public int getRgimv() { return rgimv.get(); }
    public SimpleIntegerProperty rgimvProperty() { return rgimv; }
    public void setRgimv(int rgimv) { this.rgimv.set(rgimv); }

    public String getEnder() { return ender.get(); }
    public SimpleStringProperty enderProperty() { return ender; }
    public void setEnder(String ender) { this.ender.set(ender); }

    public String getBairro() { return bairro.get(); }
    public SimpleStringProperty bairroProperty() { return bairro; }
    public void setBairro(String bairro) { this.bairro.set(bairro); }

    public String getCidade() { return cidade.get(); }
    public SimpleStringProperty cidadeProperty() { return cidade; }
    public void setCidade(String cidade) { this.cidade.set(cidade); }

    public String getEstado() { return estado.get(); }
    public SimpleStringProperty estadoProperty() { return estado; }
    public void setEstado(String estado) { this.estado.set(estado); }

    public String getCep() { return cep.get(); }
    public SimpleStringProperty cepProperty() { return cep; }
    public void setCep(String cep) { this.cep.set(cep); }

    public String getSituacao() { return situacao.get(); }
    public SimpleStringProperty situacaoProperty() { return situacao; }
    public void setSituacao(String situacao) { this.situacao.set(situacao); }

    public ImoveisBaixa getBaixa() { return baixa; }
    public void setBaixa(ImoveisBaixa baixa) { this.baixa = baixa; }

    public ImoveisVisitas[] getVisitas() { return visitas; }
    public void setVisitas(ImoveisVisitas[] visitas) { this.visitas = visitas; }

    public ImoveisCarteira getCarteira() { return carteira; }
    public void setCarteira(ImoveisCarteira carteira) { this.carteira = carteira; }

    public ImoveisMovimento[] getMovimentos() { return movimentos; }
    public void setMovimentos(ImoveisMovimento[] movimentos) { this.movimentos = movimentos; }
}
