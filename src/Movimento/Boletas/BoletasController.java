package Movimento.Boletas;

import Administrador.BancoBoleta;
import Bancos.DadosBanco;
import Bancos.Pagador;
import Bancos.RedeBancaria.*;
import Calculos.AvisosMensagens;
import Funcoes.*;
import Movimento.Locatarios;
import Movimento.tbvAltera;
import boleta.Boleta;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import pdfViewer.PdfViewer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Funcoes.FuncoesGlobais.StrZero;

//import etiqueta.Etiqueta_5960;

public class BoletasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;

    @FXML private TabPane Tabs;
    @FXML private Tab tabRemessas;
    @FXML private Tab tabEmail;
    @FXML private Tab tabHand;
    @FXML private Tab tabEtc;

    @FXML private Button btnIn;
    @FXML private Button btnOut;
    @FXML private Button btnGerar;
    @FXML private Button btnListar;
    @FXML private TextField txbContrato;
    @FXML private Button btnLancar;

    @FXML private Spinner<String> dtrefmm;
    @FXML private Spinner<Integer> dtrefaaaa;

    private final TreeItem<String> root = new TreeItem<>("Bancos");

    @FXML private TreeTableView<pagadores> BoletasBanco;
    @FXML private TreeTableColumn<pagadores, Number> id;
    @FXML private TreeTableColumn<pagadores, String> contrato;
    @FXML private TreeTableColumn<pagadores, String> nome;
    @FXML private TreeTableColumn<pagadores, String> vencto;

    @FXML private TreeTableView<pagadores> boletas;
    @FXML private TreeTableColumn<pagadores, Number> b_id;
    @FXML private TreeTableColumn<pagadores, String> b_contrato;
    @FXML private TreeTableColumn<pagadores, String> b_nome;
    @FXML private TreeTableColumn<pagadores, String> b_vencimento;

    // Seção de Envios (Mãos, correio, email)
    @FXML private TableView<dadosBoletaEnvio> boleta_emmaos;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_emmaos_contrato;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_emmaos_nome;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_emmaos_vencto;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_emmaos_nnumero;
    @FXML private Button boleta_emmaos_btImprimir;

    @FXML private TableView<dadosBoletaEnvio> boleta_correios;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_correios_contrato;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_correios_nome;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_correios_vencto;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_correios_nnumero;
    @FXML private Button boleta_correios_btnImprimir;
    @FXML private Button boleta_correios_btnEtiquetas;

    @FXML private TableView<dadosBoletaEnvio> boleta_email;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_email_contrato;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_email_nome;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_email_vencto;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_email_nnumero;
    @FXML private TableColumn<dadosBoletaEnvio, String> boleta_email_status;

    @FXML private TableView<dadosBoletaRemessa> boleta_remessa;
    @FXML private TableColumn<dadosBoletaRemessa, String> boleta_remessa_banco;
    @FXML private TableColumn<dadosBoletaRemessa, String> boleta_remessa_lote;
    @FXML private TableColumn<dadosBoletaRemessa, String> boleta_remessa_file;
    @FXML private TableColumn<dadosBoletaRemessa, Boolean> boleta_remessa_botoes;

    @FXML private Button btnSendAll;
    @FXML private Button btnSendSelecteds;

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

        btnIn.setOnAction(event -> {
            BtnIn_Single();
        });

        btnOut.setOnAction(event -> {
            BtnOut_Single();
        });

        btnGerar.setOnAction(event -> {
/*
            try {
                SendEmail email = new SendEmail(0);
                boolean bEniviado = email.sendMsg(
                        new String[][] {{"wellspinto@gmail.com"}, {"wellspinto@hotmail.com"}},
                        "Estou enviando os seus anexos.",
                        "Anexos no formato diversos.",
                        new String[]{"C:\\Users\\Samic\\Downloads\\ATESTADO DE OBITO.PNG", "C:\\Users\\Samic\\Pictures\\Passeli.PNG"}
                );
                System.out.println(bEniviado);
            } catch (Exception e) {e.printStackTrace();}
*/

            Geracao();
        });

        SetarTabs();

        btnSendAll.setOnAction(event -> {
            //for(dadosBoletaEnvio dados : boleta_email.getItems()) {

            //}
        });

        btnSendSelecteds.setOnAction(event -> {
            for (dadosBoletaEnvio dados : boleta_email.getSelectionModel().getSelectedItems()) {

            }
        });
    }

    private void SetarTabs() {
        tabRemessas.setDisable(false);
        tabEmail.setDisable(true);
        tabHand.setDisable(true);
        tabEtc.setDisable(true);

        Tabs.getSelectionModel().select(tabRemessas);
    }

    TreeItem<pagadores> searchTreeItem(TreeItem<pagadores> item, int name) {
        for(TreeItem<pagadores> child : item.getChildren()) {
            if (child.getValue().getId() == name) {
                return child;
            }
        }
        return null;
    }

    private void delTreeItem(TreeItem<pagadores> item, int name) {
        for(TreeItem<pagadores> child : item.getChildren()) {
            if (child.getValue().getId() == name) {
                child.getParent().getChildren().remove(child);
                return;
            }
        }
        return;
    }

    boolean searchTreeItemString(TreeItem<pagadores> item, String name) {
        TreeItem<pagadores> result = null;
        for(TreeItem<pagadores> child : item.getChildren()) {
            if (child.getValue().getContrato().equalsIgnoreCase(name)) {
                child.getChildren().remove(name);
                return true;
            }
        }
        return false;
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
            TreeItem<pagadores> root = new TreeItem("Bancos");
            TreeItem<pagadores> Lista = null;
            while (_rs.next()) {
                String _banco = _rs.getString("banco");
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
                    Lista = new TreeItem(new pagadores(Integer.parseInt(_banco),"","","",false));
                }

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<pagadores> lista_Item = new TreeItem(new pagadores(_id, _contrato, _nome, _vecto, bBloq));
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

    private void MakeBancos(TreeItem<pagadores> root) {
        BoletasBanco.setRoot(root);
        BoletasBanco.setShowRoot(false);

        BoletasBanco.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        id.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, Number> param) -> param.getValue().getValue().idProperty());
        contrato.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().contratoProperty());
        nome.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().nomeProperty());
        vencto.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().venctoProperty());

        BoletasBanco.setRowFactory(tv -> {
            return new TreeTableRow<pagadores>() {
                @Override
                public void updateItem(pagadores item, boolean empty) {
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
        boletas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        b_id.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, Number> param) -> param.getValue().getValue().idProperty());
        b_contrato.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().contratoProperty());
        b_nome.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().nomeProperty());
        b_vencimento.setCellValueFactory((TreeTableColumn.CellDataFeatures<pagadores, String> param) -> param.getValue().getValue().venctoProperty());

        TreeItem<pagadores> root = new TreeItem("Bancos");
        boletas.setRoot(root);
        boletas.setShowRoot(false);
    }

    private TreeItem<pagadores> copyItem(TreeItem<pagadores> item) {
        TreeItem<pagadores> copy = new TreeItem<pagadores>(item.getValue());
        for (TreeItem<pagadores> child : item.getChildren()) {
            copy.getChildren().add(copyItem(child));
        }
        return copy;
    }

    private void Geracao() {
        // Setar bancos para array de numero de bancos
        int nBancos = TreeTableViewNodeCount(boletas.getRoot());
        DadosBanco[] bancos = new DadosBanco[nBancos];
        for (int i=0;i<nBancos;i++) bancos[i] = new DadosBanco();
        int bco = 0;

        TreeItem<pagadores> root = boletas.getRoot();
        for (TreeItem<pagadores> item : root.getChildren()) {
            if (item.getValue().getBloq()) continue;

            // Dados do Banco
            int banco = item.getValue().getId();

            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }

            // Dados Bancários
            bancos[bco].setBanco(banco);
            bancos[bco].setBancoDv(new Banco().CalcDigBancoMod11(StrZero(String.valueOf(banco),3)));
            bancos[bco].setBanco_NNumero(String.valueOf(dadosBanco.getNnumero()));
            bancos[bco].setBanco_Aceite("N");
            bancos[bco].setBanco_Agencia(dadosBanco.getAgencia());
            bancos[bco].setBanco_AgenciaDv(String.valueOf(dadosBanco.getAgenciadv()));
            bancos[bco].setBanco_Carteira(dadosBanco.getCarteira());
            bancos[bco].setBanco_CodBenef(dadosBanco.getCedente());
            bancos[bco].setBanco_CodBenefDv(String.valueOf(dadosBanco.getCedentedv()));
            bancos[bco].setBanco_CodMoeda("9");
            bancos[bco].setBanco_Conta(dadosBanco.getConta());
            bancos[bco].setBanco_ContaDv(String.valueOf(dadosBanco.getContadv()));
            bancos[bco].setBanco_Especie("R$");
            bancos[bco].setBanco_EspecieDoc("REC");
            bancos[bco].setBanco_TamanhoNnumero(17);
            bancos[bco].setBanco_LocalPagamentoLinha1("Este boleto poderá ser pago em qualquer banco.".toUpperCase());
            bancos[bco].setBanco_LocalPagamentoLinha2("Após o vencimento seguir intruções do Beneficiário.".toUpperCase());
            bancos[bco].setBanco_Lote(dadosBanco.getLote());

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
            Pagador[] pagador = new Pagador[nPag]; for (int i=0;i<nPag;i++) pagador[i] = new Pagador();
            for (TreeItem<pagadores> subitem : item.getChildren()) {
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

                pagador[pag].setRc_instrucao01("");
                pagador[pag].setRc_instrucao02("");
                pagador[pag].setRc_instrucao03("");
                pagador[pag].setRc_instrucao04("");
                pagador[pag].setRc_instrucao05("");
                pagador[pag].setRc_instrucao06("");
                pagador[pag].setRc_instrucao07("");
                pagador[pag].setRc_instrucao08("");
                pagador[pag].setRc_instrucao09("");
                pagador[pag].setRc_instrucao10("");

                pagador[pag].setRc_linhaDIgitavel("");
                pagador[pag].setRc_codigoBarras("");

                Banco vbanco = new Banco(subitem.getValue().getContrato(),subitem.getValue().getVencto());
                List<tbvAltera> dados = vbanco.ProcessaCampos();
                pagador[pag].setRc_Valor(dados.get(dados.size() -1).getValor());

                pag++;

                // Debug
                // System.out.println(item.getValue().getId() + " -> " + subitem.getValue().getContrato() + " - " + subitem.getValue().getNome() + " " + subitem.getValue().getVencto());
            }
            //System.out.println("");

            // Setar pagadores
            bancos[bco].setBenef_pagadores(pagador);

            // Processa dados
            Object vbanco;
            switch (bancos[bco].getBanco()) {
                case 1: // Bb
                    vbanco = new Brasil(bancos[bco]);
                    ((Brasil) vbanco).Processa();
                    break;
                case 341: // Itau
                    vbanco = new Itau(bancos[bco]);
                    ((Itau) vbanco).Processa();
                    break;
                case 33: // Santander
                    vbanco = new Santander(bancos[bco]);
                    ((Santander) vbanco).Processa();
                    break;
                case 77: // Santander
                    vbanco = new Santander(bancos[bco]);
                    ((Santander) vbanco).Processa();
                    break;
                case 247: // Bradesco
                    vbanco = new Bradesco(bancos[bco]);
                    ((Bradesco) vbanco).Processa();
                    break;
                case 104: // Cef
                    vbanco = new Cef(bancos[bco]);
                    ((Cef) vbanco).Processa();
                    break;
                default: // Digital
                    vbanco = new Digital(bancos[bco]);
                    ((Digital) vbanco).Processa();
            }

            bco++;

        }

        dadosBoletaRemessa[] remessa = new dadosBoletaRemessa[bancos.length];
        int nbco = 0;
        for (DadosBanco obanco : bancos ) {
            switch (obanco.getBanco()) {
                case 1: // Bb
                    break;
                case 341: // Itau
                    Itau ibanco = new Itau(obanco);
                    int bancoItau = obanco.getBanco();
                    int loteItau = obanco.getBanco_Lote();
                    String nnumeroItau = obanco.getBanco_NNumero();
                    String pRemessaItau = ibanco.Remessa(String.valueOf(loteItau));

                    // Debug
                    // System.out.println("Lote: " + loteItau + "\n" + ibanco.Remessa(String.valueOf(loteItau)));

                    // Alimenta view
                    remessa[nbco++] = new dadosBoletaRemessa(String.valueOf(bancoItau),String.valueOf(loteItau),bancoItau + "_" + loteItau + ".rem", nnumeroItau);

                    // Gera arquivo fisico
                    StreamFile fillerItau = new StreamFile(new String[] {"TxRx/Remessa/Enviar/" + bancoItau + "_" + loteItau + ".rem"});
                    if (fillerItau.Open()) {
                        fillerItau.Print(pRemessaItau);
                    }
                    fillerItau.Close();

                    obanco.setBanco_Lote(loteItau++);
                    //UpGradeBancoLote(bancoItau, loteItau, nnumeroItau);

                    break;
                case 33: // Santander
                    Santander sbanco = new Santander(obanco);
                    int loteSant = obanco.getBanco_Lote();
                    int bancoSant = obanco.getBanco();
                    String nnumeroSant = obanco.getBanco_NNumero();
                    String pRemessaSant = sbanco.Remessa(String.valueOf(loteSant));

                    // Debug
                    // System.out.println("Lote: " + loteSant + "\n" + pRemessaSant);

                    // Alimenta view
                    remessa[nbco++] = new dadosBoletaRemessa(String.valueOf(bancoSant),String.valueOf(loteSant),bancoSant + "_" + loteSant + ".rem", nnumeroSant);

                    // Gera arquivo fisico
                    StreamFile fillerSant = new StreamFile(new String[] {"TxRx/Remessa/Enviar/" + bancoSant + "_" + loteSant + ".rem"});
                    if (fillerSant.Open()) {
                        fillerSant.Print(pRemessaSant);
                    }
                    fillerSant.Close();

                    obanco.setBanco_Lote(loteSant++);
                    //UpGradeBancoLote(bancoSant, loteSant, nnumeroSant);

                    break;
                case 247: // Bradesco
                    break;
            }
        }

        // Chama Grade
        GradeRemessa(remessa);
        for (DadosBanco obanco : bancos ) {
            GradeEmMaos(obanco);
            GradeCorreios(obanco);
            GradeEmail(obanco);
        }

        SetarTabs();
    }

    private void GradeEmMaos(DadosBanco banco) {
        boleta_emmaos_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        boleta_emmaos_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        boleta_emmaos_vencto.setCellValueFactory(new PropertyValueFactory<>("vencto"));
        boleta_emmaos_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));

        Pagador[] pagador = banco.getBenef_pagadores();
        List<dadosBoletaEnvio> data = new ArrayList<>();
        for (Pagador boleto : pagador) {
            if (boleto.getEnvio().equalsIgnoreCase("0")) data.add(new dadosBoletaEnvio(boleto.getRc_Codigo(),boleto.getRazao(), boleto.getRc_Vencimento(), boleto.getRc_NNumero(),""));
        }
        boleta_emmaos.setItems(FXCollections.observableArrayList(data));

        // botao - boleta_emmaos_btImprimir;
        boleta_emmaos_btImprimir.setOnAction(event -> {
            List<Boleta> lista = new ArrayList<Boleta>();
            Pagador[] pagadores = banco.getBenef_pagadores();
            for (Pagador paga : pagadores) {
                if (!EstaNaGrade(boleta_emmaos, paga.getRazao(),true)) continue;

                Boleta beans = new Boleta();
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

                beans.setbcoMsgL01(banco.getBanco_LocalPagamentoLinha1());
                beans.setbcoMsgL02(banco.getBanco_LocalPagamentoLinha2());

                boolean maniv = new AvisosMensagens().VerificaAniLocatario(paga.getRc_NumDocumento());
                beans.setlocaMsgL01(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");

                Object[] mreaj = new AvisosMensagens().VerificaReajuste(paga.getRc_NumDocumento());
                beans.setlocaMsgL02((boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");

                beans.setlocaMsgL03("");
                beans.setlocaMsgL04("");

                // Dados do Boleto
                int linha = 1;
                for (int i = 0; i < paga.getRc_Dados().length - 1; i++) locaDesc(beans, paga.getRc_Dados()[i], linha++);

                BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
                if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }

                String vrBoleta = paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];
                if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);

                beans.setbolDadosValor(vrBoleta);
                beans.setbolDadosVencimento(paga.getRc_Vencimento());
                beans.setbolDadosVrdoc(vrBoleta);
                beans.setbolDadosDatadoc(paga.getRc_DtDocumento());
                beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
                beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
                beans.setbolDadosEspecie(banco.getBanco_Especie());
                beans.setbolDadosEspeciedoc(banco.getBanco_EspecieDoc());
                beans.setbolDadosAceite(banco.getBanco_Aceite());
                beans.setbolDadosCarteira(banco.getBanco_Carteira());
                beans.setbolDadosAgcodced(banco.getBanco_Agencia() + "/" + banco.getBanco_CodBenef() + "-" + banco.getBanco_CodBenefDv() );
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

                beans.setbolDadosMsg01(MsgChanges(VariaveisGlobais.pb_int01, paga));
                beans.setbolDadosMsg02(MsgChanges(VariaveisGlobais.pb_int02, paga));
                beans.setbolDadosMsg03(MsgChanges(VariaveisGlobais.pb_int03, paga));
                beans.setbolDadosMsg04(MsgChanges(VariaveisGlobais.pb_int04, paga));
                beans.setbolDadosMsg05(MsgChanges(VariaveisGlobais.pb_int05, paga));
                beans.setbolDadosMsg06(MsgChanges(VariaveisGlobais.pb_int06, paga));
                beans.setbolDadosMsg07(MsgChanges(VariaveisGlobais.pb_int07, paga));
                beans.setbolDadosMsg08(MsgChanges(VariaveisGlobais.pb_int08, paga));
                beans.setbolDadosMsg09(MsgChanges(VariaveisGlobais.pb_int09, paga));
                beans.setbolDadosMsg10(MsgChanges(VariaveisGlobais.bol_txbanc ? "Incluso Tarifa bancária $F{TXBANCO}" : "", paga));

                beans.setcodDadosBarras(paga.getRc_codigoBarras().substring(0, banco.getBenef_pagadores()[0].getRc_codigoBarras().length() - 1));
                beans.setcodDadosDigitavel(paga.getRc_linhaDIgitavel());

                lista.add(beans);
            }

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos");
            // new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Boleto", pdfName);
        });

    }

/*
    private String GeraEtiquetaPDFTemp(List<Etiqueta_5960> lista) {
        String outFileName = new tempFile("pdf").getsPathNameExt();
        JasperPrint jasperPrint = null;
        try {
            JRDataSource jrds = new JRBeanCollectionDataSource(lista);

            String reportFileName = "Reports/Etiqueta_5960.jasper";
            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
            jasperPrint = JasperFillManager.fillReport(reporte, null, jrds);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return outFileName;
    }
*/

    private String MsgChanges(String msg, Pagador banco) {
        String retorno = msg;
        retorno = retorno.replace("$F{VENCIMENTO}", banco.getRc_Vencimento());
        retorno = retorno.replace("$F{CARENCIA}",String.valueOf(VariaveisGlobais.ca_multa));
        retorno = retorno.replace("$F{VENCCARENC}", Dates.DateFormata("dd/MM/yyyy", Dates.sqlDateAdd(Dates.DIA,VariaveisGlobais.ca_multa,Dates.StringtoDate(banco.getRc_Vencimento(),"dd/MM/yyyy"))));
        retorno = retorno.replace("$F{MULTA}", String.valueOf(VariaveisGlobais.mu_res));
        retorno = retorno.replace("$F{ENCARGOS}",String.valueOf(VariaveisGlobais.co_perc));
        retorno = retorno.replace("$F{JUROS}",String.valueOf(VariaveisGlobais.ju_percent));
        retorno = retorno.replace("$F{CORRECAO}",String.valueOf(VariaveisGlobais.co));
        retorno = retorno.replace("$F{VALORBOLETA}",banco.getRc_Valor());

        BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
        if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return retorno; }
        retorno = retorno.replace("$F{TXBANCO}","R$ " + dadosBanco.getTarifa());

        return retorno;
    }

    private void locaDesc(Boleta beans, String[] texto, int linha) {
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

    private void GradeCorreios(DadosBanco banco) {
        boleta_correios_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        boleta_correios_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        boleta_correios_vencto.setCellValueFactory(new PropertyValueFactory<>("vencto"));
        boleta_correios_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));

        Pagador[] pagador = banco.getBenef_pagadores();
        List<dadosBoletaEnvio> data = new ArrayList<>();
        for (Pagador boleto : pagador) {
            if (boleto.getEnvio().equalsIgnoreCase("2")) data.add(new dadosBoletaEnvio(boleto.getRc_Codigo(),boleto.getRazao(), boleto.getRc_Vencimento(), boleto.getRc_NNumero(),""));
        }
        boleta_correios.setItems(FXCollections.observableArrayList(data));

        boleta_correios_btnImprimir.setOnAction(event -> {
            List<Boleta> lista = new ArrayList<Boleta>();
            Pagador[] pagadores = banco.getBenef_pagadores();
            for (Pagador paga : pagadores) {
                if (!EstaNaGrade(boleta_correios, paga.getRazao(),false)) continue;

                Boleta beans = new Boleta();
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

                beans.setbcoMsgL01(banco.getBanco_LocalPagamentoLinha1());
                beans.setbcoMsgL02(banco.getBanco_LocalPagamentoLinha2());

                beans.setlocaMsgL01("Boleto de Teste");
                beans.setlocaMsgL02("");
                beans.setlocaMsgL03("");
                beans.setlocaMsgL04("");

                // Dados do Boleto
                int linha = 1;
                for (int i = 0; i < paga.getRc_Dados().length - 1; i++) locaDesc(beans, paga.getRc_Dados()[i], linha++);

                BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
                if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }

                String vrBoleta = paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];
                if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);

                beans.setbolDadosValor(vrBoleta);
                beans.setbolDadosVencimento(paga.getRc_Vencimento());
                beans.setbolDadosVrdoc(vrBoleta);
                beans.setbolDadosDatadoc(paga.getRc_DtDocumento());
                beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
                beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
                beans.setbolDadosEspecie(banco.getBanco_Especie());
                beans.setbolDadosEspeciedoc(banco.getBanco_EspecieDoc());
                beans.setbolDadosAceite(banco.getBanco_Aceite());
                beans.setbolDadosCarteira(banco.getBanco_Carteira());
                beans.setbolDadosAgcodced(banco.getBanco_Agencia() + "/" + banco.getBanco_CodBenef() + "-" + banco.getBanco_CodBenefDv() );
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

                beans.setbolDadosMsg01(MsgChanges(VariaveisGlobais.pb_int01, paga));
                beans.setbolDadosMsg02(MsgChanges(VariaveisGlobais.pb_int02, paga));
                beans.setbolDadosMsg03(MsgChanges(VariaveisGlobais.pb_int03, paga));
                beans.setbolDadosMsg04(MsgChanges(VariaveisGlobais.pb_int04, paga));
                beans.setbolDadosMsg05(MsgChanges(VariaveisGlobais.pb_int05, paga));
                beans.setbolDadosMsg06(MsgChanges(VariaveisGlobais.pb_int06, paga));
                beans.setbolDadosMsg07(MsgChanges(VariaveisGlobais.pb_int07, paga));
                beans.setbolDadosMsg08(MsgChanges(VariaveisGlobais.pb_int08, paga));
                beans.setbolDadosMsg09(MsgChanges(VariaveisGlobais.pb_int09, paga));
                beans.setbolDadosMsg10(MsgChanges(VariaveisGlobais.bol_txbanc ? "Incluso Tarifa bancária $F{TXBANCO}" : "", paga));

                beans.setcodDadosBarras(paga.getRc_codigoBarras().substring(0, banco.getBenef_pagadores()[0].getRc_codigoBarras().length() - 1));
                beans.setcodDadosDigitavel(paga.getRc_linhaDIgitavel());

                lista.add(beans);
            }

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos");
            // new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Boleto", pdfName);
        });

        boleta_correios_btnEtiquetas.setOnAction(event -> {
/*
            List<Etiqueta_5960> lista = new ArrayList<Etiqueta_5960>();
            Pagador[] pagadores = banco.getBenef_pagadores();
            for (Pagador paga : pagadores) {
                if (!EstaNaGrade(boleta_correios, paga.getRazao(),false)) continue;

                Etiqueta_5960 beans = new Etiqueta_5960();

                // Dados do Pagador
                beans.setNome(paga.getRazao());
                beans.setEndereco(paga.getEndereco());
                beans.setNumero(paga.getNumero());
                beans.setCompremento(paga.getComplto());
                beans.setBairro(paga.getBairro());
                beans.setCidade(paga.getCidade());
                beans.setEstado(paga.getEstado());
                beans.setCep(paga.getCep());
                beans.setMensagem(paga.getRc_Codigo() + "/" + paga.getRc_NNumero());

                lista.add(beans);
            }

            String pdfName = GeraEtiquetaPDFTemp(lista);
            // new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview das Etiquetas", pdfName);
*/
        });

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

    private void GradeEmail(DadosBanco banco) {
        boleta_email_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        boleta_email_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        boleta_email_vencto.setCellValueFactory(new PropertyValueFactory<>("vencto"));
        boleta_email_nnumero.setCellValueFactory(new PropertyValueFactory<>("nnumero"));
        boleta_email_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        Pagador[] pagador = banco.getBenef_pagadores();
        List<dadosBoletaEnvio> data = new ArrayList<>();
        for (Pagador boleto : pagador) {
            if (boleto.getEnvio().equalsIgnoreCase("1")) data.add(new dadosBoletaEnvio(boleto.getRc_Codigo(),boleto.getRazao(), boleto.getRc_Vencimento(), boleto.getRc_NNumero(),""));
        }
        boleta_email.setItems(FXCollections.observableArrayList(data));

        // botao - boleta_emmaos_btImprimir;

    }

    private void GradeRemessa(dadosBoletaRemessa[] remessas) {
        boleta_remessa_banco.setCellValueFactory(new PropertyValueFactory<>("banco"));
        boleta_remessa_lote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        boleta_remessa_file.setCellValueFactory(new PropertyValueFactory<>("arquivo"));
        boleta_remessa_botoes.setCellFactory(new Callback<TableColumn<dadosBoletaRemessa, Boolean>, TableCell<dadosBoletaRemessa, Boolean>>() {
            @Override public TableCell<dadosBoletaRemessa, Boolean> call(TableColumn<dadosBoletaRemessa, Boolean> personBooleanTableColumn) {
                return new AddRemessaCell(boleta_remessa);
            }
        });

        boleta_remessa.setItems(FXCollections.observableArrayList(remessas));
    }

    private void UpGradeBancoLote(int banco, int lote, String nnumero) {
        try {
            String uSql = "UPDATE banco_boleta SET lote = %s, nnumero = '%s' WHERE banco = '%s';";
            uSql = String.format(uSql, lote, nnumero, StrZero(String.valueOf(banco),3));
            conn.ExecutarComando(uSql);
        } catch (Exception e) {}
    }

    private int TreeTableViewNodeCount(TreeItem<pagadores> node) {
        int i = 0;
        try {
            for (TreeItem<pagadores> item : node.getChildren()) {
                i += 1;
            }
        } catch (Exception e) {}
        return i;
    }

    private int TreeTableViewItemCount(TreeItem<pagadores> node) {
        int i = 0;
        try {
            for (TreeItem<pagadores> item : node.getChildren()) {
                for (TreeItem<pagadores> subitem : item.getChildren()) {
                    i += 1;
                }
            }
        } catch (Exception e) {}
        return i;
    }

    private void BtnOut_Single() {
        TreeItem<pagadores> selected = (TreeItem<pagadores>) boletas.getSelectionModel().getSelectedItem();
        if (selected != boletas.getRoot()) {
            int parent = -1;
            try {parent = selected.getParent().getValue().getId();} catch (Exception e) {}
            if (parent > -1) {
                TreeItem<pagadores> result = searchTreeItem(BoletasBanco.getRoot(), parent);
                if (result != null) {
                    result.getChildren().addAll(copyItem(selected));
                    boletas.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                } else {
                    TreeItem<pagadores> newBanco = new TreeItem(new pagadores(parent,"","","",false));
                    newBanco.setExpanded(true);
                    BoletasBanco.getRoot().getChildren().add(newBanco);

                    // Expandir
                    BoletasBanco.getRoot().setExpanded(true);

                    TreeItem<pagadores> newresult = searchTreeItem(BoletasBanco.getRoot(), parent);
                    if (newresult != null) {
                        newresult.getChildren().addAll(copyItem(selected));
                        boletas.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                    }
                }
            }

            TreeItem<pagadores> seachNode = searchTreeItem(boletas.getRoot(), parent);
            if (seachNode != null) {
                if (seachNode.getChildren().isEmpty()) {
                    delTreeItem(boletas.getRoot(),parent);
                }
            }
        }
        boletas.getSelectionModel().clearSelection();
    }

    private void BtnIn_Single() {
        TreeItem<pagadores> selected = (TreeItem<pagadores>) BoletasBanco.getSelectionModel().getSelectedItem();
        if (selected != BoletasBanco.getRoot()) {
            if (selected.getValue().getBloq()) return;

            int parent = -1;
            try {parent = selected.getParent().getValue().getId();} catch (Exception e) {}
            if (parent > -1) {
                TreeItem<pagadores> result = searchTreeItem(boletas.getRoot(), parent);
                if (result != null) {
                    result.getChildren().addAll(copyItem(selected));
                    BoletasBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                } else {
                    TreeItem<pagadores> newBanco = new TreeItem(new pagadores(parent,"","","", false));
                    newBanco.setExpanded(true);
                    boletas.getRoot().getChildren().add(newBanco);

                    // Expandir
                    boletas.getRoot().setExpanded(true);

                    TreeItem<pagadores> newresult = searchTreeItem(boletas.getRoot(), parent);
                    if (newresult != null) {
                        newresult.getChildren().addAll(copyItem(selected));
                        BoletasBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                    }
                }
            }

            TreeItem<pagadores> seachNode = searchTreeItem(BoletasBanco.getRoot(), parent);
            if (seachNode != null) {
                if (seachNode.getChildren().isEmpty()) {
                    delTreeItem(BoletasBanco.getRoot(),parent);
                }
            }
        }
        BoletasBanco.getSelectionModel().clearSelection();
    }

    private void BtnIn_Multiplos() {
        ObservableList<TreeItem<pagadores>> selecteds =  BoletasBanco.getSelectionModel().getSelectedItems();

        for (TreeItem<pagadores> selected : selecteds) {
            if (selected != BoletasBanco.getRoot()) {
                int parent = -1;
                try {
                    parent = selected.getParent().getValue().getId();
                } catch (Exception e) {
                }
                if (parent > -1) {
                    TreeItem<pagadores> result = searchTreeItem(boletas.getRoot(), parent);
                    if (result != null) {
                        result.getChildren().addAll(copyItem(selected));
                        BoletasBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                    } else {
                        TreeItem<pagadores> newBanco = new TreeItem(new pagadores(parent, "", "", "", false));
                        boletas.getRoot().getChildren().add(newBanco);
                        TreeItem<pagadores> newresult = searchTreeItem(boletas.getRoot(), parent);
                        if (newresult != null) {
                            newresult.getChildren().addAll(copyItem(selected));
                            BoletasBanco.getSelectionModel().getSelectedItem().getParent().getChildren().remove(selected);
                        }
                    }
                }

                TreeItem<pagadores> seachNode = searchTreeItem(BoletasBanco.getRoot(), parent);
                if (seachNode != null) {
                    if (seachNode.getChildren().isEmpty()) {
                        delTreeItem(BoletasBanco.getRoot(), parent);
                    }
                }
            }
        }
    }

    public static class pagadores {
        private SimpleIntegerProperty id;
        private SimpleStringProperty contrato;
        private SimpleStringProperty nome;
        private SimpleStringProperty vencto;
        private SimpleBooleanProperty bloq;

        public pagadores(int id, String contrato, String nome, String vencto, boolean bloq) {
            this.id = new SimpleIntegerProperty(id);
            this.contrato = new SimpleStringProperty(contrato);
            this.nome = new SimpleStringProperty(nome);
            this.vencto = new SimpleStringProperty(vencto);
            this.bloq = new SimpleBooleanProperty(bloq);
        }

        public int getId() { return id.get(); }
        public SimpleIntegerProperty idProperty() { return id; }
        public void setId(int id) { this.id.set(id); }

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

    public class dadosBoletaRemessa {
        private SimpleStringProperty banco;
        private SimpleStringProperty lote;
        private SimpleStringProperty nnumero;
        private SimpleStringProperty arquivo;

        public dadosBoletaRemessa(String banco, String lote, String arquivo, String nnumero) {
            this.banco = new SimpleStringProperty(banco);
            this.lote = new SimpleStringProperty(lote);
            this.arquivo = new SimpleStringProperty(arquivo);
            this.nnumero = new SimpleStringProperty(nnumero);
        }

        public String getBanco() { return banco.get(); }
        public SimpleStringProperty bancoProperty() { return banco; }
        public void setBanco(String banco) { this.banco.set(banco); }

        public String getLote() { return lote.get(); }
        public SimpleStringProperty loteProperty() { return lote; }
        public void setLote(String lote) { this.lote.set(lote); }

        public String getNnumero() { return nnumero.get(); }
        public SimpleStringProperty nnumeroProperty() { return nnumero; }
        public void setNnumero(String nnumero) { this.nnumero.set(nnumero); }

        public String getArquivo() { return arquivo.get(); }
        public SimpleStringProperty arquivoProperty() { return arquivo; }
        public void setArquivo(String arquivo) { this.arquivo.set(arquivo); }
    }

    private class AddRemessaCell extends TableCell<dadosBoletaRemessa, Boolean> {
        final Button sendButton       = new Button("Enviar");
        final Button consButton       = new Button("Consolidar");
        final StackPane paddedButton = new StackPane();

        /**
         * AddPersonCell constructor
         * @param table the table to which a new remessa can be added.
         */
        AddRemessaCell(final TableView table) {
            paddedButton.setPadding(new Insets(3,3,3,3));
            HBox pane = new HBox(sendButton, consButton);
            paddedButton.getChildren().add(pane);

            sendButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    openWebpage(table);
                }
            });

            consButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    ConsolidaRemessa();
                }
            });

        }

        private void ConsolidaRemessa() {
            try {
                int nLote = Integer.parseInt(boleta_remessa.getSelectionModel().getSelectedItem().getLote());
                int nBanco = Integer.parseInt(boleta_remessa.getSelectionModel().getSelectedItem().getBanco());
                String nNumero = boleta_remessa.getSelectionModel().getSelectedItem().getNnumero().replace(".0","");

                System.out.println("Banco: " + nBanco + " / Lote: " + nLote + " / NNumero: " + nNumero);
                //UpGradeBancoLote(nBanco, nLote, nNumero);

                boleta_remessa.getSelectionModel().setCellSelectionEnabled(false);
                tabEmail.setDisable(false);
                tabHand.setDisable(false);
                tabEtc.setDisable(false);
                setDisable(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Você deve selecionar a Remessa primeiro!");
            }
        }

        /** coloca um botão na linha se a mesma não for vazia */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
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

    private void openWebpage(TableView table) {
        String nbanco = ((dadosBoletaRemessa) table.getItems().get(0)).getBanco();
        String site = getSiteBanco(nbanco);
        try {
            try {
                new ProcessBuilder("x-www-browser", site).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
