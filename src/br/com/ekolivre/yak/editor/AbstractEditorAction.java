package br.com.ekolivre.yak.editor;

import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import static java.lang.System.*;

public abstract class AbstractEditorAction {
  public static interface Callback {
    void run();
  };
  
  private JComponent components[];
  
  protected abstract JComponent makeComponents()[];
  
  public JComponent getComponents()[] {
    if(components == null)
      components = makeComponents();
    return components;
  };
  
  protected JButton makeButton(String icon) {
    JButton res = new JButton();
    setIconForButton(res, icon);
    return res;
  };
  
  protected JButton makeButton(String icon, Callback callback) {
    JButton res = makeButton(icon);
    res.addActionListener(e -> callback.run());
    return res;
  };
  
  private void setIconForButton(JButton button, String icon) {
    try {
      URL url = ClassLoader.getSystemClassLoader().getResource(icon);
      
      button.setBorderPainted(false);
      button.setContentAreaFilled(false);
      
      button.setIcon(new ImageIcon(url));
      
    } catch(Throwable t) {
      out.printf("fail: %s%n", t);
    };
  };
};
