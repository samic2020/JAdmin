package Relatorios.Termino;

import Classes.ptelcontatoModel;
import Classes.setTels;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Termino implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private Spinner<String> termMes;
    @FXML private Button BtnPreview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> months = FXCollections.observableArrayList("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(months);

        // Default value
        valueFactory.setValue(months.get(DbMain.getDateTimeServer().getMonth()));
        termMes.setValueFactory(valueFactory);

        BtnPreview.setOnAction(event -> {
            List<cTermino> lista = new ArrayList<cTermino>();

            String selectSQL = "select l.l_id id,l.l_rgprp rgprp,l.l_rgimv rgimv,l.l_contrato contrato," +
                    "case when l.l_fisjur then l.l_f_nome else l.l_j_razao end nome," +
                    "case when l.l_fisjur then l.l_f_tel else l.l_j_tel end telef," +
                    "c.dtinicio,case when c.dtaditamento is null then c.dtfim else c.dtaditamento end dtfim " +
                    "from locatarios l inner join carteira c on l.l_contrato = c.contrato " +
                    "where case when c.dtaditamento is null then c.dtfim else c.dtaditamento end between ? and ? " +
                    "order by case when c.dtaditamento is null then c.dtfim else c.dtaditamento end;";
            String dataRef = "01/" + FuncoesGlobais.StrZero(String.valueOf(Dates.MonthToInteger(termMes.getValue().toString())),2) + "/" + (1900 + new Date().getYear());
            Date iniMes = Dates.primDataMes(Dates.StringtoDate(dataRef,"dd-MM-yyyy"));
            Date fimMes = Dates.ultDataMes(Dates.StringtoDate(dataRef,"dd-MM-yyyy"));

            Object[][] param = {{"date", Dates.toSqlDate(iniMes)}, {"date", Dates.toSqlDate(fimMes)}};
            ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, param);
            try {
                while (rs.next()) {
                    List<ptelcontatoModel> telef = null;
                    try {telef = new setTels(rs.getString("telef")).rString();} catch (SQLException e) {}

                    cTermino values = new cTermino(
                            rs.getInt("id"),
                            rs.getString("rgprp"),
                            rs.getString("rgimv"),
                            rs.getString("contrato"),
                            rs.getString("nome"),
                            telef == null ? "" : telef.get(0).toString(),
                            Dates.DateFormata("dd-MM-yyyy", rs.getDate("dtinicio")),
                            Dates.DateFormata("dd-MM-yyyy", rs.getDate("dtfim"))
                    );
                    lista.add(values);
                }
            } catch (SQLException e) {e.printStackTrace();}
            try {DbMain.FecharTabela(rs);} catch (Exception ex) {}


            Map parametros = new HashMap();
            parametros.put("mes", termMes.getValue().toString());

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"Termino", parametros);
            new PdfViewer("Preview - Termino de Contrato", pdfName);
        });
    }
}
