package Movimento.FecCaixa;

import Classes.paramEvent;
import Funcoes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FecCaixa implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneCaixa;
    @FXML private ListView<Date> fecCaixas;
    @FXML private TextField tDN;
    @FXML private TextField tCH;
    @FXML private Button btnFechar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFechar();
    }

    private void initFechar() {
        FillCaixas();
        btnFechar.disableProperty().bind(fecCaixas.getSelectionModel().selectedItemProperty().isNull());
        btnFechar.setOnAction(event -> {
            if (fecCaixas.getSelectionModel().getSelectedIndex() != 0) {
                new Alert(Alert.AlertType.INFORMATION,"Você não pode fechar este caixa sem fechar o caixa anterior.").showAndWait();
                return;
            }
            Fechamento(fecCaixas.getSelectionModel().getSelectedItem(), new BigDecimal(LerValor.Number2BigDecimal(tDN.getText())), new BigDecimal(LerValor.Number2BigDecimal(tCH.getText())));
        });        
    }
    
    private void Fechamento(Date data, BigDecimal tgDN, BigDecimal tgCH) {
        List<cCaixa> Lista = new ArrayList<>();
        String selectSQL = "SELECT rgprp, rgimv, contrato, lancamentos, id, s, aut, datahora, documento, operacao, lancamentos[s][1]::varchar(2) tipo, valor::decimal total, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE Upper(Trim(logado)) = ? AND (to_char(datahora,'DD-MM-YYYY') = ? OR datahora Is Null) AND fechado = FALSE ORDER BY to_char(datahora,'YYYY-MM-DD'), documento, operacao, aut, s, lancamentos[s][1]::varchar(2);";
        ResultSet fcRs = null;
        try {
            int oldaut = -1;
            List<cBanco> dataBanco = new ArrayList<>();
            fcRs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY,new Object[][] {
                    {"string", VariaveisGlobais.usuario.trim().toUpperCase()},
                    {"string", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(data))}
            });
            String doc = null; int aut = -1; Date hora = null; BigDecimal total = null;
            String registro = null, situacao = null, operacao = null, tipo = null;

            while (fcRs.next()) {
                if (fcRs.getString("tipo").trim().toUpperCase().equalsIgnoreCase("CH")) {
                    if (oldaut != fcRs.getInt("aut")) {
                        if (oldaut == -1) {
                            try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                            try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                            try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                            try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                            try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                            try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                            try {total = fcRs.getBigDecimal("total"); } catch (SQLException e) {}
                            try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}

                            dataBanco = new ArrayList<>();
                            String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                            try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                            try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                            try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                            try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                            try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                            dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));

                            oldaut = fcRs.getInt("aut");
                        } else {
                            try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                            try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                            try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                            try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                            try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                            try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                            try {total = fcRs.getBigDecimal("total"); } catch (SQLException e) {}
                            try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}

                            String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                            try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                            try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                            try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                            try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                            try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                            dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));
                        }
                    } else {
                        String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                        try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                        try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                        try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                        try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                        try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                        dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));
                    }
                } else {
                    if (oldaut == -1) {
                        try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                        try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                        try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                        try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                        try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                        try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                        try {total = fcRs.getBigDecimal("valor"); } catch (SQLException e) {} //total
                        try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}
                        dataBanco = new ArrayList<>();

                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                    } else {
                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, TotalCheques(dataBanco), tipo, dataBanco));

                        try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                        try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                        try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                        try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                        try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                        try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                        try {total = fcRs.getBigDecimal("valor"); } catch (SQLException e) {} // total
                        try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}
                        dataBanco = new ArrayList<>();

                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                        oldaut = -1;
                    }
                }
            }
            if (oldaut != -1) {
                if (tipo.equalsIgnoreCase("CH")) {
                    total = TotalCheques(dataBanco);
                }
                Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                oldaut = -1;
            }
        } catch (SQLException sex) {sex.printStackTrace();}
        DbMain.FecharTabela(fcRs);
        System.out.println(Lista);

        Collections dadm = VariaveisGlobais.getAdmDados();
        new Impressao(new BigInteger("0"), new String[][] {}).ImprimeCaixaPDF(dadm, new BigDecimal[] {new BigDecimal(LerValor.Number2BigDecimal(tDN.getText())), new BigDecimal(LerValor.Number2BigDecimal(tCH.getText()))}, Lista, false);

        // Fechamento do caixa
        String updateSQL = "UPDATE caixa SET fechado = TRUE WHERE Upper(Trim(logado)) = ? AND (to_char(datahora,'DD-MM-YYYY') = ? OR datahora Is Null) AND fechado = FALSE;";
        conn.ExecutarComando(updateSQL,new Object[][] {
                {"string", VariaveisGlobais.usuario.trim().toUpperCase()},
                {"string", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(data))}
        });

        BigDecimal vrDn =  new BigDecimal(LerValor.Number2BigDecimal(tDN.getText()));
        BigDecimal vrCh =  new BigDecimal(LerValor.Number2BigDecimal(tCH.getText()));
        BigDecimal vrCaixa = vrDn.add(vrCh);

        String[][] lanctos = {};
        lanctos = FuncoesGlobais.ArraysAdd(lanctos, new String[]{"DN", vrDn.toPlainString(), "", "", "", ""});
        lanctos = FuncoesGlobais.ArraysAdd(lanctos, new String[]{"CH", vrCh.toPlainString(), "", "", ""});

        String insertSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, contrato, valor, lancamentos) " +
                "VALUES (?, ?, ?, 'CRE', 'CXA', 0, 0, '', ?, ?);";

        try {
            conn.ExecutarComando(insertSQL, new Object[][]{
                    {"bigint", new BigInteger("0")},
                    {"date", null},
                    {"string", VariaveisGlobais.usuario},
                    {"decimal", vrCaixa},
                    {"array", conn.conn.createArrayOf("text" + "",
                            new Object[][]{
                                    {lanctos[0][0], lanctos[0][1], lanctos[0][4], lanctos[0][3], lanctos[0][2], "", Dates.DateFormata("dd-MM-yyyy HH:mm:00", data)},
                                    {lanctos[1][0], lanctos[1][1], lanctos[1][4], lanctos[1][3], lanctos[1][2], "", Dates.DateFormata("dd-MM-yyyy HH:mm:00", data)}
                            })}});
        } catch (Exception ex) { ex.printStackTrace();}

        //try {anchorPaneCaixa.fireEvent(new paramEvent(new String[] {"Caixa"},paramEvent.GET_PARAM));} catch (NullPointerException e) {e.printStackTrace();}
        initFechar();
    }

    private BigDecimal TotalCheques(List<cBanco> dataBanco) {
        if (dataBanco.size() == 0) return new BigDecimal("0");

        BigDecimal tTotal = new BigDecimal("0");
        for (cBanco o : dataBanco) {
            tTotal = tTotal.add(o.getValor());
        }
        return tTotal;
    }

    private void FillCaixas() {
        ObservableList<Date> datas = FXCollections.observableArrayList();
        String selectSQL = "SELECT DISTINCT to_date(to_char(datahora,'YYYY-MM-DD'),'YYYY-MM-DD') datahora FROM caixa WHERE Upper(Trim(logado)) = ? AND fechado = FALSE ORDER BY datahora;";
        ResultSet caixaRs = null;
        try {
            caixaRs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", VariaveisGlobais.usuario.trim().toUpperCase()}});
            while (caixaRs.next()) {
                Date qData = null;
                try { qData = caixaRs.getDate("datahora"); } catch (SQLException sex) {}
                datas.add(qData);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(caixaRs);

        fecCaixas.setCellFactory((AbstractConvertCellFactory<Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        if (datas != null) {
            fecCaixas.getItems().setAll(FXCollections.observableArrayList(datas));
        } else {
            fecCaixas.getItems().clear();
        }
    }

    public interface AbstractConvertCellFactory<T> extends Callback<ListView<T>, ListCell<T>> {
        @Override
        default ListCell<T> call(ListView<T> param) {
            return new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }
        String convert(T value);
    }

}
