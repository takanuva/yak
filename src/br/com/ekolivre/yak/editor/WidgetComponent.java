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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import static javax.swing.SwingConstants.*;

/**
 * Thanks to http://www.jroller.com/santhosh/entry/resizable_components for the
 * base to implement this. Thanks to the author. :)
 */
public class WidgetComponent extends JComponent {
  
  private static class ResizableBorder implements Border {
    final static int locations[] = {
      NORTH,
      SOUTH,
      WEST,
      EAST,
      NORTH_WEST,
      NORTH_EAST,
      SOUTH_WEST,
      SOUTH_EAST,
      0,
      -1
    };
    
    final static int cursors[] = {
      Cursor.N_RESIZE_CURSOR,
      Cursor.S_RESIZE_CURSOR,
      Cursor.W_RESIZE_CURSOR,
      Cursor.E_RESIZE_CURSOR,
      Cursor.NW_RESIZE_CURSOR,
      Cursor.NE_RESIZE_CURSOR,
      Cursor.SW_RESIZE_CURSOR,
      Cursor.SE_RESIZE_CURSOR,
      Cursor.MOVE_CURSOR,
      Cursor.DEFAULT_CURSOR
    };
    
    private int distance;
    
    private ResizableBorder(int distance) {
      this.distance = distance;
    };
    
    public Insets getBorderInsets(Component c) {
      return new Insets(distance, distance, distance, distance);
    };
    
    @Override
    public boolean isBorderOpaque() {
      return false;
    };
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w,
                            int h) {
      //
      
    };
    
    //
    private Rectangle getRect(int x, int y, int w, int h, int l) {
      switch(l) {
        case NORTH:
          return new Rectangle(x + w / 2 - distance / 2 ,
                               y,
                               distance,
                               distance);
        case SOUTH:
          return new Rectangle(x + w / 2 - distance / 2,
                               y + h - distance,
                               distance,
                               distance);
        case WEST:
          return new Rectangle(x,
                               y + h / 2 - distance / 2,
                               distance,
                               distance);
        case EAST:
          return new Rectangle(x + w - distance,
                               y + h / 2 - distance / 2,
                               distance,
                               distance);
        case NORTH_WEST:
          return new Rectangle(x,
                               y,
                               distance,
                               distance);
        case NORTH_EAST:
          return new Rectangle(x + w - distance,
                               y,
                               distance,
                               distance);
        case SOUTH_WEST:
          return new Rectangle(x,
                               y + h - distance,
                               distance,
                               distance);
        case SOUTH_EAST:
          return new Rectangle(x + w - distance,
                               y + h - distance,
                               distance,
                               distance); 
      };
      
      return null;
    };
    
    //
    private int getResizeCursor(MouseEvent e) {
      
      Component c = e.getComponent();
      
      int w = c.getWidth();
      int h = c.getHeight();
      
      Rectangle r = new Rectangle(0, 0, w, h);
      
      if(!r.contains(e.getPoint()))
        return Cursor.DEFAULT_CURSOR;
      
      Rectangle a = new Rectangle(distance, distance, w - 2 * distance,
                                  h - 2 * distance);
      if(a.contains(e.getPoint()))
        return Cursor.DEFAULT_CURSOR;
      
      
      
      
      
      return Cursor.MOVE_CURSOR;
    };
  };
  
  //
  private final MouseInputListener listener = new MouseInputAdapter() {
    private int cursor;
    private Point start = null;
    
    @Override
    public void mouseMoved(MouseEvent e) {
      ResizableBorder border = (ResizableBorder)getBorder();
      cursor = border.getResizeCursor(e);
      start = e.getPoint();
    };
    
    @Override
    public void mouseExited(MouseEvent e) {
      
    };
    
    @Override
    public void mousePressed(MouseEvent e) {
      
    };
    
    @Override
    public void mouseDragged(MouseEvent e) {
      
    };
    
    @Override
    public void mouseReleased(MouseEvent e) {
      
    };
  };
  
  public WidgetComponent(JComponent child) {
    setLayout(new BorderLayout());
    add(child);
    setBorder(new ResizableBorder(6));
  };
  
  public void setBorder(Border border) {
    if(!(border instanceof ResizableBorder)) {
      removeMouseListener(listener);
      removeMouseMotionListener(listener);
    };
    super.setBorder(border);
  };
  
  private void didResize() {
    if(getParent() != null) {
      getParent().repaint();
      invalidate();
      ((JComponent)getParent()).revalidate();
    };
  };
};
