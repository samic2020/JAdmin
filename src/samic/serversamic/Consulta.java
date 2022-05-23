
package samic.serversamic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de consulta complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteudo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="consulta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cpfcnpj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datacadastro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datanasc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endereco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nomerazao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="observacoes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="positivo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="rginsc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefones" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consulta", propOrder = {
    "bairro",
    "cep",
    "cidade",
    "cliente",
    "complemento",
    "cpfcnpj",
    "datacadastro",
    "datanasc",
    "emails",
    "endereco",
    "estacao",
    "estado",
    "id",
    "nomerazao",
    "numero",
    "observacoes",
    "positivo",
    "rginsc",
    "telefones",
    "tipo"
})
public class Consulta {

    protected String bairro;
    protected String cep;
    protected String cidade;
    protected String cliente;
    protected String complemento;
    protected String cpfcnpj;
    protected String datacadastro;
    protected String datanasc;
    protected String emails;
    protected String endereco;
    protected String estacao;
    protected String estado;
    protected int id;
    protected String nomerazao;
    protected String numero;
    protected String observacoes;
    protected boolean positivo;
    protected String rginsc;
    protected String telefones;
    protected String tipo;

    /**
     * Obtem o valor da propriedade bairro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBairro() {
        return bairro;
    }

    /**
     * Define o valor da propriedade bairro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBairro(String value) {
        this.bairro = value;
    }

    /**
     * Obtem o valor da propriedade cep.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCep(String value) {
        this.cep = value;
    }

    /**
     * Obtem o valor da propriedade cidade.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidade() {
        return cidade;
    }

    /**
     * Define o valor da propriedade cidade.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidade(String value) {
        this.cidade = value;
    }

    /**
     * Obtem o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCliente(String value) {
        this.cliente = value;
    }

    /**
     * Obtem o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplemento(String value) {
        this.complemento = value;
    }

    /**
     * Obtem o valor da propriedade cpfcnpj.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpfcnpj() {
        return cpfcnpj;
    }

    /**
     * Define o valor da propriedade cpfcnpj.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpfcnpj(String value) {
        this.cpfcnpj = value;
    }

    /**
     * Obtem o valor da propriedade datacadastro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatacadastro() {
        return datacadastro;
    }

    /**
     * Define o valor da propriedade datacadastro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatacadastro(String value) {
        this.datacadastro = value;
    }

    /**
     * Obtem o valor da propriedade datanasc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatanasc() {
        return datanasc;
    }

    /**
     * Define o valor da propriedade datanasc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatanasc(String value) {
        this.datanasc = value;
    }

    /**
     * Obtem o valor da propriedade emails.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmails() {
        return emails;
    }

    /**
     * Define o valor da propriedade emails.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmails(String value) {
        this.emails = value;
    }

    /**
     * Obtem o valor da propriedade endereco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Define o valor da propriedade endereco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndereco(String value) {
        this.endereco = value;
    }

    /**
     * Obtem o valor da propriedade estacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstacao() {
        return estacao;
    }

    /**
     * Define o valor da propriedade estacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstacao(String value) {
        this.estacao = value;
    }

    /**
     * Obtem o valor da propriedade estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define o valor da propriedade estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Obtem o valor da propriedade id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define o valor da propriedade id.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Obtem o valor da propriedade nomerazao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomerazao() {
        return nomerazao;
    }

    /**
     * Define o valor da propriedade nomerazao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomerazao(String value) {
        this.nomerazao = value;
    }

    /**
     * Obtem o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Obtem o valor da propriedade observacoes.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacoes() {
        return observacoes;
    }

    /**
     * Define o valor da propriedade observacoes.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacoes(String value) {
        this.observacoes = value;
    }

    /**
     * Obtem o valor da propriedade positivo.
     * 
     */
    public boolean isPositivo() {
        return positivo;
    }

    /**
     * Define o valor da propriedade positivo.
     * 
     */
    public void setPositivo(boolean value) {
        this.positivo = value;
    }

    /**
     * Obtem o valor da propriedade rginsc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRginsc() {
        return rginsc;
    }

    /**
     * Define o valor da propriedade rginsc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRginsc(String value) {
        this.rginsc = value;
    }

    /**
     * Obtem o valor da propriedade telefones.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefones() {
        return telefones;
    }

    /**
     * Define o valor da propriedade telefones.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefones(String value) {
        this.telefones = value;
    }

    /**
     * Obtem o valor da propriedade tipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define o valor da propriedade tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

}
