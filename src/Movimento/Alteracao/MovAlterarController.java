package Movimento.Alteracao;

import Calculos.Processa;
import Classes.paramEvent;
import Funcoes.*;
import Movimento.tbvAltera;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.TextFields;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/*
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.scene.control.window.WindowIcon;
*/

/**
 * Created by supervisor on 23/11/16.
 */
public class MovAlterarController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    public int linha = 0;
    boolean isFillVecto;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;
    private String rgprp = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField contrato;
    @FXML private TextField nome;
    @FXML private TextField rgimv;
    @FXML private TextField nomeprop;
    @FXML private TextField enderimv;
    @FXML private TextField bairroimv;
    @FXML private TextField cidadeimv;
    @FXML private TextField estadoimv;
    @FXML private TextField cepimv;
    @FXML private TextField nrvenctos;
    @FXML private ComboBox<String> venctos;
    @FXML private TextField valorvenctos;
    @FXML private CheckBox mu;
    @FXML private TextField muvr;
    @FXML private CheckBox co;
    @FXML private TextField covr;
    @FXML private CheckBox ju;
    @FXML private TextField juvr;
    @FXML private CheckBox te;
    @FXML private TextField tevr;
    @FXML private Button btnExcluir;
    @FXML private Button btnApagar;
    @FXML private Button btnDesDif;
    @FXML private Button btnSeg;
    @FXML private Button btnTaxas;
    @FXML private ToggleSwitch btnAlterar;
    @FXML private ToggleSwitch btnLiberar;

    @FXML private ProgressBar progressBar;

    @FXML private TableView<tbvAltera> tableView;
    @FXML private TableColumn<tbvAltera, Boolean> tableColumn_Tag;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Descr;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Cota;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Valor;
    @FXML private TableColumn<tbvAltera, Boolean> tableColumn_Ret;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        AutocompleteContratoNome();

        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if ((String)event.sparam[0] != null) {
                contrato.setText((String) event.sparam[0]);
                DadosImovel((boolean) event.sparam[1]);
            } else {
                Task task = ProcessaCampos();
                progressBar.setVisible(true);
                progressBar.progressProperty().bind(task.progressProperty());
                new Thread(task).start();
            }
        });

        MaskFieldUtil.monetaryField(muvr);
        MaskFieldUtil.monetaryField(juvr);
        MaskFieldUtil.monetaryField(covr);
        MaskFieldUtil.monetaryField(tevr);

        muvr.editableProperty().bind(mu.selectedProperty());
        mu.setOnAction(event -> { if(mu.isSelected()) muvr.requestFocus(); });

        covr.editableProperty().bind(co.selectedProperty());
        co.setOnAction(event -> { if(co.isSelected()) covr.requestFocus(); });

        juvr.editableProperty().bind(ju.selectedProperty());
        ju.setOnAction(event -> { if(ju.isSelected()) juvr.requestFocus(); });

        tevr.editableProperty().bind(te.selectedProperty());
        te.setOnAction(event -> { if(te.isSelected()) tevr.requestFocus(); });

        mu.disableProperty().bind(btnLiberar.selectedProperty().not());
        muvr.disableProperty().bind(btnLiberar.selectedProperty().not());
        co.disableProperty().bind(btnLiberar.selectedProperty().not());
        covr.disableProperty().bind(btnLiberar.selectedProperty().not());
        ju.disableProperty().bind(btnLiberar.selectedProperty().not());
        juvr.disableProperty().bind(btnLiberar.selectedProperty().not());
        te.disableProperty().bind(btnLiberar.selectedProperty().not());
        tevr.disableProperty().bind(btnLiberar.selectedProperty().not());

        tableView.disableProperty().bind(btnAlterar.selectedProperty().not());
        btnAlterar.setOnMouseClicked(event -> {
            if (!btnAlterar.isSelected()) AtualizaGrid();
            //tableView.setDisable(!btnAlterar.isSelected());
            tableView.getSelectionModel().clearSelection();
            btnAlterar.requestFocus();
        });

        BooleanProperty AlterarBtn = btnAlterar.selectedProperty();
        btnDesDif.disableProperty().bind(AlterarBtn.not());
        btnDesDif.visibleProperty().bind(AlterarBtn);
        btnSeg.disableProperty().bind(AlterarBtn.not());
        btnSeg.visibleProperty().bind(AlterarBtn);
        btnTaxas.disableProperty().bind(AlterarBtn.not());
        btnTaxas.visibleProperty().bind(AlterarBtn);

        btnDesDif.setOnAction(event -> { try {ChamaTela("Desconto/Diferença Aluguel","/Movimento/Alteracao/DescDif.fxml", "loca.png");} catch (Exception e) {} });
        btnTaxas.setOnAction(event -> { try {ChamaTela("Taxas","/Movimento/Alteracao/Taxas.fxml", "loca.png");} catch (Exception e) {} });
        btnSeg.setOnAction(event -> { try {ChamaTela("Seguro","/Movimento/Alteracao/Seguro.fxml", "loca.png");} catch (Exception e) {} });

        mujucoepLiberacoesInicializacoes();

        btnApagar.setOnAction(event -> {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir o nnumero?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Sim")) {
                String uSql = "UPDATE movimento SET nnumero = null WHERE contrato = '%s' AND dtvencimento = '%s';";
                uSql = String.format(uSql,
                        this.contrato.getText(),
                        Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(), "dd/MM/yyyy", "yyyy-MM-dd")
                );
                try { conn.ExecutarComando(uSql); } catch (Exception e) { }
                btnApagar.setDisable(true);
            }
        });

        btnExcluir.setOnAction(event -> {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir o vencimento?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Sim")) {
                String tvcto = venctos.getSelectionModel().getSelectedItem();
                String uSql = "DELETE FROM movimento WHERE contrato = '%s' AND dtvencimento = '%s';";
                uSql = String.format(uSql,
                        this.contrato.getText(),
                        Dates.StringtoString(tvcto, "dd/MM/yyyy", "yyyy-MM-dd")
                );
                try { conn.ExecutarComando(uSql); } catch (Exception e) { }

                // --> Atualiza carteira voltando o vencimento para geração
                String tnvcto = Dates.DateFormata("yyyy-MM-dd", Dates.DateAdd(Dates.MES, -1, Dates.StringtoDate(tvcto,"dd-MM-yyyy")));
                String trefer = FuncoesGlobais.subCota(Dates.StringtoString(tnvcto,"yyyy-MM-dd", "MM/yyyy"));
                uSql = "UPDATE carteira SET dtvencimento = '%s', referencia = '%s', cota = Right(Repeat('0',2) || Cast(substring(cota from 1 for 2) as integer) - 1,2) || substring(cota from 3) WHERE contrato = '%s';";
                uSql = String.format(uSql,
                        tnvcto,
                        trefer,
                        this.contrato.getText()
                );
                try { conn.ExecutarComando(uSql); } catch (Exception e) { }


                // -> deleta o item do combobox e mostra item anterior na tela
                venctos.getItems().remove(tvcto);
                venctos.getSelectionModel().select(venctos.getItems().size() - 1);
                nrvenctos.setText(FuncoesGlobais.StrZero(String.valueOf(venctos.getItems().size()),2));
            }
        });

        Platform.runLater(() -> {
            mu.setText(VariaveisGlobais.contas_ca.get("MUL"));
            co.setText(VariaveisGlobais.contas_ca.get("COR"));
            ju.setText(VariaveisGlobais.contas_ca.get("JUR"));
            te.setText(VariaveisGlobais.contas_ca.get("EXP"));

            contrato.requestFocus();
        });
    }

    private void mujucoepLiberacoesInicializacoes() {
        // Multa
        mu.setOnAction(event -> {
            String uSql = "UPDATE movimento SET lmu = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
            uSql = String.format(uSql,
                    mu.isSelected(),
                    this.contrato.getText(),
                    Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
            );
            try { conn.ExecutarComando(uSql); } catch (Exception e) {}
            if (mu.isSelected()) {
                muvr.setText("0,00");
                Platform.runLater(() -> muvr.selectAll());
                muvr.requestFocus();
            } else if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
        });

        muvr.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (mu.isSelected() && LerValor.StringToFloat(muvr.getText()) > 0) {
                    String uSql = "UPDATE movimento SET mu = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            LerValor.StringToFloat(muvr.getText()),
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                } else if (mu.isSelected() && LerValor.StringToFloat(muvr.getText()) == 0) {
                    String uSql = "UPDATE movimento SET mu = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            "0",
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                }
                if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
            }
        });

        muvr.disableProperty().bind(mu.selectedProperty().not().or(btnLiberar.selectedProperty().not()));

        // Juros
        ju.setOnAction(event -> {
            String uSql = "UPDATE movimento SET lju = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
            uSql = String.format(uSql,
                    ju.isSelected(),
                    this.contrato.getText(),
                    Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
            );
            try { conn.ExecutarComando(uSql); } catch (Exception e) {}
            if (ju.isSelected()) {
                juvr.setText("0,00");
                Platform.runLater(() -> juvr.selectAll());
                juvr.requestFocus();
            } else if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
        });

        juvr.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (ju.isSelected() && LerValor.StringToFloat(juvr.getText()) > 0) {
                    String uSql = "UPDATE movimento SET ju = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            LerValor.StringToFloat(juvr.getText()),
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                } else if (ju.isSelected() && LerValor.StringToFloat(juvr.getText()) == 0) {
                    String uSql = "UPDATE movimento SET ju = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            "0",
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                }
                if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
            }
        });

        juvr.disableProperty().bind(ju.selectedProperty().not().or(btnLiberar.selectedProperty().not()));

        // Correção
        co.setOnAction(event -> {
            String uSql = "UPDATE movimento SET lco = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
            uSql = String.format(uSql,
                    co.isSelected(),
                    this.contrato.getText(),
                    Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
            );
            try { conn.ExecutarComando(uSql); } catch (Exception e) {}
            if (co.isSelected()) {
                covr.setText("0,00");
                Platform.runLater(() -> covr.selectAll());
                covr.requestFocus();
            } else if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
        });

        covr.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (co.isSelected() && LerValor.StringToFloat(covr.getText()) > 0) {
                    String uSql = "UPDATE movimento SET co = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            LerValor.StringToFloat(covr.getText()),
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                } else if (co.isSelected() && LerValor.StringToFloat(covr.getText()) == 0) {
                    String uSql = "UPDATE movimento SET co = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            "0",
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                }
                if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
            }
        });

        covr.disableProperty().bind(co.selectedProperty().not().or(btnLiberar.selectedProperty().not()));

        // Expediente
        te.setOnAction(event -> {
            String uSql = "UPDATE movimento SET lep = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
            uSql = String.format(uSql,
                    te.isSelected(),
                    this.contrato.getText(),
                    Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
            );
            try { conn.ExecutarComando(uSql); } catch (Exception e) {}
            if (te.isSelected()) {
                tevr.setText("0,00");
                Platform.runLater(() -> tevr.selectAll());
                tevr.requestFocus();
            } else if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
        });

        tevr.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (te.isSelected() && LerValor.StringToFloat(tevr.getText()) > 0) {
                    String uSql = "UPDATE movimento SET ep = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            LerValor.StringToFloat(tevr.getText()),
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                } else if (te.isSelected() && LerValor.StringToFloat(tevr.getText()) == 0) {
                    String uSql = "UPDATE movimento SET ep = '%s' WHERE contrato = '%s' AND dtvencimento = '%s';";
                    uSql = String.format(uSql,
                            "0",
                            this.contrato.getText(),
                            Dates.StringtoString(venctos.getSelectionModel().getSelectedItem(),"dd/MM/yyyy","yyyy-MM-dd")
                    );
                    try { conn.ExecutarComando(uSql); } catch (Exception e) {}
                }
                if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
            }
        });

        tevr.disableProperty().bind(te.selectedProperty().not().or(btnLiberar.selectedProperty().not()));
    }

    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        String qSQL = "SELECT l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome FROM locatarios ORDER BY l_contrato;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        try {
            TextFields.bindAutoCompletion(contrato, possibleSuggestionsContrato);
            TextFields.bindAutoCompletion(nome, possibleSuggestionsNome);
        } catch (Exception ex) {}
        
        contrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                contrato.setText(null);
                nome.setText(null);

                // Limpa dados do Imovel
                rgimv.setText(null);
                nomeprop.setText(null);
                enderimv.setText(null);
                bairroimv.setText(null);
                cidadeimv.setText(null);
                estadoimv.setText(null);
                cepimv.setText(null);

                btnExcluir.setDisable(true);
                btnAlterar.setDisable(true);
                btnAlterar.setSelected(false);
                btnLiberar.setDisable(true);
                btnLiberar.setSelected(false);

                BooleanBinding LiberarBtn = btnLiberar.selectedProperty().not();
                mu.disableProperty().bind(LiberarBtn);
                muvr.disableProperty().bind(LiberarBtn);
                co.disableProperty().bind(LiberarBtn);
                covr.disableProperty().bind(LiberarBtn);
                ju.disableProperty().bind(LiberarBtn);
                juvr.disableProperty().bind(LiberarBtn);
                te.disableProperty().bind(LiberarBtn);
                tevr.disableProperty().bind(LiberarBtn);

                btnLiberar.setOnMouseClicked(event -> {
                    btnLiberar.requestFocus();
                });

                tableView.setItems(FXCollections.observableArrayList());

                nrvenctos.setText(null);
                venctos.setItems(FXCollections.observableArrayList());
                valorvenctos.setText("0,00");

                muvr.setText("0,00");
                covr.setText("0,00");
                juvr.setText("0,00");
                tevr.setText("0,00");

            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = contrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, contrato.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        nome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, nome.getText());} catch (NullPointerException e) {}
                String pcontrato = null;
                try {pcontrato = contrato.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,nome.getText())) {
                        contrato.setText(_possibleSuggestions[pos][0]);
                    } else {
                        WindowEnderecos(nome.getText());
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
                DadosImovel(true);
            }
        });
    }

    private void WindowEnderecos(String snome) {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource("/Movimento/Alteracao/Enderecos.fxml")); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(snome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(false);


        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);
        root.fireEvent(new paramEvent(new Object[] {anchorPane, nome.getText()}, paramEvent.GET_PARAM));
    }

    private void DadosImovel(boolean isOk) {
        String sql = "SELECT p.p_nome, i.i_rgimv, i.i_end || ', ' || i.i_num || ' ' || i.i_cplto AS i_ender, i.i_bairro, i.i_cidade, i.i_estado, i.i_cep FROM locatarios l, imoveis i, proprietarios p WHERE l.l_contrato = '%s' and l.l_rgimv = i.i_rgimv and l.l_rgprp = CAST(p.p_rgprp AS text)";
        sql = String.format(sql, contrato.getText());
        ResultSet imv = null;
        try {
            imv = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qpnome = null, qirgimv = null, qiend = null, qibai = null, qicid = null;
                String qiest = null, qicep = null;
                try {qpnome = imv.getString("p_nome");} catch (SQLException e) {}
                try {qirgimv = imv.getString("i_rgimv");} catch (SQLException e) {}
                try {qiend = imv.getString("i_ender");} catch (SQLException e) {}
                try {qibai = imv.getString("i_bairro");} catch (SQLException e) {}
                try {qicid = imv.getString("i_cidade");} catch (SQLException e) {}
                try {qiest = imv.getString("i_estado");} catch (SQLException e) {}
                try {qicep = imv.getString("i_cep");} catch (SQLException e) {}

                rgimv.setText(qirgimv);
                nomeprop.setText(qpnome);
                enderimv.setText(qiend);
                bairroimv.setText(qibai);
                cidadeimv.setText(qicid);
                estadoimv.setText(qiest);
                cepimv.setText(qicep);
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        if (isOk) {
            nrvenctos.setDisable(false);
            venctos.setDisable(false);
            valorvenctos.setDisable(false);
            btnApagar.setDisable(false);
            btnAlterar.setDisable(false);
            btnLiberar.setDisable(false);

            cepimv.requestFocus();
        }

        isFillVecto = true;
        Object[] vctos = FiltraVencimentos(contrato.getText());
        vctos = FuncoesGlobais.ObjectsOrdenaData(vctos);

        nrvenctos.setText(FuncoesGlobais.StrZero(String.valueOf(vctos.length),2));
        venctos.getItems().clear();
        if (vctos.length > 0) {
            for (Object d : vctos) {
                String dt = Dates.DateFormata("dd/MM/yyyy",(Date)d);
                venctos.getItems().add(dt);
            }
            venctos.getSelectionModel().select(0);
        } else {
            new Alert(Alert.AlertType.INFORMATION,"Contrato sem Vencimentos").show();
            Platform.runLater(() -> contrato.requestFocus());
            return;
        }
        isFillVecto = false;

        venctos.valueProperty().addListener((observable, oldValue, newValue) -> {
            btnExcluir.setDisable(!venctos.getSelectionModel().isSelected(venctos.getItems().size() - 1));

            if (!isFillVecto) {
                if (venctos.getItems().size() > 0) {
                    Task task = ProcessaCampos();
                    progressBar.setVisible(true);
                    progressBar.progressProperty().bind(task.progressProperty());
                    new Thread(task).start();
                }
            }
        });

        Task task = ProcessaCampos();
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

    }

    private Object[] FiltraVencimentos(String contrato) {
        Object[] venctos = {};

        String sql = "SELECT dtvencimento FROM movimento WHERE dtrecebimento Is Null AND contrato = '%s' ORDER BY dtvencimento;";
        sql = String.format(sql, contrato);
        ResultSet resultSet = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                Date vcto = null;
                try { vcto = resultSet.getDate("dtvencimento"); } catch (SQLException e) {}
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) {}
        try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM taxas WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {}
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
        }

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM seguros WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {}
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {
                DbMain.FecharTabela(resultSet);
            } catch (Exception e) {}
        }

        return FuncoesGlobais.ObjectsRemoveDup(venctos);
    }

    public Task ProcessaCampos() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> {
                    contrato.setDisable(true);
                    nome.setDisable(true);

                    tableView.getItems().clear();
                });

                // Pegar o mes de referencia do Vencimento no descdif
                String vcto = venctos.getSelectionModel().getSelectedItem();
                String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,venctos.getItems().size() <= 1 ? 0 : -1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));

                linha = 0;
                List<tbvAltera> data = new ArrayList<tbvAltera>();

                Object[][] DadosLoc = null;
                try { DadosLoc = conn.LerCamposTabela(new String[] {"l_rgprp", "l_rgimv"}, "locatarios", "l_contrato = ?", new Object[][] {{"string", contrato.getText()}});} catch (Exception ex) {}
                if (DadosLoc != null) rgprp = DadosLoc[0][3].toString(); else rgprp = null;

                // Movimento
                String sql = "SELECT * FROM movimento WHERE contrato = '%s' AND referencia = '%s' and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                int recordCount = DbMain.RecordCount(resultSet);

                BigDecimal vrAluguel = new BigDecimal(0);
                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "C";
                        String qdes = "ALUGUEL", qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "mensal";

                        try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("mensal").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new tbvAltera("movimento", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                        try {vrAluguel = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}

                        // disabilita botao quando nnumero for igual a branco
                        boolean isnnumero = true;
                        try {
                            isnnumero = resultSet.getString("nnumero").equalsIgnoreCase("");
                        } catch (Exception e) {isnnumero = true;}
                        btnApagar.setDisable(isnnumero);
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // Desconto Diferença
                BigDecimal desal = new BigDecimal("0");
                BigDecimal difal = new BigDecimal("0");
                sql = "SELECT * FROM descdif WHERE contrato = '%s' AND referencia = '%s' and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "";
                        String qdes = "IPTU #" + ++linha, qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        if (rgprp == null) {
                            try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                        }

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            try {qdes = "Dif.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                        } else {
                            // Desconto
                            try {qdes = "Desc.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                        }
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new tbvAltera("descdif", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            difal = difal.add(resultSet.getBigDecimal("valor"));
                        } else {
                            // Desconto
                            desal = desal.add(resultSet.getBigDecimal("valor"));
                        }

                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // IRRF
                BigDecimal irenda = new BigDecimal("0");
                try {
                    irenda = new Calculos.Irrf().Irrf(rgprp, contrato.getText(), descdif_refer.trim(), vrAluguel, difal, desal);
                    if (irenda.compareTo(BigDecimal.ZERO) == 1) {
                        data.add(new tbvAltera("irrf", "", 0, true, "C", VariaveisGlobais.contas_ca.get("IRF"), descdif_refer.trim(), LerValor.FormatPattern(irenda.toPlainString(), "#,##0.00"), false));
                    }
                } catch (Exception e) {}

                // Taxas
                sql = "SELECT * FROM taxas WHERE contrato = '%s' AND referencia = '%s' and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true, qret = false; int qid = 0; String qtipo = "";
                        String qdes = "" , qpos = "", qcota = "";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        // Ler o nome da taxa
                        String sWhere = null;
                        try {sWhere = "codigo = '" + resultSet.getString("campo") + "'";} catch (SQLException e) {}

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                        try {qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif." ) + (String)conn.LerCamposTabela(new String[] {"descricao"},"campos", sWhere)[0][3];} catch (SQLException e) {}
                        try {qpos = resultSet.getString("poscampo");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}
                        try {qret = resultSet.getBoolean("retencao");} catch (SQLException e) {}

                        data.add(new tbvAltera("taxas", qvariavel, qid, qtag, qtipo, qdes + " " + qpos, qcota, qvalor, qret));
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // Seguros
                sql = "SELECT * FROM seguros WHERE contrato = '%s' AND referencia = '%s';";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "C";
                        String qdes = "SEGURO", qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new tbvAltera("seguros", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                String[] ciptu = new Calculos.Iptu().Iptu(rgimv.getText(),descdif_refer.trim());
                int iptuId = 0;
                String iptuMes = null;
                String iptuRef = null;
                String vrIptu = null;
                if (ciptu != null) {
                    if (ciptu[0] != null) {
                        iptuId = Integer.valueOf(ciptu[0]);
                        iptuMes = ciptu[1];
                        iptuRef = ciptu[2];
                        vrIptu = LerValor.FormatPattern(ciptu[3], "#,##0.00");
                    }
                }
                if (vrIptu != null && !vrIptu.trim().equalsIgnoreCase("0,00")) data.add(new tbvAltera("iptu",iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu, false));

                Platform.runLater(() -> {
                    tableColumn_Tag.setCellValueFactory(new PropertyValueFactory<tbvAltera, Boolean>("tag"));
                    final BooleanProperty selected = new SimpleBooleanProperty();
                    tableColumn_Tag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                        @Override
                        public ObservableValue<Boolean> call(Integer index) {
                            tbvAltera tbvlinhas = ((tbvAltera) tableView.getItems().get(index));
                            if (tbvlinhas.getId() > 0 && btnAlterar.isSelected()) {
                                String uSql = "UPDATE " + tbvlinhas.getOnde() + " SET selected = '" + tableView.getItems().get(index).isCheckedTag().get() + "' WHERE id = " + tbvlinhas.getId() + ";";
                                //System.out.println(uSql);
                                try {conn.ExecutarComando(uSql);} catch (Exception e) {}

                                // TODO - Auditor
                                try {conn.Auditor("Contrato: " + contrato.getText() + " Cota:" + tableView.getItems().get(index).getCota().trim(),tableView.getItems().get(index).getDesc().toUpperCase().trim() + ": " + tableView.getItems().get(index).isCheckedTag().get());} catch (Exception ex) {}

                                if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
                            }
                            return tableView.getItems().get(index).isCheckedTag();
                        }
                    }));

                    tableColumn_Descr.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("desc"));

                    tableColumn_Cota.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("cota"));
                    tableColumn_Cota.setStyle( "-fx-alignment: CENTER;");

                    tableColumn_Valor.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("valor"));
                    tableColumn_Valor.setEditable(true);

                    Callback<TableColumn<tbvAltera, String>, TableCell<tbvAltera, String>> cellFactory = (TableColumn<tbvAltera, String> p) -> new EditingCellConteudo();
                    tableColumn_Valor.setCellFactory(cellFactory);
                    tableColumn_Valor.setOnEditCommit((TableColumn.CellEditEvent<tbvAltera, String> t) -> {
                                //String xcampo = ((tbvAltera) t.getTableView().getItems().get(t.getTablePosition().getRow())).getValor();
                                t.getTableView().getItems().get(t.getTablePosition().getRow()).setValor(t.getNewValue());
                                tbvAltera tbvlinhas = ((tbvAltera) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                                System.out.println("ID: " + tbvlinhas.getId() + " Onde: " +tbvlinhas.getOnde() + " Valor: " + tbvlinhas.getValor() + " " + tbvlinhas.getTipo() + " " + (tbvlinhas.isCheckedTag().getValue() ? "Selecionada" : "Desabilitada"));
                                if (tbvlinhas.getId() > 0 && btnAlterar.isSelected()) {
                                    String uSql = null;
                                    if (tbvlinhas.getValor().trim().equals("0,00") && !tbvlinhas.getOnde().equals("iptu")) {
                                        uSql = "DELETE FROM " + tbvlinhas.getOnde() + " WHERE id = " + tbvlinhas.getId() + ";";
                                    } else {
                                        uSql = "UPDATE " + tbvlinhas.getOnde() + " SET " + tbvlinhas.getVariavel() + " = '" + LerValor.StringToFloat(tbvlinhas.getValor()) + "' WHERE id = " + tbvlinhas.getId() + ";";
                                    }
                                    System.out.println(uSql);
                                    conn.ExecutarComando(uSql);

                                    if (venctos.getItems().size() > 0) UpdateCalculos(venctos.getSelectionModel().getSelectedItem());
                                }
                            }
                    );
                    tableColumn_Valor.setStyle( "-fx-alignment: CENTER-RIGHT;");

                    tableColumn_Ret.setCellValueFactory(new PropertyValueFactory<tbvAltera, Boolean>("rt"));
                    tableColumn_Ret.setStyle( "-fx-alignment: CENTER;");
                    tableColumn_Ret.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                        @Override
                        public ObservableValue<Boolean> call(Integer index) {
                            return tableView.getItems().get(index).isCheckedRt();
                        }
                    }));
                    tableColumn_Ret.setEditable(false);

                    if (!data.isEmpty()) tableView.setItems(FXCollections.observableArrayList(data));
                    if (venctos.getItems().size() > 0 && !vcto.isEmpty()) UpdateCalculos(vcto);

                    progressBar.setVisible(false);
                    contrato.setDisable(false); nome.setDisable(false);
                    progressBar.progressProperty().unbind();
                    //progressBar.setProgress(0);
                });
                return true;
            }
        };
    }

    private void UpdateCalculos(String vcto) {
        Platform.runLater(() -> {
            Processa calc = new Processa(rgprp, rgimv.getText(), contrato.getText(), Dates.StringtoDate(vcto,"dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()),"dd-MM-yyyy"));
            valorvenctos.setText(new DecimalFormat("#,##0.00").format(calc.TotalRecibo()));
            muvr.setText(new DecimalFormat("#,##0.00").format(calc.Multa()));
            covr.setText(new DecimalFormat("#,##0.00").format(calc.Correcao()));
            juvr.setText(new DecimalFormat("#,##0.00").format(calc.Juros()));
            tevr.setText(new DecimalFormat("#,##0.00").format(calc.Expediente()));

            mu.setSelected(calc.isIsmu());
            ju.setSelected(calc.isIsju());
            co.setSelected(calc.isIsco());
            te.setSelected(calc.isIsep());
        });
    }

    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);


        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        // Pegar o mes de referencia do Vencimento no descdif
        String vcto = venctos.getSelectionModel().getSelectedItem();
        String refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));
        root.fireEvent(new paramEvent(new Object[]{contrato.getText(), rgprp, rgimv.getText(), refer}, paramEvent.GET_PARAM));

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam[0] == null) {
                try {internalFrame.close();} catch (NullPointerException e) {}
                AtualizaGrid();
            }
        });
    }

    private void AtualizaGrid() {
        Task task = ProcessaCampos();
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
/*
    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource(url));
            root.setStyle("-fx-font-family: 'Arial';\n" +
                    "-fx-font-size: 12;"
            );
            Window canvas = new Window(nome);
            canvas.setContentPane(root);
            canvas.setResizableWindow(false);
            canvas.setStyle("-fx-background-color:" +
                            "rgba(255, 255, 255, 0.08)," +
                            "rgba(0, 0, 0, 0.8)," +
                            "#090a0c," +
                            "linear-gradient(#4a5661 0%, #1f2429 20%, #1f242a 100%)," +
                            "linear-gradient(#242a2e, #23282e)," +
                            "radial-gradient(center 50% 0%, radius 100%, rgba(0,142,148,0.9)," +
                            "rgba(255,255,255,0));\n" +
                            "-fx-font-family: 'DejaVu Sans';" +
                            "-fx-font-style: italic;" +
                            "-fx-alignment: center;" +
                            "-fx-text-fill: rgba(0, 0, 0, 1);" +
                            "-fx-effect: dropshadow( one-pass-box , rgba(255, 255, 255, 0.6), 0, 0.0 , 0 , 1 );");

            WindowIcon windowIcon = new WindowIcon();
            canvas.getLeftIcons().add(windowIcon);
            canvas.getRightIcons().add(new CloseIcon(canvas));
            anchorPane.getChildren().add(canvas);

            //root.fireEvent(new vieaEvent("VIEA", vieaEvent.GET_VIEA));
            // Pegar o mes de referencia do Vencimento no descdif
            String vcto = venctos.getSelectionModel().getSelectedItem();
            String refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));
            root.fireEvent(new paramEvent(new Object[]{contrato.getText(), rgprp, rgimv.getText(), refer}, paramEvent.GET_PARAM));
        } catch (Exception e) {e.printStackTrace();}
    }
*/

}

