package Movimento.Alteracao;

import Classes.paramEvent;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by supervisor on 23/11/16.
 */
public class EnderecosController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String cnome = null; AnchorPane ChildAnchorPane;
    paramEvent pevent = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<Enderecos> tbvEnder;
    @FXML private TableColumn<Enderecos, String> tend;
    @FXML private TableColumn<Enderecos, String> tbai;
    @FXML private TableColumn<Enderecos, String> tcid;
    @FXML private TableColumn<Enderecos, String> test;
    @FXML private TableColumn<Enderecos, String> tcep;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            this.ChildAnchorPane = (AnchorPane)event.sparam[0];
            this.cnome = (String)event.sparam[1];
        });
        Platform.runLater(() -> {FillTableView();});
        tbvEnder.setOnMousePressed(event -> {
            TableView.TableViewSelectionModel<Enderecos> select = tbvEnder.getSelectionModel();
            if (event.getClickCount() == 2) {
                if (select != null) {
                    pevent = new paramEvent(new Object[]{select.getSelectedItems().get(0).getContrato(), true}, paramEvent.GET_PARAM);
                    ChildAnchorPane.fireEvent(pevent);
                }
            } else {
                if (select != null) {
                    pevent = new paramEvent(new Object[]{select.getSelectedItems().get(0).getContrato(),false}, paramEvent.GET_PARAM);
                    ChildAnchorPane.fireEvent(pevent);
                }
            }
        });
    }

    private void FillTableView() {
        List<Enderecos> data = new ArrayList<Enderecos>();
        ResultSet imv;
        String qSQL = "SELECT l.l_contrato, i.i_end || ', ' || i.i_num || ' ' || i.i_cplto AS i_ender, i.i_bairro, i.i_cidade, i.i_estado, i.i_cep FROM locatarios l, imoveis i WHERE l.l_rgimv = i.i_rgimv AND Upper(i.i_situacao) = 'OCUPADO' AND l_f_nome LIKE '%s' OR l_j_razao LIKE '%s';";
        qSQL = String.format(qSQL, this.cnome + "%", this.cnome + "%");
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qender = null;
                String qbairro = null, qcidade = null;
                String qestado = null, qcep = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qender = imv.getString("i_ender");} catch (SQLException e) {}
                try {qbairro = imv.getString("i_bairro");} catch (SQLException e) {}
                try {qcidade = imv.getString("i_cidade");} catch (SQLException e) {}
                try {qestado = imv.getString("i_estado");} catch (SQLException e) {}
                try {qcep = imv.getString("i_cep");} catch (SQLException e) {}

                data.add(new Enderecos(qcontrato, qender, qbairro, qcidade, qestado, qcep));
            }
            imv.close();
        } catch (SQLException e) {}

        tend.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        tbai.setCellValueFactory(new PropertyValueFactory<>("bairro"));
        tcid.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        test.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tcep.setCellValueFactory(new PropertyValueFactory<>("cep"));

        tbvEnder.setItems(FXCollections.observableArrayList(data));
    }

    public class Enderecos {
        private SimpleStringProperty contrato;
        private SimpleStringProperty endereco;
        private SimpleStringProperty bairro;
        private SimpleStringProperty cidade;
        private SimpleStringProperty estado;
        private SimpleStringProperty cep;

        public Enderecos(String contrato, String endereco, String bairro, String cidade, String estado, String cep) {
            this.contrato = new SimpleStringProperty(contrato);
            this.endereco = new SimpleStringProperty(endereco);
            this.bairro = new SimpleStringProperty(bairro);
            this.cidade = new SimpleStringProperty(cidade);
            this.estado = new SimpleStringProperty(estado);
            this.cep = new SimpleStringProperty(cep);
        }

        public String getContrato() { return contrato.get(); }
        public SimpleStringProperty contratoProperty() { return contrato; }
        public void setContrato(String contrato) { this.contrato.set(contrato); }

        public String getEndereco() { return endereco.get(); }
        public SimpleStringProperty enderecoProperty() { return endereco; }
        public void setEndereco(String endereco) { this.endereco.set(endereco); }

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

        @Override
        public String toString() {
            return "Enderecos{" +
                    "contrato=" + contrato +
                    ", endereco=" + endereco +
                    ", bairro=" + bairro +
                    ", cidade=" + cidade +
                    ", estado=" + estado +
                    ", cep=" + cep +
                    '}';
        }
    }

}
