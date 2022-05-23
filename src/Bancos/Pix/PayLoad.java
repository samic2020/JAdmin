package Bancos.Pix;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;

import javax.swing.*;
import java.text.DecimalFormat;

public class PayLoad {
    /**
     * IDs do Payload do Pix
     * @var string
     */
    private final String ID_PAYLOAD_FORMAT_INDICATOR = "00";
    private final String ID_MERCHANT_ACCOUNT_INFORMATION = "26";
    private final String ID_MERCHANT_ACCOUNT_INFORMATION_GUI = "00";
    private final String ID_MERCHANT_ACCOUNT_INFORMATION_KEY = "01";
    private final String ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION = "02";
    private final String ID_MERCHANT_CATEGORY_CODE = "52";
    private final String ID_TRANSACTION_CURRENCY = "53";
    private final String ID_TRANSACTION_AMOUNT = "54";
    private final String ID_COUNTRY_CODE = "58";
    private final String ID_MERCHANT_NAME = "59";
    private final String ID_MERCHANT_CITY = "60";
    private final String ID_POSTAL_CODE = "61";
    private final String ID_ADDITIONAL_DATA_FIELD_TEMPLATE = "62";
    private final String ID_ADDITIONAL_DATA_FIELD_TEMPLATE_TXID = "05";
    private final String ID_CRC16 = "63";

    /**
     * Chave Pix
     * @var string
     */
    private String pixKey;

    /**
     * DescriÃ§ao do pagamento
     * @var string
     */
    private String description;

    /**
     * Nome do titular da conta
     * @var string
     */
    private String merchantName;

    /**
     * Cidade do titular da conta
     * @var string
     */
    private String merchantCity;

    /**
     * Id da transacao Pix
     * @var string
     */
    private String txid;

    /**
     * Cep do Titular do Pix
     */
    private String postalCode;

    /**
     * Valor da transacao
     * @var string
     */
    private String amount;

    /**
     * Metodo responsavel por definir o valor de pixKey
     * @param string pixKey
     */
    public String setPixKey(String pixKey) {
        this.pixKey = pixKey;
        return this.pixKey;
    }

    /**
     * Metodo responsavel por definir o valor de description
     * @param string pixKey
     */
    public String setDescription(String description) {
        this.description = description;
        return this.description;
    }

    /**
     * Metodo responsavel por definir o valor de merchantName
     * @param string merchantName
     */
    public String setMerchantName(String merchantname) {
        this.merchantName = merchantname;
        return this.merchantName;
    }

    /**
     * Metodo responsavel por definir o valor de merchantCity
     * @param string merchantcity
     */
    public String setMerchantCity(String merchantcity) {
        this.merchantCity = merchantcity;
        return this.merchantCity;
    }

    /**
     * Metodo responsavel por definir o valor de txid
     * @param string txid
     */
    public String setTxId(String txid) {
        this.txid = txid;
        return this.txid;
    }

    /**
     * Metodo responsavel por definir o cep do Pix
     * @param string postalcode
     */
    public String setPostalCode(String postalcode) {
        this.postalCode = postalcode;
        return this.postalCode;
    }

    /**
     * Metodo responsavel por definir o valor de amount
     * @param string amount
     */
    public String setAmount(float amount) {
        this.amount = new DecimalFormat("0.00").format(amount).replace(",", ".");
        return this.amount;
    }

    /**
     *
     * @param string id
     * @param string value
     * @return string id+size+value
     */
    private String getValue(String id, String value) {
        String size =  FuncoesGlobais.StrZero(String.valueOf(value.length()),2);
        return id + size + value;
    }

    /**
     * Metodo responsavel por retornar os valores completos da informacao da conta
     * @return
     */
    private String getMerchantAccountInformation() {
        // DOMINIO DO BANCO
        String gui = this.getValue(this.ID_MERCHANT_ACCOUNT_INFORMATION_GUI, "BR.GOV.BCB.PIX");

        // CHAVE PIX
        String key = this.getValue(this.ID_MERCHANT_ACCOUNT_INFORMATION_KEY, this.pixKey);

        // DESCRICAO DO PAGAMENTO
        String description = (this.description.length() > 0 ? this.getValue(this.ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION, this.description) : "");

        // VALOR COMPLETO DA CONTA
        return this.getValue(this.ID_MERCHANT_ACCOUNT_INFORMATION, gui + key + description);
    }

    /**
     * Metodo responsavel por retornar os valores completos do campo adicional do pix (TXID)
     * @return string
     */
    private String getAditionalDataFieldTemplate() {
        // TXID
        String txid = this.getValue(this.ID_ADDITIONAL_DATA_FIELD_TEMPLATE_TXID, this.txid);

        // RETORNA O VALOR COMPLETO
        return this.getValue(this.ID_ADDITIONAL_DATA_FIELD_TEMPLATE, txid);
    }

    /**
     * Metodo responsavel por retornar os valores completos do campo adicional do pix (TXID)
     * @return string
     */
    private String getAditionalDataFieldTemplate_old() {
        // TXID
        return this.getValue(this.ID_ADDITIONAL_DATA_FIELD_TEMPLATE, this.txid);
    }

    /**
     * Metodo responsavel pelo calculo do CRC16 no padrao Pix
     * @param string.getBytes buffer
     * @return string crc16
     */
    private static String CRC16(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x1021;
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return Integer.toHexString(wCRCin ^= 0x0000).toUpperCase();
    }

    /**
     * Metodo responsavel por gerar codigo de identificacao do pagamento em TxId
     * @param string value
     * @param string tam
     * @return string
     */
    public String NossoNumeroPix(String value, int tam) {
        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
                Integer.valueOf(value).toString().trim(),tam - 1);
        String valor2 = CalcDig11N(valor1);
        return valor1 + valor2;
    }

    /**
     * Metodo responsavel pela leitura dos dados do PIX
     * @param banco
     * @return string[][]
     */
    static public Object[][] LerBancoPIX(String banco) {
        DbMain conn = VariaveisGlobais.conexao;
        Object dadosPix[][] = null;
        try {
            dadosPix = conn.LerCamposTabela(
                    new String[] {"tipo","chave","banco","nnumero"}, "bancos_pix",
                    "Trim(Upper(banco)) = '" + banco.trim().toUpperCase() + "'");
        } catch (Exception e) {e.printStackTrace();}
        return dadosPix;
    }

    /**
     * Metodo responsavel por atualizar o nnumero PIX no bancos_pix
     * @param string nbanco
     * @param string Value
     */
    static public void GravarNnumeroPIX(String nbanco, String Value) {
        DbMain conn = VariaveisGlobais.conexao;

        Object dadosBol[][] = null; double oldnnumero = -1;
        try { dadosBol = conn.LerCamposTabela( new String[] {"nnumero"}, "bancos_pix", "Trim(Upper(banco)) = '" + nbanco.trim().toUpperCase() + "'"); } catch (Exception e) {e.printStackTrace();}
        if (dadosBol != null) {
            oldnnumero = Double.valueOf(dadosBol[0][3].toString());
        } else {
            JOptionPane.showMessageDialog(null, "Houve um problema ao ler nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)97665-9897");
            System.exit(1);
        }

        if (Double.valueOf(Value) > oldnnumero) {
            String sql = "UPDATE bancos_pix SET nnumero = '" + Value + "' WHERE Trim(Upper(banco)) = '" + nbanco.trim().toUpperCase() + "';";
            try { conn.ExecutarComando(sql);} catch (Exception e) {
                JOptionPane.showMessageDialog(null, "NÃ£o consegui gravar Nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)97665-9897");
                System.exit(1);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nnumero nÃ£o coerente!!!\nContacte o administrador do sistema.\nTel.:(21)997665-9897");
            System.exit(1);
        }
    }

    /**
     * Metodo responsavel por gera o codigo completo do payload Pix
     * @return string
     */
    public String getPayload() {
        String payLoad = this.getValue(this.ID_PAYLOAD_FORMAT_INDICATOR, "01");
        payLoad += this.getMerchantAccountInformation();
        payLoad += this.getValue(this.ID_MERCHANT_CATEGORY_CODE, "0000");
        payLoad += this.getValue(this.ID_TRANSACTION_CURRENCY, "986");
        payLoad += this.getValue(this.ID_TRANSACTION_AMOUNT, this.amount);
        payLoad += this.getValue(this.ID_COUNTRY_CODE, "BR");
        payLoad += this.getValue(this.ID_MERCHANT_NAME, this.merchantName);
        payLoad += this.getValue(this.ID_MERCHANT_CITY, this.merchantCity);
        //payLoad += this.getValue(this.ID_POSTAL_CODE, this.postalCode);
        payLoad += this.getAditionalDataFieldTemplate();
        payLoad += this.ID_CRC16 + "04";

        // RETORNA O PAYLOAD + CRC16
        return payLoad + CRC16(payLoad.getBytes());
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
