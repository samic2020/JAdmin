package pdfViewer;

import Funcoes.tempFile;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PdfViewer implements Initializable {
    private Controller controller;
    private String file = null;
    private String title = null;

    public void setTitle(String title) { this.title = title; }

    public PdfViewer() {}

    public PdfViewer(String title, String file) {
        this.title = title;
        this.file = file;

        initialize(null, null);
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            final FXMLLoader parent = new FXMLLoader(getClass().getResource("/pdfViewer/PdfViewer.fxml"));
            Pane root = (Pane) parent.load();
            controller = (Controller) parent.getController();
            controller.title = this.title;
            controller.OpenPdf(file);
            Stage stage = new Stage();

            int y;
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            y = (int) d.getHeight() - 100;

            stage.setScene(new Scene(root, 600, y));
            stage.show();
        } catch (IOException e) {e.printStackTrace();}
    }

    public String GeraPDFTemp(List<?> lista, String reportName) {
        String outFileName = new tempFile("pdf").getsPathNameExt();
        JasperPrint jasperPrint = null;
        try {
            JRDataSource jrds = new JRBeanCollectionDataSource(lista);

            String reportFileName = "Reports/" + reportName + ".jasper";
            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
            jasperPrint = JasperFillManager.fillReport(reporte, null, jrds);

            JRPdfExporter exporter = new JRPdfExporter();

            try {
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new FileOutputStream(outFileName)));
                //SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                //exporter.setConfiguration(configuration);

                exporter.exportReport();
            } catch (Exception jex) {jex.printStackTrace();}
        } catch (JRException e) {
            e.printStackTrace();
        }
        return outFileName;
    }

    public String GeraPDFTemp(List<?> lista, String reportName, Map parametros) {
        String outFileName = new tempFile("pdf").getsPathNameExt();
        JasperPrint jasperPrint = null;
        try {
            JRDataSource jrds = new JRBeanCollectionDataSource(lista);

            String reportFileName = "Reports/" + reportName + ".jasper";
            if (new File(reportFileName).exists()) {
                JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
                jasperPrint = JasperFillManager.fillReport(reporte, parametros, jrds);

                JRPdfExporter exporter = new JRPdfExporter();
                try {
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new FileOutputStream(outFileName)));
                    //SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                    //exporter.setConfiguration(configuration);

                    exporter.exportReport();
                } catch (Exception jex) {
                    jex.printStackTrace();
                }
            } else outFileName = null;
        } catch(JRException e){
            e.printStackTrace();
        }

        return outFileName;
    }
}
