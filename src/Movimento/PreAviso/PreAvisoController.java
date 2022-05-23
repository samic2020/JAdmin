package Movimento.PreAviso;

import Classes.TextAreaInputDialog;
import Classes.paramEvent;
import Funcoes.*;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.controlsfx.control.textfield.TextFields;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PreAvisoController implements Initializable {
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


    public static DataFormat dataFormat = new DataFormat("mydata");

    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane anchorPaneAvisos;
    @FXML private AnchorPane anchorPaneRetornar;
    @FXML private TabPane tbpAvisos;

    @FXML private Tab avAdm;
    @FXML private TextField txbAvCodigo;
    @FXML private TextField txbAvNome;

    @FXML private Tab AvPrp;
    @FXML private TextField txbPrCodigo;
    @FXML private TextField txbPrNome;

    @FXML private Tab AvLoc;
    @FXML private TextField txbLcCodigo;
    @FXML private TextField txbLcNome;

    @FXML private Tab AvSc;
    @FXML private TextField txbScCodigo;
    @FXML private TextField txbScNome;

    @FXML private Tab AvCta;
    @FXML private TextField txbCtCodigo;
    @FXML private TextField txbCtNome;

    @FXML private HTMLEditor txaTexto;

    @FXML private ToggleButton tgbCrDb;
    @FXML private TextField txbValor;
    @FXML private Button btnLancar;

    @FXML private Button tgbLancamentos;
    @FXML private Button tgbRetornar;

    @FXML private Pane pnlLancamentos;

    @FXML private TableView<TablePreAviso> tblLancados;
    @FXML private TableColumn<TablePreAviso, Integer> tblLan_id;
    @FXML private TableColumn<TablePreAviso, String> tblLan_conta;
    @FXML private TableColumn<TablePreAviso, String> tblLan_codigo;
    @FXML private TableColumn<TablePreAviso, String> tblLan_tipo;
    @FXML private TableColumn<TablePreAviso, String> tblLan_texto;
    @FXML private TableColumn<TablePreAviso, BigDecimal> tblLan_valor;
    @FXML private TableColumn<TablePreAviso, String> tblLan_usuario;
    @FXML private TableColumn<TablePreAviso, String> tblLan_dtlanc;
    @FXML private TableColumn<TablePreAviso, String> tblLan_obs;
    @FXML private TableColumn<TablePreAviso, Integer> tblLan_aut;

    private RotateTransition ida;
    private RotateTransition vol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        AutocompletePr();
        AutocompleteLc();
        AutocompleteAdm();
        AutocompleteSocios();
        AutocompleteContas();
        //AtualizaPreAvisos();

        tgbCrDb.setOnAction((ActionEvent event) -> {
            CrDb();
        });

        tgbLancamentos.setOnAction((ActionEvent event) -> {
            TelaLancamentos();

            MaskFieldUtil.monetaryField(txbValor);
            txbValor.requestFocus();
        });

        tgbRetornar.setOnAction((ActionEvent event) -> {
            TelaRetornar();
            tblLancados.requestFocus();
        });

        btnLancar.setOnAction(event -> {
            TextAreaInputDialog dialog = new TextAreaInputDialog();
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            Optional result = dialog.showAndWait();

            String tRegistro = "";
            switch (tbpAvisos.getSelectionModel().getSelectedIndex()) {
                case 0: // Adm
                    tRegistro = txbAvCodigo.getText();
                    break;
                case 1: // Proprietário
                    tRegistro = txbPrCodigo.getText();
                    break;
                case 2: // Locatários
                    tRegistro = txbLcCodigo.getText();
                    break;
                case 3: // Sócios
                    tRegistro = txbScCodigo.getText();
                    break;
                case 4: // Contas
                    tRegistro = txbCtCodigo.getText();
                    break;
                default:
                    tRegistro = null;
            }
            int tConta = tbpAvisos.getSelectionModel().getSelectedIndex();
            String tTipo = tgbCrDb.isSelected() ? "DEB" : "CRE";
            String tTexto = txaTexto.getHtmlText();
            BigDecimal tValor = new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText()));
            BigInteger aut = conn.PegarAutenticacao();
            String tUser = VariaveisGlobais.usuario;
            java.sql.Date tData = new java.sql.Date(DbMain.getDateTimeServer().getTime());
            String tObs = null;
            if (result.isPresent()) {
                tObs = (String)result.get();
            }

            Object[][] param = new Object[][] {
                    {"int", tConta},
                    {"string", tRegistro},
                    {"string", tTipo},
                    {"string", tTexto},
                    {"decimal", tValor},
                    {"bigint", aut},
                    {"string", tUser},
                    {"date", tData},
                    {"string", tObs}
            };

            LancarPreAviso(param);
        });
    }

/*
  conta integer,
  registro character varying(6),
  tipo character varying(3),
  texto text,
  valor numeric(10,2),
  bloqueio character varying(7),
  dtbloqueio date,
  usr_bloqueio character varying(20),
  dtrecebimento date,
  aut_rec bigint,
  usr_rec character varying(20),
  aut_pag character varying[],
  reserva character varying[],
  pre_aut bigint,
  pre_usr character varying(20),
  pre_dtlanc date,
  pre_dtconta date,
  pre_obs text
  */

    private void LancarPreAviso(Object[][] param) {
        String Sql = "INSERT INTO avisos (conta, registro, tipo, texto, valor, pre_aut, pre_usr, pre_dtlanc, pre_obs) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        if (param.length > 0) {
            conn.ExecutarComando(Sql,param);
        }
    }

    private void TelaRetornar() {
        Platform.runLater(() -> {
            anchorPaneAvisos.setVisible(false);
            anchorPaneRetornar.setVisible(true);
            ida = new RotateTransition(Duration.seconds(2), anchorPaneRetornar);
            ida.setFromAngle(0);
            ida.setToAngle(180);
            ida.setAutoReverse(false);
            ida.setCycleCount(1);
            ida.setAxis(new Point3D(0, 90, 90));
            ida.play();

            ida.setOnFinished(event -> {
                anchorPaneAvisos.setVisible(true);
                anchorPaneRetornar.setVisible(false);
                voltaRetornar();
            });
        });
    }

    private void voltaRetornar() {
        Platform.runLater(() -> {
            vol = new RotateTransition(Duration.seconds(2), anchorPaneAvisos);
            vol.setFromAngle(180);
            vol.setToAngle(0);
            vol.setAutoReverse(false);
            vol.setCycleCount(1);
            vol.setAxis(new Point3D(0, 90, 90));
            vol.play();
        });
    }

    private void TelaLancamentos() {
        Platform.runLater(() -> {
            anchorPaneAvisos.setVisible(true);
            anchorPaneRetornar.setVisible(false);
            ida = new RotateTransition(Duration.seconds(2), anchorPaneAvisos);
            ida.setFromAngle(0);
            ida.setToAngle(180);
            ida.setAutoReverse(false);
            ida.setCycleCount(1);
            ida.setAxis(new Point3D(0, 90, 90));
            ida.play();

            ida.setOnFinished(event -> {
                anchorPaneAvisos.setVisible(false);
                anchorPaneRetornar.setVisible(true);
                voltaLancamentos();
                AtualizaPreAvisos();
            });
        });
    }

    private void voltaLancamentos() {
        Platform.runLater(() -> {
            vol = new RotateTransition(Duration.seconds(2), anchorPaneRetornar);
            vol.setFromAngle(180);
            vol.setToAngle(0);
            vol.setAutoReverse(false);
            vol.setCycleCount(1);
            vol.setAxis(new Point3D(0, 90, 90));
            vol.play();
        });
    }

    private void CrDb() {
        if (tgbCrDb.isSelected()) {
            tgbCrDb.setText("D�bito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#ff5400, #be1d00);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;" +
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        } else {
            tgbCrDb.setText("Cr�dito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#00ff54, #00be1d);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;"+
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        }

        MaskFieldUtil.monetaryField(txbValor);
        txbValor.requestFocus();
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

            root.fireEvent(new paramEvent(new Object[] {anchorPane, txbLcNome.getText()}, paramEvent.GET_PARAM));
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
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'ADM' ORDER BY id;";
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
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'CONTAS' ORDER BY id;";

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

    private void AtualizaPreAvisos() {
        List<TablePreAviso> data = new ArrayList<TablePreAviso>();
        ResultSet vrs;
        String Sql = "SELECT id, conta, registro, tipo, texto, valor, pre_usr, pre_dtlanc, pre_obs, aut_rec FROM avisos WHERE dtrecebimento is null and not pre_dtlanc is null;";

        try {
            vrs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
            while (vrs.next()) {
                TablePreAviso registro = new TablePreAviso();
                try {registro.setId(vrs.getInt("id"));} catch (SQLException e) {}
                try {registro.setConta(tipoConta(vrs.getString("conta")));} catch (SQLException e) {}
                try {registro.setCodigo(vrs.getString("registro"));} catch (SQLException e) {}
                try {registro.setTipo(vrs.getString("tipo"));} catch (SQLException e) {}
                try {registro.setTexto(getPreview(vrs.getString("texto")));} catch (SQLException e) {}
                try {registro.setValor(vrs.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setUsuario(vrs.getString("pre_usr"));} catch (SQLException e) {}
                try {registro.setDatalanc(vrs.getDate("pre_dtlanc"));} catch (SQLException e) {}
                try {registro.setObs(vrs.getString("pre_obs"));} catch (SQLException e) {}
                try {registro.setAut(vrs.getInt("aut_rec"));} catch (SQLException e) {}

                data.add(new TablePreAviso(registro.getId(), registro.getConta(), registro.getCodigo(), registro.getTipo(), registro.getTexto(), registro.getValor(), registro.getUsuario(), registro.getDatalanc(), registro.getObs(), registro.getAut()));
            }
            vrs.close();
        } catch (SQLException e) {}

        tblLan_id.setCellValueFactory(new PropertyValueFactory("id"));
        tblLan_conta.setCellValueFactory(new PropertyValueFactory("conta"));
        tblLan_codigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        tblLan_tipo.setCellValueFactory(new PropertyValueFactory("tipo"));
        tblLan_texto.setCellValueFactory(new PropertyValueFactory("texto"));
        /*tblLan_texto.setCellFactory(new Callback<TableColumn<TablePreAviso, String>, TableCell<TablePreAviso, String>>() {

            @Override
            public TableCell<TablePreAviso, String> call(TableColumn<TablePreAviso, String> param) {
                return new HTMLCell();
            }
        });*/

        tblLan_valor.setCellValueFactory(new PropertyValueFactory("valor"));
        tblLan_usuario.setCellValueFactory(new PropertyValueFactory("usuario"));
        tblLan_dtlanc.setCellValueFactory(new PropertyValueFactory("datalanc"));
        tblLan_obs.setCellValueFactory(new PropertyValueFactory("obs"));
        tblLan_aut.setCellValueFactory(new PropertyValueFactory("aut"));

        tblLancados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblLancados.setItems(FXCollections.observableArrayList(data));
        tblLancados.setEditable(true);

        tblLancados.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DELETE && !tblLancados.getSelectionModel().getSelectedItems().isEmpty()) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lan�amento?", new ButtonType("Sim"), new ButtonType("N�o"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("N�o")) return;

                String dSql = "DELETE FROM avisos WHERE id = ?";
                conn.ExecutarComando(dSql, new Object[][]{{"int", tblLancados.getSelectionModel().getSelectedItems().get(0).getId()}});
                tblLancados.getItems().removeAll(tblLancados.getSelectionModel().getSelectedItems());
            }
        });

        tblLancados.setRowFactory(tv -> {
            return new TableRow<TablePreAviso>() {
                @Override
                public void updateItem(TablePreAviso item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (item == null) {
                        setStyle("");
                    } else if (item.getAut() < 0) {
                        setStyle("-fx-text-background-color: #FF0000;");
                    } else {
                        setStyle("-fx-background-color: #000000;");
                    }
                }
            };
        });
    }

    private String tipoConta(String conta) {
        String retorno = "";
        switch (conta) {
            case "0": retorno = "ADM"; break;
            case "1": retorno = "PROPRIET�RIOS"; break;
            case "2": retorno = "LOCAT�RIOS"; break;
            case "3": retorno = "S�CIOS"; break;
            case "4": retorno = "CONTAS"; break;
        }
        return retorno;
    }

    private class HTMLCell extends TableCell<TablePreAviso, String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                WebView webView = new WebView();
                webView.setPrefSize(tblLan_texto.getWidth(), 18);
                webView.setMaxWidth(tblLan_texto.getWidth());
                webView.setMaxHeight(18);
                WebEngine engine = webView.getEngine();
                // setGraphic(new Label("Test"));
                setGraphic(webView);
                String formula = item;
                engine.loadContent(formula);

            }
        }
    }

    private String getPreview(String _html) {
        String preview = null;
        if (_html != null) {
            Document doc = Jsoup.parse(_html);
            preview = doc.body().text();
        }
        return preview;
    }
}
