/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes.Email;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmailAttachments {
    List<String> attachments;

    public List<String> getAttachments() { return attachments; }
    
    @XmlElement(name="attachment")
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }      
}