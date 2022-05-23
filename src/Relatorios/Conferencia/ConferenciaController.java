package Relatorios.Conferencia;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Relatorios.GetFields;
import Relatorios.GetFieldsClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import pdfViewer.PdfViewer;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ConferenciaController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private DatePicker dtIni;
    @FXML private DatePicker dtFim;
    @FXML private Button btnListar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dtIni.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        dtFim.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        btnListar.setOnAction(event -> {
            Listar(Dates.toDate(dtIni.getValue()), Dates.toDate(dtFim.getValue()));
        });
    }

    private void Listar(Date ini, Date fim) {
        List<GetFieldsClass> lista = new ArrayList<>();

        String movimentoSQL = "select rgprp, rgimv, contrato from caixa where documento = 'REC' and (datahora::date between ? AND ?) ORDER BY aut;";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, new Object[][]{
                {"date",Dates.toSqlDate(ini)},
                {"date", Dates.toSqlDate(fim)}
        });
        try {
            while (rs.next()) {
                GetFieldsClass values = new GetFields().GetFields(rs.getString("rgprp"), rs.getString("rgimv"), rs.getString("contrato"), ini, fim);
                lista.add(values);
            }
        } catch (SQLException e) {e.printStackTrace();}
        try {DbMain.FecharTabela(rs);} catch (Exception ex) {}

        Map parametros = new HashMap();
        parametros.put("dataIni", Dates.DateFormata("dd-MM-yyyy", ini));
        parametros.put("dataFin", Dates.DateFormata("dd-MM-yyyy", fim));

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Conferencia", parametros);
        new PdfViewer("Preview da Conferência", pdfName);
    }
}
