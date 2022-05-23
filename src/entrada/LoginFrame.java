package entrada;

import Classes.paramEvent;
import Classes.vieaEvent;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.rad.genui.UIColor;
import javax.rad.genui.UIImage;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.component.UIIcon;
import javax.rad.genui.container.UIDesktopPanel;
import javax.rad.genui.container.UIFrame;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.container.UIPanel;
import javax.rad.genui.layout.UIBorderLayout;
import javax.rad.genui.layout.UIFormLayout;
import javax.rad.genui.menu.UIMenu;
import javax.rad.genui.menu.UIMenuBar;
import javax.rad.genui.menu.UIMenuItem;
import javax.rad.ui.IAlignmentConstants;
import javax.rad.ui.IContainer;
import javax.rad.ui.IImage;
import javax.rad.ui.layout.IFormLayout;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends UIFrame {
    private DbMain conn = null;
    private UIMenuBar barraMenu;
    private UIDesktopPanel desktopPanel;
    private boolean denyLogOut = false;

    private HTMLEditor htmlEditor;

    public LoginFrame() {
        super();

        // Todo - Otimizar Inicialização
        // The layout for the header panel.
        UIFormLayout headerPanelLayout = new UIFormLayout();

        // The panel used for the top bar.
        UIPanel headerPanel = new UIPanel();
        //headerPanel.setBackgroundImage(UIImage.getImage("/entrada/images/20101020_Sheep_shepherd_at_Vistonida_lake_Glikoneri_Rhodope_Prefecture_Thrace_Greece.jpg"));
        headerPanel.setLayout(headerPanelLayout);
        headerPanel.setBackground(UIColor.white);
        headerPanel.setPreferredSize(100,38);
        headerPanel.setMaximumSize(100,38);
        headerPanel.setMinimumSize(100,38);
        headerPanel.setSize(100,38);

        AnchorPane root = null;
        VariaveisGlobais.loader = null;
        try {
            VariaveisGlobais.loader = new FXMLLoader(getClass().getResource("/entrada/Barra.fxml"));
            root = VariaveisGlobais.loader.load();
        } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIIcon icon = new UIIcon("/Figuras/" + VariaveisGlobais.marca.toLowerCase().trim() + ".gif");
        icon.setHorizontalAlignment(UIIcon.ALIGN_STRETCH);
        icon.setVerticalAlignment(UIIcon.ALIGN_STRETCH);
        //icon.setPreserveAspectRatio(true);
        icon.setPreferredSize(100, 37);
        headerPanel.add(icon); //, headerPanelLayout.getConstraints(0, 0));
        headerPanel.add(wrappedRoot, UIBorderLayout.CENTER);

        addBorder(headerPanel, IAlignmentConstants.ALIGN_STRETCH, IAlignmentConstants.ALIGN_BOTTOM);

        // Setting up the Frame.
        UIBorderLayout blayout = new UIBorderLayout();
        setLayout(blayout);
        setTitle("jAdministrador");
        // Icone do Programa
        setIconImage(UIImage.getImage("/Figuras/icone.jpg"));

        add(headerPanel, UIBorderLayout.NORTH);
        setPreferredSize(VariaveisGlobais.screenWidth,VariaveisGlobais.screenHeight);
        setMaximumSize(VariaveisGlobais.screenWidth,VariaveisGlobais.screenHeight);
        setMinimumSize(VariaveisGlobais.screenWidth,VariaveisGlobais.screenHeight);
        setSize(VariaveisGlobais.screenWidth,VariaveisGlobais.screenHeight);

        // Login do Sistema
        UIPanel content = Login();
        add(content,UIBorderLayout.CENTER);
    }

    private void logoutSistema() {
        this.eventKeyTyped().addListener(event -> {
            denyLogOut = true;
        });
        denyLogOut = false;
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(10000),
                ae -> TimeExpiration()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void TimeExpiration() {
        if (denyLogOut) return;

        setMenuBar(null);

        // Login do Sistema
        UIPanel content = Login();
        add(content,UIBorderLayout.CENTER);
    }

    private UIPanel Login() {
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/entrada/Login2.fxml"));
        } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        this.desktopPanel = new UIDesktopPanel();
        VariaveisGlobais.desktopPanel = this.desktopPanel;

        UIInternalFrame internalFrame = new UIInternalFrame(this.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        //internalFrame.setPreferredSize(565, 289);
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.setIconifiable(false);
        internalFrame.setClosable(false);
        internalFrame.setTitle("Login do Sistema");
        internalFrame.setIconImage(UIImage.getImage("/Figuras/icone.jpg"));
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);

        internalFrame.pack();
        internalFrame.setVisible(true);

        UIPanel content = new UIPanel();
        UIBorderLayout uiBorderLayout = new UIBorderLayout();
        uiBorderLayout.setMargins(2,2,2,2);
        content.setPreferredSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight-38);
        content.setMaximumSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight-38);
        content.setMinimumSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight-38);
        content.setSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight-38);

        // BackGround do DeskTop
        BufferedImage bImage = null;
        try { bImage = ImageIO.read(getClass().getResource("/Figuras/Imagens/fundo2.jpg")); } catch (Exception e) {}
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {ImageIO.write(bImage, "jpg", bos );}catch (Exception e) {}
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        BufferedImage newBi = null;
        try {newBi = ImageIO.read(is); } catch (Exception e) {}
        BufferedImage img = resize(newBi,VariaveisGlobais.screenWidth,VariaveisGlobais.screenHeight - 38);
        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        try {ImageIO.write(img, "jpg", bos2 );}catch (Exception e) {}
        byte[] data2 = bos2.toByteArray();

        IImage image = new UIImage(data2);
        UIIcon icon = new UIIcon(image);
        icon.setHorizontalAlignment(UIIcon.ALIGN_STRETCH);
        icon.setVerticalAlignment(UIIcon.ALIGN_STRETCH);

        //icon.setPreserveAspectRatio(true);
        icon.setPreferredSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight);
        icon.setMaximumSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight);
        icon.setMinimumSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight);
        icon.setSize(VariaveisGlobais.screenWidth, VariaveisGlobais.screenHeight);

        IImage uiImage = icon.getImage();
        content.setBackgroundImage(uiImage);

        content.setLayout(uiBorderLayout);
        content.add(this.desktopPanel, UIBorderLayout.CENTER);

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            conn = VariaveisGlobais.conexao;
            internalFrame.close();

            // Ler Parametros Iniciais do sistema
/*
            new Calculos_mujucoep();
            new Calculos.Config().Config_ADM();
            new Calculos.Config().Config_AC();
            new Calculos.Config().Config_CA();
            new Calculos.Config().Config_BA();
            new Calculos.Config().Config_BB();
            new Calculos.Config().Config_Email();
            new Calculos.Config().Config_MsgProp();
*/

            // Menu do Sistema
            CriaMenu();
            setMenuBar(this.barraMenu);
        });

        return content;
    }

    private void CriaMenu() {
        UIMenuBar barraMenu = new UIMenuBar();
        UIMenu menu = null; UIMenuItem submenu = null;

        ResultSet mnu = null;
        String mnuSQL = "SELECT rot_id, rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close, menu, submenu FROM rotinas ORDER BY menu, submenu;";
        mnu = conn.AbrirTabela(mnuSQL, ResultSet.CONCUR_READ_ONLY);

        String rot_desc = "", rot_call = "", rot_shortcut = "", rot_icon = "", rot_options = "";
        int rot_look = 0; boolean resize = false, close = false;
        int imenu = -1, isubmenu = -1;
        try {
            while (mnu.next()) {
                rot_desc = TrocaNome(mnu.getString("rot_desc"));
                rot_call = mnu.getString("rot_call");
                rot_shortcut = mnu.getString("rot_shortcut");
                rot_icon = mnu.getString("rot_icon");
                rot_look = mnu.getInt("rot_look");
                rot_options = mnu.getString("rot_options");
                resize = mnu.getBoolean("resize");
                close = mnu.getBoolean("close");

                imenu = mnu.getInt("menu");
                isubmenu = mnu.getInt("submenu");

                Object[] ttmenu = new Object[] {rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close};
                Object[] sProtocolo = SeekInProtocol(imenu, isubmenu);
                if (isubmenu == 0) {
                    if (imenu > 0) {
                            if (menu.getName().equalsIgnoreCase("1")) barraMenu.add(menu);
                    }
                    menu = new UIMenu(ttmenu[0].toString());
                    menu.setName((Boolean) sProtocolo[0] ? "1" : "0");
                } else {
                    submenu = new UIMenuItem();
                    // Setar Icones
                    try{
                        submenu.setImage(UIImage.getImage("/Figuras/" + rot_icon.toString()));
                    } catch (Exception e) {}
                    submenu.setText(ttmenu[0].toString());

                    submenu.eventAction().addListener(() -> {
                        try {
                            if (ttmenu[1].toString().toLowerCase().contains("fxml")) {
                                ChamaTela(ttmenu[0].toString(), ttmenu[1].toString(), ttmenu[3].toString(),(((String)sProtocolo[1]).length() > 0 ? (String)sProtocolo[1] : null),(boolean)ttmenu[6], (Boolean)ttmenu[7]);
                            } else {
                                chamaClasse(ttmenu[1].toString());
                                //Class classe = Class.forName(ttmenu[1].toString());
                                //classe.getMethod("Inicializa",null).invoke(null,new Object[0]);
                            }
                        } catch (Exception ex) {ex.printStackTrace();}
                    });

                    if ((Boolean) sProtocolo[0]) menu.add(submenu);
                }
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(mnu);
        barraMenu.add(menu);

        //Cria o menu
        UIMenu menuSair = new UIMenu("Sair");

        // Aqui v�o os itens
        UIMenuItem itemSair = new UIMenuItem();
        itemSair.setText("Logout");
        itemSair.setImage(UIImage.getImage("/Figuras/logoff.png"));
        itemSair.eventAction().addListener(() -> {
            setMenuBar(null);

            // Login do Sistema
            UIPanel content = Login();
            add(content,UIBorderLayout.CENTER);
        });
        //itemSair.setAccelerator(Key); //new KeyCodeCombination(X, KeyCombination.SHORTCUT_DOWN));

        menuSair.add(itemSair);

        // Aqui v�o os itens
        UIMenuItem itemFechar = new UIMenuItem(); //"Encerrar o Sistema", UIImage.getImage("/Figuras/close.png"), (pEvent) -> System.exit(0));
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setImage(UIImage.getImage("/Figuras/close.png"));
        itemFechar.eventAction().addListener(() -> {System.exit(0);});
        //itemFechar.setAccelerator();

        menuSair.add(itemFechar);

        if (VariaveisGlobais.cargo.equalsIgnoreCase("GER")) {
            // Cria o menu SAMIC - GER
            UIMenu menuSamic = new UIMenu("SAMIC");

            // Aqui v�o os itens
            UIMenuItem ItemConc = new UIMenuItem();
            ItemConc.setText("Concecionárias");
            //itemFechar.setImage(UIImage.getImage("/Figuras/close.png"));
            ItemConc.eventAction().addListener(() -> {
                try {
                    ChamaTela("Concecionárias", "/Samic/Concecionarias.fxml", null,null,false, true);
                } catch (Exception ex) {ex.printStackTrace();}
            });

            menuSamic.add(ItemConc);

            //return menuBar;
            barraMenu.add(menuSamic);
        }

        //return menuBar;
        barraMenu.add(menuSair);

        this.barraMenu = barraMenu;
    }

    private void chamaClasse(String nomeClasse) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(nomeClasse);
            Method metodo = clazz.getDeclaredMethod("main");
            Object obj = clazz.newInstance();

            metodo.invoke(obj);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Object[] SeekInProtocol(int menu, int submenu) {
        boolean retorno = false; String viea = "";
        if (VariaveisGlobais.protocolo == null) {
            retorno = true;
            viea = "";
        } else {
            String tmpSeek = menu + "," + submenu;
            String[] aproto = VariaveisGlobais.protocolo.split(";");
            if (aproto.length > 0) {
                int npos = FuncoesGlobais.FindLike(aproto, tmpSeek);
                if (npos > -1) {
                    retorno = true;
                    int spos = aproto[npos].indexOf(":");
                    if (spos > -1) {
                        viea = aproto[npos].substring(spos + 1);
                    } else viea = "";
                }
            } else {
                retorno = true;
                viea = "";
            }
        }
        return new Object[] {retorno,viea};
    }

    private static void addBorder(IContainer pContainer, int pHorizontalAlignment, int pVerticalAlignment) {
        UIFormLayout layout = (UIFormLayout)pContainer.getLayout();
        IFormLayout.IConstraints constraints = layout.getConstraints(layout.getTopAnchor(), layout.getLeftAnchor(), layout.getBottomAnchor(), layout.getRightAnchor());

        UIIcon border = new UIIcon(UIImage.getImage("/images/border-pixel.png"));
        border.setHorizontalAlignment(pHorizontalAlignment);
        border.setVerticalAlignment(pVerticalAlignment);

        pContainer.add(border, constraints);
    }

    private Object[] LerMenuItens(String where) {
        ResultSet mnu = null;
        String mnuSQL = "SELECT rot_id, rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close FROM rotinas WHERE rot_id = " + where + " ORDER BY rot_id;";
        mnu = conn.AbrirTabela(mnuSQL, ResultSet.CONCUR_READ_ONLY);

        String rot_desc = "", rot_call = "", rot_shortcut = "", rot_icon = "", rot_options = "";
        int rot_look = 0; boolean resize = false, close = false;
        try {
            while (mnu.next()) {
                rot_desc = TrocaNome(mnu.getString("rot_desc"));
                rot_call = mnu.getString("rot_call");
                rot_shortcut = mnu.getString("rot_shortcut");
                rot_icon = mnu.getString("rot_icon");
                rot_look = mnu.getInt("rot_look");
                rot_options = mnu.getString("rot_options");
                resize = mnu.getBoolean("resize");
                close = mnu.getBoolean("close");
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(mnu);
        return new Object[] {rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close};
    }

    private void montaMenu(String uid) {
        //Cria a barra
        this.barraMenu = SetarMenu(uid);

        //Cria o menu
        UIMenu menuSair = new UIMenu("Sair");

        // Aqui v�o os itens
        UIMenuItem itemSair = new UIMenuItem();
        itemSair.setText("Logout");
        itemSair.setImage(UIImage.getImage("/Figuras/logoff.png"));
        itemSair.eventAction().addListener(() -> {
            setMenuBar(null);

            // Login do Sistema
            UIPanel content = Login();
            add(content,UIBorderLayout.CENTER);
        });
        //itemSair.setAccelerator(Key); //new KeyCodeCombination(X, KeyCombination.SHORTCUT_DOWN));

        menuSair.add(itemSair);

        // Aqui v�o os itens
        UIMenuItem itemFechar = new UIMenuItem();
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setImage(UIImage.getImage("/Figuras/close.png"));
        itemFechar.eventAction().addListener(() -> {System.exit(0);});
        //itemFechar.setAccelerator();

        menuSair.add(itemFechar);

        if (VariaveisGlobais.cargo.equalsIgnoreCase("GER")) {
            // Cria o menu SAMIC - GER
            UIMenu menuSamic = new UIMenu("SAMIC");

            // Aqui v�o os itens
            UIMenuItem ItemConc = new UIMenuItem();
            ItemConc.setText("Concecion�rias");
            //itemFechar.setImage(UIImage.getImage("/Figuras/close.png"));
            ItemConc.eventAction().addListener(() -> {
                try {
                    ChamaTela("Concecion�rias", "/Samic/Concecionarias.fxml", null,null,false, true);
                } catch (Exception ex) {ex.printStackTrace();}
            });

            menuSamic.add(ItemConc);

            //return menuBar;
            this.barraMenu.add(menuSamic);
        }

        //return menuBar;
        this.barraMenu.add(menuSair);
    }

    private UIMenuBar SetarMenu(String uid) {
        UIMenuBar barraMenu = new UIMenuBar();

        ResultSet mnu = null; String protocolo = "";
        try {
            mnu = conn.AbrirTabela("SELECT menu_id, user_id, menu_item FROM menu WHERE user_id = " + uid + " ORDER BY menu_id;", ResultSet.CONCUR_READ_ONLY);
            while (mnu.next()) {
                protocolo = mnu.getString("menu_item");
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(mnu);

        if (protocolo.isEmpty()) return null;

        UIMenu menu = null; UIMenu[] submenu = {}; UIMenuItem item = null; UIMenuItem menuItem = null;

        Object[] imenu = protocolo.split("@");
        for (Object iimenu : imenu) {
            submenu = new UIMenu[] {};
            Object[] umenu = iimenu.toString().split(";");
            if (umenu.length > 0) {
                Object[] tmenu = LerMenuItens(umenu[0].toString());
                menu = new UIMenu(tmenu[0].toString());

                if (umenu.length > 1) {
                    for (int i = 1; i < umenu.length; i++) {
                        Object[] menu_item = umenu[i].toString().split(",");

                        Object[] viea = menu_item[menu_item.length - 1].toString().split(":");
                        if (viea.length > 0) {
                            Object[] ttmenu = LerMenuItens(viea[0].toString());
                            if ((int)ttmenu[4] == 0) continue;

                            if (ttmenu[1] == null) {
                                // menu
                                UIMenu tsubmenu = new UIMenu(ttmenu[0].toString());
                                submenu = FuncoesGlobais.MenuArrayAdd(submenu, tsubmenu);
                            } else {
                                // item
                                item = new UIMenuItem();
                                // Setar Icones
                                try{
                                    item.setImage(UIImage.getImage("/Figuras/" + ttmenu[3].toString()));
                                } catch (Exception e) {}
                                item.setText(ttmenu[0].toString());

                                item.eventAction().addListener(() -> {
                                    try {
                                        if (ttmenu[1].toString().toLowerCase().contains("fxml")) {
                                            ChamaTela(ttmenu[0].toString(), ttmenu[1].toString(), ttmenu[3].toString(), (viea.length > 1 ? viea[1].toString() : null), (boolean) ttmenu[6], (Boolean) ttmenu[7]);
                                        } else {
                                            Class classe = Class.forName(ttmenu[1].toString());
                                            classe.getMethod("main", null).invoke(null,null);
                                        }
                                    } catch (Exception ex) {ex.printStackTrace();}
                                });


                                if (submenu.length > 0) {
                                    submenu[submenu.length - 1].add(item);
                                    for (int z = submenu.length - 1; z > 0; z--) {
                                        submenu[z - 1].add(submenu[z]);
                                    }

                                    menu.add(submenu[0]);
                                } else {
                                    menu.add(item);
                                }
                                // -> if (ttmenu[2] != null) item.setAccelerator(new KeyCodeCombination(getKeyCode(ttmenu[2].toString()), KeyCombination.SHORTCUT_DOWN));
                            }
                        }
                    }
                }

            }
            barraMenu.add(menu);
        }

        return barraMenu;
    }

    private void ChamaTela(String nome, String url, String icone, String viea, boolean resize, boolean close) throws IOException, Exception {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(this.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(false);
        internalFrame.setResizable(resize);
        internalFrame.setMaximizable(false);
        internalFrame.setIconifiable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(close);

        // TODO Caption and Borders
        //internalFrame.setBackground(new UIColor(103,165, 162));
        internalFrame.setBackground(new UIColor(221,221, 221));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                if (event.sparam[0].equals("PAGAMENTO")) {
                    return;
                }
                if (event.sparam[0].equals("RECEBIMENTO")) {
                    return;
                }
                //try { internalFrame.close(); } catch (NullPointerException e) { }
            } else {
                if (event.sparam[0].equals("PAGAMENTO")) {
                    return;
                }
                if (event.sparam[0].equals("RECEBIMENTO")) {
                    return;
                }
                if (event.sparam[0].toString().toUpperCase().equals("PROPRIETARIO") || event.sparam[0].toString().toUpperCase().equals("LOCATARIO")) {
                    try { internalFrame.close(); } catch (NullPointerException e) { }
                }
                if (event.sparam[0].toString().toUpperCase().equals("USUARIO") || event.sparam[0].toString().toUpperCase().equals("LOCATARIO")) {
                    try { internalFrame.close(); } catch (NullPointerException e) { }
                }
            }
        });

        try {
            root.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    KeyEvent newEvent = new KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", "\t", KeyCode.TAB, event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown());
                    Event.fireEvent(event.getTarget(), newEvent);
                    event.consume();
                }
            });
        } catch (Exception e) {}

        if (viea != null) root.fireEvent(new vieaEvent(viea,vieaEvent.GET_VIEA));
    }

    private String TrocaNome(String value) {
        for (int contas = 0; contas <= VariaveisGlobais.contas_ca.size() - 1; contas++) {
            value = value.replace("[" + VariaveisGlobais.contas_ca.key(contas) + "]", VariaveisGlobais.contas_ca.get(VariaveisGlobais.contas_ca.key(contas)));
        }
        return value;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
