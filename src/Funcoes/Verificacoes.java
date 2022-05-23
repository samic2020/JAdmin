package Funcoes;

import Classes.cFiadores;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Verificacoes {
    DbMain conn = VariaveisGlobais.conexao;

    public Verificacoes() {}

    public cFiadores VerificaFiadorEmFiadores(String value) {
        cFiadores retorno = null;

        ResultSet rs = null;
        try {
            String selectSQL = "SELECT * FROM fiadores WHERE f_cpfcnpj = ? ORDER BY f_id DESC LIMIT 1;";
            rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", value}});
            while (rs.next()) {
                boolean qfisjur = false; try { qfisjur = rs.getBoolean("f_fisjur"); } catch (SQLException sex) {}
                String qcpfcnpj = null; try { qcpfcnpj = rs.getString("f_cpfcnpj"); } catch (SQLException sex) {}
                String qrginsc = null; try { qrginsc = rs.getString("f_rginsc"); } catch (SQLException sex) {}
                String qnome = null; try { qnome = rs.getString(qfisjur ? "f_f_nome" : "f_j_razao"); } catch (SQLException sex) {}
                String qfantasia = null; try { qfantasia = !qfisjur ?  rs.getString("f_j_fantasia") : null; } catch (SQLException sex) {}
                String qsexo = null; try { qsexo = rs.getString("f_f_sexo"); } catch (SQLException sex) {}
                Date qdtnasc = null; try { qdtnasc = rs.getDate("f_f_dtnasc"); } catch (SQLException sex) {}
                Date qdtctro = null; try { qdtctro = rs.getDate("f_j_dtctrosocial"); } catch (SQLException sex) {}
                String qendereco = null; try { qendereco = rs.getString(qfisjur  ? "f_f_endereco_fiador" : "f_j_endereco"); } catch (SQLException sex) {}
                String qnumero = null; try { qnumero = rs.getString(qfisjur  ? "f_f_numero_fiador" : "f_j_numero"); } catch (SQLException sex) {}
                String qcplto = null; try { qcplto = rs.getString(qfisjur  ? "f_f_cplto_fiador" : "f_j_cplto"); } catch (SQLException sex) {}
                String qbairro = null; try { qbairro = rs.getString(qfisjur  ? "f_f_bairro_fiador" : "f_j_bairro"); } catch (SQLException sex) {}
                String qcidade = null; try { qcidade = rs.getString(qfisjur  ? "f_f_cidade_fiador" : "f_j_cidade"); } catch (SQLException sex) {}
                String qestado = null; try { qestado = rs.getString(qfisjur  ? "f_f_estado_fiador" : "f_j_estado"); } catch (SQLException sex) {}
                String qcep = null; try { qcep = rs.getString(qfisjur  ? "f_f_cep_fiador" : "f_j_cep"); } catch (SQLException sex) {}
                String qnacional = null; try { qnacional = rs.getString("f_f_nacionalidade"); } catch (SQLException sex) {}
                String qecivil = null; try { qecivil = rs.getString("f_f_estcivil"); } catch (SQLException sex) {}
                String qmae = null; try { qmae = rs.getString("f_f_mae"); } catch (SQLException sex) {}
                String qpai = null; try { qpai = rs.getString("f_f_pai"); } catch (SQLException sex) {}

                retorno = new cFiadores(qfisjur, qcpfcnpj, qrginsc, qnome, qfantasia, qsexo, qdtnasc, qdtctro, qendereco, qnumero, qcplto, qbairro, qcidade, qestado, qcep, qnacional, qecivil, qmae, qpai);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
        return retorno;
    }

    public cFiadores VerificaFiadorEmProprietarios(String value) {
        cFiadores retorno = null;

        ResultSet rs = null;
        try {
            String selectSQL = "SELECT * FROM proprietarios WHERE p_cpfcnpj = ? ORDER BY p_id DESC LIMIT 1;";
            rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", value}});
            while (rs.next()) {
                boolean qfisjur = false; try { qfisjur = rs.getString("p_fisjur").equalsIgnoreCase("F") ? true : false; } catch (SQLException sex) {}
                String qcpfcnpj = null; try { qcpfcnpj = rs.getString("p_cpfcnpj"); } catch (SQLException sex) {}
                String qrginsc = null; try { qrginsc = rs.getString("p_rginsc"); } catch (SQLException sex) {}
                String qnome = null; try { qnome = rs.getString("p_nome"); } catch (SQLException sex) {}
                String qfantasia = null;
                String qsexo = null; try { qsexo = rs.getString("p_sexo"); } catch (SQLException sex) {}
                Date qdtnasc = null; try { qdtnasc = rs.getDate("p_dtnasc"); } catch (SQLException sex) {}
                Date qdtctro = null;
                String qendereco = null; try { qendereco = rs.getString("p_end"); } catch (SQLException sex) {}
                String qnumero = null; try { qnumero = rs.getString("p_num"); } catch (SQLException sex) {}
                String qcplto = null; try { qcplto = rs.getString("p_compl"); } catch (SQLException sex) {}
                String qbairro = null; try { qbairro = rs.getString("p_bairro"); } catch (SQLException sex) {}
                String qcidade = null; try { qcidade = rs.getString("p_cidade"); } catch (SQLException sex) {}
                String qestado = null; try { qestado = rs.getString("p_estado"); } catch (SQLException sex) {}
                String qcep = null; try { qcep = rs.getString("p_cep"); } catch (SQLException sex) {}
                String qnacional = null; try { qnacional = rs.getString("p_nacionalidade"); } catch (SQLException sex) {}
                String qecivil = null; try { qecivil = rs.getString("p_estcivil"); } catch (SQLException sex) {}
                String qmae = null;
                String qpai = null;

                retorno = new cFiadores(qfisjur, qcpfcnpj, qrginsc, qnome, qfantasia, qsexo, qdtnasc, qdtctro, qendereco, qnumero, qcplto, qbairro, qcidade, qestado, qcep, qnacional, qecivil, qmae, qpai);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
        return retorno;
    }

    public cFiadores VerificaFiadorEmLocatarios(String value) {
        cFiadores retorno = null;

        ResultSet rs = null;
        try {
            String selectSQL = "SELECT * FROM locatarios WHERE l_cpfcnpj = ? ORDER BY l_id DESC LIMIT 1;";
            rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", value}});
            while (rs.next()) {
                boolean qfisjur = false; try { qfisjur = rs.getBoolean("l_fisjur"); } catch (SQLException sex) {}
                String qcpfcnpj = null; try { qcpfcnpj = rs.getString("l_cpfcnpj"); } catch (SQLException sex) {}
                String qrginsc = null; try { qrginsc = rs.getString("l_rginsc"); } catch (SQLException sex) {}
                String qnome = null; try { qnome = rs.getString(qfisjur ? "l_f_nome" : "l_j_razao"); } catch (SQLException sex) {}
                String qfantasia = null; try { qfantasia = rs.getString("l_j_fantasia"); } catch (SQLException sex) {}
                String qsexo = null; try { qsexo = rs.getString("l_f_sexo"); } catch (SQLException sex) {}
                Date qdtnasc = null; try { qdtnasc = rs.getDate("l_f_dtnasc"); } catch (SQLException sex) {}
                Date qdtctro = null;try { qdtctro = rs.getDate("l_j_dtctrosocial"); } catch (SQLException sex) {}
                String qendereco = null; try { qendereco = rs.getString(qfisjur ? "l_f_endereco" : "l_j_endereco"); } catch (SQLException sex) {}
                String qnumero = null; try { qnumero = rs.getString(qfisjur ? "l_f_numero" : "l_j_numero"); } catch (SQLException sex) {}
                String qcplto = null; try { qcplto = rs.getString(qfisjur ? "l_f_cplto" : "l_j_cplto"); } catch (SQLException sex) {}
                String qbairro = null; try { qbairro = rs.getString(qfisjur ? "l_f_bairro" : "l_j_bairro"); } catch (SQLException sex) {}
                String qcidade = null; try { qcidade = rs.getString(qfisjur ? "l_f_cidade" : "l_j_cidade"); } catch (SQLException sex) {}
                String qestado = null; try { qestado = rs.getString(qfisjur ? "l_f_estado" : "l_j_estado"); } catch (SQLException sex) {}
                String qcep = null; try { qcep = rs.getString(qfisjur ? "l_f_cep" : "l_j_cep"); } catch (SQLException sex) {}
                String qnacional = null; try { qnacional = rs.getString("l_f_nacionalidade"); } catch (SQLException sex) {}
                String qecivil = null; try { qecivil = rs.getString("l_f_estcivil"); } catch (SQLException sex) {}
                String qmae = null; try { qmae = rs.getString("l_f_mae"); } catch (SQLException sex) {}
                String qpai = null; try { qpai = rs.getString("l_f_pai"); } catch (SQLException sex) {}

                retorno = new cFiadores(qfisjur, qcpfcnpj, qrginsc, qnome, qfantasia, qsexo, qdtnasc, qdtctro, qendereco, qnumero, qcplto, qbairro, qcidade, qestado, qcep, qnacional, qecivil, qmae, qpai);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
        return retorno;
    }

}
