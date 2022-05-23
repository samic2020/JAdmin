package Movimento.Depositos;

import Funcoes.*;
import SegundaVia.Avisos.AvisosController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class DepositoController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;

    @FXML private TableView<cDeposito> depCheques;
    @FXML private TableColumn<cDeposito, Integer> depChequesId;
    @FXML private TableColumn<cDeposito, Integer> depChequesS;
    @FXML private TableColumn<cDeposito, Date> depChequesDtLanc;
    @FXML private TableColumn<cDeposito, Date> depChequesDtPre;
    @FXML private TableColumn<cDeposito, String> depChequesBanco;
    @FXML private TableColumn<cDeposito, String> depChequesAgencia;
    @FXML private TableColumn<cDeposito, String> depChequesNumero;
    @FXML private TableColumn<cDeposito, BigDecimal> depChequesValor;
    @FXML private TableColumn<cDeposito, Boolean> depChequesTag;

    @FXML private CheckBox depSelChComum;

    @FXML private TextField depQtChPre;
    @FXML private TextField depTtChPre;

    @FXML private TextField depQtChCmomum;
    @FXML private TextField depTtChComum;

    @FXML private TextField depVrDn;
    @FXML private TextField depTg;

    @FXML private ComboBox<cBancoDep> depBanco;
    @FXML private Button btnDepositar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.monetaryField(depVrDn);
        depSelChComum.setOnAction(event -> {
            for (cDeposito item:depCheques.getItems()) {
                if (item.getDtpre() == null) {
                    item.setTag(depSelChComum.isSelected() ? true : false);
                }
            }
        });
        LerBancos();
        LerCheques();

        depVrDn.focusedProperty().addListener((observable, oldValue, newValue) -> { if (oldValue) {/* got focus*/TotalizaSelected(); } else {/* lost focus*/ }});
        btnDepositar.disableProperty().bind(depBanco.getSelectionModel().selectedIndexProperty().isEqualTo(-1));

        btnDepositar.setOnAction(event -> {
                    String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                            "contrato, valor, lancamentos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    Collections dadm = VariaveisGlobais.getAdmDados();
                    BigInteger aut = conn.PegarAutenticacao();

                    BigDecimal vrRecibo = new BigDecimal(LerValor.Number2BigDecimal(depTg.getText()));
                    BigDecimal vrDn =  new BigDecimal(LerValor.Number2BigDecimal(depVrDn.getText()));
                    BigDecimal vrChp =  new BigDecimal(LerValor.Number2BigDecimal(depTtChPre.getText()));
                    BigDecimal vrChc =  new BigDecimal(LerValor.Number2BigDecimal(depTtChComum.getText()));
                    String[][] lanctos = {};

                    if (vrDn.floatValue() > 0) {
                        lanctos = FuncoesGlobais.ArraysAdd(lanctos, new String[]{"DN", vrDn.toPlainString(), "", "", "", ""});
                    }
                    if (vrChp.add(vrChc).floatValue()> 0) {
                        lanctos = FuncoesGlobais.ArraysAdd(lanctos, new String[]{"CH", vrChp.add(vrChc).toPlainString(),
                                depBanco.getSelectionModel().getSelectedItem().getBanco(),
                                depBanco.getSelectionModel().getSelectedItem().getAgencia(),
                                depBanco.getSelectionModel().getSelectedItem().getConta()
                        });
                    }

            try {
                conn.ExecutarComando(caixaSQL, new Object[][]{
                        {"bigint", aut},
                        {"date", Dates.toSqlDate(DbMain.getDateTimeServer())},
                        {"string", VariaveisGlobais.usuario},
                        {"string", "DEB"},
                        {"string", "DEP"},
                        {"int", 0},
                        {"int", 0},
                        {"string", ""},
                        {"decimal", vrRecibo},
                        {"array", conn.conn.createArrayOf("text" + "",
                                new Object[][]{
                                        {lanctos[0][0], lanctos[0][1], lanctos[0][4], lanctos[0][3], lanctos[0][2], ""},
                                        {lanctos[1][0], lanctos[1][1], lanctos[1][4], lanctos[1][3], lanctos[1][2], ""}
                        })} //lanctos
                });
            } catch (Exception ex) { ex.printStackTrace();}
            // Gravar autenticação nos cheques selecionados
            List<cDeposito> dados = new ArrayList<>();
            for (cDeposito item:depCheques.getItems()) {
                if (!item.getTag()) continue;
                dados.add(new cDeposito(item.getId(), item.getS(), item.getDtlanc(), item.getDtpre(), item.getBanco(), item.getAgencia(), item.getNumero(), item.getValor(), false));

                Object[][] lerdeposito = null;
                try {
                    lerdeposito = conn.LerCamposTabela(new String[] {"lancamentos"},"caixa", "id = ?", new Object[][] {{"int", item.getId()}});
                } catch (Exception ex) {}
                if (lerdeposito != null) {
                    String[][] lctos = ConvertArrayString2ObjectArrays((String) lerdeposito[0][3]);
                    if (lctos[0].length == 6) {
                        for (int i = 0; i <= lctos.length - 1; i++) {
                            lctos[i] = FuncoesGlobais.ArrayAdd(lctos[i],"");
                        }
                    }
                    lctos[item.getS() - 1][6] = aut.toString();
                    String updateSQL = "UPDATE caixa SET lancamentos = '" + DbMain.GeraLctosArray(lctos) + "' WHERE id = ?;";
                    conn.ExecutarComando(updateSQL, new Object[][]{{"int", item.getId()}});
                }
            }
            new Impressao(aut, new String[][] {
                    {
                        "DP",
                        depBanco.getSelectionModel().getSelectedItem().getBanco(),
                        depBanco.getSelectionModel().getSelectedItem().getAgencia(),
                        depBanco.getSelectionModel().getSelectedItem().getConta(),
                        Dates.DateFormata("dd-MM-yyyy", DbMain.getDateTimeServer()),
                        LerValor.Number2BigDecimal(depTg.getText())
            }}).ImprimeDepositoPDF(dadm, dados, new BigDecimal(LerValor.Number2BigDecimal(depTg.getText())), new BigDecimal(LerValor.Number2BigDecimal(depVrDn.getText())), true);

            // Reiniciar tela
            LerCheques();
        });
    }

    private void LerBancos() {
        depBanco.getItems().clear();
        List<cBancoDep> data = new ArrayList<cBancoDep>();
        String selectSQL = "SELECT a.banco, b.nome, a.agencia, a.conta, a.tipo FROM banco_adm a INNER JOIN bancos b ON b.numero = a.banco ORDER BY 1;";
        try {
            ResultSet brs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (brs.next()) {
                String qbanco = null; String qdescr = null; String qagencia = null;
                String qconta = null; String qtipo = null;
                try {qbanco = brs.getString("banco");} catch (SQLException ex) {}
                try {qdescr = brs.getString("nome");} catch (SQLException ex) {}
                try {qagencia = brs.getString("agencia");} catch (SQLException ex) {}
                try {qconta = brs.getString("conta");} catch (SQLException ex) {}
                try {qtipo = brs.getString("tipo");} catch (SQLException ex) {}
                data.add(new cBancoDep(qbanco,qdescr,qagencia,qconta,qtipo));
            }
        } catch (SQLException sex) {}
        if (data != null) depBanco.setItems(FXCollections.observableArrayList(data));
    }

    private void LerCheques() {
        List<cDeposito> data = new ArrayList<cDeposito>();
        ResultSet imv;
        String qSQL = "SELECT lancamentos, id, s, aut, datahora, lancamentos[s][1]::varchar(2) tipo, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE lancamentos[s][1] = 'CH' AND (lancamentos[s][7] is null OR lancamentos[s][7] = '') AND Upper(Trim(logado)) = ? AND (operacao = 'CRE' AND (documento = 'REC' OR documento = 'AVI')) ORDER BY 1,7,8,9";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", VariaveisGlobais.usuario.trim().toUpperCase()}});
            while (imv.next()) {
                int gId = -1; int gS = -1; Date gDtLanc = null; String gBanco = null;
                String gAgencia = null; String gNumero = null; Date gDtPre = null;
                BigDecimal gValor = new BigDecimal("0"); boolean gTag = false;
                cDeposito registro = new cDeposito();
                try {gId = imv.getInt("id");} catch (SQLException e) {}
                try {gS = imv.getInt("s");} catch (SQLException e) {}
                try {gDtLanc = imv.getDate("datahora");} catch (SQLException e) {}
                try {gBanco = imv.getString("banco");} catch (SQLException e) {}
                try {gAgencia = imv.getString("agencia");} catch (SQLException e) {}
                try {gNumero = imv.getString("ncheque");} catch (SQLException e) {}
                try {gValor = imv.getBigDecimal("valor");} catch (SQLException e) {}
                try {gDtPre = Dates.StringtoDate(imv.getString("dtcheq"),"yyyy/MM/dd");} catch (SQLException e) {}
                gTag = false;

                data.add(new cDeposito(gId, gS, gDtLanc, gDtPre, gBanco, gAgencia, gNumero, gValor, gTag));
            }
            imv.close();
        } catch (SQLException e) {}

        depChequesId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        depChequesId.setStyle( "-fx-alignment: CENTER;");

        depChequesS.setCellValueFactory(new PropertyValueFactory<>("s"));
        depChequesS.setStyle( "-fx-alignment: CENTER;");

        depChequesDtLanc.setCellValueFactory(new PropertyValueFactory<>("dtlanc"));
        depChequesDtLanc.setCellFactory((AvisosController.AbstractConvertCellFactory<cDeposito, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        depChequesDtLanc.setStyle( "-fx-alignment: CENTER;");

        depChequesDtPre.setCellValueFactory(new PropertyValueFactory<>("dtpre"));
        depChequesDtPre.setCellFactory((AvisosController.AbstractConvertCellFactory<cDeposito, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        depChequesDtPre.setStyle( "-fx-alignment: CENTER;");

        depChequesBanco.setCellValueFactory(new PropertyValueFactory<>("banco"));
        depChequesBanco.setStyle( "-fx-alignment: CENTER;");

        depChequesAgencia.setCellValueFactory(new PropertyValueFactory<>("agencia"));
        depChequesAgencia.setStyle( "-fx-alignment: CENTER;");

        depChequesNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        depChequesNumero.setStyle( "-fx-alignment: CENTER;");

        depChequesValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        depChequesValor.setCellFactory((AvisosController.AbstractConvertCellFactory<cDeposito, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        depChequesValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        Platform.runLater(() -> {
            depChequesTag.setCellValueFactory(new PropertyValueFactory<cDeposito, Boolean>("tag"));
            depChequesTag.setStyle( "-fx-alignment: CENTER;");
            final BooleanProperty selected = new SimpleBooleanProperty();
            depChequesTag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(Integer index) {
                    cDeposito tbvlinhas = ((cDeposito) depCheques.getItems().get(index));
                    if (tbvlinhas.getValor().doubleValue() <= 0) {
                        tbvlinhas.setTag(false);
                    }
                    Platform.runLater(() -> TotalizaSelected());
                    return tbvlinhas.isTag();
                }
            }));
        });

        if (!data.isEmpty()) depCheques.setItems(FXCollections.observableArrayList(data));
    }

    private void TotalizaSelected() {
        BigDecimal comum = new BigDecimal("0");
        int ncomum = 0;
        BigDecimal pre = new BigDecimal("0");
        int npre = 0;
        for (cDeposito item:depCheques.getItems()) {
            if (item.getTag()) {
                if (item.getDtpre() == null) {
                    comum = comum.add(item.getValor());
                    ncomum += 1;
                } else {
                    pre = pre.add(item.getValor());
                    npre += 1;
                }
            }
        }
        depQtChPre.setText(FuncoesGlobais.StrZero(String.valueOf(npre),3));
        depTtChPre.setText(LerValor.BigDecimalToCurrency(pre));
        depQtChCmomum.setText(FuncoesGlobais.StrZero(String.valueOf(ncomum),3));
        depTtChComum.setText(LerValor.BigDecimalToCurrency(comum));

        depTg.setText(LerValor.BigDecimalToCurrency(comum.add(pre).add(new BigDecimal(LerValor.Number2BigDecimal(depVrDn.getText())))));
    }

    public interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
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

    private String[][] ConvertArrayString2ObjectArrays(String value) {
        String[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            if (vtr.length == 7) {
                retorno = FuncoesGlobais.ArraysAdd(retorno,
                        new String[]{
                                vtr[0].trim().replace("\"", ""),
                                vtr[1].trim().replace("\"", ""),
                                vtr[2].trim().replace("\"", ""),
                                vtr[3].trim().replace("\"", ""),
                                vtr[4].trim().replace("\"", ""),
                                vtr[5].trim().replace("\"", ""),
                                vtr[6].trim().replace("\"", "")
                        });
            } else {
                retorno = FuncoesGlobais.ArraysAdd(retorno,
                        new String[]{
                                vtr[0].trim().replace("\"", ""),
                                vtr[1].trim().replace("\"", ""),
                                vtr[2].trim().replace("\"", ""),
                                vtr[3].trim().replace("\"", ""),
                                vtr[4].trim().replace("\"", ""),
                                vtr[5].trim().replace("\"", "")
                        });
            }
        }
        return retorno;
    }

}
