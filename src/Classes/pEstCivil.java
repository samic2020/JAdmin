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
public class pEstCivil {
    public ObservableList<String> EstCivil() {
        List<String> list = new ArrayList<>();
        list.add("Solteiro"); list.add("Casado"); list.add("União Estavel");
        list.add("Separado"); list.add("Divorciado"); list.add("Desquitado");
        list.add("Viuvo");
        ObservableList<String> observableList = observableList(list);
        return observableList;
    }
}
