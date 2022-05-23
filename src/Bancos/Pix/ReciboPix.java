package Bancos.Pix;

import java.awt.*;

public class ReciboPix {
    private String logoLocation;
    private String empNome;
    private String empEndL1;
    private String empEndL2;
    private String empEndL3;
    private String empEndL4;

    private String locaDescL01;
    private String locaCpL01;
    private String locaVrL01;
    private String locaDescL02;
    private String locaCpL02;
    private String locaVrL02;
    private String locaDescL03;
    private String locaCpL03;
    private String locaVrL03;
    private String locaDescL04;
    private String locaCpL04;
    private String locaVrL04;
    private String locaDescL05;
    private String locaCpL05;
    private String locaVrL05;
    private String locaDescL06;
    private String locaCpL06;
    private String locaVrL06;
    private String locaDescL07;
    private String locaCpL07;
    private String locaVrL07;
    private String locaDescL08;
    private String locaCpL08;
    private String locaVrL08;
    private String locaDescL09;
    private String locaCpL09;
    private String locaVrL09;
    private String locaDescL10;
    private String locaCpL10;
    private String locaVrL10;
    private String locaDescL11;
    private String locaCpL11;
    private String locaVrL11;
    private String locaDescL12;
    private String locaCpL12;
    private String locaVrL12;
    private String locaDescL13;
    private String locaCpL13;
    private String locaVrL13;
    private String locaDescL14;
    private String locaCpL14;
    private String locaVrL14;

    private String locaMsgL01;
    private String locaMsgL02;
    private String locaMsgL03;
    private String locaMsgL04;

    private String pixValidadePIX;
    private String pixDadosDatadoc;
    private String pixDadosVencimento;
    private String pixNNumero;
    private String pixDadosNumdoc;
    private String pixDadosVrdoc;

    private String sacDadosNome;
    private String sacDadosEndereco;
    private String sacDadosNumero;
    private String sacDadosCompl;
    private String sacDadosBairro;
    private String sacDadosCidade;
    private String sacDadosEstado;
    private String sacDadosCep;
    private String sacDadosCpfcnpj;

    private String pixCopiaeCola;
    private Image pixImage;

    public String getlogoLocation () { return logoLocation; }
    public void setlogoLocation(String Valor) { this.logoLocation = Valor; }

    public String getempNome() { return empNome; }
    public void setempNome(String Valor) { this.empNome = Valor; }

    public String getempEndL1() { return empEndL1; }
    public void setempEndL1(String Valor) { this.empEndL1 = Valor; }

    public String getempEndL2() { return empEndL2; }
    public void setempEndL2(String Valor) { this.empEndL2 = Valor; }

    public String getempEndL3() { return empEndL3; }
    public void setempEndL3(String Valor) { this.empEndL3 = Valor; }

    public String getempEndL4() { return empEndL4; }
    public void setempEndL4(String Valor) { this.empEndL4 = Valor; }

    public String getlocaDescL01() { return locaDescL01; }
    public void setlocaDescL01(String Valor) { this.locaDescL01 = Valor; }

    public String getlocaDescL02() { return locaDescL02; }
    public void setlocaDescL02(String Valor) { this.locaDescL02 = Valor; }

    public String getlocaDescL03() { return locaDescL03; }
    public void setlocaDescL03(String Valor) { this.locaDescL03 = Valor; }

    public String getlocaDescL04() { return locaDescL04; }
    public void setlocaDescL04(String Valor) { this.locaDescL04 = Valor; }

    public String getlocaDescL05() { return locaDescL05; }
    public void setlocaDescL05(String Valor) { this.locaDescL05 = Valor; }

    public String getlocaDescL06() { return locaDescL06; }
    public void setlocaDescL06(String Valor) { this.locaDescL06 = Valor; }

    public String getlocaDescL07() { return locaDescL07; }
    public void setlocaDescL07(String Valor) { this.locaDescL07 = Valor; }

    public String getlocaDescL08() { return locaDescL08; }
    public void setlocaDescL08(String Valor) { this.locaDescL08 = Valor; }

    public String getlocaDescL09() { return locaDescL09; }
    public void setlocaDescL09(String Valor) { this.locaDescL09 = Valor; }

    public String getlocaDescL10() { return locaDescL10; }
    public void setlocaDescL10(String Valor) { this.locaDescL10 = Valor; }

    public String getlocaDescL11() { return locaDescL11; }
    public void setlocaDescL11(String Valor) { this.locaDescL11 = Valor; }

    public String getlocaDescL12() { return locaDescL12; }
    public void setlocaDescL12(String Valor) { this.locaDescL12 = Valor; }

    public String getlocaDescL13() { return locaDescL13; }
    public void setlocaDescL13(String Valor) { this.locaDescL13 = Valor; }

    public String getlocaDescL14() { return locaDescL14; }
    public void setlocaDescL14(String Valor) { this.locaDescL14 = Valor; }

    public String getlocaCpL01() { return locaCpL01; }
    public void setlocaCpL01(String Valor) { this.locaCpL01 = Valor; }

    public String getlocaCpL02() { return locaCpL02; }
    public void setlocaCpL02(String Valor) { this.locaCpL02 = Valor; }

    public String getlocaCpL03() { return locaCpL03; }
    public void setlocaCpL03(String Valor) { this.locaCpL03 = Valor; }

    public String getlocaCpL04() { return locaCpL04; }
    public void setlocaCpL04(String Valor) { this.locaCpL04 = Valor; }

    public String getlocaCpL05() { return locaCpL05; }
    public void setlocaCpL05(String Valor) { this.locaCpL05 = Valor; }

    public String getlocaCpL06() { return locaCpL06; }
    public void setlocaCpL06(String Valor) { this.locaCpL06 = Valor; }

    public String getlocaCpL07() { return locaCpL07; }
    public void setlocaCpL07(String Valor) { this.locaCpL07 = Valor; }

    public String getlocaCpL08() { return locaCpL08; }
    public void setlocaCpL08(String Valor) { this.locaCpL08 = Valor; }

    public String getlocaCpL09() { return locaCpL09; }
    public void setlocaCpL09(String Valor) { this.locaCpL09 = Valor; }

    public String getlocaCpL10() { return locaCpL10; }
    public void setlocaCpL10(String Valor) { this.locaCpL10 = Valor; }

    public String getlocaCpL11() { return locaCpL11; }
    public void setlocaCpL11(String Valor) { this.locaCpL11 = Valor; }

    public String getlocaCpL12() { return locaCpL12; }
    public void setlocaCpL12(String Valor) { this.locaCpL12 = Valor; }

    public String getlocaCpL13() { return locaCpL13; }
    public void setlocaCpL13(String Valor) { this.locaCpL13 = Valor; }

    public String getlocaCpL14() { return locaCpL14; }
    public void setlocaCpL14(String Valor) { this.locaCpL14 = Valor; }

    public String getlocaVrL01() { return locaVrL01; }
    public void setlocaVrL01(String Valor) { this.locaVrL01 = Valor; }

    public String getlocaVrL02() { return locaVrL02; }
    public void setlocaVrL02(String Valor) { this.locaVrL02 = Valor; }

    public String getlocaVrL03() { return locaVrL03; }
    public void setlocaVrL03(String Valor) { this.locaVrL03 = Valor; }

    public String getlocaVrL04() { return locaVrL04; }
    public void setlocaVrL04(String Valor) { this.locaVrL04 = Valor; }

    public String getlocaVrL05() { return locaVrL05; }
    public void setlocaVrL05(String Valor) { this.locaVrL05 = Valor; }

    public String getlocaVrL06() { return locaVrL06; }
    public void setlocaVrL06(String Valor) { this.locaVrL06 = Valor; }

    public String getlocaVrL07() { return locaVrL07; }
    public void setlocaVrL07(String Valor) { this.locaVrL07 = Valor; }

    public String getlocaVrL08() { return locaVrL08; }
    public void setlocaVrL08(String Valor) { this.locaVrL08 = Valor; }

    public String getlocaVrL09() { return locaVrL09; }
    public void setlocaVrL09(String Valor) { this.locaVrL09 = Valor; }

    public String getlocaVrL10() { return locaVrL10; }
    public void setlocaVrL10(String Valor) { this.locaVrL10 = Valor; }

    public String getlocaVrL11() { return locaVrL11; }
    public void setlocaVrL11(String Valor) { this.locaVrL11 = Valor; }

    public String getlocaVrL12() { return locaVrL12; }
    public void setlocaVrL12(String Valor) { this.locaVrL12 = Valor; }

    public String getlocaVrL13() { return locaVrL13; }
    public void setlocaVrL13(String Valor) { this.locaVrL13 = Valor; }

    public String getlocaVrL14() { return locaVrL14; }
    public void setlocaVrL14(String Valor) { this.locaVrL14 = Valor; }

    public String getlocaMsgL01() { return locaMsgL01; }
    public void setlocaMsgL01(String Valor) { this.locaMsgL01 = Valor; }

    public String getlocaMsgL02() { return locaMsgL02; }
    public void setlocaMsgL02(String Valor) { this.locaMsgL02 = Valor; }

    public String getlocaMsgL03() { return locaMsgL03; }
    public void setlocaMsgL03(String Valor) { this.locaMsgL03 = Valor; }

    public String getlocaMsgL04() { return locaMsgL04; }
    public void setlocaMsgL04(String Valor) { this.locaMsgL04 = Valor; }

    public String getpixValidadePIX() { return pixValidadePIX; }
    public void setpixValidadePIX(String pixValidadePIX) { this.pixValidadePIX = pixValidadePIX; }

    public String getpixDadosDatadoc() { return pixDadosDatadoc; }
    public void setpixDadosDatadoc(String Valor) { this.pixDadosDatadoc = Valor; }

    public String getpixDadosVencimento() { return pixDadosVencimento; }
    public void setpixDadosVencimento(String pixDadosVencimento) { this.pixDadosVencimento = pixDadosVencimento; }

    public String getpixNNumero() { return pixNNumero; }
    public void setpixNNumero(String pixNNumero) { this.pixNNumero = pixNNumero; }

    public String getpixDadosNumdoc() { return pixDadosNumdoc; }
    public void setpixDadosNumdoc(String Valor) { this.pixDadosNumdoc = Valor; }

    public String getpixDadosVrdoc() { return pixDadosVrdoc; }
    public void setpixDadosVrdoc(String Valor) { this.pixDadosVrdoc = Valor; }

    public String getsacDadosNome() { return sacDadosNome; }
    public void setsacDadosNome(String Valor) { this.sacDadosNome = Valor; }

    public String getsacDadosEndereco() { return sacDadosEndereco; }
    public void setsacDadosEndereco(String Valor) { this.sacDadosEndereco = Valor; }

    public String getsacDadosNumero() { return sacDadosNumero; }
    public void setsacDadosNumero(String Valor) { this.sacDadosNumero = Valor; }

    public String getsacDadosCompl() { return sacDadosCompl; }
    public void setsacDadosCompl(String Valor) { this.sacDadosCompl = Valor; }

    public String getsacDadosBairro() { return sacDadosBairro; }
    public void setsacDadosBairro(String Valor) { this.sacDadosBairro = Valor; }

    public String getsacDadosCidade() { return sacDadosCidade; }
    public void setsacDadosCidade(String Valor) { this.sacDadosCidade = Valor; }

    public String getsacDadosEstado() { return sacDadosEstado; }
    public void setsacDadosEstado(String Valor) { this.sacDadosEstado = Valor; }

    public String getsacDadosCep() { return sacDadosCep; }
    public void setsacDadosCep(String Valor) { this.sacDadosCep = Valor; }

    public String getsacDadosCpfcnpj() { return sacDadosCpfcnpj; }
    public void setsacDadosCpfcnpj(String Valor) { this.sacDadosCpfcnpj = Valor; }

    public String getPixCopiaeCola() { return pixCopiaeCola; }
    public void setPixCopiaeCola(String pixCopiaeCola) { this.pixCopiaeCola = pixCopiaeCola; }

    public Image getPixImage() { return pixImage; }
    public void setPixImage(Image pixImage) { this.pixImage = pixImage; }
}
