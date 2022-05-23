package Relatorios.Proprietarios;

import Funcoes.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.controlsfx.control.textfield.TextFields;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ListaImoveis implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    @FXML private TextField rgprp;
    @FXML private TextField nmprop;
    @FXML private ToggleGroup selecao;
    @FXML private Button btnPrint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutocompleteProprietarios();

        btnPrint.setOnAction(event -> {
            ImoveisProprietario prop = PopulaDados(rgprp.getText());
            new Impressao(VariaveisGlobais.usuario).ImprimePropImvPDF(prop,true);
        });

        Platform.runLater(() -> rgprp.requestFocus());
    }

    private void AutocompleteProprietarios() {
        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios WHERE exclusao is null ORDER BY p_rgprp;";
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

        TextFields.bindAutoCompletion(rgprp, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(nmprop, possibleSuggestionsNome);

        rgprp.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                rgprp.setText(null);
                nmprop.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = rgprp.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, rgprp.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        nmprop.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        nmprop.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, nmprop.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = rgprp.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,nmprop.getText())) {
                        rgprp.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
            }
        });
    }

    private ImoveisProprietario PopulaDados(String rgprp) {
        // Dados do proprietário
        String pSQL = "select p_rgprp, p_nome, p_fisjur from proprietarios where p_rgprp = ? order by p_rgprp::integer;";

        String iSQL = "select i_rgprp, i_rgimv, i_tipo, Trim(i_end) || ', ' || i_num || ' - ' || i_cplto ender, i_bairro, i_cidade, i_estado, i_cep, i_situacao from imoveis where (exclusao is null) and i_rgprp::integer = ? and i_situacao = ? order by i_rgprp::integer, i_rgimv::integer;";
        String itSQL = "select i_rgprp, i_rgimv, i_tipo, Trim(i_end) || ', ' || i_num || ' - ' || i_cplto ender, i_bairro, i_cidade, i_estado, i_cep, i_situacao from imoveis where (exclusao is null) and i_rgprp::integer = ? order by i_rgprp::integer, i_rgimv::integer;";

        // Vazio / Ação / Etc...
        String vSQL = "select v_rgimv, v_nome, v_documento, to_char(to_date(v_dthrsaida,'DD/MM/YYYY'),'DD/MM/YYYY') v_data, v_historico from visitas where v_rgimv::integer = ? order by v_dthrsaida;";
        String cSQL = "select l_rgimv, l_dtbaixa, l_baixamotivo from locatarios where l_rgimv::integer = ? order by l_rgimv desc limit 1;";

        // Ocupado
        String oSQL = "select rgprp, rgimv, contrato, dtinicio, dtfim, dtaditamento, to_char(dtinicio, 'TMMonth') reajuste from carteira where rgimv::integer = ? order by rgimv desc limit 1;";
        //String mSQL = "select rgprp, rgimv, contrato, to_char(dtvencimento,'TMMon') as mes, mensal from movimento where aut_rec != 0 and contrato = ? and to_char(dtvencimento,'YYYY')::integer = to_char(now(),'YYYY')::integer order by to_char(dtvencimento,'MM')::integer;";
        String mSQL = "select rgprp, rgimv, contrato, to_char(dtvencimento,'TMMon') as mes, to_char(dtvencimento,'YYYY') as ano, mensal from movimento where aut_rec != 0 and contrato = ? and to_char(dtvencimento,'YYYY')::integer <= to_char(now(),'YYYY')::integer order by to_char(dtvencimento,'YYYY')::integer, to_char(dtvencimento,'MM')::integer;";

        RadioButton selectedRadioButton = (RadioButton) selecao.getSelectedToggle();
        String toogleGroupValue = selectedRadioButton.getText();

        ImoveisProprietario proprietario = null;
        ResultSet prs = conn.AbrirTabela(pSQL,ResultSet.CONCUR_READ_ONLY, new Object[][]{{"int", Integer.valueOf(rgprp)}});
        try {
            while (prs.next()) {
                // p_rgprp, p_nome, p_fisjur
                int prs_rgprp = 0;
                String prs_nome = "";
                String prs_fisjur = "F";

                try { prs_rgprp = prs.getInt("p_rgprp"); } catch (SQLException e) {}
                try { prs_nome = prs.getString("p_nome"); } catch (SQLException e) {}
                try { prs_fisjur = prs.getString("p_fisjur"); } catch (SQLException e) {}

                // Imoveis
                String imSQL = ""; Object[][] param = {};
                if (selecao.getSelectedToggle() != null) {
                    if (toogleGroupValue.equalsIgnoreCase("vazios")) {
                        // Vazios
                        imSQL = iSQL;
                        param = new Object[][] {{"int", Integer.valueOf(rgprp)}, {"string", "Vazio"}};
                    } else if (toogleGroupValue.equalsIgnoreCase("ocupados")) {
                        // Ocupados
                        imSQL = iSQL;
                        param = new Object[][] {{"int", Integer.valueOf(rgprp)}, {"string", "Ocupado"}};
                    } else {
                        // Todos
                        imSQL = itSQL;
                        param = new Object[][] {{"int", Integer.valueOf(rgprp)}};
                    }
                }

                ImoveisImovel[] imoveis = {};
                ResultSet irs = conn.AbrirTabela(imSQL, ResultSet.CONCUR_READ_ONLY, param);
                try {
                    while (irs.next()) {
                        // i_rgprp, i_rgimv, i_tipo, Trim(i_end) || ', ' || i_num || ' - ' || i_cplto ender, i_bairro, i_cidade, i_estado, i_cep, i_situacao
                        int irs_rgprp = 0;
                        int irs_rgimv = 0;
                        String irs_tipo = "";
                        String irs_end = "";
                        String irs_bairro = "";
                        String irs_cidade = "";
                        String irs_estado = "";
                        String irs_cep = "";
                        String irs_situacao = "Vazio";

                        try { irs_rgprp = Integer.valueOf(irs.getString("i_rgprp")); } catch (SQLException e) {}
                        try { irs_rgimv = Integer.valueOf(irs.getString("i_rgimv")); } catch (SQLException e) {}
                        try { irs_tipo = irs.getString("i_tipo"); } catch (SQLException e) {}
                        try { irs_end = irs.getString("ender"); } catch (SQLException e) {}
                        try { irs_bairro = irs.getString("i_bairro"); } catch (SQLException e) {}
                        try { irs_cidade = irs.getString("i_cidade"); } catch (SQLException e) {}
                        try { irs_estado = irs.getString("i_estado"); } catch (SQLException e) {}
                        try { irs_cep = irs.getString("i_cep"); } catch (SQLException e) {}
                        try { irs_situacao = irs.getString("i_situacao"); } catch (SQLException e) {}

                        ImoveisCarteira carteira = null;
                        ImoveisMovimento[] movimentos = {};
                        ImoveisBaixa baixa = null;
                        ImoveisVisitas[] visitas = {};
                        if (irs_situacao.equalsIgnoreCase("Ocupado")) {
                            // Ocupado - carteira - rgprp, rgimv, contrato, dtinicio, dtfim, dtaditamento, to_char(dtinicio, 'TMMonth') reajuste
                            ResultSet crs = conn.AbrirTabela(oSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", irs_rgimv}});
                            try {
                                while (crs.next()) {
                                    int crs_rgprp = 0;
                                    int crs_rgimv = 0;
                                    String crs_contrato = "";
                                    Date crs_dtinicio = null;
                                    Date crs_dtfim = null;
                                    Date crs_dtadito = null;
                                    String crs_reajuste = "";

                                    try { crs_rgprp = crs.getInt("rgprp"); } catch (SQLException e) {}
                                    try { crs_rgimv = crs.getInt("rgimv"); } catch (SQLException e) {}
                                    try { crs_contrato = crs.getString("contrato"); } catch (SQLException e) {}
                                    try { crs_dtinicio = crs.getDate("dtinicio"); } catch (SQLException e) {}
                                    try { crs_dtfim = crs.getDate("dtfim"); } catch (SQLException e) {}
                                    try { crs_dtadito = crs.getDate("dtaditamento"); } catch (SQLException e) {}
                                    try { crs_reajuste = crs.getString("reajuste"); } catch (SQLException e) {}

                                    carteira = new ImoveisCarteira(crs_rgprp, crs_rgimv, crs_contrato, crs_dtinicio, crs_dtfim, crs_dtadito, crs_reajuste);
                                }
                            } catch (SQLException cex) {}
                            DbMain.FecharTabela(crs);

                            // rgprp, rgimv, contrato, to_char(dtvencimento,'TMMon') as mes, mensal
                            ResultSet mrs = conn.AbrirTabela(mSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", carteira.getContrato()}});
                            try {
                                while (mrs.next()) {
                                    int mrs_rgprp = 0;
                                    int mrs_rgimv = 0;
                                    String mrs_contrato = "";
                                    String mrs_mes = "";
                                    String mrs_ano = "";
                                    BigDecimal mrs_aluguel = new BigDecimal("0");

                                    try { mrs_rgprp = mrs.getInt("rgprp"); } catch (SQLException e) {}
                                    try { mrs_rgimv = mrs.getInt("rgimv"); } catch (SQLException e) {}
                                    try { mrs_contrato = mrs.getString("contrato"); } catch (SQLException e) {}
                                    try { mrs_mes = mrs.getString("mes"); } catch (SQLException e) {}
                                    try { mrs_ano = mrs.getString("ano"); } catch (SQLException e) {}
                                    try { mrs_aluguel = mrs.getBigDecimal("mensal"); } catch (SQLException e) {}

                                    ImoveisMovimento movimento = new ImoveisMovimento(mrs_rgprp, mrs_rgimv, mrs_contrato, mrs_mes, mrs_ano, mrs_aluguel);
                                    movimentos = MovimentoAdd(movimentos,movimento);
                                }
                            } catch (SQLException mex) {}
                            DbMain.FecharTabela(mrs);
                        } else {
                            // Vazio, Ação, Etc...
                            // Baixa Locatarios - l_rgimv, l_dtbaixa, l_baixamotivo
                            ResultSet brs = conn.AbrirTabela(cSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", irs_rgimv}});
                            try {
                                while (brs.next()) {
                                    int brs_rgimv = 0;
                                    Date brs_dtbaixa = null;
                                    String brs_motivo = "";

                                    try { brs_rgimv = brs.getInt("l_rgimv"); } catch (SQLException e) {}
                                    try { brs_dtbaixa = brs.getDate("l_dtbaixa"); } catch (SQLException e) {}
                                    try { brs_motivo = brs.getString("l_baixamotivo"); } catch (SQLException e) {}

                                    baixa = new ImoveisBaixa(brs_rgimv, brs_dtbaixa, brs_motivo);
                                }
                            } catch (SQLException bex) {}
                            DbMain.FecharTabela(brs);

                            // Visitas - v_rgimv, v_nome, v_documento, to_char(to_date(v_dthrsaida,'DD/MM/YYYY'),'DD/MM/YYYY') v_data, v_historico
                            ResultSet vrs = conn.AbrirTabela(vSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", irs_rgimv}});
                            try {
                                while (vrs.next()) {
                                    int vrs_rgimv = 0;
                                    String vrs_nome = "";
                                    String vrs_doc = "";
                                    Date vrs_data = null;
                                    String vrs_hist = "";

                                    try { vrs_rgimv = vrs.getInt("v_rgimv"); } catch (SQLException e) {}
                                    try { vrs_nome = vrs.getString("v_nome"); } catch (SQLException e) {}
                                    try { vrs_doc = vrs.getString("v_documento"); } catch (SQLException e) {}
                                    try { vrs_data = Dates.toSqlDate( Dates.StringtoDate(vrs.getString("v_data"), "dd/MM/yyyy")); } catch (SQLException e) {}
                                    try { vrs_hist = vrs.getString("v_historico"); } catch (SQLException e) {}

                                    ImoveisVisitas visita = new ImoveisVisitas(vrs_rgimv, vrs_nome, vrs_doc, vrs_data, vrs_hist);
                                    visitas = VisitasAdd(visitas, visita);
                                }
                            } catch (SQLException vex) {}
                            DbMain.FecharTabela(vrs);
                        }
                        imoveis = ImovelAdd(imoveis, new ImoveisImovel(irs_rgprp, irs_rgimv, irs_end, irs_bairro, irs_cidade, irs_estado, irs_cep, irs_situacao,baixa ,visitas,carteira,movimentos));
                    }
                } catch (SQLException iex) {}
                DbMain.FecharTabela(irs);

                proprietario = new ImoveisProprietario(prs_rgprp, prs_nome, prs_fisjur, imoveis);
            }
        } catch (SQLException pe) {}
        DbMain.FecharTabela(prs);

        return proprietario;
    }

    private ImoveisVisitas[] VisitasAdd(ImoveisVisitas[] mArray, ImoveisVisitas value) {
        ImoveisVisitas[] temp = new ImoveisVisitas[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

    private ImoveisMovimento[] MovimentoAdd(ImoveisMovimento[] mArray, ImoveisMovimento value) {
        ImoveisMovimento[] temp = new ImoveisMovimento[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

    private ImoveisImovel[] ImovelAdd(ImoveisImovel[] mArray, ImoveisImovel value) {
        ImoveisImovel[] temp = new ImoveisImovel[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

    private ImoveisProprietario[] ProprietarioAdd(ImoveisProprietario[] mArray, ImoveisProprietario value) {
        ImoveisProprietario[] temp = new ImoveisProprietario[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

}
