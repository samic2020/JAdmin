package Email;

import Funcoes.Outlook;

import java.util.ArrayList;
import java.util.List;

public class SendEmail {
    public SendEmail() { }

    public boolean sendMsg(String recipient, String subject, String content, String[] anexos) throws Exception {
        boolean retorno = false;
        
        List<String> address = new ArrayList<String>();
        if (recipient.contains(";") || recipient.contains(",")) {
            String sSeparator = "";
            if (recipient.contains(";")) { sSeparator = ";"; }
            if (recipient.contains(",")) { sSeparator = ","; }
            
            String[] aLista = recipient.split(sSeparator);
            for (String item : aLista) address.add(item.trim());            
        } else {
            address.add(recipient);
        }
       
        Outlook email = new Outlook(true);
        try {            
            String To = String.join(";", address);
            String Subject = subject.trim();
            String Body = content;
            String[] Attachments = anexos;
            email.Send(To, null, Subject, Body, Attachments);
            retorno = email.isSend();
        } catch (Exception ex) {
            retorno = false;
            ex.printStackTrace();
        } finally {
            email = null;
        }                                
        
        return retorno;
    }
}
