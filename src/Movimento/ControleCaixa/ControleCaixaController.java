package Movimento.ControleCaixa;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Movimento.FecCaixa.FecCaixa;
import Movimento.FecCaixa.cBanco;
import Movimento.FecCaixa.cCaixa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ControleCaixaController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private ListView<String> cxCaixas;
    @FXML private ListView<Date> cxFechamentos;
    @FXML private TextField cxDN;
    @FXML private TextField cxCH;
    @FXML private TextField cxBC;
    @FXML private TextField cxBO;
    @FXML private TextField cxCT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Caixas();
    }

    private void Caixas() {
        ObservableList<String> datas = FXCollections.observableArrayList();
        String selectSQL = "SELECT DISTINCT Lower(Trim(logado)) logado FROM caixa WHERE fechado = FALSE AND documento != 'CXA' ORDER BY logado;";
        ResultSet caixaRs = null;
        try {
            caixaRs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (caixaRs.next()) {
                String qLogado = null;
                try { qLogado = caixaRs.getString("logado"); } catch (SQLException sex) {}
                datas.add(qLogado);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(caixaRs);

        if (datas != null) {
            cxCaixas.getItems().setAll(FXCollections.observableArrayList(datas));
        } else {
            cxCaixas.getItems().clear();
        }
        cxCaixas.setOnMouseClicked(event -> {
            FillCaixas(cxCaixas.getSelectionModel().getSelectedItem());
        });
        cxCaixas.setOnKeyPressed(event -> {
            FillCaixas(cxCaixas.getSelectionModel().getSelectedItem());
        });
    }

    private void FillCaixas(String usuario) {
        ObservableList<Date> datas = FXCollections.observableArrayList();
        String selectSQL = "SELECT DISTINCT to_date(to_char(datahora,'YYYY-MM-DD'),'YYYY-MM-DD') datahora FROM caixa WHERE Upper(Trim(logado)) = ? AND fechado = FALSE ORDER BY datahora;";
        ResultSet caixaRs = null;
        try {
            caixaRs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", VariaveisGlobais.usuario.trim().toUpperCase()}});
            while (caixaRs.next()) {
                Date qData = null;
                try { qData = caixaRs.getDate("datahora"); } catch (SQLException sex) {}
                if (qData != null) datas.add(qData);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(caixaRs);

        cxFechamentos.setCellFactory((FecCaixa.AbstractConvertCellFactory<Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        if (datas != null) {
            cxFechamentos.getItems().setAll(FXCollections.observableArrayList(datas));
        } else {
            cxFechamentos.getItems().clear();
        }
        cxFechamentos.setOnMouseClicked(event -> {
            ListaTotais(cxFechamentos.getSelectionModel().getSelectedItem(),cxCaixas.getSelectionModel().getSelectedItem());
        });
        cxFechamentos.setOnKeyPressed(event -> {
            ListaTotais(cxFechamentos.getSelectionModel().getSelectedItem(),cxCaixas.getSelectionModel().getSelectedItem());
        });
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

    private void ListaTotais(Date data, String logado) {
        if (data == null) return;
        List<cCaixa> Lista = new ArrayList<>();
        String selectSQL = "SELECT rgprp, rgimv, contrato, lancamentos, id, s, aut, datahora, documento, operacao, lancamentos[s][1]::varchar(2) tipo, valor::decimal total, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE Upper(Trim(logado)) = ? AND (to_char(datahora,'DD-MM-YYYY') = ?) AND fechado = FALSE ORDER BY to_char(datahora,'YYYY-MM-DD'), documento, operacao, aut, s, lancamentos[s][1]::varchar(2);";
        ResultSet fcRs = null;
        try {
            int oldaut = -1;
            List<cBanco> dataBanco = new ArrayList<>();
            fcRs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY,new Object[][] {
                    {"string", logado.trim().toUpperCase()},
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

        TotaLizadores(Lista);
    }

    private BigDecimal TotalCheques(List<cBanco> dataBanco) {
        if (dataBanco.size() == 0) return new BigDecimal("0");

        BigDecimal tTotal = new BigDecimal("0");
        for (cBanco o : dataBanco) {
            tTotal = tTotal.add(o.getValor());
        }
        return tTotal;
    }

    public void TotaLizadores(List<cCaixa> dados) {
        BigDecimal tcrdn = new BigDecimal("0"); BigDecimal tdbdn = new BigDecimal("0");
        BigDecimal tcrch = new BigDecimal("0"); BigDecimal tdbch = new BigDecimal("0");
        BigDecimal tcrbc = new BigDecimal("0"); BigDecimal tdbbc = new BigDecimal("0");
        BigDecimal tcrbo = new BigDecimal("0"); BigDecimal tdbbo = new BigDecimal("0");
        BigDecimal tcrct = new BigDecimal("0"); BigDecimal tdbct = new BigDecimal("0");

        String oldDoc = ""; String oldTipo = ""; Object[][] Totais = {};
        for( final cCaixa os : dados) {
            // Para encapsular
            // Contabilização dos Totais
            if (os.getTipo().equalsIgnoreCase("DN")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrdn = tcrdn.add(os.getValor());
                } else {
                    tdbdn = tdbdn.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("CH")) {
                for (cBanco obc : os.getDatabanco()) {
                    if (os.getOperacao().equalsIgnoreCase("CRE")) {
                        tcrch = tcrch.add(obc.getValor());
                    } else {
                        tdbch = tdbch.add(obc.getValor());
                    }
                }
            } else if (os.getTipo().equalsIgnoreCase("BC")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrbc = tcrbc.add(os.getValor());
                } else {
                    tdbbc = tdbbc.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("BO")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrbo = tcrbo.add(os.getValor());
                } else {
                    tdbbo = tdbbo.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("CT")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrct = tcrct.add(os.getValor());
                } else {
                    tdbct = tdbct.add(os.getValor());
                }
            }

            if (oldDoc != os.getDoc()) {
                int pos = FuncoesGlobais.FindinObject(Totais, 0, os.getDoc());
                if (pos < 0) {
                    Totais = FuncoesGlobais.ObjectsAdd(Totais, new Object[]{os.getDoc(), new Object[]{new Object[]{"CRE", new Object[]{tcrdn, tcrch, tcrbc, tcrbo, tcrct}}}, new Object[]{new Object[]{"DEB", new Object[]{tdbdn, tdbch, tdbbc, tdbbo, tdbct}}}});
                } else {
                    // CRE
                    Object[] tTCred = (Object[]) Totais[pos][1];
                    Object[] tmpTtCrDb = (Object[]) tTCred[0];
                    Object[] tmpCRE = (Object[]) tmpTtCrDb[1];

                    tmpCRE[0] = ((BigDecimal) tmpCRE[0]).add(tcrdn);
                    tmpCRE[1] = ((BigDecimal) tmpCRE[1]).add(tcrch);
                    tmpCRE[2] = ((BigDecimal) tmpCRE[2]).add(tcrbc);
                    tmpCRE[3] = ((BigDecimal) tmpCRE[3]).add(tcrbo);
                    tmpCRE[4] = ((BigDecimal) tmpCRE[4]).add(tcrct);
                    // DEB
                    Object[] tTDeb = (Object[]) Totais[pos][2];
                    Object[] tmpTtDeb = (Object[]) tTDeb[0];
                    Object[] tmpDeb = (Object[]) tmpTtDeb[1];
                    tmpDeb[0] = ((BigDecimal) tmpDeb[0]).add(tdbdn);
                    tmpDeb[1] = ((BigDecimal) tmpDeb[1]).add(tdbch);
                    tmpDeb[2] = ((BigDecimal) tmpDeb[2]).add(tdbbc);
                    tmpDeb[3] = ((BigDecimal) tmpDeb[3]).add(tdbbo);
                    tmpDeb[4] = ((BigDecimal) tmpDeb[4]).add(tdbct);
                }
            }

            tcrdn = new BigDecimal("0");
            tdbdn = new BigDecimal("0");
            tcrch = new BigDecimal("0");
            tdbch = new BigDecimal("0");
            tcrbc = new BigDecimal("0");
            tdbbc = new BigDecimal("0");
            tcrbo = new BigDecimal("0");
            tdbbo = new BigDecimal("0");
            tcrct = new BigDecimal("0");
            tdbct = new BigDecimal("0");

            if (!oldDoc.equalsIgnoreCase(os.getDoc()) || !oldTipo.equalsIgnoreCase(os.getOperacao())) {
                oldDoc = os.getDoc();
                oldTipo = os.getOperacao();
            }
        }

        if (oldDoc != "") {
            int pos = FuncoesGlobais.FindinObject(Totais,0,oldDoc);
            if (pos < 0) {
                Totais = FuncoesGlobais.ObjectsAdd(Totais, new Object[]{oldDoc, new Object[]{new Object[]{"CRE", new Object[]{tcrdn, tcrch, tcrbc, tcrbo, tcrct}}}, new Object[]{new Object[]{"DEB", new Object[]{tdbdn, tdbch, tdbbc, tdbbo, tdbct}}}});
            } else {
                // CRE
                Object[] tTCred = (Object[]) Totais[pos][1];
                Object[] tmpTtCrDb = (Object[]) tTCred[0];
                Object[] tmpCRE = (Object[])tmpTtCrDb[1];

                tmpCRE[0] = ((BigDecimal)tmpCRE[0]).add(tcrdn);
                tmpCRE[1] = ((BigDecimal)tmpCRE[1]).add(tcrch);
                tmpCRE[2] = ((BigDecimal)tmpCRE[2]).add(tcrbc);
                tmpCRE[3] = ((BigDecimal)tmpCRE[3]).add(tcrbo);
                tmpCRE[4] = ((BigDecimal)tmpCRE[4]).add(tcrct);
                // DEB
                Object[] tTDeb = (Object[]) Totais[pos][2];
                Object[] tmpTtDeb = (Object[]) tTDeb[0];
                Object[] tmpDeb = (Object[])tmpTtDeb[1];
                tmpDeb[0] = ((BigDecimal)tmpDeb[0]).add(tdbdn);
                tmpDeb[1] = ((BigDecimal)tmpDeb[1]).add(tdbch);
                tmpDeb[2] = ((BigDecimal)tmpDeb[2]).add(tdbbc);
                tmpDeb[3] = ((BigDecimal)tmpDeb[3]).add(tdbbo);
                tmpDeb[4] = ((BigDecimal)tmpDeb[4]).add(tdbct);
            }

            tcrdn = new BigDecimal("0"); tdbdn = new BigDecimal("0");
            tcrch = new BigDecimal("0"); tdbch = new BigDecimal("0");
            tcrbc = new BigDecimal("0"); tdbbc = new BigDecimal("0");
            tcrbo = new BigDecimal("0"); tdbbo = new BigDecimal("0");
            tcrct = new BigDecimal("0"); tdbct = new BigDecimal("0");
        }

        BigDecimal tdin = new BigDecimal("0"); BigDecimal tche = new BigDecimal("0");
        BigDecimal tban = new BigDecimal("0"); BigDecimal tbol = new BigDecimal("0");
        BigDecimal tcar = new BigDecimal("0");

        BigDecimal tEtd = new BigDecimal("0"); BigDecimal tEta = new BigDecimal("0");
        for (Object[] tt : Totais) {
            Object[] tTCred = (Object[]) tt[1];
            Object[] tmpTtCrDb = (Object[]) tTCred[0];
            Object[] tmpCRE = (Object[])tmpTtCrDb[1];
            // DEB
            Object[] tTDeb = (Object[]) tt[2];
            Object[] tmpTtDeb = (Object[]) tTDeb[0];
            Object[] tmpDeb = (Object[])tmpTtDeb[1];

            tdin = tdin.add(((BigDecimal)tmpCRE[0]).subtract((BigDecimal)tmpDeb[0]));
            tche = tche.add(((BigDecimal)tmpCRE[1]).subtract((BigDecimal)tmpDeb[1]));
            tban = tban.add(((BigDecimal)tmpCRE[2]).subtract((BigDecimal)tmpDeb[2]));
            tbol = tbol.add(((BigDecimal)tmpCRE[3]).subtract((BigDecimal)tmpDeb[3]));
            tcar = tcar.add(((BigDecimal)tmpCRE[4]).subtract((BigDecimal)tmpDeb[4]));

            tEtd = tEtd.add((BigDecimal)tmpCRE[0]);
            tEtd = tEtd.add((BigDecimal)tmpCRE[1]);

            tEta = tEta.add((BigDecimal)tmpDeb[0]);
            tEta = tEta.add((BigDecimal)tmpDeb[1]);
        }

        cxDN.setText(new DecimalFormat("#,##0.00").format(tdin));
        cxCH.setText(new DecimalFormat("#,##0.00").format(tche));
        cxBC.setText(new DecimalFormat("#,##0.00").format(tban));
        cxBO.setText(new DecimalFormat("#,##0.00").format(tbol));
        cxCT.setText(new DecimalFormat("#,##0.00").format(tcar));
    }

}
