/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak;

import java.io.*;
import java.net.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Point;
import java.util.*;
import java.util.prefs.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.plaf.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.*;
import br.com.ekolivre.VT100;
import br.com.ekolivre.yak.editor.*;
import br.com.ekolivre.yak.editor.actions.*;
import static java.lang.System.*;
import static java.awt.BorderLayout.*;

/**
 *
 */
public final class Main extends JFrame {
  
  private final static String ICON = "accessories-code-editor.png";
  private final static String WINDOW_X = "x";
  private final static String WINDOW_Y = "y";
  private final static String WINDOW_WIDTH = "width";
  private final static String WINDOW_HEIGHT = "height";
  private final static String WINDOW_STATE = "state";
  
  private JSplitPane first;
  private JSplitPane second;
  
  private JMenuBar menu;
  
  private JToolBar toolbar;
  
  private JTree tree_data;
  private JScrollPane tree;
  private JTabbedPane tabs;
  private JPanel panel;
  private JLabel status;
  private VT100 terminal;
  
  private DropTarget target;
  
  private JPanel contents;
  
  private WeakHashMap<JEditorPane, File> hash = new WeakHashMap<>();
  private Preferences preferences = Preferences.userNodeForPackage(Main.class);
  
  private Main() {
    
    init();
    makeTree();
    makeTabs();
    makeConsole();
    
    makeStatusBar();
    
    makeFirstSlider();
    makeSecondSlider();
    //makeMenu();
    makeDropTarget();
    
    makeToolbar();
    
    setupIcon();
    
    setVisible(true);
    
    //
    //openFile(new File("test.aaa"));
    //openFile(new File("test.gnu"));
    //openFile(new File("test.krc"));
    //openFile(new File("test.lsp"));
    //openFile(new File("test.psy"));
    //openFile(new File("test.jav"));
    //openFile(new File("test.plc"));
    //openFile(new File("test.prg"));
    openUntitledFile("text/x-source-code");
    //
  };
  
  private void init() {
    //
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                       getAppName());
    System.setProperty("awt.useSystemAAFontSettings","on");
    System.setProperty("swing.aatext", "true");
    
    //
    setSize(
      preferences.getInt(WINDOW_WIDTH, 800),
      preferences.getInt(WINDOW_HEIGHT, 600)
    );
    
    int x = preferences.getInt(WINDOW_X, -1);
    int y = preferences.getInt(WINDOW_Y, -1);
    
    if(x < 0 || y < 0)
      setLocationRelativeTo(null);
    else setLocation(x, y);
    
    setExtendedState(preferences.getInt(WINDOW_STATE, getExtendedState()));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        preferences.putInt(WINDOW_WIDTH, getWidth());
        preferences.putInt(WINDOW_HEIGHT, getHeight());
        preferences.putInt(WINDOW_STATE, getExtendedState());
        try {
          preferences.flush();
        } catch(BackingStoreException foo) {
          //
        };
      };
      
      @Override
      public void componentMoved(ComponentEvent e) {
        preferences.putInt(WINDOW_X, getX());
        preferences.putInt(WINDOW_Y, getY());
        preferences.putInt(WINDOW_STATE, getExtendedState());
        try {
          preferences.flush();
        } catch(BackingStoreException foo) {
          //
        };
      };
    });
  };
  
  private void makeTree() {
    
    /*
    DefaultMutableTreeNode top =
        new DefaultMutableTreeNode("/");
    
    
    tree_data = new JTree(top);
    
    
    tree = new JScrollPane(tree_data);
    tree.setBorder(new BevelBorder(BevelBorder.LOWERED));
    
    tree.setPreferredSize(new Dimension(0, 0));
    tree.setMinimumSize(new Dimension(0, 0));
    */
  };
  
  private void makeTabs() {
    
    tabs = new JTabbedPane();
    //tabs.setBorder(new BevelBorder(BevelBorder.LOWERED));
    tabs.setBorder(null);
    
    add(tabs, BorderLayout.CENTER);
    
    
    
    tabs.addChangeListener(e -> {
      JScrollPane scroll = (JScrollPane)tabs.getSelectedComponent();
      
      JEditorPane editor = (JEditorPane)scroll.getViewport().getView();
      
      DefaultSyntaxKit kit = (DefaultSyntaxKit)editor.getEditorKit();
      
      makeToolbar(kit, editor);
      
      editor.requestFocusInWindow();
      
      SyntaxDocument doc = (SyntaxDocument)editor.getDocument();
      
      int caret = editor.getCaretPosition();
      
      Point p = doc.getPointForPos(caret);
      
      updateStatus(p.x, p.y, caret);
      
    });
    
    
  };
  
  private void makeConsole() {
    
    
    
    //terminal = new VT100();
    
    
  };
  
  private void makeStatusBar() {
    panel = new JPanel();
    panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    
    panel.setPreferredSize(new Dimension(getWidth(), 18));
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    
    status = new JLabel();
    status.setHorizontalAlignment(SwingConstants.LEFT);
    panel.add(status);
    
    add(panel, SOUTH);
    
  };
  
  private void updateStatus(CaretEvent e) {
    
    
    int dot = e.getDot();
    
    
    JScrollPane scroll =
      (JScrollPane)tabs.getComponentAt(tabs.getSelectedIndex());
    
    JEditorPane editor = (JEditorPane)scroll.getViewport().getView();
    
    SyntaxDocument doc = (SyntaxDocument)editor.getDocument();
    
    Point p = doc.getPointForPos(dot);
    
    updateStatus(p.x, p.y, dot);
    
  };
  
  private void updateStatus(int x, int y, int dot) {
    status.setText(String.format(getStatusFormat(), x, y, dot));
  };
  
  private String getStatusFormat() {
    return "Caret: [%2$d, %1$d]";
  };
  
  private void makeFirstSlider() {
    /*
    first = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, tabs);
    first.setBorder(null);
    */
  };
  
  private void makeSecondSlider() {
    /*
    second = new JSplitPane(JSplitPane.VERTICAL_SPLIT, first, terminal);
    second.setResizeWeight(1);
    second.setBorder(null);
    
    add(second);*/
  };
  
  private void makeMenu() {
    menu = new JMenuBar();
    
    menu.add(new JMenu("File"));
    
    addLanguageListToMenu(menu.add(new JMenu("Lexer")));
    
    setJMenuBar(menu);
    
  };
  
  @SuppressWarnings("unchecked")
  private void makeDropTarget() {
    
    target = new DropTarget() {
      @Override
      public void drop(DropTargetDropEvent e) {
        try {
          Transferable t = e.getTransferable();
          
          e.acceptDrop(e.getDropAction());
            
          java.util.List<File> list = (java.util.List)t.getTransferData(
            DataFlavor.javaFileListFlavor
          );
          
          for(File file: list)
            if(file.isFile() && file.canRead())
              openFile(file);
          
          e.dropComplete(true);
        } catch(IOException foo) {
          e.rejectDrop();
          out.printf("IOException!%n");
          return;
        } catch(UnsupportedFlavorException bar) {
          e.rejectDrop();
          out.printf("UnsupportedFlavorException!%n");
          return;
        };
        
      };
    };
    
  };
  
  private void makeToolbar() {
    makeToolbar(null, null);
  };
  
  private void makeToolbar(DefaultSyntaxKit kit, JEditorPane com) {
    
    if(toolbar != null) {
      toolbar.removeAll();
    } else {
      toolbar = new JToolBar();
      add(toolbar, BorderLayout.PAGE_START);
    };
      
    
    //toolbar.setRollover(false);
    toolbar.setFloatable(false);
    
    
    appendAction(new NewFileAction(this::openUntitledFile));
    appendAction(new OpenFileAction(this, this::openFile));
    if(kit != null) {
      appendAction(new SaveFileAction(this, kit, hash.get(com),
                                      this::saveActiveTab));
      //appendAction(new PrintFileAction(this::printActiveTab));
      
      appendSeparator();
      
      kit.populate(toolbar);
      
    };
    
    //
    toolbar.add(Box.createHorizontalGlue());
    
    appendSeparator();
    appendAction(new ConfigAction(this::openConfig));
    
    if(kit != null)
      appendAction(new ChoseLanguageAction(kit.getContentType(),
                                           kit.getDialect(),
                                           this::setContentOfActiveTab));
    
    
    
    
  };
  
  private void appendSeparator() {
    toolbar.addSeparator();
  };
  
  private void appendAction(AbstractEditorAction action) {
    
    JComponent components[] = action.getComponents();
    
    if(components != null)
      for(JComponent i: components)
        if(i != null)
          toolbar.add(i);
    
  };
  
  private void setupIcon() {
    
    URL url = ClassLoader.getSystemClassLoader().getResource(ICON);
    
    ImageIcon ico = new ImageIcon(url);
    
    
    
    
    try {
      Class<?> Application = Class.forName("com.apple.eawt.Application");
      Method getApplication = Application.getDeclaredMethod("getApplication");
      Object app = getApplication.invoke(null, (Object[])null);
      Method setDockIconImage =
        Application.getDeclaredMethod("setDockIconImage", java.awt.Image.class);
      setDockIconImage.invoke(app, ico.getImage());
    } catch(Throwable t) {
      setIconImage(ico.getImage());
    };
    
    
  };
  
  private void addLanguageListToMenu(JMenu menu) {
    //
    List<? super DefaultSyntaxKit> kits =
      DefaultSyntaxKit.getArbitraryKitsList();
  };
  
  private void openUntitledFile() {
    
    
    
    JScrollPane scroll = new JScrollPane();
    JEditorPane editor = new JEditorPane();
    
    hash.put(editor, null);
    
    scroll.setViewportView(editor);
    scroll.setPreferredSize(new Dimension(0, 0));
    
    editor.setContentType(DefaultSyntaxKit.defaultContentType());
    editor.setDropTarget(target);
    editor.setDoubleBuffered(true);
    
    /*editor.addInputMethodListener(new InputMethodListener() {
      @Override
      public void caretPositionChanged(InputMethodEvent event) {
        
      };
      @Override
      public void inputMethodTextChanged(InputMethodEvent event) {
        out.printf("inputMethodTextChanged()! %s%n", event);
      };
    });*/
    
    
    tabs.addTab(getUntitledName(), null, scroll, /* hint */ null);
    tabs.setSelectedIndex(tabs.getTabCount() - 1);
    
    addCloseButtonForTab(tabs.getTabCount() - 1, getUntitledName());
    
    editor.requestFocusInWindow();
    updateStatus(1, 1, 0);
  };
  
  static class TabComponent extends JPanel {
    
    JLabel label;
    JButton button;
    
    TabComponent(String title) {
      super(new GridBagLayout());
      setOpaque(false);
      
      label = new JLabel(title);
      
      
      URL url = ClassLoader.getSystemClassLoader()
                           .getResource("icons/close-active.png");
      
      button = new JButton();
      
      button.setBorder(null);
      button.setBorderPainted(false);
      button.setContentAreaFilled(false);
      
      button.setIcon(new ImageIcon(url));
      
      button.setMargin(new Insets(-5, 0, -5, -5));
      
      add(label);
      add(button);
      
    };
    
    
  };
  
  void addCloseButtonForTab(int index, String title) {
    tabs.setTabComponentAt(index, new TabComponent(title));
  };
  
  private void openUntitledFile(String content_type) {
    openUntitledFile();
    setContentTypeOfTab(tabs.getTabCount() - 1, content_type);
  };
  
  private void openUntitledFile(String content_type, int dialect) {
    openUntitledFile();
    
    setContentTypeOfTab(tabs.getTabCount() - 1, content_type, dialect);
  };
  
  private void openFile(File file) {
    JScrollPane scroll = new JScrollPane();
    JEditorPane editor = new JEditorPane();
    
    editor.addCaretListener(e -> updateStatus(e));
    
    scroll.setViewportView(editor);
    scroll.setPreferredSize(new Dimension(0, 0));
    
    
    //editor.setDropTarget(target);
    editor.setDoubleBuffered(true);
    
    hash.put(editor, file);
    
    if(setContentTypeAndLoad(editor, file)) {
      
      tabs.addTab(file.getName(), null, scroll, file.getPath());
      int i = tabs.getTabCount() - 1;
      
      tabs.setSelectedIndex(i);
      addCloseButtonForTab(i, file.getName()); 
      
      editor.requestFocusInWindow();
      updateStatus(1, 1, 0);
    };
    
  };
  
  private boolean setContentTypeAndLoad(JEditorPane editor, File file) {
    
    try {
      setContentTypeForFile(editor, file);
      editor.read(new FileReader(file), null);
    } catch(Throwable t) {
      return false;
    };
    
    
    
    
    return true;
  };
  
  private void setContentTypeForFile(JEditorPane editor, File file) {
    setContentTypeForFile(editor, file.getName());
  };
  
  private void setContentTypeForFile(JEditorPane editor, String file) {
    editor.setEditorKit(DefaultSyntaxKit.getKitForFileName(file));
  };
  
  private void setContentTypeOfTab(int i, String lang) {
    setContentTypeOfTab(i, lang, null);
  };
  
  private void setContentTypeOfTab(int i, String lang, Integer dialect) {
    
    JScrollPane scroll = (JScrollPane)tabs.getComponentAt(i);
    JEditorPane editor = (JEditorPane)scroll.getViewport().getView();
    
    try {
      String text = editor.getText();
      editor.setContentType(lang);
      editor.read(new StringReader(text), lang);
    } catch(Throwable t) {
      //
    };
    
    DefaultSyntaxKit kit = (DefaultSyntaxKit)editor.getEditorKit();
    
    if(dialect != null)
      kit.setDialect(dialect);
    
    makeToolbar(kit, editor);
    
    editor.requestFocusInWindow();
  };
  
  private void saveActiveTab(File file) {
    out.printf("saving active tab on [%s]%n", file.getAbsolutePath());
    
    int i = tabs.getSelectedIndex();
    
    JScrollPane scroll = (JScrollPane)tabs.getComponentAt(i);
    JEditorPane editor = (JEditorPane)scroll.getViewport().getView();
    
    tabs.setTitleAt(i, file.getName());
    
  };
  
  private void printActiveTab() {
    
  };
  
  private void openConfig() {
    
  };
  
  private void setContentOfActiveTab(String kit, Integer dialect) {
    int i = tabs.getSelectedIndex();
    
    if(i < 0)
      return;
    
    if(dialect == null)
      setContentTypeOfTab(i, kit);
    else setContentTypeOfTab(i, kit, dialect);
  };
  
  private String getUntitledName() {
    return "Untitled";
  };
  
  private String getAppName() {
    return "YAK Code Editor";
  };
  
  public static void main(String args[]) {
    //
    DefaultSyntaxKit.load();
    
    //
    try {
      JFrame.setDefaultLookAndFeelDecorated(true);
      UIManager.setLookAndFeel(
        //"javax.swing.plaf.metal.MetalLookAndFeel"
        //"com.sun.java.swing.plaf.motif.MotifLookAndFeel"
        //"javax.swing.plaf.synth.SynthLookAndFeel"
        UIManager.getSystemLookAndFeelClassName()
      );
    } catch(Throwable t) {
      //out.println(t);
    };
    
    //
    java.awt.EventQueue.invokeLater(() -> {
      //
      try {
				Main main = new Main();
			} catch (Exception e) {
			  e.printStackTrace();
			  System.exit(2);
			};
		});
  };
};
