package NotaFiscal;

import Calculos.Multas;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NotaFiscal implements Initializable {
    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField nfe_rgprp;
    @FXML private TextField nfe_nome;

    @FXML private TableView<nfeImoveis> nfe_imoveis;
    @FXML private TableColumn<nfeImoveis, Integer> nfe_imoveis_id;
    @FXML private TableColumn<nfeImoveis, String> nfe_imoveis_rgimv;
    @FXML private TableColumn<nfeImoveis, String> nfe_imoveis_contrato;
    @FXML private TableColumn<nfeImoveis, String> nfe_imoveis_endereco;
    @FXML private TableColumn<nfeImoveis, Date> nfe_imoveis_vencto;
    @FXML private TableColumn<nfeImoveis, BigDecimal> nfe_imoveis_aluguel;
    @FXML private TableColumn<nfeImoveis, Integer> nfe_imoveis_ntfiscal;
    @FXML private TableColumn<nfeImoveis, Integer> nfe_imoveis_comissao;

    @FXML private Spinner<String> nfe_mes;
    @FXML private Spinner<Integer> nfe_ano;

    @FXML private Button nfe_btnGerar;
    @FXML private TextField nfe_proxnfe;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String nmes = Dates.Month(DbMain.getDateTimeServer());
        SpinnerValueFactory Value = new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"));
        Value.setValue(nmes);
        nfe_mes.setValueFactory(Value);
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2016, 2050, 2018);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        nfe_ano.setValueFactory(ano);

        nfe_ano.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;
            ListarAlugueis(nfe_rgprp.getText());
        });


        AutocompleteRegistroNome();
        try {nfe_proxnfe.setText(FuncoesGlobais.StrZero(conn.LerParametros("NOTAFISCAL"),6));} catch (SQLException e) {}
        nfe_proxnfe.setEditable(false);

        // Botão Gerar
        nfe_btnGerar.setOnAction(event -> {
            ObservableList<nfeImoveis> selecteds = nfe_imoveis.getSelectionModel().getSelectedItems();
            if (selecteds.size() <= 0) {
                JOptionPane.showMessageDialog(null, "Você deve selecionar um vencimento.");
                return;
            }

            // Verifica se a nota já foi lançada e status é diferente de enviada
            int wNotaFiscal = selecteds.get(0).getNtfiscal();
            if (wNotaFiscal > 0) {
                // Totaliza e Gera XML
                float ttNtFiscal = 0; boolean jaemitida = false;
                for (nfeImoveis i : selecteds) {
                    if (i.getNtfiscal() > 0) {jaemitida = true; break;}
                    ttNtFiscal += i.getAluguel().floatValue() * (i.getComissao() / 100D);
                }

                if (jaemitida) {
                    JOptionPane.showMessageDialog(null, "Você selecionou item que já emitiu nota!");
                    return;
                }
                
                nfse_xml nfse = new nfse_xml();
                //nfse.set...();
                nfse.setNota_numero(nfe_proxnfe.getText());
                nfse.nfse_102(nfe_proxnfe.getText());
                
                try {conn.GravarParametros(new String[] {"NOTAFISCAL",String.valueOf(Integer.valueOf(nfe_proxnfe.getText()) + 1),"NUMERICO"}); } catch (SQLException ex) {}
                for (nfeImoveis i : selecteds) {
                    conn.ExecutarComando("UPDATE FROM movimento SET notafiscal = ? WHERE id = ?;", new Object[][] {{"bigint", new BigInteger(nfe_proxnfe.getText())}, {"bigint", new BigInteger(String.valueOf(i.getId()))}});
                }
                JOptionPane.showMessageDialog(null, "Nota gerada!");
                ListarAlugueis(nfe_rgprp.getText());
            }            
        });

        Platform.runLater(() -> nfe_rgprp.requestFocus());
    }

    private void ListarAlugueis(String rgprp) {
        List<nfeImoveis> data = new ArrayList<nfeImoveis>();
        ResultSet imv;
        String qSQL = "SELECT m.id, m.rgimv, m.contrato, i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro AS endereco, m.dtvencimento, m.mensal, m.notafiscal FROM movimento m INNER JOIN imoveis i ON i.i_rgprp = m.rgprp AND i.i_rgimv = m.rgimv WHERE (exclusao is null) and m.dtrecebimento Is Null AND m.rgprp = '%s' ORDER BY m.dtvencimento;";
        qSQL = String.format(qSQL, rgprp);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                Integer gid = null;
                String grgimv = null;
                String gcontrato = null;
                String gendereco = null;
                Date gvencto = null;
                BigDecimal galuguel = null;
                Integer gnotafiscal = null;
                Integer gcomissao = null;
                try {gid = imv.getInt("id");} catch (SQLException e) {}
                try {grgimv = imv.getString("rgimv");} catch (SQLException e) {}
                try {gcontrato = imv.getString("contrato");} catch (SQLException e) {}
                try {gendereco = imv.getString("endereco");} catch (SQLException e) {}
                try {gvencto = imv.getDate("dtvencimento");} catch (SQLException e) {}
                try {galuguel = imv.getBigDecimal("mensal");} catch (SQLException e) {}
                try {gnotafiscal = imv.getInt("notafiscal");} catch (SQLException e) {}

                // Setar Comissão
                new Multas(nfe_rgprp.getText(),grgimv);
                double perComis = VariaveisGlobais.co;
                gcomissao =  (int)perComis;

                data.add(new nfeImoveis(gid, grgimv, gcontrato, gendereco, gvencto, galuguel, gnotafiscal, gcomissao));
            }
            imv.close();
        } catch (SQLException e) {}

        nfe_imoveis_id.setCellValueFactory(new PropertyValueFactory("id"));
        nfe_imoveis_rgimv.setCellValueFactory(new PropertyValueFactory("rgimv"));
        nfe_imoveis_contrato.setCellValueFactory(new PropertyValueFactory("contrato"));
        nfe_imoveis_endereco.setCellValueFactory(new PropertyValueFactory("endereco"));
        nfe_imoveis_vencto.setCellValueFactory(new PropertyValueFactory("vencto"));
        nfe_imoveis_aluguel.setCellValueFactory(new PropertyValueFactory("aluguel"));
        nfe_imoveis_ntfiscal.setCellValueFactory(new PropertyValueFactory("ntfiscal"));
        nfe_imoveis_comissao.setCellValueFactory(new PropertyValueFactory("comissao"));

        nfe_imoveis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nfe_imoveis.setItems(FXCollections.observableArrayList(data));
        nfe_imoveis.setEditable(false);

        nfe_imoveis.setOnMousePressed(event -> {
            TableView.TableViewSelectionModel<nfeImoveis> select = nfe_imoveis.getSelectionModel();
            if (!select.getSelectedItems().isEmpty() && select.getSelectedItems().size() >= 1) {
                for (nfeImoveis o : select.getSelectedItems()) {
                    int gnotafiscal = o.getNtfiscal();
                    if (gnotafiscal > 0) {
                        nfe_imoveis.getSelectionModel().clearSelection();
                        for (nfeImoveis column : nfe_imoveis.getItems()) {
                            if (column.getNtfiscal() == gnotafiscal) {
                                nfe_imoveis.getSelectionModel().select(column);
                            }
                        }
                    } else {
                        nfe_imoveis.getSelectionModel().select(select.getSelectedIndex());
                    }
                }
            }
        });
    }

    private void AutocompleteRegistroNome() {
        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios ORDER BY p_rgprp;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("p_rgprp");} catch (SQLException e) {}
                try {qnome = imv.getString("p_nome");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        if (_possibleSuggestionsContrato.length > 0) {
            TextFields.bindAutoCompletion(nfe_rgprp, possibleSuggestionsContrato);
            TextFields.bindAutoCompletion(nfe_nome, possibleSuggestionsNome);
        }
        
        nfe_rgprp.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                nfe_rgprp.setText(null);
                nfe_nome.setText(null);

                // Zera Grid
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = nfe_rgprp.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, nfe_rgprp.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        nfe_nome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        nfe_nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, nfe_nome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = nfe_rgprp.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,nfe_nome.getText())) {
                        nfe_rgprp.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }

                // Popula Grid
            }
        });
    }
}
