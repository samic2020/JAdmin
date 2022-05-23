/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 *
 * @author supervisor
 */
public class Imagens {
    public Imagens() {}
    
    public Image Byte2Imagens(byte[] byteImage) {
        Image icon = null;
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(byteImage));
            icon = SwingFXUtils.toFXImage(bi, null);
        } catch (Exception e) {}
        return icon;
    }    
}
