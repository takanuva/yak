package br.com.ekolivre;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import static java.lang.System.*;

public class VT100 extends JScrollPane
implements Runnable, KeyListener, MouseListener, ActionListener {
  
  private static final int MINIMUM_VISIBLE_LINES = 5;
  private static final int VERTICAL_MARGIN = 4;
  private static final int HORIZONTAL_MARGIN = 4;
  
  private Thread thread;
  private JTextPane txt;
  private DefaultStyledDocument doc;
  
  public VT100() {
    
    init();
    initTextHelper();
    
    thread = new Thread(this);
    thread.start();
  };
  
  private void init() {
    setBorder(new BevelBorder(BevelBorder.LOWERED));
  };
  
  private void initTextHelper() {
    
    doc = new DefaultStyledDocument();
    txt = new JTextPane(doc) {
      /*@Override
      public void cut() {
        if(text.getCaretPosition() < cmdStart) {
          super.copy();
        } else {
          super.cut();
        };
      };
      
      @Override
      public void paste() {
        forceCaretMoveToEnd();
        super.paste();
      };*/
      
    };
    
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        
        Dimension d = c.getSize();
        
        d.width -= d.width % 20;
        d.height -= d.height % 20;
        
        c.setSize(d);
        
      };
    });
    
    txt.setBackground(Color.BLACK);
    txt.setForeground(Color.GREEN);
    
    txt.setCaretColor(Color.GREEN);
    
    Font font = new Font("Monospaced", Font.PLAIN, 12);
    txt.setText("> ");
    txt.setFont(font);
    txt.setMargin(new Insets(VERTICAL_MARGIN, HORIZONTAL_MARGIN,
                             VERTICAL_MARGIN, HORIZONTAL_MARGIN));
    setViewportView(txt);
    
    txt.addKeyListener(this);
    txt.addMouseListener(this);
    
    
    FontMetrics metrics = getFontMetrics(font);
    
    setTerminalSize(metrics.charWidth('A'), metrics.getHeight());
    
  };
  
  
  void setTerminalSize(int cw, int ch) {
    
    Dimension d = new Dimension(0, ch * MINIMUM_VISIBLE_LINES +
                                   VERTICAL_MARGIN * 2);
    
    setMinimumSize(new Dimension(0, 0));
    
    
    setPreferredSize(d);
    
    
  };
  
  
  
  
  
  
  @Override
  public void run() {};
  
  @Override
  public void keyReleased(KeyEvent e) {};
  
  @Override
  public void keyPressed(KeyEvent e) {};
  
  @Override
  public void keyTyped(KeyEvent e) {};
  
  @Override
  public void mouseExited(MouseEvent e) {};
  
  @Override
  public void mouseEntered(MouseEvent e) {};
  
  @Override
  public void mouseReleased(MouseEvent e) {};
  
  @Override
  public void mousePressed(MouseEvent e) {};
  
  @Override
  public void mouseClicked(MouseEvent e) {};
  
  @Override
  public void actionPerformed(ActionEvent e) {};
  
  
};
