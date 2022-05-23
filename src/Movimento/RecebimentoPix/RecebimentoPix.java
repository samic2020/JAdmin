package Movimento.RecebimentoPix;

import Bancos.Pix.PayLoad;
import Bancos.Pix.bancosPix;
import Bancos.RedeBancaria.Banco;
import Calculos.AvisosMensagens;
import Funcoes.Collections;
import Funcoes.*;
import Movimento.Locatarios;
import Movimento.tbvAltera;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static Funcoes.FuncoesGlobais.StrZero;

public class RecebimentoPix implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML
    private AnchorPane anchorPane;

    @FXML private TabPane Tabs;
    @FXML private Tab tabEmail;

    @FXML private Button btnIn;
    @FXML private Button btnOut;
    @FXML private Button btnGerar;
    @FXML private Button btnListar;
    @FXML private TextField txbContrato;
    @FXML private Button btnLancar;

    @FXML private Spinner<String> dtrefmm;
    @FXML private Spinner<Integer> dtrefaaaa;

    private final TreeItem<String> root = new TreeItem<>("Bancos");

    @FXML private TreeTableView<RecebimentoPix.pagadores> pixBanco;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, Number> id;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> contrato;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> nome;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> vencto;

    @FXML private TreeTableView<RecebimentoPix.pagadores> pix;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> b_id;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> b_contrato;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> b_nome;
    @FXML private TreeTableColumn<RecebimentoPix.pagadores, String> b_vencimento;

    @FXML private TableView<RecebimentoPix.dadosBoletaEnvio> pix_email;
    @FXML private TableColumn<RecebimentoPix.dadosBoletaEnvio, String> pix_email_contrato;
    @FXML private TableColumn<RecebimentoPix.dadosBoletaEnvio, String> pix_email_nome;
    @FXML private TableColumn<RecebimentoPix.dadosBoletaEnvio, String> pix_email_vencto;
    @FXML private TableColumn<RecebimentoPix.dadosBoletaEnvio, String> pix_email_nnumero;
    @FXML private TableColumn<RecebimentoPix.dadosBoletaEnvio, String> pix_email_status;

    @FXML private Button btnSendAll;
    @FXML private Button btnSendSelecteds;

    @FXML private ComboBox<bancosPix> bancoPix;

    ObservableList<String> months = FXCollections.observableArrayList(
            "Janeiro", "Fevereiro", "Março", "Abril",
            "Maio", "Junho", "Julho", "Agosto",
            "Setembro", "Outubro", "Novembro", "Dezembro");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(months);
        valueFactory.setValue(months.get(DbMain.getDateTimeServer().getMonth()));
        dtrefmm.setValueFactory(valueFactory);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(2017, 2050, DbMain.getDateTimeServer().getYear());
        valueFactory2.setValue(DbMain.getDateTimeServer().getYear());
        dtrefaaaa.setValueFactory(valueFactory2);

        btnListar.setOnAction(event -> {
            ListarMes(months.indexOf(dtrefmm.getValue()) + 1, dtrefaaaa.getValue());
        });

        btnLancar.setOnAction(event -> {
            ListarContrato(txbContrato.getText(), months.indexOf(dtrefmm.getValue()) + 1, dtrefaaaa.getValue());
        });

        MakeBanco();
        LerBancosPix();

        btnIn.setOnAction(event -> {
            BtnIn_Single();
        });

        btnOut.setOnAction(event -> {
            BtnOut_Single();
        });

        btnGerar.setOnAction(event -> {
            Geracao();
        });

        SetarTabs();

        btnSendAll.setOnAction(event -> {
            //for(dadosBoletaEnvio dados : boleta_email.getItems()) {

            //}
        });

/*
        btnSendSelecteds.setOnAction(event -> {
            for (RecebimentoPix.dadosBoletaEnvio dados : pix_email.getSelectionModel().getSelectedItems()) {

            }
        });
*/
    }

    private void LerBancosPix() {
        bancoPix.getItems().clear();
        List<bancosPix> data = new ArrayList<>();
        bancosPix[] bcosPix = new bancosPix().LerBancos();

        if (bcosPix != null) {
            for (bancosPix pix : bcosPix) {
                bancoPix.getItems().add(pix);
            }
            bancoPix.getSelectionModel().select(0);
        }
    }

    private void SetarTabs() {
        tabEmail.setDisable(true);
        Tabs.getSelectionModel().select(tabEmail);
    }

    TreeItem<RecebimentoPix.pagadores> searchTreeItem(TreeItem<RecebimentoPix.pagadores> item, String name) {
        for(TreeItem<RecebimentoPix.pagadores> child : item.getChildren()) {
            if (child.getValue().getId() == name) {
                return child;
            }
        }
        return null;
    }

    private void delTreeItem(TreeItem<RecebimentoPix.pagadores> item, String name) {
        for(TreeItem<RecebimentoPix.pagadores> child : item.getChildren()) {
            if (child.getValue().getId() == name) {
                child.getParent().getChildren().remove(child);
                return;
            }
        }
        return;
    }

    private void ListarContrato(String contrato, int mes, int ano) {

    }

    private void ListarMes(int mes, int ano) {
        String _refer = StrZero(String.valueOf(mes),2) + "/" + StrZero(String.valueOf(ano),4);
        String sql = "SELECT m.id, m.rgprp, m.rgimv, m.contrato, m.dtvencimento, " +
                "CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END AS nome, " +
                "l.l_tprecebimento AS banco FROM movimento m, locatarios l WHERE (exclusao is null) and " +
                "(l.l_contrato = m.contrato) AND m.referencia = '%s' AND m.selected = true " +
                "AND l.l_tprecebimento <> 'REC' " +
                "ORDER BY l.l_tprecebimento;";
        sql = String.format(sql, _refer);
        ResultSet _rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            String t_banco = "";
            TreeItem<RecebimentoPix.pagadores> root = new TreeItem("Bancos");
            TreeItem<RecebimentoPix.pagadores> Lista = null;
            while (_rs.next()) {
                String _banco = bancoPix.getSelectionModel().getSelectedItem().getBanco();
                int _id = _rs.getInt("id");
                String _contrato = _rs.getString("contrato");
                String _nome = _rs.getString("nome");
                String _vecto = Dates.DateFormata("dd/MM/yyyy", _rs.getDate("dtvencimento"));

                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(_contrato);

                if (!t_banco.equalsIgnoreCase(_banco)) {
                    if (!t_banco.equalsIgnoreCase("")) {
                        root.getChildren().add(Lista);
                    }
                    t_banco = _banco;
                    Lista = new TreeItem(new pagadores(t_banco,"","","",false));
                }

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<RecebimentoPix.pagadores> lista_Item = new TreeItem(new pagadores(t_banco, _contrato, _nome, _vecto, bBloq));
                lista_Item.setExpanded(true);
                Lista.getChildren().add(lista_Item);
                Lista.setExpanded(true);
            }
            if (!t_banco.equalsIgnoreCase("")) {
                root.getChildren().add(Lista);
            }
            root.setExpanded(true);
            MakeBancos(root);
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(_rs); } catch (Exception e) {e.printStackTrace();}
    }

    private void MakeBancos(TreeItem<RecebimentoPix.pagadores> root) {
        pixBanco.setRoot(root);
        pixBanco.setShowRoot(false);

        pixBanco.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //id.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().idProperty());
        contrato.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().contratoProperty());
        nome.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().nomeProperty());
        vencto.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().venctoProperty());

        pixBanco.setRowFactory(tv -> {
            return new TreeTableRow<RecebimentoPix.pagadores>() {
                @Override
                public void updateItem(RecebimentoPix.pagadores item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (item == null) {
                        this.setStyle("-fx-text-fill: black; ");
                    } else if (item.getBloq()) {
                        this.setStyle("-fx-text-fill: red;");
                    } else {
                        this.setStyle("-fx-text-fill: black; ");
                    }
                }
            };
        });
    }

    private void MakeBanco() {
        pix.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        b_id.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().idProperty());
        b_contrato.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().contratoProperty());
        b_nome.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().nomeProperty());
        b_vencimento.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecebimentoPix.pagadores, String> param) -> param.getValue().getValue().venctoProperty());

        TreeItem<RecebimentoPix.pagadores> root = new TreeItem("Bancos");
        pix.setRoot(root);
        pix.setShowRoot(false);
    }

    private TreeItem<RecebimentoPix.pagadores> copyItem(TreeItem<RecebimentoPix.pagadores> item) {
        TreeItem<RecebimentoPix.pagadores> copy = new TreeItem<RecebimentoPix.pagadores>(item.getValue());
        for (TreeItem<RecebimentoPix.pagadores> child : item.getChildren()) {
            copy.getChildren().add(copyItem(child));
        }
        return copy;
    }

    private void Geracao() {
        // Setar bancos para array de numero de bancos
        int nBancos = 1; //bancoPix.getItems().size();
        DadosBancoPix[] bancos = new DadosBancoPix[nBancos];
        for (int i=0;i<nBancos;i++) bancos[i] = new DadosBancoPix();
        int bco = 0;

        TreeItem<RecebimentoPix.pagadores> root = pix.getRoot();
        for (TreeItem<RecebimentoPix.pagadores> item : root.getChildren()) {
            if (item.getValue().getBloq()) continue;

            // Dados do Banco
            String banco = item.getValue().getId();

            //bancosPix[] dadosBanco = new bancosPix().LerBancos();
            //if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }

            // Dados do Beneficiario - Imobiliária
            bancos[bco].setBenef_Codigo("");
            try {bancos[bco].setBenef_Razao((!VariaveisGlobais.pb_razao_noprint) ? conn.LerParametros("da_razao") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Fantasia(conn.LerParametros("da_fanta"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_CNPJ((!VariaveisGlobais.pb_cnpj_noprint) ? conn.LerParametros("da_cnpj") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Inscricao(conn.LerParametros("da_insc"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_InscTipo(conn.LerParametros("da_tipo"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_Endereco((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_ender") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Numero((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_numero") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Complto((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cplto") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Bairro((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_bairro") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Cidade((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cidade") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Estado((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_estado") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Cep((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cep") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Telefone((!VariaveisGlobais.pb_telefone_noprint) ? conn.LerParametros("da_tel") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Email(conn.LerParametros("da_email"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_HPage(conn.LerParametros("da_hpage"));} catch (SQLException e) {}
            // Não esquecer de dimensionar o logo pb_logo_width/pb_logo_height
            try {bancos[bco].setBenef_Logo(new Image(new File(conn.LerParametros("da_logo")).toURI().toString()));} catch (SQLException e) {}
            // Parametros da QCR

            int pag = 0; int nPag = TreeTableViewNodeCount(item);
            PagadorPix[] pagador = new PagadorPix[nPag]; for (int i = 0; i<nPag; i++) pagador[i] = new PagadorPix();
            for (TreeItem<RecebimentoPix.pagadores> subitem : item.getChildren()) {
                // Dados do Locatario
                Locatarios dadosLoc = new Banco(null,null).LerDadosLocatario(subitem.getValue().getContrato());
                pagador[pag].setBanco(banco);
                pagador[pag].setCodigo(subitem.getValue().getContrato()); // Registro do Proprietário
                pagador[pag].setRazao(dadosLoc.getRazao());
                pagador[pag].setFantasia(dadosLoc.getFantasia());
                pagador[pag].setCNPJ(dadosLoc.getCpfcnpj());
                pagador[pag].setEndereco(dadosLoc.getEndereco());
                pagador[pag].setNumero(dadosLoc.getNumero());
                pagador[pag].setComplto(dadosLoc.getComplemento());
                pagador[pag].setBairro(dadosLoc.getBairro());
                pagador[pag].setCidade(dadosLoc.getCidade());
                pagador[pag].setEstado(dadosLoc.getEstado());
                pagador[pag].setCep(dadosLoc.getCep());
                pagador[pag].setTelefone(dadosLoc.getTelefone());
                pagador[pag].setEmail(dadosLoc.getEmail());
                pagador[pag].setEnvio(dadosLoc.getEnvio());

                pagador[pag].setRc_Codigo(subitem.getValue().getContrato());
                pagador[pag].setRc_NNumero("");
                pagador[pag].setRc_DtDocumento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
                pagador[pag].setRc_NumDocumento(subitem.getValue().getContrato());
                pagador[pag].setRc_DtProcessamento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
                pagador[pag].setRc_Vencimento(subitem.getValue().getVencto());

                pagador[pag].setRc_Dados(new String[][] {}); // {{cod, desc, cp, valor}, ...}
                pagador[pag].setRc_mensagem("");

                Banco vbanco = new Banco(subitem.getValue().getContrato(),subitem.getValue().getVencto());
                List<tbvAltera> dados = vbanco.ProcessaCampos();
                pagador[pag].setRc_Valor(dados.get(dados.size() -1).getValor());

                PayLoad payLoad = new PayLoad();
                Object[][] Pix = PayLoad.LerBancoPIX(bancoPix.getSelectionModel().getSelectedItem().toString());

                // Setar pagadores
                String pixNNumero = Pix[3][3].toString();
                pagador[pag].setRc_NNumero("PIX" + payLoad.NossoNumeroPix(pixNNumero,10));

                Collections dadm = VariaveisGlobais.getAdmDados();

                payLoad.setPixKey(PixEmpresa(Pix[1][3].toString()));
                String tpRecibo = (dadm.get("recibo").isEmpty() ? "RECIBOPIX" : dadm.get("recibo").trim() + "PIX");
                payLoad.setDescription(tpRecibo + " Vencimento " + pagador[pag].getRc_Vencimento());
                payLoad.setAmount(LerValor.StringToFloat(pagador[pag].getRc_Valor().toString()));
                payLoad.setMerchantName(dadm.get("empresa").toUpperCase().trim());
                payLoad.setMerchantCity("SAO PAULO");
                payLoad.setTxId(pagador[pag].getRc_NNumero());

                String PixCopiaCola = payLoad.getPayload();
                Map<EncodeHintType, Object> qrParam = new HashMap<EncodeHintType, Object>();
                qrParam.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                qrParam.put (EncodeHintType.CHARACTER_SET, "UTF-8" );

                BarcodeQRCode code25 = new BarcodeQRCode(PixCopiaCola, 66, 66, qrParam);
                java.awt.Image cdbar = null;
                cdbar = code25.createAwtImage(Color.BLACK, Color.WHITE);

                pagador[pag].setRc_linhaDIgitavel(PixCopiaCola);
                pagador[pag].setRc_codigoBarras(cdbar);

                ProcessaPagador(pagador[pag]);

                pixNNumero = String.valueOf(Float.valueOf(pixNNumero) + 1).replace(".0","");
                payLoad.GravarNnumeroPIX(bancoPix.getSelectionModel().getSelectedItem().getBanco(), pixNNumero);

                pag++;
            }
            bancos[bco].setBenef_pagadores(pagador);

            bco++;
        }

        for (DadosBancoPix obanco : bancos ) {
            if (obanco.getBenef_Razao() != null) GradeEmail(obanco);
        }

    }

    /**
     * Retorna KeyPix sem formatação
     * @param string pix
     * @return string
     */
    private String PixEmpresa(String pix) {
        return pix.replace(".", "").replace("-", "").replace("/", "").replace("(", "").replace(")", "");
    }

    private void locaDesc(BoletaPix beans, String[] texto, int linha) {
        if (linha < 1 && linha > 14) linha = 1;
        switch (linha) {
            case 1: beans.setlocaDescL01(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL01(texto[2]);beans.setlocaVrL01(texto[3]);break;
            case 2: beans.setlocaDescL02(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL02(texto[2]);beans.setlocaVrL02(texto[3]);break;
            case 3: beans.setlocaDescL03(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL03(texto[2]);beans.setlocaVrL03(texto[3]);break;
            case 4: beans.setlocaDescL04(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL04(texto[2]);beans.setlocaVrL04(texto[3]);break;
            case 5: beans.setlocaDescL05(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL05(texto[2]);beans.setlocaVrL05(texto[3]);break;
            case 6: beans.setlocaDescL06(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL06(texto[2]);beans.setlocaVrL06(texto[3]);break;
            case 7: beans.setlocaDescL07(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL07(texto[2]);beans.setlocaVrL07(texto[3]);break;
            case 8: beans.setlocaDescL08(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL08(texto[2]);beans.setlocaVrL08(texto[3]);break;
            case 9: beans.setlocaDescL09(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL09(texto[2]);beans.setlocaVrL09(texto[3]);break;
            case 10: beans.setlocaDescL10(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL10(texto[2]);beans.setlocaVrL10(texto[3]);break;
            case 11: beans.setlocaDescL11(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL11(texto[2]);beans.setlocaVrL11(texto[3]);break;
            case 12: beans.setlocaDescL12(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL12(texto[2]);beans.setlocaVrL12(texto[3]);break;
            case 13: beans.setlocaDescL13(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL13(texto[2]);beans.setlocaVrL13(texto[3]);break;
            case 14: beans.setlocaDescL14(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL14(texto[2]);beans.setlocaVrL14(texto[3]);break;
            default: beans.setlocaDescL01(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL01(texto[2]);beans.setlocaVrL01(texto[3]);
        }
    }

    private boolean EstaNaGrade(TableView<dadosBoletaEnvio> grade, String variavel, boolean All_or_Selected) {
        boolean retorno = false;
        if (All_or_Selected) {
            ObservableList<dadosBoletaEnvio> itens = grade.getItems();
            for (dadosBoletaEnvio boleto : itens) {
                if (boleto.getNome().equalsIgnoreCase(variavel)) {
                    retorno = true;
                    break;
                }
            }
        } else {
            ObservableList<dadosBoletaEnvio> itens = grade.getSelectionModel().getSelectedItems();
            for (dadosBoletaEnvio boleto : itens) {
                if (boleto.getNome().equalsIgnoreCase(variavel)) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    private void GradeEmail(DadosBancoPix banco) {
        pix_email_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        pix_email_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        pix_email_vencto.setCellValueFactory(new PropertyValueFactory<>("vencto"));
        pix_email_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));
        pix_email_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        PagadorPix[] pagador = banco.getBenef_pagadores();
        List<RecebimentoPix.dadosBoletaEnvio> data = new ArrayList<>();
        for (PagadorPix boleto : pagador) {
            data.add(new dadosBoletaEnvio(boleto.getRc_Codigo(),boleto.getRazao(), boleto.getRc_Vencimento(), boleto.getRc_NNumero(),""));
        }
        pix_email.setItems(FXCollections.observableArrayList(data));

        // Ativa itens
        Tabs.setDisable(data.size() <= 0);
        tabEmail.setDisable(data.size() <= 0);
        pix_email.setDisable(data.size() <= 0);
        btnSendSelecteds.setDisable(data.size() <= 0);
        btnSendAll.setDisable(data.size() <= 0);

        btnSendSelecteds.setOnAction(event -> {
            List<BoletaPix> lista = new ArrayList<BoletaPix>();
            PagadorPix[] pagadores = banco.getBenef_pagadores();
            for (PagadorPix paga : pagadores) {
                if (!EstaNaGrade(pix_email, paga.getRazao(),false)) continue;

                BoletaPix beans = new BoletaPix();
                beans.setempNome(banco.getBenef_Razao());
                beans.setempEndL1(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Endereco() + ", " + banco.getBenef_Numero() + " " + banco.getBenef_Complto() + " - " + banco.getBenef_Bairro() : "");
                beans.setempEndL2(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Cidade() + " - " + banco.getBenef_Estado() + " - CEP " + banco.getBenef_Cep() : "");
                beans.setempEndL3(!banco.getBenef_Telefone().equalsIgnoreCase("") ? "Tel/Fax.: " + banco.getBenef_Telefone() : "");
                beans.setempEndL4(banco.getBenef_HPage() + " / " + banco.getBenef_Email());

                beans.setbolDadosCedente(new Pad(banco.getBenef_Razao(),50).RPad() + (!VariaveisGlobais.pb_cnpj_noprint ? "CNPJ: " + banco.getBenef_CNPJ() : ""));
                beans.setlogoBanco(System.getProperty("user.dir") + "/resources/logoBancos/" + StrZero(String.valueOf(banco.getBanco()), 3) + ".jpg");
                beans.setnumeroBanco(StrZero(String.valueOf(banco.getBanco()), 3) + "-" + banco.getBancoDv());

                // Logo da Imobiliaria
                beans.setlogoLocation(banco.getBenef_Logo().impl_getUrl());

                boolean maniv = new AvisosMensagens().VerificaAniLocatario(paga.getRc_NumDocumento());
                beans.setlocaMsgL01(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");

                //Object[] mreaj = new AvisosMensagens().VerificaReajuste(paga.getRc_NumDocumento());
                //beans.setlocaMsgL02((boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");

                beans.setlocaMsgL02("Referente ao vencimento: " + paga.getRc_Vencimento());
                beans.setlocaMsgL03("");
                beans.setlocaMsgL04("");

                // Dados do Boleto
                int linha = 1;
                for (int i = 0; i < paga.getRc_Dados().length - 1; i++) locaDesc(beans, paga.getRc_Dados()[i], linha++);

                Banco vbanco = new Banco(paga.getRc_NumDocumento(), paga.getRc_Vencimento());
                List<tbvAltera> dados = vbanco.ProcessaCampos();

                String vrBoleta = dados.get(dados.size() - 1).getValor(); //paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];

                beans.setbolDadosValor(vrBoleta);
                beans.setbolDadosVrcobrado(vrBoleta);
                beans.setbolDadosVencimento(paga.getRc_Vencimento());
                beans.setbolDadosVrdoc(vrBoleta);
                beans.setbolDadosDatadoc(paga.getRc_DtDocumento());
                beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
                beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
                beans.setbolDadosEspecie(banco.getBanco_Especie());
                beans.setbolDadosEspeciedoc(banco.getBanco_EspecieDoc());
                beans.setbolDadosAceite(banco.getBanco_Aceite());
                beans.setbolDadosCarteira(banco.getBanco_Carteira());
                beans.setbolDadosAgcodced("+5521976659897");
                beans.setbolDadosNnumero(paga.getRc_NNumero());

                // Dados do Pagador
                beans.setsacDadosNome(paga.getRazao());
                beans.setsacDadosEndereco(paga.getEndereco());
                beans.setsacDadosNumero(paga.getNumero());
                beans.setsacDadosCompl(paga.getComplto());
                beans.setsacDadosBairro(paga.getBairro());
                beans.setsacDadosCidade(paga.getCidade());
                beans.setsacDadosEstado(paga.getEstado());
                beans.setsacDadosCep(paga.getCep());
                String _cpfcnpj = paga.getCNPJ().length() <= 14 ? "CPF: " + paga.getCNPJ() : "CNPJ: " + paga.getCNPJ();
                beans.setsacDadosCpfcnpj(_cpfcnpj);

                beans.setcodDadosDigitavel(paga.getRc_linhaDIgitavel());
                beans.setcodDadosBarras(paga.getRc_codigoBarras());

                lista.add(beans);
            }

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"BoletosPix");
            new PdfViewer("Preview do Recebimento PIX", pdfName);
        });
    }

    private int TreeTableViewNodeCount(TreeItem<RecebimentoPix.pagadores> node) {
        int i = 0;
        try {
            for (TreeItem<RecebimentoPix.pagadores> item : node.getChildren()) {
                i += 1;
            }
        } catch (Exception e) {}
        return i;
    }

    private void BtnOut_Single() {
        TreeItem<RecebimentoPix.pagadores> selected = (TreeItem<RecebimentoPix.pagadores>) pix.getSelectionModel().getSelectedItem();
        if (selected != pix.getRoot()) {
            String parent = null;
            try {parent = selected.getParent().getValue().getId();} catch (Exception e) {}
            if (parent != null) {
                TreeItem<RecebimentoPix.pagadores> result = searchTreeItem(pixBanco.getRoot(), parent);
                if (result != null) {
                    result.getChildren().addAll(copyItem(selected));
                    pix.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                } else {
                    TreeItem<RecebimentoPix.pagadores> newBanco = new TreeItem(new pagadores(parent,"","","",false));
                    newBanco.setExpanded(true);
                    pixBanco.getRoot().getChildren().add(newBanco);

                    // Expandir
                    pixBanco.getRoot().setExpanded(true);

                    TreeItem<RecebimentoPix.pagadores> newresult = searchTreeItem(pixBanco.getRoot(), parent);
                    if (newresult != null) {
                        newresult.getChildren().addAll(copyItem(selected));
                        pix.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                    }
                }
            }

            TreeItem<RecebimentoPix.pagadores> seachNode = searchTreeItem(pix.getRoot(), parent);
            if (seachNode != null) {
                if (seachNode.getChildren().isEmpty()) {
                    delTreeItem(pix.getRoot(),parent);
                }
            }
        }
        pix.getSelectionModel().clearSelection();
    }

    private void BtnIn_Single() {
        TreeItem<RecebimentoPix.pagadores> selected = (TreeItem<RecebimentoPix.pagadores>) pixBanco.getSelectionModel().getSelectedItem();
        if (selected != pixBanco.getRoot()) {
            if (selected.getValue().getBloq()) return;

            String parent = null;
            try {parent = selected.getParent().getValue().getId();} catch (Exception e) {}
            if (parent != null) {
                TreeItem<RecebimentoPix.pagadores> result = searchTreeItem(pix.getRoot(), parent);
                if (result != null) {
                    result.getChildren().addAll(copyItem(selected));
                    pixBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                } else {
                    TreeItem<RecebimentoPix.pagadores> newBanco = new TreeItem(new pagadores(parent,"","","", false));
                    newBanco.setExpanded(true);
                    pix.getRoot().getChildren().add(newBanco);

                    // Expandir
                    pix.getRoot().setExpanded(true);

                    TreeItem<RecebimentoPix.pagadores> newresult = searchTreeItem(pix.getRoot(), parent);
                    if (newresult != null) {
                        newresult.getChildren().addAll(copyItem(selected));
                        pixBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                    }
                }
            }

            TreeItem<RecebimentoPix.pagadores> seachNode = searchTreeItem(pixBanco.getRoot(), parent);
            if (seachNode != null) {
                if (seachNode.getChildren().isEmpty()) {
                    delTreeItem(pixBanco.getRoot(),parent);
                }
            }
        }
        pixBanco.getSelectionModel().clearSelection();
    }

    public class pagadores {
        private SimpleStringProperty id;
        private SimpleStringProperty contrato;
        private SimpleStringProperty nome;
        private SimpleStringProperty vencto;
        private SimpleBooleanProperty bloq;

        public pagadores(String id, String contrato, String nome, String vencto, boolean bloq) {
            this.id = new SimpleStringProperty(id);
            this.contrato = new SimpleStringProperty(contrato);
            this.nome = new SimpleStringProperty(nome);
            this.vencto = new SimpleStringProperty(vencto);
            this.bloq = new SimpleBooleanProperty(bloq);
        }

        public String getId() { return id.get(); }
        public SimpleStringProperty idProperty() { return id; }
        public void setId(String id) { this.id.set(id); }

        public String getContrato() { return contrato.get(); }
        public SimpleStringProperty contratoProperty() { return contrato; }
        public void setContrato(String contrato) { this.contrato.set(contrato); }

        public String getNome() { return nome.get(); }
        public SimpleStringProperty nomeProperty() { return nome; }
        public void setNome(String nome) { this.nome.set(nome); }

        public String getVencto() { return vencto.get(); }
        public SimpleStringProperty venctoProperty() { return vencto; }
        public void setVencto(String vencto) { this.vencto.set(vencto); }

        public boolean getBloq() { return bloq.get(); }
        public SimpleBooleanProperty bloqProperty() { return bloq; }
        public void setBloq(boolean bloq) { this.bloq.set(bloq); }
    }

    public class dadosBoletaEnvio {
        private SimpleStringProperty contrato;
        private SimpleStringProperty nome;
        private SimpleStringProperty vencto;
        private SimpleStringProperty nnumero;
        private SimpleStringProperty status;

        public dadosBoletaEnvio(String contrato, String nome, String vencto, String nnumero) {
            this.contrato = new SimpleStringProperty(contrato);
            this.nome = new SimpleStringProperty(nome);
            this.vencto = new SimpleStringProperty(vencto);
            this.nnumero = new SimpleStringProperty(nnumero);
        }

        public dadosBoletaEnvio(String contrato, String nome, String vencto, String nnumero, String status) {
            this.contrato = new SimpleStringProperty(contrato);
            this.nome = new SimpleStringProperty(nome);
            this.vencto = new SimpleStringProperty(vencto);
            this.nnumero = new SimpleStringProperty(nnumero);
            this.status = new SimpleStringProperty(status);
        }

        public String getContrato() { return contrato.get(); }
        public SimpleStringProperty contratoProperty() { return contrato; }
        public void setContrato(String contrato) { this.contrato.set(contrato); }

        public String getNome() { return nome.get(); }
        public SimpleStringProperty nomeProperty() { return nome; }
        public void setNome(String nome) { this.nome.set(nome); }

        public String getVencto() { return vencto.get(); }
        public SimpleStringProperty venctoProperty() { return vencto; }
        public void setVencto(String vencto) { this.vencto.set(vencto); }

        public String getNnumero() { return nnumero.get(); }
        public SimpleStringProperty nnumeroProperty() { return nnumero; }
        public void setNnumero(String nnumero) { this.nnumero.set(nnumero); }

        public String getStatus() { return status.get(); }
        public SimpleStringProperty statusProperty() { return status; }
        public void setStatus(String status) { this.status.set(status); }
    }

    private String getSiteBanco(String sbanco) {
        String retorno = "";
        Object[][] site = null;

        try {
            site = conn.LerCamposTabela(new String[] {"site"}, "bancos", "numero Like '%" + sbanco + "'");
        } catch (SQLException e) {}

        if (site != null) retorno = (String) site[0][3];
        return retorno;
    }

    public void ProcessaPagador(PagadorPix pagadors) {
        pagadors.setRc_NNumero(pagadors.getRc_NNumero());

        String vrBoleta = pagadors.getRc_Valor();
        if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta), 2);

        // Processa recibo para preencher msgs
        List<tbvAltera> data = new ArrayList<tbvAltera>();
        Banco banco = new Banco(pagadors.getCodigo(), pagadors.getRc_Vencimento());
        data = banco.ProcessaCampos();

        String[][] dados = {};
        for (tbvAltera dado : data) {
            dados = FuncoesGlobais.ArraysAdd(dados, new String[] {String.valueOf(dado.getId()), dado.getDesc(), dado.getCota(), dado.getValor()});
        }
        pagadors.setRc_Dados(dados);
    }

}
