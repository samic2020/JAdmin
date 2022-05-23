package Administrador.Abas.ContasBoleta;

import Administrador.BancoBoleta;
import Administrador.cbancos;
import Bancos.Pix.bancosPix;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import com.samic.maskedtextfield.MaskedTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContasBoleta implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean pixInclusao = false;

    @FXML private AnchorPane anchorPaneBancos;
    @FXML private ComboBox<cbancos> bb_banco;
    @FXML private TextField bb_agencia;
    @FXML private TextField bb_conta;
    @FXML private TextField bb_contadv;
    @FXML private TextField bb_cedente;
    @FXML private TextField bb_carteira;
    @FXML private TextField bb_tarifa;
    @FXML private TextField bb_nnumero;
    @FXML private Button bb_btnLancar;

    @FXML private TableView<BancoBoleta> bb_bancos;
    @FXML private TableColumn<BancoBoleta, Integer> bb_bancos_id;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_banco;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_agencia;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_agenciadv;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_conta;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_contadv;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_cedente;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_cedentedv;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_carteira;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_tarifa;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_nnumero;
    @FXML private TableColumn<BancoBoleta, String> bb_bancos_nnumerotam;

    @FXML private TextField bb_agenciadv;
    @FXML private TextField bb_cedentedv;
    @FXML private TextField bb_nnumerotam;
    @FXML private ComboBox<String> bb_linha1;
    @FXML private TextField bb_linha1desc;
    @FXML private ComboBox<String> bb_linha2;
    @FXML private TextField bb_linha2desc;

    @FXML private TableView<bancosPix> pix_bancos;
    @FXML private TableColumn<bancosPix, Integer> pix_bancos_id;
    @FXML private TableColumn<bancosPix, String> pix_bancos_tipo;
    @FXML private TableColumn<bancosPix, String> pix_bancos_chave;
    @FXML private TableColumn<bancosPix, String> pix_bancos_banco;
    @FXML private TableColumn<bancosPix, Integer> pix_bancos_nnumero;
    @FXML private ComboBox<String> pixTipo;
    @FXML private MaskedTextField pixChave;
    @FXML private TextField pixNomeBanco;

    @FXML private Button pixIncluir;
    @FXML private Button pixExcluir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initialize_bb();
    }

    private void Initialize_bb() {
        /**
         * Popula bancosPix
         */

        pixTipo.setDisable(true);
        pixChave.setDisable(true);
        pixNomeBanco.setDisable(true);
        pixInclusao = false;
        populaBancosPix();

        populaComboTipoPix();

        pixIncluir.setOnAction(event -> {
            pixInclusao = true;
            pixTipo.getSelectionModel().select(0);
            pixTipo.setDisable(false);
            pixChave.setText(null);
            pixChave.setDisable(false);
            pixNomeBanco.setText("");
            pixNomeBanco.setDisable(false);
            pixTipo.requestFocus();
        });

        pixExcluir.setOnAction(event -> {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equalsIgnoreCase("não")) return;

            int pos = pix_bancos.getSelectionModel().getFocusedIndex();
            if (pos > -1) {
                String tid = String.valueOf(pix_bancos.getSelectionModel().getSelectedItem().getId());
                String sql = "DELETE FROM bancos_pix WHERE id = '" + tid + "';";
                try {
                    conn.ExecutarComando(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            populaBancosPix();
        });

        pixNomeBanco.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                if (pixNomeBanco.getText().isEmpty()) {
                    pixNomeBanco.requestFocus();
                    return;
                }
                if (pixInclusao) {
                    // Gravar
                    String insertSQL = "INSERT INTO bancos_pix(tipo, chave, banco) " +
                            "VALUES ('" + pixTipo.getSelectionModel().getSelectedItem().toString() + "','" +
                            pixChave.getText() + "','" + pixNomeBanco.getText() + "');";
                    conn.ExecutarComando(insertSQL);

                    pixTipo.setDisable(true);
                    pixChave.setDisable(true);
                    pixNomeBanco.setDisable(true);

                    pixInclusao = false;
                    populaBancosPix();
                }
            }
        });

        // Preencher combobox bancos
        {
            ResultSet rs = conn.AbrirTabela("SELECT numero, nome, site, agenciatam, contatam, cedentetam FROM bancos ORDER BY numero;", ResultSet.CONCUR_READ_ONLY);
            try {
                bb_banco.getItems().clear();
                while (rs.next()) {
                    bb_banco.getItems().add(new cbancos(
                            rs.getString("numero"),
                            rs.getString("nome"),
                            rs.getString("site"),
                            rs.getInt("agenciatam"),
                            rs.getInt("contatam"),
                            rs.getInt("cedentetam")
                    ));
                }
            } catch (SQLException e) {
            }
            try {
                DbMain.FecharTabela(rs);
            } catch (Exception e) {
            }

            Callback<ListView<cbancos>, ListCell<cbancos>> cb = new Callback<ListView<cbancos>, ListCell<cbancos>>() {
                @Override
                public ListCell<cbancos> call(ListView<cbancos> l) {
                    return new ListCell<cbancos>() {

                        private final HBox hbx;
                        private final Label codigo;
                        private final Label sep;
                        private final Label nome;

                        {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            codigo = new Label();
                            codigo.setTextFill(Color.DARKBLUE);
                            codigo.setPrefWidth(30);
                            codigo.setPrefHeight(25);
                            codigo.setAlignment(Pos.CENTER_RIGHT);

                            sep = new Label(" - ");
                            sep.setPrefWidth(20);
                            sep.setPrefHeight(25);
                            sep.setAlignment(Pos.CENTER);

                            nome = new Label();
                            nome.setTextFill(Color.DARKGREEN);
                            nome.setPrefWidth(180);
                            nome.setPrefHeight(25);
                            nome.setAlignment(Pos.CENTER_LEFT);

                            hbx = new HBox(codigo, sep, nome);
                        }

                        @Override
                        protected void updateItem(cbancos item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                //setText(item.getNumero() + " - "+item.getNome());
                                codigo.setText(item.getNumero());
                                nome.setText(item.getNome());
                                setGraphic(hbx);
                            }
                        }
                    };
                }
            };
            bb_banco.setCellFactory(cb);
        }

        MaskFieldUtil.maxField(bb_agencia, 10);
        MaskFieldUtil.numericField(bb_agencia);
        MaskFieldUtil.maxField(bb_agenciadv, 1);
        MaskFieldUtil.numericField(bb_agenciadv);

        MaskFieldUtil.maxField(bb_conta, 15);
        MaskFieldUtil.numericField(bb_conta);
        MaskFieldUtil.maxField(bb_contadv, 1);
        MaskFieldUtil.numericField(bb_contadv);

        MaskFieldUtil.maxField(bb_cedente, 20);
        MaskFieldUtil.numericField(bb_cedente);
        MaskFieldUtil.maxField(bb_cedentedv, 1);
        MaskFieldUtil.numericField(bb_cedentedv);

        MaskFieldUtil.maxField(bb_carteira, 3);
        MaskFieldUtil.numericField(bb_carteira);

        MaskFieldUtil.monetaryField(bb_tarifa);

        MaskFieldUtil.maxField(bb_nnumero, 20);
        MaskFieldUtil.numericField(bb_nnumero);
        MaskFieldUtil.maxField(bb_nnumerotam, 3);
        MaskFieldUtil.numericField(bb_nnumerotam);

        // Configura campos de acordo com o banco selecionado
        bb_banco.setOnAction(event -> {
            cbancos infos = bb_banco.getSelectionModel().getSelectedItem();

            MaskFieldUtil.maxField(bb_agencia, infos.getAgenciatam());
            MaskFieldUtil.numericField(bb_agencia);

            MaskFieldUtil.maxField(bb_conta, infos.getContatam());
            MaskFieldUtil.numericField(bb_conta);

            if (infos.getCedentetam() > 0) {
                bb_cedente.setDisable(false);
                bb_cedentedv.setDisable(false);
                MaskFieldUtil.maxField(bb_cedente, infos.getCedentetam());
                MaskFieldUtil.numericField(bb_cedente);
            } else {
                bb_cedente.setText("");
                bb_cedente.setDisable(true);
                bb_cedentedv.setText("");
                bb_cedentedv.setDisable(true);
            }
        });

        bb_btnLancar.setOnAction(event -> {
            String iSql = "INSERT INTO banco_boleta(" +
                    "banco, agencia, agenciadv, conta, contadv, cedente, cedentedv, carteira, tarifa, nnumero, nnumerotam) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                int nid = 1;
                pstmt.setString(nid++, bb_banco.getSelectionModel().getSelectedItem().getNumero());
                pstmt.setString(nid++, bb_agencia.getText().trim());
                pstmt.setInt(nid++, Integer.parseInt(bb_agenciadv.getText().trim()));
                pstmt.setString(nid++, bb_conta.getText().trim());
                pstmt.setInt(nid++, Integer.parseInt(bb_contadv.getText().trim()));
                pstmt.setString(nid++, bb_cedente.getText().trim());
                pstmt.setInt(nid++, Integer.parseInt(bb_cedentedv.getText().trim()));
                pstmt.setString(nid++, bb_carteira.getText().trim());
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(bb_tarifa.getText())));
                pstmt.setDouble(nid++, Double.valueOf(bb_nnumero.getText().trim()));
                pstmt.setDouble(nid++, Double.valueOf(bb_nnumerotam.getText().trim()));
                pstmt.executeUpdate();
            } catch (SQLException ex) {
            }

            populateBancoBoleta();
            ClearCampos();
        });

        bb_bancos.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String sql = "DELETE FROM banco_boleta WHERE id = '%s';";
                    sql = String.format(sql, bb_bancos.getSelectionModel().getSelectedItem().getId());
                    try {
                        conn.ExecutarComando(sql);
                    } catch (Exception e) {
                    }
                    populateBancoBoleta();
                    bb_banco.requestFocus();
                }

            }
        });

        // Desabilita Botao se campos vazios
        bb_btnLancar.disableProperty().bind(bb_agencia.textProperty().isEqualTo("").or(bb_conta.textProperty().isEqualTo("").or(
                bb_contadv.textProperty().isEqualTo("").or(bb_cedente.textProperty().isEqualTo("").or(
                        bb_carteira.textProperty().isEqualTo("").or(bb_tarifa.textProperty().isEqualTo("").or(
                                bb_nnumero.textProperty().isEqualTo(""))))))));
        populateBancoBoleta();
        ClearCampos();
    }

    private void ClearCampos() {
        bb_banco.getSelectionModel().select(0);
        bb_agencia.setText("");
        bb_agenciadv.setText("");
        bb_conta.setText("");
        bb_contadv.setText("");
        bb_cedente.setText("");
        bb_cedentedv.setText("");
        bb_carteira.setText("");
        bb_tarifa.setText("");
        bb_nnumero.setText("");
        bb_nnumerotam.setText("");

        bb_banco.requestFocus();
    }

    private void populateBancoBoleta() {
        List<BancoBoleta> data = new ArrayList<BancoBoleta>();
        ResultSet rs;
        String qSQL = "SELECT id, banco, agencia, agenciadv, conta, contadv, cedente, cedentedv, carteira, tarifa, nnumero, nnumerotam, lote FROM banco_boleta ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qbanco = null, qagencia = null, qconta = null, qcedente = null,
                        qcarteira = null;
                int qagenciadv = 0, qcontadv = 0, qcedentedv = 0;
                BigDecimal qtarifa = null;
                int qid = -1, qlote = 0;
                double qnnumero = 0, qnnumerotam = 0;

                try { qid = rs.getInt("id"); } catch (SQLException e) { }
                try { qbanco = rs.getString("banco"); } catch (SQLException e) { }
                try { qagencia = rs.getString("agencia"); } catch (SQLException e) { }
                try { qagenciadv = rs.getInt("agenciadv"); } catch (SQLException e) { }
                try { qconta = rs.getString("conta"); } catch (SQLException e) { }
                try { qcontadv = rs.getInt("contadv"); } catch (SQLException e) { }
                try { qcedente = rs.getString("cedente"); } catch (SQLException e) { }
                try { qcedentedv = rs.getInt("cedentedv"); } catch (SQLException e) { }
                try { qcarteira = rs.getString("carteira"); } catch (SQLException e) { }
                try { qtarifa = rs.getBigDecimal("tarifa"); } catch (SQLException e) { }
                try { qnnumero = rs.getDouble("nnumero"); } catch (SQLException e) { }
                try { qnnumerotam = rs.getDouble("nnumerotam"); } catch (SQLException e) { }
                try { qlote = rs.getInt("lote"); } catch (SQLException e) { }

                data.add(new BancoBoleta(qid, qbanco, qagencia, qagenciadv, qconta, qcontadv, qcedente, qcedentedv, qcarteira,
                        LerValor.FormatPattern(qtarifa.toPlainString(), "#,##0.00"), qnnumero, qnnumerotam, qlote)
                );
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        bb_bancos_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        bb_bancos_id.setStyle("-fx-alignment: CENTER;");
        bb_bancos_banco.setCellValueFactory(new PropertyValueFactory<>("banco"));
        bb_bancos_banco.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_agencia.setCellValueFactory(new PropertyValueFactory<>("agencia"));
        bb_bancos_agencia.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_agenciadv.setCellValueFactory(new PropertyValueFactory<>("agenciadv"));
        bb_bancos_agenciadv.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_conta.setCellValueFactory(new PropertyValueFactory<>("conta"));
        bb_bancos_conta.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_contadv.setCellValueFactory(new PropertyValueFactory<>("contadv"));
        bb_bancos_contadv.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_cedente.setCellValueFactory(new PropertyValueFactory<>("cedente"));
        bb_bancos_cedente.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_cedentedv.setCellValueFactory(new PropertyValueFactory<>("cedentedv"));
        bb_bancos_cedentedv.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_carteira.setCellValueFactory(new PropertyValueFactory<>("carteira"));
        bb_bancos_carteira.setStyle("-fx-alignment: CENTER;");
        bb_bancos_tarifa.setCellValueFactory(new PropertyValueFactory<>("tarifa"));
        bb_bancos_tarifa.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));
        bb_bancos_nnumero.setStyle("-fx-alignment: CENTER-RIGHT;");
        bb_bancos_nnumerotam.setCellValueFactory(new PropertyValueFactory<>("nnumero"));
        bb_bancos_nnumerotam.setStyle("-fx-alignment: CENTER-RIGHT;");

        if (!data.isEmpty()) bb_bancos.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * Aqui fica o cadastro dos bancos PIX
     */
    private void populaBancosPix() {
        /**
         * Checa a existencia da tabela no banco de dados, se não existir cia-o.
         */
        new bancosPix().BancoPixStructure();

        List<bancosPix> data = new ArrayList<>();
        bancosPix[] bcosPix = new bancosPix().LerBancos();

        if (bcosPix != null) {
            for (bancosPix pix : bcosPix) {
                data.add(pix);
            }
        }

        pix_bancos_id.setCellValueFactory(new PropertyValueFactory<>("id"));;
        pix_bancos_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));;
        pix_bancos_chave.setCellValueFactory(new PropertyValueFactory<>("chave"));;
        pix_bancos_banco.setCellValueFactory(new PropertyValueFactory<>("banco"));;
        pix_bancos_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));;

        pix_bancos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pix_bancos.setItems(FXCollections.observableArrayList(data));;
    }

    private void populaComboTipoPix() {
        pixTipo.getItems().clear();
        String[] bcosPix = new String[] {"Telefone", "Email", "CPF", "CNPJ", "Outros"};

        if (bcosPix != null) {
            for (String pix : bcosPix) {
                pixTipo.getItems().add(pix);
            }
            pixTipo.getSelectionModel().select(0);

            pixTipo.valueProperty().addListener((observable, oldValue, newValue) -> {
                int tipo = pixTipo.getSelectionModel().getSelectedIndex();
                ((MaskedTextField)pixChave).setMask("");
                switch (tipo) {
                    case 0: // Telefone
                        pixChave.setMask("(##)#####-####");
                        break;
                    case 1: // Email
                        pixChave.setMask("");
                        MaskFieldUtil.maxField(pixChave, 45);
                        break;
                    case 2: // CPF
                        pixChave.setMask("###.###.###-##");
                        break;
                    case 3: // CNPJ
                        pixChave.setMask("##.###.###/####-##");
                        break;
                    case 4: // Outros
                        pixChave.setMask("");
                        MaskFieldUtil.maxField(pixChave, 45);
                        break;
                }
                pixChave.setText("");
                pixChave.requestFocus();
            });
            MaskFieldUtil.maxField(pixNomeBanco, 45);

            pixTipo.requestFocus();
        }
    }
}
