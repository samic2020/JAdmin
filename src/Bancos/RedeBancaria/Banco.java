package Bancos.RedeBancaria;

import Administrador.BancoBoleta;
import Calculos.Processa;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Movimento.Locatarios;
import Movimento.tbvAltera;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Banco {
    DbMain conn = VariaveisGlobais.conexao;
    public int linha = 0;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String venctos;

    public Banco(String contrato, String venctos) {
        this.contrato = contrato;
        this.venctos = venctos;
    }

    public Banco() {}

    public List<tbvAltera> ProcessaCampos() {
        // Pegar o mes de referencia do Vencimento no descdif
        String vcto = venctos;
        String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));

        linha = 0;
        List<tbvAltera> data = new ArrayList<tbvAltera>();
        rgprp = null; rgimv = null;

        // Movimento
        String sql = "SELECT * FROM movimento WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        int recordCount = DbMain.RecordCount(resultSet);

        BigDecimal vrAluguel = new BigDecimal(0);
        try {
            while (resultSet.next()) {
                //Thread.sleep(30);
                //updateProgress(resultSet.getRow(), recordCount);

                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "ALUGUEL", qcota = "99/99";
                String qvalor = "0,00"; String qvariavel = "mensal";

                try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                try {rgimv = resultSet.getString("rgimv");} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("mensal").toString(),"#,##0.00");} catch (SQLException e) {}

                data.add(new tbvAltera("movimento", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                try {vrAluguel = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}
            }
        } catch (Exception e) {e.printStackTrace();}
        try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Desconto Diferença
        BigDecimal desal = new BigDecimal("0");
        BigDecimal difal = new BigDecimal("0");
        sql = "SELECT * FROM descdif WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        recordCount = DbMain.RecordCount(resultSet);

        try {
            while (resultSet.next()) {
                //Thread.sleep(30);
                //updateProgress(resultSet.getRow(), recordCount);

                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "";
                String qdes = "IPTU #" + ++linha, qcota = "99/99";
                String qvalor = "0,00"; String qvariavel = "valor";

                if (rgprp == null) {
                    try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                    try {rgimv = resultSet.getString("rgimv");} catch (SQLException e) {}
                }

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    try {qdes = "Dif.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                } else {
                    // Desconto
                    try {qdes = "Desc.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                }
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                data.add(new tbvAltera("descdif", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    difal = difal.add(resultSet.getBigDecimal("valor"));
                } else {
                    // Desconto
                    desal = desal.add(resultSet.getBigDecimal("valor"));
                }

            }
        } catch (Exception e) {e.printStackTrace();}
        try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // IRRF
        BigDecimal irenda = new BigDecimal("0");
        try {
            irenda = new Calculos.Irrf().Irrf(rgprp, contrato, descdif_refer.trim(), vrAluguel, difal, desal);
            if (irenda.compareTo(BigDecimal.ZERO) == 1) {
                data.add(new tbvAltera("irrf", "", 0, true, "D", "IRRF", descdif_refer.trim(), LerValor.FormatPattern(irenda.toPlainString(), "#,##0.00"), false));
            }
        } catch (Exception e) {}

        // Taxas
        sql = "SELECT * FROM taxas WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        recordCount = DbMain.RecordCount(resultSet);

        try {
            while (resultSet.next()) {
                //Thread.sleep(30);
                //updateProgress(resultSet.getRow(), recordCount);

                // MontaGrade
                Boolean qtag = true, qret = false; int qid = 0; String qtipo = "";
                String qdes = "" , qpos = "", qcota = "";
                String qvalor = "0,00"; String qvariavel = "valor";

                // Ler o nome da taxa
                String sWhere = null;
                try {sWhere = "codigo = '" + resultSet.getString("campo") + "'";} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                try {qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif." ) + (String)conn.LerCamposTabela(new String[] {"descricao"},"campos", sWhere)[0][3];} catch (SQLException e) {}
                try {qpos = resultSet.getString("poscampo");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}
                try {qret = resultSet.getBoolean("retencao");} catch (SQLException e) {}

                data.add(new tbvAltera("taxas", qvariavel, qid, qtag, qtipo, qdes + " " + qpos, qcota, qvalor, qret));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Seguros
        sql = "SELECT * FROM seguros WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        recordCount = DbMain.RecordCount(resultSet);

        try {
            while (resultSet.next()) {
                //Thread.sleep(30);
                //updateProgress(resultSet.getRow(), recordCount);

                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "SEGURO", qcota = "99/99";
                String qvalor = "0,00"; String qvariavel = "valor";

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                data.add(new tbvAltera("seguros", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}


        String[] ciptu = new Calculos.Iptu().Iptu(rgimv,descdif_refer.trim());
        int iptuId = 0;
        String iptuMes = null;
        String iptuRef = null;
        String vrIptu = null;
        if (ciptu != null) {
            if (ciptu[0] != null) {
                iptuId = Integer.valueOf(ciptu[0]);
                iptuMes = ciptu[1];
                iptuRef = ciptu[2];
                vrIptu = LerValor.FormatPattern(ciptu[3], "#,##0.00");
            }
        }
        if (vrIptu != null && !vrIptu.trim().equalsIgnoreCase("0,00")) data.add(new tbvAltera("iptu",iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu, false));

        // Multa, Juros, Correção e Total
        Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto,"dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()),"dd-MM-yyyy"));
        if (calc.Multa().floatValue() > 0) data.add(new tbvAltera("mul","", 0, true, "C", VariaveisGlobais.contas_ca.get("MUL"), "", new DecimalFormat("#,##0.00").format(calc.Multa()), false));
        if (calc.Correcao().floatValue() > 0) data.add(new tbvAltera("cor","", 0, true, "C", VariaveisGlobais.contas_ca.get("COR"), "", new DecimalFormat("#,##0.00").format(calc.Correcao()), false));
        if (calc.Juros().floatValue() > 0) data.add(new tbvAltera("jur","", 0, true, "C", VariaveisGlobais.contas_ca.get("JUR"), "", new DecimalFormat("#,##0.00").format(calc.Juros()), false));
        if (calc.Expediente().floatValue() > 0) data.add(new tbvAltera("exp","", 0, true, "C", VariaveisGlobais.contas_ca.get("EXP"), "", new DecimalFormat("#,##0.00").format(calc.Expediente()), false));

        data.add(new tbvAltera("rec","", 0, true, "C", "Total Geral a Pagar", "", new DecimalFormat("#,##0.00").format(calc.TotalRecibo()), false));
        return data;
    }

    public BancoBoleta LerBancoBoleta(String banco) {
        BancoBoleta data = null;
        ResultSet rs;
        String qSQL = "SELECT id, banco, agencia, agenciadv, conta, contadv, cedente, cedentedv, carteira, tarifa, nnumero, nnumerotam, lote FROM banco_boleta WHERE banco Like '%" + banco + "' ORDER BY id;";
        //qSQL = String.format(qSQL,banco);
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

                data = new BancoBoleta(qid, qbanco, qagencia, qagenciadv, qconta, qcontadv, qcedente, qcedentedv, qcarteira,
                        LerValor.FormatPattern(qtarifa.toPlainString(),"#,##0.00"),qnnumero, qnnumerotam, qlote);
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}

        return data;
    }

    public Locatarios LerDadosLocatario(String contrato) {
        Locatarios data = null;

        ResultSet rs;
        String qSQL = "SELECT l_id AS id, l_contrato AS contrato, l_cpfcnpj AS cpfcnpj, l_rginsc AS rginsc, " +
                "       CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS NOME, " +
                "       CASE WHEN l_fisjur THEN l_f_endereco ELSE l_j_endereco END AS ENDER, " +
                "       CASE WHEN l_fisjur THEN l_f_numero ELSE l_j_numero END AS NUMERO, " +
                "       CASE WHEN l_fisjur THEN l_f_cplto ELSE l_j_cplto END AS CPLTO, " +
                "       CASE WHEN l_fisjur THEN l_f_bairro ELSE l_j_bairro END AS BAIRRO, " +
                "       CASE WHEN l_fisjur THEN l_f_cidade ELSE l_j_cidade END AS CIDADE, " +
                "       CASE WHEN l_fisjur THEN l_f_estado ELSE l_j_estado END AS ESTADO, " +
                "       CASE WHEN l_fisjur THEN l_f_cep ELSE l_j_cep END AS CEP, " +
                "       CASE WHEN l_fisjur THEN l_f_email ELSE l_j_email END AS EMAIL, " +
                "       CASE WHEN l_fisjur THEN l_f_tel ELSE l_j_tel END AS TEL, l_formaenvio AS envio " +
                "  FROM locatarios WHERE l_contrato LIKE '%s' ORDER BY id;";
        qSQL = String.format(qSQL,contrato);
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcontrato = null, qrazao = null, qfanta = null, qcpfcnpj = null,
                       qender = null, qnum = null, qcomplto = null, qbairro = null,
                       qcidade = null, qest = null, qcep = null, qtel = null, qemail = null, qenvio = null;

                try {qcontrato = rs.getString("contrato");} catch (SQLException e) {}
                try {qrazao = rs.getString("nome");} catch (SQLException e) {}
                //try {qfanta = rs.getString("fanta");} catch (SQLException e) {}
                try {qcpfcnpj = rs.getString("cpfcnpj");} catch (SQLException e) {}
                try {qender = rs.getString("ender");} catch (SQLException e) {}
                try {qnum = rs.getString("numero");} catch (SQLException e) {}
                try {qcomplto = rs.getString("cplto");} catch (SQLException e) {}
                try {qbairro = rs.getString("bairro");} catch (SQLException e) {}
                try {qcidade = rs.getString("cidade");} catch (SQLException e) {}
                try {qest = rs.getString("estado");} catch (SQLException e) {}
                try {qcep = rs.getString("cep");} catch (SQLException e) {}
                try {qtel = rs.getString("tel");} catch (SQLException e) {}
                try {qemail = rs.getString("email");} catch (SQLException e) {}
                try {qenvio = rs.getString("envio");} catch (SQLException e) {}

                data = new Locatarios(qcontrato, qrazao, qfanta, qcpfcnpj, qender, qnum, qcomplto, qbairro, qcidade, qest, qcep, qtel, qemail, qenvio);
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}

        return  data;
    }

    public String CalcDigBancoMod10(String cadeia) {
        int mult; int total; int res; int pos;
        mult = (cadeia.length() % 2);
        mult += 1; total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            res = Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
            if (res > 9) { res = (res / 10) + (res % 10); }
            total += res;
            if (mult == 2) { mult =1; } else mult = 2;
        }
        total = ((10 - (total % 10)) % 10);
        return  String.valueOf(total);
    }

    public String CalcDigBancoMod11(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total; // * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1 || resto == 10) {
            resto = 1;
        } else if (resto > 10) {
            resto = 1;
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }
}
