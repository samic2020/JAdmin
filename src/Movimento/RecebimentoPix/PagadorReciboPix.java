package Movimento.RecebimentoPix;

public class PagadorReciboPix {
    private String rc_Codigo;
    private String rc_NNumero;
    private String rc_DtDocumento;
    private String rc_NumDocumento;
    private String rc_DtProcessamento;
    private String rc_Vencimento;
    private String rc_Valor;

    private String[][] rc_Dados; // {{cod, desc, cp, valor}, ...}
    private String rc_mensagem;

    private String rc_instrucao01 = "";
    private String rc_instrucao02 = "";
    private String rc_instrucao03 = "";
    private String rc_instrucao04 = "";
    private String rc_instrucao05 = "";
    private String rc_instrucao06 = "";
    private String rc_instrucao07 = "";
    private String rc_instrucao08 = "";
    private String rc_instrucao09 = "";
    private String rc_instrucao10 = "";

    private String rc_linhaDIgitavel;
    private java.awt.Image rc_codigoBarras;

    public String getRc_Codigo() {
        return rc_Codigo;
    }

    public void setRc_Codigo(String rc_Codigo) {
        this.rc_Codigo = rc_Codigo;
    }

    public String getRc_NNumero() {
        return rc_NNumero;
    }

    public void setRc_NNumero(String rc_NNumero) {
        this.rc_NNumero = rc_NNumero;
    }

    public String getRc_DtDocumento() {
        return rc_DtDocumento;
    }

    public void setRc_DtDocumento(String rc_DtDocumento) {
        this.rc_DtDocumento = rc_DtDocumento;
    }

    public String getRc_NumDocumento() {
        return rc_NumDocumento;
    }

    public void setRc_NumDocumento(String rc_NumDocumento) {
        this.rc_NumDocumento = rc_NumDocumento;
    }

    public String getRc_DtProcessamento() {
        return rc_DtProcessamento;
    }

    public void setRc_DtProcessamento(String rc_DtProcessamento) {
        this.rc_DtProcessamento = rc_DtProcessamento;
    }

    public String getRc_Vencimento() {
        return rc_Vencimento;
    }

    public void setRc_Vencimento(String rc_Vencimento) {
        this.rc_Vencimento = rc_Vencimento;
    }

    public String getRc_Valor() {
        return rc_Valor;
    }

    public void setRc_Valor(String rc_Valor) {
        this.rc_Valor = rc_Valor;
    }

    public String[][] getRc_Dados() {
        return rc_Dados;
    }

    public void setRc_Dados(String[][] rc_Dados) {
        this.rc_Dados = rc_Dados;
    }

    public String getRc_mensagem() {
        return rc_mensagem;
    }

    public void setRc_mensagem(String rc_mensagem) {
        this.rc_mensagem = rc_mensagem;
    }

    public String getRc_instrucao01() {
        return rc_instrucao01;
    }

    public void setRc_instrucao01(String rc_instrucao01) {
        this.rc_instrucao01 = rc_instrucao01;
    }

    public String getRc_instrucao02() {
        return rc_instrucao02;
    }

    public void setRc_instrucao02(String rc_instrucao02) {
        this.rc_instrucao02 = rc_instrucao02;
    }

    public String getRc_instrucao03() {
        return rc_instrucao03;
    }

    public void setRc_instrucao03(String rc_instrucao03) {
        this.rc_instrucao03 = rc_instrucao03;
    }

    public String getRc_instrucao04() {
        return rc_instrucao04;
    }

    public void setRc_instrucao04(String rc_instrucao04) {
        this.rc_instrucao04 = rc_instrucao04;
    }

    public String getRc_instrucao05() {
        return rc_instrucao05;
    }

    public void setRc_instrucao05(String rc_instrucao05) {
        this.rc_instrucao05 = rc_instrucao05;
    }

    public String getRc_instrucao06() {
        return rc_instrucao06;
    }

    public void setRc_instrucao06(String rc_instrucao06) {
        this.rc_instrucao06 = rc_instrucao06;
    }

    public String getRc_instrucao07() {
        return rc_instrucao07;
    }

    public void setRc_instrucao07(String rc_instrucao07) {
        this.rc_instrucao07 = rc_instrucao07;
    }

    public String getRc_instrucao08() {
        return rc_instrucao08;
    }

    public void setRc_instrucao08(String rc_instrucao08) {
        this.rc_instrucao08 = rc_instrucao08;
    }

    public String getRc_instrucao09() {
        return rc_instrucao09;
    }

    public void setRc_instrucao09(String rc_instrucao09) {
        this.rc_instrucao09 = rc_instrucao09;
    }

    public String getRc_instrucao10() {
        return rc_instrucao10;
    }

    public void setRc_instrucao10(String rc_instrucao10) {
        this.rc_instrucao10 = rc_instrucao10;
    }

    public String getRc_linhaDIgitavel() {
        return rc_linhaDIgitavel;
    }

    public void setRc_linhaDIgitavel(String rc_linhaDIgitavel) {
        this.rc_linhaDIgitavel = rc_linhaDIgitavel;
    }

    public java.awt.Image getRc_codigoBarras() {
        return rc_codigoBarras;
    }

    public void setRc_codigoBarras(java.awt.Image rc_codigoBarras) {
        this.rc_codigoBarras = rc_codigoBarras;
    }
}
