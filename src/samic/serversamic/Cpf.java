
package samic.serversamic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de cpf complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteudo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="cpf">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="data_nascimento" type="{http://SamicServer/}date" minOccurs="0"/>
 *         &lt;element name="nome_da_pf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="situacao_cadastral" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpf", propOrder = {
    "cpf",
    "dataNascimento",
    "nomeDaPf",
    "situacaoCadastral"
})
public class Cpf {

    protected String cpf;
    @XmlElement(name = "data_nascimento")
    protected Date dataNascimento;
    @XmlElement(name = "nome_da_pf")
    protected String nomeDaPf;
    @XmlElement(name = "situacao_cadastral")
    protected String situacaoCadastral;

    /**
     * Obtem o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpf(String value) {
        this.cpf = value;
    }

    /**
     * Obtem o valor da propriedade dataNascimento.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define o valor da propriedade dataNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setDataNascimento(Date value) {
        this.dataNascimento = value;
    }

    /**
     * Obtem o valor da propriedade nomeDaPf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeDaPf() {
        return nomeDaPf;
    }

    /**
     * Define o valor da propriedade nomeDaPf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeDaPf(String value) {
        this.nomeDaPf = value;
    }

    /**
     * Obtem o valor da propriedade situacaoCadastral.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    /**
     * Define o valor da propriedade situacaoCadastral.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSituacaoCadastral(String value) {
        this.situacaoCadastral = value;
    }

}
