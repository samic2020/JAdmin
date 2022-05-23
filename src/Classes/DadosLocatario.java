package Classes;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 24/05/17.
 */
public class DadosLocatario {
    SimpleStringProperty contrato;
    SimpleStringProperty nomelocatario;
    SimpleStringProperty endimovel;
    SimpleStringProperty numimovel;
    SimpleStringProperty cpltoimovel;
    SimpleStringProperty baiimovel;
    SimpleStringProperty cidimovel;
    SimpleStringProperty estimovel;
    SimpleStringProperty cepimovel;

    public DadosLocatario(String contrato, String nomelocatario, String endimovel, String numimovel, String cpltoimovel, String baiimovel, String cidimovel, String estimovel, String cepimovel) {
        this.contrato = new SimpleStringProperty(contrato);
        this.nomelocatario = new SimpleStringProperty(nomelocatario);
        this.endimovel = new SimpleStringProperty(endimovel);
        this.numimovel = new SimpleStringProperty(numimovel);
        this.cpltoimovel = new SimpleStringProperty(cpltoimovel);
        this.baiimovel = new SimpleStringProperty(baiimovel);
        this.cidimovel = new SimpleStringProperty(cidimovel);
        this.estimovel = new SimpleStringProperty(estimovel);
        this.cepimovel = new SimpleStringProperty(cepimovel);
    }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getNomelocatario() { return nomelocatario.get(); }
    public SimpleStringProperty nomelocatarioProperty() { return nomelocatario; }
    public void setNomelocatario(String nomelocatario) { this.nomelocatario.set(nomelocatario); }

    public String getEndimovel() { return endimovel.get(); }
    public SimpleStringProperty endimovelProperty() { return endimovel; }
    public void setEndimovel(String endimovel) { this.endimovel.set(endimovel); }

    public String getNumimovel() { return numimovel.get(); }
    public SimpleStringProperty numimovelProperty() { return numimovel; }
    public void setNumimovel(String numimovel) { this.numimovel.set(numimovel); }

    public String getCpltoimovel() { return cpltoimovel.get(); }
    public SimpleStringProperty cpltoimovelProperty() { return cpltoimovel; }
    public void setCpltoimovel(String cpltoimovel) { this.cpltoimovel.set(cpltoimovel); }

    public String getBaiimovel() { return baiimovel.get(); }
    public SimpleStringProperty baiimovelProperty() { return baiimovel; }
    public void setBaiimovel(String baiimovel) { this.baiimovel.set(baiimovel); }

    public String getCidimovel() { return cidimovel.get(); }
    public SimpleStringProperty cidimovelProperty() { return cidimovel; }
    public void setCidimovel(String cidimovel) { this.cidimovel.set(cidimovel); }

    public String getEstimovel() { return estimovel.get(); }
    public SimpleStringProperty estimovelProperty() { return estimovel; }
    public void setEstimovel(String estimovel) { this.estimovel.set(estimovel); }

    public String getCepimovel() { return cepimovel.get(); }
    public SimpleStringProperty cepimovelProperty() { return cepimovel; }
    public void setCepimovel(String cepimovel) { this.cepimovel.set(cepimovel); }
}
