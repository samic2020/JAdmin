/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes.Email;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Email {
    String smtp;
    int port;
    String from;
    String pawd;
    boolean auth;
    EmailTo to;
    String subject;
    String body;
    EmailAttachments attachments;

    public String getSmtp() { return smtp; }
    @XmlElement
    public void setSmtp(String smtp) { this.smtp = smtp; }
    
    public int getPort() { return port; }
    @XmlElement
    public void setPort(int port) { this.port = port; }
    
    public String getFrom() { return from; }
    @XmlElement
    public void setFrom(String from) { this.from = from; }

    public String getPawd() { return pawd; }
    @XmlElement
    public void setPawd(String pawd) { this.pawd = pawd; }

    public boolean getAuth() { return auth; }
    @XmlElement
    public void setAuth(boolean auth) { this.auth = auth; }

    public EmailTo getTo() { return to; }
    @XmlElement
    public void setTo(EmailTo to) { this.to = to; }
    
    public String getSubject() { return subject; }
    @XmlElement
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    @XmlElement
    public void setBody(String body) { this.body = body; }

    public EmailAttachments getAttachments() { return attachments; }
    @XmlElement
    public void setAttachments(EmailAttachments attachments) { this.attachments = attachments; }
}