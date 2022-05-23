package Imoveis.Fotos;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Funcoes.tempFile;
import es.carlosmontero.javafx.gallery.Gallery;
import es.carlosmontero.javafx.gallery.GalleryAddEvent;
import es.carlosmontero.javafx.gallery.PhotoGallery;
import es.carlosmontero.javafx.gallery.ProgramEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class fotosImovel extends Application {
    DbMain conn = VariaveisGlobais.conexao;
    private Gallery gallery = null;
    private String tmpFileName = new tempFile("png").getsPathNameExt();
    private String tmpDir = new tempFile().getTempPath();
    private final List<PhotoGallery> photos = new ArrayList();
    private String titulo = "";
    private String rgimv = "";

    public void setTitulo(String Titulo) {this.titulo = Titulo;}
    public void setRgimv(String RgImv) {this.rgimv = RgImv; }

    @Override
    public void start(final Stage stage) throws Exception {
        new File(tmpDir).deleteOnExit();

        final String[] urls = ListaFotosImovel(this.rgimv);
        for (String ph : urls) {
            PhotoGallery pg = new PhotoGallery(ph);
            photos.add(pg);
        }

        final HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: grey");
        hBox.setAlignment(Pos.CENTER);

        final StackPane root = new StackPane();
        root.getChildren().add(hBox);
        final Scene scene = new Scene(root);

        Platform.runLater(() -> {
            gallery = new Gallery(stage, photos, 0.5);
            gallery.show();
            setupGestureTarget(scene, gallery);
        });

        stage.addEventHandler(ProgramEvent.GET_ACTION, event -> {
            Platform.runLater(() -> PhotoManager(event.action, event.photoUrl));
        });

        stage.setOnCloseRequest(event -> Platform.runLater(() -> {
            /***
             * Deleta Pasta e seus arquivos
             */
            try {
                File isDir = new File(tmpDir);
                if (isDir.exists()) {
                    File[] arquivos = isDir.listFiles();
                    for (File var : arquivos) {var.delete();}
                    isDir.delete();
                }

                if (new File(tmpDir).exists()) {
                    File[] arquivos = new File(tmpDir).listFiles();
                    for (File var : arquivos) { if (var.getName().toUpperCase().contains("PNG")) var.delete(); }
                }
            } catch (Exception e) {e.printStackTrace();}
        }));

        stage.setScene(scene);
        stage.setHeight(600);
        stage.setWidth(600);
        stage.setTitle(this.titulo);
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

    void setupGestureTarget(final Scene target, Gallery gallery){

        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        });

        target.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                try {
                    Dragboard db = event.getDragboard();

                    if (db.hasFiles()) {
                        for (File file : db.getFiles()) {
                            if (file.getName().toUpperCase().contains("JPG") || file.getName().toUpperCase().contains("PNG")) {
                                String absolutePath = file.getAbsolutePath();
                                String foto = absolutePath;
                                PhotoGallery pg = new PhotoGallery(foto);
                                gallery.fireEvent(new GalleryAddEvent(pg, GalleryAddEvent.GET_ADDEVENT));
                            }
                        }
                        event.setDropCompleted(true);
                    } else if (db.hasString()) {
                        if (db.getString().toUpperCase().contains("JPG") || db.getString().toUpperCase().contains("PNG")) {
                            String foto = WriteImageUrl(db.getString());
                            System.out.println(foto);
                            PhotoGallery pg = new PhotoGallery(foto);
                            gallery.fireEvent(new GalleryAddEvent(pg, GalleryAddEvent.GET_ADDEVENT));
                        }
                    } else {
                        event.setDropCompleted(false);
                    }

                    event.consume();
                } catch (Exception e) {e.printStackTrace();}
            }
        });
    }

    private void PhotoManager(String action, String photoUrl) {
        if (action.equalsIgnoreCase("ADD")) {
            //System.out.println("ADD " + photoUrl);
            String wimage = WriteImage(photoUrl);
            if (wimage != null) salvarFotoImovel(wimage, this.rgimv);
            if (wimage != null) try {new File(wimage).delete();} catch (Exception e) {}
        } else if (action.equalsIgnoreCase("DEL")) {
            //System.out.println("DEL " + photoUrl);
            if (deletaFotoImovel(photoUrl, this.rgimv)) {
                try {new File(photoUrl).delete();} catch (Exception e) {}
            }
        }
    }

    private String WriteImage(String photoUrl) {
        String imgRet = null;
        try {
            File foto = new File(photoUrl);
            Image fxImage = new Image(foto.toURI().toString());
            BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);
            File imgtmp = new File(tmpFileName);
            ImageIO.write(image, "PNG", imgtmp);

            imgRet = tmpFileName;
        } catch (Exception e) {e.printStackTrace();}

        return imgRet;
    }

    private String WriteImageUrl(String photoUrl) {
        String imgRet = null;
        try {
            Image fxImage = new Image(photoUrl);
            BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);
            File imgtmp = new File(tmpFileName);
            ImageIO.write(image, "PNG", imgtmp);

            imgRet = tmpFileName;
        } catch (Exception e) {e.printStackTrace();}

        return imgRet;
    }

    public boolean salvarFotoImovel(String photoUrl, String rgimv) {
        boolean isSafet = true;
        String insertRow = "INSERT INTO fotosimovel (rgimv, foto, nome) VALUES (?,?,?);";
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(insertRow);
            File imagemFile = new File(photoUrl);
            byte[] imagemArray = new byte[(int) imagemFile.length()];
            DataInputStream imagemStream = new DataInputStream(new FileInputStream(imagemFile));
            imagemStream.readFully(imagemArray);
            imagemStream.close();
            pstmt.setString(1, rgimv);
            pstmt.setBytes(2,imagemArray);
            pstmt.setString(3, imagemFile.getName());
            pstmt.executeUpdate();
        } catch (SQLException | IOException ee) {
            ee.printStackTrace();
            isSafet = false;
        }
        return isSafet;
    }

    public boolean deletaFotoImovel(String photoUrl, String rgimv) {
        boolean isSafet = true;
        String insertRow = "DELETE FROM fotosimovel WHERE nome = ? AND rgimv = ?";
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(insertRow);
            File imagemFile = new File(photoUrl);
            pstmt.setString(1, imagemFile.getName());
            pstmt.setString(2,rgimv);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            isSafet = false;
        }
        return isSafet;
    }

    public String[] ListaFotosImovel(String rgimv) {
        String[] photos = {};
        byte[] imageByte = null;
        Image image = null;
        String sql = "SELECT foto, nome FROM fotosimovel WHERE rgimv = '" + rgimv + "';";
        try {
            Statement stm = conn.conn.createStatement();
            ResultSet recs = stm.executeQuery(sql);

            while (recs.next()) {
                imageByte = recs.getBytes("foto");
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageByte));
                ImageIO.write(bi,"PNG",new File(tmpDir + recs.getString("nome").trim()));
                photos = FuncoesGlobais.ArrayAdd(photos,tmpDir + recs.getString("nome").trim());
            }
        } catch (SQLException | IOException e) {}
        return photos;
    }
}
