/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios.Carteira;

import Classes.paramEvent;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Locatarios.Aditamento.AditarController;
import entrada.PasswordDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static Funcoes.FuncoesGlobais.StrZero;

/**
 * FXML Controller class
 *
 * @author supervisor
 */
public class CarteiraController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String contrato = ""; int rgimv = 0, rgprp = 0;
    int idcarteira = -1;

    private String cpoMensalVencimentoTag = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField dtInicio;
    @FXML private TextField dtFim;
    @FXML private TextField dtAditamento;
    @FXML private ScrollPane cpoTaxasScroll;
    @FXML private AnchorPane cpoTaxasWorkArea;
    @FXML private Label cpoMensalDesc;
    @FXML private TextField cpoMensalCota;
    @FXML private TextField cpoMensalValor;
    @FXML private DatePicker cpoMensalVencimento;
    @FXML private TextField cpoRef;
    @FXML private GridPane gridFields;
    @FXML private Button btnAditar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event1 -> {
            Object[] param = event1.sparam;
            this.rgprp = Integer.valueOf((String) param[1]);
            this.rgimv = Integer.valueOf((String) param[2]);
            this.contrato = (String)param[3];
        });

        Platform.runLater(() -> {
            lerCarteira();
            lerFields();

/*
            BooleanBinding obd = Bindings.createBooleanBinding(() ->
                Dates.DateDiff(Dates.DIA, Dates.StringtoDate(dtFim.getText(),"dd/MM/yyyy"), new java.util.Date()) >= 365,
                dtAditamento.textProperty().isEmpty()
            );
*/

            btnAditar.disableProperty().bind(dtAditamento.textProperty().isEmpty().not());
        });

        btnAditar.setOnAction(event -> {
            try { ChamaTela("Aditamento", "/Locatarios/Aditamento/Aditar.fxml","prop.png"); } catch (Exception ex) {}
        });

        cpoMensalVencimento.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // enter focus
                } else {
                    // leave focus
                    if (VariaveisGlobais.cargo.equalsIgnoreCase("GER") || VariaveisGlobais.cargo.equalsIgnoreCase("ADM")) {
                        if (cpoMensalVencimento.getValue() != null) {
                            String dDtvectoAtual = Dates.DateFormata("yyyy-MM-dd", Dates.toDate(cpoMensalVencimento.getValue()));
                            String dDtvectoAnter = Dates.StringtoString(cpoMensalVencimentoTag, "dd/MM/yyyy", "yyyy-MM-dd");
                            long ddif = Dates.DtDiff(Dates.DIA, dDtvectoAnter, dDtvectoAtual);
                            if (ddif > 0) {
                                System.out.println("Diferença de > 0  = " + ddif + " dia(s)");
                                PasswordDialog pd = new PasswordDialog();
                                Optional<Boolean> result = pd.showAndWait();
                                result.ifPresent(password -> {
                                    System.out.println(password);
                                    if (password) {
                                        Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Gera diferença para o próximo vencimento?", new ButtonType("Sim"), new ButtonType("Não"));
                                        Optional<ButtonType> result2 = msg.showAndWait();
                                        if (result2.get().getText().equals("Sim")) {
                                            int rmes = 0, rano = 0;
                                            rmes = Integer.parseInt(cpoRef.getText().subSequence(0,2).toString()) + 1;
                                            rano = Integer.parseInt(cpoRef.getText().substring(3).toString());
                                            String tmpRef = "";
                                            if (rmes > 12) { rmes = 1; rano += 1; }
                                            tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);

                                            float mensal = Float.valueOf(cpoMensalValor.getText());
                                            mensal = (mensal / 30) * ddif;

                                            String sql = "INSERT INTO descdif(tipo, rgprp, rgimv, contrato, descricao, cota, referencia, valor, dtlanc, usr_lanc, selected) " +
                                                    "VALUES ('C', '%s', '%s', '%s', '%s dia(s)', '01/01', '%s', '%s', '%s', '%s', 'True');";
                                            sql = String.format(sql,
                                                    rgprp,
                                                    rgimv,
                                                    contrato,
                                                    ddif,
                                                    tmpRef,
                                                    mensal,
                                                    Dates.DateFormata("yyyy-MM-dd", new java.util.Date()),
                                                    VariaveisGlobais.usuario
                                            );

                                            conn.ExecutarComando(sql);
                                            //System.out.println(sql);
                                        }

                                        String sql = "UPDATE carteira SET dtvencimento = '%s' WHERE rgprp = '%s' and rgimv = '%s' and contrato = '%s';";
                                        sql = String.format(sql,
                                                Dates.DateFormata("yyyy-MM-dd", Dates.toDate(cpoMensalVencimento.getValue())),
                                                rgprp,
                                                rgimv,
                                                contrato
                                        );
                                        conn.ExecutarComando(sql);
                                        System.out.println(sql);

                                        cpoMensalVencimentoTag = Dates.DateFormata("dd/MM/yyyy",Dates.toDate(cpoMensalVencimento.getValue()));
                                    }
                                });
                            } else if (ddif < 0) {
                                new Alert(Alert.AlertType.INFORMATION, "Vencimento não pode ser menor que " + cpoMensalVencimentoTag).show();
                                cpoMensalVencimento.getEditor().setText(cpoMensalVencimentoTag);
                                cpoMensalVencimento.setValue(Dates.toLocalDate(Dates.StringtoDate(cpoMensalVencimentoTag, "dd-MM-yyyy")));
                                cpoMensalVencimento.requestFocus();
                                return;
                            }
                        }
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Usuário não tem permissão para mudar vencimento!\n\nSolicite a um Gerente ou Administrador.").show();
                        cpoMensalVencimento.getEditor().setText(cpoMensalVencimentoTag);
                        cpoMensalVencimento.setValue(Dates.toLocalDate(Dates.StringtoDate(cpoMensalVencimentoTag, "dd-MM-yyyy")));
                        cpoMensalVencimento.requestFocus();
                        return;
                    }
                }
            }
        });
    }

    private boolean lerCarteira() {
        boolean carteira = false;
        String cSql = "SELECT id, rgprp, rgimv, contrato, dtinicio, dtfim, dtaditamento, cota, mensal, dtvencimento, referencia FROM CARTEIRA WHERE contrato = '%s';";
        cSql = String.format(cSql, this.contrato);
        ResultSet rs = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                carteira = true;

                try {idcarteira = rs.getInt("id");} catch (SQLException e) {idcarteira = -1;}
                Date tdtInicio = null, tdtFim = null, tdtAditamento = null;
                try {tdtInicio = rs.getDate("dtinicio");} catch (SQLException e) {}
                if (tdtInicio != null) dtInicio.setText(Dates.DateFormata("dd/MM/yyyy", tdtInicio));

                try {tdtFim = rs.getDate("dtfim");} catch (SQLException e) {}
                if (tdtFim != null) dtFim.setText(Dates.DateFormata("dd/MM/yyyy", tdtFim));

                try {tdtAditamento = rs.getDate("dtaditamento");} catch (SQLException e) {}
                if (tdtAditamento != null) dtAditamento.setText(Dates.DateFormata("dd/MM/yyyy", tdtAditamento));

                String tcota = null, tmensal = null, tref = null;
                try {tcota = rs.getString("cota");} catch (SQLException e) {}
                if (tcota != null) cpoMensalCota.setText(tcota);

                cpoMensalDesc.setText("Aluguel");

                try {tmensal = rs.getBigDecimal("mensal").toString();} catch (SQLException e) {}
                if (tmensal != null) cpoMensalValor.setText(tmensal);

                Date tdtVencimento = null;
                try {tdtVencimento= rs.getDate("dtvencimento");} catch (SQLException e) {}
                if (tdtVencimento != null) {
                    cpoMensalVencimento.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", tdtVencimento));
                    cpoMensalVencimento.setValue(Dates.toLocalDate(tdtVencimento));
                    cpoMensalVencimentoTag = Dates.DateFormata("dd/MM/yyyy", tdtVencimento);
                }

                try {tref = rs.getString("referencia");} catch (SQLException e) {}
                if (tref != null) cpoRef.setText(tref);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}

        return carteira;
    }

    private void lerFields() {
        int linha = 1;
        String cpoRefer = cpoRef.getText();

        // Desc/Dif Aluguel
        BigDecimal desal = new BigDecimal("0");
        BigDecimal difal = new BigDecimal("0");
        String tSql = "SELECT id, tipo, rgprp, rgimv, contrato, descricao, cota, referencia, " +
                "       valor FROM descdif WHERE contrato = '%s' AND referencia = '%s' AND dtrecebimento IS NULL;";
        tSql = String.format(tSql, contrato, cpoRefer);
        ResultSet rs = conn.AbrirTabela(tSql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Color cor;
                if (rs.getString("tipo").equalsIgnoreCase("C")) cor = Color.GREEN; else cor = Color.RED;
                linha = MontaGrade(
                        linha,
                        new Object[] {"", Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.CENTER},
                        new Object[] {(rs.getString("tipo").equalsIgnoreCase("C") ? "Diferença" : "Desconto") + " Aluguel", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {rs.getString("descricao"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {rs.getString("cota"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.CENTER},
                        new Object[] {rs.getBigDecimal("valor").toPlainString(), Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.RIGHT}
                );
                if (rs.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    difal = difal.add(rs.getBigDecimal("valor"));
                } else {
                    // Desconto
                    desal = desal.add(rs.getBigDecimal("valor"));
                }
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}

        BigDecimal irenda = new Calculos.Irrf().Irrf(String.valueOf(rgprp),contrato,cpoRefer,new BigDecimal(cpoMensalValor.getText()),difal,desal);
        if (irenda.compareTo(BigDecimal.ZERO) == 1) {
            Color cor = Color.RED;
            linha = MontaGrade(
                    linha,
                    new Object[]{"", Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.CENTER},
                    new Object[]{"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{"IRRF", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{cpoRefer, Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.CENTER},
                    new Object[]{irenda.toPlainString(), Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.RIGHT}
            );
        }

        // Taxas
        tSql = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, t.precampo, t.campo, (SELECT c.descricao FROM campos c WHERE c.codigo = campo) AS descricao, t.poscampo, t.cota, " +
                "       t.valor, t.dtvencimento, t.referencia, t.retencao, t.tipo" +
                "  FROM taxas t WHERE t.contrato = '%s' AND t.referencia = '%s' AND t.dtrecebimento IS NULL;";
        tSql = String.format(tSql, contrato, cpoRefer);
        rs = conn.AbrirTabela(tSql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Color cor;
                if (rs.getString("tipo").equalsIgnoreCase("C")) cor = Color.GREEN; else cor = Color.RED;
                linha = MontaGrade(
                        linha,
                        new Object[] {rs.getString("campo"), Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.CENTER},
                        new Object[] {rs.getString("precampo"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {rs.getString("descricao"), Font.font("Arial", FontWeight.NORMAL, 12), rs.getBoolean("retencao") ? Color.BLUE : Color.BLACK, HPos.LEFT},
                        new Object[] {rs.getString("poscampo"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {rs.getString("cota"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.CENTER},
                        new Object[] {rs.getBigDecimal("valor").toPlainString(), Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.RIGHT}
                );
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}

        // Seguros
        tSql = "SELECT  cota, valor FROM seguros WHERE contrato = '%s' AND referencia = '%s' AND dtrecebimento IS NULL;";
        tSql = String.format(tSql, contrato, cpoRefer);
        rs = conn.AbrirTabela(tSql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Color cor = Color.RED;
                linha = MontaGrade(
                        linha,
                        new Object[] {"", Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.CENTER},
                        new Object[] {"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {"SEGURO", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                        new Object[] {rs.getString("cota"), Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.CENTER},
                        new Object[] {rs.getBigDecimal("valor").toPlainString(), Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.RIGHT}
                );
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}

        // IPTU
        BigDecimal faixa = null;
        String rmes = cpoRefer.substring(0,2);
        String rano = cpoRefer.substring(3);
        String[] ciptu = new Calculos.Iptu().Iptu(String.valueOf(rgimv),cpoRefer.trim());
        int iptuId = 0;
        String iptuMes = null;
        String iptuRef = null;
        String vrIptu = null;
        if (ciptu != null) {
            iptuId = Integer.valueOf(ciptu[0] == null ? "-1" : ciptu[0]);
            iptuMes = ciptu[1];
            iptuRef = ciptu[2];
            vrIptu = ciptu[3].indexOf(",") < 0 ? ciptu[3].concat(",00") : ciptu[3];
        }
        if (vrIptu != null && !vrIptu.trim().equalsIgnoreCase("0,00")) {
            Color cor = Color.RED;
            linha = MontaGrade(
                    linha,
                    new Object[]{"", Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.CENTER},
                    new Object[]{"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{"IPTU", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{"", Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.LEFT},
                    new Object[]{iptuRef, Font.font("Arial", FontWeight.NORMAL, 12), cor, HPos.CENTER},
                    new Object[]{vrIptu, Font.font("Arial", FontWeight.BOLD, 12), cor, HPos.RIGHT}
            );
        }
    }

    private int MontaGrade(int linha, Object[] id, Object[] pre, Object[] cpo, Object[] pos, Object[] cotaparc, Object[] valor) {
        int coluna = 0;
        Label fieldid = new Label((String)id[0]);
        fieldid.getStyleClass().add("label2d_center"); fieldid.setPrefSize(60,24);
        if (id.length > 2) fieldid.setFont((Font)id[1]); else fieldid.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        if (id.length > 3) fieldid.setStyle("-fx-text-fill: " + ColorConvert((Color)id[2]) + ";");
        if (id.length > 4) GridPane.setHalignment(fieldid, (HPos)id[3]); else GridPane.setHalignment(fieldid, HPos.CENTER);
        gridFields.add(fieldid, coluna++, linha);

        Label fieldpre = new Label((String)pre[0]);
        fieldpre.getStyleClass().add("label2d_left"); fieldpre.setPrefSize(140,24);
        if (pre.length > 2) fieldpre.setFont((Font)pre[1]); else fieldpre.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        if (pre.length > 3) fieldpre.setStyle("-fx-text-fill: " + ColorConvert((Color)pre[2]) + ";");
        if (pre.length > 4) GridPane.setHalignment(fieldpre, (HPos)pre[3]); else GridPane.setHalignment(fieldpre, HPos.LEFT);
        gridFields.add(fieldpre,coluna++,linha);

        Label fieldcpo = new Label((String)cpo[0]);
        fieldcpo.getStyleClass().add("label2d_left"); fieldcpo.setPrefSize(140,24);
        if (cpo.length > 2) fieldcpo.setFont((Font)cpo[1]); else fieldcpo.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        if (cpo.length > 3) fieldcpo.setStyle("-fx-text-fill: " + ColorConvert((Color)cpo[2]) + ";");
        if (cpo.length > 4) GridPane.setHalignment(fieldcpo, (HPos)cpo[3]); else GridPane.setHalignment(fieldcpo, HPos.LEFT);
        gridFields.add(fieldcpo,coluna++,linha);

        Label fieldpos = new Label((String)pos[0]);
        fieldpos.getStyleClass().add("label2d_left"); fieldpos.setPrefSize(125,24);
        if (pos.length > 2) fieldpos.setFont((Font)pos[1]); else fieldpos.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        if (pos.length > 3) fieldpos.setStyle("-fx-text-fill: " + ColorConvert((Color)pos[2]) + ";");
        if (pos.length > 4) GridPane.setHalignment(fieldpos, (HPos)pos[3]); else GridPane.setHalignment(fieldpos, HPos.LEFT);
        gridFields.add(fieldpos,coluna++,linha);

        Label fieldcota = new Label((String)cotaparc[0]);
        fieldcota.getStyleClass().add("label2d_center"); fieldcota.setPrefSize(100,24);
        if (cotaparc.length > 2) fieldcota.setFont((Font)cotaparc[1]); else fieldcota.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        if (cotaparc.length > 3) fieldcota.setStyle("-fx-text-fill: " + ColorConvert((Color)cotaparc[2]) + ";");
        if (cotaparc.length > 4) GridPane.setHalignment(fieldcota, (HPos)cotaparc[3]); else GridPane.setHalignment(fieldcota, HPos.CENTER);
        gridFields.add(fieldcota,coluna++,linha);

        Label fieldvlr = new Label((String)valor[0]);
        fieldvlr.getStyleClass().add("label2d_right"); fieldvlr.setPrefSize(125,24);
        if (valor.length > 2) fieldvlr.setFont((Font)valor[1]); else fieldvlr.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        if (valor.length > 3) fieldvlr.setStyle("-fx-text-fill: " + ColorConvert((Color)valor[2]) + ";");
        if (valor.length > 4) GridPane.setHalignment(fieldvlr, (HPos)valor[3]); else GridPane.setHalignment(fieldvlr, HPos.RIGHT);
        gridFields.add(fieldvlr,coluna,linha);

        return ++linha;
    }

    private String ColorConvert(Color cor) {
        String scor = cor.toString();
        return  "#" + scor.substring(2,8);
    }

    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        AnchorPane root = null;

        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource(url));
        } catch (Exception e) {e.printStackTrace();}
        root = loader.load();
        AditarController controller = loader.getController();
        controller.setRgprp(String.valueOf(rgprp));
        controller.setRgimv(String.valueOf(rgimv));
        controller.setContrato(contrato);
        controller.setDtFim(dtFim.getText());
        controller.setdVencto(cpoMensalVencimento.getEditor().getText());
        controller.setCotaAdito(cpoMensalCota.getText());
        controller.setRefAdito(cpoRef.getText());
        controller.setVrmensalAdito(cpoMensalValor.getText());
        controller.setIdcarteira(idcarteira);

        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);

        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (controller.isRetorno()) {
                lerCarteira();
                lerFields();
            }
            try {internalFrame.close();} catch (NullPointerException e) {}
        });

        //root.fireEvent(new paramEvent(new Object[]{-1, rgprp, rgimv, contrato, dtInicio.getText()}, paramEvent.GET_PARAM));
    }

}
