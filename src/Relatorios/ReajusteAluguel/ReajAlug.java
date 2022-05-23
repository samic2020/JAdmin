package Relatorios.ReajusteAluguel;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import pdfViewer.PdfViewer;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReajAlug implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private Spinner<String> mesIni;
    @FXML private Spinner<Integer> anoIni;
    @FXML private Spinner<String> mesFim;
    @FXML private Spinner<Integer> anoFim;
    @FXML private Button btnListar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        anoIni.setValueFactory(ano);

        SpinnerValueFactory ano2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        ano2.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        anoFim.setValueFactory(ano2);

        String nmes = Dates.Month(DbMain.getDateTimeServer());
        SpinnerValueFactory Value = new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"));
        Value.setValue(nmes);
        mesIni.setValueFactory(Value);

        String nmes2 = Dates.Month(DbMain.getDateTimeServer());
        SpinnerValueFactory Value2 = new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"));
        Value.setValue(nmes2);
        mesFim.setValueFactory(Value2);

        btnListar.setOnAction(event -> {
            Listagem(Dates.MonthToInteger(mesIni.getValue()), anoIni.getValue(), Dates.MonthToInteger(mesFim.getValue()), anoFim.getValue());
        });
    }

    private void Listagem(int mesIni, int anoIni, int mesFim, int anoFim) {
        List<cReajAlug> lista = new ArrayList<>();

        String selectSQL = "select Upper(Trim(loca.l_tipoimovel)) AS tpimovel, loca.l_contrato as contrato, CASE WHEN loca.l_fisjur THEN loca.l_f_nome ELSE loca.l_j_razao END AS NomeRazao, ctro.dtinicio, ctro.dtfim, ctro.dtaditamento from carteira ctro inner join locatarios loca on loca.l_contrato = ctro.contrato WHERE loca.l_dtbaixa is null and ((extract(month from ctro.dtinicio) between ? and ?) and (extract(year from ctro.dtinicio) between ? and ?) or (extract(month from ctro.dtaditamento) between ? and ?) and (extract(year from ctro.dtaditamento) between ? and ?)) order by  Lower(Trim(loca.l_tipoimovel)), ctro.dtinicio, CASE WHEN l_fisjur THEN Upper(Trim(loca.l_f_nome)) ELSE Upper(Trim(l_j_razao)) END;";
        ResultSet rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][]{
                {"int",mesIni},
                {"int",mesFim},
                {"int", anoIni},
                {"int", anoFim},
                {"int",mesIni},
                {"int",mesFim},
                {"int", anoIni},
                {"int", anoFim},
        });
        try {
            while (rs.next()) {
                cReajAlug values = new cReajAlug(rs.getString("tpimovel"), rs.getString("contrato"), rs.getString("nomerazao"), rs.getDate("dtinicio"), rs.getDate("dtfim"), rs.getDate("dtaditamento"));
                lista.add(values);
            }
        } catch (SQLException e) {}
        try {
            DbMain.FecharTabela(rs);} catch (Exception ex) {}

        Map parametros = new HashMap();
        parametros.put("mesIni", mesIni);
        parametros.put("anoIni", anoIni);
        parametros.put("mesFim", mesFim);
        parametros.put("anoFim", anoFim);

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"ReajAlug", parametros);
        new PdfViewer("Preview do Reajuste de Alugueis", pdfName);
    }
}
