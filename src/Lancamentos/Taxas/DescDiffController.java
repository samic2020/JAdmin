package Lancamentos.Taxas;

import Classes.Taxas;
import Funcoes.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static Funcoes.FuncoesGlobais.StrZero;
import static javafx.collections.FXCollections.observableList;

public class DescDiffController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    int _rgprp = -1, _rgimv = -1, _id = -1;
    String _contrato = null; String _matriculas = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField contrato;
    @FXML private Label nomeLocatario;
    @FXML private Label rgimv;
    @FXML private Label endereco;
    @FXML private Label numero;
    @FXML private Label complto;
    @FXML private Label bairro;
    @FXML private Label cidade;
    @FXML private Label estado;
    @FXML private Label cep;

    @FXML private RadioButton desconto_al;
    @FXML private ToggleGroup al_descdif;
    @FXML private RadioButton diferenca_al;

    @FXML private TextField refer_al;
    @FXML private Spinner<Integer> qtd_al;
    @FXML private TextField descr_al;
    @FXML private TextField valor_al;
    @FXML private TableView<AluguelModel> lista_al;
    @FXML private TableColumn<AluguelModel, String> al_lista_id;
    @FXML private TableColumn<AluguelModel, String> al_lista_tipo;
    @FXML private TableColumn<AluguelModel, String> al_lista_descr;
    @FXML private TableColumn<AluguelModel, String> al_lista_cota;
    @FXML private TableColumn<AluguelModel, String> al_lista_refer;
    @FXML private TableColumn<AluguelModel, BigDecimal> al_lista_valor;

    @FXML private TextField cota_al;
    @FXML private Button btLancar_al;

    @FXML private RadioButton desconto_tx;
    @FXML private ToggleGroup tx_descdif;
    @FXML private RadioButton diferenca_tx;

    @FXML private ComboBox<Taxas> codigo_tx;
    @FXML private TextField PreTexto_tx;
    @FXML private TextField Descricao_tx;
    @FXML private TextField PosTexto_tx;
    @FXML private TextField CotaParcela_tx;
    @FXML private TextField Referencia_tx;
    @FXML private CheckBox Retencao_tx;
    @FXML private CheckBox Extrato_tx;
    @FXML private TextField Valor_tx;
    @FXML private DatePicker Vencimento_tx;
    @FXML private Spinner<Integer> qtd_tx;
    @FXML private TableView<TaxasModel> lista_tx;
    @FXML private TableColumn<TaxasModel, Integer> tx_lista_id;
    @FXML private TableColumn<TaxasModel, String> tx_lista_tipo;
    @FXML private TableColumn<TaxasModel, String> tx_lista_mat;
    @FXML private TableColumn<TaxasModel, String> tx_lista_predesc;
    @FXML private TableColumn<TaxasModel, String> tx_lista_desc;
    @FXML private TableColumn<TaxasModel, String> tx_lista_posdesc;
    @FXML private TableColumn<TaxasModel, String> tx_lista_ref;
    @FXML private TableColumn<TaxasModel, String> tx_lista_cota;
    @FXML private TableColumn<TaxasModel, String> tx_lista_vcto;
    @FXML private TableColumn<TaxasModel, BigDecimal> tx_lista_valor;
    @FXML private Button btLancar_tx;

    @FXML private TextField sg_apolice;
    @FXML private DatePicker sg_vencimento;
    @FXML private TextField sg_referencia;
    @FXML private Spinner<Integer> sg_qtde;
    @FXML private TextField sg_cotaparc;
    @FXML private CheckBox sg_extrato;
    @FXML private TextField sg_valor;
    @FXML private Button btLancar_sg;
    @FXML private TableView<SegurosModel> lista_sg;
    @FXML private TableColumn<SegurosModel, Integer> lista_sg_id;
    @FXML private TableColumn<SegurosModel, String> lista_sg_apolice;
    @FXML private TableColumn<SegurosModel, String> lista_sg_vecto;
    @FXML private TableColumn<SegurosModel, String> lista_sg_refer;
    @FXML private TableColumn<SegurosModel, String> lista_sg_cotaparc;
    @FXML private TableColumn<SegurosModel, Boolean> lista_sg_extrato;
    @FXML private TableColumn<SegurosModel, BigDecimal> lista_sg_valor;

    @FXML private Tab aluguelTab;
    @FXML private Tab taxasTab;
    @FXML private Tab segurosTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DescDifTaxasInitialize();
        DescDifAluguelInitializa();
        SegurosInitialize();
        Initial();

        Platform.runLater(() -> {
            aluguelTab.setDisable(true);
            taxasTab.setDisable(true);
            segurosTab.setDisable(true);
            contrato.setText("");
            contrato.requestFocus();
        });
    }

    private void Initial() {
        MaskFieldUtil.numericField(contrato);
        MaskFieldUtil.maxField(contrato,6);

        contrato.focusedProperty().addListener((arg0, oldvalue, newvalue) -> {
            if (newvalue) {
                nomeLocatario.setText("");
                rgimv.setText("");
                endereco.setText("");
                numero.setText("");
                complto.setText("");
                bairro.setText("");
                cidade.setText("");
                estado.setText("");
                cep.setText("");
                clearAlugueres();
                clearTaxas();
                clearSeguros();

                aluguelTab.setDisable(true);
                taxasTab.setDisable(true);
                segurosTab.setDisable(true);
                contrato.selectAll();
                contrato.requestFocus();
            } else {
                if (!contrato.getText().equalsIgnoreCase("")) {
                    BuscaLocat(contrato.getText().trim());
                    if (this._contrato == null) {
                        Alert msg = new Alert(Alert.AlertType.WARNING);
                        msg.setTitle("Atenção!");
                        msg.setHeaderText("Contrato inexistente ou Já baixado!");
                        msg.setContentText("Entre com um contrato ativo.");
                        msg.showAndWait();
                        contrato.requestFocus();
                    }
                    BuscaImovelLoca();
                    populateAlugueres(this._contrato);
                    populateTaxas(this._contrato);
                    populateSeguros(this._contrato);
                }
            }
        });
    }

    private void BuscaLocat(String ctro) {
        nomeLocatario.setText("");
        this._rgprp = -1;
        this._rgimv = -1;
        this._contrato = null;

        String sql = "SELECT l_rgprp, l_rgimv, l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_fantasia END AS nome FROM locatarios WHERE l_contrato = '%s';";
        sql = String.format(sql, ctro);
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                this._rgprp = rs.getInt("l_rgprp");
                this._rgimv = rs.getInt("l_rgimv");
                this._contrato = rs.getString("l_contrato");
                nomeLocatario.setText(StringManager.ConvStr(rs.getString("nome")));
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
    }

    private void BuscaImovelLoca() {
        rgimv.setText("");
        endereco.setText("");
        numero.setText("");
        complto.setText("");
        bairro.setText("");
        cidade.setText("");
        estado.setText("");
        cep.setText("");
        this._matriculas = null;

        String sql = "SELECT i_situacao, i_end, i_num, i_cplto, i_bairro, i_cidade, i_estado, i_cep, i_matriculas FROM " +
                     "imoveis WHERE i_rgprp = '%s' AND i_rgimv = '%s';";
        sql = String.format(sql, this._rgprp, this._rgimv);
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                rgimv.setText(String.valueOf(this._rgimv));
                endereco.setText(rs.getString("i_end"));
                numero.setText(rs.getString("i_num"));
                complto.setText(rs.getString("i_cplto"));
                bairro.setText(rs.getString("i_bairro"));
                cidade.setText(rs.getString("i_cidade"));
                estado.setText(rs.getString("i_estado"));
                cep.setText(rs.getString("i_cep"));
                this._matriculas = rs.getString("i_matriculas");

                aluguelTab.setDisable(false);
                taxasTab.setDisable(false);
                segurosTab.setDisable(false);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
    }

    private void populateAlugueres(String ctro) {
        List<AluguelModel> data = new ArrayList<AluguelModel>();
        ResultSet imv;
        String qSQL = "SELECT id, tipo, descricao, cota, referencia, valor FROM descdif WHERE contrato = '%s' AND dtrecebimento IS NULL;";
        qSQL = String.format(qSQL, ctro);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qtipo = null, qdesc = null, qcota = null, qrefer = null;
                BigDecimal qvalor = null; int qid = -1;

                try {qid = imv.getInt("id");} catch (SQLException e) {}
                try {qtipo = imv.getString("tipo");} catch (SQLException e) {}
                try {qdesc = imv.getString("descricao");} catch (SQLException e) {}
                try {qcota = imv.getString("cota");} catch (SQLException e) {}
                try {qrefer = imv.getString("referencia");} catch (SQLException e) {}
                try {qvalor = imv.getBigDecimal("valor");} catch (SQLException e) {}

                data.add(new AluguelModel(qid, qtipo.equalsIgnoreCase("D") ? "DES" : "DIF", qdesc, qcota, qrefer, qvalor));
            }
            imv.close();
        } catch (SQLException e) {}

        al_lista_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        al_lista_tipo.setCellValueFactory(new PropertyValueFactory<>("ptipo"));
        al_lista_descr.setCellValueFactory(new PropertyValueFactory<>("pdesc"));
        al_lista_cota.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        al_lista_refer.setCellValueFactory(new PropertyValueFactory<>("prefer"));
        al_lista_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_al.setItems(FXCollections.observableArrayList(data));
        //TableFilter<AluguelModel> tableFilter = new TableFilter<>(lista_al);
    }

    private void populateTaxas(String ctro) {
        List<TaxasModel> data = new ArrayList<TaxasModel>();
        ResultSet imv;
        String qSQL = "SELECT id, tipo, matricula, precampo, campo, poscampo, referencia, cota, dtvencimento, valor FROM taxas WHERE contrato = '%s' AND dtrecebimento IS NULL;";
        qSQL = String.format(qSQL, ctro);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qtipo = null, qdesc = null, qmat = null, qvcto = null;
                String qref = null, qcota = null, qpredesc = null, qposdesc = null;
                String qcampo= null; BigDecimal qvalor = null; int qid = -1;

                try {qid = imv.getInt("id");} catch (SQLException e) {}
                try {qtipo = imv.getString("tipo");} catch (SQLException e) {}

                try {qcampo = imv.getString("campo");} catch (SQLException e) {}
                try {qdesc = this._matriculas.substring(this._matriculas.indexOf(qcampo + ","));} catch (Exception e) {}
                if (qdesc != null) {
                    if (qdesc.indexOf(";") != -1) {
                        qdesc = qdesc.substring(0, qdesc.indexOf(";") - 1);
                    }
                    qmat = qdesc.substring(qdesc.indexOf(",") + 1);
                    qdesc = qdesc.substring(0, qdesc.indexOf(","));
                    int apos = FuncoesGlobais.ClassIndexOf(codigo_tx.getItems().toArray(), qdesc);
                    qdesc = codigo_tx.getItems().get(apos).getDescricao();
                } else {
                    qmat = "";
                    int apos = FuncoesGlobais.ClassIndexOf(codigo_tx.getItems().toArray(), imv.getString("campo"));
                    qdesc = codigo_tx.getItems().get(apos).getDescricao();
                }

                try {qpredesc = imv.getString("precampo");} catch (SQLException e) {qpredesc = "";}
                try {qposdesc = imv.getString("poscampo");} catch (SQLException e) {qposdesc = "";}

                try {qref = imv.getString("referencia");} catch (SQLException e) {}
                try {qcota = imv.getString("cota");} catch (SQLException e) {}
                try {qvcto = imv.getString("dtvencimento");} catch (SQLException e) {}
                qvcto = Dates.StringtoString(qvcto,"yyyy-MM-dd","dd-MM-yyyy");
                try {qvalor = imv.getBigDecimal("valor");} catch (SQLException e) {}

                data.add(new TaxasModel(qid, qtipo.equalsIgnoreCase("D") ? "DEB" : "CRE", qmat,qpredesc,qdesc,qposdesc,qref,qcota,qvcto,qvalor));
            }
            imv.close();
        } catch (SQLException e) {}

        tx_lista_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        tx_lista_tipo.setCellValueFactory(new PropertyValueFactory<>("ptipo"));
        tx_lista_mat.setCellValueFactory(new PropertyValueFactory<>("pmat"));
        tx_lista_predesc.setCellValueFactory(new PropertyValueFactory<>("ppdesc"));
        tx_lista_desc.setCellValueFactory(new PropertyValueFactory<>("pdesc"));
        tx_lista_posdesc.setCellValueFactory(new PropertyValueFactory<>("pposdesc"));
        tx_lista_ref.setCellValueFactory(new PropertyValueFactory<>("pref"));
        tx_lista_cota.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        tx_lista_vcto.setCellValueFactory(new PropertyValueFactory<>("pvenc"));
        tx_lista_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_tx.setItems(FXCollections.observableArrayList(data));
        //TableFilter<TaxasModel> tableFilter = new TableFilter<>(lista_tx);
    }

    private void populateSeguros(String ctro) {
        List<SegurosModel> data = new ArrayList<SegurosModel>();
        ResultSet imv;
        String qSQL = "SELECT id, cota, valor, dtvencimento, referencia, extrato, apolice " +
                "  FROM seguros WHERE contrato = '%s' AND dtrecebimento IS NULL;";
        qSQL = String.format(qSQL, ctro);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qapolice = null, qvcto = null;
                String qref = null, qcota = null;
                boolean qext = false;
                BigDecimal qvalor = null; int qid = -1;

                try {qid = imv.getInt("id");} catch (SQLException e) {}
                try {qapolice = imv.getString("apolice");} catch (SQLException e) {}
                try {qvcto = imv.getString("dtvencimento");} catch (SQLException e) {}
                qvcto = Dates.StringtoString(qvcto,"yyyy-MM-dd","dd-MM-yyyy");
                try {qref = imv.getString("referencia");} catch (SQLException e) {}
                try {qcota = imv.getString("cota");} catch (SQLException e) {}
                try {qext = imv.getBoolean("extrato");} catch (SQLException e) {}
                try {qvalor = imv.getBigDecimal("valor");} catch (SQLException e) {}

                data.add(new SegurosModel(qid, qapolice,qvcto,qref,qcota,qext,qvalor));
            }
            imv.close();
        } catch (SQLException e) {}

        lista_sg_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        lista_sg_apolice.setCellValueFactory(new PropertyValueFactory<>("papolice"));
        lista_sg_vecto.setCellValueFactory(new PropertyValueFactory<>("pvencto"));
        lista_sg_refer.setCellValueFactory(new PropertyValueFactory<>("pref"));
        lista_sg_cotaparc.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        lista_sg_extrato.setCellValueFactory(new PropertyValueFactory<>("pextrato"));
        lista_sg_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_sg.setItems(FXCollections.observableArrayList(data));
        //TableFilter<TaxasModel> tableFilter = new TableFilter<>(lista_tx);
    }

    private void clearAlugueres() {
        List<AluguelModel> data = new ArrayList<AluguelModel>();

        al_lista_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        al_lista_tipo.setCellValueFactory(new PropertyValueFactory<>("ptipo"));
        al_lista_descr.setCellValueFactory(new PropertyValueFactory<>("pdesc"));
        al_lista_cota.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        al_lista_refer.setCellValueFactory(new PropertyValueFactory<>("prefer"));
        al_lista_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_al.setItems(FXCollections.observableArrayList(data));
        //TableFilter<AluguelModel> tableFilter = new TableFilter<>(lista_al);
    }

    private void clearTaxas() {
        List<TaxasModel> data = new ArrayList<>();

        tx_lista_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        tx_lista_tipo.setCellValueFactory(new PropertyValueFactory<>("ptipo"));
        tx_lista_mat.setCellValueFactory(new PropertyValueFactory<>("pmat"));
        tx_lista_predesc.setCellValueFactory(new PropertyValueFactory<>("ppdesc"));
        tx_lista_desc.setCellValueFactory(new PropertyValueFactory<>("pdesc"));
        tx_lista_posdesc.setCellValueFactory(new PropertyValueFactory<>("pposdesc"));
        tx_lista_ref.setCellValueFactory(new PropertyValueFactory<>("pref"));
        tx_lista_cota.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        tx_lista_vcto.setCellValueFactory(new PropertyValueFactory<>("pvenc"));
        tx_lista_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_tx.setItems(FXCollections.observableArrayList(data));
        //TableFilter<AluguelModel> tableFilter = new TableFilter<>(lista_al);
    }

    private void clearSeguros() {
        List<SegurosModel> data = new ArrayList<>();

        lista_sg_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        lista_sg_apolice.setCellValueFactory(new PropertyValueFactory<>("papolice"));
        lista_sg_vecto.setCellValueFactory(new PropertyValueFactory<>("pvencto"));
        lista_sg_refer.setCellValueFactory(new PropertyValueFactory<>("pref"));
        lista_sg_cotaparc.setCellValueFactory(new PropertyValueFactory<>("pcota"));
        lista_sg_extrato.setCellValueFactory(new PropertyValueFactory<>("pextrato"));
        lista_sg_valor.setCellValueFactory(new PropertyValueFactory<>("pvalor"));

        lista_sg.setItems(FXCollections.observableArrayList(data));
        //TableFilter<AluguelModel> tableFilter = new TableFilter<>(lista_al);
    }

    private void SegurosInitialize() {
        MaskFieldUtil.maxField(sg_apolice, 30);
        MaskFieldUtil.dateRefField(sg_referencia);
        MaskFieldUtil.dateRefField(sg_cotaparc);
        MaskFieldUtil.monetaryField(sg_valor);

        {
            sg_qtde.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, Integer.parseInt("01")));
            EventHandler<KeyEvent> enterKeyEventHandler;

            enterKeyEventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            Integer.parseInt(sg_qtde.getEditor().textProperty().get());
                        } catch (NumberFormatException e) {
                            sg_qtde.getEditor().textProperty().set("01");
                        }
                    }
                }
            };

            sg_qtde.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);
            sg_qtde.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (newPropertyValue) {
                    // on focus
                } else {
                    // out focus
                    sg_cotaparc.setText("01/" + StrZero(sg_qtde.getValue().toString(), 2));
                }
            });
        }

        btLancar_sg.disableProperty().bind(sg_vencimento.promptTextProperty().isEmpty().or(sg_vencimento.promptTextProperty().lessThan(Dates.DateFormata("dd-MM-yyyy",DbMain.getDateTimeServer()))).and(sg_valor.textProperty().isEmpty().or(sg_valor.textProperty().isEqualToIgnoreCase("0,00"))));
        btLancar_sg.setOnAction(e -> {
            String tref = sg_referencia.getText().trim();
            if (tref.trim().length() < 7 || tref.trim().length() > 7) {
                new Alert(Alert.AlertType.ERROR,"Você de colocar (MM/AAAA) para a referência!").showAndWait();
                sg_referencia.requestFocus();
                return;
            }
            int rmes = 0, rano = 0;
            rmes = Integer.parseInt(tref.substring(0,2).toString());
            rano = Integer.parseInt(tref.substring(3,7).toString());
            if (rmes == 0 || rano == 0) {
                new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                sg_referencia.requestFocus();
                return;
            }
            if (rmes < 1 || rmes > 12) {
                new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                sg_referencia.requestFocus();
                return;
            }
            int nqtde = sg_qtde.getValue();
            String tcota = sg_cotaparc.getText().trim();
            int mes = 0; int ano = 0;
            boolean isCota = false;
            if (tcota.trim().length() < 5) {
                new Alert(Alert.AlertType.ERROR,"Você de colocar Cota (MM/AAAA) ou parcela (NN/TT)!").showAndWait();
                sg_cotaparc.requestFocus();
                return;
            } else if (tcota.trim().length() == 5) {
                // Parcelas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,5).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    sg_cotaparc.requestFocus();
                    return;
                } else if (mes > ano) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    sg_cotaparc.requestFocus();
                    return;
                }
                isCota = false;
            } else {
                // Cotas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,7).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    sg_cotaparc.requestFocus();
                    return;
                }
                if (mes < 1 || mes > 12) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    sg_cotaparc.requestFocus();
                    return;
                }
                isCota = true;
            }

            for (int i=0;i<nqtde;i++) {
                String iSql = "INSERT INTO seguros(" +
                        "            rgprp, rgimv, contrato, cota, valor, dtvencimento, referencia, " +
                        "            extrato, apolice, dtlanc, usr_lanc)" +
                        "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                try {
                    PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                    int nid = 1;
                    pstmt.setInt(nid++, _rgprp);
                    pstmt.setInt(nid++, _rgimv);
                    pstmt.setString(nid++, _contrato);

                    String tmpCotaParc = ""; String tmpRef = "";
                    if (isCota) {
                        if (mes > 12) { mes = 1; ano += 1; }
                        if (rmes > 12) { rmes = 1; rano += 1; }
                        tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),4);
                        tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                    } else {
                        if (rmes > 12) { rmes = 1; rano += 1; }
                        tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                        tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),2);
                    }
                    pstmt.setString(nid++, tmpCotaParc);

                    pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(sg_valor.getText())));
                    pstmt.setDate(nid++, Dates.toSqlDate(sg_vencimento));
                    pstmt.setString(nid++, tmpRef);
                    pstmt.setBoolean(nid++, sg_extrato.isSelected());
                    pstmt.setString(nid++, sg_apolice.getText().trim());

                    pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    pstmt.setString(nid++, VariaveisGlobais.usuario);

                    pstmt.executeUpdate();
                } catch (SQLException ex) {}

                contrato.requestFocus();
            }
        });
        lista_sg.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String sql = "DELETE FROM seguros WHERE id = '%s';";
                    sql = String.format(sql, lista_sg.getSelectionModel().getSelectedItem().getPid());
                    try { conn.ExecutarComando(sql); } catch (Exception e) {}
                    contrato.requestFocus();
                }

            }
        });

    }

    private void DescDifTaxasInitialize() {
        MaskFieldUtil.maxField(PreTexto_tx,10);
        MaskFieldUtil.maxField(PosTexto_tx,10);
        MaskFieldUtil.dateRefField(CotaParcela_tx);
        MaskFieldUtil.dateRefField(Referencia_tx);
        MaskFieldUtil.monetaryField(Valor_tx);

        {
            btLancar_tx.disableProperty().bind(Vencimento_tx.promptTextProperty().isEmpty().or(Vencimento_tx.promptTextProperty().lessThan(Dates.DateFormata("dd-MM-yyyy",DbMain.getDateTimeServer()))).and(Valor_tx.textProperty().isEmpty().or(Valor_tx.textProperty().isEqualToIgnoreCase("0,00"))));
            btLancar_tx.setOnAction(e -> {
                String tref = Referencia_tx.getText().trim();
                if (tref.trim().length() < 7 || tref.trim().length() > 7) {
                    new Alert(Alert.AlertType.ERROR,"Você de colocar (MM/AAAA) para a referência!").showAndWait();
                    Referencia_tx.requestFocus();
                    return;
                }
                int rmes = 0, rano = 0;
                rmes = Integer.parseInt(tref.substring(0,2).toString());
                rano = Integer.parseInt(tref.substring(3,7).toString());
                if (rmes == 0 || rano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    Referencia_tx.requestFocus();
                    return;
                }
                if (rmes < 1 || rmes > 12) {
                    new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    Referencia_tx.requestFocus();
                    return;
                }
                int nqtde = qtd_tx.getValue();
                String tcota = CotaParcela_tx.getText().trim();
                int mes = 0; int ano = 0;
                boolean isCota = false;
                if (tcota.trim().length() < 5) {
                    new Alert(Alert.AlertType.ERROR,"Você de colocar Cota (MM/AAAA) ou parcela (NN/TT)!").showAndWait();
                    CotaParcela_tx.requestFocus();
                    return;
                } else if (tcota.trim().length() == 5) {
                    // Parcelas
                    mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                    ano = Integer.parseInt(tcota.substring(3,5).toString());
                    if (mes == 0 || ano == 0) {
                        new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                        CotaParcela_tx.requestFocus();
                        return;
                    } else if (mes > ano) {
                        new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                        CotaParcela_tx.requestFocus();
                        return;
                    }
                    isCota = false;
                } else {
                    // Cotas
                    mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                    ano = Integer.parseInt(tcota.substring(3,7).toString());
                    if (mes == 0 || ano == 0) {
                        new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                        CotaParcela_tx.requestFocus();
                        return;
                    }
                    if (mes < 1 || mes > 12) {
                        new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                        CotaParcela_tx.requestFocus();
                        return;
                    }
                    isCota = true;
                }

                for (int i=0;i<nqtde;i++) {
                    String iSql = "INSERT INTO taxas(" +
                            "            rgprp, rgimv, contrato, precampo, campo, poscampo, cota, " +
                            "            valor, dtvencimento, referencia, retencao, extrato, tipo, " +
                            "            matricula, dtlanc, usr_lanc)" +
                            "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    try {
                        PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                        int nid = 1;
                        pstmt.setInt(nid++, _rgprp);
                        pstmt.setInt(nid++, _rgimv);
                        pstmt.setString(nid++, _contrato);
                        pstmt.setString(nid++, PreTexto_tx.getText().trim());
                        pstmt.setString(nid++, codigo_tx.getSelectionModel().getSelectedItem().toString());
                        pstmt.setString(nid++, PosTexto_tx.getText().trim());

                        String tmpCotaParc = ""; String tmpRef = "";
                        if (isCota) {
                            if (mes > 12) { mes = 1; ano += 1; }
                            if (rmes > 12) { rmes = 1; rano += 1; }
                            tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),4);
                            tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                        } else {
                            if (rmes > 12) { rmes = 1; rano += 1; }
                            tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                            tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),2);
                        }
                        pstmt.setString(nid++, tmpCotaParc);

                        pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(Valor_tx.getText())));
                        //Date newVencto = Dates.DateAdd( Dates.DIA, 30 * i, Dates.toDate(Vencimento_tx));
                        //pstmt.setDate(nid++, Dates.toSqlDate(Dates.toDatePicker(newVencto)));
                        pstmt.setDate(nid++, Dates.toSqlDate(Vencimento_tx));
                        pstmt.setString(nid++, tmpRef);
                        pstmt.setBoolean(nid++, Retencao_tx.isSelected());
                        pstmt.setBoolean(nid++, Extrato_tx.isSelected());
                        pstmt.setString(nid++, desconto_tx.isSelected() ? "D" : "C");

                        String qmat = null;
                        String qcodigo = codigo_tx.getSelectionModel().getSelectedItem().toString();
                        try {qmat = this._matriculas.substring(this._matriculas.indexOf(qcodigo + ","));} catch (Exception ex) {}
                        if (qmat != null) {
                            if (qmat.indexOf(";") != -1) {
                                qmat = qmat.substring(0, qmat.indexOf(";") - 1);
                            }
                            qmat = qmat.substring(qmat.indexOf(",") + 1);
                        }
                        pstmt.setString(nid++, qmat);

                        pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                        pstmt.setString(nid++, VariaveisGlobais.usuario);

                        pstmt.executeUpdate();
                    } catch (SQLException ex) {}
                    contrato.requestFocus();
                }
            });

            lista_tx.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.DELETE)) {
                    Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                    Optional<ButtonType> result = msg.showAndWait();
                    if (result.get().getText().equals("Sim")) {
                        String sql = "DELETE FROM taxas WHERE id = '%s';";
                        sql = String.format(sql, lista_tx.getSelectionModel().getSelectedItem().getPid());
                        try { conn.ExecutarComando(sql); } catch (Exception e) {}
                        contrato.requestFocus();
                    }

                }
            });

        }

        {
            String tSql = "SELECT id, codigo, descricao, predesc, posdesc, retencao, extrato FROM campos ORDER BY codigo;";
            ResultSet rs = conn.AbrirTabela(tSql,ResultSet.CONCUR_READ_ONLY);
            List<Taxas> ttaxas = new ArrayList<>();
            try {
                while (rs.next()) {
                    ttaxas.add(
                            new Taxas(
                                    rs.getInt("id"),
                                    rs.getString("codigo"),
                                    rs.getString("descricao"),
                                    rs.getBoolean("predesc"),
                                    rs.getBoolean("posdesc"),
                                    rs.getBoolean("retencao"),
                                    rs.getBoolean("extrato")
                            )
                    );
                }
            } catch (SQLException e) {}
            try {rs.close();} catch (SQLException e) {}
            ObservableList<Taxas> observableList = observableList(ttaxas);
            codigo_tx.setItems(observableList);

            Callback cb = new Callback<ListView<Taxas>,ListCell<Taxas>>(){
                @Override
                public ListCell<Taxas> call(ListView<Taxas> l){
                    return new ListCell<Taxas>(){

                        private final HBox hbx;
                        private final Label codigo;
                        private final Label sep;
                        private final Label nome;

                        {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            codigo = new Label(); codigo.setTextFill(Color.DARKBLUE);
                            codigo.setPrefWidth(30); codigo.setPrefHeight(25); codigo.setAlignment(Pos.CENTER_RIGHT);

                            sep = new Label(" - ");
                            sep.setPrefWidth(20); sep.setPrefHeight(25); sep.setAlignment(Pos.CENTER);

                            nome = new Label(); nome.setTextFill(Color.DARKGREEN);
                            nome.setPrefWidth(180); nome.setPrefHeight(25); nome.setAlignment(Pos.CENTER_LEFT);

                            hbx = new HBox(codigo, sep, nome);
                        }

                        @Override
                        protected void updateItem(Taxas item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                //setText(item.getNumero() + " - "+item.getNome());
                                codigo.setText(item.getCodigo());
                                nome.setText(item.getDescricao());
                                setGraphic(hbx);
                            }
                        }
                    } ;
                }
            };
            codigo_tx.setCellFactory(cb);

            codigo_tx.setOnAction(e -> {
                PreTexto_tx.setDisable(false);
                Descricao_tx.setText(codigo_tx.getSelectionModel().getSelectedItem().getDescricao());
                PosTexto_tx.setDisable(false);
                CotaParcela_tx.setText("");
                Referencia_tx.setText("");
                Retencao_tx.setSelected(desconto_tx.isSelected() ? false : codigo_tx.getSelectionModel().getSelectedItem().getRetencao());
                Extrato_tx.setSelected(desconto_tx.isSelected() ? false : codigo_tx.getSelectionModel().getSelectedItem().getExtrato());
                Vencimento_tx.setValue(null);
                Valor_tx.setText("0,00");
            });
        }

        {
            qtd_tx.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12,Integer.parseInt("01")));
            EventHandler<KeyEvent> enterKeyEventHandler;

            enterKeyEventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            Integer.parseInt(qtd_tx.getEditor().textProperty().get());
                        }
                        catch (NumberFormatException e) {
                            qtd_tx.getEditor().textProperty().set("01");
                        }
                    }
                }
            };

            qtd_tx.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);
        }

        qtd_tx.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                CotaParcela_tx.setText("01/" + StrZero(qtd_tx.getValue().toString(),2));
            }
        });

        tx_descdif.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                //System.out.println("Selected Radio Button - "+chk.getText());
                if (chk.getText().equalsIgnoreCase("desconto")) {
                    Retencao_tx.setSelected(false);
                    Retencao_tx.setDisable(true);

                    Extrato_tx.setSelected(false);
                    Extrato_tx.setDisable(true);
                } else {
                    Retencao_tx.setDisable(false);
                    Extrato_tx.setDisable(false);
                }
            }
        });

    }

    private void DescDifAluguelInitializa() {
        MaskFieldUtil.dateRefField(cota_al);
        MaskFieldUtil.dateRefField(refer_al);
        MaskFieldUtil.monetaryField(valor_al);
        MaskFieldUtil.maxField(descr_al, 20);

        {
            qtd_al.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12,Integer.parseInt("01")));
            EventHandler<KeyEvent> enterKeyEventHandler;

            enterKeyEventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            Integer.parseInt(qtd_al.getEditor().textProperty().get());
                        }
                        catch (NumberFormatException e) {
                            qtd_al.getEditor().textProperty().set("01");
                        }
                    }
                }
            };

            qtd_al.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);

            qtd_al.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (newPropertyValue) {
                    // on focus
                } else {
                    // out focus
                    cota_al.setText("01/" + StrZero(qtd_al.getValue().toString(),2));
                }
            });
        }

        btLancar_al.setOnAction(evt -> {
            String tref = refer_al.getText().trim();
            if (tref.trim().length() < 7 || tref.trim().length() > 7) {
                new Alert(Alert.AlertType.ERROR,"Você de colocar (MM/AAAA) para a referência!").showAndWait();
                refer_al.requestFocus();
                return;
            }
            int rmes = 0, rano = 0;
            rmes = Integer.parseInt(tref.substring(0,2).toString());
            rano = Integer.parseInt(tref.substring(3,7).toString());
            if (rmes == 0 || rano == 0) {
                new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                refer_al.requestFocus();
                return;
            }
            if (rmes < 1 || rmes > 12) {
                new Alert(Alert.AlertType.ERROR,"Referência (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                refer_al.requestFocus();
                return;
            }
            int qtde = qtd_al.getValue();
            int mes = 0; int ano = 0;
            String tcota = cota_al.getText();
            boolean isCota = false;
            if (tcota.trim().length() < 5) {
                new Alert(Alert.AlertType.ERROR,"Você de colocar Cota (MM/AAAA) ou parcela (NN/TT)!").showAndWait();
                cota_al.requestFocus();
                return;
            } else if (tcota.trim().length() == 5) {
                // Parcelas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,5).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    cota_al.requestFocus();
                    return;
                } else if (mes > ano) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    cota_al.requestFocus();
                    return;
                }
                isCota = false;
            } else {
                // Cotas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,7).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    cota_al.requestFocus();
                    return;
                }
                if (mes < 1 || mes > 12) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    cota_al.requestFocus();
                    return;
                }
                isCota = true;
            }

            for (int i=0;i<qtde;i++) {
                String sql = "INSERT INTO descdif(tipo, rgprp, rgimv, contrato, descricao, cota, referencia, valor, dtlanc, usr_lanc) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                try {
                    PreparedStatement pstmt = conn.conn.prepareStatement(sql);
                    int nid = 1;
                    pstmt.setString(nid++, desconto_al.isSelected() ? "D" : "C");
                    pstmt.setString(nid++, String.valueOf(this._rgprp));
                    pstmt.setString(nid++, String.valueOf(this._rgimv));
                    pstmt.setString(nid++, this._contrato);
                    pstmt.setString(nid++, descr_al.getText());

                    String tmpCotaParc = ""; String tmpRef = "";
                    if (isCota) {
                        if (mes > 12) { mes = 1; ano += 1; }
                        if (rmes > 12) { rmes = 1; rano += 1; }
                        tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),4);
                        tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                    } else {
                        if (rmes > 12) { rmes = 1; rano += 1; }
                        tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),2);
                        tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                    }
                    pstmt.setString(nid++, tcota.trim().length() <= 3 ? "" : tmpCotaParc);

                    pstmt.setString(nid++, tmpRef);
                    pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(valor_al.getText())));
                    pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    pstmt.setString(nid++, VariaveisGlobais.usuario);

                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            contrato.requestFocus();
        });

        lista_al.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String sql = "DELETE FROM descdif WHERE id = '%s';";
                    sql = String.format(sql, lista_al.getSelectionModel().getSelectedItem().getPid());
                    try { conn.ExecutarComando(sql); } catch (Exception e) {}
                    contrato.requestFocus();
                }

            }
        });
    }

    public class AluguelModel {
        int pid;
        String ptipo;
        String pdesc;
        String pcota;
        String prefer;
        BigDecimal pvalor;

        public AluguelModel(int pid, String ptipo, String pdesc, String pcota, String prefer, BigDecimal pvalor) {
            this.pid = pid;
            this.ptipo = ptipo;
            this.pdesc = pdesc;
            this.pcota = pcota;
            this.prefer = prefer;
            this.pvalor = pvalor;
        }

        public int getPid() { return pid; }
        public void setPid(int pid) { this.pid = pid; }

        public String getPtipo() { return ptipo; }
        public void setPtipo(String ptipo) { this.ptipo = ptipo; }

        public String getPdesc() { return pdesc; }
        public void setPdesc(String pdesc) { this.pdesc = pdesc; }

        public String getPcota() { return pcota; }
        public void setPcota(String pcota) { this.pcota = pcota; }

        public String getPrefer() { return prefer; }
        public void setPrefer(String prefer) { this.prefer = prefer; }

        public BigDecimal getPvalor() { return pvalor; }
        public void setPvalor(BigDecimal pvalor) { this.pvalor = pvalor; }
    }

    public class SegurosModel {
        int pid;
        String papolice;
        String pvencto;
        String pref;
        String pcota;
        boolean pextrato;
        BigDecimal pvalor;

        public SegurosModel(int pid, String papolice, String pvencto, String pref, String pcota, boolean pextrato, BigDecimal pvalor) {
            this.pid = pid;
            this.papolice = papolice;
            this.pvencto = pvencto;
            this.pref = pref;
            this.pcota = pcota;
            this.pextrato = pextrato;
            this.pvalor = pvalor;
        }

        public int getPid() { return pid; }
        public void setPid(int pid) { this.pid = pid; }

        public String getPapolice() { return papolice; }
        public void setPapolice(String papolice) { this.papolice = papolice; }

        public String getPvencto() { return pvencto; }
        public void setPvencto(String pvencto) { this.pvencto = pvencto; }

        public String getPref() { return pref; }
        public void setPref(String pref) { this.pref = pref; }

        public String getPcota() { return pcota; }
        public void setPcota(String pcota) { this.pcota = pcota; }

        public boolean isPextrato() { return pextrato; }
        public void setPextrato(boolean pextrato) { this.pextrato = pextrato; }

        public BigDecimal getPvalor() { return pvalor; }
        public void setPvalor(BigDecimal pvalor) { this.pvalor = pvalor; }
    }

    public class TaxasModel {
        int pid;
        String ptipo;
        String pmat;
        String ppdesc;
        String pdesc;
        String pposdesc;
        String pref;
        String pcota;
        String pvenc;
        BigDecimal pvalor;

        public TaxasModel(int pid, String ptipo, String pmat, String ppdesc, String pdesc, String pposdesc, String pref, String pcota, String pvenc, BigDecimal pvalor) {
            this.pid = pid;
            this.ptipo = ptipo;
            this.pmat = pmat;
            this.ppdesc = ppdesc;
            this.pdesc = pdesc;
            this.pposdesc = pposdesc;
            this.pref = pref;
            this.pcota = pcota;
            this.pvenc = pvenc;
            this.pvalor = pvalor;
        }

        public int getPid() { return pid; }
        public void setPid(int pid) { this.pid = pid; }

        public String getPtipo() { return ptipo; }
        public void setPtipo(String ptipo) { this.ptipo = ptipo; }

        public String getPmat() { return pmat; }
        public void setPmat(String pmat) { this.pmat = pmat; }

        public String getPpdesc() { return ppdesc; }
        public void setPpdesc(String ppdesc) { this.ppdesc = ppdesc; }

        public String getPdesc() { return pdesc; }
        public void setPdesc(String pdesc) { this.pdesc = pdesc; }

        public String getPposdesc() { return pposdesc; }
        public void setPposdesc(String pposdesc) { this.pposdesc = pposdesc; }

        public String getPref() { return pref; }
        public void setPref(String pref) { this.pref = pref; }

        public String getPcota() { return pcota; }
        public void setPcota(String pcota) { this.pcota = pcota; }

        public String getPvenc() { return pvenc; }
        public void setPvenc(String pvenc) { this.pvenc = pvenc; }

        public BigDecimal getPvalor() { return pvalor; }
        public void setPvalor(BigDecimal pvalor) { this.pvalor = pvalor; }
    }
}
