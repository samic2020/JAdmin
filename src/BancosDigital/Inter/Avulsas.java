/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import Administrador.BancoBoleta;
import Bancos.DadosBanco;
import Bancos.Pagador;
import Bancos.RedeBancaria.Banco;
import Calculos.AvisosMensagens;
import Funcoes.AutoCompleteComboBoxListener;
import Funcoes.Dates;
import Funcoes.DbMain;
import static Funcoes.FuncoesGlobais.StrZero;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import Funcoes.Pad;
import Funcoes.VariaveisGlobais;
import boleta.Boleta;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;
import pdfViewer.PdfViewer;

/**
 *
 * @author Samic
 */
public class Avulsas implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String codErro;
    private String msgErro;

    @FXML private RadioButton tipoProp;
    @FXML private ToggleGroup tipo;
    @FXML private RadioButton tipoLoca;
    @FXML private RadioButton tipoAvulsa;

    @FXML private ComboBox<classCodDesc> codigo;
    @FXML private ComboBox<classDescCod> descricao;
    @FXML private TextField cpfcnpj;
    @FXML private TextField nome;
    @FXML private TextField endereco;
    @FXML private TextField numero;
    @FXML private TextField complemento;
    @FXML private TextField bairro;
    @FXML private TextField cidade;
    @FXML private TextField estado;
    @FXML private TextField cep;
    @FXML private HTMLEditor historico;

    @FXML private Button btnLimpa;
    @FXML private ToggleButton btnGuarda;
    @FXML private Button btnExclui;

    @FXML private DatePicker vencimento;
    @FXML private TextField valor;

    @FXML private Button btnEnviar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            FillCombo("SELECT p_rgprp codigo, p_nome nome, p_cpfcnpj cpfcnpj, p_end endereco, p_num numero, p_compl completo, p_bairro bairro, p_cidade cidade, p_estado estado, p_cep cep, p_email email FROM proprietarios ORDER BY p_rgprp;");
            btnLimpa.setDisable(true);
            btnGuarda.setDisable(true);
            btnExclui.setDisable(true);
        }); 
        
        MaskFieldUtil.monetaryField(valor);
        
        btnEnviar.disableProperty().bind(Bindings.createBooleanBinding(
            () -> nome.getText().isEmpty() || cpfcnpj.getText().isEmpty() || (vencimento.getValue() == null) || (LerValor.StringToFloat(valor.getText()) == 0),
            nome.textProperty(), cpfcnpj.textProperty(), vencimento.valueProperty(), valor.textProperty()
        ));
        btnEnviar.setOnAction((event) -> {
            DadosBanco bancos = new DadosBanco();
            
            int banco = 77;
            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }
            
            // Dados Bancários
            bancos.setBanco(banco);
            bancos.setBancoDv(new Banco().CalcDigBancoMod11(StrZero(String.valueOf(banco),3)));
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
            try {bancos.setBenef_Razao((!VariaveisGlobais.pb_razao_noprint) ? conn.LerParametros("da_razao") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Fantasia(conn.LerParametros("da_fanta"));} catch (SQLException e) {}
            try {bancos.setBenef_CNPJ((!VariaveisGlobais.pb_cnpj_noprint) ? conn.LerParametros("da_cnpj") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Inscricao(conn.LerParametros("da_insc"));} catch (SQLException e) {}
            try {bancos.setBenef_InscTipo(conn.LerParametros("da_tipo"));} catch (SQLException e) {}
            try {bancos.setBenef_Endereco((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_ender") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Numero((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_numero") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Complto((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cplto") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Bairro((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_bairro") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Cidade((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cidade") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Estado((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_estado") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Cep((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cep") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Telefone((!VariaveisGlobais.pb_telefone_noprint) ? conn.LerParametros("da_tel") : "");} catch (SQLException e) {}
            try {bancos.setBenef_Email(conn.LerParametros("da_email"));} catch (SQLException e) {}
            try {bancos.setBenef_HPage(conn.LerParametros("da_hpage"));} catch (SQLException e) {}
            // Não esquecer de dimensionar o logo pb_logo_width/pb_logo_height
            try {bancos.setBenef_Logo(new Image(new File(conn.LerParametros("da_logo")).toURI().toString()));} catch (SQLException e) {}
            // Parametros da QCR
                        
            // Dados do Locatario
            Pagador pagador = new Pagador();
            pagador.setBanco(banco);
            pagador.setCodigo(codigo.getEditor().getText()); // Registro do Proprietário
            pagador.setRazao(nome.getText());
            pagador.setFantasia(nome.getText());
            pagador.setCNPJ(cpfcnpj.getText());
            pagador.setEndereco(endereco.getText());
            pagador.setNumero(numero.getText());
            pagador.setComplto(complemento.getText());
            pagador.setBairro(bairro.getText());
            pagador.setCidade(cidade.getText());
            pagador.setEstado(estado.getText());
            pagador.setCep(cep.getText());
            pagador.setTelefone("");
            //pagador.setEmail(dadosLoc.getEmail());
            //pagador.setEnvio(dadosLoc.getEnvio());

            pagador.setRc_Codigo(codigo.getEditor().getText());
            pagador.setRc_NNumero("");
            pagador.setRc_DtDocumento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
            pagador.setRc_NumDocumento("AVULSO");
            pagador.setRc_DtProcessamento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
            pagador.setRc_Vencimento(Dates.DateFormata("dd-MM-yyyy", Dates.toDate(vencimento.getValue())));

            pagador.setRc_Dados(new String[][] {}); // {{cod, desc, cp, valor}, ...}
            pagador.setRc_mensagem("");

            pagador.setRc_instrucao01("");
            pagador.setRc_instrucao02("");
            pagador.setRc_instrucao03("");
            pagador.setRc_instrucao04("");
            pagador.setRc_instrucao05("");
            pagador.setRc_instrucao06("");
            pagador.setRc_instrucao07("");
            pagador.setRc_instrucao08("");
            pagador.setRc_instrucao09("");
            pagador.setRc_instrucao10("");

            pagador.setRc_linhaDIgitavel("");
            pagador.setRc_codigoBarras("");

            pagador.setRc_Valor(valor.getText());            

            String[][] dados = {{String.valueOf(-1), historico.getHtmlText(), "", valor.getText()}};
            pagador.setRc_Dados(dados);

            pagador.setRc_instrucao09("Não Receber após o vencimento.");
            pagador.setRc_instrucao10("Após o Vencimento somente na Imobiliária.");            
            
            Pagador[] pagadors = new Pagador[] {pagador};
            bancos.setBenef_pagadores(pagadors);
                                
            Boleta Bean1 = null;
//            try {
                Bean1 = CreateBoleta(bancos);

                if (Bean1 != null) {
                    // Gravar no arquivo Boletas
/*
                    String cSql = "INSERT INTO bloquetos (`rgprp`,`rgimv`,`contrato`," +
                            "`nome`,`vencimento`,`valor`,`nnumero`,`remessa`) " +
                            "VALUES (\"&1.\",\"&2.\",\"&3.\",\"&4.\",\"&5.\",\"&6.\",\"&7.\",\"&8.\")";
                    cSql = FuncoesGlobais.Subst(cSql, new String[] {
                            rgprp,
                            rgimv,
                            contrato,
                            Bean1.getsacDadosNome(),
                            Dates.DateFormata("yyyy-MM-dd", vencto),
                            Bean1.getbolDadosVrdoc(),
                            Bean1.getbolDadosNnumero(),
                            "S"
                    });
                    try {
                        if (conn.ExisteTabelaBloquetos()) conn.ExecutarComando(cSql);
                    } catch (Exception e) {e.printStackTrace();}
*/

                    List<Boleta> lista = new ArrayList<Boleta>();
                    lista.add(Bean1);

                    String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos_avulsas", null);
                    // new toPrint(pdfName,"LASER","INTERNA");

                    new PdfViewer("Preview Boleta Avulsa", pdfName);        
                } else {
                    //bancosErros.add(new BancosErros(banco.getBenef_Codigo(),banco.getBenef_Razao(), Dates.DateFormata("dd-MM-yyyy", Dates.toDate(Dates.toLocalDate(new Date()))), codErro, msgErro)); //*
                }
//            } catch (SQLException ex) {}
                    
        });
        
        btnLimpa.setOnAction((event) -> { 
            FillFields(null,false); 
            codigo.getSelectionModel().select(-1);
            descricao.getSelectionModel().select(-1);
        });
        
        btnExclui.setOnAction((event) -> {
            int pos = codigo.getSelectionModel().getSelectedIndex();
            if (pos > -1) {
                String deleteSQL = "DELETE FROM avulsas WHERE id = ?";
                conn.ExecutarComando(deleteSQL, new Object[][] {{"int", Integer.valueOf(codigo.getEditor().getText())}});
                FillCombo("SELECT id codigo, nome descricao, cpfcnpj, endereco, numero, cplto completo, bairro, cidade, estado, cep, email FROM avulsas ORDER BY Upper(nome);");                
                nome.requestFocus();
            }            
        });
        
        tipo.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            btnGuarda.disableProperty().unbind();
            btnExclui.disableProperty().unbind();
            if (newValue.toString().contains("Proprietarios")) {
                FillCombo("SELECT p_rgprp codigo, p_nome nome, p_cpfcnpj cpfcnpj, p_end endereco, p_num numero, p_compl completo, p_bairro bairro, p_cidade cidade, p_estado estado, p_cep cep, p_email email FROM proprietarios ORDER BY p_rgprp;");                
                btnLimpa.setDisable(true);
                btnGuarda.setDisable(true);
                btnExclui.setDisable(true);
                
                codigo.setDisable(false);
                descricao.setDisable(false);
                codigo.requestFocus();
            } else if (newValue.toString().contains("Locatarios")) { 
                FillCombo("SELECT l_contrato codigo, CASE WHEN l_fisjur then l_f_nome else l_j_razao END nome, l_cpfcnpj cpfcnpj, " +
                "CASE WHEN l_fisjur then l_f_endereco else l_j_endereco END endereco, " +
                "CASE WHEN l_fisjur then l_f_numero else l_j_numero END numero, " +
                "CASE WHEN l_fisjur then l_f_cplto else l_j_cplto END completo, " +
                "CASE WHEN l_fisjur then l_f_bairro else l_j_bairro END bairro, " +
                "CASE WHEN l_fisjur then l_f_cidade else l_j_cidade END cidade, " +
                "CASE WHEN l_fisjur then l_f_estado else l_j_estado END estado, " +
                "CASE WHEN l_fisjur then l_f_cep else l_j_cep END cep, " +
                "CASE WHEN l_fisjur then l_f_email else l_j_email END email " +
                "FROM locatarios ORDER BY l_contrato::int");
                btnLimpa.setDisable(true);
                btnGuarda.setDisable(true);
                btnExclui.setDisable(true);

                codigo.setDisable(false);
                descricao.setDisable(false);
                codigo.requestFocus();
            } else {
                FillCombo("SELECT id codigo, nome, cpfcnpj, endereco, numero, cplto completo, " + 
                          "bairro, cidade, estado, cep, email FROM avulsas WHERE lista ORDER BY Upper(nome);");
                FillFields(null, false);
                btnLimpa.setDisable(false);
                btnGuarda.disableProperty().bind(codigo.valueProperty().isNull().not());            
                btnExclui.disableProperty().bind(codigo.valueProperty().isNull());                
                
                codigo.setDisable(false);
                descricao.setDisable(false);
                nome.requestFocus();
            }
        });
        
    }
    
    private void FillCombo(String selectSQL) {
        codigo.getItems().clear();
        descricao.getItems().clear();

        if (selectSQL == null) return;
        
        FillFields(null, true);
        
        ArrayList<classCodDesc> combocod = new ArrayList<>();
        ArrayList<classDescCod> combodesc = new ArrayList<>();
        try (ResultSet rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY)) {
            while (rs.next()) {
                combocod.add(new classCodDesc(
                        rs.getString("codigo"), 
                        rs.getString("nome"), 
                        rs.getString("cpfcnpj"),
                        rs.getString("endereco"), 
                        rs.getString("numero"),
                        rs.getString("completo"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("cep"),
                        rs.getString("email")
                ));
                combodesc.add(new classDescCod(rs.getString("codigo"), rs.getString("nome")));
            }            
        } catch (SQLException e) {} 
        codigo.setItems(FXCollections.observableArrayList(combocod));
                
        Callback cd = new Callback<ListView<classCodDesc>,ListCell<classCodDesc>>(){
                @Override
                public ListCell<classCodDesc> call(ListView<classCodDesc> l){
                    return new ListCell<classCodDesc>(){

                        private final HBox hbx;
                        private final Label codigo;
                        private final Label sep;
                        private final Label nome;

                        {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            codigo = new Label(); codigo.setTextFill(Color.DARKBLUE);
                            codigo.setPrefWidth(40); codigo.setPrefHeight(25); codigo.setAlignment(Pos.CENTER_RIGHT);

                            sep = new Label(" - ");
                            sep.setPrefWidth(20); sep.setPrefHeight(25); sep.setAlignment(Pos.CENTER);

                            nome = new Label(); nome.setTextFill(Color.DARKGREEN);
                            nome.setPrefWidth(180); nome.setPrefHeight(25); nome.setAlignment(Pos.CENTER_LEFT);

                            hbx = new HBox(codigo, sep, nome);
                        }

                        @Override
                        protected void updateItem(classCodDesc item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                codigo.setText(item.getCodigo());
                                nome.setText(item.getDescricao());
                                setGraphic(hbx);
                            }
                        }
                    } ;
                }
            };
            codigo.setCellFactory(cd);        
        new AutoCompleteComboBoxListener<classCodDesc>(codigo);
        codigo.focusedProperty().addListener((observable) -> {
            int pos = codigo.getSelectionModel().getSelectedIndex();
            descricao.getSelectionModel().select(pos);
            
            if (pos > -1) {
                classCodDesc cod = codigo.getSelectionModel().getSelectedItem();
                FillFields(cod, true);
            }
        });

        descricao.setItems(FXCollections.observableArrayList(combodesc));
        Callback dc = new Callback<ListView<classDescCod>,ListCell<classDescCod>>(){
                @Override
                public ListCell<classDescCod> call(ListView<classDescCod> l){
                    return new ListCell<classDescCod>(){

                        private final HBox hbx;
                        private final Label codigo;
                        private final Label sep;
                        private final Label nome;

                        {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            codigo = new Label(); codigo.setTextFill(Color.DARKBLUE);
                            codigo.setPrefWidth(40); codigo.setPrefHeight(25); codigo.setAlignment(Pos.CENTER_RIGHT);

                            sep = new Label(" - ");
                            sep.setPrefWidth(20); sep.setPrefHeight(25); sep.setAlignment(Pos.CENTER);

                            nome = new Label(); nome.setTextFill(Color.DARKGREEN);
                            nome.setPrefWidth(180); nome.setPrefHeight(25); nome.setAlignment(Pos.CENTER_LEFT);

                            hbx = new HBox(codigo, sep, nome);
                        }

                        @Override
                        protected void updateItem(classDescCod item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                codigo.setText(item.getCodigo());
                                nome.setText(item.getDescricao());
                                setGraphic(hbx);
                            }
                        }
                    } ;
                }
            };
            descricao.setCellFactory(dc);        
            new AutoCompleteComboBoxListener<>(descricao);
            descricao.focusedProperty().addListener((observable) -> {
                int pos = descricao.getSelectionModel().getSelectedIndex();
                codigo.getSelectionModel().select(pos);

                if (pos > -1) {
                    classCodDesc cod = codigo.getSelectionModel().getSelectedItem();
                    FillFields(cod, true);
                }
            });
    }

    private void FillFields(classCodDesc cod, boolean disable) {
        try { nome.setText(cod.getDescricao()); } catch (NullPointerException e) { nome.setText(""); }
        try { endereco.setText(cod.getEndereco()); } catch (NullPointerException e) { endereco.setText(""); }
        try { cpfcnpj.setText(cod.getCpfcnpj()); } catch (NullPointerException e) { cpfcnpj.setText(""); }
        try { numero.setText(cod.getNumero()); } catch (NullPointerException e) { numero.setText(""); }
        try { complemento.setText(cod.getComplto()); } catch (NullPointerException e) { complemento.setText(""); }
        try { bairro.setText(cod.getBairro()); } catch (NullPointerException e) { bairro.setText(""); }
        try { cidade.setText(cod.getCidade()); } catch (NullPointerException e) { cidade.setText(""); }
        try { estado.setText(cod.getEstado()); } catch (NullPointerException e) { estado.setText(""); }
        try { cep.setText(cod.getCep()); } catch (NullPointerException e) { cep.setText(""); }

        nome.setDisable(disable); endereco.setDisable(disable); numero.setDisable(disable);
        complemento.setDisable(disable); bairro.setDisable(disable); cidade.setDisable(disable);
        estado.setDisable(disable); cep.setDisable(disable); cpfcnpj.setDisable(disable);        
    }
    
    private Boleta CreateBoleta(DadosBanco banco) {
        bancos bcos = new bancos("077");
        classInter pagador = new classInter();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<>();

        List<Boleta> lista = new ArrayList<>();
        Pagador[] pagadores = banco.getBenef_pagadores();
        Boleta beans = null;
        for (Pagador paga : pagadores) {
            //if (!EstaNaGrade(boleta, paga.getRazao(),true)) continue;

            beans = new Boleta();
            beans.setempNome(banco.getBenef_Razao());
            beans.setempEndL1(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Endereco() + ", " + banco.getBenef_Numero() + " " + banco.getBenef_Complto() + " - " + banco.getBenef_Bairro() : "");
            beans.setempEndL2(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Cidade() + " - " + banco.getBenef_Estado() + " - CEP " + banco.getBenef_Cep() : "");
            beans.setempEndL3(!banco.getBenef_Telefone().equalsIgnoreCase("") ? "Tel/Fax.: " + banco.getBenef_Telefone() : "");
            beans.setempEndL4(banco.getBenef_HPage() + " / " + banco.getBenef_Email());

            beans.setbolDadosCedente(new Pad(banco.getBenef_Razao(),50).RPad() + (!VariaveisGlobais.pb_cnpj_noprint ? "CNPJ: " + banco.getBenef_CNPJ() : ""));
            pagador.setCnpjCPFBeneficiario(banco.getBenef_CNPJ());

            beans.setlogoBanco(System.getProperty("user.dir") + "/resources/logoBancos/" + StrZero(String.valueOf(banco.getBanco()), 3) + ".jpg");
            beans.setnumeroBanco(StrZero(String.valueOf(banco.getBanco()), 3) + "-" + banco.getBancoDv());

            // Logo da Imobiliaria
            beans.setlogoLocation(banco.getBenef_Logo().impl_getUrl());

            beans.setbcoMsgL01(banco.getBanco_LocalPagamentoLinha1());
            beans.setbcoMsgL02(banco.getBanco_LocalPagamentoLinha2());

            boolean maniv = new AvisosMensagens().VerificaAniLocatario(paga.getRc_NumDocumento());
            beans.setlocaMsgL01(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
            pagador.setMensagem_linha4(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");

            Object[] mreaj = null;
            try {
                mreaj = new AvisosMensagens().VerificaReajuste(paga.getRc_NumDocumento());
            } catch (NullPointerException nex) {}
            beans.setlocaMsgL02((Boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");
            pagador.setMensagem_linha1((Boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");
            //pagador.setMensagem_linha1("Calculos feitos em referencia a data original de Vencimento " + paga.getRc_Vencimento());

            beans.setlocaMsgL03("");
            beans.setlocaMsgL04("");

            // Dados do Boleto
            int linha = 1;
            for (int i = 0; i <= paga.getRc_Dados().length - 1; i++) {
                locaDesc(beans, paga.getRc_Dados()[i], linha++);
            }

            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!"); return null; }

            String vrBoleta = paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];
            if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);
            beans.setbolDadosValor(vrBoleta);

            beans.setbolDadosVencimento(paga.getRc_Vencimento());
            pagador.setDataVencimento(paga.getRc_Vencimento());

            beans.setbolDadosVrdoc(vrBoleta);
            pagador.setValorNominal(vrBoleta.replace(".","").replace(",", "."));
            pagador.setValorAbatimento("0");

            beans.setbolDadosDatadoc(paga.getRc_DtDocumento());

            beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
            pagador.setDataEmissao(paga.getRc_DtProcessamento());

            beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
            pagador.setSeuNumero(paga.getRc_NumDocumento());  //*

            beans.setbolDadosEspecie(banco.getBanco_Especie());
            beans.setbolDadosEspeciedoc(banco.getBanco_EspecieDoc());
            beans.setbolDadosAceite(banco.getBanco_Aceite());
            beans.setbolDadosCarteira(banco.getBanco_Carteira());
            beans.setbolDadosAgcodced(banco.getBanco_Agencia() + "/" + banco.getBanco_CodBenef() + "-" + banco.getBanco_CodBenefDv() );

            // Dados do Pagador
            beans.setsacDadosNome(paga.getRazao());
            pagador.setNome(paga.getRazao());

            beans.setsacDadosEndereco(paga.getEndereco());
            pagador.setEndereco(paga.getEndereco());

            beans.setsacDadosNumero(paga.getNumero());
            pagador.setNumero(paga.getNumero());

            beans.setsacDadosCompl(paga.getComplto());
            pagador.setComplemento(paga.getComplto() != null ? paga.getComplto() : "");

            beans.setsacDadosBairro(paga.getBairro());
            pagador.setBairro(paga.getBairro());

            beans.setsacDadosCidade(paga.getCidade());
            pagador.setCidade(paga.getCidade());

            beans.setsacDadosEstado(paga.getEstado());
            pagador.setUf(paga.getEstado());

            beans.setsacDadosCep(paga.getCep());
            pagador.setCep(paga.getCep());

            String _cpfcnpj = paga.getCNPJ().length() < 14 ? "CPF: " + paga.getCNPJ() : "CNPJ: " + paga.getCNPJ();
            beans.setsacDadosCpfcnpj(_cpfcnpj);
            pagador.setCnpjCpf(paga.getCNPJ());
            pagador.setTipopessoa(pagador.getCnpjCpf().length() == 11 ? pagador.inter_PAGADOR_TIPOPESSOA_FISICA : pagador.inter_PAGADOR_TIPOPESSOA_JURIDICA);

            beans.setbolDadosMsg01(MsgChanges(VariaveisGlobais.pb_int01, paga));

            beans.setbolDadosMsg02(MsgChanges(VariaveisGlobais.pb_int02, paga));
            pagador.setMensagem_linha2(MsgChanges(VariaveisGlobais.pb_int04, paga));

            beans.setbolDadosMsg03(MsgChanges(VariaveisGlobais.pb_int03, paga));
            pagador.setMensagem_linha3(MsgChanges(VariaveisGlobais.pb_int05, paga));

            beans.setbolDadosMsg04(MsgChanges(VariaveisGlobais.pb_int04, paga));
            pagador.setMensagem_linha4(MsgChanges(VariaveisGlobais.pb_int08, paga));

            beans.setbolDadosMsg05(MsgChanges(VariaveisGlobais.pb_int05, paga));
            pagador.setMensagem_linha5(MsgChanges(VariaveisGlobais.pb_int09, paga));

            beans.setbolDadosMsg06(MsgChanges(VariaveisGlobais.pb_int06, paga));
            beans.setbolDadosMsg07(MsgChanges(VariaveisGlobais.pb_int07, paga));
            beans.setbolDadosMsg08(MsgChanges(VariaveisGlobais.pb_int08, paga));
            beans.setbolDadosMsg09(MsgChanges(VariaveisGlobais.pb_int09, paga));
            beans.setbolDadosMsg10(MsgChanges(VariaveisGlobais.bol_txbanc ? "Incluso Tarifa bancária $F{TXBANCO}" : "", paga));

            pagador.setDesconto1_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto1_data("");
            pagador.setDesconto1_taxa("0");
            pagador.setDesconto1_valor("0");

            pagador.setDesconto2_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto2_data("");
            pagador.setDesconto2_taxa("0");
            pagador.setDesconto2_valor("0");

            pagador.setDesconto3_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto3_data("");
            pagador.setDesconto3_taxa("0");
            pagador.setDesconto3_valor("0");

            beans.setbolDadosMora("");

            // Multa
            pagador.setMulta_codigoMulta(pagador.inter_MULTA_PERCENTUAL);
            pagador.setMulta_data(Dates.DateFormata("dd/MM/yyyy",
                    Dates.DateAdd(Dates.DIA, 1, Dates.StringtoDate(paga.getRc_Vencimento(),"dd-MM-yyyy"))));

            pagador.setMulta_taxa(String.valueOf(10));
            pagador.setMulta_valor("0");

            // Mora
            pagador.setMora_codigoMora(pagador.inter_MORA_TAXAMENSAL);
            pagador.setMora_data(Dates.DateFormata("dd/MM/yyyy",
                    Dates.DateAdd(Dates.DIA, 1, Dates.StringtoDate(paga.getRc_Vencimento(),"dd-MM-yyyy"))));
            pagador.setMora_taxa(String.valueOf(0.033));
            pagador.setMora_valor("0");

            pagador.setDataLimite(pagador.inter_NUNDIASAGENDA_TRINTA);
            pagador.setNumDiasAgenda(pagador.inter_NUNDIASAGENDA_TRINTA);

            /*
            / Registrar boleta no banco
            */
            String url_ws = "https://apis.bancointer.com.br/openbanking/v1/certificado/boletos";
            String path = bcos.getBanco_CertPath();
            String path_crt = path + bcos.getBanco_CrtFile();
            String path_key = path + bcos.getBanco_KeyFile();

            boolean pegueiBanco = true;
            Inter c  = new Inter();
            Object[] message = null;
            try {
                message = c.insertBoleta(url_ws, path_crt, path_key, pagador.getJSONBoleto());
            } catch (Exception e) { pegueiBanco = false; }

            codErro = c.getCodErro();
            msgErro = c.getMsgErro();

            if (!pegueiBanco) {
                bancosErros.add(new BancosErros(pagador.getSeuNumero(),pagador.getNome(), Dates.DateFormata("dd-MM-yyyy", Dates.toDate(Dates.toLocalDate(Dates.StringtoDate(pagador.getDataVencimento(),"dd-MM-yyyy")))), codErro, msgErro));
            }

            int statusCode = (int)message[0];
            String[] infoMessage = (String[])message[1];
            if ((int)message[0] != 200) {
                System.out.println("Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
                return null;
            } else {
                beans.setbolDadosNnumero(infoMessage[0]);
                beans.setcodDadosBarras(infoMessage[1]);
                beans.setcodDadosDigitavel(infoMessage[2]);
            }
        }

/*
        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos");
        // new toPrint(pdfName,"LASER","INTERNA");
        new PdfViewer("Preview do Boleto", pdfName);
*/

        return beans;
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
}
