package Imoveis.Pendencias;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PendenciaController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<tblPend> tPend;
    @FXML private TableColumn<tblPend, String> cDtHr;
    @FXML private TableColumn<tblPend, String> cImv;
    @FXML private TableColumn<tblPend, String> cNome;
    @FXML private TableColumn<tblPend, String> cEnd;
    @FXML private TableColumn<tblPend, String> cDoc;
    @FXML private TableColumn<tblPend, String> cTel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateImoveis();
    }

    private void populateImoveis() {
        List<tblPend> data = new ArrayList<tblPend>();
        ResultSet imv;
        String qSQL = "SELECT v.v_rgimv, v.v_nome, i.i_end || ', ' || i.i_num || ' - ' || i.i_cplto || ' / ' || i.i_bairro v_ender, v.v_documento, v.v_telefones, v.v_dthrsaida FROM visitas v INNER JOIN imoveis i on i.i_rgimv = v.v_rgimv WHERE not v.v_dthrsaida is null and v.v_dthrdevolucao is null;";
        String qrgimv = null, qnome = null, qender = null, qdoc = null;
        String qdthr = null; String[] qtmp = null;
        ObservableList<String> qtels = FXCollections.observableArrayList();
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                try {qrgimv = imv.getString("v_rgimv");} catch (SQLException e) {}
                try {qnome = imv.getString("v_nome");} catch (SQLException e) {}
                try {qender = imv.getString("v_ender");} catch (SQLException e) {}
                try {qdoc = imv.getString("v_documento");} catch (SQLException e) {}
                try {qdthr = imv.getString("v_dthrsaida");} catch (SQLException e) {}
                try {qtmp = imv.getString("v_telefones").split(";");} catch (SQLException e) {}
                if (qtmp.length > 0) {
                    for (String mtel : qtmp ) {
                        String[] ztel = mtel.split(",");
                        if (!ztel[0].isEmpty()) {
                            String stel = "(" + ztel[0].substring(0,3) + ") " + ztel[0].substring(3) + " - " + ztel[1];
                            qtels.add(stel);
                        }
                    }
                }
                // Avalia conforme o tipoprop (NORMAL, ESPOLIO, DIVIDIDO)

                data.add(new tblPend(qdthr, qrgimv, qnome, qender, qdoc, qtels));
            }
            imv.close();
        } catch (SQLException e) {}

        cDtHr.setCellValueFactory(new PropertyValueFactory<>("DtHr"));
        cImv.setCellValueFactory(new PropertyValueFactory<>("Imv"));
        cNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        cEnd.setCellValueFactory(new PropertyValueFactory<>("Ender"));
        cDoc.setCellValueFactory(new PropertyValueFactory<>("Doc"));
        cTel.setCellValueFactory(new PropertyValueFactory<>("Tels"));
        cTel.setCellFactory(ComboBoxTableCell.forTableColumn(qtels));

        tPend.setItems(FXCollections.observableArrayList(data));
    }

    public class tblPend {
        private String DtHr;
        private String Imv;
        private String Nome;
        private String Ender;
        private String Doc;
        private ObservableList<String> Tels;

        public tblPend(String dtHr, String imv, String nome, String ender, String doc, ObservableList<String> tels) {
            DtHr = dtHr;
            Imv = imv;
            Nome = nome;
            Ender = ender;
            Doc = doc;
            Tels = tels;
        }

        public String getDtHr() { return DtHr; }
        public void setDtHr(String dtHr) { DtHr = dtHr; }

        public String getImv() { return Imv; }
        public void setImv(String imv) { Imv = imv; }

        public String getNome() { return Nome; }
        public void setNome(String nome) { Nome = nome; }

        public String getEnder() { return Ender; }
        public void setEnder(String ender) { Ender = ender; }

        public String getDoc() { return Doc; }
        public void setDoc(String doc) { Doc = doc; }

        public ObservableList<String> getTels() { return Tels; }
        public void setTels(ObservableList<String> tels) { Tels = tels; }

        @Override
        public String toString() {
            return "tblPend{" +
                    "DtHr=" + DtHr +
                    ", Imv='" + Imv + '\'' +
                    ", Nome='" + Nome + '\'' +
                    ", Ender='" + Ender + '\'' +
                    ", Doc='" + Doc + '\'' +
                    ", Tels=" + Tels.toString() +
                    '}';
        }
    }
}
