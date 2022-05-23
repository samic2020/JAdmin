package SegundaVia.Avisos;

import Classes.AttachEvent;
import Classes.paramEvent;
import Funcoes.Collections;
import Funcoes.*;
import com.sun.prism.impl.Disposer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AvisosController implements Initializable {
    @FXML private AnchorPane anchorPaneAvisos;

    @FXML private TabPane tabPaneAvisos;
    @FXML private Tab aviAdm;
    @FXML private Tab aviProp;
    @FXML private Tab aviLoca;
    @FXML private Tab aviSoc;
    @FXML private Tab aviCtas;

    @FXML private TextField txbAvCodigo;
    @FXML private TextField txbAvNome;
    @FXML private TextField txbPrCodigo;
    @FXML private TextField txbPrNome;
    @FXML private TextField txbLcCodigo;
    @FXML private TextField txbLcNome;
    @FXML private TextField txbScCodigo;
    @FXML private TextField txbScNome;
    @FXML private TextField txbCtCodigo;
    @FXML private TextField txbCtNome;

    @FXML private TableView<cAvisos> ListaAvi;
    @FXML private TableColumn<cAvisos, Integer> aviId;
    @FXML private TableColumn<cAvisos, String> aviOper;
    @FXML private TableColumn<cAvisos, String> aviAut;
    @FXML private TableColumn<cAvisos, Date> aviDataHora;
    @FXML private TableColumn<cAvisos, BigDecimal> aviValor;
    @FXML private TableColumn<cAvisos, String> aviLogado;
    @FXML private TableColumn<cAvisos, String> aviLanctos;
    @FXML private TableColumn<Disposer.Record, Boolean> aviAcoes;

    @FXML private Spinner<Integer> aviAno;
    @FXML private Spinner<Integer> aviMes;
    @FXML private Button btnListar;

    @FXML private TextField aviAutent;
    @FXML private Button btnImprimir;

    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsAvCodigo = {};
    private String[] _possibleSuggestionsAvNome = {};
    private String[][] _possibleSuggestionsAv = {};
    private Set<String> possibleSuggestionsAvCodigo;
    private Set<String> possibleSuggestionsAvNome;
    private boolean isSearchAvCodigo = true;
    private boolean isSearchAvNome = true;

    private String[] _possibleSuggestionsPrRegistro = {};
    private String[] _possibleSuggestionsPrNome = {};
    private String[][] _possibleSuggestionsPr = {};
    private Set<String> possibleSuggestionsPrRegistro;
    private Set<String> possibleSuggestionsPrNome;
    private boolean isSearchPrRegistro = true;
    private boolean isSearchPrNome = true;

    private String[] _possibleSuggestionsLcContrato = {};
    private String[] _possibleSuggestionsLcNome = {};
    private String[][] _possibleSuggestionsLc = {};
    private Set<String> possibleSuggestionsLcContrato;
    private Set<String> possibleSuggestionsLcNome;
    private boolean isSearchLcContrato = true;
    private boolean isSearchLcNome = true;

    private String[] _possibleSuggestionsScCodigo = {};
    private String[] _possibleSuggestionsScNome = {};
    private String[][] _possibleSuggestionsSc = {};
    private Set<String> possibleSuggestionsScCodigo;
    private Set<String> possibleSuggestionsScNome;
    private boolean isSearchScCodigo = true;
    private boolean isSearchScNome = true;

    private String[] _possibleSuggestionsCtCodigo = {};
    private String[] _possibleSuggestionsCtNome = {};
    private String[][] _possibleSuggestionsCt = {};
    private Set<String> possibleSuggestionsCtCodigo;
    private Set<String> possibleSuggestionsCtNome;
    private boolean isSearchCtCodigo = true;
    private boolean isSearchCtNome = true;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        aviAno.setValueFactory(ano);

        SpinnerValueFactory mes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        aviMes.setValueFactory(mes);

        Platform.runLater(() -> {
            mes.setValue(Dates.iMonth(DbMain.getDateTimeServer()));
            ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        });

        aviAdm.setOnSelectionChanged(event -> {Platform.runLater(() ->txbAvCodigo.requestFocus());});
        aviProp.setOnSelectionChanged(event -> {Platform.runLater(() ->txbPrCodigo.requestFocus());});
        aviLoca.setOnSelectionChanged(event -> {Platform.runLater(() ->txbLcCodigo.requestFocus());});
        aviSoc.setOnSelectionChanged(event -> {Platform.runLater(() ->txbScCodigo.requestFocus());});
        aviCtas.setOnSelectionChanged(event -> {Platform.runLater(() ->txbCtCodigo.requestFocus());});

        tabPaneAvisos.getSelectionModel().select(0);

        AutocompletePr();
        AutocompleteLc();
        AutocompleteAdm();
        AutocompleteSocios();
        AutocompleteContas();

        btnListar.setOnAction(event -> {
            if (aviAdm.isSelected()) {
                FillAvisos("ADM", txbAvCodigo.getText(),aviAno.getValue(), aviMes.getValue());
            } else if (aviProp.isSelected()) {
                FillAvisos("PRO", txbPrCodigo.getText(),aviAno.getValue(), aviMes.getValue());
            } else if (aviLoca.isSelected()) {
                FillAvisos("LOC", txbLcCodigo.getText(),aviAno.getValue(), aviMes.getValue());
            } else if (aviSoc.isSelected()) {
                FillAvisos("SOC", txbScCodigo.getText(),aviAno.getValue(), aviMes.getValue());
            } else {
                FillAvisos("CON", txbCtCodigo.getText(),aviAno.getValue(), aviMes.getValue());
            }
        });

        btnImprimir.setOnAction(event -> {
            if (Integer.valueOf(aviAutent.getText()) <= 0) return;
            Object[][] dadosAviso = null;
            try { dadosAviso = conn.LerCamposTabela(new String[] {"documento", "operacao", "aut", "datahora", "valor", "logado", "lancamentos"}, "caixa", "aut = " + aviAutent.getText()); } catch (Exception e) {}
            if (dadosAviso == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Não existe este autenticação!!!");
                alert.showAndWait();
                return;
            }
            if (!dadosAviso[0][3].toString().equalsIgnoreCase("AVI")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Esta autenticação não é um aviso!!!");
                alert.showAndWait();
                return;
            }

            String[][] lancto = ConvertArrayString2ObjectArrays(dadosAviso[6][3].toString());
            BigInteger Aut = new BigInteger(aviAutent.getText());
            Collections dadm = VariaveisGlobais.getAdmDados();

            Object[][] textoAdm = null;
            try { textoAdm = conn.LerCamposTabela(new String[] {"texto", "conta", "registro"},"avisos", "aut_rec = " + aviAutent.getText()); } catch (Exception e) {}
            String Texto = null;
            if (textoAdm != null) Texto = textoAdm[0][3].toString();

            String tpAviso = null; String codigo = null; String nome = null;
            if (textoAdm[1][3].toString().equalsIgnoreCase("0")) {
                codigo = textoAdm[2][3].toString();
                try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                tpAviso = "ADM";
            } else if (textoAdm[1][3].toString().equalsIgnoreCase("1")) {
                codigo = textoAdm[2][3].toString();
                try {nome = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                tpAviso = "PROPRIETARIOS";
            } else if (textoAdm[1][3].toString().equalsIgnoreCase("2")) {
                codigo = textoAdm[2][3].toString();
                try {nome = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome"}, "locatarios", "l_contrato = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                tpAviso = "LOCATARIOS";
            } else if (textoAdm[1][3].toString().equalsIgnoreCase("3")) {
                codigo = textoAdm[2][3].toString();
                try {nome = conn.LerCamposTabela(new String[] {"s_nome"}, "socios", "s_id = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                tpAviso = "SOCIOS";
            } else {
                codigo = textoAdm[2][3].toString();
                try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                tpAviso = "CONTAS";
            }

            Object[] dados = {dadosAviso[1][3].toString().equalsIgnoreCase("DEB") ? "D�BITO" : "CR�DITO", tpAviso, codigo, nome, new BigDecimal(LerValor.Number2BigDecimal(dadosAviso[4][3].toString().replace("R$ ",""))), Texto};
            new Impressao(Aut, lancto, Dates.String2Date(dadosAviso[3][3].toString()), dadosAviso[5][3].toString()).ImprimeAvisoPDF(dadm, dados, true);
        });
    }

    private void AutocompleteLc() {
        _possibleSuggestionsLcContrato = new String[]{};
        _possibleSuggestionsLcNome = new String[]{};
        _possibleSuggestionsLc = new String[][]{};
        possibleSuggestionsLcContrato = new HashSet<String>();
        possibleSuggestionsLcNome = new HashSet<String>();
        isSearchLcContrato = true;
        isSearchLcNome = true;

        try {
            TextFields.bindAutoCompletion(txbLcCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbLcNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Locatarios
        qSQL = "SELECT l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome FROM locatarios ORDER BY l_contrato;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsLcContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcContrato, qcontrato);
                possibleSuggestionsLcContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsLcContrato));

                _possibleSuggestionsLcNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcNome, qnome);
                possibleSuggestionsLcNome = new HashSet<>(Arrays.asList(_possibleSuggestionsLcNome));

                _possibleSuggestionsLc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsLc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbLcCodigo, possibleSuggestionsLcContrato);
        TextFields.bindAutoCompletion(txbLcNome, possibleSuggestionsLcNome);

        txbLcCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbLcCodigo.setText(null);
                txbLcNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbLcCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc, 0, txbLcCodigo.getText());
                    if (pos > -1 && isSearchLcContrato) {
                        isSearchLcNome = false;
                        txbLcNome.setText(_possibleSuggestionsLc[pos][1]);
                        isSearchLcNome = true;
                    }
                } else {
                    isSearchLcContrato = false;
                    isSearchLcNome = true;
                }
            }
        });

        txbLcNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc,1, txbLcNome.getText());
                String pcontrato = null;
                try {pcontrato = txbLcCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchLcNome && pcontrato == null) {
                    isSearchLcContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsLc,1,txbLcNome.getText())) {
                        txbLcCodigo.setText(_possibleSuggestionsLc[pos][0]);
                    } else {
                        WindowEnderecos(txbLcNome.getText());
                    }
                    isSearchLcContrato = true;
                } else {
                    isSearchLcContrato = true;
                    isSearchLcNome = false;
                }
            }
        });
    }

    private void WindowEnderecos(String snome) {
        try {
            AnchorPane root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/Movimento/Alteracao/Enderecos.fxml"));
            } catch (Exception e) {e.printStackTrace();}
            UICustomComponent wrappedRoot = new UICustomComponent(root);

            UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
            internalFrame.setLayout(new UIBorderLayout());
            internalFrame.setModal(true);
            internalFrame.setResizable(false);
            internalFrame.setMaximizable(false);
            internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
            internalFrame.setTitle("Endere�os");
            //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
            internalFrame.setClosable(true);


            internalFrame.setBackground(new UIColor(103,165, 162));
            //internalFrame.setBackground(new UIColor(51,81, 135));

            internalFrame.pack();
            internalFrame.setVisible(true);

            root.fireEvent(new paramEvent(new Object[] {anchorPaneAvisos, txbLcNome.getText()}, paramEvent.GET_PARAM));
        } catch (Exception e) {e.printStackTrace();}
    }

    private void AutocompletePr() {
        _possibleSuggestionsPrRegistro = new String[]{};
        _possibleSuggestionsPrNome = new String[]{};
        _possibleSuggestionsPr = new String[][]{};
        possibleSuggestionsPrRegistro = new HashSet<String>();
        possibleSuggestionsPrNome = new HashSet<String>();
        isSearchPrRegistro = true;
        isSearchPrNome = true;

        try {
            TextFields.bindAutoCompletion(txbPrCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbPrNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Proprietarios
        qSQL = "SELECT p_rgprp AS l_contrato, p_nome AS l_nome FROM proprietarios ORDER BY p_rgprp;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsPrRegistro = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrRegistro, qcontrato);
                possibleSuggestionsPrRegistro = new HashSet<>(Arrays.asList(_possibleSuggestionsPrRegistro));

                _possibleSuggestionsPrNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrNome, qnome);
                possibleSuggestionsPrNome = new HashSet<>(Arrays.asList(_possibleSuggestionsPrNome));

                _possibleSuggestionsPr = FuncoesGlobais.ArraysAdd(_possibleSuggestionsPr, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbPrCodigo, possibleSuggestionsPrRegistro);
        TextFields.bindAutoCompletion(txbPrNome, possibleSuggestionsPrNome);

        txbPrCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbPrCodigo.setText(null);
                txbPrNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbPrCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr, 0, txbPrCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchPrNome = false;
                        txbPrNome.setText(_possibleSuggestionsPr[pos][1]);
                        isSearchPrNome = true;
                    }
                } else {
                    isSearchPrRegistro = false;
                    isSearchPrNome = true;
                }
            }
        });

        txbPrNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr,1, txbPrNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbPrCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchPrNome && pcontrato != null) {
                    isSearchPrRegistro = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsPr,1,txbPrNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsPr[pos][0]);
                    }
                    isSearchPrRegistro = true;
                } else {
                    isSearchPrRegistro = true;
                    isSearchPrNome = false;
                }
            }
        });
    }

    private void AutocompleteAdm() {
        _possibleSuggestionsAvCodigo = new String[]{};
        _possibleSuggestionsAvNome = new String[]{};
        _possibleSuggestionsAv = new String[][]{};
        possibleSuggestionsAvCodigo = new HashSet<String>();
        possibleSuggestionsAvNome = new HashSet<String>();
        isSearchAvCodigo = true;
        isSearchAvNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Contas ADM Fixas
        qSQL = "SELECT id, codigo, descricao FROM adm ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsAvCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvCodigo, qcontrato);
                possibleSuggestionsAvCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsAvCodigo));

                _possibleSuggestionsAvNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvNome, qnome);
                possibleSuggestionsAvNome = new HashSet<>(Arrays.asList(_possibleSuggestionsAvNome));

                _possibleSuggestionsAv = FuncoesGlobais.ArraysAdd(_possibleSuggestionsAv, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        // Contas ADM Variaveis
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'ADM' AND deletada = false ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsAvCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvCodigo, qcontrato);
                possibleSuggestionsAvCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsAvCodigo));

                _possibleSuggestionsAvNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvNome, qnome);
                possibleSuggestionsAvNome = new HashSet<>(Arrays.asList(_possibleSuggestionsAvNome));

                _possibleSuggestionsAv = FuncoesGlobais.ArraysAdd(_possibleSuggestionsAv, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbAvCodigo, possibleSuggestionsAvCodigo);
        TextFields.bindAutoCompletion(txbAvNome, possibleSuggestionsAvNome);

        txbAvCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbAvCodigo.setText(null);
                txbAvNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbAvCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsAv, 0, txbAvCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchAvNome = false;
                        txbAvNome.setText(_possibleSuggestionsAv[pos][1]);
                        isSearchAvNome = true;
                    }
                } else {
                    isSearchAvCodigo = false;
                    isSearchAvNome = true;
                }
            }
        });

        txbAvNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsAv,1, txbAvNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbAvCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchAvNome && pcontrato != null) {
                    isSearchAvCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsAv,1,txbAvNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsAv[pos][0]);
                    }
                    isSearchAvCodigo = true;
                } else {
                    isSearchAvCodigo = true;
                    isSearchAvNome = false;
                }
            }
        });
    }

    private void AutocompleteSocios() {
        _possibleSuggestionsScCodigo = new String[]{};
        _possibleSuggestionsScNome = new String[]{};
        _possibleSuggestionsSc = new String[][]{};
        possibleSuggestionsScCodigo = new HashSet<String>();
        possibleSuggestionsScNome = new HashSet<String>();
        isSearchScCodigo = true;
        isSearchScNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Socios_Adm
        qSQL = "SELECT s_id, s_nome FROM socios ORDER BY s_id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("s_id");} catch (SQLException e) {}
                try {qnome = imv.getString("s_nome");} catch (SQLException e) {}
                _possibleSuggestionsScCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsScCodigo, qcontrato);
                possibleSuggestionsScCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsScCodigo));

                _possibleSuggestionsScNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsScNome, qnome);
                possibleSuggestionsScNome = new HashSet<>(Arrays.asList(_possibleSuggestionsScNome));

                _possibleSuggestionsSc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsSc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbScCodigo, possibleSuggestionsScCodigo);
        TextFields.bindAutoCompletion(txbScNome, possibleSuggestionsScNome);

        txbScCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbScCodigo.setText(null);
                txbScNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbScCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsSc, 0, txbScCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchScNome = false;
                        txbScNome.setText(_possibleSuggestionsSc[pos][1]);
                        isSearchScNome = true;
                    }
                } else {
                    isSearchScCodigo = false;
                    isSearchScNome = true;
                }
            }
        });

        txbScNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsSc,1, txbScNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbScCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchScNome && pcontrato != null) {
                    isSearchScCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsSc,1,txbScNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsSc[pos][0]);
                    }
                    isSearchScCodigo = true;
                } else {
                    isSearchScCodigo = true;
                    isSearchScNome = false;
                }
            }
        });
    }

    private void AutocompleteContas() {
        _possibleSuggestionsCtCodigo = new String[]{};
        _possibleSuggestionsCtNome = new String[]{};
        _possibleSuggestionsCt = new String[][]{};
        possibleSuggestionsCtCodigo = new HashSet<String>();
        possibleSuggestionsCtNome = new HashSet<String>();
        isSearchCtCodigo = true;
        isSearchCtNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Socios_Adm
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'CONTAS' AND deletada = false ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsCtCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsCtCodigo, qcontrato);
                possibleSuggestionsCtCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsCtCodigo));

                _possibleSuggestionsCtNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsCtNome, qnome);
                possibleSuggestionsCtNome = new HashSet<>(Arrays.asList(_possibleSuggestionsCtNome));

                _possibleSuggestionsCt = FuncoesGlobais.ArraysAdd(_possibleSuggestionsCt, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbCtCodigo, possibleSuggestionsCtCodigo);
        TextFields.bindAutoCompletion(txbCtNome, possibleSuggestionsCtNome);

        txbCtCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbCtCodigo.setText(null);
                txbCtNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbCtCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsCt, 0, txbCtCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchCtNome = false;
                        txbCtNome.setText(_possibleSuggestionsCt[pos][1]);
                        isSearchCtNome = true;
                    }
                } else {
                    isSearchCtCodigo = false;
                    isSearchCtNome = true;
                }
            }
        });

        txbCtNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsCt,1, txbCtNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbCtCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchCtNome && pcontrato != null) {
                    isSearchCtCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsCt,1,txbCtNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsCt[pos][0]);
                    }
                    isSearchCtCodigo = true;
                } else {
                    isSearchCtCodigo = true;
                    isSearchCtNome = false;
                }
            }
        });
    }

    private void FillAvisos(String tipoAviso, String documento, int anoAviso, int mesAviso) {
        List<cAvisos> data = new ArrayList<cAvisos>();
        String Sql = null;
        switch (tipoAviso) {
            case "ADM":
                Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.contrato = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";
                break;
            case "PRO":
                Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.rgprp::varchar = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";
                break;
            case "LOC":
                Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.contrato = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";
                break;
            case "SOC":
                Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.rgprp::varchar = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";
                break;
            case "CON":
                Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.contrato = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";
                break;
        }

        Object[][] param = new Object[][] {
                {"string", documento},
                {"int", mesAviso},
                {"int", anoAviso}
        };
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, param);
            int gId = -1; int gAut = -1;
            BigDecimal gValor = new BigDecimal("0");
            Date gDataHora = null;
            String gLogado = null; String gLanctos = null; String gOper = null;

            while (rs.next()) {
                try {gId = rs.getInt("id");} catch (SQLException sqlex) {}
                try {gOper = rs.getString("operacao");} catch (SQLException sqlex) {}
                try {gAut = rs.getInt("aut");} catch (SQLException sqlex) {}
                try {gDataHora = Dates.String2Date(rs.getString("datahora"));} catch (SQLException sqlex) {}
                try {gValor = new BigDecimal(LerValor.Number2BigDecimal(rs.getString("valor").replace("R$ ","")));} catch (SQLException sqlex) {}
                try {gLogado = rs.getString("logado");} catch (SQLException sqlex) {}
                try {gLanctos = rs.getString("lancamentos");} catch (SQLException sqlex) {}

                data.add(new cAvisos(gId, gOper, gAut, gDataHora, gValor, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        aviId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        aviId.setStyle( "-fx-alignment: CENTER;");

        aviOper.setCellValueFactory(new PropertyValueFactory<>("Operacao"));
        aviOper.setStyle( "-fx-alignment: CENTER;");

        aviAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        aviAut.setStyle( "-fx-alignment: CENTER;");

        aviDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        aviDataHora.setCellFactory((AbstractConvertCellFactory<cAvisos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        aviDataHora.setStyle( "-fx-alignment: CENTER;");

        aviValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        aviValor.setCellFactory((AbstractConvertCellFactory<cAvisos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        aviValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        aviLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        aviLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        aviLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        aviLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        aviAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        aviAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) ListaAvi.setItems(FXCollections.observableArrayList(data));

        ListaAvi.setOnMouseClicked(event -> {
            cAvisos select = ListaAvi.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                Collections dadm = VariaveisGlobais.getAdmDados();

                String tpAviso = null; String codigo = null; String nome = null;
                if (aviAdm.isSelected()) {
                    codigo = txbAvCodigo.getText();
                    nome = txbAvNome.getText();
                    tpAviso = "ADM";
                } else if (aviProp.isSelected()) {
                    codigo = txbPrCodigo.getText();
                    nome = txbPrNome.getText();
                    tpAviso = "PROPRIET�RIOS";
                } else if (aviLoca.isSelected()) {
                    codigo = txbLcCodigo.getText();
                    nome = txbLcNome.getText();
                    tpAviso = "LOCAT�RIOS";
                } else if (aviSoc.isSelected()) {
                    codigo = txbScCodigo.getText();
                    nome = txbScNome.getText();
                    tpAviso = "S�CIOS";
                } else {
                    codigo = txbCtCodigo.getText();
                    nome = txbCtNome.getText();
                    tpAviso = "CONTAS";
                }

                Object[][] textoAdm = null;
                try { textoAdm = conn.LerCamposTabela(new String[] {"texto"},"avisos", "aut_rec = " + select.getAut()); } catch (Exception e) {}
                String Texto = null;
                if (textoAdm != null) Texto = textoAdm[0][3].toString();

                Object[] dados = {select.getOperacao().equalsIgnoreCase("DEB") ? "D�BITO" : "CR�DITO", tpAviso, codigo, nome, select.getValor(), Texto};
                new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeAvisoPDF(dadm, dados, true);
            }
        });
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

        // Fase 1 - Remo��o dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ArraysAdd(retorno,
                    new String[]{
                            vtr[0].trim().replace("\"",""),
                            vtr[4].trim().replace("\"",""),
                            vtr[3].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[5].trim().replace("\"",""),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }

    // Classe que cria o bot�o
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell(){
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cAvisos select = (cAvisos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPaneAvisos.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cAvisos select = (cAvisos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    if (select == null) return;

                    Object[][] dadosAviso = null;
                    try { dadosAviso = conn.LerCamposTabela(new String[] {"documento", "operacao", "aut", "datahora", "valor", "logado", "lancamentos"}, "caixa", "aut = " + select.getAut()); } catch (Exception e) {}
                    if (dadosAviso == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Aten��o");
                        alert.setHeaderText("Autentica��o");
                        alert.setContentText("N�o existe este autentica��o!!!");
                        alert.showAndWait();
                        return;
                    }
                    if (!dadosAviso[0][3].toString().equalsIgnoreCase("AVI")) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Aten��o");
                        alert.setHeaderText("Autentica��o");
                        alert.setContentText("Esta autentica��o n�o � um aviso!!!");
                        alert.showAndWait();
                        return;
                    }

                    String[][] lancto = ConvertArrayString2ObjectArrays(dadosAviso[6][3].toString());
                    BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                    Collections dadm = VariaveisGlobais.getAdmDados();

                    Object[][] textoAdm = null;
                    try { textoAdm = conn.LerCamposTabela(new String[] {"texto", "conta", "registro"},"avisos", "aut_rec = " + select.getAut()); } catch (Exception e) {}
                    String Texto = null;
                    if (textoAdm != null) Texto = textoAdm[0][3].toString();

                    String tpAviso = null; String codigo = null; String nome = null;
                    if (textoAdm[1][3].toString().equalsIgnoreCase("0")) {
                        codigo = textoAdm[2][3].toString();
                        try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                        tpAviso = "ADM";
                    } else if (textoAdm[1][3].toString().equalsIgnoreCase("1")) {
                        codigo = textoAdm[2][3].toString();
                        try {nome = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                        tpAviso = "PROPRIET�RIOS";
                    } else if (textoAdm[1][3].toString().equalsIgnoreCase("2")) {
                        codigo = textoAdm[2][3].toString();
                        try {nome = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome"}, "locatarios", "l_contrato = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                        tpAviso = "LOCAT�RIOS";
                    } else if (textoAdm[1][3].toString().equalsIgnoreCase("3")) {
                        codigo = textoAdm[2][3].toString();
                        try {nome = conn.LerCamposTabela(new String[] {"s_nome"}, "socios", "s_id = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                        tpAviso = "S�CIOS";
                    } else {
                        codigo = textoAdm[2][3].toString();
                        try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
                        tpAviso = "CONTAS";
                    }

                    Object[] dados = {dadosAviso[1][3].toString().equalsIgnoreCase("DEB") ? "D�BITO" : "CR�DITO", tpAviso, codigo, nome, new BigDecimal(LerValor.Number2BigDecimal(dadosAviso[4][3].toString().replace("R$ ",""))), Texto};
                    new Impressao(Aut, lancto, Dates.String2Date(dadosAviso[3][3].toString()), dadosAviso[5][3].toString()).ImprimeAvisoPDF(dadm, dados, true);
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                HBox pane = new HBox(cellButton, cellAnexar);
                setGraphic(pane);
            }
        }
    }

    private String[][] ConvertArrayString2ObjectArrays_REC(String value) {
        String[][] retorno = {};

        // Fase 1 - Remo��o dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ArraysAdd(retorno,
                    new String[]{
                            vtr[0].trim().replace("\"",""),
                            vtr[4].trim().replace("\"",""),
                            vtr[3].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[5].trim().replace("\"",""),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }
}
