/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.scene.control.ComboBox;

/**
 *
 * @author supervisor
 */
public class getEmails {
    String memail = "";
    public getEmails(ComboBox<pemailModel> email, boolean comsenha) {
        for (pemailModel semail : email.getItems()) {
            if (comsenha) {
                memail += semail.getEmail() + "," + semail.getSenha() + ";";
            } else {
                memail += semail.getEmail() +  ";";
            }
        }
    }
    
    public String toString() {
        return (memail.length() > 0 ? memail.substring(0, memail.length() - 1) : "");        
    }
}
