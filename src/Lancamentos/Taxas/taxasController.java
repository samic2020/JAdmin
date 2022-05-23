package Lancamentos.Taxas;

import Classes.Taxas;
import Classes.findImovel;
import Classes.pimoveisModel;
import Funcoes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

import static javafx.collections.FXCollections.observableList;

public class taxasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    int rgprp = -1, rgimv = -1, id = -1;
    String contrato = null;

    @FXML private AnchorPane anchorTaxas;
    @FXML private TextField txMatricula;
    @FXML private TextField txLeitura;
    @FXML private Label txDados;
    @FXML private ComboBox<Taxas> txCodigo;
    @FXML private TextField txPreTexto;
    @FXML private TextField txDescricao;
    @FXML private TextField txPosTexto;
    @FXML private TextField txCotaParcela;
    @FXML private TextField txReferencia;
    @FXML private CheckBox txRetencao;
    @FXML private CheckBox txExtrato;
    @FXML private DatePicker txVencimento;
    @FXML private TextField txValor;
    @FXML private Button btLancar;

    @FXML private TableView<Lancados> txLista;
    @FXML private TableColumn<Lancados, String> txLista_matricula;
    @FXML private TableColumn<Lancados, String> txLista_vencimento;
    @FXML private TableColumn<Lancados, String> txLista_descr;
    @FXML private TableColumn<Lancados, BigDecimal> txLista_valor;
    @FXML private Button btPrint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btPrint.setOnAction(evt -> {
            System.out.println(Printer.getAllPrinters());
            Printer printer = Printer.getDefaultPrinter();
            Stage dialogStage = new Stage(StageStyle.DECORATED);
            PrinterJob job = PrinterJob.createPrinterJob(printer);
            if (job != null) {
                boolean showDialog = job.showPageSetupDialog(dialogStage);
                if (showDialog) {
                    boolean success = job.printPage(txLista);
                    if (success) {
                        job.endJob();
                    }
                }
            }
        });

        // tx???Campo
        {
            MaskFieldUtil.maxField(txPreTexto,10);
            MaskFieldUtil.maxField(txPosTexto,10);
        }

        // Campo txCotaParcela
        {
            MaskFieldUtil.dateRefField(txCotaParcela);
        }

        // Campo txReferencia
        {
            MaskFieldUtil.dateRefField(txReferencia);
        }

        // Campo Valor
        {
            MaskFieldUtil.monetaryField(txValor);
        }

        // btLancar
        {
            btLancar.disableProperty().bind(txVencimento.promptTextProperty().isEmpty().or(txVencimento.promptTextProperty().lessThan(Dates.DateFormata("dd-MM-yyyy",DbMain.getDateTimeServer()))).and(txValor.textProperty().isEmpty().or(txValor.textProperty().isEqualToIgnoreCase("0,00"))));
            btLancar.setOnAction(e -> {
                String iSql = "INSERT INTO taxas(" +
                        "            rgprp, rgimv, contrato, precampo, campo, poscampo, cota, " +
                        "            valor, dtvencimento, referencia, retencao, extrato, tipo, " +
                        "            matricula, dtlanc, usr_lanc)" +
                        "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                try {
                    PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                    int nid = 1;
                    pstmt.setInt(nid++, rgprp);
                    pstmt.setInt(nid++, rgimv);
                    pstmt.setString(nid++, contrato);
                    pstmt.setString(nid++, "" + txPreTexto.getText().trim());
                    pstmt.setString(nid++, "" + txCodigo.getSelectionModel().getSelectedItem().toString());
                    pstmt.setString(nid++, "" + txPosTexto.getText().trim());
                    pstmt.setString(nid++, "" + txCotaParcela.getText().trim());
                    pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(txValor.getText())));
                    pstmt.setDate(nid++, Dates.toSqlDate(txVencimento));
                    pstmt.setString(nid++, "" + txReferencia.getText().trim());
                    pstmt.setBoolean(nid++, txRetencao.isSelected());
                    pstmt.setBoolean(nid++, txExtrato.isSelected());
                    pstmt.setString(nid++, "C");
                    pstmt.setString(nid++, "" + txMatricula.getText().trim());
                    pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    pstmt.setString(nid++, "" + VariaveisGlobais.usuario);

                    pstmt.executeUpdate();
                } catch (SQLException ex) {}
                adcLanc();
            });
        }

        // Campo txLeitura
        {
            txLeitura.focusedProperty().addListener((arg0, velho, novo) -> {
                if (txLeitura.getText() == null) return;
                if (txLeitura.getText().equalsIgnoreCase("")) return;
                if (txLeitura.getText().trim().length() != 44) return;

                if (novo) {
                    // on focus
                    txLeitura.selectAll();
                } else {
                    // out focus
                    String cseg = txLeitura.getText().substring(1,2);
                    String cval = txLeitura.getText().substring(4,15);
                    String cidm = txLeitura.getText().substring(15,19);

                    Object[][] concessionarias = null;
                    String twhere = "idcod = '%s' and cod = '%s'";
                    twhere = String.format(twhere, cseg, cidm);
                    try {
                        concessionarias = conn.LerCamposTabela(new String[] {"nome", "posmat", "cdtaxa"},"concessionarias",twhere);
                    } catch (SQLException ex) {}
                    if (concessionarias != null) {

                    } else {
                        new Alert(Alert.AlertType.INFORMATION,"Concessionária não cadastrada!\n\nAvise o suporte...").show();
                        txLeitura.requestFocus();
                        return;
                    }

                    String cnom = (String)concessionarias[0][3];
                    String ccdt = (String)concessionarias[2][3];
                    String cpmt = (String)concessionarias[1][3];
                    int posini = Integer.valueOf(cpmt.substring(0,2));
                    int tammat = Integer.valueOf(cpmt.substring(2,4));

                    String cmat = txLeitura.getText().trim().substring(posini, posini + tammat);

/*
                    System.out.println(
                            "Seg: " + cseg + "\n" +
                            ccdt + " - " + cnom + " Valor: " + cval + "\n" +
                            "Matricula: " + cmat
                    );
*/

                    txMatricula.setText(cmat);

                    int apos = FuncoesGlobais.ClassIndexOf(txCodigo.getItems().toArray(),ccdt);
                    txCodigo.getSelectionModel().select(apos);
                    txValor.setText(LerValor.FormatNumber(cval.substring(1),2));
                    FindMatLostFocus();
                }
            });
        }

        // Campo Taxas - txtCodigo
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
            txCodigo.setItems(observableList);

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
            txCodigo.setCellFactory(cb);

            txCodigo.setOnAction(e -> {
                txPreTexto.setDisable(!txCodigo.getSelectionModel().getSelectedItem().getPredesc());
                txDescricao.setText(txCodigo.getSelectionModel().getSelectedItem().getDescricao());
                txPosTexto.setDisable(!txCodigo.getSelectionModel().getSelectedItem().getPosdesc());
                txCotaParcela.setText(null);
                txReferencia.setText(null);
                txRetencao.setSelected(txCodigo.getSelectionModel().getSelectedItem().getRetencao());
                txExtrato.setSelected(txCodigo.getSelectionModel().getSelectedItem().getExtrato());
                txVencimento.setValue(null);
                txValor.setText("0,00");
            });
        }

        // Campo Matricula
        {
            MaskFieldUtil.maxField(txMatricula,20);
            MaskFieldUtil.numericField(txMatricula);
            txMatricula.focusedProperty().addListener((arg0, velho, novo) -> {
                if (novo) {
                    // on focus
                    txMatricula.selectAll();
                } else {
                    // out focus
                    if (txMatricula.getText().trim().equalsIgnoreCase("")) {
                        txLeitura.setText(""); txDados.setText("");
                        txLeitura.requestFocus();
                    } else {
                        FindMatLostFocus();
                    }
                }
            });
        }
    }

    private void FindMatLostFocus() {
        if (BuscaTaxa(txMatricula.getText().trim())) {
            String txt = "";
            String sql = "SELECT * FROM imoveis WHERE (exclusao is null) and i_id = '%s';";
            sql = String.format(sql,id);
            ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            String posCampo = null;
            try {
                while (rs.next()) {
                    String nmProp = (String)conn.LerCamposTabela(new String[] {"p_nome"},"proprietarios","p_rgprp = '" + rgprp + "'")[0][3];
                    txt = "Proprietário: " + rgprp + " - " + nmProp + "\n" +
                            "Imóvel: " + rgimv + " - " + rs.getString("i_end") +
                            ", " + rs.getString("i_num") + " / " + rs.getString("i_cplto") + "\n" +
                            rs.getString("i_bairro") + " / " + rs.getString("i_cidade") + " / " +
                            rs.getString("i_estado") + " / " + rs.getString("i_cep");
                    posCampo = rs.getString("i_matriculas");
                }
            } catch (SQLException e) {}
            try {rs.close();} catch (SQLException e) {}

            String[] acampos = posCampo.split(";");
            int pos = -1;
            for (String tcampo : acampos) {
                if (LerValor.isNumeric(tcampo.substring(0, 3))) {
                    if (tcampo.split(",")[1].equalsIgnoreCase(txMatricula.getText())) {
                        int apos = FuncoesGlobais.ClassIndexOf(txCodigo.getItems().toArray(),tcampo.split(",")[0]);
                        txCodigo.getSelectionModel().select(apos);
                        txPreTexto.setDisable(!txCodigo.getItems().get(apos).getPredesc());
                        txDescricao.setText(txCodigo.getItems().get(apos).getDescricao());
                        txPosTexto.setDisable(!txCodigo.getItems().get(apos).getPosdesc());
                        txCotaParcela.setText("");
                        txReferencia.setText("");
                        txRetencao.setSelected(txCodigo.getItems().get(apos).getRetencao());
                        txExtrato.setSelected(txCodigo.getItems().get(apos).getExtrato());
                        txVencimento.setValue(null);
                        txValor.setText("0,00");
                        break;
                    }
                }
            }

            txDados.setText(txt);
            if (!txPreTexto.isDisable()) {
                txPreTexto.requestFocus();
            } else if (!txPosTexto.isDisable()) {
                txPosTexto.requestFocus();
            } else {
                txCotaParcela.requestFocus();
            }
        } else {
            findImovel dialog = new findImovel();
            Optional<pimoveisModel> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                id = Integer.valueOf(b.getId());
                rgprp = Integer.valueOf(b.getRgprp());
                rgimv = Integer.valueOf(b.getRgimv());

                // Pegar o numero do ultimo contrato
                String tcontrato = null;
                {
                    String sSql = "SELECT * FROM locatarios WHERE (exclusao is null) and l_rgprp = '%s' and l_rgimv = '%s' ORDER BY l_contrato DESC LIMIT 1;";
                    sSql = String.format(sSql, rgprp, rgimv);
                    ResultSet rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (rs.next()) {
                            tcontrato = rs.getString("l_contrato");
                        }
                    } catch (SQLException e) {}
                    try {rs.close();} catch (SQLException e) {}
                }
                contrato = b.getSituacao().toUpperCase().equalsIgnoreCase("OCUPADO") ? tcontrato : null;

                // Dados do Imóvel
                {
                    String txt = "";
                    String sql = "SELECT * FROM imoveis WHERE (exclusao is null) and i_rgimv = '%d';";
                    sql = String.format(sql,rgimv);
                    ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (rs.next()) {
                            String nmProp = (String)conn.LerCamposTabela(new String[] {"p_nome"},"proprietarios","p_rgprp = '" + rgprp + "'")[0][3];
                            txt = "Proprietário: " + rgprp + " - " + nmProp + "\n" +
                                    "Imóvel: " + rgimv + " - " + rs.getString("i_end") +
                                    ", " + rs.getString("i_num") + " / " + rs.getString("i_cplto") + "\n" +
                                    rs.getString("i_bairro") + " / " + rs.getString("i_cidade") + " / " +
                                    rs.getString("i_estado") + " / " + rs.getString("i_cep");
                        }
                    } catch (SQLException e) {}
                    try {rs.close();} catch (SQLException e) {}
                    txDados.setText(txt);
                    txCodigo.requestFocus();
                }

                //System.out.println("rgimv: " + rgimv + "\n" + txCodigo.getSelectionModel().getSelectedItem() + "," + txMatricula.getText());

                String iSql = "UPDATE imoveis SET i_matriculas = CASE WHEN i_matriculas IS NULL OR Trim(i_matriculas) = '' THEN '%s,%s' ELSE i_matriculas || ';%s,%s' END WHERE i_rgimv = '%s';";
                iSql = String.format(iSql,
                        txCodigo.getSelectionModel().getSelectedItem(),
                        txMatricula.getText(),
                        txCodigo.getSelectionModel().getSelectedItem(),
                        txMatricula.getText(),
                        rgimv);
                try {conn.ExecutarComando(iSql);} catch (Exception ex) {}

            });

        }
    }

    private boolean BuscaTaxa(String mat) {
        String sql = "SELECT i_id, i_rgprp, i_rgimv, i_matriculas FROM imoveis WHERE (exclusao is null) and i_matriculas LIKE '%," + mat + "%';";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Boolean bAchei = false;
        try {
            while (rs.next()) {
                String campo = rs.getString("i_matriculas");
                int pos = campo.indexOf("," + mat );
                String[] scampo = campo.split(";");
                for (String gcampo : scampo) {
                    if (LerValor.isNumeric(gcampo.substring(0, 3))) {
                        bAchei = true;
                        id = rs.getInt("i_id");
                        rgprp = rs.getInt("i_rgprp");
                        rgimv = rs.getInt("i_rgimv");

                        // Pegar o numero do ultimo contrato
                        String tcontrato = null;
                        {
                            String sSql = "SELECT * FROM locatarios WHERE (exclusao is null) and l_rgprp = '%s' and l_rgimv = '%s' ORDER BY l_contrato DESC LIMIT 1;";
                            sSql = String.format(sSql, rgprp, rgimv);
                            ResultSet lrs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
                            try {
                                while (rs.next()) {
                                    tcontrato = lrs.getString("l_contrato");
                                }
                            } catch (SQLException e) {}
                            try {lrs.close();} catch (SQLException e) {}
                        }
                        try {
                            contrato = rs.getString("i_situacao").toUpperCase().equalsIgnoreCase("VAZIO") ? tcontrato : null;
                        } catch (SQLException ex) {contrato = null;}
                        break;
                    }
                }
                if (bAchei) break;
            }
        } catch (SQLException e) {id = -1; rgprp = -1; rgimv = -1; contrato = null;}
        try {rs.close();} catch (SQLException e) {}
        return bAchei;
    }

    private void adcLanc() {
        List<Lancados> data = new ArrayList<Lancados>();
        data.add(new Lancados(
                txMatricula.getText(),
                Dates.DatetoString(Date.from(txVencimento.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())),
                txDescricao.getText(),
                new BigDecimal(LerValor.Number2BigDecimal(txValor.getText()))
                )
        );

        txLista_matricula.setCellValueFactory(new PropertyValueFactory<Lancados, String>("mat"));
        txLista_vencimento.setCellValueFactory(new PropertyValueFactory<Lancados, String>("vct"));
        txLista_descr.setCellValueFactory(new PropertyValueFactory<Lancados, String>("des"));
        txLista_valor.setCellValueFactory(new PropertyValueFactory<Lancados, BigDecimal>("vlr"));
        txLista.setItems(FXCollections.observableArrayList(data));
    }

    public class Lancados {
        String mat;
        String vct;
        String des;
        BigDecimal vlr;

        public Lancados(String mat, String vct, String des, BigDecimal vlr) {
            this.mat = mat;
            this.vct = vct;
            this.des = des;
            this.vlr = vlr;
        }

        public String getMat() { return mat; }
        public void setMat(String mat) { this.mat = mat; }
        public String getVct() { return vct; }
        public void setVct(String vct) { this.vct = vct; }
        public String getDes() { return des; }
        public void setDes(String des) { this.des = des; }
        public BigDecimal getVlr() { return vlr; }
        public void setVlr(BigDecimal vlr) { this.vlr = vlr; }
    }
}

