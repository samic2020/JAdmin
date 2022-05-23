package Funcoes;

import javax.print.PrintService;
import java.awt.print.PrinterJob;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import static Funcoes.ComandoExterno.ComandoExterno;

/**
 * Created by supervisor on 16/03/17.
 */

public class toPrint {
    public toPrint(String outFileName, String PrnType,String PrnMode) {
        // Retorna caso impressora esteja desligada ou ausente
        if (PrnType.equalsIgnoreCase("THERMICA")) {
            if (!VariaveisGlobais.statPrinterThermica) return;
            if (VariaveisGlobais.Thermica == null) return;
        } else {
            if (!VariaveisGlobais.statPrinterInkLaser) return;
            if (VariaveisGlobais.Printer == null) return;
        }

        String defaultFileName = null;
        if (!System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
            defaultFileName = backlashReplace(outFileName);
        } else defaultFileName = LinuxTags(outFileName);

        String defaultNamePrinter = null;

        String printers = null;
        String ipprinters = null;
        // THERMICA ou LASER
        if (PrnType.equalsIgnoreCase("THERMICA")) {
            printers = VariaveisGlobais.unidade + "," + VariaveisGlobais.Thermica;
        } else {
            printers = VariaveisGlobais.Printer;
            //ipprinters = VariaveisGlobais.
        }

        // EXTERNA ou INTERNA
        if (PrnMode.equalsIgnoreCase("EXTERNA")) {
            String defaultIpPrinter = null;
            String tprint[] = printers.split(",");
            if (tprint.length == 2) {
                defaultIpPrinter = tprint[0];
                defaultNamePrinter = tprint[1];
            }
            String cmdPrint = VariaveisGlobais.Externo;
            try {
                cmdPrint = cmdPrint.replace("[IP]", defaultIpPrinter);
                cmdPrint = cmdPrint.replace("[PRINTER]", defaultNamePrinter);
                cmdPrint = cmdPrint.replace("[FILENAME]", defaultFileName);
            } catch (Exception e) {e.printStackTrace();}

            try {
                ComandoExterno(cmdPrint);
            } catch (Exception e) {}
            System.out.println(cmdPrint);
        } else {
            String tprint[] = printers.split(",");
            if (tprint.length == 1) {
                defaultNamePrinter = tprint[0];
            } else {
                defaultNamePrinter = tprint[1];
            }

            try {
                PrintService impressora = null;
                PrintService[] pservices = PrinterJob.lookupPrintServices();

                if (pservices.length > 0) {
                    for (PrintService ps : pservices) {
                        System.out.println("Impressora Encontrada: " + ps.getName());

                        if (ps.getName().trim().equalsIgnoreCase(defaultNamePrinter.trim())) {
                            System.out.println("Impressora Selecionada: " + ps.getName());
                            impressora = ps;
                            break;
                        }
                    }
                }
                if (impressora != null) {
                    PrinterJob pjob = PrinterJob.getPrinterJob();
                    pjob.setPrintService(impressora);
                    pjob.setJobName(defaultFileName.substring(defaultFileName.lastIndexOf("\\") + 1));

                    // TODO - Resolver dependencia //PDDocument pdf = PDDocument.load(new File(defaultFileName.replace("\\ ", " ")));
                    //pjob.setPageable(new PDFPageable(pdf));
                    pjob.print();

                    //pdf.silentPrint(pjob);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private String LinuxTags(String outFileName) {
        outFileName = outFileName.replace(" ", "\\ ");
        outFileName = outFileName.replace("(", "\\(");
        outFileName = outFileName.replace(")", "\\)");

        return outFileName;
    }

    public String[] ListarImp() {
        String[] ret = {};
        PrintService[] pservices = PrinterJob.lookupPrintServices();

        if (pservices.length > 0) {
            for (PrintService ps : pservices) {
                ret = FuncoesGlobais.ArrayAdd(ret, ps.getName());
            }
        }

        return ret;
    }

    public static String backlashReplace(String myStr){
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){

            if (character == '/') {
                result.append("\\");
            }
            else {
                result.append(character);
            }


            character = iterator.next();
        }
        return result.toString();
    }


}
