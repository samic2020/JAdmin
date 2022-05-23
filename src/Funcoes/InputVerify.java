/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author supervisor
 */
public class InputVerify {
    public boolean InputVerify(KeyEvent event) {
        Object[] Letras = new Object[] {KeyCode.A,
                                        KeyCode.B,
                                        KeyCode.C,
                                        KeyCode.D,
                                        KeyCode.E,
                                        KeyCode.F,
                                        KeyCode.G,
                                        KeyCode.H,
                                        KeyCode.I,
                                        KeyCode.J,
                                        KeyCode.K,
                                        KeyCode.L,
                                        KeyCode.M,
                                        KeyCode.N,
                                        KeyCode.O,
                                        KeyCode.P,
                                        KeyCode.Q,
                                        KeyCode.R,
                                        KeyCode.S,
                                        KeyCode.T,
                                        KeyCode.U,
                                        KeyCode.V,
                                        KeyCode.W,
                                        KeyCode.X,
                                        KeyCode.Y,
                                        KeyCode.Z};
        Object[] Numeros = new Object[] {KeyCode.DIGIT0,
                                         KeyCode.DIGIT1,
                                         KeyCode.DIGIT2,
                                         KeyCode.DIGIT3,
                                         KeyCode.DIGIT4,
                                         KeyCode.DIGIT5,
                                         KeyCode.DIGIT6,
                                         KeyCode.DIGIT7,
                                         KeyCode.DIGIT8,
                                         KeyCode.DIGIT9};
        
        int pos = FuncoesGlobais.ObjIndexOf(Letras, event.getCode());
        int pos1 = FuncoesGlobais.ObjIndexOf(Numeros, event.getCode());
        return pos > -1 || pos1 > -1;
    }
}
