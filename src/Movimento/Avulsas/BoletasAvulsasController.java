package Movimento.Avulsas;

import Administrador.BancoBoleta;
import Bancos.DadosBanco;
import Bancos.Pagador;
import Bancos.RedeBancaria.Banco;
import Bancos.RedeBancaria.Itau;
import Bancos.RedeBancaria.Santander;
import Funcoes.*;
import Movimento.Boletas.BoletasController;
import boleta.Boleta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;
import pdfViewer.PdfViewer;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static Funcoes.FuncoesGlobais.StrZero;

public class BoletasAvulsasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsCpfCnpj = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsCpfCnpj;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchCpfCnpj = true;
    private boolean isSearchNome = true;
    private int statSave = -1;   // -1 - nada; 0 - inserindo novo; 1 - atualizando

    @FXML private AnchorPane anchorPane;

    @FXML private Tab avuEmissao;

    @FXML private TextField avuNome;
    @FXML private TextField avuCPFCNPJ;
    @FXML private TextField avuRGINSC;
    @FXML private TextField avuEnder;
    @FXML private TextField avuNumero;
    @FXML private TextField avuCplto;
    @FXML private TextField avuBairro;
    @FXML private TextField avuCidade;
    @FXML private TextField avuEstado;
    @FXML private TextField avuCep;
    @FXML private TextField avuEmail;
    @FXML private TextArea avuDescricao;
    @FXML private TextField avuValor;
    @FXML private DatePicker avuVencimento;

    @FXML private ComboBox<String> avuBanco;

    @FXML private TextField avuMsg1;
    @FXML private TextField avuMsg2;
    @FXML private TextField avuMsg3;
    @FXML private Button avuBtnImprimir;

    @FXML private Tab avuControle;

    @FXML private TableView<BoletasAvulsas> avuBaixar;
    @FXML private TableColumn<BoletasAvulsas, Integer> avuBaixarId;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixarBanco;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixarNossoNumero;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixarNome;
    @FXML private TableColumn<BoletasAvulsas, Date> avuBaixarVencimento;
    @FXML private TableColumn<BoletasAvulsas, BigDecimal> avuBaixarValor;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixarDescricao;

    @FXML private TextField avuBaixarBuscar;
    @FXML private Label avuBaixarLimpar;

    @FXML private Spinner<String> avuMes;
    @FXML private Spinner<Integer> avuAno;
    @FXML private Button avuBaixadasBtnPesquisar;
    @FXML private Button avuBaixadasBtnImprimir;

    @FXML private TableView<BoletasAvulsas> avuBaixadas;
    @FXML private TableColumn<BoletasAvulsas, Integer> avuBaixadasId;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixadasBanco;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixadasNossoNumero;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixadasNome;
    @FXML private TableColumn<BoletasAvulsas, Date> avuBaixadasVencimento;
    @FXML private TableColumn<BoletasAvulsas, BigDecimal> avuBaixadasValor;
    @FXML private TableColumn<BoletasAvulsas, String> avuBaixadasDescricao;

    @FXML private TextField avuBaixadasBuscar;
    @FXML private Label avuBaixadasLimpar;

    ObservableList<String> months = FXCollections.observableArrayList(
            "Janeiro", "Fevereiro", "Março", "Abril",
            "Maio", "Junho", "Julho", "Agosto",
            "Setembro", "Outubro", "Novembro", "Dezembro");

    @Override public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(months);
        valueFactory.setValue(months.get(DbMain.getDateTimeServer().getMonth()));
        avuMes.setValueFactory(valueFactory);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(2019, 2050, DbMain.getDateTimeServer().getYear());
        valueFactory2.setValue(DbMain.getDateTimeServer().getYear());
        avuAno.setValueFactory(valueFactory2);

        avuBaixarLimpar.setOnMouseClicked(event -> avuBaixarBuscar.clear());
        avuBaixadasLimpar.setOnMouseClicked(event -> avuBaixadasBuscar.clear());

        MaskFieldUtil.maxField(avuNome, 60);
        MaskFieldUtil.maxField(avuCPFCNPJ, 18);
        MaskFieldUtil.maxField(avuRGINSC, 20);
        MaskFieldUtil.maxField(avuEnder, 60);
        MaskFieldUtil.maxField(avuNumero, 15);
        MaskFieldUtil.maxField(avuCplto, 20);
        MaskFieldUtil.maxField(avuBairro, 30);
        MaskFieldUtil.maxField(avuCidade, 30);
        MaskFieldUtil.maxField(avuEstado, 2);
        MaskFieldUtil.maxField(avuCep, 9);
        MaskFieldUtil.maxField(avuEmail, 60);

        MaskFieldUtil.cpfCnpjField(avuCPFCNPJ);
        MaskFieldUtil.numericField(avuNumero);
        MaskFieldUtil.cepField(avuCep);

        MaskFieldUtil.monetaryField(avuValor);
        avuVencimento.setValue(Dates.toLocalDate(DbMain.getDateTimeServer()));
        AutoCompletaEstados();

        Bancos();

        avuBtnImprimir.setOnAction(event -> {
            if (avuNome.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Nome é Obrigatório!").showAndWait();
                avuNome.requestFocus();
                return;
            }

            if (avuCPFCNPJ.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo CPF/CNPJ é Obrigatório!").showAndWait();
                avuCPFCNPJ.requestFocus();
                return;
            }

            if (avuEnder.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Endereço é Obrigatório!").showAndWait();
                avuEnder.requestFocus();
                return;
            }

            if (avuNumero.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Número é Obrigatório!").showAndWait();
                avuNumero.requestFocus();
                return;
            }

            if (avuBairro.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Bairro é Obrigatório!").showAndWait();
                avuBairro.requestFocus();
                return;
            }

            if (avuCidade.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Cidade é Obrigatório!").showAndWait();
                avuCidade.requestFocus();
                return;
            }

            if (avuEstado.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Estado é Obrigatório!").showAndWait();
                avuEstado.requestFocus();
                return;
            }

            if (avuCep.getText().trim().equalsIgnoreCase("")) {
                new Alert(Alert.AlertType.INFORMATION, "Campo Cep é Obrigatório!").showAndWait();
                avuCep.requestFocus();
                return;
            }

            if( avuBanco.getSelectionModel().getSelectedIndex() < 0) {
                new Alert(Alert.AlertType.INFORMATION, "Selecione um Banco!").showAndWait();
                avuBanco.requestFocus();
                return;
            }

            Avulsa(avuBanco.getSelectionModel().getSelectedItem().substring(0,3));

/*
            String insertSQL = "INSERT INTO avulsas(" +
                    "nome, cpfcnpj, rginsc, endereco, numero, cplto, bairro, cidade, " +
                    "estado, cep, email, vencimento, valor, banco, nnumero, descricao, dtemissão, logado_emissao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Object[][] param = new Object[][] {
                    {"string", StringManager.ConvStr(avuNome.getText())},
                    {"string", avuCPFCNPJ.getText()},
                    {"string", avuRGINSC.getText()},
                    {"string", StringManager.ConvStr(avuEnder.getText())},
                    {"string", avuNumero.getText()},
                    {"string", avuCplto.getText()},
                    {"string", StringManager.ConvStr(avuBairro.getText())},
                    {"string", StringManager.ConvStr(avuCidade.getText())},
                    {"string", avuEstado.getText().toUpperCase()},
                    {"string", avuCep.getText()},
                    {"string", avuEmail.getText().trim()},
                    {"date", avuVencimento.getValue()},
                    {"decimal", LerValor.Number2BigDecimal(avuValor.getText())},
                    {"string", avuBanco.getSelectionModel().getSelectedItem().substring(0,3)},
                    {"string",nnumero},
                    {"string", avuDescricao.getText().trim()},
                    {"date", new Date()},
                    {"string", VariaveisGlobais.usuario}
            };
            conn.ExecutarComando(insertSQL, param);
*/
        });

        // Criar tabela caso não exista
        CreateTableAvulsas();

        AutocompleteContratoNome();
    }

    private void Avulsa(String banco) {
        BancoBoleta dadosBanco = new Banco(null, null).LerBancoBoleta(StrZero(String.valueOf(banco), 3));
        if (dadosBanco == null) {
            System.out.println("Banco não cadastrado!");
            return;
        }

        int nbanco = Integer.valueOf(banco);
        DadosBanco bancos = new DadosBanco();

        // Dados Bancários
        bancos.setBanco(nbanco);
        bancos.setBancoDv(new Banco().CalcDigBancoMod11(StrZero(String.valueOf(nbanco), 3)));
        bancos.setBanco_NNumero(String.valueOf(dadosBanco.getNnumero()));
        bancos.setBanco_Aceite("N");
        bancos.setBanco_Agencia(dadosBanco.getAgencia());
        bancos.setBanco_AgenciaDv(String.valueOf(dadosBanco.getAgenciadv()));
        bancos.setBanco_Carteira(dadosBanco.getCarteira());
        bancos.setBanco_CodBenef(dadosBanco.getCedente());
        bancos.setBanco_CodBenefDv(String.valueOf(dadosBanco.getCedentedv()));
        bancos.setBanco_CodMoeda("9");
        bancos.setBanco_Conta(dadosBanco.getConta());
        bancos.setBanco_ContaDv(String.valueOf(dadosBanco.getContadv()));
        bancos.setBanco_Especie("R$");
        bancos.setBanco_EspecieDoc("REC");
        bancos.setBanco_TamanhoNnumero(17);
        bancos.setBanco_LocalPagamentoLinha1("Este boleto poderá ser pago em qualquer banco.".toUpperCase());
        bancos.setBanco_LocalPagamentoLinha2("Após o vencimento seguir intruções do Beneficiário.".toUpperCase());
        bancos.setBanco_Lote(dadosBanco.getLote());

        // Dados do Beneficiario - Imobiliária
        bancos.setBenef_Codigo("");
        try {
            bancos.setBenef_Razao((!VariaveisGlobais.pb_razao_noprint) ? conn.LerParametros("da_razao") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Fantasia(conn.LerParametros("da_fanta"));
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_CNPJ((!VariaveisGlobais.pb_cnpj_noprint) ? conn.LerParametros("da_cnpj") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Inscricao(conn.LerParametros("da_insc"));
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_InscTipo(conn.LerParametros("da_tipo"));
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Endereco((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_ender") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Numero((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_numero") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Complto((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cplto") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Bairro((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_bairro") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Cidade((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cidade") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Estado((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_estado") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Cep((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cep") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Telefone((!VariaveisGlobais.pb_telefone_noprint) ? conn.LerParametros("da_tel") : "");
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_Email(conn.LerParametros("da_email"));
        } catch (SQLException e) {
        }
        try {
            bancos.setBenef_HPage(conn.LerParametros("da_hpage"));
        } catch (SQLException e) {
        }
        // Não esquecer de dimensionar o logo pb_logo_width/pb_logo_height
        try {
            bancos.setBenef_Logo(new Image(new File(conn.LerParametros("da_logo")).toURI().toString()));
        } catch (SQLException e) {
        }
        // Parametros da QCR

        // Dados do Locatario
        Pagador[] pagador = new Pagador[1]; for (int i=0;i<1;i++) pagador[i] = new Pagador();
        pagador[0].setBanco(nbanco);
        pagador[0].setCodigo("000000");
        pagador[0].setRazao(StringManager.ConvStr(avuNome.getText()));
        pagador[0].setFantasia(StringManager.ConvStr(avuNome.getText()));
        pagador[0].setCNPJ(avuCPFCNPJ.getText());
        pagador[0].setEndereco(StringManager.ConvStr(avuEnder.getText()));
        pagador[0].setNumero(avuNumero.getText());
        pagador[0].setComplto(avuCplto.getText());
        pagador[0].setBairro(StringManager.ConvStr(avuBairro.getText()));
        pagador[0].setCidade(StringManager.ConvStr(avuCidade.getText()));
        pagador[0].setEstado(avuEstado.getText().toUpperCase());
        pagador[0].setCep(avuCep.getText());
        pagador[0].setTelefone("");
        pagador[0].setEmail(avuEmail.getText().trim());
        pagador[0].setEnvio("");

        pagador[0].setRc_Codigo("000000");
        pagador[0].setRc_NNumero("");
        pagador[0].setRc_DtDocumento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
        pagador[0].setRc_NumDocumento("AVULSO");
        pagador[0].setRc_DtProcessamento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
        pagador[0].setRc_Vencimento(Dates.StringtoString(avuVencimento.getValue().toString(), "yyyy/MM/dd","dd/MM/yyyy"));

        pagador[0].setRc_Dados(new String[][]{{"1",avuDescricao.getText().trim(),"-",avuValor.getText()}}); // {{cod, desc, cp, valor}, ...}
        pagador[0].setRc_mensagem("");

        pagador[0].setRc_instrucao01("");
        pagador[0].setRc_instrucao02("");
        pagador[0].setRc_instrucao03("");
        pagador[0].setRc_instrucao04("");
        pagador[0].setRc_instrucao05("");
        pagador[0].setRc_instrucao06("");
        pagador[0].setRc_instrucao07("");
        pagador[0].setRc_instrucao08("");
        pagador[0].setRc_instrucao09("");
        pagador[0].setRc_instrucao10("");

        pagador[0].setRc_linhaDIgitavel("");
        pagador[0].setRc_codigoBarras("");

        pagador[0].setRc_Valor(avuValor.getText());

        // Setar pagadores
        bancos.setBenef_pagadores(pagador);
        // Processa dados
        Object vbanco;
        switch (bancos.getBanco()) {
            case 1: // Bb
                break;
            case 341: // Itau
                vbanco = new Itau(bancos);
                ((Itau) vbanco).Processa();
                break;
            case 33: // Santander
                vbanco = new Santander(bancos);
                ((Santander) vbanco).ProcessaAvulsa();
                break;
            case 247: // Bradesco
                break;
        }

        BoletasController.dadosBoletaRemessa[] remessa = new BoletasController.dadosBoletaRemessa[0];
        switch (bancos.getBanco()) {
            case 1: // Bb
                break;
            case 341: // Itau
                Itau ibanco = new Itau(bancos);
                int bancoItau = bancos.getBanco();
                int loteItau = bancos.getBanco_Lote();
                String nnumeroItau = bancos.getBanco_NNumero();
                String pRemessaItau = ibanco.Remessa(String.valueOf(loteItau));

                // Alimenta view
                //remessa[0] = new BoletasController.dadosBoletaRemessa(String.valueOf(bancoItau), String.valueOf(loteItau), bancoItau + "_" + loteItau + ".rem", nnumeroItau);

                // Gera arquivo fisico
                StreamFile fillerItau = new StreamFile(new String[]{"TxRx/Remessa/Enviar/" + bancoItau + "_" + loteItau + ".rem"});
                if (fillerItau.Open()) {
                    fillerItau.Print(pRemessaItau);
                }
                fillerItau.Close();

                bancos.setBanco_Lote(loteItau++);

                break;
            case 33: // Santander
                Santander sbanco = new Santander(bancos);
                int loteSant = bancos.getBanco_Lote();
                int bancoSant = bancos.getBanco();
                String nnumeroSant = bancos.getBanco_NNumero();
                String pRemessaSant = sbanco.Remessa(String.valueOf(loteSant));

                // Gera arquivo fisico
                StreamFile fillerSant = new StreamFile(new String[]{"TxRx/Remessa/Enviar/" + bancoSant + "_" + loteSant + ".rem"});
                if (fillerSant.Open()) {
                    fillerSant.Print(pRemessaSant);
                }
                fillerSant.Close();

                bancos.setBanco_Lote(loteSant++);

                break;
            case 247: // Bradesco
                break;
        }

        List<Boleta> lista = new ArrayList<Boleta>();
        Pagador[] pagadores = bancos.getBenef_pagadores();
        for (Pagador paga : pagadores) {
            Boleta beans = new Boleta();
            beans.setempNome(bancos.getBenef_Razao());
            beans.setempEndL1(!bancos.getBenef_Endereco().equalsIgnoreCase("") ? bancos.getBenef_Endereco() + ", " + bancos.getBenef_Numero() + " " + bancos.getBenef_Complto() + " - " + bancos.getBenef_Bairro() : "");
            beans.setempEndL2(!bancos.getBenef_Endereco().equalsIgnoreCase("") ? bancos.getBenef_Cidade() + " - " + bancos.getBenef_Estado() + " - CEP " + bancos.getBenef_Cep() : "");
            beans.setempEndL3(!bancos.getBenef_Telefone().equalsIgnoreCase("") ? "Tel/Fax.: " + bancos.getBenef_Telefone() : "");
            beans.setempEndL4(bancos.getBenef_HPage() + " / " + bancos.getBenef_Email());

            beans.setbolDadosCedente(new Pad(bancos.getBenef_Razao(),50).RPad() + (!VariaveisGlobais.pb_cnpj_noprint ? "CNPJ: " + bancos.getBenef_CNPJ() : ""));
            beans.setlogoBanco(System.getProperty("user.dir") + "/resources/logoBancos/" + StrZero(String.valueOf(bancos.getBanco()), 3) + ".jpg");
            beans.setnumeroBanco(StrZero(String.valueOf(bancos.getBanco()), 3) + "-" + bancos.getBancoDv());

            // Logo da Imobiliaria
            beans.setlogoLocation(bancos.getBenef_Logo().impl_getUrl());

            beans.setbcoMsgL01(bancos.getBanco_LocalPagamentoLinha1());
            beans.setbcoMsgL02(bancos.getBanco_LocalPagamentoLinha2());

            beans.setlocaMsgL01("Boleto de Teste");
            beans.setlocaMsgL02("");
            beans.setlocaMsgL03("");
            beans.setlocaMsgL04("");

            // Dados do Boleto
            int linha = 1;
            for (int i = 0; i < paga.getRc_Dados().length - 1; i++) locaDesc(beans, paga.getRc_Dados()[i], linha++);

            String vrBoleta = paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];
            if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);

            beans.setbolDadosValor(vrBoleta);
            beans.setbolDadosVencimento(paga.getRc_Vencimento());
            beans.setbolDadosVrdoc(vrBoleta);
            beans.setbolDadosDatadoc(paga.getRc_DtDocumento());
            beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
            beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
            beans.setbolDadosEspecie(bancos.getBanco_Especie());
            beans.setbolDadosEspeciedoc(bancos.getBanco_EspecieDoc());
            beans.setbolDadosAceite(bancos.getBanco_Aceite());
            beans.setbolDadosCarteira(bancos.getBanco_Carteira());
            beans.setbolDadosAgcodced(bancos.getBanco_Agencia() + "/" + bancos.getBanco_CodBenef() + "-" + bancos.getBanco_CodBenefDv() );
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

            beans.setcodDadosBarras(paga.getRc_codigoBarras().substring(0, bancos.getBenef_pagadores()[0].getRc_codigoBarras().length() - 1));
            beans.setcodDadosDigitavel(paga.getRc_linhaDIgitavel());

            // Propaganda
            /////////////////beans.setPropaganda("resources/Boleta/propaganda.jpeg");

            lista.add(beans);
        }

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos_avulsas");
        // new toPrint(pdfName,"LASER","INTERNA");
        new PdfViewer("Preview do Boleto", pdfName);
    }

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

    private void Bancos() {
        List<String> bancos = new ArrayList<>();
        ResultSet bco = conn.AbrirTabela("SELECT numero, nome FROM bancos;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (bco.next()) {
                bancos.add(bco.getString("numero") + " - " + bco.getString("nome"));
            }
        } catch (SQLException sex) {}
        try {DbMain.FecharTabela(bco);} catch (Exception ex) {}

        avuBanco.setItems(FXCollections.observableArrayList(bancos));
    }

    private void AutoCompletaEstados() {
        List<String> Estados = new ArrayList<String>();
        Estados.add("AC");
        Estados.add("AL");
        Estados.add("AP");
        Estados.add("AM");
        Estados.add("BA");
        Estados.add("CE");
        Estados.add("DF");
        Estados.add("ES");
        Estados.add("GO");
        Estados.add("MA");
        Estados.add("MT");
        Estados.add("MS");
        Estados.add("MG");
        Estados.add("PA");
        Estados.add("PB");
        Estados.add("PR");
        Estados.add("PE");
        Estados.add("PI");
        Estados.add("RJ");
        Estados.add("RN");
        Estados.add("RS");
        Estados.add("RO");
        Estados.add("RR");
        Estados.add("SC");
        Estados.add("SP");
        Estados.add("SE");
        Estados.add("TO");

        TextFields.bindAutoCompletion(avuEstado, Estados);
    }

    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        // Aqui vai o SELECT para o campo
        String qSQL = "SELECT Upper(Trim(nome)) AS nome FROM avulsas ORDER BY Upper(Trim(nome));";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcpfcnpj = null, qnome = null;
                try { qnome = imv.getString("p_nome"); } catch (SQLException e) { }

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));
                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[]{null,qnome});
            }
        } catch (SQLException e) {
        }
        try { DbMain.FecharTabela(imv); } catch (Exception e) { }
        if (possibleSuggestionsNome != null) TextFields.bindAutoCompletion(avuNome, possibleSuggestionsNome);

        avuNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, avuNome.getText());} catch (Exception e) {}
                if (pos > -1 && isSearchNome) {
                    // Pega na tabela os dados
                    String _avuCPFCNPJ = null;
                    String _avuRGINS = null;
                    String _avuEnder = null;
                    String _avuNumero = null;
                    String _avuCplto = null;
                    String _avuBairro = null;
                    String _avuCidade = null;
                    String _avuEstado = null;
                    String _avuCep = null;

                    Object[][] dados = null;
                    try {
                        dados = conn.LerCamposTabela(new String[] {
                                "cpfcnpj",
                                "rginsc",
                                "endereco",
                                "numero",
                                "cplto",
                                "bairro",
                                "cidade",
                                "estado",
                                "cep"
                        }, "avulsas", "Upper(Trim(nome)) = '" + _possibleSuggestions[0][1] + "'");

                    } catch (SQLException sex) {}
                    if (dados != null) {
                        _avuCPFCNPJ = dados[0][3].toString();
                        _avuRGINS = dados[1][3].toString();
                        _avuEnder = dados[2][3].toString();
                        _avuNumero = dados[3][3].toString();
                        _avuCplto = dados[4][3].toString();
                        _avuBairro = dados[5][3].toString();
                        _avuCidade = dados[6][3].toString();
                        _avuEstado = dados[7][3].toString();
                        _avuCep = dados[8][3].toString();
                    }

                    // Complementa os campos quando nescessário
                    if (avuCPFCNPJ.getText().equalsIgnoreCase("")) {
                        avuCPFCNPJ.setText(_avuCPFCNPJ);
                    } else if (!avuCPFCNPJ.getText().equalsIgnoreCase(_avuCPFCNPJ)) {
                        avuCPFCNPJ.setText(_avuCPFCNPJ);
                    }

                    if (avuRGINSC.getText().equalsIgnoreCase("")) {
                        avuRGINSC.setText(_avuRGINS);
                    } else if (!avuRGINSC.getText().equalsIgnoreCase(_avuRGINS)) {
                        avuRGINSC.setText(_avuRGINS);
                    }

                    if (avuEnder.getText().equalsIgnoreCase("")) {
                        avuEnder.setText(_avuEnder);
                    } else if (!avuEnder.getText().equalsIgnoreCase(_avuEnder)) {
                        avuEnder.setText(_avuEnder);
                    }

                    if (avuNumero.getText().equalsIgnoreCase("")) {
                        avuNumero.setText(_avuNumero);
                    } else if (!avuNumero.getText().equalsIgnoreCase(_avuNumero)) {
                        avuNumero.setText(_avuNumero);
                    }

                    if (avuCplto.getText().equalsIgnoreCase("")) {
                        avuCplto.setText(_avuCplto);
                    } else if (!avuCplto.getText().equalsIgnoreCase(_avuCplto)) {
                        avuCplto.setText(_avuCplto);
                    }

                    if (avuBairro.getText().equalsIgnoreCase("")) {
                        avuBairro.setText(_avuBairro);
                    } else if (!avuBairro.getText().equalsIgnoreCase(_avuBairro)) {
                        avuBairro.setText(_avuBairro);
                    }

                    if (avuCidade.getText().equalsIgnoreCase("")) {
                        avuCidade.setText(_avuCidade);
                    } else if (!avuCidade.getText().equalsIgnoreCase(_avuCidade)) {
                        avuCidade.setText(_avuCidade);
                    }

                    if (avuEstado.getText().equalsIgnoreCase("")) {
                        avuEstado.setText(_avuEstado);
                    } else if (!avuEstado.getText().equalsIgnoreCase(_avuEstado)) {
                        avuEstado.setText(_avuEstado);
                    }

                    if (avuCep.getText().equalsIgnoreCase("")) {
                        avuCep.setText(_avuCep);
                    } else if (!avuCep.getText().equalsIgnoreCase(_avuCep)) {
                        avuCep.setText(_avuCep);
                    }
                } else {}
            }
        });

    }

    private void CreateTableAvulsas() {
        String sequenceSQL = "CREATE SEQUENCE IF NOT EXISTS avulsas_id_seq" +
                " INCREMENT 1" +
                " MINVALUE 1" +
                " MAXVALUE 9223372036854775807" +
                " START 1" +
                " CACHE 1;";
        String createSQL = "CREATE TABLE IF NOT EXISTS public.avulsas (" +
                "  id integer NOT NULL DEFAULT nextval('avulsas_id_seq'::regclass)," +
                "  nome character varying(60)," +
                "  cpfcnpj character varying(18)," +
                "  rginsc character varying(20)," +
                "  endereco character varying(60)," +
                "  numero character varying(15)," +
                "  cplto character varying(20)," +
                "  bairro character varying(30)," +
                "  cidade character varying(30)," +
                "  estado character varying(2)," +
                "  cep character varying(9)," +
                "  email character varying(60)," +
                "  vencimento date," +
                "  valor numeric(12,2)," +
                "  banco character varying(3)," +
                "  nnumero character varying(20)," +
                "  descricao text," +
                "  dtemissão date," +
                "  dtbaixa date," +
                "  valorbaixa numeric(12,2)," +
                "  logado_emissao character varying(30)," +
                "  logado_baixa character varying(30)," +
                "  CONSTRAINT avulsas_pkey PRIMARY KEY (id)) WITH (OIDS=FALSE); ALTER TABLE public.avulsas OWNER TO postgres;";
        conn.ExecutarComando(sequenceSQL);
        conn.ExecutarComando(createSQL);
    }
}
