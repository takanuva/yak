/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU GPL.                                                            *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
*                                                                              *
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

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import static java.lang.System.*;

public abstract class SyntaxSkin {
  public static final SyntaxSkin SCITE = new SyntaxSkin() {
    
    private final int SIDE_SIZE = 11;
    private final Color SIDE_COLOR = new Color(0xEDEDED);
    private final Color BACK_COLOR = new Color(0xBEBEBE);
    private final Color LINE_COLOR = new Color(0x000000);
    private final Color FONT_COLOR = new Color(0x000000);
    private final Color AREA_COLOR = new Color(0x99BBFF);
    private final Color WHSP_COLOR = new Color(0xCCCCCC);
    
    private final int INSET = 15;
    
    @Override
    public int getGutterWidth(SyntaxView v) {
      try {
        return INSET * 2 + v.metrics.charWidth('0') * numberOfGutterDigits(v) +
               SIDE_SIZE;
      } catch(Throwable t) {
        //
      };
      
      return 0;
    };
    
    @Override
    public void drawGutterBase(Graphics g, SyntaxView v) {
      
      int w = getGutterWidth(v) - SIDE_SIZE;
      int h = v.getContainer().getHeight();
      
      g.setColor(SIDE_COLOR);
      g.fillRect(w, 0, SIDE_SIZE, h);
      g.setColor(BACK_COLOR);
      g.fillRect(0, 0, w, h);
      
    };
    
    @Override
    public void drawLimitArea(Graphics g, SyntaxView v) {
      g.setColor(AREA_COLOR);
      
      int x = getGutterWidth(v) + v.metrics.charWidth(' ') * v.limitPosition();
      
      g.fillRect(x, 0, 1, v.getContainer().getHeight());
    };
    
    @Override
    public void drawLineNumber(Graphics g, SyntaxView v, int n, int y) {
      
      g.setFont(g.getFont().deriveFont(0));
      g.setColor(FONT_COLOR);
      
      String format = "%" + Integer.toString(numberOfGutterDigits(v)) + "d";
      
      
      
      g.drawString(String.format(format, n), INSET, y);
      
    };
    
    @Override
    public int drawStartOfLine(Segment s, int x, int y, Graphics g,
                               SyntaxView e, Dimension size, boolean invert) {
      //
      FontMetrics m = g.getFontMetrics();
      int a = m.getAscent();
      int h = a + m.getDescent();
      int w = Utilities.getTabbedTextWidth(s, m, x, e, 0);
      
      //
      y -= a;
      
      //
      if(invert)
        g.setColor(Color.DARK_GRAY);
      else g.setColor(Color.LIGHT_GRAY);
      
      int z = x + w;
      
      for(float i = e.nextTabStop(x - 2, 0); i < z; i = e.nextTabStop(i, 0)) {
      
        for(int j = y % 2; j < h; j += 2)
          g.fillRect((int)i + 1, y + j, 1, 1);
        
      };
      
      //
      return drawWhitespaces(s, x, y + a, g, e, size, invert);
    };
    
    @Override
    public int drawWhitespaces(Segment s, int x, int y, Graphics g,
                               SyntaxView e, Dimension size, boolean invert) {
      
      FontMetrics m = g.getFontMetrics();
      int a = m.getAscent();
      int h = a + m.getDescent();
      
      
      return x + Utilities.getTabbedTextWidth(s, m, x, e, 0);
      /*
      //
      //int w = Utilities.getTabbedTextWidth(s, m, x, e, 0);
      int w = m.charWidth(' ');
      
      //
      g.setColor(WHSP_COLOR);
      
      //
      int z = w / 4;
      
      //
      for(int i = 0; i < s.count; i++) {
        char c = s.array[s.offset + i];
        
        switch(c) {
          case ' ': {
            //
            g.fillOval(x + (w - z * 2) / 2, y - (h / 2 - z), z * 2, z * 2);
            
            x += w;
          } break;
          case '\t': {
            //
            int p = (int)e.nextTabStop(x, 0) - x;
            
            assert p % w == 0;
            
            if(p == w) {
              //
              g.fillRect(x + 2, y - (h / 2 - z / 2), w - 4, z);
              g.fillRect(x + (w - z) / 2, y - a + 2, z, h - 4);
              
              x += w;
            } else {
              //
              g.fillRect(x + 3, y - (h / 2 - z / 2), w - 3, z);
              
              p -= w;
              x += w;
              
              while(p > w) {
                //
                g.fillRect(x, y - (h / 2 - z / 2), w, z);
                
                p -= w;
                x += w;
              };
              
              //
              g.fillRect(x, y - (h / 2 - z / 2), w - 2, z);
              g.fillRect(x + (w - z) / 2, y - a + 2, z, h - 4);
              
              p -= w;
              x += w;
            };
            
          } break;
        
          default: assert false;
        };
      };
      
      
      //
      return x;*/
    };
  };
  
  public static final SyntaxSkin NOTEBOOK = new SyntaxSkin() {
    
    private final int SIDE_SIZE = 25;
    private final Color FONT_COLOR = new Color(0x000000);
    private final Color LINE_COLOR = new Color(0xEBF2F5);
    private final Color AREA_COLOR = new Color(0xEFC7C0);
    
    private final int INSET = 5;
    
    @Override
    public int getGutterWidth(SyntaxView v) {
      try {
        return v.metrics.charWidth('0') * 7;
      } catch(Throwable t) {
        //
      };
      return 0;
    };
    
    @Override
    public void drawGutterBase(Graphics g, SyntaxView v) {
      
      int w = getGutterWidth(v);
      int h = v.metrics.getHeight();
      
      
      Rectangle rect = g.getClipBounds();
      
      g.setColor(LINE_COLOR);
      for(int y = (rect.y / h) * h - 3; y < rect.y + rect.height; y += h) {
        g.fillRect(0, y, v.getContainer().getWidth(), 1);
      };
      
      g.setColor(AREA_COLOR);
      g.fillRect(w - 4, rect.y, 1, rect.height);
      g.fillRect(w - 4 - v.metrics.charWidth('0') / 2, rect.y, 1, rect.height);
      
      //
      //
      
    };
    
    @Override
    public void drawLimitArea(Graphics g, SyntaxView v) {
      
      g.setColor(AREA_COLOR);
      
      int x = getGutterWidth(v) + v.metrics.charWidth(' ') * v.limitPosition();
      int h = v.metrics.getHeight();
      
      Rectangle rect = g.getClipBounds();
      
      for(int y = (rect.y / h) * h - 2; y < rect.y + rect.height; y += h) {
        g.fillRect(x, y + 2, 1, h - 4);
      };
      
    };
    
    @Override
    public void drawLineNumber(Graphics g, SyntaxView v, int n, int y) {
      
      g.setFont(g.getFont().deriveFont(0));
      g.setColor(FONT_COLOR);
      
      g.drawString(String.format("%5d.", n), 0, y);
    };
    
    @Override
    public int drawStartOfLine(Segment s, int x, int y, Graphics g,
                               SyntaxView e, Dimension size, boolean invert) {
      /*//
      FontMetrics m = g.getFontMetrics();
      int a = m.getAscent();
      int h = a + m.getDescent();
      int w = Utilities.getTabbedTextWidth(s, m, x, e, 0);
      
      //
      return x + w;*/
      return SCITE.drawStartOfLine(s, x, y, g, e, size, invert);
    };
    
    @Override
    public int drawWhitespaces(Segment s, int x, int y, Graphics g,
                               SyntaxView e, Dimension size, boolean invert) {
      return SCITE.drawWhitespaces(s, x, y, g, e, size, invert);
    };
  };
  
  protected int numberOfGutterDigits(SyntaxView v) {
    return Integer.toString(v.getNumberOfLines()).length();
  };
  
  public abstract int getGutterWidth(SyntaxView v);
  public abstract void drawGutterBase(Graphics g, SyntaxView v);
  public abstract void drawLimitArea(Graphics g, SyntaxView v);
  public abstract void drawLineNumber(Graphics g, SyntaxView v, int n, int y);
  public abstract int drawStartOfLine(Segment s, int x, int y, Graphics g,
                                      SyntaxView e, Dimension size,
                                      boolean invert);
  public abstract int drawWhitespaces(Segment s, int x, int y, Graphics g,
                                      SyntaxView e, Dimension size,
                                      boolean invert);
};
