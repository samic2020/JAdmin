package Relatorios.Despesas;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Movimento.Despesas.grpDespesas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import pdfViewer.PdfViewer;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Despesas implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private DatePicker dtIni;
    @FXML private DatePicker dtFim;
    @FXML private Button btnListar;
    @FXML private ComboBox<grpDespesas> grupo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FillGrupos();
        dtIni.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        dtFim.setValue(Dates.toLocalDate(Dates.ultDataMes(DbMain.getDateTimeServer())));
        btnListar.setOnAction(event -> {
            Listar(Dates.toDate(dtIni.getValue()), Dates.toDate(dtFim.getValue()));
        });
    }

    private void FillGrupos() {
        ObservableList<grpDespesas> items = FXCollections.observableArrayList();
        items.add(new grpDespesas(0,"TODAS"));

        String selectSQL = "SELECT id, descricao FROM despesasgrupo ORDER BY Upper(descricao);";
        ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY);
        try {
            int qid = 0; String qdescr = null;
            while (rs.next()) {
                try { qid = rs.getInt("id"); } catch (SQLException exsql) {}
                try { qdescr = rs.getString("descricao"); } catch (SQLException exsql) {}

                items.add(new grpDespesas(qid, qdescr));
            }
        } catch (SQLException sqlex) {}
        try { DbMain.FecharTabela(rs); } catch (Exception ex) {}

        if (!items.isEmpty()) {
            grupo.setItems(FXCollections.observableArrayList(items));
            grupo.getSelectionModel().selectFirst();
        }
    }

    private void Listar(Date ini, Date fim) {
        List<cDespesas> lista = new ArrayList<>();

        String movimentoSQL = ""; Object[][] param = null;
        if (grupo.getSelectionModel().getSelectedIndex() == 0) {
            movimentoSQL = "select id, idgrupo, descricao, texto, valor, aut, dtpagto, logado from despesas where (dtpagto between ? AND ?) ORDER BY aut;";
            param = new Object[][] {
                    {"date", Dates.toSqlDate(ini)},
                    {"date", Dates.toSqlDate(fim)}
            };
        } else {
            movimentoSQL = "select id, idgrupo, descricao, texto, valor, aut, dtpagto, logado from despesas where UPPER(TRIM(descricao)) = ? and (dtpagto between ? AND ?) ORDER BY aut;";
            param = new Object[][] {
                    {"string", grupo.getSelectionModel().getSelectedItem().getDescricao().toUpperCase().trim()},
                    {"date", Dates.toSqlDate(ini)},
                    {"date", Dates.toSqlDate(fim)}
            };
        }
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, param);
        try {
            while (rs.next()) {
                cDespesas values = new cDespesas(
                        rs.getString("id"),
                        rs.getString("descricao"),
                        rs.getString("texto"),
                        rs.getBigDecimal("valor"),
                        rs.getString("aut"),
                        rs.getDate("dtpagto"),
                        rs.getString("logado")
                        );
                lista.add(values);
            }
        } catch (SQLException e) {}
        try {DbMain.FecharTabela(rs);} catch (Exception ex) {}

        Map parametros = new HashMap();
        parametros.put("grupo", grupo.getSelectionModel().getSelectedItem().getDescricao().toUpperCase().trim());
        parametros.put("dataIni", Dates.DateFormata("dd-MM-yyyy", ini));
        parametros.put("dataFin", Dates.DateFormata("dd-MM-yyyy", fim));

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Despesas", parametros);
        new PdfViewer("Preview das Despesas", pdfName);
    }
}
