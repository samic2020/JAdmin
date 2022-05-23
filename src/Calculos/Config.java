package Calculos;

import Administrador.BancoAdm;
import Administrador.BancoBoleta;
import Administrador.EmailAdm;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import javafx.scene.image.Image;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by supervisor on 06/02/17.
 */
public class Config {
    static DbMain conn = VariaveisGlobais.conexao;

    public static void Config_ADM() {
        // Dados da Administradora
        try { VariaveisGlobais.da_razao = conn.LerParametros("da_razao"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_fanta = conn.LerParametros("da_fanta"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_cnpj = conn.LerParametros("da_cnpj"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_creci = conn.LerParametros("da_creci"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_tipo = conn.LerParametros("da_tipo"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_insc = conn.LerParametros("da_insc"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_ender = conn.LerParametros("da_ender"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_numero = conn.LerParametros("da_numero"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_cplto = conn.LerParametros("da_cplto"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_bairro = conn.LerParametros("da_bairro"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_cidade = conn.LerParametros("da_cidade"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_codmun = conn.LerParametros("da_codmun"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_estado = conn.LerParametros("da_estado"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_cep = conn.LerParametros("da_cep"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_tel = conn.LerParametros("da_tel");} catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_email = conn.LerParametros("da_email"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_hpage = conn.LerParametros("da_hpage"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_marca = conn.LerParametros("da_marca"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_logo = new Image(new File(conn.LerParametros("da_logo")).toURI().toString()); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_responsavel = conn.LerParametros("da_responsavel"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.da_respcpf = conn.LerParametros("da_respcpf"); } catch (SQLException e) {} catch (NullPointerException e) {}
    }

    public void Config_CA() {
        ResultSet rs;
        String qSQL = "SELECT id, codigo, descricao FROM adm ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qdescricao = null; int qid = -1;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qcodigo = rs.getString("codigo");} catch (SQLException e) {}
                try {qdescricao = rs.getString("descricao");} catch (SQLException e) {}

                VariaveisGlobais.contas_ca.add(qcodigo, qdescricao);
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {} catch (NullPointerException e) {}
    }

    public void Config_AC() {
        ResultSet rs;
        String qSQL = "SELECT id, codigo, descricao FROM adm_contas ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qdescricao = null; int qid = -1;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qcodigo = rs.getString("codigo");} catch (SQLException e) {}
                try {qdescricao = rs.getString("descricao");} catch (SQLException e) {}

                VariaveisGlobais.contas_ac.add(qcodigo, qdescricao);
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}  catch (NullPointerException e) {}
    }

    public void Config_BB() {
        ResultSet rs;
        String qSQL = "SELECT id, banco, agencia, agenciadv, conta, contadv, cedente, cedentedv, carteira, tarifa, nnumero, nnumerotam, lote FROM banco_boleta ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qbanco = null, qagencia = null, qconta = null, qcedente = null, qcarteira = null;
                int qagenciadv = 0, qcontadv = 0, qcedentedv = 0;
                BigDecimal qtarifa = null; int qid = -1, qlote = 0; double qnnumero = 0, qnnumerotam = 0;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qbanco = rs.getString("banco");} catch (SQLException e) {}
                try {qagencia = rs.getString("agencia");} catch (SQLException e) {}
                try {qagenciadv = rs.getInt("agenciadv");} catch (SQLException e) {}
                try {qconta = rs.getString("conta");} catch (SQLException e) {}
                try {qcontadv = rs.getInt("contadv");} catch (SQLException e) {}
                try {qcedente = rs.getString("cedente");} catch (SQLException e) {}
                try {qcedentedv = rs.getInt("cedentedv");} catch (SQLException e) {}
                try {qcarteira = rs.getString("carteira");} catch (SQLException e) {}
                try {qtarifa = rs.getBigDecimal("tarifa");} catch (SQLException e) {}
                try {qnnumero = rs.getDouble("nnumero");} catch (SQLException e) {}
                try {qnnumerotam = rs.getDouble("nnumerotam");} catch (SQLException e) {}
                try {qlote = rs.getInt("lote");} catch (SQLException e) {}

                VariaveisGlobais.bancos_boleta.add(new BancoBoleta(qid, qbanco, qagencia, qagenciadv, qconta, qcontadv, qcedente, qcedentedv, qcarteira,
                        LerValor.FormatPattern(qtarifa.toPlainString(),"#,##0.00"),qnnumero, qnnumerotam, qlote)
                );
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {} catch (NullPointerException e) {}
    }

    public void Config_BA() {
        ResultSet rs;
        String qSQL = "SELECT id, banco, agencia, conta, tipo, ted, doc, cheque, transferencia FROM banco_adm ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qbanco = null, qagencia = null, qconta = null, qtipo = null;
                BigDecimal qted = null, qdoc = null, qcheque = null, qtransferencia = null; int qid = -1;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qbanco = rs.getString("banco");} catch (SQLException e) {}
                try {qagencia = rs.getString("agencia");} catch (SQLException e) {}
                try {qconta = rs.getString("conta");} catch (SQLException e) {}
                try {qtipo = rs.getString("tipo");} catch (SQLException e) {}
                try {qted = rs.getBigDecimal("ted");} catch (SQLException e) {}
                try {qdoc = rs.getBigDecimal("doc");} catch (SQLException e) {}
                try {qcheque = rs.getBigDecimal("cheque");} catch (SQLException e) {}
                try {qtransferencia = rs.getBigDecimal("transferencia");} catch (SQLException e) {}

                VariaveisGlobais.bancos_adm.add(new BancoAdm(qid, qbanco, qagencia, qconta, qtipo,
                        LerValor.FormatPattern(qted.toPlainString(),"#,##0.00"),
                        LerValor.FormatPattern(qdoc.toPlainString(),"#,##0.00"),
                        LerValor.FormatPattern(qcheque.toPlainString(),"#,##0.00"),
                        LerValor.FormatPattern(qtransferencia.toPlainString(),"#,##0.00"))
                );
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {} catch (NullPointerException e) {}
    }

    public void Config_Email() {
        ResultSet rs;
        String qSQL = "SELECT id, email, senha, smtp, porta, autentica, ssl FROM conta_email ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qemail = null, qsenha = null, qsmtp = null, qporta = null;
                boolean qautentica = false, qssl = false; int qid = -1;

                try {qid = rs.getInt("id");} catch (SQLException e) {}
                try {qemail = rs.getString("email");} catch (SQLException e) {}
                try {qsenha = rs.getString("senha");} catch (SQLException e) {}
                try {qsmtp = rs.getString("smtp");} catch (SQLException e) {}
                try {qporta = rs.getString("porta");} catch (SQLException e) {}
                try {qautentica = rs.getBoolean("autentica");} catch (SQLException e) {}
                try {qssl = rs.getBoolean("ssl");} catch (SQLException e) {}

                VariaveisGlobais.email_adm.add(new EmailAdm(qid, qemail, qsenha, qsmtp, qporta, qautentica, qssl));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {} catch (NullPointerException e) {}
    }

    public void Config_MsgProp() {
        try { VariaveisGlobais.mp_msg = conn.LerParametros("mp_msg"); } catch (SQLException e) {} catch (NullPointerException e) {}
        try { VariaveisGlobais.ml_msg = conn.LerParametros("ml_msg"); } catch (SQLException e) {} catch (NullPointerException e) {}
    }
}
