package Objetos.JasperViewerFX;

import Classes.jExtrato;
import Funcoes.toPrint;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pdfViewer.PdfViewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class JasperViewerFX extends Pane {
    private Button btnPrint;
    private Button btnSave;
    private Button btnBackPage;
    private Button btnFirstPage;
    private Button btnNextPage;
    private Button btnLastPage;
    private Button btnZoomIn;
    private Button btnZoomOut;
    private ImageView report;
    private Label lblReportPages;
    private Stage view;
    private TextField txtPage;

    private JasperPrint jasperPrint;

    private SimpleIntegerProperty currentPage;
    private int imageHeight = 0;
    private int imageWidth = 0;
    private int reportPages = 0;

    private int Height = 0;
    private int Width = 0;

    private List<jExtrato> lista;
    private Map parametros;

    public JasperViewerFX(int iWidth, int iHeight, String menuStyle) {
        Height = iHeight;
        Width = iWidth;
        getChildren().add(createContentPane(menuStyle));
        currentPage = new SimpleIntegerProperty(this, "currentPage", 1);
    }

    public JasperViewerFX(int iWidth, int iHeight, String menuStyle, List<jExtrato> ilista, Map iparametros) {
        Height = iHeight;
        Width = iWidth;
        lista = ilista;
        parametros = iparametros;

        getChildren().add(createContentPane(menuStyle));
        currentPage = new SimpleIntegerProperty(this, "currentPage", 1);
    }

    private BorderPane createContentPane(String menuStyle) {
        btnPrint = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/printer.png"))));
        btnSave = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/save.png"))));
        btnBackPage = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/backimg.png"))));
        btnFirstPage = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/firstimg.png"))));
        btnNextPage = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/nextimg.png"))));
        btnLastPage = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/lastimg.png"))));
        btnZoomIn = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/zoomin.png"))));
        btnZoomOut = new Button(null, new ImageView(new Image(getClass().getResourceAsStream("/Figuras/JasperViewerFX/zoomout.png"))));

        btnPrint.setPrefSize(30, 30);
        btnSave.setPrefSize(30, 30);
        btnBackPage.setPrefSize(30, 30);
        btnFirstPage.setPrefSize(30, 30);
        btnNextPage.setPrefSize(30, 30);
        btnLastPage.setPrefSize(30, 30);
        btnZoomIn.setPrefSize(30, 30);
        btnZoomOut.setPrefSize(30, 30);

        btnBackPage.setOnAction(event -> renderPage(getCurrentPage() - 1));
        btnFirstPage.setOnAction(event -> renderPage(1));
        btnNextPage.setOnAction(event -> renderPage(getCurrentPage() + 1));
        btnLastPage.setOnAction(event -> renderPage(reportPages));
        btnZoomIn.setOnAction(event -> zoom(0.15));
        btnZoomOut.setOnAction(event -> zoom(-0.15));

        printAction();
        saveAction();

        txtPage = new TextField("1");
        txtPage.setPrefSize(40, 30);
        txtPage.setOnAction(event -> {
            try {
                int page = Integer.parseInt(txtPage.getText());
                renderPage(((page > 0 && page <= reportPages) ? page : 1));
            } catch (NumberFormatException e) {
                renderPage(1);
            }
        });

        lblReportPages = new Label("/ 1");

        HBox menu = new HBox(5);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(5));
        menu.setPrefHeight(50.0);
        menu.getChildren().addAll(btnPrint, btnSave, btnFirstPage, btnBackPage, txtPage,
                lblReportPages, btnNextPage, btnLastPage, btnZoomIn, btnZoomOut);

        // Border
        if (menuStyle != null) {
            menu.setStyle(menuStyle);
        }

        // This imageview will preview the pdf inside scrollpane
        report = new ImageView();
        report.setFitHeight(imageHeight);
        report.setFitWidth(imageWidth);

        // Centralizing the ImageView on Scrollpane
        Group contentGroup = new Group();
        contentGroup.getChildren().add(report);

        StackPane stack = new StackPane(contentGroup);
        stack.setAlignment(Pos.CENTER);
        stack.setStyle("-fx-background-color: gray");

        ScrollPane scroll = new ScrollPane(stack);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        BorderPane root = new BorderPane();
        root.setTop(menu);
        root.setCenter(scroll);
        root.setPrefSize(this.Width, this.Height);

        return root;
    }

    public void setCurrentPage(int pageNumber) {
        currentPage.set(pageNumber);
    }

    public int getCurrentPage() {
        return currentPage.get();
    }

    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }

    private void printAction() {
        btnPrint.setOnAction(event -> {
            String fileName = jasperPrint.getName();
            String pdfName = new PdfViewer().GeraPDFTemp(lista,fileName, parametros);
            new toPrint(pdfName,"LASER","INTERNA");

/*
            try {
                JasperPrintManager.printReport(jasperPrint, true);
            } catch (JRException ex) {
                ex.printStackTrace();
            }
*/
        });
    }

    private void saveAction() {
        btnSave.setOnAction(event -> {
            FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("Portable Document Format", "*.pdf");
            FileChooser.ExtensionFilter html = new FileChooser.ExtensionFilter("HyperText Markup Language", "*.html");
            FileChooser.ExtensionFilter xml = new FileChooser.ExtensionFilter("eXtensible Markup Language", "*.xml");
            FileChooser.ExtensionFilter xls = new FileChooser.ExtensionFilter("Microsoft Excel 2007", "*.xls");
            FileChooser.ExtensionFilter xlsx = new FileChooser.ExtensionFilter("Microsoft Excel 2016", "*.xlsx");

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save As");
            chooser.getExtensionFilters().addAll(pdf, html, xml, xls, xlsx);
            chooser.setSelectedExtensionFilter(pdf);

            File file = chooser.showSaveDialog(view);

            if (file != null) {
                List<String> selectedExtension = chooser.getSelectedExtensionFilter().getExtensions();
                exportTo(file, selectedExtension.get(0));
            }
        });
    }

    private void disableUnnecessaryButtons(int pageNumber) {
        boolean isFirstPage = (pageNumber == 1);
        boolean isLastPage = (pageNumber == reportPages);

        btnBackPage.setDisable(isFirstPage);
        btnFirstPage.setDisable(isFirstPage);
        btnNextPage.setDisable(isLastPage);
        btnLastPage.setDisable(isLastPage);
    }

    private void exportTo(File file, String extension) {
        switch (extension) {
            case "*.pdf":
                exportToPdf(file);
                break;
            case "*.html":
                exportToHtml(file);
                break;
            case "*.xml":
                exportToXml(file);
                break;
            case "*.xls":
                exportToXls(file);
                break;
            case "*.xlsx":
                exportToXlsx(file);
                break;
            default:
                exportToPdf(file);
        }
    }

    public void exportToHtml(File file) {
        try {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getPath());
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    public void exportToPdf(File file) {
        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, file.getPath());
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    public void exportToXls(File file) {
        try {
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
            exporter.exportReport();
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    public void exportToXlsx(File file) {
        try {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
            exporter.exportReport();
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    public void exportToXml(File file) {
        try {
            JasperExportManager.exportReportToXmlFile(jasperPrint, file.getPath(), false);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    private Image pageToImage(int pageNumber) {
        try {
            float zoom = (float) 1.33;
            BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber - 1, zoom);
            WritableImage fxImage = new WritableImage(imageHeight, imageWidth);

            return SwingFXUtils.toFXImage(image, fxImage);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void renderPage(int pageNumber) {
        setCurrentPage(pageNumber);
        disableUnnecessaryButtons(pageNumber);
        txtPage.setText(Integer.toString(pageNumber));
        report.setImage(pageToImage(pageNumber));
    }

    public void zoom(double factor) {
        report.setScaleX(report.getScaleX() + factor);
        report.setScaleY(report.getScaleY() + factor);
        report.setFitHeight(imageHeight + factor);
        report.setFitWidth(imageWidth + factor);
    }

    public void viewReport(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;

        imageHeight = jasperPrint.getPageHeight() + 284;
        imageWidth = jasperPrint.getPageWidth() + 201;
        reportPages = jasperPrint.getPages().size();
        lblReportPages.setText("/ " + reportPages);

        if(reportPages > 0) {
            renderPage(1);
        }
    }
}
