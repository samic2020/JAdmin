/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios.Pagamentos;

import Classes.DadosLocatario;
import Classes.gRecibo;
import Classes.paramEvent;
import Funcoes.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author supervisor
 */
public class PagtosLocatarioController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private String contrato;
    private String rgimv;
    private String nomeLoca;
    private Object[] param = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<cPagtos> pgtPagamentos;
    @FXML private TableColumn<cPagtos, Integer> pgtId;
    @FXML private TableColumn<cPagtos, Integer> pgtAut;
    @FXML private TableColumn<cPagtos, Date> pgtVencto;
    @FXML private TableColumn<cPagtos, Date> pgtRecto;
    @FXML private TableColumn<cPagtos, BigDecimal> pgtValor;
    @FXML private TableColumn<cPagtos, Date> pgtDataHora;
    @FXML private TableColumn<cPagtos, String> pgtLogado;
    @FXML private TableColumn<cPagtos, String> pgtLanctos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            this.param = event.sparam;

            if (this.param != null) {
                this.contrato = (String)this.param[0];
                this.rgimv = (String)this.param[1];
                this.nomeLoca = (String)this.param[2];

                FillRectos();
            }
        });
    }

    private void FillRectos() {
        List<cPagtos> data = new ArrayList<cPagtos>();
        String Sql = "SELECT m.id, c.aut, m.dtvencimento, m.dtrecebimento, c.valor, c.datahora, c.logado, c.lancamentos FROM movimento m INNER JOIN caixa c ON c.aut = m.aut_rec WHERE NOT m.dtrecebimento Is Null AND m.contrato = ? ORDER BY m.dtvencimento;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", this.contrato}});
            int gId = -1; int gAut = -1;
            Date gVencto = null; Date gRecto = null;
            BigDecimal gValor = new BigDecimal("0");
            Date gDataHora = null;
            String gLogado = null; String gLanctos = null;

            while (rs.next()) {
                try {gId = rs.getInt("id");} catch (SQLException sqlex) {}
                try {gAut = rs.getInt("aut");} catch (SQLException sqlex) {}
                try {gVencto = rs.getDate("dtvencimento");} catch (SQLException sqlex) {}
                try {gRecto = rs.getDate("dtrecebimento");} catch (SQLException sqlex) {}
                try {gValor = new BigDecimal(LerValor.Number2BigDecimal(rs.getString("valor").replace("R$ ","")));} catch (SQLException sqlex) {}
                try {gDataHora = Dates.String2Date(rs.getString("datahora"));} catch (SQLException sqlex) {}
                try {gLogado = rs.getString("logado");} catch (SQLException sqlex) {}
                try {gLanctos = rs.getString("lancamentos");} catch (SQLException sqlex) {}

                data.add(new cPagtos(gId, gAut, gVencto, gRecto, gValor, gDataHora, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        pgtId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        pgtId.setStyle( "-fx-alignment: CENTER;");

        pgtAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        pgtAut.setStyle( "-fx-alignment: CENTER;");

        pgtVencto.setCellValueFactory(new PropertyValueFactory<>("Vencto"));
        pgtVencto.setCellFactory((AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        pgtVencto.setStyle( "-fx-alignment: CENTER;");

        pgtRecto.setCellValueFactory(new PropertyValueFactory<>("Recto"));
        pgtRecto.setCellFactory((AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        pgtRecto.setStyle( "-fx-alignment: CENTER;");

        pgtValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        pgtValor.setCellFactory((AbstractConvertCellFactory<cPagtos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        pgtValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        pgtDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        //pgtDataHora.setCellFactory((AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(Dates.toLocalDate(value)));
        pgtDataHora.setStyle( "-fx-alignment: CENTER;");

        pgtLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        pgtLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        pgtLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        pgtLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        if (!data.isEmpty()) pgtPagamentos.setItems(FXCollections.observableArrayList(data));

        pgtPagamentos.setOnMouseClicked(event -> {
            cPagtos select = pgtPagamentos.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                Object[][] DadosImovel = null;
                try {
                    DadosImovel = conn.LerCamposTabela(new String[] {
                            "i_end || ', ' || i_num || ' ' || i_cplto AS i_ender",
                            "i_bairro",
                            "i_cidade",
                            "i_estado",
                            "i_cep"
                    }, "imoveis","i_rgimv = '" + this.rgimv + "'");
                } catch (Exception e) {}
                String qiend = "", qibai = "", qicid = "", qiest = "", qicep = "";
                if (DadosImovel != null) {
                    qiend = DadosImovel[0][3].toString();
                    qibai = DadosImovel[1][3].toString();
                    qicid = DadosImovel[2][3].toString();
                    qiest = DadosImovel[3][3].toString();
                    qicep = DadosImovel[4][3].toString();
                }
                DadosLocatario dadosLocatario = new DadosLocatario(this.contrato, this.nomeLoca, qiend, "", "", qibai, qicid, qiest, qicep);

                gRecibo recibo = new gRecibo();
                recibo.GeraReciboSegundaVia(this.contrato, Dates.DateFormata("dd/MM/yyyy", select.getVencto()));

                Collections dadm = VariaveisGlobais.getAdmDados();

                String[][] Lancamentos = ConvertArrayString2ObjectArrays_REC(select.getLanctos().toString());

                new Impressao(new BigInteger(String.valueOf(select.getAut())), Lancamentos, select.getDataHora(), select.getLogado()).ImprimeReciboPDF(dadm, null, dadosLocatario, recibo, true);
            }
        });
    }

    public interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }

        String convert(T value);
    }

    private String[][] ConvertArrayString2ObjectArrays_REC(String value) {
        String[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ArraysAdd(retorno,
                    new String[]{
                            vtr[0].trim().replace("\"",""),
                            vtr[4].trim().replace("\"",""),
                            vtr[3].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[5].trim().replace("\"",""),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }
}
