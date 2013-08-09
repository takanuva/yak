/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*******************************************************************************/
package br.com.ekolivre.yak.editor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public interface AbstractTokenType {
  public static final short BOLD = 0x01;
  public static final short ITALIC = 0x02;
  public static final short TEOL = 0x04;
  
  public int compareTo(AbstractTokenType other);
  public boolean isComment();
  public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                   Dimension size, boolean invert);
};
