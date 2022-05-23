/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import Calculos.AutentMult;
import Calculos.Processa;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

/**
 *
 * @author Samic
 */
public class Baixa implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    List<classBaixa> lst;
    
    @FXML private AnchorPane anchorPane;
    
    @FXML private TableView<classBaixa> gridLista;
    @FXML private TableColumn<classBaixa, String> tipo;
    @FXML private TableColumn<classBaixa, String> nome;
    @FXML private TableColumn<classBaixa, String> emissao;
    @FXML private TableColumn<classBaixa, String> vencimento;
    @FXML private TableColumn<classBaixa, String> cpfcnpj;
    @FXML private TableColumn<classBaixa, String> nnumero;
    @FXML private TableColumn<classBaixa, BigDecimal> multa;
    @FXML private TableColumn<classBaixa, BigDecimal> juros;
    @FXML private TableColumn<classBaixa, BigDecimal> valor;
    @FXML private TableColumn<classBaixa, Boolean> tag;
    
    @FXML private TextField vmarcadas;
    @FXML private TextField bmarcadas;
    
    @FXML private TextField vdesmarcadas;
    @FXML private TextField bdesmacadas;
    
    @FXML private Button btnbaixar;

    public void setLista(List<classBaixa> lst) {
        this.lst = lst;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()-> {
            PopulateGrid();
            Platform.runLater(()-> {
                Object[] totais = GridTotal();
                int iunSelected = (int)totais[0]; BigDecimal bunSelected = (BigDecimal)totais[1];
                int iSelected = (int)totais[2]; BigDecimal bSelected = (BigDecimal)totais[3];
                
                // Atualiza Tela
                vdesmarcadas.setText(LerValor.BigDecimalToCurrency(bunSelected));
                bdesmacadas.setText(String.valueOf(iunSelected));

                vmarcadas.setText(LerValor.BigDecimalToCurrency(bSelected));
                bmarcadas.setText(String.valueOf(iSelected));                                
            });
        });
        
        btnbaixar.setOnAction((event) -> {            
            // Linhas do relatório
            List<classBaixa> rows = new ArrayList<>();
            
            for (classBaixa item : gridLista.getItems()) {
                if (item.getTag()) {
                    String nnumero = item.getNnumero();
                    Object[][] dados = null;
                    try {
                        dados = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "contrato"}, "movimento", "nnumero = ?", new Object[][] {{"string", nnumero}});
                    } catch (Exception e) {}
                    
                    Object[] baixa = null;
                    if (dados != null) {
                        String rgprp = (String)dados[0][3];
                        String rgimv = (String)dados[1][3];
                        String contrato = (String)dados[2][3];
                        String vencto = item.getVencimento();
                        String pagto = item.getEmissao();
                        String muljur = item.getMulta().add(item.getJuros()).toPlainString();
                        String valor = item.getValor().toPlainString();
                        
                        baixa = Distribuicao(rgprp, rgimv, contrato, vencto, pagto, muljur, valor);
                        
                        boolean sucesso = (boolean)baixa[0];
                        BigInteger naut = new BigInteger("0");
                        if (sucesso) {
                            naut = (BigInteger)baixa[1];
                        }
                        
                        rows.add(new classBaixa(
                                item.getTipo(), 
                                contrato, 
                                item.getNome(), 
                                item.getEmissao(), 
                                item.getVencimento(), 
                                item.getCpfcnpj(), 
                                item.getNnumero(), 
                                item.getValor(), 
                                naut)
                        );
                    }
                     
                }
            }
            String pdfName = new PdfViewer().GeraPDFTemp(rows,"Bordero", null);
            new PdfViewer("Preview do Borderô", pdfName);
        });
    }
    
    private Object[] GridTotal() {
        Object[] total = new Object[] {0, new BigDecimal("0"),0, new BigDecimal("0")};
        if (this.lst.isEmpty()) return total;
        
        int iunSelected = 0; BigDecimal bunSelected = new BigDecimal("0");
        int iSelected = 0; BigDecimal bSelected = new BigDecimal("0");
        for (classBaixa item : lst) {
            if (!item.getTag()) {
                bunSelected = bunSelected.add(item.getValor().add(item.getMulta().add(item.getJuros())));
                iunSelected += 1;
            } else {
                bSelected = bSelected.add(item.getValor().add(item.getMulta().add(item.getJuros())));
                iSelected += 1;
            }
        }
        btnbaixar.setDisable(iSelected == 0);
        return new Object[] {iunSelected, bunSelected, iSelected, bSelected};
    }
    
    private void PopulateGrid() {
        tipo.setCellValueFactory(new PropertyValueFactory("tipo"));
        tipo.setStyle("-fx-alignment: CENTER;");
        nome.setCellValueFactory(new PropertyValueFactory("nome"));
        nome.setStyle("-fx-alignment: CENTER-LEFT;");       
        emissao.setCellValueFactory(new PropertyValueFactory("emissao"));
        emissao.setStyle("-fx-alignment: CENTER;");
        vencimento.setCellValueFactory(new PropertyValueFactory("vencimento"));
        vencimento.setStyle("-fx-alignment: CENTER;");
        
        cpfcnpj.setCellValueFactory(new PropertyValueFactory("cpfcnpj"));
        cpfcnpj.setStyle("-fx-alignment: CENTER;");
        nnumero.setCellValueFactory(new PropertyValueFactory("nnumero"));
        nnumero.setStyle("-fx-alignment: CENTER;");

        multa.setCellValueFactory(new PropertyValueFactory<>("multa"));
        multa.setCellFactory((BancoInter.AbstractConvertCellFactory<classBaixa, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        multa.setStyle( "-fx-alignment: CENTER-RIGHT;");

        juros.setCellValueFactory(new PropertyValueFactory<>("juros"));
        juros.setCellFactory((BancoInter.AbstractConvertCellFactory<classBaixa, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        juros.setStyle( "-fx-alignment: CENTER-RIGHT;");

        valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        valor.setCellFactory((BancoInter.AbstractConvertCellFactory<classBaixa, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        valor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        tag.setCellValueFactory(new PropertyValueFactory("tag"));
        tag.setStyle("-fx-alignment: CENTER;");
        
        //tag.setCellFactory(tc -> new CheckBoxTableCell<>());
        tag.setCellFactory(t -> new TableCell<classBaixa, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CheckBox r = new CheckBox();
                    if (item == null) {
                        r.setSelected(false);
                    } else {
                        r.setSelected(item);
                    }

                    r.selectedProperty().addListener((v, o, n) -> {
                        System.out.println("Tag --> " + n.booleanValue());
                    });
                    
                    // Mostra o Rating na coluna
                    setGraphic(r);
                }
            }

        });
        
        gridLista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        gridLista.setEditable(true);        
        if (!this.lst.isEmpty()) {
            ObservableList<classBaixa> data = FXCollections.observableArrayList(this.lst);
            gridLista.setItems(data);
        }
    }
    
    private Object[] Distribuicao(String rgprp, String rgimv, String contrato, String vct, String pagto, String multajuros, String valor) {
        BigInteger aut = conn.PegarAutenticacao();
        String lanctos = DbMain.GeraLancamentosArray(new String[][] {{"","","","","",valor}});
        String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                "contrato, valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
        
        boolean sucesso = true;
        // Gravar no caixa
        try {
            caixaSQL = String.format(caixaSQL,
                    aut,
                    new java.util.Date(),
                    VariaveisGlobais.usuario,
                    "CRE",
                    "REC",
                    rgprp, rgimv, contrato,
                    valor, lanctos
            );
            if (conn.ExecutarComando(caixaSQL) > 0) {
                System.out.println("incluido.");
                Baixa(vct, aut, rgprp, rgimv, contrato, pagto, multajuros);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sucesso = false;
        }
        return new Object[] {sucesso, sucesso ? aut : null};
    }

    private void Baixa(String vcto, BigInteger aut, String rgprp, String rgimv, String contrato, String pagto, String multajuros) {
        // Pegar o mes de referencia do Vencimento no descdif
        String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES, -1, Dates.StringtoDate(vcto, "dd/MM/yyyy")));

        //
        BigDecimal[] valores = ProcessarCampos(rgprp, rgimv, contrato, vcto, pagto);

        // IRRF
        BigDecimal irenda = valores[3];
        if (irenda.compareTo(BigDecimal.ZERO) == 1) {
            irenda.toPlainString();
        }

        BigDecimal vrIptu = valores[4];

        // Calcula comissao
        BigDecimal baseComissao = valores[0].subtract(valores[2]).add(valores[1]);
        Double Comissao = baseComissao.doubleValue() * (VariaveisGlobais.co / 100);

        // MU, JU, CO, EP
        BigDecimal mu = valores[6];
        BigDecimal ep = valores[9];
        
        BigDecimal muju = new BigDecimal(multajuros);
        BigDecimal dfmrmc = muju.subtract(mu);
        
        // Juros -> 70% do saldo; Correção -> 30% do saldo
        BigDecimal ju = dfmrmc.multiply(new BigDecimal("0.70"));
        BigDecimal co = dfmrmc.subtract(ju);

        // Movimento
        String sql = "UPDATE movimento SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', ir = '%s', cm = '%s', mu = '%s', ju = '%s', co = '%s', ep = '%s', aut_pag = '%s' WHERE contrato = '%s' AND dtvencimento = '%s' AND dtrecebimento is null;";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                irenda.toPlainString(),
                Comissao,
                mu.toPlainString(),
                ju.toPlainString(),
                co.toPlainString(),
                ep.toPlainString(),
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.trim(),
                Dates.StringtoString(vcto,"dd/MM/yyyy", "yyyy/MM/dd")
                //descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [movimento]. " + vcto);
        }

        // Desconto Diferença
        sql = "UPDATE descdif SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [descdif]. " + vcto);
        }

        // Taxas
        sql = "UPDATE taxas SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [taxas]. " + vcto);
        }

        // Seguros
        sql = "UPDATE seguros SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [seguro]. " + vcto);
        }
    }    

    public BigDecimal[] ProcessarCampos(String rgprp, String rgimv, String contrato, String vencimento, String pagamento) {
        Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vencimento, "dd-MM-yyyy"), Dates.StringtoDate(pagamento, "dd-MM-yyyy"));

        return new BigDecimal[]{calc.getAluguel(), calc.getDiferenca(), calc.getDescontos(), calc.getIrenda(), calc.getIptu(), calc.getSeguro(), calc.Multa(), calc.Juros(), calc.Correcao(), calc.Expediente()};
    }
    
}
