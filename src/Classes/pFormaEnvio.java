/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class pFormaEnvio {
    public ObservableList<String> FormaEnvio() {
        List<String> list = new ArrayList<>();
        list.add("Em Mãos"); list.add("Correio Eletrônico"); list.add("Correspondência");
        ObservableList<String> observableList = observableList(list);
        return observableList;
    }
}
