package Imposto;

import Classes.ir;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by supervisor on 05/09/16.
 */
public class TabelaIRController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<ir> tbvIndices;
    @FXML private TableColumn<ir, Integer> clnId;
    @FXML private TableColumn<ir, String> clnMesAno;
    @FXML private TableColumn<ir, BigDecimal> clnIndice;
    @FXML private TextField mesAno;
    @FXML private TextField indicePerc;
    @FXML private Button btnLancar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.dateRefField(mesAno);
        MaskFieldUtil.monetaryField(indicePerc);

        tbvIndices.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION,"Confirma exclusão?", new ButtonType("Sim"), new ButtonType("N�o"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String tSql = "DELETE FROM ir WHERE id = %s;";
                    tSql = String.format(tSql, tbvIndices.getSelectionModel().getSelectedItem().getId());
                    try {
                        if (conn.ExecutarComando(tSql) <= 0) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao gravar!");
                            alert.setTitle("Erro");
                            alert.showAndWait();
                        } else LerIR();
                    } catch (Exception e) {
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Per�odo excluid!");
                alert.setTitle("Erro");
                alert.showAndWait();
            }
        });

        btnLancar.setOnAction(event -> {
            if (!VerifyIr(mesAno.getText().trim())) {
                String tSql = "INSERT INTO ir (mesano, indice) VALUES('%s','%s');";
                tSql = String.format(tSql, mesAno.getText().trim(), LerValor.StringToFloat(indicePerc.getText()));
                try {
                    if (conn.ExecutarComando(tSql) <= 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,"Erro ao gravar!");
                        alert.setTitle("Erro");
                        alert.showAndWait();
                    } else LerIR();
                } catch (Exception e) {}
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"�ndice do per�odo j� cadastrado!");
                alert.setTitle("Erro");
                alert.showAndWait();
            }
        });

        LerIR();
    }

    private boolean VerifyIr(String tmesano) {
        boolean iscad = false;
        String tSql = "SELECT id, mesano, indice from ir WHERE mesano = '%s';";
        tSql = String.format(tSql, tmesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) { iscad = true; }
        } catch (SQLException e) {}
        finally { try {trs.close();} catch (SQLException e) {} }
        return iscad;
    }

    private void LerIR() {
        List<ir> data = new ArrayList<ir>();
        ResultSet irs;
        String qSQL = "SELECT id, mesano, indice FROM ir ORDER BY mesano;";
        try {
            irs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (irs.next()) {
                Integer qid = -1;
                String qmesano = null;
                BigDecimal qindice = null;
                try {qid = irs.getInt("id");} catch (SQLException e) {}
                try {qmesano = irs.getString("mesano");} catch (SQLException e) {}
                try {qindice = irs.getBigDecimal("indice");} catch (SQLException e) {}
                data.add(new ir(qid, qmesano, qindice));
            }
            irs.close();
        } catch (SQLException e) {}

        clnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clnMesAno.setCellValueFactory(new PropertyValueFactory<>("mesano"));
        clnIndice.setCellValueFactory(new PropertyValueFactory<>("indice"));

        tbvIndices.setItems(FXCollections.observableArrayList(data));
    }
}
