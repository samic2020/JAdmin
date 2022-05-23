package Gerencia;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by supervisor on 26/01/17.
 */
public class MultasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsp_codigo = {};
    private String[] _possibleSuggestionsp_Nome = {};
    private String[][] _possibleSuggestions_p = {};
    private Set<String> possibleSuggestionsp_codigo;
    private Set<String> possibleSuggestionsp_Nome;

    private String[] _possibleSuggestionsi_codigo = {};
    private String[] _possibleSuggestionsi_Nome = {};
    private String[][] _possibleSuggestions_i = {};
    private Set<String> possibleSuggestionsi_codigo;
    private Set<String> possibleSuggestionsi_Nome;

    private boolean isSearchcodigo_p = true;
    private boolean isSearchNome_p = true;
    private boolean isSearchcodigo_i = true;
    private boolean isSearchNome_i = true;

    private boolean isNew = false;

    @FXML private AnchorPane anchorPane;

    @FXML private ToggleGroup tipomulta;
    @FXML private RadioButton mu_geral;
    @FXML private RadioButton mu_proprietarios;
    @FXML private RadioButton mu_imoveis;

    // Proprietarios
    @FXML private TextField p_codigo;
    @FXML private TextField p_nome;

    // Imoveis
    @FXML private TextField i_codigo;
    @FXML private TextField i_nome;

    // Precentuais da Administradora
    @FXML private Label lMulta;
    @FXML private TextField tMulta;

    @FXML private Label lJuros;
    @FXML private TextField tJuros;

    @FXML private Label lCorrecao;
    @FXML private TextField tCorrecao;

    @FXML private Label lExpediente;
    @FXML private TextField tExpediente;

    // Multa
    @FXML private Label titleMU;
    @FXML private TextField residencial;
    @FXML private TextField comercial;

    // Correção
    @FXML private Label titleCO;
    @FXML private ToggleGroup tipocor;
    @FXML private RadioButton co_simples;
    @FXML private RadioButton co_composta;
    @FXML private TextField co_pecent;
    @FXML private CheckBox cor_limite;
    @FXML private TextField cor_limite_a;

    // Juros
    @FXML private Label titleJU;
    @FXML private ToggleGroup tipojur;
    @FXML private RadioButton ju_mes;
    @FXML private RadioButton ju_aomes;
    @FXML private TextField ju_perc;

    // Carencias
    @FXML private Label titleCA;
    @FXML private TextField ca_mu;
    @FXML private TextField ca_ju;
    @FXML private TextField ca_co;

    // Comissão
    @FXML private Label titleCM;
    @FXML private TextField cm_perc;

    // Expediente
    @FXML private TextField ep_perc;
    @FXML private TextField ep_valor;

    // Botoes
    @FXML private Label titleEP;
    @FXML private Button btlancar;

    // TableView multas
    @FXML private TableView<MultasModel> multas;
    @FXML private TableColumn<MultasModel, Integer> multas_id;
    @FXML private TableColumn<MultasModel, String> multas_tipo;
    @FXML private TableColumn<MultasModel, String> pa_mu;
    @FXML private TableColumn<MultasModel, String> pa_ju;
    @FXML private TableColumn<MultasModel, String> pa_co;
    @FXML private TableColumn<MultasModel, String> pa_ep;
    @FXML private TableColumn<MultasModel, String> mu_res;
    @FXML private TableColumn<MultasModel, String> mu_com;
    @FXML private TableColumn<MultasModel, String> co_tipo;
    @FXML private TableColumn<MultasModel, String> co_perc;
    @FXML private TableColumn<MultasModel, Boolean> co_limite;
    @FXML private TableColumn<MultasModel, String> co_dias;
    @FXML private TableColumn<MultasModel, String> ju_tipo;
    @FXML private TableColumn<MultasModel, String> ju_percent;
    @FXML private TableColumn<MultasModel, String> ca_multa;
    @FXML private TableColumn<MultasModel, String> ca_juros;
    @FXML private TableColumn<MultasModel, String> ca_correcao;
    @FXML private TableColumn<MultasModel, String> co;
    @FXML private TableColumn<MultasModel, String> ep_percent;
    @FXML private TableColumn<MultasModel, String> ep_vrlor;
    @FXML private Button btexcluir;

    @FXML private CheckBox bol_txbanc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Autocomplete_p();
        Autocomplete_i();
        tipomulta.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            clearFields();
            if (((String) ((RadioButton) newValue).getText()).toLowerCase().equalsIgnoreCase("geral")) {
                populaMultas(0);
                p_codigo.setVisible(false);
                p_codigo.setDisable(true);
                p_nome.setVisible(false);
                p_nome.setDisable(true);

                i_codigo.setVisible(false);
                i_codigo.setDisable(true);
                i_nome.setVisible(false);
                i_nome.setDisable(true);

                btlancar.setDisable(multas.getItems().size() != 0);
                isNew = multas.getItems().size() == 0;
            } else if (((String) ((RadioButton) newValue).getText()).toLowerCase().equalsIgnoreCase("proprietarios")) {
                populaMultas(1);
                p_codigo.setDisable(false);
                p_codigo.setVisible(true);
                p_nome.setDisable(false);
                p_nome.setVisible(true);

                i_codigo.setDisable(true);
                i_codigo.setVisible(false);
                i_nome.setDisable(true);
                i_nome.setVisible(false);
            } else if (((String) ((RadioButton) newValue).getText()).toLowerCase().equalsIgnoreCase("imoveis")) {
                populaMultas(2);
                p_codigo.setDisable(true);
                p_codigo.setVisible(false);
                p_nome.setDisable(true);
                p_nome.setVisible(false);

                i_codigo.setDisable(false);
                i_codigo.setVisible(true);
                i_nome.setDisable(false);
                i_nome.setVisible(true);
            }
        });

        p_codigo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            //p_codigo.setText("");
           // p_nome.setText("");
            multas.getSelectionModel().clearSelection();
            isNew = true;
        });
        i_codigo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            //i_codigo.setText("");
            //i_nome.setText("");
            multas.getSelectionModel().clearSelection();
            isNew = true;
        });
        btexcluir.disableProperty().bind(multas.getSelectionModel().selectedIndexProperty().isEqualTo(-1));

        btexcluir.setOnAction(event -> {
            String iSql = "DELETE FROM public.multas WHERE id = " + multas.getSelectionModel().getSelectedItem().getId() +";";
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Sim")) {
                conn.ExecutarComando(iSql);
                if (mu_geral.isSelected()) {
                    populaMultas(0);
                } else if (mu_proprietarios.isSelected()) {
                    populaMultas(1);
                } else if (mu_imoveis.isSelected()) {
                    populaMultas(2);
                }
            }
        });
        btlancar.setOnAction(event -> {
            if (isNew) {
                String iSql = "INSERT INTO multas(" +
                        "multa_tipo, registro, pa_mu, pa_ju, pa_co, pa_ep, mu_res, " +
                        "mu_com, co_tipo, co_perc, co_limite, co_dia, ju_tipo, ju_percent, " +
                        "ca_multa, ca_juros, ca_correcao, co, ep_percent, ep_vrlor, bol_txlanc) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                        "?, ?, ?, ?, ?, ?, ?);";

                try {
                    int nid = 1;
                    PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                    if (mu_geral.isSelected()) {
                        pstmt.setString(nid++, "geral");
                        pstmt.setString(nid++, "0");
                    } else if (mu_proprietarios.isSelected()) {
                        pstmt.setString(nid++, "proprietario");
                        pstmt.setString(nid++, p_codigo.getText().trim());
                    } else if (mu_imoveis.isSelected()) {
                        pstmt.setString(nid++, "imovel");
                        pstmt.setString(nid++, i_codigo.getText().trim());
                    }
                    pstmt.setDouble(nid++, Double.valueOf(tMulta.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tJuros.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tCorrecao.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tExpediente.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(residencial.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(comercial.getText().trim()));

                    pstmt.setString(nid++, co_simples.isSelected() ? "simples" : "composta");
                    pstmt.setDouble(nid++, Double.valueOf(co_pecent.getText().trim().replace(",",".")));
                    pstmt.setInt(nid++, cor_limite.isSelected() ? 1 : 0);
                    pstmt.setInt(nid++, Integer.valueOf(cor_limite_a.getText().trim()));

                    pstmt.setString(nid++, ju_mes.isSelected() ? "ao mes" : "por mes");
                    pstmt.setDouble(nid++, Double.valueOf(ju_perc.getText().trim()));

                    pstmt.setInt(nid++, Integer.valueOf(ca_mu.getText().trim()));
                    pstmt.setInt(nid++, Integer.valueOf(ca_ju.getText().trim()));
                    pstmt.setInt(nid++, Integer.valueOf(ca_co.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(cm_perc.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(ep_perc.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(ep_valor.getText().trim()));
                    pstmt.setBoolean(nid++, bol_txbanc.isSelected());

                    pstmt.executeUpdate();
                } catch (SQLException ex) {}
            } else {
                String iSql = "UPDATE public.multas " +
                              "SET multa_tipo=?, registro=?, pa_mu=?, pa_ju=?, pa_co=?, pa_ep=?, " +
                              "mu_res=?, mu_com=?, co_tipo=?, co_perc=?, co_limite=?, co_dia=?, " +
                              "ju_tipo=?, ju_percent=?, ca_multa=?, ca_juros=?, ca_correcao=?, " +
                              "co=?, ep_percent=?, ep_vrlor=?, bol_txbanc=? " +
                              "WHERE id = " + multas.getSelectionModel().getSelectedItem().getId() +";";

                try {
                    int nid = 1;
                    PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                    if (mu_geral.isSelected()) {
                        pstmt.setString(nid++, "geral");
                        pstmt.setString(nid++, "0");
                    } else if (mu_proprietarios.isSelected()) {
                        pstmt.setString(nid++, "proprietario");
                        pstmt.setString(nid++, p_codigo.getText().trim());
                    } else if (mu_imoveis.isSelected()) {
                        pstmt.setString(nid++, "imovel");
                        pstmt.setString(nid++, i_codigo.getText().trim());
                    }
                    pstmt.setDouble(nid++, Double.valueOf(tMulta.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tJuros.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tCorrecao.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(tExpediente.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(residencial.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(comercial.getText().trim()));

                    pstmt.setString(nid++, co_simples.isSelected() ? "simples" : "composta");
                    pstmt.setDouble(nid++, Double.valueOf(co_pecent.getText().trim()));
                    pstmt.setInt(nid++, cor_limite.isSelected() ? 1 : 0);
                    pstmt.setInt(nid++, Integer.valueOf(co_dias.getText().trim()));

                    pstmt.setString(nid++, ju_mes.isSelected() ? "ao mes" : "por mes");
                    pstmt.setDouble(nid++, Double.valueOf(ju_perc.getText().trim()));

                    pstmt.setInt(nid++, Integer.valueOf(ca_mu.getText().trim()));
                    pstmt.setInt(nid++, Integer.valueOf(ca_ju.getText().trim()));
                    pstmt.setInt(nid++, Integer.valueOf(ca_co.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(cm_perc.getText().trim()));

                    pstmt.setDouble(nid++, Double.valueOf(ep_perc.getText().trim()));
                    pstmt.setDouble(nid++, Double.valueOf(ep_valor.getText().trim()));
                    pstmt.setBoolean(nid++, bol_txbanc.isSelected());

                    pstmt.executeUpdate();
                } catch (SQLException ex) {}
            }
            if (mu_geral.isSelected()) {
                populaMultas(0);
            } else if (mu_proprietarios.isSelected()) {
                populaMultas(1);
            } else if (mu_imoveis.isSelected()) {
                populaMultas(2);
            }
        });

        multas.setOnMouseClicked(event -> {
            if (!multas.getSelectionModel().isEmpty()) {
                tMulta.setText(multas.getSelectionModel().getSelectedItem().getPa_mu());
                tJuros.setText(multas.getSelectionModel().getSelectedItem().getPa_ju());
                tCorrecao.setText(multas.getSelectionModel().getSelectedItem().getPa_co());
                tExpediente.setText(multas.getSelectionModel().getSelectedItem().getPa_ep());

                residencial.setText(multas.getSelectionModel().getSelectedItem().getMu_res());
                comercial.setText(multas.getSelectionModel().getSelectedItem().getMu_com());

                co_simples.setSelected(multas.getSelectionModel().getSelectedItem().getCo_tipo().toLowerCase().equalsIgnoreCase("simples") ? true : false);
                co_pecent.setText(multas.getSelectionModel().getSelectedItem().getCo_perc());
                cor_limite.setSelected(multas.getSelectionModel().getSelectedItem().getCo_limite());
                cor_limite_a.setText(multas.getSelectionModel().getSelectedItem().getCo_dias());

                ju_mes.setSelected(multas.getSelectionModel().getSelectedItem().getJu_tipo().toLowerCase().equalsIgnoreCase("ao mes") ? true : false);
                ju_perc.setText(multas.getSelectionModel().getSelectedItem().getJu_percent());

                ca_mu.setText(multas.getSelectionModel().getSelectedItem().getCa_multa());
                ca_ju.setText(multas.getSelectionModel().getSelectedItem().getCa_juros());
                ca_co.setText(multas.getSelectionModel().getSelectedItem().getCa_correcao());

                cm_perc.setText(multas.getSelectionModel().getSelectedItem().getCo());

                ep_perc.setText(multas.getSelectionModel().getSelectedItem().getEp_percent());
                ep_valor.setText(multas.getSelectionModel().getSelectedItem().getEp_vrlor());

                bol_txbanc.setSelected(multas.getSelectionModel().getSelectedItem().isBol_txbanc());

                isNew = false;
            } else {
                clearFields();
                isNew = true;
            }
        });
    }

    private void clearFields() {
        tMulta.setText("");
        tJuros.setText("");
        tCorrecao.setText("");
        tExpediente.setText("");

        residencial.setText("");
        comercial.setText("");

        co_simples.setSelected(false);
        co_composta.setSelected(false);
        co_pecent.setText("");
        cor_limite.setSelected(false);
        cor_limite_a.setText("");

        ju_mes.setSelected(false);
        ju_perc.setText("");

        ca_mu.setText("");
        ca_ju.setText("");
        ca_co.setText("");

        cm_perc.setText("");

        ep_perc.setText("");
        ep_valor.setText("");

        bol_txbanc.setSelected(false);
    }

    private void populaMultas(int tipomulta) {
        List<MultasModel> data = new ArrayList<MultasModel>();
        ResultSet rs;
        String qSQL = null;
        String campos = "id, multa_tipo, pa_mu, pa_ju, pa_co, pa_ep, mu_res, " +
                "mu_com, co_tipo, co_perc, co_limite, co_dia, ju_tipo, ju_percent," +
                "ca_multa, ca_juros, ca_correcao, co, ep_percent, ep_vrlor, bol_txbanc";

        if (tipomulta == 0) { qSQL = "SELECT " + campos + " FROM multas WHERE  (lower(multa_tipo) = 'geral') ORDER BY id;"; } else
        if (tipomulta == 1) { qSQL = "SELECT " + campos + " FROM multas WHERE  (lower(multa_tipo) = 'proprietario') ORDER BY id;"; } else
        if (tipomulta == 2) { qSQL = "SELECT " + campos + " FROM multas WHERE  (lower(multa_tipo) = 'imovel') ORDER BY id;"; }

        multas.getItems().clear();
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                int qid = -1;
                String qmulta_tipo = null, qpa_mu = null;
                String qpa_ju = null, qpa_co = null;
                String qpa_ep = null, qmu_res = null;
                String qmu_com = null, qco_tipo = null;
                String qco_perc = null; boolean qco_limite = false;
                String qco_dia = null, qju_tipo = null;
                String qju_percent = null, qca_multa = null;
                String qca_juros = null, qca_correcao = null;
                String qco = null, qep_percent = null;
                String qep_vrlor = null;
                boolean qbol_txbanc = false;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qmulta_tipo = rs.getString("multa_tipo");} catch (SQLException e) {}
                try {qpa_mu = rs.getString("pa_mu");} catch (SQLException e) {}
                try {qpa_ju = rs.getString("pa_ju");} catch (SQLException e) {}
                try {qpa_co = rs.getString("pa_co");} catch (SQLException e) {}
                try {qpa_ep = rs.getString("pa_ep");} catch (SQLException e) {}
                try {qmu_res = rs.getString("mu_res");} catch (SQLException e) {}
                try {qmu_com = rs.getString("mu_com");} catch (SQLException e) {}
                try {qco_tipo = rs.getString("co_tipo");} catch (SQLException e) {}
                try {qco_perc = rs.getString("co_perc");} catch (SQLException e) {}
                try {qco_limite = rs.getBoolean("co_limite");} catch (SQLException e) {}
                try {qco_dia = rs.getString("co_dia");} catch (SQLException e) {}
                try {qju_tipo = rs.getString("ju_tipo");} catch (SQLException e) {}
                try {qju_percent = rs.getString("ju_percent");} catch (SQLException e) {}
                try {qca_multa = rs.getString("ca_multa");} catch (SQLException e) {}
                try {qca_juros = rs.getString("ca_juros");} catch (SQLException e) {}
                try {qca_correcao = rs.getString("ca_correcao");} catch (SQLException e) {}
                try {qco = rs.getString("co");} catch (SQLException e) {}
                try {qep_percent = rs.getString("ep_percent");} catch (SQLException e) {}
                try {qep_vrlor = rs.getString("ep_vrlor");} catch (SQLException e) {}
                try {qbol_txbanc = rs.getBoolean("bol_txbanc");} catch (SQLException e) {}

                data.add(new MultasModel(qid,qmulta_tipo,qpa_mu,qpa_ju,qpa_co,qpa_ep,qmu_res,qmu_com,qco_tipo,qco_perc,
                         qco_limite,qco_dia,qju_tipo,qju_percent,qca_multa,qca_juros,qca_correcao,qco,qep_percent,qep_vrlor,qbol_txbanc));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}

        multas_id.setCellValueFactory(new PropertyValueFactory<MultasModel, Integer>("multas_id"));
        multas_id.setStyle( "-fx-alignment: CENTER;");
        multas_id.setEditable(false);

        multas_tipo.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("multas_tipo"));
        multas_tipo.setStyle( "-fx-alignment: CENTER;");
        multas_tipo.setEditable(false);

        pa_mu.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("pa_mu"));
        pa_mu.setStyle( "-fx-alignment: CENTER-RIGHT;");
        pa_mu.setEditable(false);

        pa_ju.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("pa_ju"));
        pa_ju.setStyle( "-fx-alignment: CENTER-RIGHT;");
        pa_ju.setEditable(false);

        pa_co.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("pa_co"));
        pa_co.setStyle( "-fx-alignment: CENTER-RIGHT;");
        pa_co.setEditable(false);

        pa_ep.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("pa_ep"));
        pa_ep.setStyle( "-fx-alignment: CENTER-RIGHT;");
        pa_ep.setEditable(false);

        mu_res.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("mu_res"));
        mu_res.setStyle( "-fx-alignment: CENTER-RIGHT;");
        mu_res.setEditable(false);

        mu_com.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("mu_com"));
        mu_com.setStyle( "-fx-alignment: CENTER-RIGHT;");
        mu_com.setEditable(false);

        co_tipo.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("co_tipo"));
        co_tipo.setStyle( "-fx-alignment: CENTER;");
        co_tipo.setEditable(false);

        co_perc.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("co_perc"));
        co_perc.setStyle( "-fx-alignment: CENTER-RIGHT;");
        co_perc.setEditable(false);

        co_limite.setCellValueFactory(new PropertyValueFactory<MultasModel, Boolean>("co_limite"));
        co_limite.setCellFactory(CheckBoxTableCell.forTableColumn(co_limite));
        co_limite.setStyle( "-fx-alignment: CENTER;");
        co_limite.setEditable(false);

        co_dias.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("co_dias"));
        co_dias.setStyle( "-fx-alignment: CENTER-RIGHT;");
        co_dias.setEditable(false);

        ju_tipo.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ju_tipo"));
        ju_tipo.setStyle( "-fx-alignment: CENTER;");
        ju_tipo.setEditable(false);

        ju_percent.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ju_percent"));
        ju_percent.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ju_percent.setEditable(false);

        ca_multa.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ca_multa"));
        ca_multa.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ca_multa.setEditable(false);

        ca_juros.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ca_juros"));
        ca_juros.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ca_juros.setEditable(false);

        ca_correcao.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ca_correcao"));
        ca_correcao.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ca_multa.setEditable(false);

        co.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("co"));
        co.setStyle( "-fx-alignment: CENTER-RIGHT;");
        co.setEditable(false);

        ep_percent.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ep_percent"));
        ep_percent.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ep_percent.setEditable(false);

        ep_vrlor.setCellValueFactory(new PropertyValueFactory<MultasModel, String>("ep_vrlor"));
        ep_vrlor.setStyle( "-fx-alignment: CENTER-RIGHT;");
        ep_vrlor.setEditable(false);

        if (!data.isEmpty()) multas.setItems(FXCollections.observableArrayList(data));
    }

    private void Autocomplete_p() {
        ResultSet rs = null;
        String qSQL = "SELECT p_rgprp AS codigo, p_nome AS nome FROM proprietarios WHERE (exclusao is null) ORDER BY p_rgprp;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qnome = null;
                try {qcodigo = rs.getString("codigo");} catch (SQLException e) {}
                try {qnome = rs.getString("nome");} catch (SQLException e) {}
                _possibleSuggestionsp_codigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsp_codigo, qcodigo);
                possibleSuggestionsp_codigo = new HashSet<>(Arrays.asList(_possibleSuggestionsp_codigo));

                _possibleSuggestionsp_Nome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsp_Nome, qnome);
                possibleSuggestionsp_Nome = new HashSet<>(Arrays.asList(_possibleSuggestionsp_Nome));

                _possibleSuggestions_p = FuncoesGlobais.ArraysAdd(_possibleSuggestions_p, new String[] {qcodigo, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        TextFields.bindAutoCompletion(p_codigo, possibleSuggestionsp_codigo.toArray(new String[0]));
        TextFields.bindAutoCompletion(p_nome, possibleSuggestionsp_Nome.toArray(new String[0]));

        p_codigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                p_codigo.setText(null);
                p_nome.setText(null);

                // Limpa dados do Imovel

            } else {
                // out focus
                String pcodigo = null;
                try {pcodigo = p_codigo.getText();} catch (NullPointerException e) {}
                if (pcodigo != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_p, 0, p_codigo.getText());
                    if (pos > -1 && isSearchcodigo_p) {
                        isSearchNome_p = false;
                        p_nome.setText(_possibleSuggestions_p[pos][1]);
                        isSearchNome_p = true;
                    }
                } else {
                    isSearchcodigo_p = false;
                    isSearchNome_p = true;
                }
            }
        });

        p_nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_p,1, p_nome.getText());
                String pcodigo = null;
                try {pcodigo = p_codigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome_p && pcodigo == null) {
                    isSearchcodigo_p = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions_p,1,p_nome.getText())) {
                        p_codigo.setText(_possibleSuggestions_p[pos][0]);
                    } else {
                        //WindowEnderecos(nome.getText());
                    }
                    isSearchcodigo_p = true;
                } else {
                    isSearchcodigo_p = true;
                    isSearchNome_p = false;
                }
                //DadosImovel(true);
            }
        });
    }

    private void Autocomplete_i() {
        ResultSet rs = null;
        String qSQL = "SELECT i_rgimv AS codigo, i_end || ', ' || i_num || ' ' || i_cplto AS nome FROM imoveis WHERE exclusao is null ORDER BY i_rgimv;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qnome = null;
                try {qcodigo = rs.getString("codigo");} catch (SQLException e) {}
                try {qnome = rs.getString("nome");} catch (SQLException e) {}
                _possibleSuggestionsi_codigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsi_codigo, qcodigo);
                possibleSuggestionsi_codigo = new HashSet<>(Arrays.asList(_possibleSuggestionsi_codigo));

                _possibleSuggestionsi_Nome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsi_Nome, qnome);
                possibleSuggestionsi_Nome = new HashSet<>(Arrays.asList(_possibleSuggestionsi_Nome));

                _possibleSuggestions_i = FuncoesGlobais.ArraysAdd(_possibleSuggestions_i, new String[] {qcodigo, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        TextFields.bindAutoCompletion(i_codigo, possibleSuggestionsi_codigo.toArray(new String[0]));
        TextFields.bindAutoCompletion(i_nome, possibleSuggestionsi_Nome.toArray(new String[0]));

        i_codigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                i_codigo.setText(null);
                i_nome.setText(null);
            } else {
                // out focus
                String icodigo = null;
                try {icodigo = i_codigo.getText();} catch (NullPointerException e) {}
                if (icodigo != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_i, 0, i_codigo.getText());
                    if (pos > -1 && isSearchcodigo_i) {
                        isSearchNome_i = false;
                        i_nome.setText(_possibleSuggestions_i[pos][1]);
                        isSearchNome_i = true;
                    }
                } else {
                    isSearchcodigo_i = false;
                    isSearchNome_i = true;
                }
            }
        });

        i_nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_i,1, i_nome.getText());
                String icodigo = null;
                try {icodigo = i_codigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome_i && icodigo == null) {
                    isSearchcodigo_i = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions_i,1,i_nome.getText())) {
                        i_codigo.setText(_possibleSuggestions_i[pos][0]);
                    } else {
                        //WindowEnderecos(nome.getText());
                    }
                    isSearchcodigo_i = true;
                } else {
                    isSearchcodigo_i = true;
                    isSearchNome_i = false;
                }
            }
        });
    }
}
