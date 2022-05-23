package Calculos;

import Funcoes.DbMain;

import java.sql.SQLException;

import static Funcoes.VariaveisGlobais.*;

/**
 * Created by supervisor on 06/02/17.
 */
public class Calculos_mujucoep {
    DbMain conn = conexao;

    public Calculos_mujucoep() {
        try { mu_cm = conn.LerParametros("mu_cm").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { mu_al = conn.LerParametros("mu_al").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { mu_co = conn.LerParametros("mu_co").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { mu_te = conn.LerParametros("mu_te").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { mu_ju = conn.LerParametros("mu_ju").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { mu_tx = conn.LerParametros("mu_tx").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}

        try { ju_al = conn.LerParametros("ju_al").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ju_co = conn.LerParametros("ju_co").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ju_ep = conn.LerParametros("ju_ep").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ju_mu = conn.LerParametros("ju_mu").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ju_sg =  conn.LerParametros("ju_sg").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ju_tx = conn.LerParametros("ju_tx").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}

        try { co_al = conn.LerParametros("co_al").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { co_ep =  conn.LerParametros("co_ep").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { co_mu = conn.LerParametros("co_mu").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { co_ju =  conn.LerParametros("co_ju").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { co_sg =  conn.LerParametros("co_sg").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { co_tx = conn.LerParametros("co_tx").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}

        try { ep_al = conn.LerParametros("ep_al").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_bl = conn.LerParametros("ep_bl").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_mu = conn.LerParametros("ep_mu").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_ju = conn.LerParametros("ep_ju").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_co = conn.LerParametros("ep_co").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_sg = conn.LerParametros("ep_sg").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
        try { ep_tx = conn.LerParametros("ep_tx").equalsIgnoreCase("true") ? true : false; } catch (SQLException e) {} catch (NullPointerException e) {}
    }
}
