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
