package Imposto;

import Funcoes.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by supervisor on 05/09/16.
 */
public class TabelaIRRFController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField irrf_mesano;
    @FXML private TextField irf_faixa1;
    @FXML private TextField irf_faixa2;
    @FXML private TextField irf_faixa3;
    @FXML private TextField irf_faixa4;
    @FXML private TextField irf_faixa5;
    @FXML private TextField irf_aliquota2;
    @FXML private TextField irf_aliquota3;
    @FXML private TextField irf_aliquota4;
    @FXML private TextField irf_aliquota5;
    @FXML private TextField irf_deducao2;
    @FXML private TextField irf_deducao3;
    @FXML private TextField irf_deducao4;
    @FXML private TextField irf_deducao5;
    @FXML private TableView<irrf> tbvIRF;
    @FXML private TableColumn<irrf, Integer> tbvIRF_id;
    @FXML private TableColumn<irrf, String> tbvIRF_mesano;
    @FXML private Button btnInserir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        irrf_mesano.setText(Dates.DateFormata("MM/yyyy", DbMain.getDateTimeServer()));

        MaskFieldUtil.monetaryField(irf_faixa1);
        MaskFieldUtil.monetaryField(irf_faixa2);
        MaskFieldUtil.monetaryField(irf_faixa3);
        MaskFieldUtil.monetaryField(irf_faixa4);
        MaskFieldUtil.monetaryField(irf_faixa5);
        MaskFieldUtil.monetaryField(irf_aliquota2);
        MaskFieldUtil.monetaryField(irf_aliquota3);
        MaskFieldUtil.monetaryField(irf_aliquota4);
        MaskFieldUtil.monetaryField(irf_aliquota5);
        MaskFieldUtil.monetaryField(irf_deducao2);
        MaskFieldUtil.monetaryField(irf_deducao3);
        MaskFieldUtil.monetaryField(irf_deducao4);
        MaskFieldUtil.monetaryField(irf_deducao5);

        tbvIRF.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION,"Confirma exclusão?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String tSql = "DELETE FROM irrf WHERE id = %s;";
                    tSql = String.format(tSql, tbvIRF.getSelectionModel().getSelectedItem().getId());
                    try {
                        if (conn.ExecutarComando(tSql) <= 0) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao gravar!");
                            alert.setTitle("Erro");
                            alert.showAndWait();
                        } else ListarIRRF();
                    } catch (Exception e) {
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Período excluid!");
                alert.setTitle("Erro");
                alert.showAndWait();
            }
        });

        btnInserir.setOnAction(event -> {
            if (!VerifyIrrf(irrf_mesano.getText().trim())) {
                String tSql = "INSERT INTO irrf (mesano, faixa1, faixa2, aliquota2, deducao2, " +
                        "faixa3, aliquota3, deducao3, " +
                        "faixa4, aliquota4, deducao4, " +
                        "faixa5, aliquota5, deducao5" +
                        ") VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
                tSql = String.format(tSql,
                        irrf_mesano.getText().trim(),
                        LerValor.StringToFloat(irf_faixa1.getText()),
                        LerValor.StringToFloat(irf_faixa2.getText()),
                        LerValor.StringToFloat(irf_aliquota2.getText()),
                        LerValor.StringToFloat(irf_deducao2.getText()),
                        LerValor.StringToFloat(irf_faixa3.getText()),
                        LerValor.StringToFloat(irf_aliquota3.getText()),
                        LerValor.StringToFloat(irf_deducao3.getText()),
                        LerValor.StringToFloat(irf_faixa4.getText()),
                        LerValor.StringToFloat(irf_aliquota4.getText()),
                        LerValor.StringToFloat(irf_deducao4.getText()),
                        LerValor.StringToFloat(irf_faixa5.getText()),
                        LerValor.StringToFloat(irf_aliquota5.getText()),
                        LerValor.StringToFloat(irf_deducao5.getText())
                );

                try {
                    if (conn.ExecutarComando(tSql) <= 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,"Erro ao gravar!");
                        alert.setTitle("Erro");
                        alert.showAndWait();
                    } else ListarIRRF();
                } catch (Exception e) {}
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"IRRF do período já cadastrado!");
                alert.setTitle("Erro");
                alert.showAndWait();
            }
        });

        tbvIRF.setOnMouseReleased(event -> {
            ShowIrrf();
        });

        ListarIRRF();
    }

    private void ShowIrrf() {
        irf_faixa1.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().faixa1.floatValue(),2));
        irf_faixa2.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().faixa2.floatValue(),2));
        irf_aliquota2.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().aliquota2.floatValue(),2));
        irf_deducao2.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().deducao2.floatValue(),2));
        irf_faixa3.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().faixa3.floatValue(),2));
        irf_aliquota3.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().aliquota3.floatValue(),2));
        irf_deducao3.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().deducao3.floatValue(),2));
        irf_faixa4.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().faixa4.floatValue(),2));
        irf_aliquota4.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().aliquota4.floatValue(),2));
        irf_deducao4.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().deducao4.floatValue(),2));
        irf_faixa5.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().faixa5.floatValue(),2));
        irf_aliquota5.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().aliquota5.floatValue(),2));
        irf_deducao5.setText(LerValor.floatToCurrency(tbvIRF.getSelectionModel().getSelectedItem().deducao5.floatValue(),2));
    }

    private boolean VerifyIrrf(String tmesano) {
        boolean iscad = false;
        String tSql = "SELECT * FROM irrf WHERE mesano = '%s';";
        tSql = String.format(tSql, tmesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) { iscad = true; }
        } catch (SQLException e) {}
        finally { try {trs.close();} catch (SQLException e) {} }
        return iscad;
    }

    private void ListarIRRF() {
        List<irrf> data = new ArrayList<irrf>();
        ResultSet irs;
        String qSQL = "SELECT * FROM irrf ORDER BY mesano;";
        try {
            irs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (irs.next()) {
                Integer qid = -1;
                String qmesano = null;
                BigDecimal qfaixa1 = null;
                BigDecimal qfaixa2 = null;
                BigDecimal qfaixa3 = null;
                BigDecimal qfaixa4 = null;
                BigDecimal qfaixa5 = null;
                BigDecimal qaliquota2 = null;
                BigDecimal qaliquota3 = null;
                BigDecimal qaliquota4 = null;
                BigDecimal qaliquota5 = null;
                BigDecimal qdeducao2 = null;
                BigDecimal qdeducao3 = null;
                BigDecimal qdeducao4 = null;
                BigDecimal qdeducao5 = null;

                try {qid = irs.getInt("id");} catch (SQLException e) {}
                try {qmesano = irs.getString("mesano");} catch (SQLException e) {}
                try {qfaixa1 = irs.getBigDecimal("faixa1");} catch (SQLException e) {}
                try {qfaixa2 = irs.getBigDecimal("faixa2");} catch (SQLException e) {}
                try {qfaixa3 = irs.getBigDecimal("faixa3");} catch (SQLException e) {}
                try {qfaixa4 = irs.getBigDecimal("faixa4");} catch (SQLException e) {}
                try {qfaixa5 = irs.getBigDecimal("faixa5");} catch (SQLException e) {}
                try {qaliquota2 = irs.getBigDecimal("aliquota2");} catch (SQLException e) {}
                try {qaliquota3 = irs.getBigDecimal("aliquota3");} catch (SQLException e) {}
                try {qaliquota4 = irs.getBigDecimal("aliquota4");} catch (SQLException e) {}
                try {qaliquota5 = irs.getBigDecimal("aliquota5");} catch (SQLException e) {}
                try {qdeducao2 = irs.getBigDecimal("deducao2");} catch (SQLException e) {}
                try {qdeducao3 = irs.getBigDecimal("deducao3");} catch (SQLException e) {}
                try {qdeducao4 = irs.getBigDecimal("deducao4");} catch (SQLException e) {}
                try {qdeducao5 = irs.getBigDecimal("deducao5");} catch (SQLException e) {}
                data.add(
                        new irrf(
                                qid,
                                qmesano,
                                qfaixa1,
                                qfaixa2,
                                qaliquota2,
                                qdeducao2,
                                qfaixa3,
                                qaliquota3,
                                qdeducao3,
                                qfaixa4,
                                qaliquota4,
                                qdeducao4,
                                qfaixa5,
                                qaliquota5,
                                qdeducao5
                        )
                );
            }
            irs.close();
        } catch (SQLException e) {}

        tbvIRF_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbvIRF_mesano.setCellValueFactory(new PropertyValueFactory<>("mesano"));

        tbvIRF.setItems(FXCollections.observableArrayList(data));
    }

    public class irrf {
        int id;
        String mesano;
        BigDecimal faixa1;
        BigDecimal faixa2;
        BigDecimal aliquota2;
        BigDecimal deducao2;
        BigDecimal faixa3;
        BigDecimal aliquota3;
        BigDecimal deducao3;
        BigDecimal faixa4;
        BigDecimal aliquota4;
        BigDecimal deducao4;
        BigDecimal faixa5;
        BigDecimal aliquota5;
        BigDecimal deducao5;

        public irrf(int id, String mesano, BigDecimal faixa1, BigDecimal faixa2, BigDecimal aliquota2, BigDecimal deducao2, BigDecimal faixa3, BigDecimal aliquota3, BigDecimal deducao3, BigDecimal faixa4, BigDecimal aliquota4, BigDecimal deducao4, BigDecimal faixa5, BigDecimal aliquota5, BigDecimal deducao5) {
            this.id = id;
            this.mesano = mesano;
            this.faixa1 = faixa1;
            this.faixa2 = faixa2;
            this.aliquota2 = aliquota2;
            this.deducao2 = deducao2;
            this.faixa3 = faixa3;
            this.aliquota3 = aliquota3;
            this.deducao3 = deducao3;
            this.faixa4 = faixa4;
            this.aliquota4 = aliquota4;
            this.deducao4 = deducao4;
            this.faixa5 = faixa5;
            this.aliquota5 = aliquota5;
            this.deducao5 = deducao5;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getMesano() { return mesano; }
        public void setMesano(String mesano) { this.mesano = mesano; }

        public BigDecimal getFaixa1() { return faixa1; }
        public void setFaixa1(BigDecimal faixa1) { this.faixa1 = faixa1; }

        public BigDecimal getFaixa2() { return faixa2; }
        public void setFaixa2(BigDecimal faixa2) { this.faixa2 = faixa2; }

        public BigDecimal getAliquota2() { return aliquota2; }
        public void setAliquota2(BigDecimal aliquota2) { this.aliquota2 = aliquota2; }

        public BigDecimal getDeducao2() { return deducao2; }
        public void setDeducao2(BigDecimal deducao2) { this.deducao2 = deducao2; }

        public BigDecimal getFaixa3() { return faixa3; }
        public void setFaixa3(BigDecimal faixa3) { this.faixa3 = faixa3; }

        public BigDecimal getAliquota3() { return aliquota3; }
        public void setAliquota3(BigDecimal aliquota3) { this.aliquota3 = aliquota3; }

        public BigDecimal getDeducao3() { return deducao3; }
        public void setDeducao3(BigDecimal deducao3) { this.deducao3 = deducao3; }

        public BigDecimal getFaixa4() { return faixa4; }
        public void setFaixa4(BigDecimal faixa4) { this.faixa4 = faixa4; }

        public BigDecimal getAliquota4() { return aliquota4; }
        public void setAliquota4(BigDecimal aliquota4) { this.aliquota4 = aliquota4; }

        public BigDecimal getDeducao4() { return deducao4; }
        public void setDeducao4(BigDecimal deducao4) { this.deducao4 = deducao4; }

        public BigDecimal getFaixa5() { return faixa5; }
        public void setFaixa5(BigDecimal faixa5) { this.faixa5 = faixa5; }

        public BigDecimal getAliquota5() { return aliquota5; }
        public void setAliquota5(BigDecimal aliquota5) { this.aliquota5 = aliquota5; }

        public BigDecimal getDeducao5() { return deducao5; }
        public void setDeducao5(BigDecimal deducao5) { this.deducao5 = deducao5; }

        @Override
        public String toString() {
            return "irrf{" +
                    "mesano='" + mesano + '\'' +
                    '}';
        }
    }
}
