/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import Classes.paramEvent;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Samic
 */
public class Consulta implements Initializable{
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private ComboBox<String> jTipoListagem;
    @FXML private DatePicker conDataInicial;
    @FXML private DatePicker conDataFinal;
    @FXML private Button conBtnListar;
    @FXML private Button jBtnBaixar;
    
    @FXML private TableView<ConsultaInter> conLista;
    @FXML private TableColumn<ConsultaInter, LocalDate> conLista_Emissao;
    @FXML private TableColumn<ConsultaInter, LocalDate> conLista_Vencimento;
    @FXML private TableColumn<ConsultaInter, LocalDate> conLista_Pagamento;
    @FXML private TableColumn<ConsultaInter, String> conLista_SeuNumero;
    @FXML private TableColumn<ConsultaInter, String> conLista_NossoNumero;
    @FXML private TableColumn<ConsultaInter, String> conLista_CpfCnpj;
    @FXML private TableColumn<ConsultaInter, String> conLista_Sacado;
    @FXML private TableColumn<ConsultaInter, BigDecimal> conLista_Multa;
    @FXML private TableColumn<ConsultaInter, BigDecimal> conLista_Juros;
    @FXML private TableColumn<ConsultaInter, BigDecimal> conLista_Valor;
    @FXML private TableColumn<ConsultaInter, String> conLista_Situacao;
    @FXML private TableColumn<ConsultaInter, String> conLista_B;

    @FXML private ProgressBar jProgressListaBoletasConsulta;
    @FXML private ProgressBar jProgressListaBoletasConsulta1;
    
    @FXML private TextField baiQuantidade;
    @FXML private TextField baiValor;
    @FXML private TextField recQuantidade;
    @FXML private TextField recValor;
    @FXML private TextField expQuantidade;
    @FXML private TextField expValor;
    @FXML private TextField preQuantidade;
    @FXML private TextField preValor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> listarTipos = FXCollections.observableArrayList(
                "TODOS", "VENCIDOSAVENCER", "EXPIRADOS",
                "PAGOS", "TODOSBAIXADOS"
        );
        jTipoListagem.setItems(listarTipos);
        Platform.runLater(() -> jTipoListagem.getSelectionModel().select(0));

        //conDataInicial.disableProperty().bind(jTipoListagem.valueProperty().isNull());
        //cPeriodoAte.disableProperty().bind(jTipoListagem.valueProperty().isNull());

        conDataInicial.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        conDataFinal.setValue(Dates.toLocalDate(Dates.ultDataMes(DbMain.getDateTimeServer())));

        conBtnListar.disableProperty().bind(conDataInicial.valueProperty().isNull().or(conDataFinal.valueProperty().isNull()));
        conBtnListar.setOnAction(event -> {
            try { ListaBoletasBanco(); } catch (Exception e) {}
        });
        
        jBtnBaixar.setOnAction((event) -> {
            List<classBaixa> lst = new ArrayList<>();
            BigDecimal total = new BigDecimal("0");
            for (ConsultaInter lista : conLista.getItems()) {
                if ("PAGO".indexOf(lista.getSituacao().toString().toUpperCase()) == -1) continue;
                lst.add(new classBaixa(
                        lista.getSeunumero(), 
                        lista.getSacado(), 
                        Dates.DateFormata("dd-MM-yyyy", Dates.toDate(lista.getPagamento())), 
                        Dates.DateFormata("dd-MM-yyyy", Dates.toDate(lista.getVencimento())), 
                        lista.getCpfcnpj(), 
                        lista.getNossonumero(), 
                        lista.getMulta(), 
                        lista.getJuros(), 
                        lista.getValor(),
                        true 
                ));
                total = total.add(lista.getValor());
            }    
            
            if (total.floatValue() != 0) WindowBaixa(lst);
        });
        
    }
    
    private void WindowBaixa(List<classBaixa> lst) {
        try {
            AnchorPane root = null;
            FXMLLoader loader = null;
            try {
                //loader = FXMLLoader.load(getClass().getResource("/BancosDigital/Inter/Baixa.fxml"));
                loader = new FXMLLoader(getClass().getResource("/BancosDigital/Inter/Baixa.fxml"));
            } catch (Exception e) {e.printStackTrace();}
            root = loader.load();
            UICustomComponent wrappedRoot = new UICustomComponent(root);

            Baixa controler = loader.<Baixa>getController();
            controler.setLista(lst);
            
            UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
            internalFrame.setLayout(new UIBorderLayout());
            internalFrame.setModal(true);
            internalFrame.setResizable(false);
            internalFrame.setMaximizable(false);
            internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
            internalFrame.setTitle(".:: Baixa no Sistema");
            //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
            internalFrame.setClosable(true);


            internalFrame.setBackground(new UIColor(103,165, 162));
            //internalFrame.setBackground(new UIColor(51,81, 135));

            internalFrame.pack();
            internalFrame.setVisible(true);

            //root.fireEvent(new paramEvent(new Object[] {anchorPane, txbLcNome.getText()}, paramEvent.GET_PARAM));
        } catch (Exception e) {e.printStackTrace();}
    }
    
    
    private void ListaBoletasBanco() throws Exception {
        List<ConsultaInter> data = new ArrayList<>();

        int a = 1; int b = 1;
        jProgressListaBoletasConsulta.setProgress(0);

        Inter c  = new Inter();

        bancos bco = new bancos("077");
        String path = bco.getBanco_CertPath();
        String path_crt = path + bco.getBanco_CrtFile();
        String path_key = path + bco.getBanco_KeyFile();

        String tipoListagem = jTipoListagem.getSelectionModel().getSelectedItem().toString();
        String conDataIni = Dates.DateFormata("yyyy-MM-dd", Dates.toDate(conDataInicial.getValue()));
        String conDataFim = Dates.DateFormata("yyyy-MM-dd", Dates.toDate(conDataFinal.getValue()));
        /*
        / Inicializa variavel para jtable
        */
        List<classInterConsulta> pessoasBoleta = new ArrayList<classInterConsulta>();

        String url_consulta = "https://apis.bancointer.com.br/openbanking/v1/certificado/boletos?filtrarPor=&1.&dataInicial=&2.&dataFinal=&3.&ordenarPor=DATAVENCIMENTO_DSC&page=0&size=20";
        url_consulta = FuncoesGlobais.Subst(url_consulta, new String[] {tipoListagem, conDataIni, conDataFim});

        Object[] consulta = c.selectBoleta(url_consulta, path_crt, path_key);
        int statusCode = (int)consulta[0];
        Object[] infoMessage = (Object[])consulta[1];
        if ((int)consulta[0] != 200) {
            System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
        } else {
            JSONObject jsonOb = (JSONObject)infoMessage[0];

            int totalPages = Integer.valueOf(c.myfunction(jsonOb,"totalPages").toString());

            String jsonSummary = c.myfunction(jsonOb,"summary").toString();
            JSONObject jsonObSummary = new JSONObject(jsonSummary);
            String jsonBaixados = c.myfunction(jsonObSummary,"baixados").toString();
            jsonOb = new JSONObject(jsonBaixados);
            String baixadosQuantidade = c.myfunction(jsonOb,"quantidade").toString();
            baiQuantidade.setText(baixadosQuantidade);
            String baixadosValor = c.myfunction(jsonOb,"valor").toString();
            baiValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(baixadosValor)));

            String jsonRecebidos = c.myfunction(jsonObSummary,"recebidos").toString();
            jsonOb = new JSONObject(jsonRecebidos);
            String recebidosQuantidade = c.myfunction(jsonOb,"quantidade").toString();
            recQuantidade.setText(recebidosQuantidade);
            String recebidosValor = c.myfunction(jsonOb,"valor").toString();
            recValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(recebidosValor)));

            String jsonExpirados = c.myfunction(jsonObSummary,"expirados").toString();
            jsonOb = new JSONObject(jsonExpirados);
            String expiradosQuantidade = c.myfunction(jsonOb,"quantidade").toString();
            expQuantidade.setText(expiradosQuantidade);
            String expiradosValor = c.myfunction(jsonOb,"valor").toString();
            expValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(expiradosValor)));

            String jsonPrevistos = c.myfunction(jsonObSummary,"previstos").toString();
            jsonOb = new JSONObject(jsonPrevistos);
            String previstosQuantidade = c.myfunction(jsonOb,"quantidade").toString();
            preQuantidade.setText(previstosQuantidade);
            String previstosValor = c.myfunction(jsonOb,"valor").toString();
            preValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(previstosValor)));

            String emitidosQuantidade = String.valueOf(
                    Integer.valueOf(baixadosQuantidade) +
                            Integer.valueOf(recebidosQuantidade) +
                            Integer.valueOf(expiradosQuantidade) +
                            Integer.valueOf(previstosQuantidade));

            if (totalPages > 0) {
                int arc = totalPages;
                for (int i=0; i <= totalPages - 1; i++) {
                    String url_Paginas = "https://apis.bancointer.com.br/openbanking/v1/certificado/boletos?filtrarPor=&1.&dataInicial=&2.&dataFinal=&3.&ordenarPor=DATAVENCIMENTO_DSC&page=&4.&size=20";
                    url_Paginas = FuncoesGlobais.Subst(url_Paginas, new String[] {tipoListagem, conDataIni, conDataFim, String.valueOf(i)});

                    consulta = c.selectBoleta(url_Paginas, path_crt, path_key);
                    statusCode = (int)consulta[0];
                    infoMessage = (Object[])consulta[1];
                    if ((int)consulta[0] != 200) {
                        System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
                    } else {
                        jsonOb = (JSONObject)infoMessage[0];
                        String jsonContent = c.myfunction(jsonOb,"content").toString();

                        JSONArray jsonObContent = new JSONArray(jsonContent);

                        int brc = jsonObContent.length();
                        for(int n=0; n < jsonObContent.length(); n++) {
                            JSONObject lista = new JSONObject(jsonObContent.getString(n));
                            Date dataEmissao = Dates.StringtoDate(c.myfunction(lista,"dataEmissao").toString(),"dd-MM-yyyy");
                            Date dataVencimento = Dates.StringtoDate(c.myfunction(lista,"dataVencimento").toString(),"dd-MM-yyyy");
                            Date dataPagamento = null;
                            if (c.myfunction(lista, "dataPagtoBaixa") != null) dataPagamento = Dates.StringtoDate(c.myfunction(lista, "dataPagtoBaixa").toString(), "dd-MM-yyyy");
                            String seuNumero = c.myfunction(lista,"seuNumero").toString();
                            String nossoNumero = c.myfunction(lista,"nossoNumero").toString();
                            String cnpjCpfSacado = c.myfunction(lista,"cnpjCpfSacado").toString();
                            String nomeSacado = c.myfunction(lista,"nomeSacado").toString();
                            BigDecimal valorMulta = new BigDecimal(c.myfunction(lista,"valorMulta").toString());
                            BigDecimal valorJuros = new BigDecimal(c.myfunction(lista,"valorJuros").toString());
                            BigDecimal valorNominal = new BigDecimal(c.myfunction(lista,"valorNominal").toString());
                            String situacao = c.myfunction(lista,"situacao").toString();
                            boolean baixado = isBaixado(nossoNumero);
                            pessoasBoleta.add(new classInterConsulta(dataEmissao, dataVencimento, dataPagamento, seuNumero, nossoNumero, cnpjCpfSacado, nomeSacado, valorMulta, valorJuros, valorNominal, situacao, baixado));

                            int pgs2 = ((b++ * 100) / brc) + 1;
                            jProgressListaBoletasConsulta1.setProgress(pgs2);
                            try { Thread.sleep(20); } catch (InterruptedException ex) { }
                        }
                    }
                    int pgs1 = ((a++ * 100) / arc) + 1;
                    jProgressListaBoletasConsulta.setProgress(pgs1);
                    try { Thread.sleep(20); } catch (InterruptedException ex) { }
                }
            }
        }

        if (pessoasBoleta.size() > 0) {
            for (classInterConsulta item : pessoasBoleta) {
                ConsultaInter dado = new ConsultaInter(
                        Dates.toLocalDate(item.getDataEmissao().getValue()),
                        Dates.toLocalDate(item.getDataVencimento().getValue()),
                        item.getDataPagamento().getValue() == null ? null : Dates.toLocalDate(item.getDataPagamento().getValue()),
                        item.getSeuNumero().getValue(),
                        item.getNossoNumero().getValue(),
                        item.getCnpjCpfSacado().getValue(),
                        item.getNomeSacado().getValue(),
                        item.getValorMulta().getValue(),
                        item.getValorJuros().getValue(),
                        item.getValorNominal().getValue(),
                        item.getSituacao().getValue(),
                        item.getBaixado().getValue() ? "S" : "N"
                );
                data.add(dado);
            }
        }

        SetDisplayBoletosConsulta(data);
    }

    private void SetDisplayBoletosConsulta(List<ConsultaInter> bancosConsulta) {
        conLista_Emissao.setCellValueFactory(new PropertyValueFactory("emissao"));
        conLista_Emissao.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        conLista_Emissao.setStyle("-fx-alignment: CENTER;");
        conLista_Vencimento.setCellValueFactory(new PropertyValueFactory("vencimento"));
        conLista_Vencimento.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        conLista_Vencimento.setStyle("-fx-alignment: CENTER;");
        conLista_Pagamento.setCellValueFactory(new PropertyValueFactory("pagamento"));
        conLista_Pagamento.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        conLista_Pagamento.setStyle("-fx-alignment: CENTER;");
        conLista_SeuNumero.setCellValueFactory(new PropertyValueFactory("seunumero"));
        conLista_SeuNumero.setStyle("-fx-alignment: CENTER;");
        conLista_NossoNumero.setCellValueFactory(new PropertyValueFactory("nossonumero"));
        conLista_NossoNumero.setStyle("-fx-alignment: CENTER;");
        conLista_CpfCnpj.setCellValueFactory(new PropertyValueFactory("cpfcnpj"));
        conLista_CpfCnpj.setStyle("-fx-alignment: LEFT;");
        conLista_Sacado.setCellValueFactory(new PropertyValueFactory("sacado"));
        conLista_Sacado.setStyle("-fx-alignment: LEFT;");

        conLista_Multa.setCellValueFactory(new PropertyValueFactory<>("multa"));
        conLista_Multa.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        conLista_Multa.setStyle( "-fx-alignment: CENTER-RIGHT;");

        conLista_Juros.setCellValueFactory(new PropertyValueFactory<>("juros"));
        conLista_Juros.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        conLista_Juros.setStyle( "-fx-alignment: CENTER-RIGHT;");

        conLista_Valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        conLista_Valor.setCellFactory((BancoInter.AbstractConvertCellFactory<ConsultaInter, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        conLista_Valor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        conLista_Situacao.setCellValueFactory(new PropertyValueFactory("situacao"));
        conLista_Situacao.setStyle("-fx-alignment: CENTER;");
        conLista_B.setCellValueFactory(new PropertyValueFactory("baixa"));
        conLista_B.setStyle("-fx-alignment: CENTER;");

        List<ConsultaInter> data = new ArrayList<>();
        if (bancosConsulta.size() != 0) {
            for (ConsultaInter item : bancosConsulta) {
                data.add(new ConsultaInter(
                                item.getEmissao(),
                                item.getVencimento(),
                                item.getPagamento(),
                                item.getSeunumero(),
                                item.getNossonumero(),
                                item.getCpfcnpj(),
                                item.getSacado(),
                                item.getMulta(),
                                item.getJuros(),
                                item.getValor(),
                                item.getSituacao(),
                                item.getBaixa()
                        )
                );
            }
        }
        conLista.setItems(FXCollections.observableArrayList(data));
        
        conLista.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY) { conLista.setContextMenu(null); return; }
            
            ConsultaInter select = conLista.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (select.getBaixa().equalsIgnoreCase("N")) {
                if ("EXPIRADO;PAGO;BAIXADO".indexOf(select.getSituacao().toString().toUpperCase()) > -1) {
                    conLista.setContextMenu(null);
                    return;
                } 
                    
                final ContextMenu rowMenu = new ContextMenu();
                MenuItem cancelItem = new MenuItem("Baixar a pedido do Cliente");
                cancelItem.setOnAction(new EventHandler<ActionEvent>() {       
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            bancos bcos = new bancos("077");
                            String path = bcos.getBanco_CertPath();
                            String path_crt = path + bcos.getBanco_CrtFile();
                            String path_key = path + bcos.getBanco_KeyFile();

                            String url_baixa = "https://apis.bancointer.com.br/openbanking/v1/certificado/boletos/&1./baixas";
                            url_baixa = FuncoesGlobais.Subst(url_baixa, new String[] {select.getNossonumero()});
                            String codBaixa = "{\"codigoBaixa\": \"APEDIDODOCLIENTE\"}";
                            Inter c  = new Inter();
                            Object[] baixa = c.baixaBoleta(url_baixa, path_crt, path_key, codBaixa);
                            int statusCode = (int)baixa[0];
                            Object[] infoMessage = (Object[])baixa[1];
                            if ((int)baixa[0] != 204) {            
                                System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
                            } else {
                                try {
                                    ListaBoletasBanco();
                                } catch (Exception ex) {
                                    System.out.println("Problema de comunicação com o banco ou sem Internet.");
                                }
                                System.out.println(infoMessage[0]);
                            }
                        } catch (Exception ex) {}
                    }
                });
                rowMenu.getItems().addAll(cancelItem);
                conLista.setContextMenu(rowMenu);
            }
        });
    }    
   
    private boolean isBaixado(String nnumero) {
        String sSql = "SELECT NNUMERO FROM movimento WHERE NNUMERO LIKE '%" + nnumero + "%';";
        ResultSet imResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        boolean isDown = false;
        try {
            while (imResult.next()) {
                isDown = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        return isDown;
    }
    
}
