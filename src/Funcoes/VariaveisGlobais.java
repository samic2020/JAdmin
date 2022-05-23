/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import Administrador.BancoAdm;
import Administrador.BancoBoleta;
import Administrador.EmailAdm;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

import javax.print.PrintService;
import javax.rad.genui.container.UIDesktopPanel;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author supervisor
 */
public class VariaveisGlobais {
    // Caminhos do sistema
    static public String appPath = System.getProperty("user.dir");

    // Parametros de Internet
    static public String urlNET = "http://127.0.0.1:9876/SamicServer?wsdl";

    static public FXMLLoader loader = null;

    // Inicialização SQL
    static public String pegarMaxAutenticacao = "SELECT MAX(aut) AS autenticacao FROM caixa;";
    static public String criaAutenticacao = "CREATE SEQUENCE IF NOT EXISTS caixa_aut MINVALUE %s";
    // Fim SQL

    static public boolean dbsenha = false;

    // Variaveis de identificação
    static public String cliente = "passeli";
    static public String estacao = "sg";

    // Variaveis de conexão
    static public String unidade = "";
    static public String usuario = "";
    static public String user_id = "";
    static public String senha = "";
    static public String dbnome = "";
    static public String cargo = "USU";
    static public String protocolo = "";

    static public String  remoto1  = "";
    static public String  dbnome1  = "";
    static public boolean dbsenha1 = false;

    static public Object[][] unidades = {};

    // Ordenacao de Matriz
    static public int Inicio = 1;
    static public int Final = 1;

    static public String marca = "icone";

    // Conexao compartilhada
    static public DbMain conexao = null;
    static public String KeyPwd = "";

    public static String myLogo = "";

    static public boolean local;

    // Impressão do Sistema
    public static String PrinterMode = "INTERNA";  // INTERNA | EXTERNA
    public static String Thermica = null;
    public static float[] bobinaSize = {215f, 730f, 12, 2, -2, 2};
    public static String Printer = null;
    public static String Externo = null;
    public static boolean statPrinterThermica = true;
    public static boolean statPrinterInkLaser = true;

    // Variaveis do Sistema FX4Rent
    // Parametros de Calculos Multas
    public static double pa_mu;       // Percentual Administradora Multa
    public static double pa_ju;       // Percentual Administradora Juros
    public static double pa_co;       // Percentual Administradora Correcao
    public static double pa_ep;       // Percentual Administradora Expediente

    public static double mu_res;      // Multa para Imóveis Residencial
    public static double mu_com;      // Multa para Imóveis Comercial

    public static String co_tipo;     // Tipo de Correção (0 - Simple, 1 - Composta)
    public static double co_perc;     // Percentual de Correção
    public static boolean co_limite;  // Limitado
    public static double co_dia;      // Dias de Limitação

    public static String ju_tipo;     // Tipo de Juros (0 - por mes, 1 - ao mes)
    public static double ju_percent;  // Percentual de juros

    public static int ca_multa;       // Carencia em multas
    public static int ca_juros;       // Carencia em juros
    public static int ca_correcao;    // Carencia em correção

    public static double co;          // Comissão

    public static double ep_percent;  // Percentual de expediente
    public static double ep_vrlor;    // Valor de expediente

    // Parametros de Calculos Geral
    public static boolean mu_cm;
    public static boolean mu_al;
    public static boolean mu_co;
    public static boolean mu_te;
    public static boolean mu_ju;
    public static boolean mu_tx;

    public static boolean ju_al;
    public static boolean ju_co;
    public static boolean ju_ep;
    public static boolean ju_mu;
    public static boolean ju_sg;
    public static boolean ju_tx;

    public static boolean co_al;
    public static boolean co_ep;
    public static boolean co_mu;
    public static boolean co_ju;
    public static boolean co_sg;
    public static boolean co_tx;

    public static boolean ep_al;
    public static boolean ep_bl;
    public static boolean ep_mu;
    public static boolean ep_ju;
    public static boolean ep_co;
    public static boolean ep_sg;
    public static boolean ep_tx;

    public static boolean bol_txbanc;

    // Dados da Administradora
    public static String da_razao;
    public static String da_fanta;
    public static String da_cnpj;
    public static String da_creci;
    public static String da_tipo;
    public static String da_insc;
    public static String da_ender;
    public static String da_numero;
    public static String da_cplto;
    public static String da_bairro;
    public static String da_cidade;
    public static String da_codmun;
    public static String da_estado;
    public static String da_cep;
    public static String da_email;
    public static String da_hpage;
    public static String da_marca;
    public static Image da_logo;
    public static String da_nmlogo;
    public static String da_tel;
    public static String da_responsavel;
    public static String da_respcpf;

    static public Collections contas_ca = new Collections();
    static public Collections contas_ac = new Collections();

    public static List<BancoBoleta> bancos_boleta = new ArrayList<BancoBoleta>();
    public static List<BancoAdm> bancos_adm = new ArrayList<BancoAdm>();
    public static List<EmailAdm> email_adm = new ArrayList<EmailAdm>();

    public static String mp_msg = "";
    public static String ml_msg = "";

    // Dados para exibição de Recibo
    public static boolean cf_mu_sw;
    public static boolean cf_mu_ad;
    public static boolean cf_ju_sw;
    public static boolean cf_ju_ad;
    public static boolean cf_co_sw;
    public static boolean cf_co_ad;
    public static boolean cf_ep_sw;
    public static boolean cf_ep_ad;
    public static boolean cf_sg_sw;
    public static boolean cf_sg_ad;
    public static boolean cf_dc_sw;
    public static boolean cf_dc_ad;
    public static boolean cf_df_sw;
    public static boolean cf_df_ad;

    // Configuração de Impressão do Recibo
    public static int recibo_vias;
    public static float logo_width;
    public static float logo_Heigth;
    public static String logo_allign;
    public static boolean logo_noprint;
    public static boolean razao_noprint;
    public static boolean cnpj_noprint;
    public static boolean creci_noprint;
    public static boolean endereco_noprint;
    public static boolean telefone_noprint;
    public static String recibo_titulo;
    public static boolean copa_print;
    public static boolean ref_print;
    public static boolean qcr_print;
    public static boolean md5_print;

    public static UIDesktopPanel desktopPanel;
    // Fim

    // Configuração de Impressão do Boleto
    public static float pb_logo_width;
    public static float pb_logo_Heigth;
    public static boolean pb_logo_noprint;
    public static boolean pb_razao_noprint;
    public static boolean pb_cnpj_noprint;
    public static boolean pb_creci_noprint;
    public static boolean pb_endereco_noprint;
    public static boolean pb_telefone_noprint;
    public static boolean pb_copa_print;
    public static boolean pb_qcr_print;
    // Instruções do Boleto
    public static String pb_int01;
    public static String pb_int02;
    public static String pb_int03;
    public static String pb_int04;
    public static String pb_int05;
    public static String pb_int06;
    public static String pb_int07;
    public static String pb_int08;
    public static String pb_int09;

    // Avisos e Mensagens
    public static boolean am_aniv;
    public static boolean am_term;
    public static String am_dias;
    public static boolean am_reaj;
    public static String am_reajdias;
    public static boolean am_seg;
    public static String am_segdias;
    public static boolean reajManAluguel;

    public static String title;
    public static String posicao;

    public static String remPath;
    public static String traPath;
    public static String retPath;
    public static String recPath;
    public static int timTimer;

    // Mensagens WhatsApp
    public static String INSTANCE_ID = "14157386170";
    public static String CLIENT_ID = "1198624f";
    public static String CLIENT_SECRET = "75wMfRGfWqidEnkt";
    public static String WA_GATEWAY_URL = "https://messages-sandbox.nexmo.com/v0.1/messages";
    // Produção - WA_GATEWAY_URL = "https://api.nexmo.com/v0.1/messages

    // Dimob
    public static boolean dip_al = true;
    public static boolean dip_tx = true;
    public static boolean dip_mu = true;
    public static boolean dip_ju = true;
    public static boolean dip_co = true;
    public static boolean dip_ep = true;
    public static boolean dia_cm = true;
    public static boolean dia_tx = true;
    public static boolean dia_mu = true;
    public static boolean dia_ju = true;
    public static boolean dia_co = true;
    public static boolean dia_ep = true;

    // Screen Size
    public static int screenWidth = ScreenSize.getWidth();
    public static int screenHeight = ScreenSize.getHeight();

    // Variavel da conecxao de Internet
    public static boolean bInternet = false;

    public static void LerConf() {
        VariaveisGlobais.myLogo = System.getProperty("myLogo", "resources/login.jpg");

        // Printers
        VariaveisGlobais.Thermica = System.getProperty("Thermica", null);
        if (!checaPrint(VariaveisGlobais.Thermica)) VariaveisGlobais.Thermica = null;

        VariaveisGlobais.Printer = System.getProperty("Printer", null);
        if (!checaPrint(VariaveisGlobais.Printer)) VariaveisGlobais.Printer = null;

        VariaveisGlobais.Externo = System.getProperty("Externo", null);
        VariaveisGlobais.PrinterMode = System.getProperty("PrinterMode", "INTERNA"); // EXTERNA || INTERNA

        String BobSize[] = System.getProperty("bobinaSize", "215.0, 730.0, 12.0, 10.0, 0.0, 2.0").split(",");
        VariaveisGlobais.bobinaSize = new float[] {Float.valueOf(BobSize[0]),Float.valueOf(BobSize[1]),Float.valueOf(BobSize[2]),
                                                   Float.valueOf(BobSize[3]),Float.valueOf(BobSize[4]),Float.valueOf(BobSize[5])};

        // Impressão do Recibo
        VariaveisGlobais.logo_allign = System.getProperty("logo_allign", "CENTER");
        VariaveisGlobais.logo_width = Float.valueOf(System.getProperty("logo_width", "180"));
        VariaveisGlobais.logo_Heigth = Float.valueOf(System.getProperty("logo_heigth", "60"));
        VariaveisGlobais.recibo_vias = Integer.valueOf(System.getProperty("recibo_vias", "1"));
        VariaveisGlobais.logo_allign = System.getProperty("logo_allign", "Centro");
        VariaveisGlobais.logo_noprint = Boolean.valueOf(System.getProperty("logo_noprint", "false"));
        VariaveisGlobais.razao_noprint = Boolean.valueOf(System.getProperty("razao_noprint", "false"));
        VariaveisGlobais.cnpj_noprint = Boolean.valueOf(System.getProperty("cnpj_noprint", "false"));
        VariaveisGlobais.creci_noprint = Boolean.valueOf(System.getProperty("creci_noprint", "false"));
        VariaveisGlobais.endereco_noprint = Boolean.valueOf(System.getProperty("endereco_noprint", "false"));
        VariaveisGlobais.recibo_titulo = System.getProperty("recibo_titulo", "R E C I B O");
        VariaveisGlobais.copa_print = Boolean.valueOf(System.getProperty("copa_print", "true"));
        VariaveisGlobais.ref_print = Boolean.valueOf(System.getProperty("ref_print", "true"));
        VariaveisGlobais.qcr_print = Boolean.valueOf(System.getProperty("qcr_print", "true"));
        VariaveisGlobais.md5_print = Boolean.valueOf(System.getProperty("md5_print", "true"));

        // Impressão do Boleto
        VariaveisGlobais.pb_logo_width = Float.valueOf(System.getProperty("pb_logo_width", "180"));
        VariaveisGlobais.pb_logo_Heigth = Float.valueOf(System.getProperty("pb_logo_heigth", "60"));
        VariaveisGlobais.pb_logo_noprint = Boolean.valueOf(System.getProperty("pb_logo_noprint", "false"));
        VariaveisGlobais.pb_razao_noprint = Boolean.valueOf(System.getProperty("pb_razao_noprint", "false"));
        VariaveisGlobais.pb_cnpj_noprint = Boolean.valueOf(System.getProperty("pb_cnpj_noprint", "false"));
        VariaveisGlobais.pb_creci_noprint = Boolean.valueOf(System.getProperty("pb_creci_noprint", "false"));
        VariaveisGlobais.pb_endereco_noprint = Boolean.valueOf(System.getProperty("pb_endereco_noprint", "false"));
        VariaveisGlobais.pb_copa_print = Boolean.valueOf(System.getProperty("pb_copa_print", "true"));
        VariaveisGlobais.pb_qcr_print = Boolean.valueOf(System.getProperty("pb_qcr_print", "true"));
        // Instruções do Boleto
        VariaveisGlobais.pb_int01 = System.getProperty("pb_inst01", "");
        VariaveisGlobais.pb_int02 = System.getProperty("pb_inst02", "");
        VariaveisGlobais.pb_int03 = System.getProperty("pb_inst03", "");
        VariaveisGlobais.pb_int04 = System.getProperty("pb_inst04", "");
        VariaveisGlobais.pb_int05 = System.getProperty("pb_inst05", "");
        VariaveisGlobais.pb_int06 = System.getProperty("pb_inst06", "");
        VariaveisGlobais.pb_int07 = System.getProperty("pb_inst07", "");
        VariaveisGlobais.pb_int08 = System.getProperty("pb_inst08", "");
        VariaveisGlobais.pb_int09 = System.getProperty("pb_inst09", "");

        // Avisos e Mensagens
        VariaveisGlobais.am_aniv = Boolean.valueOf(System.getProperty("am_aniv", "true"));
        VariaveisGlobais.am_term = Boolean.valueOf(System.getProperty("am_term", "true"));
        VariaveisGlobais.am_dias = System.getProperty("am_dias", "60");

        VariaveisGlobais.am_reaj = Boolean.valueOf(System.getProperty("am_reaj", "true"));
        VariaveisGlobais.am_reajdias = System.getProperty("am_reajdias", "2");
        VariaveisGlobais.am_seg = Boolean.valueOf(System.getProperty("am_seg", "true"));
        VariaveisGlobais.am_segdias = System.getProperty("am_segdias", "60");

        VariaveisGlobais.reajManAluguel = Boolean.valueOf(System.getProperty("reajManAluguel", "false"));

        VariaveisGlobais.dbsenha = Boolean.valueOf(System.getProperty("dbSenha", "false"));
        VariaveisGlobais.dbnome  = System.getProperty("dbNome", "jgeralfx");
        VariaveisGlobais.unidade = System.getProperty("Unidade", "127.0.0.1");

        VariaveisGlobais.remPath = System.getProperty("remPath", "");
        VariaveisGlobais.traPath = System.getProperty("tranPath", "");
        VariaveisGlobais.retPath = System.getProperty("retPath", "");
        VariaveisGlobais.recPath = System.getProperty("recPath", "");
        VariaveisGlobais.timTimer = Integer.valueOf(System.getProperty("timTempo", "0").replace(".0", ""));

        // WhatsApp
        VariaveisGlobais.INSTANCE_ID = System.getProperty("instance_id", "14157386170");
        VariaveisGlobais.CLIENT_ID = System.getProperty("client_id", "1198624f");
        VariaveisGlobais.CLIENT_SECRET = System.getProperty("client_secret", "75wMfRGfWqidEnkt");

        // Dimob
        VariaveisGlobais.dip_al = Boolean.valueOf(System.getProperty("dip_al", "true"));
        VariaveisGlobais.dip_mu = Boolean.valueOf(System.getProperty("dip_mu", "true"));
        VariaveisGlobais.dip_ju = Boolean.valueOf(System.getProperty("dip_ju", "true"));
        VariaveisGlobais.dip_co = Boolean.valueOf(System.getProperty("dip_co", "true"));
        VariaveisGlobais.dip_ep = Boolean.valueOf(System.getProperty("dip_ep", "true"));
        VariaveisGlobais.dip_tx = Boolean.valueOf(System.getProperty("dip_tx", "true"));
        VariaveisGlobais.dia_cm = Boolean.valueOf(System.getProperty("dia_cm", "true"));
        VariaveisGlobais.dia_tx = Boolean.valueOf(System.getProperty("dia_tx", "true"));
        VariaveisGlobais.dia_mu = Boolean.valueOf(System.getProperty("dia_mu", "true"));
        VariaveisGlobais.dia_ju = Boolean.valueOf(System.getProperty("dia_ju", "true"));
        VariaveisGlobais.dia_co = Boolean.valueOf(System.getProperty("dia_co", "true"));
        VariaveisGlobais.dia_ep = Boolean.valueOf(System.getProperty("dia_ep", "true"));
    }
    
    public static Collections getAdmDados() {
        Collections retorno = new Collections();

        retorno.add("empresa", da_razao);
        retorno.add("endereco", da_ender);
        retorno.add("numero", da_numero);
        retorno.add("complemento", da_cplto);
        retorno.add("bairro", da_bairro);
        retorno.add("cidade", da_cidade);
        retorno.add("codmun", da_codmun);
        retorno.add("estado", da_estado);
        retorno.add("cep", da_cep);
        retorno.add("cnpj", da_cnpj);
        retorno.add("creci", da_creci);
        retorno.add("tipo", da_tipo);
        retorno.add("inscricao", da_insc);
        retorno.add("marca", da_marca);
        retorno.add("telefone", da_tel);
        retorno.add("hpage", da_hpage);
        retorno.add("email", da_email);
        retorno.add("marca", da_marca);
        retorno.add("responsavel", da_responsavel);
        retorno.add("respcpf", da_respcpf);

        String logoUri = null;
        try { logoUri = conexao.LerParametros("da_logo"); } catch (Exception ex) {}
        retorno.add("logo", logoUri);

        return retorno;
    }

    private static boolean checaPrint(String sprinter) {
        boolean retorno = false;
        if (sprinter == null) return retorno;

        try {
            PrintService[] pservices = PrinterJob.lookupPrintServices();
            if (pservices.length > 0) {
                for (PrintService ps : pservices) {

                if (ps.getName().trim().contains(sprinter.trim())) {
                        retorno = true;
                        break;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return retorno;
    }
}
