package Administrador.Abas.Configuracoes.cnfRecibo;

import Administrador.recibo_cf;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class cnfRecibo implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private Label cfrc_mu;
    @FXML private Label cfrc_ju;
    @FXML private Label cfrc_co;
    @FXML private Label cfrc_ep;
    @FXML private Label cfrc_sg;
    @FXML private Label cfrc_dc;
    @FXML private Label cfrc_df;

    @FXML private TableView<recibo_cf> cf_recibos;
    @FXML private TableColumn<recibo_cf, Integer> cf_recibos_id;
    @FXML private TableColumn<recibo_cf, String> cf_recibos_descricao;
    @FXML private TableColumn<recibo_cf, Boolean> cf_recibos_tag;
    @FXML private Label cf_texto;
    @FXML private CheckBox cf_mu_sw;
    @FXML private CheckBox cf_mu_ad;
    @FXML private CheckBox cf_ju_sw;
    @FXML private CheckBox cf_ju_ad;
    @FXML private CheckBox cf_co_sw;
    @FXML private CheckBox cf_co_ad;
    @FXML private CheckBox cf_ep_sw;
    @FXML private CheckBox cf_ep_ad;
    @FXML private CheckBox cf_sg_sw;
    @FXML private CheckBox cf_sg_ad;
    @FXML private CheckBox cf_dc_sw;
    @FXML private CheckBox cf_dc_ad;
    @FXML private CheckBox cf_df_sw;
    @FXML private CheckBox cf_df_ad;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initializa_recibos();
    }

    private void Initializa_recibos() {
        cf_texto.setText("Incorporar ao " + VariaveisGlobais.contas_ca.get("ALU"));
        cfrc_mu.setText(VariaveisGlobais.contas_ca.get("MUL"));
        cfrc_ju.setText(VariaveisGlobais.contas_ca.get("JUR"));
        cfrc_co.setText(VariaveisGlobais.contas_ca.get("COR"));
        cfrc_ep.setText(VariaveisGlobais.contas_ca.get("EXP"));
        cfrc_sg.setText(VariaveisGlobais.contas_ca.get("SEG"));
        cfrc_dc.setText(VariaveisGlobais.contas_ca.get("DES"));
        cfrc_df.setText(VariaveisGlobais.contas_ca.get("DIF"));

        try { cf_mu_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_mu_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_mu_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_mu_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_ju_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_ju_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_ju_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_ju_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_co_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_co_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_co_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_co_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_ep_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_ep_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_ep_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_ep_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_sg_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_sg_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_sg_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_sg_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_dc_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_dc_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_dc_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_dc_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_df_sw.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_df_sw"))); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { cf_df_ad.setSelected("true".equalsIgnoreCase(conn.LerParametros("cf_df_ad"))); } catch (SQLException e) { } catch (NullPointerException e) { }

        cf_mu_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_mu_sw", cf_mu_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_mu_sw"); }
        });
        cf_mu_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_mu_ad", cf_mu_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_mu_ad"); }
        });
        cf_ju_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_ju_sw", cf_ju_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_ju_sw"); }
        });
        cf_ju_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_ju_ad", cf_ju_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_ju_ad"); }
        });
        cf_co_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_co_sw", cf_co_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_co_sw"); }
        });
        cf_co_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_co_ad", cf_co_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_co_ad"); }
        });
        cf_ep_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_ep_sw", cf_ep_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_ep_sw"); }
        });
        cf_ep_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_ep_ad", cf_ep_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_ep_ad"); }
        });
        cf_sg_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_sg_sw", cf_sg_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_sg_sw"); }
        });
        cf_sg_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_sg_ad", cf_sg_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_sg_ad"); }
        });
        cf_dc_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_dc_sw", cf_dc_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_dc_sw"); }
        });
        cf_dc_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_dc_ad", cf_dc_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_dc_ad"); }
        });
        cf_df_sw.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_df_sw", cf_df_sw.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_df_sw"); }
        });
        cf_df_ad.setOnAction(event -> {
            try { conn.GravarParametros(new String[]{"cf_df_ad", cf_df_ad.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("cf_df_ad"); }
        });

        // GridViewe dos campos do Recibo
        populateReciboCf();
    }

    private void populateReciboCf() {
        List<recibo_cf> data = new ArrayList<recibo_cf>();
        ResultSet rs;
        String qSQL = "SELECT id, descricao, recibo FROM campos ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qdesc = null;
                boolean qrecibo = false;
                int qid = -1;

                try { qid = rs.getInt("id"); } catch (SQLException e) { }
                try { qdesc = rs.getString("descricao"); } catch (SQLException e) { }
                try { qrecibo = rs.getBoolean("recibo"); } catch (SQLException e) { }

                data.add(new recibo_cf(qid, qdesc, qrecibo));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        cf_recibos_id.setCellValueFactory(new PropertyValueFactory<recibo_cf, Integer>("id"));
        cf_recibos_id.setStyle("-fx-alignment: CENTER;");

        cf_recibos_descricao.setCellValueFactory(new PropertyValueFactory<recibo_cf, String>("descr"));
        cf_recibos_descricao.setStyle("-fx-alignment: CENTER-LEFT;");
        cf_recibos_descricao.setEditable(false);

        cf_recibos_tag.setStyle("-fx-alignment: CENTER;");
        cf_recibos_tag.setCellValueFactory(new PropertyValueFactory<recibo_cf, Boolean>("tag"));
        final BooleanProperty selected = new SimpleBooleanProperty();
        cf_recibos_tag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer index) {
                recibo_cf tbvlinhas = cf_recibos.getItems().get(index);
                String uSql = "UPDATE campos SET recibo = '" + cf_recibos.getItems().get(index).isCheckedTag().get() + "' WHERE id = " + tbvlinhas.getId() + ";";
                //System.out.println(uSql);
                try {
                    conn.ExecutarComando(uSql);
                } catch (Exception e) {
                }

                return cf_recibos.getItems().get(index).isCheckedTag();
            }
        }));
        cf_recibos_tag.setEditable(false);

        if (!data.isEmpty()) cf_recibos.setItems(FXCollections.observableArrayList(data));
    }

    private void auditora() {
    }

    private void auditora(String taxa) {
        //System.out.println(taxa + " foi alterada por " + VariaveisGlobais.usuario.toLowerCase().trim());
    }

}
