package Editor.activetree;

import com.activetree.common.resource.AtImageList;
import com.activetree.jeditor.*;
import javafx.application.Platform;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebView;

public class CustomJEditor extends JApplet implements AtEditorActionListener {
    private static final String editorName = "Editor de Contratos do Sistema jAdmin";
    private JFrame frame;
    private CustomDocumentEditor editor;
    private int number = 1;
    private boolean isApplet = false;
    private boolean _isPreContrato = true;

    public void set_isPreContrato(boolean _isPreContrato) {
        this._isPreContrato = _isPreContrato;
    }

    public void main() {
        this.isApplet = true;
        CustomJEditor demo = new CustomJEditor();
        demo.postInit();
        demo.setVisible(true);
        JTextPane edit = demo.editor.getEditor();

        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                String selectedText = edit.getSelectedText();
                JPopupMenu menuFields = CriaMenu(selectedText != null, edit, _isPreContrato);
                edit.setComponentPopupMenu(menuFields);
            }
        });
        this.setToTop(true);
    }

    public CustomJEditor() { }

    public void Inicializa() {
        CustomJEditor demo = new CustomJEditor();
        demo.postInit();
        demo.setVisible(true);
    }

    public void init() {
        this.isApplet = true;
        this.postInit();
        this.setVisible(true);
    }

    public void setVisible(boolean visible) {
        this.frame.show(visible);
    }

    public void setToTop(boolean value) {
        this.frame.setAlwaysOnTop(value);
    }

    public void postInit() {
        this.frame = new JFrame("Untitled");
        this.frame.setSize(800, 600);
        this.editor = new CustomDocumentEditor();
        this.editor.addEditorActionListener(this);
        Container c = this.frame.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(this.editor, "Center");
        this.frame.setDefaultCloseOperation(0);
        this.frame.setIconImage(AtImageList.IMAGE_LIST.ACTIVETREE_ICON_SMALL.getImage());
        this.addFrameCloseActionListener();
    }

    public void openSampleDocument(String fileName) {
        String defaultDocument = System.getProperty("user.dir") + fileName;
        InputStream rtfStream = this.getClass().getResourceAsStream(defaultDocument);
        this.openDocument(rtfStream);
        this.frame.setTitle(defaultDocument + " - " + "Editor de Contratos do Sistema jAdmin");
    }

    public void enableDisableMenu(boolean enable) {
        this.editor.getFileMenu().enable(1, enable);
        this.editor.getFileMenu().enable(2, enable);
        this.editor.getFileMenu().enable(5, enable);
        this.editor.getFileMenu().enable(3, enable);

        this.editor.getEditMenu().enable(1, enable);
        this.editor.getEditMenu().enable(3, enable);
        this.editor.getEditMenu().enable(8, enable);
        this.editor.getEditMenu().enable(7, enable);
        this.editor.getEditMenu().enable(6, enable);

        this.editor.getFontMenu().enable(1, enable);
        this.editor.getFontMenu().enable(2, enable);
        this.editor.getFontMenu().enable(3, enable);

        this.editor.getFormatMenu().enable(1, enable);
        this.editor.getFormatMenu().enable(2, enable);
        this.editor.getFormatMenu().enable(3, enable);

        this.editor.getImageTools().enable(1, enable);
    }

    public void setEditable(boolean editable) {
        this.editor.getEditor().setEditable(editable);
    }

    private void addFrameCloseActionListener() {
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (CustomJEditor.this.isNeedsSaving()) {
                    int reply = JOptionPane.showOptionDialog(CustomJEditor.this.frame, "Salva as modificações de \"" + CustomJEditor.this.getDocumentName() + "\"?", "Confirme para sair", 1, 3, (Icon)null, (Object[])null, (Object)null);
                    if (reply == 0) {
                        CustomJEditor.this.save();
                    } else if (reply != 1 && reply == 2) {
                        return;
                    }
                }

                CustomJEditor.this.exit();
            }
        });
    }

    private void exit() {
        //if (this.isApplet) {
            this.frame.setVisible(false);
            this.frame.dispose();
        //} else {
        //    System.exit(0);
        //}
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public void openDocument(InputStream rtfStream) {
        try {
            this.editor.openDocument(rtfStream);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    public void setText(String texto) {
        Document doc = this.editor.getDocument();
        int length = doc.getLength();
        try {
            doc.insertString(length,texto,null);
        } catch (BadLocationException ex) { }
    }

    public void openNew() {
        this.editor.openNew();
    }

    public boolean isNeedsSaving() {
        return this.editor.isDirty();
    }

    public void openDocument(String fileName) {
        this.editor.openDocument(fileName);
    }

    public String getDocumentName() {
        String docName = this.editor.getCurrentFile();
        return docName == null ? "Untitled" : docName;
    }

    public void save() {
        this.editor.save();
    }

    public void actionWillBePerformed(ActionEvent evt) {
    }

    public void actionIsPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        String file = this.editor.getCurrentFile();
        if (cmd.equalsIgnoreCase("New")) {
            this.frame.setTitle("Untitled-" + this.number++);
        } else if (cmd.equals("Close")) {
            this.frame.setTitle("Editor de Contratos do Sistema jAdmin");
        } else if (cmd.equalsIgnoreCase("Open") && file != null) {
            this.frame.setTitle(this.editor.getCurrentFile() + " - " + "Editor de Contratos do Sistema jAdmin");
        } else if (cmd.equalsIgnoreCase("Save") && file != null) {
            this.frame.setTitle(this.editor.getCurrentFile() + " - " + "Editor de Contratos do Sistema jAdmin");
        } else if (cmd.equalsIgnoreCase("Save As") && file != null) {
            this.frame.setTitle(this.editor.getCurrentFile() + " - " + "Editor de Contratos do Sistema jAdmin");
        } else if (cmd.equalsIgnoreCase("Exit") && this.editor.isExited()) {
            this.exit();
        }

    }

    class CustomDocumentEditor extends RtfViewPane {
        public CustomDocumentEditor() {
            this.customizeEditor();
        }

        public void customizeEditor() {
            ImageIcon myPasteIcon = AtImageList.IMAGE_LIST.PASTE_16;
            String myPasteMenuName = "My Paste Menu Item";

            class MyPasteAction extends AbstractAction {
                public MyPasteAction(String name, ImageIcon imgIcon) {
                    if (imgIcon == null) {
                        this.putValue("Name", name);
                    } else {
                        this.putValue("SmallIcon", imgIcon);
                    }

                    this.putValue("SmallIcon", imgIcon);
                    this.putValue("ActionCommandKey", "My Paste Menu Item");
                    this.putValue("ShortDescription", "My Paste Menu Item");
                    this.putValue("LongDescription", "My Paste Menu Item");
                }

                public void actionPerformed(ActionEvent evt) {
                    System.out.println("My Paste Action");
                }
            }

            MyPasteAction menuPasteAction = new MyPasteAction("My Paste Menu Item", (ImageIcon)null);
            MyPasteAction buttonPasteAction = new MyPasteAction("My Paste Menu Item", myPasteIcon);
            JMenuItem myPasteMenuItem = new JMenuItem(menuPasteAction);
            super.getEditMenu().getMenu().add(myPasteMenuItem);
            JComponent myPasteToolBarButton = super.getEditMenu().createFlatButton(buttonPasteAction, "My custom paste tooltip.");
            super.getEditMenu().getToolBar().add(myPasteToolBarButton);
            super.getHelpTool().setVisible(1, false);

            JMenu myHelpMenu = new JMenu("Ajuda");
            JMenuItem helpMenuItem = new JMenuItem("Ajuda");
            helpMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            javafx.scene.control.Dialog<String> dialog;
                            dialog = new javafx.scene.control.Dialog<>();
                            dialog.setTitle("Ajuda do Editor de Contratos.");

                            ButtonType salvarButtonType = new ButtonType("Fechar", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType);
                            Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
                            salvarButton.setDisable(false);
                            dialog.setResultConverter(dialogButton -> {
                                if (dialogButton == salvarButtonType) {
                                    return null;
                                }
                                return null;
                            });                            
        
                            WebView webView = new WebView();
                            webView.getEngine().load("file://" + System.getProperty("user.dir") + "/resources//Ajuda.html");
                            dialog.getDialogPane().setContent(webView);
                            Platform.runLater(() -> webView.requestFocus());
                            dialog.showAndWait();
                        }
                    });
                }
            });
            myHelpMenu.add(helpMenuItem);
            super.getMenuBar().add(myHelpMenu);

            FormatTools formatMenuControl = super.getFormatMenu();
            EditTools editMenuControl = super.getEditMenu();
            PrintTools printMenuControls = super.getPrintMenu();
            FileTools fileMenuControls = super.getFileMenu();
        }
    }

    private JPopupMenu  CriaMenu(boolean isSelectedText, JTextPane edit, boolean isPreContrato) {
        JPopupMenu  menuContext = null;

        if (isSelectedText) {
            // --------------------------- Funções
            final JPopupMenu functionsMenu = new JPopupMenu();
            JMenuItem capitule = new JMenuItem ("Capitule");
            capitule.setToolTipText("Transforma as primeiras letras em Maiúsculas.");

            JMenuItem condicao = new JMenuItem("Condicao");
            condicao.setToolTipText("Cria condições para o campo selecionado.");
            
            JMenuItem extenso = new JMenuItem("Extenso");
            extenso.setToolTipText("Escreve um valor numerico em extenço.");
            
            JMenuItem formata = new JMenuItem("Format");
            formata.setToolTipText("Formata String, Datas e Valores(Currency)");
            
            JMenuItem limpa = new JMenuItem("Limpa");
            limpa.setToolTipText("Remove espaços antes e depois das palavras.");
            
            JMenuItem toupper = new JMenuItem("UpperCase");
            toupper.setToolTipText("Transforma todo texto selecionado em MAIUSCULA.");
            
            JMenuItem tolower = new JMenuItem("LowerCase");
            tolower.setToolTipText("Transforma todo texto selecionado em minuscula.");
            
            functionsMenu.add(capitule);
            functionsMenu.add(condicao);
            functionsMenu.add(extenso);
            functionsMenu.add(formata);
            functionsMenu.add(limpa);
            functionsMenu.add(toupper);
            functionsMenu.add(tolower);

            capitule.addActionListener(event -> {
                edit.replaceSelection("$Capitule(" + (edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'" : edit.getSelectedText()) + ")");
            });

            extenso.addActionListener(event -> {
                edit.replaceSelection("$Extenso(" + (edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'": edit.getSelectedText())  + ")");
            });

            limpa.addActionListener(event -> {
                edit.replaceSelection("$trim(" + (edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'": edit.getSelectedText())  + ")");
            });

            toupper.addActionListener(event -> {
                edit.replaceSelection("$toUpperCase(" + (edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'": edit.getSelectedText())  + ")");
            });

            tolower.addActionListener(event -> {
                edit.replaceSelection("$toLowerCase(" + (edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'": edit.getSelectedText()) + ")");
            });

            formata.addActionListener(event -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        adcFormata dialog = new adcFormata();
                        Optional<String> result = dialog.adcFormata((edit.getSelectedText().substring(0,1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'": edit.getSelectedText()) );
                        result.ifPresent(b -> {
                            edit.replaceSelection("$" + b);
                        });
                    }
                });
            });

            condicao.addActionListener(event -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        adcCondicao dialog = new adcCondicao();
                        Optional<String> result = dialog.adcCondicao((edit.getSelectedText().substring(0, 1).equalsIgnoreCase("$") ? "'" + edit.getSelectedText().substring(1) + "'" : edit.getSelectedText()));
                        result.ifPresent(b -> {
                            edit.replaceSelection("$" + b);
                        });
                    }
                });
            });

            menuContext = functionsMenu;
        } else {
            // --------------------------- Campos
            JPopupMenu fieldsMenu = new JPopupMenu();

            String[] propToolTips = new String[] {
                    "Registro do Proprietário.",
                    "Nome do Proprietário.",
                    "F para Física ou J para Jurídica.",
                    "Endereço do Proprietário.",
                    "Número do Endereço do Proprietário.",
                    "Complemento do Endereço do Proprietário.",
                    "Bairro do Proprietário",
                    "Cidade do Proprietário",
                    "Estado do Proprietário",
                    "Cep do Proprietário",
                    "Telefone do Proprietário",
                    "Representante do Proprietário",
                    "Data de Nascimento do Representante do Proprietário",
                    "CPF ou CNPJ do Proprietário",
                    "Email do Proprietário",
                    "Profiss�o do Proprietário",
                    "Nacionalidade do Proprietário",
                    "Estado Civil do Proprietário",
                    "Sexo do Proprietário",
                    "Data de Nascimento do Proprietário",
                    "RG ou Inscrição do Proprietário",
                    "Conjugue(Esposa(o)) do Proprietário",
                    "Data de Nascimento do Conjugue(Esposa(o)) do Proprietário"
            };
            JMenu propMenu = new JMenu("proprietarios");
            JMenuItem prop_item01 = new JMenuItem("p_rgprp");
            JMenuItem prop_item02 = new JMenuItem("p_nome");
            JMenuItem prop_item03 = new JMenuItem("p_fisjur");
            JMenuItem prop_item04 = new JMenuItem("p_end");
            JMenuItem prop_item05 = new JMenuItem("p_num");
            JMenuItem prop_item06 = new JMenuItem("p_compl");
            JMenuItem prop_item07 = new JMenuItem("p_bairro");
            JMenuItem prop_item08 = new JMenuItem("p_cidade");
            JMenuItem prop_item09 = new JMenuItem("p_estado");
            JMenuItem prop_item10 = new JMenuItem("p_cep");
            JMenuItem prop_item11 = new JMenuItem("p_tel");
            JMenuItem prop_item12 = new JMenuItem("p_representante");
            JMenuItem prop_item13 = new JMenuItem("p_repdtnasc");
            JMenuItem prop_item14 = new JMenuItem("p_cpfcnpj");
            JMenuItem prop_item15 = new JMenuItem("p_email");
            JMenuItem prop_item16 = new JMenuItem("p_profissao");
            JMenuItem prop_item17 = new JMenuItem("p_nacionalidade");
            JMenuItem prop_item18 = new JMenuItem("p_estcivil");
            JMenuItem prop_item19 = new JMenuItem("p_sexo");
            JMenuItem prop_item20 = new JMenuItem("p_dtnasc");
            JMenuItem prop_item21 = new JMenuItem("p_rginsc");
            JMenuItem prop_item22 = new JMenuItem("p_conjugue");
            JMenuItem prop_item23 = new JMenuItem("p_conjdtnasc");

            propMenu.add(prop_item01); propMenu.add(prop_item02);
            propMenu.add(prop_item03); propMenu.add(prop_item04);
            propMenu.add(prop_item05); propMenu.add(prop_item06);
            propMenu.add(prop_item07); propMenu.add(prop_item08);
            propMenu.add(prop_item09); propMenu.add(prop_item10);
            propMenu.add(prop_item11); propMenu.add(prop_item12);
            propMenu.add(prop_item13); propMenu.add(prop_item14);
            propMenu.add(prop_item15); propMenu.add(prop_item16);
            propMenu.add(prop_item17); propMenu.add(prop_item18);
            propMenu.add(prop_item19); propMenu.add(prop_item20);
            propMenu.add(prop_item21); propMenu.add(prop_item22);
            propMenu.add(prop_item23); propMenu.add(propMenu);

            for (int i = 0 ; i <  propMenu.getItemCount(); i++) {
                if (propMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = propMenu.getItem(i);
                item.setToolTipText(propToolTips[i]);
                item.addActionListener(event -> {
                    edit.replaceSelection("$<" + propMenu.getText() + "." + item.getText() + ">");
                });
            }

            String[] imovToolTips = new String[] {
                    "Registro do Imóvel do Proprietário",
                    "Tipo do Imóvel do Proprietário",
                    "Situa��o do Imóvel do Proprietário",
                    "Endereço do Imóvel do Proprietário",
                    "Número do Endereço do Imóvel do Proprietário",
                    "Complemento do Endereço do Imóvel do Proprietário",
                    "Bairro do Imóvel do Proprietário",
                    "Cidade do Imóvel do Proprietário",
                    "C�digo do Município do Imóvel do Proprietário",
                    "Estado do Imóvel do Proprietário",
                    "U para Urbano ou R para Rural do Imóvel do Proprietário",
                    "Cep do Imóvel do Proprietário",
                    "Tipo do Imóvel do Proprietário"
            };
            JMenu imovMenu = new JMenu("imoveis");
            JMenuItem imov_item01 = new JMenuItem("i_rgimv");
            JMenuItem imov_item02 = new JMenuItem("i_tipo");
            JMenuItem imov_item03 = new JMenuItem("i_situacao");
            JMenuItem imov_item04 = new JMenuItem("i_end");
            JMenuItem imov_item05 = new JMenuItem("i_num");
            JMenuItem imov_item06 = new JMenuItem("i_cplto");
            JMenuItem imov_item07 = new JMenuItem("i_bairro");
            JMenuItem imov_item08 = new JMenuItem("i_cidade");
            JMenuItem imov_item09 = new JMenuItem("i_cdmun");
            JMenuItem imov_item10 = new JMenuItem("i_estado");
            JMenuItem imov_item11 = new JMenuItem("i_ur");
            JMenuItem imov_item12 = new JMenuItem("i_cep");
            JMenuItem imov_item13 = new JMenuItem("i_tipoimv");
            imovMenu.add(imov_item01); imovMenu.add(imov_item02);
            imovMenu.add(imov_item03); imovMenu.add(imov_item04);
            imovMenu.add(imov_item05); imovMenu.add(imov_item06);
            imovMenu.add(imov_item07); imovMenu.add(imov_item08);
            imovMenu.add(imov_item09); imovMenu.add(imov_item10);
            imovMenu.add(imov_item11); imovMenu.add(imov_item12);
            imovMenu.add(imov_item13);

            for (int i = 0; i < imovMenu.getItemCount(); i++) {
                if (imovMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = imovMenu.getItem(i);
                item.setToolTipText(imovToolTips[i]);
                item.addActionListener(event -> {
                    edit.replaceSelection("$<" + imovMenu.getText() + "." + item.getText() + ">");
                });
            }

            String[] locaToolTip = new String[]{
                    "Contrato do Locatário.",
                    "Tipo do im�vel do Locatário.",
                    "F para Física ou J para Jurídica do Locatário."
            };
            JMenu locaMenu = new JMenu(isPreContrato ? "prelocatarios" : "locatarios");
            JMenuItem loca_item01 = new JMenuItem("l_contrato");
            JMenuItem loca_item02 = new JMenuItem("l_tipoimovel");
            JMenuItem loca_item03 = new JMenuItem("l_fisjur");

            locaMenu.add(loca_item01); locaMenu.add(loca_item02); locaMenu.add(loca_item03);
            for (int i = 0; i < locaMenu.getItemCount(); i++) {
                if (locaMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = locaMenu.getItem(i);
                item.setToolTipText(locaToolTip[i]);
                item.addActionListener(event -> {
                    edit.replaceSelection("$<" + locaMenu.getText() + "." + item.getText() + ">");
                });
            }

            String[] locaFisToolTip = new String[]{
                    "CPF ou CNPJ do Locatário.",
                    "RG ou Inscrição do Locatário.",
                    "Nome pessoa física do Locatário.",
                    "Sexo pessoa física do Locatário.",
                    "Data de Nascimento pessoa física do Locatário.",
                    "Nacionalidade pessoa física do Locatário.",
                    "Estado civil pessoa física do Locatário.",
                    "Telefone pessoa física do Locatário.",
                    "Mãe pessoa física do Locatário.",
                    "Pai pessoa física do Locatário.",
                    "Empresa aonde trabalha pessoa física do Locatário.",
                    "Data admissão da pessoa física do Locatário.",
                    "Endereço da Empresa pessoa física do Locatário.",
                    "Número do Endereço da Empresa pessoa física do Locatário.",
                    "Complemento do Endereço da Empresa pessoa física do Locatário.",
                    "Bairro da Empresa pessoa física do Locatário.",
                    "Cidade da Empresa pessoa física do Locatário.",
                    "Estado da Empresa pessoa física do Locatário.",
                    "Cep da Empresa pessoa física do Locatário.",
                    "Cargo na Empresa pessoa física do Locatário.",
                    "Salário na Empresa pessoa física do Locatário.",
                    "Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Data Nascimento Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Sexo do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "RG do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "CPF do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Salário do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Empresa do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Telefone do Conjugue(Esposa(o)) pessoa física do Locatário.",
                    "Email do Locatário."
            };
            JMenu locaFisMenu = new JMenu("Física");
            JMenuItem loca_item04 = new JMenuItem("l_cpfcnpj");
            JMenuItem loca_item05 = new JMenuItem("l_rginsc");
            JMenuItem loca_item06 = new JMenuItem("l_f_nome");
            JMenuItem loca_item07 = new JMenuItem("l_f_sexo");
            JMenuItem loca_item08 = new JMenuItem("l_f_dtnasc");
            JMenuItem loca_item09 = new JMenuItem("l_f_nacionalidade");
            JMenuItem loca_item10 = new JMenuItem("l_f_estcivil");
            JMenuItem loca_item11 = new JMenuItem("l_f_tel");
            JMenuItem loca_item12 = new JMenuItem("l_f_mae");
            JMenuItem loca_item13 = new JMenuItem("l_f_pai");
            JMenuItem loca_item14 = new JMenuItem("l_f_empresa");
            JMenuItem loca_item15 = new JMenuItem("l_f_dtadmissao");
            JMenuItem loca_item16 = new JMenuItem("l_f_endereco");
            JMenuItem loca_item17 = new JMenuItem("l_f_numero");
            JMenuItem loca_item18 = new JMenuItem("l_f_cplto");
            JMenuItem loca_item19 = new JMenuItem("l_f_bairro");
            JMenuItem loca_item20 = new JMenuItem("l_f_cidade");
            JMenuItem loca_item21 = new JMenuItem("l_f_estado");
            JMenuItem loca_item22 = new JMenuItem("l_f_cep");
            JMenuItem loca_item23 = new JMenuItem("l_f_cargo");
            JMenuItem loca_item24 = new JMenuItem("l_f_salario");
            JMenuItem loca_item25 = new JMenuItem("l_f_conjugue");
            JMenuItem loca_item26 = new JMenuItem("l_f_conjuguedtnasc");
            JMenuItem loca_item27 = new JMenuItem("l_f_conjuguesexo");
            JMenuItem loca_item28 = new JMenuItem("l_f_conjuguerg");
            JMenuItem loca_item29 = new JMenuItem("l_f_conjuguecpf");
            JMenuItem loca_item30 = new JMenuItem("l_f_conjuguesalario");
            JMenuItem loca_item31 = new JMenuItem("l_f_conjugueempresa");
            JMenuItem loca_item32 = new JMenuItem("l_f_conjuguetelefone");
            JMenuItem loca_item33 = new JMenuItem("l_f_email");

            locaFisMenu.add(loca_item04); locaFisMenu.add(loca_item05); locaFisMenu.add(loca_item06);
            locaFisMenu.add(loca_item07); locaFisMenu.add(loca_item08); locaFisMenu.add(loca_item09);
            locaFisMenu.add(loca_item10); locaFisMenu.add(loca_item11); locaFisMenu.add(loca_item12);
            locaFisMenu.add(loca_item13); locaFisMenu.add(loca_item14); locaFisMenu.add(loca_item15);
            locaFisMenu.add(loca_item16); locaFisMenu.add(loca_item17); locaFisMenu.add(loca_item18);
            locaFisMenu.add(loca_item19); locaFisMenu.add(loca_item20); locaFisMenu.add(loca_item21);
            locaFisMenu.add(loca_item22); locaFisMenu.add(loca_item23); locaFisMenu.add(loca_item24);
            locaFisMenu.add(loca_item25); locaFisMenu.add(loca_item26); locaFisMenu.add(loca_item27);
            locaFisMenu.add(loca_item28); locaFisMenu.add(loca_item29); locaFisMenu.add(loca_item30);
            locaFisMenu.add(loca_item31); locaFisMenu.add(loca_item32); locaFisMenu.add(loca_item33);
            for (int i = 0; i < locaFisMenu.getItemCount(); i++) {
                if (locaFisMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = locaFisMenu.getItem(i);
                item.setToolTipText(locaFisToolTip[i]);
                item.addActionListener(event -> {
                    //edit.replaceSelection("$<" + locaMenu.getText() + "." + item.getText() + ">");

                    String dialog = JOptionPane.showInputDialog(this, "Olhe, Coloque aqui a posição do locatario:", "Escolha o fiador", JOptionPane.QUESTION_MESSAGE);
                    int posicao = 0;
                    if (dialog == null || dialog == "") posicao = 0; else posicao = Integer.valueOf(dialog);
                    if (posicao == 0) {
                        edit.replaceSelection("$<" + locaMenu.getText() + "." + item.getText() + ">");
                    } else {
                        edit.replaceSelection("$<" + "adclocatarios" + "." + item.getText() + ">[" + (posicao - 1) + "]");
                    }
                });
            }
            locaMenu.add(locaFisMenu);

            String[] locaJurToolTip = new String[]{
                    "CPF ou CNPJ do Locatário.",
                    "RG ou Inscri��o do Locatário.",
                    "Raz�o Social pessoa jurídica do Locatário.",
                    "Nome Fantasia pessoa jurídica do Locatário.",
                    "Endereço da pessoa jurídica do Locatário.",
                    "Número do Endereço da pessoa jurídica do Locatário.",
                    "Complemento do Endereço da pessoa jurídica do Locatário.",
                    "Bairro pessoa jurídica do Locatário.",
                    "Cidade pessoa jurídica do Locatário.",
                    "Estado pessoa jurídica do Locatário.",
                    "Cep pessoa jurídica do Locatário.",
                    "Data do Contrato Social pessoa jurídica do Locatário.",
                    "Telefone pessoa jurídica do Locatário.",
                    "Email pessoa jurídica do Locatário."
            };
            JMenu locaJurMenu = new JMenu("Jurídica");
            JMenuItem loca_item34a = new JMenuItem("l_cpfcnpj");
            JMenuItem loca_item34b = new JMenuItem("l_rginsc");
            JMenuItem loca_item34 = new JMenuItem("l_j_razao");
            JMenuItem loca_item35 = new JMenuItem("l_j_fantasia");
            JMenuItem loca_item36 = new JMenuItem("l_j_endereco");
            JMenuItem loca_item37 = new JMenuItem("l_j_numero");
            JMenuItem loca_item38 = new JMenuItem("l_j_cplto");
            JMenuItem loca_item39 = new JMenuItem("l_j_bairro");
            JMenuItem loca_item40 = new JMenuItem("l_j_cidade");
            JMenuItem loca_item41 = new JMenuItem("l_j_estado");
            JMenuItem loca_item42 = new JMenuItem("l_j_cep");
            JMenuItem loca_item43 = new JMenuItem("l_j_dtctrosocial");
            JMenuItem loca_item44 = new JMenuItem("l_j_tel");
            JMenuItem loca_item45 = new JMenuItem("l_j_email");

            locaJurMenu.add(loca_item34a); locaJurMenu.add(loca_item34b);
            locaJurMenu.add(loca_item34); locaJurMenu.add(loca_item35); locaJurMenu.add(loca_item36);
            locaJurMenu.add(loca_item37); locaJurMenu.add(loca_item38); locaJurMenu.add(loca_item39);
            locaJurMenu.add(loca_item40); locaJurMenu.add(loca_item41); locaJurMenu.add(loca_item42);
            locaJurMenu.add(loca_item43); locaJurMenu.add(loca_item44); locaJurMenu.add(loca_item45);
            for (int i = 0; i < locaJurMenu.getItemCount(); i++) {
                if (locaJurMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = locaJurMenu.getItem(i);
                item.setToolTipText(locaJurToolTip[i]);
                item.addActionListener(event -> {
                    edit.replaceSelection("$<" + locaMenu.getText() + "." + item.getText() + ">");
                });
            }

            String[] locaJurSociosToolTip = new String[]{
                    "Nome do Sócio.",
                    "Sexo do Sócio.",
                    "Data de Nascimento do Sócio.",
                    "CPF ou CNPJ do Sócio.",
                    "RG ou Inscrição do Sócio.",
                    "Endereço do Sócio.",
                    "Número do Endereço do Sócio.",
                    "Complemento do Endereço do Sócio.",
                    "Bairro do Endereço do Sócio.",
                    "Cidade do Endereço do Sócio.",
                    "Estado do Endereço do Sócio.",
                    "Cep do Endereço do Sócio.",
                    "Nacionalidade do Sócio",
                    "Estado Civil do Sócio",
                    "Telefone do Sócio",
                    "Mãe do Sócio",
                    "Pai do Sócio",
                    "Cargo do Sócio",
                    "Renda do Sócio"
            };
            JMenu locaJurSociosMenu = new JMenu("Sócios");
            JMenuItem socio_item01 = new JMenuItem("s_nome");
            JMenuItem socio_item02 = new JMenuItem("s_sexo");
            JMenuItem socio_item03 = new JMenuItem("s_dtnasc");
            JMenuItem socio_item04 = new JMenuItem("s_cpfcnpj");
            JMenuItem socio_item05 = new JMenuItem("s_rginsc");
            JMenuItem socio_item06 = new JMenuItem("s_endereco");
            JMenuItem socio_item07 = new JMenuItem("s_numero");
            JMenuItem socio_item08 = new JMenuItem("s_cplto");
            JMenuItem socio_item09 = new JMenuItem("s_bairro");
            JMenuItem socio_item10 = new JMenuItem("s_cidade");
            JMenuItem socio_item11 = new JMenuItem("s_estado");
            JMenuItem socio_item12 = new JMenuItem("s_cep");
            JMenuItem socio_item13 = new JMenuItem("s_nacionalidade");
            JMenuItem socio_item14 = new JMenuItem("s_estcivil");
            JMenuItem socio_item15 = new JMenuItem("s_tel");
            JMenuItem socio_item16 = new JMenuItem("s_mae");
            JMenuItem socio_item17 = new JMenuItem("s_pai");
            JMenuItem socio_item18 = new JMenuItem("s_cargo");
            JMenuItem socio_item19 = new JMenuItem("s_renda");

            locaJurSociosMenu.add(socio_item01); locaJurSociosMenu.add(socio_item02);
            locaJurSociosMenu.add(socio_item03); locaJurSociosMenu.add(socio_item04);
            locaJurSociosMenu.add(socio_item05); locaJurSociosMenu.add(socio_item06);
            locaJurSociosMenu.add(socio_item07); locaJurSociosMenu.add(socio_item08);
            locaJurSociosMenu.add(socio_item09); locaJurSociosMenu.add(socio_item10);
            locaJurSociosMenu.add(socio_item11); locaJurSociosMenu.add(socio_item12);
            locaJurSociosMenu.add(socio_item13); locaJurSociosMenu.add(socio_item14);
            locaJurSociosMenu.add(socio_item15); locaJurSociosMenu.add(socio_item16);
            locaJurSociosMenu.add(socio_item17); locaJurSociosMenu.add(socio_item18);
            locaJurSociosMenu.add(socio_item19);
            for (int i = 0; i < locaJurSociosMenu.getItemCount(); i++) {
                if (locaJurSociosMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = locaJurSociosMenu.getItem(i);
                item.setToolTipText(locaJurSociosToolTip[i]);
                item.addActionListener(event -> {
                    //edit.replaceSelection("$<" + "socios" + "." + item.getText() + ">");

                    String dialog = JOptionPane.showInputDialog(this, "Olhe, Coloque aqui a posição do locatario:", "Escolha o fiador", JOptionPane.QUESTION_MESSAGE);
                    int posicao = 0;
                    if (dialog == null || dialog == "") posicao = 0; else posicao = Integer.valueOf(dialog);
                    edit.replaceSelection("$<" + "socios" + "." + item.getText() + ">[" + posicao + "]");
                });
            }

            locaJurMenu.add(locaJurSociosMenu);
            locaMenu.add(locaJurMenu);

            String[] fiadToolTip = new String[] {
                    "CPF ou CNPJ do Fiador.",
                    "RG ou Inscri��o do Fiador.",
                    "Nome pessoa física do Fiador.",
                    "Sexo pessoa física do Fiador.",
                    "Data de Nascimento pessoa física do Fiador.",
                    "Endereço pessoa física do Fiador.",
                    "Número do Endereço pessoa física do Fiador.",
                    "Complemento do Endereço pessoa física do Fiador.",
                    "Bairro pessoa física do Fiador.",
                    "Cidade pessoa física do Fiador.",
                    "Estado pessoa física do Fiador.",
                    "Cep pessoa física do Fiador.",
                    "Nacionalidade pessoa física do Fiador.",
                    "Estado civil pessoa física do Fiador.",
                    "Telefone pessoa física do Fiador.",
                    "Mãe pessoa física do Fiador.",
                    "Pai pessoa física do Fiador.",
                    "Empresa pessoa física do Fiador.",
                    "Data Admiss�o Empresa pessoa física do Fiador.",
                    "Endereço Empresa pessoa física do Fiador.",
                    "Número Endereço Empresa pessoa física do Fiador.",
                    "Complemento Endereço Empresa pessoa física do Fiador.",
                    "Bairro Endereço Empresa pessoa física do Fiador.",
                    "Cidade Endereço Empresa pessoa física do Fiador.",
                    "Estado Endereço Empresa pessoa física do Fiador.",
                    "Cep Endereço Empresa pessoa física do Fiador.",
                    "Cargo Empresa pessoa física do Fiador.",
                    "Salário Empresa pessoa física do Fiador.",
                    "Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Data Nascimento do Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Sexo Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "RG Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "CPF Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Salário Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Empresa Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Telefone Empresa Conjugue(Esposa(o)) pessoa física do Fiador.",
                    "Email pessoa física do Fiador.",
                    "Razão Social pessoa jurídica do Fiador.",
                    "Fantasia pessoa jurídica do Fiador.",
                    "Endereço pessoa jurídica do Fiador.",
                    "Número do Endereço pessoa jurídica do Fiador.",
                    "Complemento do Endereço pessoa jurídica do Fiador.",
                    "Bairro do Endereço pessoa jurídica do Fiador.",
                    "Cidade do Endereço pessoa jurídica do Fiador.",
                    "Estado do Endereço pessoa jurídica do Fiador.",
                    "Cep do Endereço pessoa jurídica do Fiador.",
                    "Data Contrato Social pessoa jurídica do Fiador.",
                    "Telefone pessoa jurídica do Fiador.",
                    "Email pessoa jurídica do Fiador.",
            };
            JMenu fiadMenu = new JMenu("fiadores");
            JMenuItem fia_item01 = new JMenuItem("f_cpfcnpj");
            JMenuItem fia_item02 = new JMenuItem("f_rginsc");
            JMenuItem fia_item03 = new JMenuItem("f_f_nome");
            JMenuItem fia_item04 = new JMenuItem("f_f_sexo");
            JMenuItem fia_item05 = new JMenuItem("f_f_dtnasc");
            JMenuItem fia_item06 = new JMenuItem("f_f_endereco_fiador");
            JMenuItem fia_item07 = new JMenuItem("f_f_numero_fiador");
            JMenuItem fia_item08 = new JMenuItem("f_f_cplto_fiador");
            JMenuItem fia_item09 = new JMenuItem("f_f_bairro_fiador");
            JMenuItem fia_item10 = new JMenuItem("f_f_cidade_fiador");
            JMenuItem fia_item11 = new JMenuItem("f_f_estado_fiador");
            JMenuItem fia_item12 = new JMenuItem("f_f_cep_fiador");
            JMenuItem fia_item13 = new JMenuItem("f_f_nacionalidade");
            JMenuItem fia_item14 = new JMenuItem("f_f_estcivil");
            JMenuItem fia_item15 = new JMenuItem("f_f_tel");
            JMenuItem fia_item16 = new JMenuItem("f_f_mae");
            JMenuItem fia_item17 = new JMenuItem("f_f_pai");
            JMenuItem fia_item18 = new JMenuItem("f_f_empresa");
            JMenuItem fia_item19 = new JMenuItem("f_f_dtadmissao");
            JMenuItem fia_item20 = new JMenuItem("f_f_endereco");
            JMenuItem fia_item21 = new JMenuItem("f_f_numero");
            JMenuItem fia_item22 = new JMenuItem("f_f_cplto");
            JMenuItem fia_item23 = new JMenuItem("f_f_bairro");
            JMenuItem fia_item24 = new JMenuItem("f_f_cidade");
            JMenuItem fia_item25 = new JMenuItem("f_f_estado");
            JMenuItem fia_item26 = new JMenuItem("f_f_cep");
            JMenuItem fia_item27 = new JMenuItem("f_f_cargo");
            JMenuItem fia_item28 = new JMenuItem("f_f_salario");
            JMenuItem fia_item29 = new JMenuItem("f_f_conjugue");
            JMenuItem fia_item30 = new JMenuItem("f_f_conjuguedtnasc");
            JMenuItem fia_item31 = new JMenuItem("f_f_conjuguesexo");
            JMenuItem fia_item32 = new JMenuItem("f_f_conjuguerg");
            JMenuItem fia_item33 = new JMenuItem("f_f_conjuguecpf");
            JMenuItem fia_item34 = new JMenuItem("f_f_conjuguesalario");
            JMenuItem fia_item35 = new JMenuItem("f_f_conjugueempresa");
            JMenuItem fia_item36 = new JMenuItem("f_f_conjuguetelefone");
            JMenuItem fia_item37 = new JMenuItem("f_f_email");
            JMenuItem fia_item38 = new JMenuItem("f_j_razao");
            JMenuItem fia_item39 = new JMenuItem("f_j_fantasia");
            JMenuItem fia_item40 = new JMenuItem("f_j_endereco");
            JMenuItem fia_item41 = new JMenuItem("f_j_numero");
            JMenuItem fia_item42 = new JMenuItem("f_j_cplto");
            JMenuItem fia_item43 = new JMenuItem("f_j_bairro");
            JMenuItem fia_item44 = new JMenuItem("f_j_cidade");
            JMenuItem fia_item45 = new JMenuItem("f_j_estado");
            JMenuItem fia_item46 = new JMenuItem("f_j_cep");
            JMenuItem fia_item47 = new JMenuItem("f_j_dtctrosocial");
            JMenuItem fia_item48 = new JMenuItem("f_j_tel");
            JMenuItem fia_item49 = new JMenuItem("f_j_email");
            fiadMenu.add(fia_item01); fiadMenu.add(fia_item02); fiadMenu.add(fia_item03);
            fiadMenu.add(fia_item04); fiadMenu.add(fia_item05); fiadMenu.add(fia_item06);
            fiadMenu.add(fia_item07); fiadMenu.add(fia_item08); fiadMenu.add(fia_item09);
            fiadMenu.add(fia_item10); fiadMenu.add(fia_item11); fiadMenu.add(fia_item12);
            fiadMenu.add(fia_item13); fiadMenu.add(fia_item14); fiadMenu.add(fia_item15);
            fiadMenu.add(fia_item16); fiadMenu.add(fia_item17); fiadMenu.add(fia_item18);
            fiadMenu.add(fia_item19); fiadMenu.add(fia_item20); fiadMenu.add(fia_item21);
            fiadMenu.add(fia_item22); fiadMenu.add(fia_item23); fiadMenu.add(fia_item24);
            fiadMenu.add(fia_item25); fiadMenu.add(fia_item26); fiadMenu.add(fia_item27);
            fiadMenu.add(fia_item28); fiadMenu.add(fia_item29); fiadMenu.add(fia_item30);
            fiadMenu.add(fia_item31); fiadMenu.add(fia_item32); fiadMenu.add(fia_item33);
            fiadMenu.add(fia_item34); fiadMenu.add(fia_item35); fiadMenu.add(fia_item36);
            fiadMenu.add(fia_item37); fiadMenu.add(fia_item38); fiadMenu.add(fia_item39);
            fiadMenu.add(fia_item40); fiadMenu.add(fia_item41); fiadMenu.add(fia_item42);
            fiadMenu.add(fia_item43); fiadMenu.add(fia_item44); fiadMenu.add(fia_item45);
            fiadMenu.add(fia_item46); fiadMenu.add(fia_item47); fiadMenu.add(fia_item48);
            fiadMenu.add(fia_item49);

            for (int i = 0; i < fiadMenu.getItemCount(); i++) {
                if (fiadMenu.getItem(i) instanceof JMenu) continue;
                JMenuItem item = fiadMenu.getItem(i);
                item.setToolTipText(fiadToolTip[i]);
                item.addActionListener(event -> {
                    String dialog = JOptionPane.showInputDialog(this, "Olhe, Coloque aqui a posição do fiador", "Escolha o fiador", JOptionPane.QUESTION_MESSAGE);
                    int posicao = 0;
                    if (dialog == null || dialog == "") posicao = 0; else posicao = Integer.valueOf(dialog);
                    edit.replaceSelection("$<" + fiadMenu.getText() + "." + item.getText() + ">[" + posicao + "]");
                });
            }

            JMenu cartMenu = new JMenu("carteira");
            if (!isPreContrato) {
                String[] cartToolTip = new String[]{
                        "Data de Início de Contrato.",
                        "Data de Final de Contrato.",
                        "Data de Aditamento de Contrato.",
                        "Valor mensal do Contrato.",
                        "Data de Vencimento do Contrato.",
                        "Mês e Ano de Referencia do Contrato."
                };
                JMenuItem cart_item01 = new JMenuItem("dtinicio");
                JMenuItem cart_item02 = new JMenuItem("dtfim");
                JMenuItem cart_item03 = new JMenuItem("dtaditamento");
                JMenuItem cart_item04 = new JMenuItem("mensal");
                JMenuItem cart_item05 = new JMenuItem("dtvencimento");
                JMenuItem cart_item06 = new JMenuItem("referencia");
                cartMenu.add(cart_item01); cartMenu.add(cart_item02);
                cartMenu.add(cart_item03); cartMenu.add(cart_item04);
                cartMenu.add(cart_item05); cartMenu.add(cart_item06);

                for (int i = 0; i < cartMenu.getItemCount(); i++) {
                    if (cartMenu.getItem(i) instanceof JMenu) continue;
                    JMenuItem item = cartMenu.getItem(i);
                    item.setToolTipText(cartToolTip[i]);
                    item.addActionListener(event -> {
                        edit.replaceSelection("$<carteira." + item.getText() + ">");
                    });
                }
            } else {
                String[] cartToolTip = new String[]{
                        "Valor do Depósito.",
                        "Seguradora.",
                        "Número da Apólice.",
                        "Data da Apólice.",
                        "Data de Início de Contrato.",
                        "Data de Final de Contrato.",
                        "Valor mensal do contrato."
                };
                JMenuItem cart_item01 = new JMenuItem("l_vrdeposito");
                JMenuItem cart_item02 = new JMenuItem("l_cdseguradora");
                JMenuItem cart_item03 = new JMenuItem("l_nrapolice");
                JMenuItem cart_item04 = new JMenuItem("l_dtapolice");
                JMenuItem cart_item05 = new JMenuItem("l_dtinicioctro");
                JMenuItem cart_item06 = new JMenuItem("l_dtfimctro");
                JMenuItem cart_item07 = new JMenuItem("l_vrmensal");
                cartMenu.add(cart_item01); cartMenu.add(cart_item02);
                cartMenu.add(cart_item03); cartMenu.add(cart_item04);
                cartMenu.add(cart_item05); cartMenu.add(cart_item06);
                cartMenu.add(cart_item07);

                for (int i = 0; i < cartMenu.getItemCount(); i++) {
                    if (cartMenu.getItem(i) instanceof JMenu) continue;
                    JMenuItem item = cartMenu.getItem(i);
                    item.setToolTipText(cartToolTip[i]);
                    item.addActionListener(event -> {
                        edit.replaceSelection("$<" + "prelocatarios." + item.getText() + ">");
                    });
                }
            }

            fieldsMenu.add(propMenu); fieldsMenu.add(imovMenu); fieldsMenu.add(locaMenu);
            fieldsMenu.add(fiadMenu); fieldsMenu.add(cartMenu);

            menuContext = fieldsMenu;
        }

        return menuContext;
    }
}
