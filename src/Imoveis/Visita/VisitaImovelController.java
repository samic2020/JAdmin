package Imoveis.Visita;

import Classes.adcTelefones;
import Classes.getTels;
import Classes.ptelcontatoModel;
import Classes.setTels;
import Funcoes.Collections;
import Funcoes.*;
import entrada.BarraController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;

import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Created by supervisor on 30/03/17.
 */
public class VisitaImovelController  implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean reserva = false;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField v_rgimv;
    @FXML private TextField v_endereco;
    @FXML private Button v_btnSaida;
    @FXML private Button v_btnDevolucao;
    @FXML private TextField v_dthrSaida;
    @FXML private TextField v_dthrDevolucao;
    @FXML private TextField v_nome;
    @FXML private TextField v_documento;
    @FXML private ComboBox<ptelcontatoModel> v_telefones;
    @FXML private Button v_tel_adc;
    @FXML private Button v_tel_del;
    @FXML private TextArea v_historico;
    @FXML private Button v_btnRetornar;
    @FXML private Button v_btnGravar;
    @FXML private Text reservado;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    private boolean insert = false;
    private String id = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutocompleteRgImvEnd();
        javafx.application.Platform.runLater(() -> v_rgimv.requestFocus());
    }

    private void AutocompleteRgImvEnd() {
        ResultSet imv = null;
        String qSQL = "SELECT i_rgprp, i_rgimv, i_end || ', ' || i_num || ' ' || i_cplto || ' - ' || i_bairro as i_end FROM imoveis WHERE (exclusao is null) and Upper(i_situacao) = 'VAZIO' ORDER BY i_rgimv;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("i_rgimv");} catch (SQLException e) {}
                try {qnome = imv.getString("i_end");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        if (possibleSuggestionsContrato != null) {
            TextFields.bindAutoCompletion(v_rgimv, possibleSuggestionsContrato);
            TextFields.bindAutoCompletion(v_endereco, possibleSuggestionsNome);
        }

        v_rgimv.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                v_rgimv.setText(null);
                v_endereco.setText(null);

                v_btnSaida.setDisable(true);
                v_btnDevolucao.setDisable(true);
                v_btnGravar.setDisable(true);
                v_btnRetornar.setDisable(true);

                v_dthrSaida.setText("");
                v_dthrDevolucao.setText("");
                v_nome.setText("");
                v_documento.setText("");
                v_telefones.getItems().clear();
                v_historico.setText("");
                reservado.setVisible(false);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = v_rgimv.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, v_rgimv.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        v_endereco.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        v_endereco.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, v_endereco.getText());
                String pcontrato = null;
                try {pcontrato = v_rgimv.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                        v_rgimv.setText(_possibleSuggestions[pos][0]);
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }

                Object reserva[][] = null;
                try {
                    reserva = conn.LerCamposTabela(new String[]{"i_dtfimreserv"}, "imoveis", String.format("i_rgimv = '%s'",v_rgimv.getText()));
                } catch (SQLException e) {}
                if (reserva != null) {
                    if (reserva[0][3] != null) {
                        String dtfrsv = reserva[0][3].toString();
                        long dtdif = Dates.DtDiff(Dates.DIA, dtfrsv.replaceAll("/", "-"), Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()));
                        reservado.setVisible(dtdif <= 0);
                    }
                }

                Object dados[][] = null;
                try {
                    dados = conn.LerCamposTabela(new String[]{"v_dthrsaida", "v_nome", "v_documento", "v_telefones", "id"}, "visitas", String.format("v_rgimv = '%s' AND ((not v_dthrsaida Is Null OR not Trim(v_dthrsaida) = '') AND v_dthrdevolucao is null)",v_rgimv.getText()));
                } catch (SQLException e) {}
                if (dados != null) {
                    // Pega o id
                    this.id = String.valueOf(dados[4][3]);

                    insert = false;

                    v_dthrSaida.setText((String) dados[0][3]);
                    v_nome.setText((String) dados[1][3]);
                    v_documento.setText((String) dados[2][3]);

                    List<ptelcontatoModel> data = null;
                    try {data = new setTels((String) dados[3][3]).rString();} catch (Exception e) {}
                    if (data != null) v_telefones.setItems(observableArrayList(data)); else v_telefones.getItems().clear();
                    v_telefones.setDisable(false);
                    try {v_telefones.getSelectionModel().select(0);} catch (Exception e) {}

                    v_nome.setEditable(false);
                    v_documento.setEditable(false);
                    v_tel_adc.setDisable(true);
                    v_tel_del.setDisable(true);

                    v_historico.setEditable(false);

                    v_btnSaida.setDisable(true);
                    v_btnDevolucao.setDisable(false);

                    v_btnGravar.setDisable(true);
                    v_btnRetornar.setDisable(false);

                    // Reservado

                    v_btnDevolucao.requestFocus();
                } else {
                    this.id = null;
                    insert = true;

                    v_nome.setText("");
                    v_documento.setText("");
                    v_telefones.getItems().clear();

                    v_nome.setEditable(false);
                    v_documento.setEditable(false);
                    v_tel_adc.setDisable(true);
                    v_tel_del.setDisable(true);

                    v_historico.setText("");
                    v_historico.setEditable(false);

                    v_btnSaida.setDisable(false);
                    v_btnDevolucao.setDisable(true);

                    v_btnGravar.setDisable(true);
                    v_btnRetornar.setDisable(false);

                    v_btnSaida.requestFocus();
                }
            }
        });

        v_btnSaida.setOnAction(event -> {
            v_dthrSaida.setText(Dates.DateFormata("dd-MM-yyyy HH:mm", DbMain.getDateTimeServer()));
            v_btnSaida.setDisable(true);
            v_btnDevolucao.setDisable(true);
            v_btnGravar.setDisable(false);
            v_btnRetornar.setDisable(false);

            v_nome.setEditable(true);
            v_documento.setEditable(true);
            v_tel_adc.setDisable(false);
            v_tel_del.setDisable(false);
            v_historico.setEditable(false);

            v_nome.requestFocus();
        });

        v_tel_adc.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tels = v_telefones.getItems();
                tels.addAll(b);
                v_telefones.setItems(tels);
                try {v_telefones.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        v_tel_del.setOnAction(event -> {
            if (!v_telefones.getItems().isEmpty()) v_telefones.getItems().removeAll(v_telefones.getSelectionModel().getSelectedItem());
            try {v_telefones.getSelectionModel().select(0);} catch (Exception e) {}
        });

        v_btnDevolucao.setOnAction(event -> {
            Alert quest = new Alert(Alert.AlertType.CONFIRMATION, "Reservar o imóvel por 24h?", new ButtonType("Sim"), new ButtonType("Não"));
            quest.setTitle("Pergunta");
            Optional<ButtonType> result = quest.showAndWait();
            if (result.get().getText().equals("Sim")) {
                reserva = true;
            }

            v_dthrDevolucao.setText(Dates.DateFormata("dd-MM-yyyy HH:mm", DbMain.getDateTimeServer()));

            v_btnSaida.setDisable(true);
            v_btnDevolucao.setDisable(true);
            v_btnGravar.setDisable(false);
            v_btnRetornar.setDisable(false);

            v_nome.setEditable(false);
            v_documento.setEditable(false);
            v_tel_adc.setDisable(true);
            v_tel_del.setDisable(true);

            v_historico.setEditable(true);
            v_historico.requestFocus();
        });

        v_btnGravar.setOnAction(event -> {
            String Sql;
            if (insert) {
                Sql = "INSERT INTO visitas(v_rgimv, v_nome, v_documento, v_telefones, v_dthrsaida) VALUES ('%s', '%s', '%s', '%s', '%s');";
                Sql = String.format(Sql,
                        v_rgimv.getText().trim(),
                        v_nome.getText().trim(),
                        v_documento.getText().trim(),
                        new getTels(v_telefones).toString(),
                        v_dthrSaida.getText());
            } else {
                Sql = "UPDATE visitas SET v_dthrdevolucao='%s', v_historico='%s' WHERE id='%s';";
                Sql = String.format(Sql,
                        v_dthrDevolucao.getText(),
                        v_historico.getText(),
                        this.id);
            }

            try { conn.ExecutarComando(Sql); } catch (Exception ex) {ex.printStackTrace();}

            BarraController barra = (BarraController) VariaveisGlobais.loader.getController();
            barra.VisitasPend();
            barra = null;

            if (reserva) {
                Sql = "UPDATE imoveis SET i_reservado='%s', i_dtreserva='%s', i_reservtels='%s', i_dtfimreserv='%s' WHERE i_rgimv='%s';";
                Sql = String.format(Sql,
                            v_nome.getText(),
                            Dates.DateFormata("yyyy/MM/dd", DbMain.getDateTimeServer()),
                            new getTels(v_telefones).toString(),
                            Dates.DateFormata("yyyy/MM/dd", Dates.DateAdd(Dates.DIA, 1, DbMain.getDateTimeServer())),
                            v_rgimv.getText().trim()
                      );
                try { conn.ExecutarComando(Sql); } catch (Exception ex) {ex.printStackTrace();}
                reserva = false;
            }

            if (insert) {
                Collections dadm = VariaveisGlobais.getAdmDados();
                String texto = "Eu, " + v_nome.getText().trim().toUpperCase() + ", portador do Documento " + v_documento.getText() + ",\n" +
                               "estou de posse da(s) chave(s) do imóvel acima citado,\n" +
                               "ciente de todas responsabilidades inerentes,\n" +
                               "farei devolução da(s) mesma(s), no mesmo dia,\n" +
                               "até o horário máximo de 17h.";
                Object[] dados = {v_rgimv.getText(), v_endereco.getText(),texto};
                new Impressao(new BigInteger("0"), new String[][] {}).ImprimeSaidaPDF(dadm, dados, false);
            }

            v_btnSaida.setDisable(true);
            v_btnDevolucao.setDisable(true);
            v_btnGravar.setDisable(true);
            v_btnRetornar.setDisable(false);

            v_nome.setEditable(false);
            v_documento.setEditable(false);
            v_tel_adc.setDisable(true);
            v_tel_del.setDisable(true);
            v_historico.setEditable(false);
            v_historico.setText("");
            v_rgimv.requestFocus();
        });

    }

}
