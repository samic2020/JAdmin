package Funcoes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by supervisor on 16/03/17.
 */

public class jPDF {
    String pathName; String docName;
    Document doc = null;
    PdfWriter writer = null;
    OutputStream os = null;

    public int LEFT = Element.ALIGN_LEFT;
    public int CENTER = Element.ALIGN_CENTER;
    public int RIGHT = Element.ALIGN_RIGHT;
    public int JUSTIFIED = Element.ALIGN_JUSTIFIED;

    public int ARIAL = 3;
    public int HELVETICA = 0;

    public int NORMAL = Font.NORMAL;
    public int BOLD = Font.BOLD;
    public int ITALIC = Font.ITALIC;
    public int BOLDITALIC = Font.BOLDITALIC;
    public int UNDERLINE = Font.UNDERLINE;
    public int STRIKETHRU = Font.STRIKETHRU;

    public com.itextpdf.text.BaseColor WHITE = com.itextpdf.text.BaseColor.WHITE;
    public com.itextpdf.text.BaseColor BLACK = com.itextpdf.text.BaseColor.BLACK;
    public com.itextpdf.text.BaseColor BLUE = com.itextpdf.text.BaseColor.BLUE;
    public com.itextpdf.text.BaseColor RED = com.itextpdf.text.BaseColor.RED;
    public com.itextpdf.text.BaseColor YELLOW = com.itextpdf.text.BaseColor.YELLOW;
    public com.itextpdf.text.BaseColor PINK = com.itextpdf.text.BaseColor.PINK;
    public com.itextpdf.text.BaseColor ORANGE = com.itextpdf.text.BaseColor.ORANGE;
    public com.itextpdf.text.BaseColor MAGENTA = com.itextpdf.text.BaseColor.MAGENTA;
    public com.itextpdf.text.BaseColor GREEN = com.itextpdf.text.BaseColor.GREEN;

    public void setPagerSize() {}
    public void setPathName(String Value) { pathName = Value; }
    public String getPathName() { return pathName; }
    public void setDocName(String Value) { docName = Value; }
    public String getDocName() { return docName; }
    public void jThermica() {}

    public static String[][] FONTS = {
            {BaseFont.HELVETICA, BaseFont.WINANSI},
            {"resources/fonts/cmr10.afm", BaseFont.WINANSI},
            {"resources/fonts/cmr10.pfm", BaseFont.WINANSI},
            {"c:/windows/fonts/arial.ttf", BaseFont.WINANSI},
            {"c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H},
            {"resources/fonts/Puritan2.otf", BaseFont.WINANSI},
            {"c:/windows/fonts/msgothic.ttc,0", BaseFont.IDENTITY_H},
            {"KozMinPro-Regular", "UniJIS-UCS2-H"}
    };

    public void open() {
        float width = VariaveisGlobais.bobinaSize[0]; float height = VariaveisGlobais.bobinaSize[1];
        float left = VariaveisGlobais.bobinaSize[2]; float right = VariaveisGlobais.bobinaSize[3];
        float top = VariaveisGlobais.bobinaSize[4]; float botton = VariaveisGlobais.bobinaSize[5];

        try {
            Rectangle pagesize = new Rectangle(width, height);
            doc = new Document(pagesize, left, right, top, botton);

            // Cria o diretorio se necessário
            if (!new File(pathName).exists()) {
                boolean sucess = new File(pathName).mkdirs();
                if (!sucess) { System.out.println("Não consegui criar " + pathName); System.exit(1); }
            }

            //cria a stream de saída
            os = new FileOutputStream(pathName + docName);

            //associa a stream de saída ao
            writer = PdfWriter.getInstance(doc, os);

            //abre o documento
            doc.open();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void open(float twidth, float theight, float tleft, float tright, float ttop, float tbotton) {
        float width = twidth; float height = theight;
        float left = tleft; float right = tright;
        float top = ttop; float botton = tbotton;

        try {
            Rectangle pagesize = new Rectangle(width, height);
            doc = new Document(pagesize, left, right, top, botton);

            // Cria o diretorio se necessário
            if (!new File(pathName).exists()) {
                boolean sucess = new File(pathName).mkdirs();
                if (!sucess) { System.out.println("Não consegui criar " + pathName); System.exit(1); }
            }

            //cria a stream de saída
            os = new FileOutputStream(pathName + docName);

            //associa a stream de saída ao
            writer = PdfWriter.getInstance(doc, os);

            //abre o documento
            doc.open();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void doc_add(Object p) {
        try {
            //if (p instanceof Paragraph) doc.add((Paragraph)p);
            //if (p instanceof PdfPTable) doc.add((PdfPTable)p);
            doc.add((Element) p);
        } catch (Exception e) {}
    }

    public PdfWriter writer() {return this.writer; }

    public Paragraph print(String texto, int fNum, int fSize, int fStyle, int align, com.itextpdf.text.BaseColor fcolor) {
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(FONTS[fNum][0], FONTS[fNum][1], BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Font.NORMAL = 0; Font.BOLD = 1; Font.ITALIC = 2; Font.BOLDITALIC = 3; Font.UNDERLINE = 4; Font.STRIKETHRU = 8;
        // Element.ALIGN_LEFT = 0; Element.ALIGN_CENTER = 1; Element.ALIGN_RIGHT = 2; Element.ALIGN_JUSTIFIED = 3;
        Font font = new Font(bf, fSize, fStyle);
        font.setColor(fcolor);
        Paragraph p1;
        p1 = new Paragraph(texto, font);
        p1.setAlignment(align);

        return p1;
    }

    public Paragraph pprint(String texto, int fNum, int fSize, int fStyle, int align, com.itextpdf.text.BaseColor fcolor) {
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(FONTS[fNum][0], FONTS[fNum][1], BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, fSize, fStyle);
        font.setColor(fcolor);
        Paragraph p = new Paragraph();
        p.add(new Phrase(texto,font));

        return p;
    }

    public void line() {
        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setPercentage(59500f / 523f);
    }

    public void close() {
        doc.close();
    }

}
