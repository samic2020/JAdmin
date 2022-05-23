package NotaFiscal;

import Funcoes.Dates;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class nfse_xml {
    private String nota_numero = "";
    private String nota_serie = "";
    private String nota_cnpj = "";
    private String nota_inscmun = "";
    private String nota_inscest = "ISENTA";
    private String nota_natope = "1";       // 1
    private String nota_regimetrib = "6";   // 6
    private String nota_optsimples = "1";   // 1 - Sim; 2 - Não
    private String nota_status = "1";       // 1
    private String nota_dtcancel = "";     // vazia
    private String nota_motivocancel = ""; // vazia
    private String nota_nfsesubst = "";
    private String nota_nfseserie = "";
    private String nota_data = "";
    private String nota_outros = "";
    private String nota_esitemlistaserv = "1.07";
    private String nota_escnae = "";
    private String nota_cdtribmun = "";
    private String nota_descriminacao = "";
    private String nota_esmunicipio = "";
    private String nota_vlservico = "0.00";
    private String nota_vldeducao = "0.00";
    private String nota_vlpis = "0.00";
    private String nota_vlconfins = "0.00";
    private String nota_vlinss = "0.00";
    private String nota_vlir = "0.00";
    private String nota_vlcsll = "0.00";
    private String nota_issretido = "2";  // 1 - Sim; 2 - Não
    private String nota_vliss = "0.00";
    private String nota_vlissretido = "0.00"; 
    private String nota_vloutrasretencoes = "0.00";
    private String nota_vlbasecalculo = "0.00";
    private String nota_vlaliquota = "2.0000";
    private String nota_vlliquidonfse = "0.00";
    private String nota_vldescincond = "0.00";
    private String nota_vldesccond = "0.00";
    private String nota_alpis = "0.000";
    private String nota_alconfins = "0.000";
    private String nota_alinss = "0.000";
    private String nota_alir = "0.000";
    private String nota_alcsll = "0.000";
    private String nota_razao = "";
    private String nota_fanta = "";
    private String nota_endereco = "";
    private String nota_endnumero = "";
    private String nota_endcompl = "";
    private String nota_endbairro = "";
    private String nota_enduf = "";
    private String nota_endcep = "";
    private String nota_endcdmun = "";
    private String nota_telefone = "";
    private String nota_email = "";
    private String nota_tomcpfcnpj = "";
    private String nota_tominsmun = "";
    private String nota_tominsest = "ISENTO";
    private String nota_tomrazao = "";
    private String nota_tomend = "";
    private String nota_tomendnum = "";
    private String nota_tomendcplto = "";
    private String nota_tomendbairro = "";
    private String nota_tomenduf = "";
    private String nota_tomendcep = "";
    private String nota_tomendmun = "";
    private String nota_tomtelefone = "";
    private String nota_tomemail = "";
    private String nota_intrazao = "";
    private String nota_intcpfcnpj = "";
    private String nota_intcdcpfcnpjtipo = "3";
    private String nota_intincmun = "";
    private String nota_orgaogeradorescdmun = "";
    private String nota_orgaogeradoruf = "";
    private String nota_esrps = "";

    public void setNota_numero(String nota_numero) {
        this.nota_numero = nota_numero;
    }

    public void setNota_serie(String nota_serie) {
        this.nota_serie = nota_serie;
    }

    public void setNota_cnpj(String nota_cnpj) {
        this.nota_cnpj = nota_cnpj;
    }

    public void setNota_inscmun(String nota_inscmun) {
        this.nota_inscmun = nota_inscmun;
    }

    public void setNota_inscest(String nota_inscest) {
        this.nota_inscest = nota_inscest;
    }

    public void setNota_natope(String nota_natope) {
        this.nota_natope = nota_natope;
    }

    public void setNota_regimetrib(String nota_regimetrib) {
        this.nota_regimetrib = nota_regimetrib;
    }

    public void setNota_optsimples(String nota_optsimples) {
        this.nota_optsimples = nota_optsimples;
    }

    public void setNota_status(String nota_status) {
        this.nota_status = nota_status;
    }

    public void setNota_dtcancel(String nota_dtcancel) {
        this.nota_dtcancel = nota_dtcancel;
    }

    public void setNota_motivocancel(String nota_motivocancel) {
        this.nota_motivocancel = nota_motivocancel;
    }

    public void setNota_nfsesubst(String nota_nfsesubst) {
        this.nota_nfsesubst = nota_nfsesubst;
    }

    public void setNota_nfseserie(String nota_nfseserie) {
        this.nota_nfseserie = nota_nfseserie;
    }

    public void setNota_data(String nota_data) {
        this.nota_data = nota_data;
    }

    public void setNota_outros(String nota_outros) {
        this.nota_outros = nota_outros;
    }

    public void setNota_esitemlistaserv(String nota_esitemlistaserv) {
        this.nota_esitemlistaserv = nota_esitemlistaserv;
    }

    public void setNota_escnae(String nota_escnae) {
        this.nota_escnae = nota_escnae;
    }

    public void setNota_cdtribmun(String nota_cdtribmun) {
        this.nota_cdtribmun = nota_cdtribmun;
    }

    public void setNota_descriminacao(String nota_descriminacao) {
        this.nota_descriminacao = nota_descriminacao;
    }

    public void setNota_esmunicipio(String nota_esmunicipio) {
        this.nota_esmunicipio = nota_esmunicipio;
    }

    public void setNota_vlservico(String nota_vlservico) {
        this.nota_vlservico = nota_vlservico;
    }

    public void setNota_vldeducao(String nota_vldeducao) {
        this.nota_vldeducao = nota_vldeducao;
    }

    public void setNota_vlpis(String nota_vlpis) {
        this.nota_vlpis = nota_vlpis;
    }

    public void setNota_vlconfins(String nota_vlconfins) {
        this.nota_vlconfins = nota_vlconfins;
    }

    public void setNota_vlinss(String nota_vlinss) {
        this.nota_vlinss = nota_vlinss;
    }

    public void setNota_vlir(String nota_vlir) {
        this.nota_vlir = nota_vlir;
    }

    public void setNota_vlcsll(String nota_vlcsll) {
        this.nota_vlcsll = nota_vlcsll;
    }

    public void setNota_issretido(String nota_issretido) {
        this.nota_issretido = nota_issretido;
    }

    public void setNota_vliss(String nota_vliss) {
        this.nota_vliss = nota_vliss;
    }

    public void setNota_vlissretido(String nota_vlissretido) {
        this.nota_vlissretido = nota_vlissretido;
    }

    public void setNota_vloutrasretencoes(String nota_vloutrasretencoes) {
        this.nota_vloutrasretencoes = nota_vloutrasretencoes;
    }

    public void setNota_vlbasecalculo(String nota_vlbasecalculo) {
        this.nota_vlbasecalculo = nota_vlbasecalculo;
    }

    public void setNota_vlaliquota(String nota_vlaliquota) {
        this.nota_vlaliquota = nota_vlaliquota;
    }

    public void setNota_vlliquidonfse(String nota_vlliquidonfse) {
        this.nota_vlliquidonfse = nota_vlliquidonfse;
    }

    public void setNota_vldescincond(String nota_vldescincond) {
        this.nota_vldescincond = nota_vldescincond;
    }

    public void setNota_vldesccond(String nota_vldesccond) {
        this.nota_vldesccond = nota_vldesccond;
    }

    public void setNota_alpis(String nota_alpis) {
        this.nota_alpis = nota_alpis;
    }

    public void setNota_alconfins(String nota_alconfins) {
        this.nota_alconfins = nota_alconfins;
    }

    public void setNota_alinss(String nota_alinss) {
        this.nota_alinss = nota_alinss;
    }

    public void setNota_alir(String nota_alir) {
        this.nota_alir = nota_alir;
    }

    public void setNota_alcsll(String nota_alcsll) {
        this.nota_alcsll = nota_alcsll;
    }

    public void setNota_razao(String nota_razao) {
        this.nota_razao = nota_razao;
    }

    public void setNota_fanta(String nota_fanta) {
        this.nota_fanta = nota_fanta;
    }

    public void setNota_endereco(String nota_endereco) {
        this.nota_endereco = nota_endereco;
    }

    public void setNota_endnumero(String nota_endnumero) {
        this.nota_endnumero = nota_endnumero;
    }

    public void setNota_endcompl(String nota_endcompl) {
        this.nota_endcompl = nota_endcompl;
    }

    public void setNota_endbairro(String nota_endbairro) {
        this.nota_endbairro = nota_endbairro;
    }

    public void setNota_enduf(String nota_enduf) {
        this.nota_enduf = nota_enduf;
    }

    public void setNota_endcep(String nota_endcep) {
        this.nota_endcep = nota_endcep;
    }

    public void setNota_endcdmun(String nota_endcdmun) {
        this.nota_endcdmun = nota_endcdmun;
    }

    public void setNota_telefone(String nota_telefone) {
        this.nota_telefone = nota_telefone;
    }

    public void setNota_email(String nota_email) {
        this.nota_email = nota_email;
    }

    public void setNota_tomcpfcnpj(String nota_tomcpfcnpj) {
        this.nota_tomcpfcnpj = nota_tomcpfcnpj;
    }

    public void setNota_tominsmun(String nota_tominsmun) {
        this.nota_tominsmun = nota_tominsmun;
    }

    public void setNota_tominsest(String nota_tominsest) {
        this.nota_tominsest = nota_tominsest;
    }

    public void setNota_tomrazao(String nota_tomrazao) {
        this.nota_tomrazao = nota_tomrazao;
    }

    public void setNota_tomend(String nota_tomend) {
        this.nota_tomend = nota_tomend;
    }

    public void setNota_tomendnum(String nota_tomendnum) {
        this.nota_tomendnum = nota_tomendnum;
    }

    public void setNota_tomendcplto(String nota_tomendcplto) {
        this.nota_tomendcplto = nota_tomendcplto;
    }

    public void setNota_tomendbairro(String nota_tomendbairro) {
        this.nota_tomendbairro = nota_tomendbairro;
    }

    public void setNota_tomenduf(String nota_tomenduf) {
        this.nota_tomenduf = nota_tomenduf;
    }

    public void setNota_tomendcep(String nota_tomendcep) {
        this.nota_tomendcep = nota_tomendcep;
    }

    public void setNota_tomendmun(String nota_tomendmun) {
        this.nota_tomendmun = nota_tomendmun;
    }

    public void setNota_tomtelefone(String nota_tomtelefone) {
        this.nota_tomtelefone = nota_tomtelefone;
    }

    public void setNota_tomemail(String nota_tomemail) {
        this.nota_tomemail = nota_tomemail;
    }

    public void setNota_intrazao(String nota_intrazao) {
        this.nota_intrazao = nota_intrazao;
    }

    public void setNota_intcpfcnpj(String nota_intcpfcnpj) {
        this.nota_intcpfcnpj = nota_intcpfcnpj;
    }

    public void setNota_intcdcpfcnpjtipo(String nota_intcdcpfcnpjtipo) {
        this.nota_intcdcpfcnpjtipo = nota_intcdcpfcnpjtipo;
    }

    public void setNota_intincmun(String nota_intincmun) {
        this.nota_intincmun = nota_intincmun;
    }

    public void setNota_orgaogeradorescdmun(String nota_orgaogeradorescdmun) {
        this.nota_orgaogeradorescdmun = nota_orgaogeradorescdmun;
    }

    public void setNota_orgaogeradoruf(String nota_orgaogeradoruf) {
        this.nota_orgaogeradoruf = nota_orgaogeradoruf;
    }

    public void setNota_esrps(String nota_esrps) {
        this.nota_esrps = nota_esrps;
    }
    
    public void nfse_xml() {}

    public void nfse_102(String lote) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element notas = doc.createElement("NOTAS");
            doc.appendChild(notas);

            Element versao = doc.createElement("VERSAO");
            versao.setTextContent("1.02");
            notas.appendChild(versao);

            Element nota = doc.createElement("NOTA");
            notas.appendChild(nota);

            Element numero = doc.createElement("NUMERO");
            numero.setTextContent(nota_numero);
            nota.appendChild(numero);

            Element serie = doc.createElement("SERIE");
            serie.setTextContent(nota_serie);
            nota.appendChild(serie);

            Element cnpj = doc.createElement("CNPJ");
            cnpj.setTextContent(nota_cnpj);
            nota.appendChild(cnpj);

            Element inscricaoMunicipal = doc.createElement("INSCRICAO_MUNICIPAL");
            inscricaoMunicipal.setTextContent(nota_inscmun);
            nota.appendChild(inscricaoMunicipal);

            Element preInscricaoEstadual = doc.createElement("PRE_INSCRICAO_ESTADUAL");
            preInscricaoEstadual.setTextContent(nota_inscest);
            nota.appendChild(preInscricaoEstadual);

            //Element cdVerificacao = doc.createElement("CD_VERIFICACAO");
            //cdVerificacao.setTextContent("917345C67");
            //nota.appendChild(cdVerificacao);

            Element cdNaturezaOperacao = doc.createElement("CD_NATUREZA_OPERACAO");
            cdNaturezaOperacao.setTextContent(nota_natope);
            nota.appendChild(cdNaturezaOperacao);

            Element cdRegimeEspecialTributacao = doc.createElement("CD_REGIME_ESPECIAL_TRIBUTACAO");
            cdRegimeEspecialTributacao.setTextContent(nota_regimetrib);
            nota.appendChild(cdRegimeEspecialTributacao);

            Element snOptanteSimplesNacional = doc.createElement("SN_OPTANTE_SIMPLES_NACIONAL");
            snOptanteSimplesNacional.setTextContent(nota_optsimples);
            nota.appendChild(snOptanteSimplesNacional);

            Element cdStatus = doc.createElement("CD_STATUS");
            cdStatus.setTextContent(nota_status);
            nota.appendChild(cdStatus);

            if (!nota_dtcancel.equalsIgnoreCase("")) {
                Element dtCancelamento = doc.createElement("DT_CANCELAMENTO");
                dtCancelamento.setTextContent(nota_dtcancel);
                nota.appendChild(dtCancelamento);

                Element motivoCancelamento = doc.createElement("MOTIVO_CANCELAMENTO");
                motivoCancelamento.setTextContent(nota_motivocancel);
                nota.appendChild(motivoCancelamento);
            }
            
            Element nfseSubstituta = doc.createElement("AR_NFSE_SUBSTITUTA");
            nfseSubstituta.setTextContent(nota_nfsesubst);
            nota.appendChild(nfseSubstituta);

            Element serieSubstituta = doc.createElement("SERIE_SUBSTITUTA");
            serieSubstituta.setTextContent(nota_nfseserie);
            nota.appendChild(serieSubstituta);

            Element dtCompetencia = doc.createElement("DT_COMPETENCIA");
            dtCompetencia.setTextContent(nota_data);
            nota.appendChild(dtCompetencia);

            Element outrasInformacoes = doc.createElement("OUTRAS_INFORMACOES");
            outrasInformacoes.setTextContent(nota_outros);
            nota.appendChild(outrasInformacoes);

            Element esItemListaServico = doc.createElement("ES_ITEM_LISTA_SERVICO");
            esItemListaServico.setTextContent(nota_esitemlistaserv);
            nota.appendChild(esItemListaServico);

            Element esCnae = doc.createElement("ES_CNAE");
            esCnae.setTextContent(nota_escnae);
            nota.appendChild(esCnae);

            Element cdTributacaoMunicipio = doc.createElement("CD_TRIBUTACAO_MUNICIPIO");
            cdTributacaoMunicipio.setTextContent(nota_cdtribmun);
            nota.appendChild(cdTributacaoMunicipio);

            Element discriminacao = doc.createElement("DISCRIMINACAO");
            discriminacao.setTextContent(nota_descriminacao);
            nota.appendChild(discriminacao);

            Element esMunicipio = doc.createElement("ES_MUNICIPIO");
            esMunicipio.setTextContent(nota_esmunicipio);
            nota.appendChild(esMunicipio);

            Element vlServico = doc.createElement("VL_SERVICO");
            vlServico.setTextContent(nota_vlservico);
            nota.appendChild(vlServico);

            Element vlDeducao = doc.createElement("VL_DEDUCAO");
            vlDeducao.setTextContent(nota_vldeducao);
            nota.appendChild(vlDeducao);

            Element vlPis = doc.createElement("VL_PIS");
            vlPis.setTextContent(nota_vlpis);
            nota.appendChild(vlPis);

            Element vlCofins = doc.createElement("VL_COFINS");
            vlCofins.setTextContent(nota_vlconfins);
            nota.appendChild(vlCofins);

            Element vlInss = doc.createElement("VL_INSS");
            vlInss.setTextContent(nota_vlinss);
            nota.appendChild(vlInss);

            Element vlIr = doc.createElement("VL_IR");
            vlIr.setTextContent(nota_vlir);
            nota.appendChild(vlIr);

            Element vlCsll = doc.createElement("VL_CSLL");
            vlCsll.setTextContent(nota_vlcsll);
            nota.appendChild(vlCsll);

            Element snIssRetido = doc.createElement("SN_ISS_RETIDO");
            snIssRetido.setTextContent(nota_issretido);  // 1 - SIM | 2 - NAO
            nota.appendChild(snIssRetido);

            Element vlIss = doc.createElement("VL_ISS");
            vlIss.setTextContent(nota_vliss);
            nota.appendChild(vlIss);

            Element vlIssRetido = doc.createElement("VL_ISS_RETIDO");
            vlIssRetido.setTextContent(nota_vlissretido);
            nota.appendChild(vlIssRetido);

            Element outrasRetencoes = doc.createElement("VL_OUTRAS_RETENCOES");
            outrasRetencoes.setTextContent(nota_vloutrasretencoes);
            nota.appendChild(outrasRetencoes);

            Element baseCalculo = doc.createElement("VL_BASE_CALCULO");
            baseCalculo.setTextContent(nota_vlbasecalculo);
            nota.appendChild(baseCalculo);

            Element vlAliquota = doc.createElement("VL_ALIQUOTA");
            vlAliquota.setTextContent(nota_vlaliquota);
            nota.appendChild(vlAliquota);

            Element vlLiquidoNfse = doc.createElement("VL_LIQUIDO_NFSE");
            vlLiquidoNfse.setTextContent(nota_vlliquidonfse);
            nota.appendChild(vlLiquidoNfse);

            Element vlDescontoIncondicionado = doc.createElement("VL_DESCONTO_INCONDICIONADO");
            vlDescontoIncondicionado.setTextContent(nota_vldescincond);
            nota.appendChild(vlDescontoIncondicionado);

            Element vlDescontoCondicionado = doc.createElement("VL_DESCONTO_CONDICIONADO");
            vlDescontoCondicionado.setTextContent(nota_vldesccond);
            nota.appendChild(vlDescontoCondicionado);

            Element alPis = doc.createElement("AL_PIS");
            alPis.setTextContent(nota_alpis);
            nota.appendChild(alPis);

            Element alCofins = doc.createElement("AL_COFINS");
            alCofins.setTextContent(nota_alconfins);
            nota.appendChild(alCofins);

            Element alInss = doc.createElement("AL_INSS");
            alInss.setTextContent(nota_alinss);
            nota.appendChild(alInss);

            Element alIr = doc.createElement("AL_IR");
            alIr.setTextContent(nota_alir);
            nota.appendChild(alIr);

            Element alCsll = doc.createElement("AL_CSLL");
            alCsll.setTextContent(nota_alcsll);
            nota.appendChild(alCsll);

            Element preRazaoSocial = doc.createElement("PRE_RAZAO_SOCIAL");
            preRazaoSocial.setTextContent(nota_razao);
            nota.appendChild(preRazaoSocial);

            Element preNomeFantasia = doc.createElement("PRE_NOME_FANTASIA");
            preNomeFantasia.setTextContent(nota_fanta);
            nota.appendChild(preNomeFantasia);

            Element preEndereco = doc.createElement("PRE_ENDERECO");
            preEndereco.setTextContent(nota_endereco);
            nota.appendChild(preEndereco);

            Element preEnderecoNumero = doc.createElement("PRE_ENDERECO_NUMERO");
            preEnderecoNumero.setTextContent(nota_endnumero);
            nota.appendChild(preEnderecoNumero);

            Element preEnderecoComplemento = doc.createElement("PRE_ENDERECO_COMPLEMENTO");
            preEnderecoComplemento.setTextContent(nota_endcompl);
            nota.appendChild(preEnderecoComplemento);

            Element preEnderecoBairro = doc.createElement("PRE_ENDERECO_BAIRRO");
            preEnderecoBairro.setTextContent(nota_endbairro);
            nota.appendChild(preEnderecoBairro);

            Element preEnderecoUf = doc.createElement("PRE_ENDERECO_UF");
            preEnderecoUf.setTextContent(nota_enduf);
            nota.appendChild(preEnderecoUf);

            Element preEnderecoCep = doc.createElement("PRE_ENDERECO_CEP");
            preEnderecoCep.setTextContent(nota_endcep);
            nota.appendChild(preEnderecoCep);

            Element preEnderecoEsMunicipio = doc.createElement("PRE_ENDERECO_ES_MUNICIPIO");
            preEnderecoEsMunicipio.setTextContent(nota_endcdmun);
            nota.appendChild(preEnderecoEsMunicipio);

            Element preTelefone = doc.createElement("PRE_TELEFONE");
            preTelefone.setTextContent(nota_telefone);
            nota.appendChild(preTelefone);

            Element preEmail = doc.createElement("PRE_EMAIL");
            preEmail.setTextContent(nota_email);
            nota.appendChild(preEmail);

            Element tomCpfCnpj = doc.createElement("TOM_CPF_CNPJ");
            tomCpfCnpj.setTextContent(nota_tomcpfcnpj);
            nota.appendChild(tomCpfCnpj);

            Element tomInscricaoMunicipal = doc.createElement("TOM_INSCRICAO_MUNICIPAL");
            tomInscricaoMunicipal.setTextContent(nota_tominsmun);
            nota.appendChild(tomInscricaoMunicipal);

            Element tomInscricaoEstadual = doc.createElement("TOM_INSCRICAO_ESTADUAL");
            tomInscricaoEstadual.setTextContent(nota_tominsest);
            nota.appendChild(tomInscricaoEstadual);

            Element tomRazaoSocial = doc.createElement("TOM_RAZAO_SOCIAL");
            tomRazaoSocial.setTextContent(nota_tomrazao);
            nota.appendChild(tomRazaoSocial);

            Element tomEndereco = doc.createElement("TOM_ENDERECO");
            tomEndereco.setTextContent(nota_tomend);
            nota.appendChild(tomEndereco);

            Element tomEnderecoNumero = doc.createElement("TOM_ENDERECO_NUMERO");
            tomEnderecoNumero.setTextContent(nota_tomendnum);
            nota.appendChild(tomEnderecoNumero);

            Element tomEnderecoComplemento = doc.createElement("TOM_ENDERECO_COMPLEMENTO");
            tomEnderecoComplemento.setTextContent(nota_tomendcplto);
            nota.appendChild(tomEnderecoComplemento);

            Element tomEnderecoBairro = doc.createElement("TOM_ENDERECO_BAIRRO");
            tomEnderecoBairro.setTextContent(nota_tomendbairro);
            nota.appendChild(tomEnderecoBairro);

            Element tomEnderecoUf = doc.createElement("TOM_ENDERECO_UF");
            tomEnderecoUf.setTextContent(nota_tomenduf);
            nota.appendChild(tomEnderecoUf);

            Element tomEnderecoCep = doc.createElement("TOM_ENDERECO_CEP");
            tomEnderecoCep.setTextContent(nota_tomendcep);
            nota.appendChild(tomEnderecoCep);

            Element tomEnderecoEsMunicipio = doc.createElement("TOM_ENDERECO_ES_MUNICIPIO");
            tomEnderecoEsMunicipio.setTextContent(nota_tomendmun);
            nota.appendChild(tomEnderecoEsMunicipio);

            Element tomTelefone = doc.createElement("TOM_TELEFONE");
            tomTelefone.setTextContent(nota_tomtelefone);
            nota.appendChild(tomTelefone);

            Element tomEmail = doc.createElement("TOM_EMAIL");
            tomEmail.setTextContent(nota_tomemail);
            nota.appendChild(tomEmail);

            Element intermediarioRazaoSocial = doc.createElement("INTERMEDIARIO_RAZAO_SOCIAL");
            intermediarioRazaoSocial.setTextContent(nota_intrazao);
            nota.appendChild(intermediarioRazaoSocial);

            Element intermediarioCpfCnpj = doc.createElement("INTERMEDIARIO_CPF_CNPJ");
            intermediarioCpfCnpj.setTextContent(nota_intcpfcnpj);
            nota.appendChild(intermediarioCpfCnpj);

            Element intermediarioCdCpfCnpjTipo = doc.createElement("INTERMEDIARIO_CD_CPF_CNPJ_TIPO");
            intermediarioCdCpfCnpjTipo.setTextContent(nota_intcdcpfcnpjtipo);
            nota.appendChild(intermediarioCdCpfCnpjTipo);

            Element intermediarioInscricaoMunicipal = doc.createElement("INTERMEDIARIO_INSCRICAO_MUNICIPAL");
            intermediarioInscricaoMunicipal.setTextContent(nota_intincmun);
            nota.appendChild(intermediarioInscricaoMunicipal);

            Element orgaoGeradorEsMunicipio = doc.createElement("ORGAO_GERADOR_ES_MUNICIPIO");
            orgaoGeradorEsMunicipio.setTextContent(nota_orgaogeradorescdmun);
            nota.appendChild(orgaoGeradorEsMunicipio);

            Element orgaoGeradorUf = doc.createElement("ORGAO_GERADOR_UF");
            orgaoGeradorUf.setTextContent(nota_orgaogeradoruf);
            nota.appendChild(orgaoGeradorUf);

            Element construcaoCivilCdObra = doc.createElement("CONSTRUCAO_CIVIL_CD_OBRA");
            construcaoCivilCdObra.setTextContent("");
            nota.appendChild(construcaoCivilCdObra);

            Element construcaoCivilArt = doc.createElement("CONSTRUCAO_CIVIL_ART");
            construcaoCivilArt.setTextContent("");
            nota.appendChild(construcaoCivilArt);

            Element esRps = doc.createElement("ES_RPS");
            esRps.setTextContent(nota_esrps);
            nota.appendChild(esRps);

            Element vlAliquotaAproximada = doc.createElement("VL_ALIQUOTA_APROXIMADA");
            vlAliquotaAproximada.setTextContent("0.0000");
            nota.appendChild(vlAliquotaAproximada);

            Element vlAliquotaAproximadaTotal = doc.createElement("VL_ALIQUOTA_APROXIMADA_TOTAL");
            vlAliquotaAproximadaTotal.setTextContent("0.00");
            nota.appendChild(vlAliquotaAproximadaTotal);

            Element listaItens = doc.createElement("LISTA_ITENS");
            listaItens.setTextContent("");
            nota.appendChild(listaItens);

            // imprime XML to system console
            //writeXml(doc, System.out);

            // write dom document to a file
            //  nome físico do arquivo deve ser
            // CPF/CNPJ_I_(AnoCompetencia)(MesCompetencia)-Lote_DataEmissao.xml
            // pois o mesmo será validado no processo de upload quando da remessa dos dados.
            try (FileOutputStream output = new FileOutputStream("notas\\" + nota_cnpj + "_I_" + Dates.DateFormata("yyyyMM", new Date()) + "-" + lote + "_" + Dates.DateFormata("ddMMyyyy", new Date()) +".xml")) {
                writeXml(doc, output);
            } catch (IOException e) {}
        } catch (ParserConfigurationException | TransformerException pce) {}
    }

    public void nfse(String nrlote) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("NFSE");
            doc.appendChild(rootElement);

            Element identificacao = doc.createElement("IDENTIFICACAO");
            rootElement.appendChild(identificacao);

            Element mescomp = doc.createElement("MESCOMP");
            mescomp.setTextContent("12");
            identificacao.appendChild(mescomp);

            Element anocomp = doc.createElement("ANOCOMP");
            anocomp.setTextContent("2021");
            identificacao.appendChild(anocomp);

            Element inscmob = doc.createElement("INSCRICAO");
            inscmob.setTextContent(nota_inscmun);
            identificacao.appendChild(inscmob);

            Element versao = doc.createElement("VERSAO");
            versao.setTextContent("4.0");
            identificacao.appendChild(versao);

            //Document doc = docBuilder.newDocument();
            //Element notas = doc.createElement("NOTAS");
            //doc.appendChild(notas);

            //Element versao = doc.createElement("VERSAO");
            //versao.setTextContent("1.02");
            //notas.appendChild(versao);

            Element nota = doc.createElement("NOTA");
            nota.appendChild(nota);

            Element lote = doc.createElement("LOTE");
            lote.setTextContent(nrlote);
            nota.appendChild(lote);

            Element sequencia = doc.createElement("SEQUENCIA");
            sequencia.setTextContent("123...89");
            nota.appendChild(sequencia);

            Element dtemissao = doc.createElement("DATAEMISSAO");
            dtemissao.setTextContent("dd/mm/yyyy");
            nota.appendChild(dtemissao);

            Element hremissao = doc.createElement("HORAEMISSAO");
            hremissao.setTextContent("HH:MM:SS");
            nota.appendChild(hremissao);

            Element local = doc.createElement("LOCAL");
            local.setTextContent("D|F");
            nota.appendChild(local);

            // Caso local seja F
            Element uffora = doc.createElement("UFFORA");
            uffora.setTextContent("RJ");
            nota.appendChild(uffora);

            // Caso local seja F
            Element municipiofora = doc.createElement("MUNICIPIOFORA");
            municipiofora.setTextContent("123456789");
            nota.appendChild(municipiofora);

            // Caso local seja F - Nao Obrigatorio
            Element paisfora = doc.createElement("PAISFORA");
            paisfora.setTextContent("US");
            nota.appendChild(paisfora);

            Element situacao = doc.createElement("SITUACAO");
            situacao.setTextContent("1..34");
            nota.appendChild(situacao);

            Element retido = doc.createElement("RETIDO");
            retido.setTextContent("S|N");
            nota.appendChild(retido);

            Element atividade = doc.createElement("ATIVIDADE");
            atividade.setTextContent("XXX0000000");
            nota.appendChild(atividade);

            Element aliquotaaplicada = doc.createElement("ALIQUOTAAPLICADA");
            aliquotaaplicada.setTextContent("5.0");
            nota.appendChild(aliquotaaplicada);

            Element deducao = doc.createElement("DEDUCAO");
            deducao.setTextContent("0.0");
            nota.appendChild(deducao);

            Element imposto = doc.createElement("IMPOSTO");
            imposto.setTextContent("5.0");
            nota.appendChild(imposto);

            Element retencao = doc.createElement("RETENCAO");
            retencao.setTextContent("5.0");
            nota.appendChild(retencao);

            // Nao Obrigatorio
            Element observacao = doc.createElement("OBSERVACAO");
            observacao.setTextContent("123...XXX...1000");
            nota.appendChild(observacao);

            Element cpfcnpjTomador = doc.createElement("CPFCNPJ");
            cpfcnpjTomador.setTextContent("23.456.789/0000-00");
            nota.appendChild(cpfcnpjTomador);

            Element rgieTomador = doc.createElement("RGIE");
            rgieTomador.setTextContent("123...15");
            nota.appendChild(rgieTomador);

            Element nomerazaoTomador = doc.createElement("NOMERAZAO");
            nomerazaoTomador.setTextContent("ABC...");
            nota.appendChild(nomerazaoTomador);

            Element nomefantasiaTomador = doc.createElement("NOMEFANTASIA");
            nomefantasiaTomador.setTextContent("ABC...");
            nota.appendChild(nomefantasiaTomador);

            // Nao Obrigatorio
            Element infcomplementarTomador = doc.createElement("INFCOMPLEMENTAR");
            infcomplementarTomador.setTextContent("ABC...");
            nota.appendChild(infcomplementarTomador);

            Element municipioTomador = doc.createElement("MUNICIPIO");
            municipioTomador.setTextContent("0000");
            nota.appendChild(municipioTomador);

            Element bairroTomador = doc.createElement("BAIRRO");
            bairroTomador.setTextContent("NOME DO BAIRRO");
            nota.appendChild(bairroTomador);

            Element cepTomador = doc.createElement("CEP");
            cepTomador.setTextContent("12345678");
            nota.appendChild(cepTomador);

            Element prefixoTomador = doc.createElement("PREFIXO");
            prefixoTomador.setTextContent("PREFIXO");
            nota.appendChild(prefixoTomador);

            Element logradouroTomador = doc.createElement("LOGRADOURO");
            logradouroTomador.setTextContent("PREFIXO");
            nota.appendChild(logradouroTomador);

            // Obrigatorio se local for F
            Element complementoTomador = doc.createElement("COMPLEMENTO");
            complementoTomador.setTextContent("COMPLEMENTO");
            nota.appendChild(complementoTomador);

            Element numeroTomador = doc.createElement("NUMERO");
            numeroTomador.setTextContent("0000");
            nota.appendChild(numeroTomador);

            Element emailTomador = doc.createElement("EMAIL");
            emailTomador.setTextContent("nome@servidor.com[.br]");
            nota.appendChild(emailTomador);

            Element dentropaisTomador = doc.createElement("DENTROPAIS");
            dentropaisTomador.setTextContent("S|N");
            nota.appendChild(dentropaisTomador);

            // Data para pagamento do serviço
            Element datavencimento = doc.createElement("DATAVENCIMENTO");
            datavencimento.setTextContent("dd/mm/yyyy");
            nota.appendChild(datavencimento);

            Element pis = doc.createElement("PIS");
            pis.setTextContent("5.0");
            nota.appendChild(pis);

            Element cofins = doc.createElement("COFINS");
            cofins.setTextContent("5.0");
            nota.appendChild(cofins);

            Element inss = doc.createElement("INSS");
            inss.setTextContent("5.0");
            nota.appendChild(inss);

            Element ir = doc.createElement("IR");
            ir.setTextContent("5.0");
            nota.appendChild(ir);

            Element csll = doc.createElement("CSLL");
            csll.setTextContent("5.0");
            nota.appendChild(csll);

            Element icms = doc.createElement("ICMS");
            icms.setTextContent("5.0");
            nota.appendChild(icms);

            Element ipi = doc.createElement("IPI");
            ipi.setTextContent("5.0");
            nota.appendChild(ipi);

            Element iof = doc.createElement("IOF");
            iof.setTextContent("5.0");
            nota.appendChild(iof);

            Element cide = doc.createElement("CIDE");
            cide.setTextContent("5.0");
            nota.appendChild(cide);

            Element outrostributos = doc.createElement("OUTROSTRIBUTOS");
            outrostributos.setTextContent("5.0");
            nota.appendChild(outrostributos);

            Element servicos = doc.createElement("SERVICOS");
            nota.appendChild(servicos);

            Element servico = doc.createElement("SERVICO");
            servicos.appendChild(servico);

            Element descricao = doc.createElement("DESCRICAO");
            descricao.setTextContent("DESCRICAO DO SERVICO");
            servico.appendChild(descricao);

            Element valorunit = doc.createElement("VALORUNIT");
            valorunit.setTextContent("10.0000");
            servico.appendChild(valorunit);

            Element quantidade = doc.createElement("QUANTIDADE");
            quantidade.setTextContent("10.00");
            servico.appendChild(quantidade);

            Element desconto = doc.createElement("DESCONTO");
            desconto.setTextContent("10.00");
            servico.appendChild(desconto);

            // imprime XML to system console
            //writeXml(doc, System.out);

            // write dom document to a file
            //  nome físico do arquivo deve ser
            // CPF/CNPJ_I_(AnoCompetencia)(MesCompetencia)-Lote_DataEmissao.xml
            // pois o mesmo será validado no processo de upload quando da remessa dos dados.
            try (FileOutputStream output = new FileOutputStream("notas\\" + nota_cnpj + "_I_" + Dates.DateFormata("yyyyMM", new Date()) + "-" + lote + "_" + Dates.DateFormata("ddMMyyyy", new Date()) +".xml")) {
                writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        // hide the xml declaration
        // hide <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        // set xml encoding
        // <?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");

        // hide the standalone="no"
        doc.setXmlStandalone(true);

        // set xml version
        // <?xml version="1.1" encoding="ISO-8859-1" standalone="no"?>
        doc.setXmlVersion("1.0");

        transformer.transform(source, result);
    }
}
