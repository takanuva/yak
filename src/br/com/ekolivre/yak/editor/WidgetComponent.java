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
import static java.awt.Cursor.*;
import static javax.swing.SwingConstants.*;

/**
 * I've used http://www.jroller.com/santhosh/entry/resizable_components as the
 * base to implement this. Thanks to the author. :)
 */
public class WidgetComponent extends JComponent {
  /**
   *
   */
  private static class ResizableBorder implements Border {
    //
    private final int dist = 7;
    
    //
    final static int locations[] = {
      NORTH, SOUTH, WEST, EAST,
      NORTH_WEST,   NORTH_EAST,
      SOUTH_WEST,   SOUTH_EAST,
      0,            -1
    };
    
    //
    final static int cursors[] = {
      N_RESIZE_CURSOR,  S_RESIZE_CURSOR,
      W_RESIZE_CURSOR,  E_RESIZE_CURSOR,
      NW_RESIZE_CURSOR, NE_RESIZE_CURSOR,
      SW_RESIZE_CURSOR, SE_RESIZE_CURSOR,
      MOVE_CURSOR,      DEFAULT_CURSOR
    };
    
    //
    @Override
    public Insets getBorderInsets(Component component){
      return new Insets(dist, dist, dist, dist);
    };
    
    //
    @Override
    public boolean isBorderOpaque() {
      return false;
    };
    
    //
    @Override
    public void paintBorder(Component c, Graphics g,
                            int x, int y, int w, int h) {
      //
      g.setColor(Color.black);
      g.drawRect(x + dist / 2,
                 y + dist / 2,
                 w - dist,
                 h - dist);
      
      //
      for(int i = 0; i < locations.length - 2; i++) {
        Rectangle rect = getRect(x, y, w, h, locations[i]);
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
        g.setColor(Color.BLACK);
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
      };
    };
    
    //
    private Rectangle getRect(int x, int y, int w, int h, int location){
      switch(location){
        case NORTH:
          return new Rectangle(x + w / 2 - dist / 2,
                               y,
                               dist,
                               dist);
        case SOUTH:
          return new Rectangle(x + w / 2 - dist / 2,
                               y + h - dist,
                               dist,
                               dist);
        case WEST:
          return new Rectangle(x,
                               y + h / 2 - dist / 2,
                               dist,
                               dist);
        case EAST:
          return new Rectangle(x + w - dist,
                               y + h / 2 - dist / 2,
                               dist,
                               dist);
        case NORTH_WEST:
          return new Rectangle(x,
                               y,
                               dist,
                               dist);
        case NORTH_EAST:
          return new Rectangle(x + w - dist,
                               y,
                               dist,
                               dist);
        case SOUTH_WEST:
          return new Rectangle(x,
                               y + h - dist,
                               dist,
                               dist);
        case SOUTH_EAST:
          return new Rectangle(x + w - dist,
                               y + h - dist,
                               dist,
                               dist);
      };
      
      return null;
    };

    public int getResizeCursor(MouseEvent e) {
      Point p = e.getPoint();
      Component c = e.getComponent();
      int w = c.getWidth();
      int h = c.getHeight();
      
      Rectangle b = new Rectangle(0, 0, w, h);
      
      if(!b.contains(p))
        return Cursor.DEFAULT_CURSOR;
      
      Rectangle a = new Rectangle(dist, dist, w - 2 * dist, h - 2 * dist);
      if(a.contains(p))
        return Cursor.DEFAULT_CURSOR;
      
      for(int i=0; i<locations.length-2; i++){
        if(getRect(0, 0, w, h, locations[i]).contains(p))
          return cursors[i];
      };
      
      return Cursor.MOVE_CURSOR;
    };
  };
  
  /**
   *
   */
  public WidgetComponent(DefaultSyntaxKit kit, Component c) {
    this(kit, c, true);
  };
  
  /**
   *
   */
  public WidgetComponent(DefaultSyntaxKit kit, Component c, boolean resizable) {
    setLayout(new BorderLayout());
    setBorder(new ResizableBorder());
    add(c);
  };
  
  public void setBorder(Border b) {
    removeMouseListener(resizeListener);
    removeMouseMotionListener(resizeListener);
    if(b instanceof ResizableBorder) {
      addMouseListener(resizeListener);
      addMouseMotionListener(resizeListener);
    };
    super.setBorder(b);
  };
  
  private void didResize() {
    if(getParent() != null){
      getParent().repaint();
      invalidate();
      ((JComponent)getParent()).revalidate();
    };
  };
  
  MouseInputListener resizeListener = new MouseInputAdapter() {
    public void mouseMoved(MouseEvent e) {
      ResizableBorder b = (ResizableBorder)getBorder();
      setCursor(getPredefinedCursor(b.getResizeCursor(e)));
    };

    public void mouseExited(MouseEvent e) {
      setCursor(getDefaultCursor());
    };
    
    private int cursor;
    private Point startPos = null;
    
    public void mousePressed(MouseEvent e) {
      ResizableBorder b = (ResizableBorder)getBorder();
      cursor = b.getResizeCursor(e);
      startPos = e.getPoint();
    };
    
    public void mouseDragged(MouseEvent e) {
      if(startPos != null){
        int dx = e.getX() - startPos.x;
        int dy = e.getY() - startPos.y;
        switch(cursor) {
          case N_RESIZE_CURSOR: {
            setBounds(getX(), getY() + dy, getWidth(), getHeight() - dy);
            didResize();
          } break;
          case S_RESIZE_CURSOR: {
            setBounds(getX(), getY(), getWidth(), getHeight() + dy);
            startPos = e.getPoint();
            didResize();
          } break;
          case W_RESIZE_CURSOR: {
            setBounds(getX() + dx, getY(), getWidth() - dx, getHeight());
            didResize();
          } break;
          case Cursor.E_RESIZE_CURSOR: {
            setBounds(getX(), getY(), getWidth() + dx, getHeight());
            startPos = e.getPoint();
            didResize();
          } break;
          case NW_RESIZE_CURSOR: {
            setBounds(getX() + dx, getY() + dy, getWidth() - dx,
                                                getHeight() - dy);
            didResize();
          } break;
          case NE_RESIZE_CURSOR: {
            setBounds(getX(), getY() + dy, getWidth() + dx, getHeight() - dy);
            startPos = new Point(e.getX(), startPos.y);
            didResize();
          } break;
          case SW_RESIZE_CURSOR: {
            setBounds(getX() + dx, getY(), getWidth() - dx, getHeight() + dy);
            startPos = new Point(startPos.x, e.getY());
            didResize();
          } break;
          case SE_RESIZE_CURSOR: {
             setBounds(getX(), getY(), getWidth() + dx, getHeight() + dy);
             startPos = e.getPoint();
             didResize();
          } break;
          case MOVE_CURSOR: {
            Rectangle bounds = getBounds();
            bounds.translate(dx, dy);
            setBounds(bounds);
            didResize();
          } break;
        };
        
        setCursor(getPredefinedCursor(cursor));
      };
    };
    
    public void mouseReleased(MouseEvent e) {
      startPos = null;
    };
  };
};
