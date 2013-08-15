/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.Shape;
import java.util.jar.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.awt.event.KeyEvent.*;

/**
 * This is the base class used for handling tokenized text files. Children will
 * populate this class in order to define colors for each part of the text.
 */
public abstract class DefaultSyntaxKit extends StyledEditorKit
implements Comparable<DefaultSyntaxKit>, ViewFactory, KeyListener {
  /// Path to look for children kits, in order to automatically register them
  private static final String KIT_PACKAGE = "br/com/ekolivre/yak/editor/kits";
  
  /// The list of registered subclasses
  private static final List<DefaultSyntaxKit> list = new ArrayList<>();
  
  /// The current dialect of this kit (0: none)
  private int dialect;
  
  /// The editor whose kit this belongs to
  private JEditorPane editor = null;
  
  /// The skin this kit is using for graphical display
  private SyntaxSkin skin = SyntaxSkin.NOTEBOOK;
  
  // When this class is loaded into the VM, seek for children
  static {
    load();
  };
  
  /**
   *
   */
  public static final List<DefaultSyntaxKit> getArbitraryKitsList() {
    return unmodifiableList(list);
  };
  
  /**
   *
   */
  public static synchronized final void load() {
    // Checks if we have already loaded everything
    if(!list.isEmpty())
      return;
    
    // Gets the URL for the kit package
    URL url = DefaultSyntaxKit.class.getClassLoader().getResource(KIT_PACKAGE);
    try {
      String sub = url.getPath().substring(5, url.getPath().indexOf("!"));
      
      JarFile jar = new JarFile(sub);
      
      Enumeration<JarEntry> entries = jar.entries();
      
      while(entries.hasMoreElements()) {
        String path = entries.nextElement().getName();
        if(path.startsWith(KIT_PACKAGE) && path.endsWith(".class")) {
          
          String name = path.replace("\\", ".").replace("/", ".");
          name = name.substring(0, name.length() - 6);
          
          try {
            Class klass = Class.forName(name);
            
            if(DefaultSyntaxKit.class.isAssignableFrom(klass)) {
              
              DefaultSyntaxKit kit = (DefaultSyntaxKit)klass.newInstance();
              String type = kit.getContentType();
              if(type != null) {
                
                JEditorPane.registerEditorKitForContentType(
                  kit.getContentType(), name
                );
                  
                list.add(kit);
                
              };
            };
            
          } catch(ClassNotFoundException |
                  InstantiationException |
                  IllegalAccessException e) {
            continue;
          };
        };
      };
      
      Collections.sort(list);
      
      out.printf("We have a total of %d editor kits loaded.%n", list.size());
      
    } catch(IOException e) {
      //
    } catch(NullPointerException e) {
      //
    };
  };
  
  public DefaultSyntaxKit() {
    setDialect(getDefaultDialect());
  };
  
  //
  @Override
  public void install(JEditorPane editor) {
    super.install(editor);
    
    editor.setFont(getDefaultFont());
    editor.setCaret(createCaret());
    editor.addKeyListener(this);
    
    assert this.editor == null;
    
    this.editor = editor;
    
    //lines = new LineNumberPanel(editor);
    
  };
  
  //
  @Override
  public void deinstall(JEditorPane editor) {
    super.deinstall(editor);
    editor.removeKeyListener(this);
    assert(this.editor == editor);
    this.editor = null;
  };
  
  //
  public JEditorPane getEditor() {
    return editor;
  };
  
  //
  public void setEditor(JEditorPane editor) {
    this.editor = editor;
  };
  
  //
  public SyntaxSkin getSkin() {
    return skin;
  };
  
  //
  public static DefaultSyntaxKit getKitForFileName(String file) {
    int i = file.indexOf('.');
    return getKitForFileExtension(i > 0 ? file.substring(i + 1) : file);
  };
  
  //
  public static DefaultSyntaxKit getKitForContentType(String type) {
    return (DefaultSyntaxKit)JEditorPane.createEditorKitForContentType(type);
  };
  
  //
  public static DefaultSyntaxKit getKitForFileExtension(String ext) {
    
    for(DefaultSyntaxKit kit: getArbitraryKitsList()) {
      for(Map.Entry<String, Integer> e: kit.getFileExtensions().entrySet()) {
        if(ext.equals(e.getKey())) {
          //
          DefaultSyntaxKit res = getKitForContentType(kit.getContentType());
          
          //
          if(e.getValue() != null)
            res.setDialect(e.getValue());
          
          //
          return res;
        };
      };
    };
    
    return getKitForContentType(defaultContentType());
  };
  
  //
  public static DefaultSyntaxKit getKitGuessingFileContents(Segment seg) {
    
    // TODO: some malefic algorithm for guessing a programming language
    
    return getKitForContentType("text/plain");
  };
  
  //
  @Override
  public View create(Element element) {
    return new SyntaxView(element, this);
  };
  
  //
  @Override
  public ViewFactory getViewFactory() {
    return this;
  };
  
  //
  @Override
	public Document createDefaultDocument() {
		return new SyntaxDocument(this);
	};
  
  //
  @Override
  public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == VK_ALT)
      e.getComponent().repaint();
  };
  
  //
  @Override
  public void keyReleased(KeyEvent e) {
    if(e.getKeyCode() == VK_ALT)
      e.getComponent().repaint();
  };
  
  //
  @Override
  public void keyTyped(KeyEvent e) {
    return;
  };
  
  //
  @Override
  public Caret createCaret() {
    return new CustomCaret();
  };
  
  //
  public int getFontSize() {
    return 12;
  };
  
  //
  public Font getDefaultFont() {
    return new Font("Monospaced", Font.PLAIN, getFontSize());
  };
  
  //
  public static String defaultContentType() {
    //return "text/plain";
    return "text/x-c";
  };
  
  //
  @Override
  public int compareTo(DefaultSyntaxKit k) {
    return getKitName().compareTo(k.getKitName());
  };
  
  //
  public abstract Map<Integer, String> getDialectList();
  
  //
  public int getDialect() {
    return dialect;
  };
  
  //
  public void setDialect(int dialect) throws InvalidDialectException {
    this.dialect = dialect;
  };
  
  //
  protected boolean isDialect(int dialect) {
    if((getDialect() & dialect) > 0)
      return true;
    return false;
  };
  
  //
  public abstract String getKitName();
  
  //
  @Override
  public abstract String getContentType();
  
  //
  public abstract int getDefaultDialect();
  
  //
  public abstract Map<String, Integer> getFileExtensions();
  
  //
  public String extensionsAsArray()[] {
    Map<String, Integer> map = getFileExtensions();
    
    if(map == null) {
      return new String[0];
    };
    
    String res[] = new String[map.size()];
    
    int i = 0;
    for(String item: map.keySet())
      res[i++] = item;
    
    return res;
  };
  
  //
  public abstract List<Token> parse(CharSequence seg, int offset, int length,
                                    int limit, TokenState state);
  
  public SyntaxDocument getDocument() {
    return (SyntaxDocument)editor.getDocument();
  };
  
  /**
   * Populates the given component with buttons for actions which can be done
   * with the editor whose kit this is. Will internally be used to make a right
   * click menu with actions such as Copy, Paste, Indent Selection, and etc, but
   * may be used to make toolbars or similar as well.
   * 
   * @param c The component to add the items.
   */
  public void populate(JComponent c) {
    
  };
  
  public void getDisplayText(int pos, int len, Segment seg)
  throws BadLocationException {
    getDocument().getText(pos, len, seg);
  };
  
  @Override
  public String toString() {
    return getKitName();
  };
};
