/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
* Copyright (C) Ekolivre TI, Paulo H. Torrens - 2013.                          *
* Ekolivre TI (http://www.ekolivre.com.br) claims rights over this software;   *
*   you may use for educational or personal uses. For comercial use (even as   *
*   a library), please contact the author.                                     *
********************************************************************************
* This file is part of Ekolivre's YAK.                                         *
*                                                                              *
* YAK is free software: you can redistribute it and/or modify it under the     *
*   terms of the GNU General Public License as published by the Free Software  *
*   Foundation, either version 3 of the License, or (at your option) any later *
*   version.                                                                   *
*                                                                              *
* YAK is distributed in the hope that it will be useful, but WITHOUT ANY       *
*   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  *
*   FOR A PARTICULAR PURPOSE. See the GNU General Public License for more      *
*   details.                                                                   *
*                                                                              *
* You should have received a copy of the GNU General Public License along with *
*   YAK.  If not, see <http://www.gnu.org/licenses/>.                          *
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
import br.com.ekolivre.yak.editor.actions.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.awt.event.KeyEvent.*;
import static javax.swing.SwingConstants.*;

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
      
      err.printf("We have a total of %d editor kits loaded.%n", list.size());
      
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
    
    addPopupMenu();
  };
  
  //
  private final void addPopupMenu() {
    JPopupMenu menu = new JPopupMenu();
    populate(menu);
    menu.pack();
    editor.setComponentPopupMenu(menu);
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
      
      Map<String, Integer> map = kit.getFileExtensions();
      
      if(map == null)
        continue;
      
      for(Map.Entry<String, Integer> e: map.entrySet()) {
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
    //return "text/x-c";
    // This will make us guess the source :3
    return "text/x-source-code";
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
    addAction(c, new UndoRedoAction());
  };
  
  /**
   *
   */
  private void addAction(JComponent c, AbstractEditorAction a) {
    
    JComponent children[] = a.makeComponents();
    if(children != null)
      for(JComponent x: children)
        if(c instanceof JPopupMenu && x instanceof JButton) {
          
          JButton b = (JButton)x;
          
          b.setText(b.getToolTipText());
          b.setToolTipText("");
          
          b.addActionListener(e -> c.setVisible(false));
          
          c.add(b);
          
        } else c.add(x);
    
  };
  
  /**
   *
   */
  public void getDisplayText(int pos, int len, Segment seg)
  throws BadLocationException {
    getDocument().getText(pos, len, seg);
  };
  
  @Override
  public final String toString() {
    return getKitName();
  };
  
  /**
   *
   */
  public String toHTML(File file) {
    
    try {
      /*List<Token> parse(CharSequence seg, int offset, int length,
                                      int limit, TokenState state);*/
      
      Scanner scanner = new Scanner(file);
  
      String str = "";
      while(scanner.hasNextLine())
        str += scanner.nextLine() + "\n";
      
      List<Token> tokens = parse(str, 0, str.length(), str.length() + 1, null);
      
      String html = "";
      
      int i = 0;
      for(Token t: tokens) {
        int j = t.start();
        
        if(i < j) {
          html += str.substring(i, j); //.replaceAll("\r\n?|\n", "<br>");
        };
        
        i = t.end();
        
        html += t.getType().toHTML(str.substring(j, i));
      };
      
      return String.format(
        "<code class=\"%s\" style=\"%s\"><pre>%s</pre></code>%n",
        (
          getDialect() == 0 ?
            getContentType()
          : getContentType()
        ).substring(5),
        "font-family: monospaced;",
        html);
    } catch(FileNotFoundException e) {
      err.printf("file not found: %s%n", file);
      exit(-1);
    };
    
    return null;
  };
  
  /**
   *
   */
  public static final void main(String... args) {
    for(String filename: args) {
      out.printf(getKitForFileName(filename).toHTML(new File(filename)));
    };
  };
};
