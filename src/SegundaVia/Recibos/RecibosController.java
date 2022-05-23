package SegundaVia.Recibos;

import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Funcoes.Collections;
import Funcoes.*;
import Locatarios.Pagamentos.PagtosLocatarioController;
import Locatarios.Pagamentos.cPagtos;
import com.sun.prism.impl.Disposer.Record;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RecibosController implements Initializable {
    private DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    @FXML private AnchorPane anchorPane;

    @FXML private TableView<cPagtos> recListaRec;
    @FXML private TableColumn<cPagtos, Integer> lrId;
    @FXML private TableColumn<cPagtos, Integer> lrAut;
    @FXML private TableColumn<cPagtos, Date> lrVencto;
    @FXML private TableColumn<cPagtos, Date> lrRecto;
    @FXML private TableColumn<cPagtos, BigDecimal> lrValor;
    @FXML private TableColumn<cPagtos, Date> lrDataHora;
    @FXML private TableColumn<cPagtos, String> lrLogado;
    @FXML private TableColumn<cPagtos, String> lrLanctos;
    @FXML private TableColumn<Record, Boolean> lrAcoes;

    @FXML private TextField recContrato;
    @FXML private TextField recNome;
    @FXML private Spinner<Integer> recAno;
    @FXML private Button btnListar;
    @FXML private TextField recAut;
    @FXML private Button btnImprimir;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        recAno.setValueFactory(ano);

        AutocompleteRegistroNome();

        btnListar.setOnAction(event -> {
            if (recContrato.getText() == null && recNome.getText() == null) {recContrato.requestFocus(); return;}
            if (!recContrato.getText().trim().equalsIgnoreCase("")) {
                // && !recNome.getText().trim().equalsIgnoreCase("")
                Object[][] dadosLoca = null;
                try {
                    dadosLoca = conn.LerCamposTabela(new String[] {"l_rgimv", "CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome"}, "locatarios", "l_contrato = '" + recContrato.getText().trim() + "'" );
                } catch (SQLException e) {}
                if (dadosLoca != null) {
                    String rgimv = dadosLoca[0][3].toString();
                    String nomeLoca = dadosLoca[1][3].toString();
                    int anoRecibo = Integer.valueOf(recAno.getValue());

                    recListaRec.setItems(null);
                    FillRectos(recContrato.getText().trim(), rgimv, nomeLoca, anoRecibo);
                }
            }
        });

        btnImprimir.disableProperty().bind(recAut.textProperty().length().greaterThan(0).not());
        btnImprimir.setOnAction(event -> {
            if (recAut.getText() == null) { recAut.requestFocus(); return; }
            if (!recAut.getText().trim().equalsIgnoreCase("")) {
                String Sql = "SELECT m.id, c.rgprp, c.rgimv, c.contrato, c.aut, m.dtvencimento, m.dtrecebimento, c.valor, c.datahora, c.logado, c.lancamentos FROM movimento m INNER JOIN caixa c ON c.aut = m.aut_rec WHERE NOT m.dtrecebimento Is Null AND c.aut = ? ORDER BY m.dtvencimento;";
                ResultSet rs = null;

                int gId = -1;
                int gAut = -1;
                String qRgimv = null;
                String qContrato = null;
                Date gVencto = null;
                Date gRecto = null;
                BigDecimal gValor = new BigDecimal("0");
                Date gDataHora = null;
                String gLogado = null;
                String gLanctos = null;
                try {
                    rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][]{{"int", Integer.valueOf(recAut.getText().trim())}});
                    while (rs.next()) {
                        try {
                            gId = rs.getInt("id");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gAut = rs.getInt("aut");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            qRgimv = rs.getString("rgimv");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            qContrato = rs.getString("contrato");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gVencto = rs.getDate("dtvencimento");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gRecto = rs.getDate("dtrecebimento");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gValor = new BigDecimal(LerValor.Number2BigDecimal(rs.getString("valor").replace("R$ ", "")));
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gDataHora = Dates.String2Date(rs.getString("datahora"));
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gLogado = rs.getString("logado");
                        } catch (SQLException sqlex) {
                        }
                        try {
                            gLanctos = rs.getString("lancamentos");
                        } catch (SQLException sqlex) {
                        }
                    }
                } catch (Exception e) {
                }
                try {
                    DbMain.FecharTabela(rs);
                } catch (Exception e) {
                }

                if (qContrato == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Menssagem");
                    alert.setHeaderText("Autenticação não encontrado ou inexistente!");
                    alert.setContentText("Entre com uma Autenticação válida!!!");
                    alert.showAndWait();
                    return;
                }

                // Dados do Locatario
                Object[][] dadosLoca = null;
                try {
                    dadosLoca = conn.LerCamposTabela(new String[]{"l_rgimv", "CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome"}, "locatarios", "l_contrato = '" + qContrato + "'");
                } catch (SQLException e) {
                }
                String nomeLoca = null;
                if (dadosLoca != null) {
                    nomeLoca = dadosLoca[1][3].toString();
                }

                Object[][] DadosImovel = null;
                try {
                    DadosImovel = conn.LerCamposTabela(new String[]{
                            "i_end || ', ' || i_num || ' ' || i_cplto AS i_ender",
                            "i_bairro",
                            "i_cidade",
                            "i_estado",
                            "i_cep"
                    }, "imoveis", "i_rgimv = '" + qRgimv + "'");
                } catch (Exception e) {
                }
                String qiend = "", qibai = "", qicid = "", qiest = "", qicep = "";
                if (DadosImovel != null) {
                    qiend = DadosImovel[0][3].toString();
                    qibai = DadosImovel[1][3].toString();
                    qicid = DadosImovel[2][3].toString();
                    qiest = DadosImovel[3][3].toString();
                    qicep = DadosImovel[4][3].toString();
                }
                DadosLocatario dadosLocatario = new DadosLocatario(qContrato, nomeLoca, qiend, "", "", qibai, qicid, qiest, qicep);

                gRecibo recibo = new gRecibo();
                recibo.GeraReciboSegundaVia(qContrato, Dates.DateFormata("dd/MM/yyyy", gVencto));

                Collections dadm = VariaveisGlobais.getAdmDados();

                String[][] Lancamentos = ConvertArrayString2ObjectArrays_REC(gLanctos);

                new Impressao(new BigInteger(String.valueOf(gAut)), Lancamentos, gDataHora, gLogado).ImprimeReciboPDF(dadm, null, dadosLocatario, recibo, true);
            }
        });

        Platform.runLater(() -> recContrato.requestFocus());
    }

    private void FillRectos(String contrato, String rgimv, String nomeLoca, int anoRecibo) {
        this.rgimv = rgimv;
        this.contrato = contrato;
        this.nomeloca = nomeLoca;

        List<cPagtos> data = new ArrayList<cPagtos>();
        String Sql = "SELECT m.id, c.aut, m.dtvencimento, m.dtrecebimento, c.valor, c.datahora, c.logado, c.lancamentos FROM movimento m INNER JOIN caixa c ON c.aut = m.aut_rec WHERE NOT m.dtrecebimento Is Null AND m.contrato = ? AND (EXTRACT(YEAR FROM m.dtvencimento) = ? OR EXTRACT(YEAR FROM m.dtrecebimento) = ?) ORDER BY m.dtvencimento;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}, {"int", anoRecibo}, {"int", anoRecibo}});
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

        lrId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        lrId.setStyle( "-fx-alignment: CENTER;");

        lrAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        lrAut.setStyle( "-fx-alignment: CENTER;");

        lrVencto.setCellValueFactory(new PropertyValueFactory<>("Vencto"));
        lrVencto.setCellFactory((PagtosLocatarioController.AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        lrVencto.setStyle( "-fx-alignment: CENTER;");

        lrRecto.setCellValueFactory(new PropertyValueFactory<>("Recto"));
        lrRecto.setCellFactory((PagtosLocatarioController.AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        lrRecto.setStyle( "-fx-alignment: CENTER;");

        lrValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        lrValor.setCellFactory((PagtosLocatarioController.AbstractConvertCellFactory<cPagtos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        lrValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        lrDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        //lrDataHora.setCellFactory((AbstractConvertCellFactory<cPagtos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(Dates.toLocalDate(value)));
        lrDataHora.setStyle( "-fx-alignment: CENTER;");

        lrLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        lrLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        lrLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        lrLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        lrAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));

        //Adding the Button to the cell
        lrAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) recListaRec.setItems(FXCollections.observableArrayList(data));
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

    private void AutocompleteRegistroNome() {
        ResultSet imv = null;
        String qSQL = "select l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome FROM locatarios ORDER BY l_contrato;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(recContrato, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(recNome, possibleSuggestionsNome);

        recContrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                recContrato.setText(null);
                recNome.setText(null);

                // Zera Grid
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = recContrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, recContrato.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        recNome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        recNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, recNome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = recContrato.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,recNome.getText())) {
                        recContrato.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
            }
        });
    }

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell(){
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cPagtos select = (cPagtos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPane.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            // Preview
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cPagtos select = (cPagtos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                    if (select == null) return;
                    Object[][] DadosImovel = null;
                    try {
                        DadosImovel = conn.LerCamposTabela(new String[] {
                                "i_end || ', ' || i_num || ' ' || i_cplto AS i_ender",
                                "i_bairro",
                                "i_cidade",
                                "i_estado",
                                "i_cep"
                        }, "imoveis","i_rgimv = '" + rgimv + "'");
                    } catch (Exception e) {}
                    String qiend = "", qibai = "", qicid = "", qiest = "", qicep = "";
                    if (DadosImovel != null) {
                        qiend = DadosImovel[0][3].toString();
                        qibai = DadosImovel[1][3].toString();
                        qicid = DadosImovel[2][3].toString();
                        qiest = DadosImovel[3][3].toString();
                        qicep = DadosImovel[4][3].toString();
                    }
                    DadosLocatario dadosLocatario = new DadosLocatario(contrato, nomeloca, qiend, "", "", qibai, qicid, qiest, qicep);

                    gRecibo recibo = new gRecibo();
                    recibo.GeraReciboSegundaVia(contrato, Dates.DateFormata("dd/MM/yyyy", select.getVencto()));

                    Collections dadm = VariaveisGlobais.getAdmDados();

                    String[][] Lancamentos = ConvertArrayString2ObjectArrays_REC(select.getLanctos().toString());

                    new Impressao(new BigInteger(String.valueOf(select.getAut())), Lancamentos, select.getDataHora(), select.getLogado()).ImprimeReciboPDF(dadm, null, dadosLocatario, recibo, true);
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                HBox pane = new HBox(cellButton, cellAnexar);
                setGraphic(pane);
            }
        }
    }
}
