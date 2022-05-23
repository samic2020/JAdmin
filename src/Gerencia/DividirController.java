package Gerencia;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;

import javax.swing.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class DividirController implements Initializable {
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

    @FXML private AnchorPane anchorPane;
    @FXML private TextField p_registro;
    @FXML private TextField p_nome;

    @FXML private TableView<divItens> p_dividir;
    @FXML private TableColumn<divItens, Integer> p_dividir_id;
    @FXML private TableColumn<divItens, String> p_dividir_cod;
    @FXML private TableColumn<divItens, String> p_dividir_desc;
    @FXML private TableColumn<divItens, BigDecimal> p_dividir_perc;

    @FXML private TableView<divProp> p_imoveis;
    @FXML private TableColumn<divProp, Integer> p_imoveis_id;
    @FXML private TableColumn<divProp, String> p_imoveis_rgimv;
    @FXML private TableColumn<divProp, String> p_imoveis_end;

    @FXML private TextField s_registro;
    @FXML private TextField s_nome;

    @FXML private TableView<divProp> s_proprietario;
    @FXML private TableColumn<divProp, Integer> s_proprietario_id;
    @FXML private TableColumn<divProp, String> s_proprietario_registro;
    @FXML private TableColumn<divProp, String> s_proprietario_nome;

    @FXML private TableView<divItens> s_dividir;
    @FXML private TableColumn<divItens, Integer> s_dividir_id;
    @FXML private TableColumn<divItens, String> s_dividir_cod;
    @FXML private TableColumn<divItens, String> s_dividir_desc;
    @FXML private TableColumn<divItens, BigDecimal> s_dividir_perc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutocompleteContratoNome();
        Platform.runLater(() -> p_registro.requestFocus());
    }

    private void AutocompleteRegistroNomeBenef(String p_registro) {
        _possibleSuggestionsi_codigo = new String[]{};
        _possibleSuggestionsi_Nome = new String[]{};
        _possibleSuggestions_i = new String[][]{};
        possibleSuggestionsi_codigo = new HashSet<String>();
        possibleSuggestionsi_Nome = new HashSet<String>();
        isSearchcodigo_i = true;
        isSearchNome_i = true;

        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios WHERE (exclusao is null) and p_rgprp <> '%s' ORDER BY p_id;";
        qSQL = String.format(qSQL, p_registro);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {
                    qcontrato = imv.getString("p_rgprp");
                } catch (SQLException e) {
                }
                try {
                    qnome = imv.getString("p_nome");
                } catch (SQLException e) {
                }
                _possibleSuggestionsi_codigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsi_codigo, qcontrato);
                possibleSuggestionsi_codigo = new HashSet<>(Arrays.asList(_possibleSuggestionsi_codigo));

                _possibleSuggestionsi_Nome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsi_Nome, qnome);
                possibleSuggestionsi_Nome = new HashSet<>(Arrays.asList(_possibleSuggestionsi_Nome));

                _possibleSuggestions_i = FuncoesGlobais.ArraysAdd(_possibleSuggestions_i, new String[]{qcontrato, qnome});
            }
        } catch (SQLException e) {
        }
        try {
            DbMain.FecharTabela(imv);
        } catch (Exception e) {
        }

        TextFields.bindAutoCompletion(s_registro, possibleSuggestionsi_codigo.toArray(new String[0]));
        TextFields.bindAutoCompletion(s_nome, possibleSuggestionsi_Nome.toArray(new String[0]));

        s_registro.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                s_registro.setText("");
                s_nome.setText("");
            } else {
                // out focus
                String icodigo = null;
                try {
                    icodigo = s_registro.getText();
                } catch (NullPointerException e) {
                }
                if (icodigo != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_i, 0, s_registro.getText());
                    if (pos > -1 && isSearchcodigo_i) {
                        isSearchNome_i = false;
                        s_nome.setText(_possibleSuggestions_i[pos][1]);
                        isSearchNome_i = true;
                    }
                } else {
                    isSearchcodigo_i = false;
                    isSearchNome_i = true;
                }
            }
        });

        s_nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_i, 1, s_registro.getText());
                String icodigo = null; try { icodigo = s_registro.getText(); } catch (NullPointerException e) { }
                if (pos > -1 && isSearchNome_i && icodigo == null) {
                    isSearchcodigo_i = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions_i, 1, s_nome.getText())) {
                        s_registro.setText(_possibleSuggestions_i[pos][0]);
                    }
                    isSearchcodigo_i = true;
                } else {
                    isSearchcodigo_i = true;
                    isSearchNome_i = false;
                }

                divProp selected = p_imoveis.getSelectionModel().getSelectedItem();
                // Checar se já não foi lançado no s_proprietario
                boolean existeNaLista = false;
                for (int q=0; q<=s_proprietario.getItems().size()-1;q++) {
                    if (s_proprietario.getItems().get(q).getRegistro().equalsIgnoreCase(this.s_registro.getText())) {
                        existeNaLista = true;
                        break;
                    }
                }

                if (selected != null) {
                    if (!existeNaLista) {
                        // Lançar no s_proprietario
                        String iSql = "INSERT INTO dividir (rgprp, rgimv, rgprp_dv) VALUES ('%s','%s','%s');";
                        iSql = String.format(iSql, selected.getRegistro(), selected.getRgimv(), this.s_registro.getText());
                        try { conn.ExecutarComando(iSql); } catch (Exception e) { }
                        populateBenefs(selected.getRegistro(), selected.getRgimv());
                    } else {
                        JOptionPane.showMessageDialog(null, "Imóvel já esta na lista!");
                    }
                    p_imoveis.requestFocus();
                }
            }
        });
    }

    private void AutocompleteContratoNome() {
        ResultSet rs = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios WHERE exclusao is null ORDER BY p_rgprp::integer;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qnome = null;
                try { qcodigo = rs.getString("p_rgprp"); } catch (SQLException e) { }
                try { qnome = rs.getString("p_nome"); } catch (SQLException e) { }
                _possibleSuggestionsp_codigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsp_codigo, qcodigo);
                possibleSuggestionsp_codigo = new HashSet<>(Arrays.asList(_possibleSuggestionsp_codigo));

                _possibleSuggestionsp_Nome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsp_Nome, qnome);
                possibleSuggestionsp_Nome = new HashSet<>(Arrays.asList(_possibleSuggestionsp_Nome));

                _possibleSuggestions_p = FuncoesGlobais.ArraysAdd(_possibleSuggestions_p, new String[]{qcodigo, qnome});
            }
        } catch (SQLException e) { }
        try { DbMain.FecharTabela(rs); } catch (Exception e) { }

        TextFields.bindAutoCompletion(p_registro, possibleSuggestionsp_codigo.toArray(new String[0]));
        TextFields.bindAutoCompletion(p_nome, possibleSuggestionsp_Nome.toArray(new String[0]));

        p_registro.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                p_registro.setText("");
                p_nome.setText("");
                p_imoveis.getItems().clear();
                p_dividir.getItems().clear();
                s_proprietario.getItems().clear();
                s_dividir.getItems().clear();

                s_registro.setText(""); s_nome.setText("");
                s_registro.setDisable(true); s_nome.setDisable(true
                );
            } else {
                // out focus
                String pcodigo = null;
                try { pcodigo = p_registro.getText(); } catch (NullPointerException e) { }
                if (pcodigo != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_p, 0, p_registro.getText());
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
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions_p, 1, p_nome.getText());
                String pcodigo = null;
                try { pcodigo = p_registro.getText(); } catch (NullPointerException e) { }
                if (pos > -1 && isSearchNome_p && pcodigo.trim().equalsIgnoreCase("")) {
                    isSearchcodigo_p = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions_p, 1, p_nome.getText())) {
                        p_registro.setText(_possibleSuggestions_p[pos][0]);
                    }
                    isSearchcodigo_p = true;
                } else {
                    isSearchcodigo_p = true;
                    isSearchNome_p = false;
                }
                FillPropDivisao(p_registro.getText());
                p_imoveis.requestFocus();
            }
        });
    }

    private void FillPropDivisao(String registro) {
        List<divProp> data = new ArrayList<divProp>();
        ResultSet imv;
        String qSQL = "SELECT DISTINCT i.i_id, i.i_rgprp, i.i_rgimv, i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro AS i_ender " +
                " FROM imoveis i WHERE (i.exclusao is null) and i.i_rgprp::integer = %s ORDER BY i.i_rgimv;";
        qSQL = String.format(qSQL, registro);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                int qid = -1; String qrgprp = null, qrgimv = null, qender = null;

                try { qid = imv.getInt("i_id"); } catch (SQLException e) { }
                try { qrgprp = imv.getString("i_rgprp"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("i_rgimv"); } catch (SQLException e) { }
                try { qender = imv.getString("i_ender"); } catch (SQLException e) { }

                // Avalia conforme o tipoprop (NORMAL, ESPOLIO, DIVIDIDO)

                data.add(new divProp(qid, qrgprp, qrgimv, qender));
            }
            imv.close();
        } catch (SQLException e) {}

        p_imoveis_id.setCellValueFactory(new PropertyValueFactory<divProp, Integer>("id"));
        p_imoveis_rgimv.setCellValueFactory(new PropertyValueFactory<divProp, String>("rgimv"));
        p_imoveis_end.setCellValueFactory(new PropertyValueFactory<divProp, String>("descricao"));

        p_imoveis.setItems(FXCollections.observableArrayList(data));

        p_imoveis.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        p_imoveis.setOnMousePressed(event -> {
            divProp selectedItem = p_imoveis.getSelectionModel().getSelectedItem();
            // Mostrar os Beneficiarios que estão dividindo
            if (selectedItem != null) populateBenefs(selectedItem.getRegistro(), selectedItem.getRgimv());
        });
        p_imoveis.setOnKeyReleased(event -> {
            divProp selectedItem = p_imoveis.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.SPACE)) {
                    // Mostrar os Beneficiarios que estão dividindo
                    populateBenefs(selectedItem.getRegistro(), selectedItem.getRgimv());
                }
                if (event.getCode().equals(KeyCode.INSERT)) {
                    AutocompleteRegistroNomeBenef(selectedItem.getRegistro());
                    Platform.runLater(() -> {
                        s_registro.setDisable(false); s_registro.setText("");
                        s_nome.setDisable(false); s_nome.setText("");
                        s_registro.requestFocus();
                    });
                }
            }
        });
        p_imoveis.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                s_registro.setDisable(true); s_registro.setText("");
                s_nome.setDisable(true); s_nome.setText("");
            } else {
                // out focus
            }
        });
    }

    private void populateBenefs(String registro, String rgimv) {
        List<divProp> data = new ArrayList<divProp>();
        String qSQL = "select id, rgimv, rgprp_dv, (SELECT p_nome FROM proprietarios WHERE p_rgprp = rgprp_dv LIMIT 1) AS nomebenef, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp = '%s' AND rgimv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro, rgimv);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");
            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            while (imv.next()) {
                int qid = -1;
                String qrgprp = null, qrgimv = null, qender = null;
                String qal = null, qsg = null;
                String qiu = null, qir = null;
                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                try { qid = imv.getInt("id"); } catch (SQLException e) { }
                try { qrgprp = imv.getString("rgprp_dv"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("rgimv"); } catch (SQLException e) { }
                try { qender = imv.getString("nomebenef"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));
//
                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));

                data.add(new divProp(qid, qrgprp, qrgimv, qender));
            }
            imv.close();

            // Mostrar dados do Imóvel
            ShowDataImovels(al,tx,sg,iu,ir, mu, ju, co, ep);
        } catch (SQLException e) { }

        s_proprietario_id.setCellValueFactory(new PropertyValueFactory<divProp, Integer>("id"));
        s_proprietario_registro.setCellValueFactory(new PropertyValueFactory<divProp, String>("registro"));
        s_proprietario_nome.setCellValueFactory(new PropertyValueFactory<divProp, String>("descricao"));

        s_proprietario.setItems(FXCollections.observableArrayList(data));
        s_proprietario.setOnMousePressed(event -> {
            divProp selectedItem = s_proprietario.getSelectionModel().getSelectedItem();
            // Mostrar dados da divisão do Imóvel
            if (selectedItem != null) FillDataImovel(selectedItem.getRegistro(), selectedItem.getRgimv());
        });
        s_proprietario.setOnKeyReleased(event -> {
            divProp selectedItem = s_proprietario.getSelectionModel().getSelectedItem();
            if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.SPACE)) {
                // Mostrar dados da divisão do Imóvel
                if (selectedItem != null) FillDataImovel(selectedItem.getRegistro(), selectedItem.getRgimv());
            }
            if (event.getCode().equals(KeyCode.DELETE)) {
                // Aqui fica a deleção
                int reply = JOptionPane.showConfirmDialog(null, "Confirma exclusão desta divisão?", "Atenção",JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    String trgprp = p_registro.getText().trim();
                    String trgimv = selectedItem.getRgimv();
                    String trgprp_dv = selectedItem.getRegistro();
                    String dSql = "DELETE FROM dividir WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                    dSql = String.format(dSql,trgprp, trgimv, trgprp_dv);
                    if (conn.ExecutarComando(dSql) > 0) {
                        s_proprietario.getItems().remove(selectedItem);

                        divProp selectedItemImovel = p_imoveis.getSelectionModel().getSelectedItem();
                        if (selectedItemImovel != null) populateBenefs(selectedItemImovel.getRegistro(), selectedItemImovel.getRgimv());
                        s_proprietario.requestFocus();
                    }
                }
            }
        });
        s_dividir.getItems().clear();
    }

    private void ShowDataImovels(BigDecimal al, Object[][] tx, BigDecimal sg, BigDecimal iu, BigDecimal ir, BigDecimal mu, BigDecimal ju, BigDecimal co, BigDecimal ep) {
        List<divItens> data = new ArrayList<divItens>();

        int id = 0;
        data.add(new divItens(id++,"ALU",VariaveisGlobais.contas_ca.get("ALU"),new BigDecimal("100").subtract(al)));

        // Taxas
        ResultSet crs = conn.AbrirTabela("SELECT codigo, descricao FROM campos ORDER BY codigo::integer;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                String qcod = null, qdescr = null;
                try {qcod = crs.getString("codigo");} catch (SQLException e) {}
                try {qdescr = crs.getString("descricao");} catch (SQLException e) {}

                int pos = -1;
                try {pos = FuncoesGlobais.FindinObject(tx,0,qcod);} catch (Exception e) {}
                if (pos > -1) {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("100").subtract((BigDecimal) tx[pos][1])));
                } else {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("100")));
                }
            }
        } catch (SQLException ex) {}
        try { DbMain.FecharTabela(crs); } catch (Exception e) {}


        data.add(new divItens(id++,"SEG",VariaveisGlobais.contas_ca.get("SEG"), new BigDecimal("100").subtract(sg)));
        data.add(new divItens(id++,"IPT",VariaveisGlobais.contas_ca.get("IPT"),new BigDecimal("100").subtract(iu)));
        data.add(new divItens(id++,"IRF",VariaveisGlobais.contas_ca.get("IRF"),new BigDecimal("100").subtract(ir)));

        data.add(new divItens(id++,"MUL",VariaveisGlobais.contas_ca.get("MUL"),new BigDecimal("100").subtract(mu)));
        data.add(new divItens(id++,"JUR",VariaveisGlobais.contas_ca.get("JUR"),new BigDecimal("100").subtract(ju)));
        data.add(new divItens(id++,"COR",VariaveisGlobais.contas_ca.get("COR"),new BigDecimal("100").subtract(co)));
        data.add(new divItens(id++,"EXP",VariaveisGlobais.contas_ca.get("EXP"),new BigDecimal("100").subtract(ep)));

        p_dividir_id.setCellValueFactory(new PropertyValueFactory<divItens,Integer>("id"));
        p_dividir_cod.setCellValueFactory(new PropertyValueFactory<divItens,String>("cod"));
        p_dividir_desc.setCellValueFactory(new PropertyValueFactory<divItens,String>("descr"));
        //p_dividir_perc.setCellValueFactory(new PropertyValueFactory<divItens,BigDecimal>("perc"));
        p_dividir_perc.setCellValueFactory(p -> p.getValue().percProperty());
        p_dividir_perc.setCellFactory(p-> new TableCell<divItens, BigDecimal>() {
            @Override
            protected  void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                DecimalFormat df = new DecimalFormat("0.000");
                setText(item==null ? null : df.format(item));

                //Para colocar cor na célula
                //if (item != null) {
                //    setTextFill(item.compareTo(BigDecimal.ZERO) == 0 ? Color.BLACK : item.compareTo(BigDecimal.ZERO) == -1 ? Color.RED : Color.GREEN);
                //}
            }
        });
        p_dividir.setItems(FXCollections.observableArrayList(data));
        //p_dividir.setSelectionModel(null);
    }

    private void FillDataImovel(String registro_dv, String rgimv) {
        List<divProp> data = new ArrayList<divProp>();
        String qSQL = "select id, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp_dv = '%s' AND rgimv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro_dv, rgimv);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");
            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            while (imv.next()) {
                int qid = -1;
                String qrgprp = null, qrgimv = null, qender = null;
                String qal = null, qsg = null;
                String qiu = null, qir = null;
                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                try { qid = imv.getInt("id"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            ShowDataImovel(al,tx,sg,iu,ir, mu, ju, co, ep);
        } catch (SQLException e) { }
    }

    private void ShowDataImovel(BigDecimal al, Object[][] tx, BigDecimal sg, BigDecimal iu, BigDecimal ir, BigDecimal mu, BigDecimal ju, BigDecimal co, BigDecimal ep) {
        List<divItens> data = new ArrayList<divItens>();

        int id = 0;
        data.add(new divItens(id++,"ALU",VariaveisGlobais.contas_ca.get("ALU"),al));

        // Taxas
        ResultSet crs = conn.AbrirTabela("SELECT codigo, descricao FROM campos ORDER BY codigo::integer;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                String qcod = null, qdescr = null;
                try {qcod = crs.getString("codigo");} catch (SQLException e) {}
                try {qdescr = crs.getString("descricao");} catch (SQLException e) {}

                int pos = -1;
                try {pos = FuncoesGlobais.FindinObject(tx,0,qcod);} catch (Exception e) {}
                if (pos > -1) {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),(BigDecimal) tx[pos][1]));
                } else {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("0")));
                }
            }
        } catch (SQLException ex) {}
        try { DbMain.FecharTabela(crs); } catch (Exception e) {}


        data.add(new divItens(id++,"SEG",VariaveisGlobais.contas_ca.get("SEG"), sg));
        data.add(new divItens(id++,"IPT",VariaveisGlobais.contas_ca.get("IPT"),iu));
        data.add(new divItens(id++,"IRF",VariaveisGlobais.contas_ca.get("IRF"),ir));

        data.add(new divItens(id++,"MUL",VariaveisGlobais.contas_ca.get("MUL"),mu));
        data.add(new divItens(id++,"JUR",VariaveisGlobais.contas_ca.get("JUR"),ju));
        data.add(new divItens(id++,"COR",VariaveisGlobais.contas_ca.get("COR"),co));
        data.add(new divItens(id++,"EXP",VariaveisGlobais.contas_ca.get("EXP"),ep));

        s_dividir_id.setCellValueFactory(new PropertyValueFactory<divItens,Integer>("id"));
        s_dividir_cod.setCellValueFactory(new PropertyValueFactory<divItens,String>("cod"));
        s_dividir_desc.setCellValueFactory(new PropertyValueFactory<divItens,String>("descr"));
        //s_dividir_perc.setCellValueFactory(new PropertyValueFactory<divItens,BigDecimal>("perc"));
        s_dividir_perc.setCellValueFactory(p -> p.getValue().percProperty());
        s_dividir_perc.setCellFactory(p-> new TableCell<divItens, BigDecimal>() {
            private TextField textField;
            private BigDecimal _oldValue = new BigDecimal("0");

            @Override
            public void startEdit() {
                if (!isEmpty()) {
                    super.startEdit();

                    // Seleciona a linha na outra caixa
                    p_dividir.getSelectionModel().select(s_dividir.getSelectionModel().getSelectedIndex());

                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.requestFocus();
                    textField.selectAll();
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem().toPlainString());
                setGraphic(null);
            }

            @Override
            protected  void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        if (textField != null) {
                            textField.setText(getItem().toPlainString());
                        }
                        setText(null);
                        setGraphic(textField);
                    } else {
                        //setText(getText());
                        DecimalFormat df = new DecimalFormat("0.000");
                        setText(getItem().toPlainString()==null ? null : df.format(getItem()));
                        setGraphic(null);
                    }
                }

            }

            private void createTextField() {
                textField = new TextField(getString());
                textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

                //'MaskFieldUtil.numericField(textField);

                textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        // gotfocus
                        _oldValue = new BigDecimal(textField.getText().replace(",","."));
                    } else {
                    //if( oldValue = true && newValue == false) {
                        BigDecimal geral = p_dividir.getItems().get(s_dividir.getSelectionModel().getSelectedIndex()).getPerc();
                        BigDecimal now = new BigDecimal(textField.getText().replace(",","."));
                        BigDecimal cvalue = _oldValue.add(geral);
                        if (now.doubleValue() > cvalue.doubleValue()) {
                            JOptionPane.showMessageDialog(null, "Percentual só pode subir em até " + geral.toPlainString() + "%");
                            cancelEdit();
                        } else {
                            commitEdit();
                        }
                        s_dividir.requestFocus();
                    }
                });

                textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                    if( e.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                });

            }

            private String getString() {
                return getItem() == null ? "" : getItem().toString();
            }

            private boolean commitEdit() {
                if (!isEditing()) return true;
                super.commitEdit(new BigDecimal(textField.getText().replace(",",".")));
                return true;
            }


        });

        s_dividir.setItems(FXCollections.observableArrayList(data));
        // Aqui fica a edição da percentagem
        s_dividir_perc.setOnEditCommit((TableColumn.CellEditEvent<divItens, BigDecimal> t) -> {
                    BigDecimal xcampo = t.getNewValue();
                    String xCodigo = ((divItens) t.getTableView().getItems().get(t.getTablePosition().getRow())).getCod();
                    String xItem = ((divItens) t.getTableView().getItems().get(t.getTablePosition().getRow())).getDescr();
                    String sqlUpdate = null;

                    if (xCodigo.equalsIgnoreCase("ALU")) {
                        sqlUpdate = "UPDATE dividir SET al = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                                );
                    } else if (xCodigo.equalsIgnoreCase("SEG")) {
                        sqlUpdate = "UPDATE dividir SET sg = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("IPT")) {
                        sqlUpdate = "UPDATE dividir SET iu = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("IRF")) {
                        sqlUpdate = "UPDATE dividir SET ir = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("MUL")) {
                        sqlUpdate = "UPDATE dividir SET mu = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("JUR")) {
                        sqlUpdate = "UPDATE dividir SET ju = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("COR")) {
                        sqlUpdate = "UPDATE dividir SET co = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else if (xCodigo.equalsIgnoreCase("EXP")) {
                        sqlUpdate = "UPDATE dividir SET ep = '%s' WHERE rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    } else {
                        sqlUpdate = "UPDATE public.dividir " +
                                "SET tx = CASE WHEN (tx IS NULL) THEN " +
                                "        ARRAY['%s'] " +
                                "     ELSE " +
                                "                CASE WHEN tx::varchar ~*'%s:' THEN" +
                                "                   regexp_replace(tx::varchar,'%s:..........','%s')::varchar[] " +
                                "                ELSE" +
                                "                   array_append(tx, '%s')" +
                                "                END" +
                                "             END " +
                                "WHERE " +
                                "        rgprp = '%s' AND rgimv = '%s' AND rgprp_dv = '%s';";
                        sqlUpdate = String.format(sqlUpdate,
                                xCodigo + ":" + GravaPercent(xcampo.toPlainString()),
                                xCodigo,
                                xCodigo,
                                xCodigo + ":" + GravaPercent(xcampo.toPlainString()),
                                xCodigo + ":" + GravaPercent(xcampo.toPlainString()),
                                p_registro.getText(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRgimv(),
                                s_proprietario.getSelectionModel().getSelectedItem().getRegistro()
                        );
                    }

                    conn.ExecutarComando(sqlUpdate);

                    BigDecimal _gerperc = p_dividir.getItems().get(s_dividir.getSelectionModel().getSelectedIndex()).getPerc();
                    BigDecimal _newperc = t.getNewValue();
                    BigDecimal _oldperc = t.getOldValue();
                    p_dividir.getItems().get(s_dividir.getSelectionModel().getSelectedIndex()).setPerc(_gerperc.subtract(_newperc.subtract(_oldperc)));

                    // Atualiza Campo
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setPerc(t.getNewValue());
                }
        );

    }

    private BigDecimal LerPercent(String value, boolean temCodigo) {
        if (value == null) return new BigDecimal("0");

        String part1 = "", part2 = "", part3 = "";
        part1 = temCodigo ? value.substring(0,3) : "";
        part2 = temCodigo ? value.substring(4,9) : value.substring(0,5);
        part3 = temCodigo ? value.substring(9,14) : value.substring(5,10);

        return new BigDecimal(part2 + "." + part3);
    }

    private Object[] LerPercent(String value) {
        if (value == null) return null;

        String part1 = "", part2 = "", part3 = "";
        part1 = value.substring(0,3);
        part2 = value.substring(4,9);
        part3 = value.substring(9,14);

        return new Object[] {part1, new BigDecimal(part2 + "." + part3)};
    }

    private String GravaPercent(String value) {
        int pos = value.indexOf(".");
        if (pos == -1) value += ".0";
        String part1 = "", part2 = "";
        part1 = "00000" + value.substring(0, value.indexOf("."));
        part1 = StringManager.Right(part1,5);
        part2 = value.substring(value.indexOf(".")+1) + "00000";
        part2 = StringManager.Left(part2,5);

        return part1 + part2;
    }
}
