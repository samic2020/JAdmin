package Administrador.Abas.MuJuCoEp;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MuJuCoEp implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    // Multa, Juros, Correção e Expediente - Configuração de calculos
    @FXML private CheckBox mu_cm;
    @FXML private CheckBox mu_al;
    @FXML private CheckBox mu_co;
    @FXML private CheckBox mu_te;
    @FXML private CheckBox mu_ju;
    @FXML private CheckBox mu_tx;
    @FXML private CheckBox ju_al;
    @FXML private CheckBox ju_co;
    @FXML private CheckBox ju_ep;
    @FXML private CheckBox ju_mu;
    @FXML private CheckBox ju_sg;
    @FXML private CheckBox ju_tx;
    @FXML private CheckBox co_al;
    @FXML private CheckBox co_ep;
    @FXML private CheckBox co_mu;
    @FXML private CheckBox co_ju;
    @FXML private CheckBox co_sg;
    @FXML private CheckBox co_tx;
    @FXML private CheckBox ep_al;
    @FXML private CheckBox ep_bl;
    @FXML private CheckBox ep_mu;
    @FXML private CheckBox ep_ju;
    @FXML private CheckBox ep_co;
    @FXML private CheckBox ep_sg;
    @FXML private CheckBox ep_tx;

    @FXML private Tab tmu;
    @FXML private Tab tju;
    @FXML private Tab tco;
    @FXML private Tab tep;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initialize_MUJUCOEP();
        Set_MUJUCOEP();
    }

    private void Set_MUJUCOEP() {
        mu_cm.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_cm", mu_cm.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_cm"); } });
        mu_al.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_al", mu_al.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_al"); } });
        mu_co.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_co", mu_co.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_co"); } });
        mu_te.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_te", mu_te.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_te"); } });
        mu_ju.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_ju", mu_ju.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_ju"); } });
        mu_tx.setOnAction(event -> { try { conn.GravarParametros(new String[]{"mu_tx", mu_tx.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("mu_tx"); } });

        ju_al.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_al", ju_al.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_al"); } });
        ju_co.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_co", ju_co.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_co"); } });
        ju_ep.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_ep", ju_ep.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_ep"); } });
        ju_mu.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_mu", ju_mu.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_mu"); } });
        ju_sg.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_sg", ju_sg.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_sg"); } });
        ju_tx.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ju_tx", ju_tx.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ju_tx"); } });

        co_al.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_al", co_al.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_al"); } });
        co_ep.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_ep", co_ep.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_ep"); } });
        co_mu.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_mu", co_mu.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_mu"); } });
        co_ju.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_ju", co_ju.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_ju"); } });
        co_sg.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_sg", co_sg.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_sg"); } });
        co_tx.setOnAction(event -> { try { conn.GravarParametros(new String[]{"co_tx", co_tx.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("co_tx"); } });

        ep_al.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_al", ep_al.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_al"); } });
        ep_bl.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_bl", ep_bl.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_bl"); } });
        ep_mu.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_mu", ep_mu.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_mu"); } });
        ep_ju.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_ju", ep_ju.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_ju"); } });
        ep_co.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_co", ep_co.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_co"); } });
        ep_sg.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_sg", ep_sg.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_sg"); } });
        ep_tx.setOnAction(event -> { try { conn.GravarParametros(new String[]{"ep_tx", ep_tx.isSelected() ? "true" : "false", "logico"}); } catch (SQLException e) { } finally { auditora("ep_tx"); } });
    }

    private void Initialize_MUJUCOEP() {
        tmu.setText(VariaveisGlobais.contas_ca.get("MUL"));
        tju.setText(VariaveisGlobais.contas_ca.get("JUR"));
        tco.setText(VariaveisGlobais.contas_ca.get("COR"));
        tep.setText(VariaveisGlobais.contas_ca.get("EXP"));

        mu_cm.setText("Gera " + VariaveisGlobais.contas_ca.get("COM"));
        mu_al.setText("Calcula sobre os campos " + VariaveisGlobais.contas_ca.get("ALU"));
        mu_co.setText("Incluir " + VariaveisGlobais.contas_ca.get("COR"));
        mu_te.setText("Incluir " + VariaveisGlobais.contas_ca.get("EXP"));
        mu_ju.setText("Incluir " + VariaveisGlobais.contas_ca.get("JUR"));

        ju_al.setText("Calcula sobre os campos " + VariaveisGlobais.contas_ca.get("ALU"));
        ju_co.setText("Incluir " + VariaveisGlobais.contas_ca.get("COR"));
        ju_ep.setText("Incluir " + VariaveisGlobais.contas_ca.get("EXP"));
        ju_mu.setText("Incluir " + VariaveisGlobais.contas_ca.get("MUL"));
        ju_sg.setText("Incluir " + VariaveisGlobais.contas_ca.get("SEG"));

        co_al.setText("Calcula sobre os campos " + VariaveisGlobais.contas_ca.get("ALU"));
        co_ep.setText("Incluir " + VariaveisGlobais.contas_ca.get("EXP"));
        co_mu.setText("Incluir " + VariaveisGlobais.contas_ca.get("MUL"));
        co_ju.setText("Incluir " + VariaveisGlobais.contas_ca.get("JUR"));
        co_sg.setText("Incluir " + VariaveisGlobais.contas_ca.get("SEG"));

        ep_al.setText("Calcula sobre os campos " + VariaveisGlobais.contas_ca.get("ALU"));
        ep_mu.setText("Incluir " + VariaveisGlobais.contas_ca.get("MUL"));
        ep_ju.setText("Incluir " + VariaveisGlobais.contas_ca.get("JUR"));
        ep_co.setText("Incluir " + VariaveisGlobais.contas_ca.get("COR"));
        ep_sg.setText("Incluir " + VariaveisGlobais.contas_ca.get("SEG"));

        try { mu_cm.setSelected(conn.LerParametros("mu_cm").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { mu_al.setSelected(conn.LerParametros("mu_al").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { mu_co.setSelected(conn.LerParametros("mu_co").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { mu_te.setSelected(conn.LerParametros("mu_te").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { mu_ju.setSelected(conn.LerParametros("mu_ju").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { mu_tx.setSelected(conn.LerParametros("mu_tx").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }

        try { ju_al.setSelected(conn.LerParametros("ju_al").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ju_co.setSelected(conn.LerParametros("ju_co").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        ju_co.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                co_ju.setSelected(false);
                co_ju.setDisable(ju_co.isSelected());
            }
        });

        try { ju_ep.setSelected(conn.LerParametros("ju_ep").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ju_mu.setSelected(conn.LerParametros("ju_mu").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ju_sg.setSelected(conn.LerParametros("ju_sg").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ju_tx.setSelected(conn.LerParametros("ju_tx").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }

        try { co_al.setSelected(conn.LerParametros("co_al").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { co_ep.setSelected(conn.LerParametros("co_ep").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { co_mu.setSelected(conn.LerParametros("co_mu").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { co_ju.setSelected(conn.LerParametros("co_ju").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { co_sg.setSelected(conn.LerParametros("co_sg").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { co_tx.setSelected(conn.LerParametros("co_tx").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }

        try { ep_al.setSelected(conn.LerParametros("ep_al").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_bl.setSelected(conn.LerParametros("ep_bl").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_mu.setSelected(conn.LerParametros("ep_mu").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_ju.setSelected(conn.LerParametros("ep_ju").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_co.setSelected(conn.LerParametros("ep_co").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_sg.setSelected(conn.LerParametros("ep_sg").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ep_tx.setSelected(conn.LerParametros("ep_tx").equalsIgnoreCase("true")); } catch (SQLException e) { } catch (NullPointerException e) { }
    }

    private void auditora() {
    }

    private void auditora(String taxa) {
        //System.out.println(taxa + " foi alterada por " + VariaveisGlobais.usuario.toLowerCase().trim());
    }

}
