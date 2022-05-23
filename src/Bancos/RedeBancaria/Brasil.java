package Bancos.RedeBancaria;

import Administrador.BancoBoleta;
import Bancos.DadosBanco;
import Bancos.Pagador;
import Funcoes.*;
import Movimento.tbvAltera;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Funcoes.FuncoesGlobais.*;

public class Brasil {
    private int ctalinhas = 1;
    private String LF = "\r\n";
    private DadosBanco dadosBanco = null;

    public Brasil(DadosBanco Banco) {
        this.dadosBanco = Banco;
    }

    public String Remessa(String lote) {
        String output = RemessaHeader240(lote);
        output += RemessaSegmentoR01240();
        output += RemessaSegmentoP240();
        output += RemessaTrailler50240();
        output += RemessaTrailler90240();

        return output;
    }

    public void Processa() {
        Pagador[] pagadors = this.dadosBanco.getBenef_pagadores();
        String nnumero = this.dadosBanco.getBanco_NNumero();
        float fnnumero = 0f;
        for (int i=0; i<=pagadors.length; i++) {
            fnnumero = Float.parseFloat(nnumero) + i;
            pagadors[i].setRc_NNumero(String.valueOf(fnnumero).replace(".0",""));

            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(this.dadosBanco.getBanco()),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }
            String vrBoleta = pagadors[i].getRc_Valor();
            if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);

            String codBar = CodBar(pagadors[i].getRc_Vencimento(), vrBoleta, pagadors[i].getRc_NNumero());
            String linDig = LinhaDigitavel(codBar, pagadors[i].getRc_Vencimento(), vrBoleta);

            pagadors[i].setRc_codigoBarras(codBar);
            pagadors[i].setRc_linhaDIgitavel(linDig);

            // Processa recibo para preencher msgs
            List<tbvAltera> data = new ArrayList<tbvAltera>();
            Banco banco = new Banco(pagadors[i].getCodigo(), pagadors[i].getRc_Vencimento());
            data = banco.ProcessaCampos();

            String[][] dados = {};
            for (tbvAltera dado : data) {
                dados = FuncoesGlobais.ArraysAdd(dados, new String[] {String.valueOf(dado.getId()), dado.getDesc(), dado.getCota(), dado.getValor()});
            }
            pagadors[i].setRc_Dados(dados);

            pagadors[i].setRc_instrucao09("Não Receber após o vencimento.");
            pagadors[i].setRc_instrucao10("Após o Vencimento somente na Imobiliária.");
        }
        this.dadosBanco.setBanco_NNumero(String.valueOf(fnnumero));
    }

    private String CalcDig10(String cadeia) {
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

    private String CalcDig11(String cadeia, int limitesup, int lflag) {

        int mult; int total; int nresto; int ndig; int pos;
        String retorno = "";

        mult = 1 + (cadeia.length() % (limitesup - 1));
        if (mult == 1) { mult = limitesup; }

        total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
            mult -= 1;
            if (mult == 1) mult = limitesup;
        }

        nresto = (total % 11);
        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
            if (nresto == 0 || nresto == 1) {
                ndig = 0;
            } else if (nresto > 9) {
                ndig = 1;
            } else ndig = 11 - nresto;
            retorno = String.valueOf(ndig);
        }
        return retorno;
    }

    private String CalcDig11N(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total; // * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1) {
            resto = 0;
        } else if (resto >= 10) {
            resto = 1;
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }

    private String FatorVencimento(String vencimento) {
        String fator = "07/10/1997";
        String retorno = "0000";
        if (vencimento.length() < 8)
            retorno = "0000";
        else
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "dd/MM/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        return retorno;
    }

    private String NossoNumero(String value) {
        String valor1 = StringManager.Right(StrZero(this.dadosBanco.getBanco_Conta(), 6) +
                Integer.valueOf(value).toString().trim(),this.dadosBanco.getBanco_TamanhoNnumero() - 1);
        String valor2 = CalcDig11(valor1,9,2);   // 6t + 5t
        return valor1 + valor2;
    }

    static public String CalcDig11NNumero(String cadeia) {
        int total= 0; int mult = 2; String ndig = "";
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 7) mult = 2;
        }

        int resto = (total % 11);
        if (resto == 0) {
            ndig = "0";
        } else if ((11 - resto) == 10) {
            ndig = "1"; //P
        } else ndig = String.valueOf(11 - resto);
        return String.valueOf(resto);
    }

    private String CodBar(String vencimento, String valor, String nossonumero) {
        String part1; String part2; String part3; String dv;
        part1 = this.dadosBanco.getBanco() + this.dadosBanco.getBanco_CodMoeda();
        part2 = FatorVencimento(vencimento) + Valor4Boleta(valor);
        part3 = this.dadosBanco.getBanco_Agencia() + this.dadosBanco.getBanco_Carteira() + nossonumero +
                FuncoesGlobais.StrZero(this.dadosBanco.getBanco_ContaDv(),7) + "0";

        String CalcDv = part1 + part2 + part3;
        dv = CalcDig11N(CalcDv);
        return part1 + dv + part2 + part3;
    }

    private String LinhaDigitavel(String codigobarras, String vencimento, String valortitulo) {
        String campo1, campo2, campo3, campo4, campo5;
        String livre = codigobarras.substring(19);

        campo1 = this.dadosBanco.getBanco() + this.dadosBanco.getBanco_CodMoeda() + livre.substring(0, 5);
        campo1 += CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);

        campo2 = livre.substring(5, 15);
        campo2 += CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);

        campo3 = livre.substring(15, 25);
        campo3 += CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);

        campo4 = codigobarras.substring(4, 5);

        campo5 = FatorVencimento( vencimento) + Valor4Boleta(valortitulo);

        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }

    private String RemessaHeader240(String nroLote) {
        String codBaco = String.valueOf(this.dadosBanco.getBanco());
        String loteSer = "0000";
        String tipoSer = "0";
        String reserVd = Space(8);
        String tipoInc = "2";
        String inscEmp = StrZero(rmvNumero(this.dadosBanco.getBenef_CNPJ()),15);                               // CNPJ EMPRESA
        String codTran = this.dadosBanco.getBanco_Agencia() + StrZero(this.dadosBanco.getBanco_Conta(), 11);   // Agencia + Conta
        String reseVad = Space(25);
        String nomeEmp = new Pad(this.dadosBanco.getBenef_Razao(),30).RPad();                                  // Razao
        String nomeBan = "BANCO ITAU" + Space(15);
        String resVado = Space(10);
        String codRems = "1";
        String dtGerac = Dates.DateFormata("ddMMyyyy", DbMain.getDateTimeServer());                                          // data atual
        String rservDo = Space(6);
        String numSequ = StrZero(nroLote, 6);                                                                  // nroLote de 1 a 999999 <
        String Versaos = "040";
        String reSerVa = Space(74);

        String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
                inscEmp + codTran + reseVad + nomeEmp + nomeBan +
                resVado + codRems + dtGerac + rservDo + numSequ +
                Versaos + reSerVa;
        return output + LF;
    }

    private String RemessaSegmentoR01240() {
        String codBco = String.valueOf(this.dadosBanco.getBanco());
        String loteRe = "0001";
        String tpRems = "1";
        String tpOper = "R";
        String tpServ = "01";
        String reseVd = Space(2);
        String nversa = "030";
        String resedo = Space(1);
        String tpInsc = "2";
        String cnpjEp = StrZero(rmvNumero(this.dadosBanco.getBenef_CNPJ()),15);
        String resVdo = Space(20);
        String codTrE = this.dadosBanco.getBanco_Agencia() + StrZero(this.dadosBanco.getBanco_Conta(), 11);
        String rseVdo = Space(5);
        String nomeCd = new Pad(this.dadosBanco.getBenef_Razao(),30).RPad();
        String mensN1 = Space(40); // 40dig
        String mensN2 = Space(40); // 40dig
        String numRRt = "00000000"; // 8dig
        String dtgrav = Dates.DateFormata("ddMMyyyy", DbMain.getDateTimeServer());;
        String reVado = Space(41);

        String output = codBco + loteRe + tpRems + tpOper + tpServ + reseVd +
                nversa + resedo + tpInsc + cnpjEp + resVdo + codTrE +
                rseVdo + nomeCd + mensN1 + mensN2 + numRRt + dtgrav +
                reVado;
        return output + LF;
    }

    private String RemessaSegmentoP240() {
        String output = "";
        Pagador[] pagadores = this.dadosBanco.getBenef_pagadores();
        for (int i = 0; i < pagadores.length - 1; i++) {
            String _contrato = pagadores[i].getCodigo();

            String _nome = pagadores[i].getRazao();
            String _cpfcnpj = pagadores[i].getCNPJ();

            String _ender = pagadores[i].getEndereco() + ", " + pagadores[i].getNumero() + " " + pagadores[i].getComplto();
            String _bairro = pagadores[i].getBairro();
            String _cidade = pagadores[i].getCidade();
            String _estado = pagadores[i].getEstado();
            String _cep = pagadores[i].getCep();

            String _vencto = pagadores[i].getRc_Vencimento();
            String _valor = pagadores[i].getRc_Valor();
            String _rnnumero = pagadores[i].getRc_NNumero();

            String[][]_msg = pagadores[i].getRc_Dados();

            // P
            String codBcC = String.valueOf(this.dadosBanco.getBanco());
            String nrReme = "0001";
            String tpRegi = "3";
            String nrSequ = StrZero(String.valueOf(ctalinhas), 5); //numero de seq do lote
            String cdSegR = "P";
            String rsvDos = Space(1);
            String cdMvRm = "01"; // 01 - Entrada de título
            String agCedn = this.dadosBanco.getBanco_Agencia(); //"3405";  // agencia do cedente
            String digAgc = CalcDig11N(this.dadosBanco.getBanco_Agencia());  //"3"; // digito verificador
            String numCoC = this.dadosBanco.getBanco_CodBenef(); //"013000516"; // Conta Corrente 013000516
            String digCoC = this.dadosBanco.getBanco_CodBenefDv(); //"0"; // digito verificador
            String contCb = this.dadosBanco.getBanco_CodBenef(); //"013000516"; // Conta cobranca 7926383
            String digtCb = this.dadosBanco.getBanco_CodBenefDv(); //"0"; // digito
            String rservo = Space(2);
            String nnumer = StrZero(_rnnumero, 13); // nosso numero com 13 dig
            String tpoCob = "5"; // tipo de cobrança
            String formCd = "1"; // forma de cadastramento
            String tipoDc = "2"; // tipo de documento
            String rsvad1 = Space(1);
            String rsvad2 = Space(1);
            String numDoc = new Pad(_contrato, 15).RPad();
            String dtavtt = Dates.StringtoString(_vencto, "dd/MM/yyyy", "ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
            String vrnmtt = fmtNumero(_valor); //"000000000123129"; // valor nominal do titulo
            String agencb = "0000"; // agencia encarregada
            String digaec = "0"; // digito
            String rsvado = Space(1);
            String esptit = "04"; /// 04 - dup de serviço, 17 - recibo/ especie de titulo // 17 - Recibo
            String idtitu = "N";
            String dtemti = Dates.DateFormata("ddMMyyyy", DbMain.getDateTimeServer()); //"ddmmaaaa"; // data emissao do titulo
            String cdjuti = "1"; // codigo juros do titulo
            String dtjrmo = Dates.StringtoString(_vencto, "dd/MM/yyyy", "ddMMyyyy"); //"ddmmaaaa"; // data juros mora
            String valor = ""; //LerValor.StringToFloat(_valor) * 0.00033f;
            String vrmtxm = fmtNumero(valor); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
            String cddesc = "0"; // codigo desconto
            String dtdesc = "00000000"; // data desconto
            String vrpecd = "000000000000000"; // valor ou percentual de desconto
            String vriofr = "000000000000000"; // iof a ser recolhido
            String vrabti = "000000000000000"; // valor abatimento
            String idttep = Space(25);
            String cdprot = "0"; // codigo para protesto
            String nrdpro = "00"; // numero de dias para protesto
            String cdbxdv = "1"; // codigo baixa devolucao (2)
            String revdao = "0";
            String nrdibd = "15"; // numero de dias baixa devolucao
            String cdmoed = "00"; // codigo moeda
            String revado = Space(11);

            output = codBcC + nrReme + tpRegi + nrSequ + cdSegR + rsvDos +
                    cdMvRm + agCedn + digAgc + numCoC + digCoC + contCb +
                    digtCb + rservo + nnumer + tpoCob + formCd + tipoDc +
                    rsvad1 + rsvad2 + numDoc + dtavtt + vrnmtt + agencb +
                    digaec + rsvado + esptit + idtitu + dtemti + cdjuti +
                    dtjrmo + vrmtxm + cddesc + dtdesc + vrpecd + vriofr +
                    vrabti + idttep + cdprot + nrdpro + cdbxdv + revdao +
                    nrdibd + cdmoed + revado + LF;

            output += RemessaSegmentoQ240(_nome, _cpfcnpj, _ender, _bairro, _cidade, _estado, _cep);
            output += RemessaSegmentoR0240(_vencto);
            output += RemessaSegmentoS240(_msg);
        }

        return output;
    }

    private String RemessaSegmentoQ240(String _nome, String _cpfcnpj, String _ender, String _bairro,
                                       String _cidade, String _estado, String _cep) {
        String cdbcoc = String.valueOf(this.dadosBanco.getBanco());
        String nrltre = "0001";
        String tiporg = "3";

        String nrSeqq = StrZero(String.valueOf(ctalinhas), 5); //numero de seq do lote
        String cdregt = "Q";
        String bracos = Space(1);
        String cdmvrm = "01"; // ou 02 - pedido de baixa
        String cpfCNPJ = rmvLetras(rmvNumero(_cpfcnpj));
        String tpinss = (cpfCNPJ.length() == 11 ? "1" : "2"); // tipo inscricao sacado
        String inscsc = StrZero(cpfCNPJ, 15); //"000000000000000"; // CPF/CNPJ
        String nmesac = myLetra(new Pad(_nome.toUpperCase(), 40).RPad()); //"(40)"; // nome do sacado
        String endsac = myLetra(new Pad(_ender, 40).RPad().toUpperCase()); //"(40)"; // endereco
        String baisac = myLetra(new Pad(_bairro, 15).RPad().toUpperCase()); // "(15)"; // bairro
        String cepsac = myLetra(new Pad(_cep.substring(0, 5), 5).RPad().toUpperCase()); // "(5)";  // cep
        String cepsus = myLetra(new Pad(_cep.substring(6, 9), 3).RPad().toUpperCase()); // "(3)";  // sufixo cep
        String cidsac = myLetra(new Pad(_cidade, 15).RPad().toUpperCase()); //"(15)"; // cidade
        String ufsaca = myLetra(new Pad(_estado, 2).RPad().toUpperCase()); //"RJ";   // UF
        String demais = "0000000000000000                                        000000000000                   ";

        String output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
                cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
                cepsac + cepsus + cidsac + ufsaca + demais;

        return output + LF;
    }

    private String RemessaSegmentoR0240(String _vencto) {
        // R
        String cbcodc = String.valueOf(this.dadosBanco.getBanco());
        String nrlotr = "0001";
        String tporeg = "3";

        String nrSeqr = StrZero(String.valueOf(ctalinhas), 5); //numero de seq do lote
        String cdgseg = "R";
        String spacob = Space(1);
        String cdomot = "01";  // ou 02 - baixa
        String cdgdes = "0"; // codigo desconto
        String dtdes2 = "00000000"; // data desconto 2
        String vrpccd = "000000000000000"; // valor perc desco
        String brac24 = Space(24);
        String cdmult = "2"; // codigo da multa (1 - fixo / 2 - perc)
        String dtamul = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa

        String vrpcap = "000000000001000"; // vr/per multa
        String bran10 = Space(10);
        String msge03 = Space(40); // msg 3
        String msge04 = Space(40); // msg 4
        String branfn = Space(61);

        String output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
                cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
                dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;

        return output + LF;
    }

    private String RemessaSegmentoS240(String[][] msg) {
        int nrlin = 1;
        String output = "";

        for (int z=0;z<msg.length;z++) {
            if (msg[z][0] != null) {
                String codbcc = String.valueOf(this.dadosBanco.getBanco());
                String nrorem = "0001";
                String tppreg = "3";

                String nrSeqs = StrZero(String.valueOf(ctalinhas), 5); //numero de seq do lote
                String cdoseg = "S";
                String branrs = Space(1);
                String cdgmvt = "01"; // ou 02 - baixa

                String idimpr = "1";
                String nrlnip = StrZero(String.valueOf(nrlin++), 2); // nrlinha impressa 01 ate 22
                String msgimp = "4";
                String msgipr = myLetra(new Pad(msg[z][1],30).LPad().toUpperCase() + "  " + new Pad(msg[z][2],7).CPad() + "  " + new Pad(msg[z][3],15).RPad() + Space(44)); //"(100)"; // mensagem a imprimir
                String brancs = Space(119);

                output += codbcc + nrorem + tppreg + nrSeqs +
                        cdoseg + branrs + cdgmvt + idimpr +
                        nrlnip + msgimp + msgipr + brancs + LF;

                ctalinhas += 1;
            }
        }
        return output;
    }

    private String RemessaTrailler50240() {
        String cdgcom = String.valueOf(this.dadosBanco.getBanco());
        String nrores = "0001";
        String tporgt = "5";
        String brantl = Space(9);
        String qtdrlt = StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
        String brcolt = Space(217);

        String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
        return output + LF;
    }

    private String RemessaTrailler90240() {
        String cgdcop = String.valueOf(this.dadosBanco.getBanco());
        String nrolte = "9999";
        String tpregi = "9";
        String brcoat = Space(9);
        String qtdlaq = "000001"; // quantidade de lotes do arquivo
        String qtdrga = StrZero(String.valueOf(ctalinhas), 6); //"000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
        String brcalt = Space(211);

        String output = cgdcop + nrolte + tpregi + brcoat + qtdlaq + qtdrga + brcalt;
        return output + LF;
    }
}
