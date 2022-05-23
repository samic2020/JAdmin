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
public class EmailTo {
    List<String> to;

    public List<String> getTo() { return to; }
    @XmlElement(name="address")
    public void setTo(List<String> to) { this.to = to; }
}
