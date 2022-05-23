package Calculos;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by supervisor on 06/02/17.
 */
public class Multas {
    DbMain conn = VariaveisGlobais.conexao;

    public Multas(String rgprp, String rgimv) {
        if (!Multa("imoveis", rgimv)) {
            if (!Multa("proprietarios", rgprp)) {
                MultaGeral();
            }
        }
    }

    private boolean MultaGeral() {
        boolean exist = false;
        String sql = "SELECT id, multa_tipo, registro, pa_mu, pa_ju, pa_co, pa_ep, mu_res, " +
                     "mu_com, co_tipo, co_perc, co_limite, co_dia, ju_tipo, ju_percent, " +
                     "ca_multa, ca_juros, ca_correcao, co, ep_percent, ep_vrlor, bol_txbanc " +
                     "FROM multas WHERE Lower(multa_tipo) = 'geral'";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                try { VariaveisGlobais.pa_mu = rs.getDouble("pa_mu"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_ju = rs.getDouble("pa_ju"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_co = rs.getDouble("pa_co"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_ep = rs.getDouble("pa_ep"); } catch (SQLException e) {}

                try { VariaveisGlobais.mu_res = rs.getDouble("mu_res"); } catch (SQLException e) {}
                try { VariaveisGlobais.mu_com = rs.getDouble("mu_com"); } catch (SQLException e) {}

                try { VariaveisGlobais.co_tipo = rs.getString("co_tipo"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_perc = rs.getDouble("co_perc"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_limite = rs.getBoolean("co_limite"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_dia = rs.getInt("co_dia"); } catch (SQLException e) {}

                try { VariaveisGlobais.ju_tipo = rs.getString("ju_tipo"); } catch (SQLException e) {}
                try { VariaveisGlobais.ju_percent = rs.getDouble("ju_percent"); } catch (SQLException e) {}

                try { VariaveisGlobais.ca_multa = rs.getInt("ca_multa"); } catch (SQLException e) {}
                try { VariaveisGlobais.ca_juros = rs.getInt("ca_juros"); } catch (SQLException e) {}
                try { VariaveisGlobais.ca_correcao = rs.getInt("ca_correcao"); } catch (SQLException e) {}

                try { VariaveisGlobais.co = rs.getDouble("co"); } catch (SQLException e) {}

                try { VariaveisGlobais.ep_percent = rs.getDouble("ep_percent"); } catch (SQLException e) {}
                try { VariaveisGlobais.ep_vrlor = rs.getDouble("ep_vrlor"); } catch (SQLException e) {}
                try { VariaveisGlobais.bol_txbanc = rs.getBoolean("bol_txbanc"); } catch (SQLException e) {}
                exist = true;
            }
        } catch (SQLException e) {exist = false;}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}
        return exist;
    }

    private boolean Multa(String tipo, String registro) {
        boolean exist = false;
        String sql = "SELECT id, multa_tipo, registro, pa_mu, pa_ju, pa_co, pa_ep, mu_res, " +
                "mu_com, co_tipo, co_perc, co_limite, co_dia, ju_tipo, ju_percent, " +
                "ca_multa, ca_juros, ca_correcao, co, ep_percent, ep_vrlor, bol_txbanc " +
                "FROM multas WHERE Lower(multa_tipo) = '%s' AND registro = '%s'";
        sql = String.format(sql,tipo.toLowerCase(), registro);
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                try { VariaveisGlobais.pa_mu = rs.getDouble("pa_mu"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_ju = rs.getDouble("pa_ju"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_co = rs.getDouble("pa_co"); } catch (SQLException e) {}
                try { VariaveisGlobais.pa_ep = rs.getDouble("pa_ep"); } catch (SQLException e) {}

                try { VariaveisGlobais.mu_res = rs.getDouble("mu_ers"); } catch (SQLException e) {}
                try { VariaveisGlobais.mu_com = rs.getDouble("mu_com"); } catch (SQLException e) {}

                try { VariaveisGlobais.co_tipo = rs.getString("co_tipo"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_perc = rs.getDouble("co_perc"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_limite = rs.getBoolean("co_limite"); } catch (SQLException e) {}
                try { VariaveisGlobais.co_dia = rs.getInt("co_dia"); } catch (SQLException e) {}

                try { VariaveisGlobais.ju_tipo = rs.getString("ju_tipo"); } catch (SQLException e) {}
                try { VariaveisGlobais.ju_percent = rs.getDouble("ju_percent"); } catch (SQLException e) {}

                try { VariaveisGlobais.ca_multa = rs.getInt("ca_multa"); } catch (SQLException e) {}
                try { VariaveisGlobais.ca_juros = rs.getInt("ca_juros"); } catch (SQLException e) {}
                try { VariaveisGlobais.ca_correcao = rs.getInt("ca_correcao"); } catch (SQLException e) {}

                try { VariaveisGlobais.co = rs.getDouble("co"); } catch (SQLException e) {}

                try { VariaveisGlobais.ep_percent = rs.getDouble("ep_percent"); } catch (SQLException e) {}
                try { VariaveisGlobais.ep_vrlor = rs.getDouble("ep_vrlor"); } catch (SQLException e) {}
                try { VariaveisGlobais.bol_txbanc = rs.getBoolean("bol_txbanc"); } catch (SQLException e) {}
                exist = true;
            }
        } catch (SQLException e) {exist = false;}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}
        return exist;
    }
}
