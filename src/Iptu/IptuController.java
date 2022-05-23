package Iptu;

import Classes.findImovel;
import Classes.pimoveisModel;
import Funcoes.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class IptuController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private int rgprp;
    private int rgimv;
    private int id;
    private boolean achei = false;

    @FXML private AnchorPane tlaimv;
    @FXML private Spinner<Integer> iptu_ano;
    @FXML private TextField iptu_matricula;
    @FXML private TextField iptu_vencimento;
    @FXML private TextField iptu_rgimv;
    @FXML private TextField iptu_end;
    @FXML private TextField iptu_num;
    @FXML private TextField iptu_cplto;
    @FXML private TextField iptu_bairro;
    @FXML private TextField iptu_cidade;
    @FXML private TextField iptu_estado;
    @FXML private TextField iptu_cep;

    @FXML private HBox iptu_faixas;
    @FXML private Label iptu_lbl_faixas;
    @FXML private TextField iptu_faixa1;
    @FXML private TextField iptu_faixa2;
    @FXML private TextField iptu_faixa3;
    @FXML private TextField iptu_faixa4;

    @FXML private Label iptu_jan_jan;
    @FXML private TextField iptu_jan_vjan;
    @FXML private Label iptu_jan_fev;
    @FXML private TextField iptu_jan_vfev;
    @FXML private Label iptu_jan_mar;
    @FXML private TextField iptu_jan_vmar;
    @FXML private Label iptu_jan_abr;
    @FXML private TextField iptu_jan_vabr;

    @FXML private Label iptu_fev_fev;
    @FXML private TextField iptu_fev_vfev;
    @FXML private Label iptu_fev_mar;
    @FXML private TextField iptu_fev_vmar;
    @FXML private Label iptu_fev_abr;
    @FXML private TextField iptu_fev_vabr;
    @FXML private Label iptu_fev_mai;
    @FXML private TextField iptu_fev_vmai;

    @FXML private Label iptu_mar_mar;
    @FXML private TextField iptu_mar_vmar;
    @FXML private Label iptu_mar_abr;
    @FXML private TextField iptu_mar_vabr;
    @FXML private Label iptu_mar_mai;
    @FXML private TextField iptu_mar_vmai;
    @FXML private Label iptu_mar_jun;
    @FXML private TextField iptu_mar_vjun;

    @FXML private Label iptu_abr_abr;
    @FXML private TextField iptu_abr_vabr;
    @FXML private Label iptu_abr_mai;
    @FXML private TextField iptu_abr_vmai;
    @FXML private Label iptu_abr_jun;
    @FXML private TextField iptu_abr_vjun;
    @FXML private Label iptu_abr_jul;
    @FXML private TextField iptu_abr_vjul;

    @FXML private Label iptu_mai_mai;
    @FXML private TextField iptu_mai_vmai;
    @FXML private Label iptu_mai_jun;
    @FXML private TextField iptu_mai_vjun;
    @FXML private Label iptu_mai_jul;
    @FXML private TextField iptu_mai_vjul;
    @FXML private Label iptu_mai_ago;
    @FXML private TextField iptu_mai_vago;

    @FXML private Label iptu_jun_jun;
    @FXML private TextField iptu_jun_vjun;
    @FXML private Label iptu_jun_jul;
    @FXML private TextField iptu_jun_vjul;
    @FXML private Label iptu_jun_ago;
    @FXML private TextField iptu_jun_vago;
    @FXML private Label iptu_jun_set;
    @FXML private TextField iptu_jun_vset;

    @FXML private Label iptu_jul_jul;
    @FXML private TextField iptu_jul_vjul;
    @FXML private Label iptu_jul_ago;
    @FXML private TextField iptu_jul_vago;
    @FXML private Label iptu_jul_set;
    @FXML private TextField iptu_jul_vset;
    @FXML private Label iptu_jul_out;
    @FXML private TextField iptu_jul_vout;

    @FXML private Label iptu_ago_ago;
    @FXML private TextField iptu_ago_vago;
    @FXML private Label iptu_ago_set;
    @FXML private TextField iptu_ago_vset;
    @FXML private Label iptu_ago_out;
    @FXML private TextField iptu_ago_vout;
    @FXML private Label iptu_ago_nov;
    @FXML private TextField iptu_ago_vnov;

    @FXML private Label iptu_set_set;
    @FXML private TextField iptu_set_vset;
    @FXML private Label iptu_set_out;
    @FXML private TextField iptu_set_vout;
    @FXML private Label iptu_set_nov;
    @FXML private TextField iptu_set_vnov;
    @FXML private Label iptu_set_dez;
    @FXML private TextField iptu_set_vdez;

    @FXML private Label iptu_out_out;
    @FXML private TextField iptu_out_vout;
    @FXML private Label iptu_out_nov;
    @FXML private TextField iptu_out_vnov;
    @FXML private Label iptu_out_dez;
    @FXML private TextField iptu_out_vdez;
    @FXML private Label iptu_out_jan;
    @FXML private TextField iptu_out_vjan;

    @FXML private Label iptu_nov_nov;
    @FXML private TextField iptu_nov_vnov;
    @FXML private Label iptu_nov_dez;
    @FXML private TextField iptu_nov_vdez;
    @FXML private Label iptu_nov_jan;
    @FXML private TextField iptu_nov_vjan;
    @FXML private Label iptu_nov_fev;
    @FXML private TextField iptu_nov_vfev;

    @FXML private Label iptu_dez_dez;
    @FXML private TextField iptu_dez_vdez;
    @FXML private Label iptu_dez_jan;
    @FXML private TextField iptu_dez_vjan;
    @FXML private Label iptu_dez_fev;
    @FXML private TextField iptu_dez_vfev;
    @FXML private Label iptu_dez_mar;
    @FXML private TextField iptu_dez_vmar;

    @FXML private Button btGravar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        {
            iptu_ano.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2016, 2050,
                    Integer.parseInt("2016")));
            //iptu_ano.setEditable(true);

            EventHandler<KeyEvent> enterKeyEventHandler;

            enterKeyEventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            Integer.parseInt(iptu_ano.getEditor().textProperty().get());
                        }
                        catch (NumberFormatException e) {
                            iptu_ano.getEditor().textProperty().set("2016");
                        }
                    }
                }
            };

            iptu_ano.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);
        }

        MaskFieldUtil.maxField(iptu_matricula,20); MaskFieldUtil.numericField(iptu_matricula);
        iptu_matricula.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                iptu_matricula.setText(null);
            } else {
                // out focus
                achei = BuscaIptu(iptu_matricula.getText(), iptu_ano.getValue());
                if (!achei) {
                    //Alert msg = new Alert(Alert.AlertType.INFORMATION,"Não cadastrado!");
                    //msg.show();

                    String uSQL = "SELECT * FROM imoveis WHERE (exclusao is null) and POSITION('" + iptu_matricula.getText() + "' in i_matriculas) > 0;";
                    ResultSet irs = conn.AbrirTabela(uSQL,ResultSet.CONCUR_READ_ONLY);
                    boolean exists = false;
                    try {
                        while (irs.next()) {
                            exists = true;
                            rgprp = irs.getInt("i_rgprp");
                            rgimv = irs.getInt("i_rgimv");
                            iptu_rgimv.setText(String.valueOf(rgimv));

                            try {iptu_end.setText(irs.getString("i_end"));} catch (SQLException ex) {}
                            try {iptu_num.setText(irs.getString("i_num"));} catch (SQLException ex) {}
                            try {iptu_cplto.setText(irs.getString("i_cplto"));} catch (SQLException ex) {}
                            try {iptu_bairro.setText(irs.getString("i_bairro"));} catch (SQLException ex) {}
                            try {iptu_cidade.setText(irs.getString("i_cidade"));} catch (SQLException ex) {}
                            try {iptu_estado.setText(irs.getString("i_estado"));} catch (SQLException ex) {}
                            try {iptu_cep.setText(irs.getString("i_cep"));} catch (SQLException ex) {}
                        }
                    } catch (Exception ex) {}
                    try {DbMain.FecharTabela(irs);} catch (Exception ex) {}
                    btGravar.setDisable(false);
                    if (exists) return;

                    findImovel dialog = new findImovel();
                    Optional<pimoveisModel> result = dialog.findEnderecos();
                    result.ifPresent(b -> {
                        rgprp = Integer.valueOf(b.getRgprp());
                        rgimv = Integer.valueOf(b.getRgimv());

                        iptu_rgimv.setText(b.getRgimv());
                        // Dados do Imóvel
                        {
                            String sql = "SELECT * FROM imoveis WHERE (exclusao is null) and i_rgimv = '%d';";
                            sql = String.format(sql,rgimv);
                            ResultSet imv = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                            try {
                                while (imv.next()) {
                                    try {iptu_end.setText(imv.getString("i_end"));} catch (SQLException ex) {}
                                    try {iptu_num.setText(imv.getString("i_num"));} catch (SQLException ex) {}
                                    try {iptu_cplto.setText(imv.getString("i_cplto"));} catch (SQLException ex) {}
                                    try {iptu_bairro.setText(imv.getString("i_bairro"));} catch (SQLException ex) {}
                                    try {iptu_cidade.setText(imv.getString("i_cidade"));} catch (SQLException ex) {}
                                    try {iptu_estado.setText(imv.getString("i_estado"));} catch (SQLException ex) {}
                                    try {iptu_cep.setText(imv.getString("i_cep"));} catch (SQLException ex) {}
                                }
                                imv.close();
                            } catch (SQLException e) {}
                        }
                        btGravar.setDisable(false);

                        // Atualiza na tabela imoveis
                        String iSql = "UPDATE imoveis SET i_matriculas = CASE WHEN i_matriculas IS NULL OR Trim(i_matriculas) = '' THEN 'IPT,%s' ELSE i_matriculas || ';IPT,%s' END WHERE i_rgimv = '%s';";
                        iSql = String.format(iSql, iptu_matricula.getText().trim(), iptu_matricula.getText().trim(), rgimv);
                        try {conn.ExecutarComando(iSql);} catch (Exception e) {}
                    });

                }
            }
        });

        MaskFieldUtil.numericField(iptu_vencimento); MaskFieldUtil.maxField(iptu_vencimento, 2);

        iptu_faixa4.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (iptu_vencimento.getText().trim().equalsIgnoreCase("")) {
                    iptu_vencimento.requestFocus();
                    return;
                }
                MontaCalendario(
                        Integer.valueOf(iptu_vencimento.getText().trim()),
                        new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa1.getText().trim())),
                        new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa2.getText().trim())),
                        new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa3.getText().trim())),
                        new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa4.getText().trim()))
                );
            }
        });

        btGravar.setOnAction(e -> {
            String sql = ""; boolean retorno = true;
            if (achei) {
                // Update
                sql = "UPDATE iptu" +
                        "   SET vencimento=?, faixa1=?, faixa2=?, faixa3=?, faixa4=?" +
                        " WHERE id=?;";
            } else {
                // Insert
                sql = "INSERT INTO iptu(rgprp, rgimv, matricula, ano, vencimento, faixa1, faixa2, faixa3, faixa4)" +
                        "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            }

            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(sql);
                int nid = 1;
                if (!achei) {
                    pstmt.setInt(nid++,rgprp );
                    pstmt.setInt(nid++, rgimv );
                    pstmt.setString(nid++, iptu_matricula.getText().trim() );
                    pstmt.setInt(nid++, iptu_ano.getValue() );
                }
                pstmt.setInt(nid++, Integer.valueOf(iptu_vencimento.getText().trim()) );
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa1.getText().trim())) );
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa2.getText().trim())) );
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa3.getText().trim())) );
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa4.getText().trim())) );

                if (achei) {
                    pstmt.setInt(nid++, id);
                }
                pstmt.executeUpdate();
            } catch (SQLException ex) {ex.printStackTrace();}
            btGravar.setDisable(true);
            MontaCalendario(
                    Integer.valueOf(iptu_vencimento.getText().trim()),
                    new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa1.getText().trim())),
                    new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa2.getText().trim())),
                    new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa3.getText().trim())),
                    new BigDecimal(LerValor.Number2BigDecimal(iptu_faixa4.getText().trim()))
            );
            iptu_faixa1.setText(LerValor.StringValue2Currency(iptu_faixa1.getText()));
            iptu_faixa2.setText(LerValor.StringValue2Currency(iptu_faixa2.getText()));
            iptu_faixa3.setText(LerValor.StringValue2Currency(iptu_faixa3.getText()));
            iptu_faixa4.setText(LerValor.StringValue2Currency(iptu_faixa4.getText()));
        });
    }

    private boolean BuscaIptu(String mat, int ano) {
        String sql = "SELECT id, rgprp, rgimv, matricula, ano, vencimento, faixa1, faixa2, " +
                "       faixa3, faixa4, jan, fev, mar, abr, mai, jun, jul, ago, set, " +
                "       \"out\", nov, dez FROM iptu WHERE matricula = '%s' AND ano = %d;";
        sql = String.format(sql, mat, ano);
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Boolean isEmpty = true;
        try {
            while (rs.next()) {
                isEmpty = false;
                MontaTela(rs);
            }
            rs.close();
        } catch (SQLException e) {}
        btGravar.setDisable(isEmpty);

        return !isEmpty;
    }

    private void MontaTela(ResultSet rs) {
        int vcto = 0;
        BigDecimal faixa1 = null, faixa2 = null, faixa3 = null, faixa4 = null;
        try {vcto =rs.getInt("vencimento"); iptu_vencimento.setText(String.valueOf(vcto));} catch (SQLException e) {}
        try {id = rs.getInt("id");} catch (SQLException e) {id = -1;}
        try {rgprp = rs.getInt("rgprp");} catch (SQLException e) {rgprp = -1;}
        try {rgimv = rs.getInt("rgimv"); iptu_rgimv.setText(String.valueOf(rgimv));} catch (SQLException e) {rgimv = -1;}

        // Dados do Imóvel
        {
            String sql = "SELECT * FROM imoveis WHERE (exclusao is null) and i_rgimv = '%d';";
            sql = String.format(sql,rgimv);
            ResultSet imv = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (imv.next()) {
                    try {iptu_end.setText(imv.getString("i_end"));} catch (SQLException ex) {}
                    try {iptu_num.setText(imv.getString("i_num"));} catch (SQLException ex) {}
                    try {iptu_cplto.setText(imv.getString("i_cplto"));} catch (SQLException ex) {}
                    try {iptu_bairro.setText(imv.getString("i_bairro"));} catch (SQLException ex) {}
                    try {iptu_cidade.setText(imv.getString("i_cidade"));} catch (SQLException ex) {}
                    try {iptu_estado.setText(imv.getString("i_estado"));} catch (SQLException ex) {}
                    try {iptu_cep.setText(imv.getString("i_cep"));} catch (SQLException ex) {}
                }
                imv.close();
            } catch (SQLException e) {}
        }

        try {faixa1 = rs.getBigDecimal("faixa1"); iptu_faixa1.setText(new DecimalFormat("#,##0.00").format(faixa1));} catch (SQLException e) {}
        try {faixa2 = rs.getBigDecimal("faixa2"); iptu_faixa2.setText(new DecimalFormat("#,##0.00").format(faixa2));} catch (SQLException e) {}
        try {faixa3 = rs.getBigDecimal("faixa3"); iptu_faixa3.setText(new DecimalFormat("#,##0.00").format(faixa3));} catch (SQLException e) {}
        try {faixa4 = rs.getBigDecimal("faixa4"); iptu_faixa4.setText(new DecimalFormat("#,##0.00").format(faixa4));} catch (SQLException e) {}

        MontaCalendario(vcto, faixa1, faixa2, faixa3, faixa4);
    }

    private void MontaCalendario(int vcto, BigDecimal faixa1, BigDecimal faixa2, BigDecimal faixa3, BigDecimal faixa4) {
        // Janeiro
        iptu_jan_jan.setText(String.valueOf(vcto) + "/01/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jan_vjan.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_jan_fev.setText(String.valueOf(vcto > 28 ? 28 : vcto) + "/02/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jan_vfev.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_jan_mar.setText(String.valueOf(vcto) + "/03/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jan_vmar.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_jan_abr.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/04/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jan_vabr.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Fevereiro
        iptu_fev_fev.setText(String.valueOf(vcto > 28 ? 28 : vcto) + "/02/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_fev_vfev.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_fev_mar.setText(String.valueOf(vcto) + "/03/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_fev_vmar.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_fev_abr.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/04/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_fev_vabr.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_fev_mai.setText(String.valueOf(vcto) + "/05/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_fev_vmai.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Março
        iptu_mar_mar.setText(String.valueOf(vcto) + "/03/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mar_vmar.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_mar_abr.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/04/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mar_vabr.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_mar_mai.setText(String.valueOf(vcto) + "/05/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mar_vmai.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_mar_jun.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/06/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mar_vjun.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Abril
        iptu_abr_abr.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/04/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_abr_vabr.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_abr_mai.setText(String.valueOf(vcto) + "/05/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_abr_vmai.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_abr_jun.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/06/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_abr_vjun.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_abr_jul.setText(String.valueOf(vcto) + "/07/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_abr_vjul.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Maio
        iptu_mai_mai.setText(String.valueOf(vcto) + "/05/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mai_vmai.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_mai_jun.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/06/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mai_vjun.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_mai_jul.setText(String.valueOf(vcto) + "/07/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mai_vjul.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_mai_ago.setText(String.valueOf(vcto) + "/08/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_mai_vago.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Junho
        iptu_jun_jun.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/06/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jun_vjun.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_jun_jul.setText(String.valueOf(vcto) + "/07/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jun_vjul.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_jun_ago.setText(String.valueOf(vcto) + "/08/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jun_vago.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_jun_set.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/09/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jun_vset.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Julho
        iptu_jul_jul.setText(String.valueOf(vcto) + "/07/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jul_vjul.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_jul_ago.setText(String.valueOf(vcto) + "/08/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jul_vago.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_jul_set.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/09/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jul_vset.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_jul_out.setText(String.valueOf(vcto) + "/10/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_jul_vout.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Agosto
        iptu_ago_ago.setText(String.valueOf(vcto) + "/08/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_ago_vago.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_ago_set.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/09/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_ago_vset.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_ago_out.setText(String.valueOf(vcto) + "/10/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_ago_vout.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_ago_nov.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/11/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_ago_vnov.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Setembro
        iptu_set_set.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/09/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_set_vset.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_set_out.setText(String.valueOf(vcto) + "/10/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_set_vout.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_set_nov.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/11/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_set_vnov.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_set_dez.setText(String.valueOf(vcto) + "/12/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_set_vdez.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Outubro
        iptu_out_out.setText(String.valueOf(vcto) + "/10/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_out_vout.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_out_nov.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/11/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_out_vnov.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_out_dez.setText(String.valueOf(vcto) + "/12/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_out_vdez.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_out_jan.setText(String.valueOf(vcto) + "/01/" + Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_out_vjan.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Novembro
        iptu_nov_nov.setText(String.valueOf(vcto > 30 ? 30 : vcto) + "/11/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_nov_vnov.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_nov_dez.setText(String.valueOf(vcto) + "/12/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_nov_vdez.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_nov_jan.setText(String.valueOf(vcto) + "/01/" + Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_nov_vjan.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_nov_fev.setText(String.valueOf(vcto > 28 ? 28 : vcto) + "/02/" + Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_nov_vfev.setText(new DecimalFormat("#,##0.00").format(faixa4));

        // Dezembro
        iptu_dez_dez.setText(String.valueOf(vcto) + "/12/" + Dates.DateFormata("yyyy", DbMain.getDateTimeServer()));
        iptu_dez_vdez.setText(new DecimalFormat("#,##0.00").format(faixa1));
        iptu_dez_jan.setText(String.valueOf(vcto) + "/01/"+ Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_dez_vjan.setText(new DecimalFormat("#,##0.00").format(faixa2));
        iptu_dez_fev.setText(String.valueOf(vcto > 28 ? 28 : vcto) + "/02/" + Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_dez_vfev.setText(new DecimalFormat("#,##0.00").format(faixa3));
        iptu_dez_mar.setText(String.valueOf(vcto) + "/03/" + Dates.DateFormata("yyyy", Dates.DateAdd(Dates.ANO, 1, DbMain.getDateTimeServer())));
        iptu_dez_vmar.setText(new DecimalFormat("#,##0.00").format(faixa4));
    }
}
