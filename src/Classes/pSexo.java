/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class pSexo extends ComboBox<String> {
    public ObservableList<String> Sexo() {
        List<String> list = new ArrayList<>();
        list.add("M"); list.add("F"); list.add("O");
        ObservableList<String> observableList = observableList(list);
        return observableList;
    }
}
