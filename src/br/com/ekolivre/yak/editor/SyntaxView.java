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
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.lang.ref.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.text.Position.*;
import static java.lang.System.*;
import static java.util.Collections.*;

public final class SyntaxView extends BoxView
implements TabExpander {
  //
  private static RenderingHints hints = null;
  
  //
  static {
    try {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      
      //
      @SuppressWarnings("unchecked")
      Map<RenderingHints.Key, ?> map = (Map)toolkit.getDesktopProperty(
        "awt.font.desktophints"
      );
      
      //
      hints = new RenderingHints(map);
    } catch(Throwable t) {
      //
    };
  };
  
  public static void applyHints(Graphics g) {
    ((Graphics2D)g).addRenderingHints(hints);
  };
  
  public static enum SelectedStyle {
    BLACK,
    COLORIZE,
    INVERT
  };
  
  private DefaultSyntaxKit kit;
  
  /* package */ FontMetrics metrics;
  private Segment lineBuffer;
  private boolean widthChanging;
  private int tabBase;
  private int charWidth;
  private int tabSize;
  private boolean wordWrap;
  
  private int sel0;
  private int sel1;
  private Color unselected;
  private Color selected;
  
  private SelectedStyle selected_style = SelectedStyle.INVERT;
  
  private int longest_line = 0;
  
  public SyntaxView(Element element, DefaultSyntaxKit kit) {
    super(element, Y_AXIS);
    this.kit = kit;
    this.wordWrap = true;
    this.lineBuffer = new Segment();
    updateGutter();
  };
  
  private void calculateLongestLine() {
    Element e = getElement();
    int n = e.getElementCount();
    int max = -1;
    
    for(int i = 0; i < n; i++) {
      int w = getLineWidth(e.getElement(i));
      if(w > max) {
        max = w;
        longest_line = i;
      };
    };
  };
  
  public int getNumberOfLines() {
    return getElement().getElementCount();
  };
  
  private int getLineWidth(int i) {
    return getLineWidth(getElement().getElement(i));
  };
  
  private int getLineWidth(Element line) {
    if(line != null) {
      int p0 = line.getStartOffset();
      int p1 = line.getEndOffset();
      
      loadText(lineBuffer, p0, p1);
      
      return Utilities.getTabbedTextWidth(lineBuffer, metrics, tabBase, this,
                                          p0);
    };
    return 0;
  };
  
  public SyntaxSkin getSkin() {
    return kit.getSkin();
  };
  
  public int limitPosition() {
    return 80;
  };
  
  public boolean useSoftWrap() {
    // Later I have to check the sizing issue...
    
    return false;
  };
  
  private void updateGutter() {
    int w = getGutterWidth();
    
    setInsets(
      (short)0,
      (short)w,
      (short)0,
      (short)0
    );
  };
  
  public int getGutterWidth() {
    return getSkin().getGutterWidth(this);
  };
  
  protected int getTabSize() {
    Integer i = (Integer)getDocument().getProperty(
      PlainDocument.tabSizeAttribute
    );
    int size = (i != null) ? i : 8;
    return size;
  };
  
  protected void drawLine(int p0, int p1, Graphics g, int x, int y) {
    Element lineMap = getElement();
    Element line = lineMap.getElement(lineMap.getElementIndex(p0));
    
    try {
      if(line.isLeaf()) {
        // Simple line
        drawText(line, p0, p1, g, x, y);
      } else {
        // Composed line
        int last = line.getElementIndex(p1);
        for(int i = line.getElementIndex(p0); i <= last; i++) {
          Element e = line.getElement(i);
          int start = Math.max(e.getStartOffset(), p0);
          int end = Math.min(e.getEndOffset(), p1);
          x = drawText(e, start, end, g, x, y);
        };
      };
    } catch(BadLocationException e) {
      err.printf("Can't render: %d, %d.%n", p0, p1);
    };
  };
  
  private int drawText(Element elem, int p0, int p1, Graphics g, int x, int y)
  throws BadLocationException {
    p1 = Math.min(getDocument().getLength(), p1);
    
    
    if(sel0 == sel1 || selected == unselected) {
      // no selection, or it is invisible
      x = drawUnselectedText(g, x, y, p0, p1);
    } else if((p0 >= sel0 && p0 <= sel1) && (p1 >= sel0 && p1 <= sel1)) {
      x = drawSelectedText(g, x, y, p0, p1);
    } else if(sel0 >= p0 && sel0 <= p1) {
      if(sel1 >= p0 && sel1 <= p1) {
        x = drawUnselectedText(g, x, y, p0, sel0);
        x = drawSelectedText(g, x, y, sel0, sel1);
        x = drawUnselectedText(g, x, y, sel1, p1);
      } else {
        x = drawUnselectedText(g, x, y, p0, sel0);
        x = drawSelectedText(g, x, y, sel0, p1);
      };
    } else if (sel1 >= p0 && sel1 <= p1) {
      x = drawSelectedText(g, x, y, p0, sel1);
      x = drawUnselectedText(g, x, y, sel1, p1);
    } else {
      x = drawUnselectedText(g, x, y, p0, p1);
    };
    
    
    return x;
  };
  
  private Dimension getContainerDimension() {
    Container c = getContainer();
    return new Dimension(c.getWidth(), c.getHeight());
  };
  
  protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1)
  throws BadLocationException {
    
    return drawText(g, x, y, p0, p1, false);
    
  };
  
  protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1)
  throws BadLocationException {
    
    //
    switch(selected_style) {
      case BLACK:
        g.setFont(g.getFont().deriveFont(0));
        g.setColor(selected);
        loadText(lineBuffer, p0, p1);
        return Utilities.drawTabbedText(lineBuffer, x, y, g, this, p0);
      case COLORIZE:
        return drawText(g, x, y, p0, p1, false);
      case INVERT:
        return drawText(g, x, y, p0, p1, true);
    };
    
    //
    assert false;
    
    //
    return x;
  };
  
  private int drawText(Graphics g, int x, int y, int p0, int p1, boolean invert)
  throws BadLocationException {
    
    
    applyHints(g);
    
    SyntaxDocument doc = (SyntaxDocument)getDocument();
    Iterable<Token> tokens = doc.getTokens(p0, p1);
    
    loadText(lineBuffer, p0, p1);
    
    // Adjust size if needed
    //p1 = p0 + lineBuffer.count;
    
    Iterator<Token> it = tokens.iterator();
    
    int caret = ((JEditorPane)getContainer()).getCaretPosition();
    int o = 0;
    
    while(it.hasNext()) {
      Token tkn = it.next();
      try {
        if(tkn.end() > p0 && tkn.start() < p1) {
          if(tkn.start() < p0) {
            int l = tkn.end() - p0 - o;
            x = tkn.getType().write(
              (Segment)lineBuffer.subSequence(o, o + l),
              x, y, g, this, getContainerDimension(), invert
            );
            o += l;
          } else {
            int l = tkn.start() - p0 - o;
            x = TokenType.DEFAULT.write(
              (Segment)lineBuffer.subSequence(o, o + l),
              x, y, g, this, getContainerDimension(), invert
            );
            o += l;
            l = tkn.length();
            x = tkn.getType().write(
              (Segment)lineBuffer.subSequence(o, o + l),
              x, y, g, this, getContainerDimension(), invert
            );
            o += l;
          };
        };
      } catch(StringIndexOutOfBoundsException e) {
        return tkn.getType().write(
          (Segment)lineBuffer.subSequence(o, p1 - p0),
           x, y, g, this, getContainerDimension(), invert
        );
      };
    };
    
    return TokenType.DEFAULT.write(
      (Segment)lineBuffer.subSequence(o, p1 - p0),
       x, y, g, this, getContainerDimension(), invert
    );
  };
  
  protected final Segment getLineBuffer() {
    return lineBuffer;
  };
  
  protected int calculateBreakPosition(int p0, int p1) {
    loadText(lineBuffer, p0, p1);
    
    if(useSoftWrap()) {

      int width = getWidth();
      if(width == Integer.MAX_VALUE) {
        width = (int)getDefaultSpan(View.X_AXIS);
      };
      if(wordWrap) {
        return p0 + Utilities.getBreakLocation(lineBuffer, metrics, tabBase,
                                               tabBase + width, this, p0);
      };
      return p0 + Utilities.getTabbedTextOffset(lineBuffer, metrics, tabBase,
                                                tabBase + width, this, p0,
                                                false);
    };
    
    return p1;
  };
  
  protected void loadChildren(ViewFactory f) {
    Element e = getElement();
    int n = e.getElementCount();
    if(n > 0) {
      View added[] = new View[n];
      for(int i = 0; i < n; i++) {
        added[i] = new WrappableLine(e.getElement(i));
      };
      replace(0, 0, added);
    };
  };
  
  void updateChildren(DocumentEvent e, Shape a) {
    DocumentEvent.ElementChange ec = e.getChange(getElement());
    if(ec != null) {
      // the structure of this element changed.
      Element removedElems[] = ec.getChildrenRemoved();
      Element addedElems[] = ec.getChildrenAdded();
      View added[] = new View[addedElems.length];
      for(int i = 0; i < addedElems.length; i++) {
        added[i] = new WrappableLine(addedElems[i]);
      };
      replace(ec.getIndex(), removedElems.length, added);
      
      if(a != null) {
        updateMetrics();
        preferenceChanged(null, true, true);
        getContainer().repaint();
      };
    };
  };
  
  public void setParent(View parent) {
    super.setParent(parent);
    try {
      updateMetrics();
    } catch(Throwable t) {
      t.printStackTrace();
    };
  };
  
  final void loadText(Segment segment, int p0, int p1) {
    try {
      if(getDocument() instanceof SyntaxDocument) {
        SyntaxDocument doc = (SyntaxDocument)getDocument();
        doc.getDisplayText(p0, p1 - p0, segment);
      } else {
        getDocument().getText(p0, p1 - p0);
      };
    } catch(BadLocationException e) {
      err.printf("Can't get line text from %d to %d...%n", p0, p1);
    };
  };
  
  final void updateMetrics() {
    Component h;
    Font f;
    try {
      h = getContainer();
      f = h.getFont();
    } catch(NullPointerException e) {
      return;
    };
    metrics = h.getFontMetrics(f);
    charWidth = metrics.charWidth(' ');
    tabSize = getTabSize() * charWidth;
    updateGutter();
    calculateLongestLine();
  };
  
  private float getDefaultSpan(int axis) {
    switch(axis) {
      case View.X_AXIS:
      case View.Y_AXIS:
        return 0;
    };
    throw new IllegalArgumentException("Invalid axis: " + axis);
  };
  
  public float nextTabStop(float x, int tabOffset) {
    // Not sure how this works... but it does
    tabBase = getLeftInset();
    
    if(tabSize == 0)
      return x;
    int ntabs = ((int) x - tabBase) / tabSize;
    return tabBase + ((ntabs + 1) * tabSize);
  };
  
  public void paint(Graphics g, Shape a) {
    //
    applyHints(g);
    
    // TODO: rewrite this so that we may have block selections
    Rectangle alloc = (Rectangle)a;
    tabBase = alloc.x;
    JTextComponent host = (JTextComponent) getContainer();
    sel0 = host.getSelectionStart();
    sel1 = host.getSelectionEnd();
    unselected = (host.isEnabled()) ? 
    host.getForeground() : host.getDisabledTextColor();
    Caret c = host.getCaret();
    selected = c.isSelectionVisible() && host.getHighlighter() != null ?
                 host.getSelectedTextColor()
               : unselected;
    g.setFont(host.getFont().deriveFont(0));
    
    //
    getSkin().drawGutterBase(g, this);
    getSkin().drawLimitArea(g, this);
    
    // Superclass will paint the children :3
    super.paint(g, a);
  };
  
  public void setSize(float width, float height) {
    updateMetrics();
    if((int)width != getWidth()) {
      // invalidate the view itself since the childrens
      // desired widths will be based upon this views width.
      preferenceChanged(null, true, true);
      widthChanging = true;
    };
    super.setSize(width, height);
    widthChanging = false;
  };
  
  public float getPreferredSpan(int axis) {
    updateMetrics();
    return super.getPreferredSpan(axis);
  };
  
  public float getMinimumSpan(int axis) {
    updateMetrics();
    return super.getMinimumSpan(axis);
  };
  
  public float getMaximumSpan(int axis) {
    updateMetrics();
    return super.getMaximumSpan(axis);
  };
  
  void updateDamage() {
    calculateLongestLine();
    getContainer().repaint();
  };
  
  public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    updateChildren(e, a);
    
    Rectangle alloc = ((a != null) && isAllocationValid()) ? 
                        getInsideAllocation(a)
                      : null;
    int pos = e.getOffset();
    View v = getViewAtPosition(pos, alloc);
    if(v != null) {
      v.insertUpdate(e, alloc, f);
    };
    preferenceChanged(null, true, false);
    
    updateDamage();
  };
  
  public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    updateChildren(e, a);
    
    Rectangle alloc = ((a != null) && isAllocationValid()) ? 
                        getInsideAllocation(a)
                      : null;
    int pos = e.getOffset();
    View v = getViewAtPosition(pos, alloc);
    if(v != null) {
      v.removeUpdate(e, alloc, f);
    };
    
    preferenceChanged(null, true, false);
    
    updateDamage();
  };
  
  public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    updateChildren(e, a);
    preferenceChanged(null, true, true);
    
    updateDamage();
  };
  
  /**
   *
   */
  class WrappableLine extends View {
    WrappableLine(Element element) {
      super(element);
      lineCount = -1;
    };
    
    @Override
    public float getAlignment(int axis) {
      return 0;
    };
    
    @Override
    public float getMinimumSpan(int axis) {
      return getPreferredSpan(axis);
    };
   
    @Override
    public float getMaximumSpan(int axis) {
      return getPreferredSpan(axis);
    };
    
    public float getPreferredSpan(int axis) {
      switch(axis) {
        case View.X_AXIS:
          
          if(useSoftWrap()) {
            
            JViewport v = (JViewport)getContainer().getParent();
            float width = (float)v.getExtentSize().getWidth();
            if(width == Integer.MAX_VALUE) {
              width = getDefaultSpan(axis);
            };
            return width;
            
          };
          
          return getLineWidth(longest_line);
          
        case View.Y_AXIS:
          if(getDocument().getLength() > 0) {
            if((lineCount < 0) || widthChanging) {
              breakLines(getStartOffset());
            };
            return lineCount * metrics.getHeight();
          };
          return getDefaultSpan(axis);
      };
      throw new IllegalArgumentException("Invalid axis: " + axis);
    };
    
    public void paint(Graphics g, Shape a) {
      //
      Rectangle alloc = (Rectangle)a;
      int y = alloc.y + metrics.getAscent();
      int x = alloc.x;
      int start = getStartOffset(); 
      int end = getEndOffset();
      int p0 = start;
      int[] lineEnds = getLineEnds();
      
      //
      getSkin().drawLineNumber(
        g,
        SyntaxView.this,
        // Calculate the line number
        SyntaxView.this.getElement().getElementIndex(start) + 1,
        y
      );
      
      JTextComponent host = (JTextComponent)getContainer();
      Highlighter h = host.getHighlighter();
      LayeredHighlighter dh = (h instanceof LayeredHighlighter) ?
                                (LayeredHighlighter)h
                              : null;
      
      //
      for(int i = 0; i < lineCount; i++) {
        int p1 = (lineEnds == null) ?
                   end
                 : start + lineEnds[i];
        if(dh != null) {
          int hOffset = (p1 == end) ? (p1 - 1) : p1;
          dh.paintLayeredHighlights(g, p0, hOffset, a, host, this);
        };
        
        drawLine(p0, p1, g, x, y);
        
        p0 = p1;
        y += metrics.getHeight();
      };
    };
    
    public Shape modelToView(int pos, Shape a, Position.Bias b)
    throws BadLocationException {
      Rectangle alloc = a.getBounds();
      alloc.height = metrics.getHeight();
      alloc.width = 1;
      
      int p0 = getStartOffset();
      if(pos < p0 || pos > getEndOffset()) {
        throw new BadLocationException("Position out of range", pos);
      };
      
      int testP = (b == Position.Bias.Forward) ? pos : Math.max(p0, pos - 1);
      int line = 0;
      int lineEnds[] = getLineEnds();
      if(lineEnds != null) {
        line = findLine(testP - p0);
        if(line > 0) {
          p0 += lineEnds[line - 1];
        };
        alloc.y += alloc.height * line;
      };
      
      if(pos > p0) {
        loadText(lineBuffer, p0, pos);
        alloc.x += Utilities.getTabbedTextWidth(lineBuffer, metrics, alloc.x,
                                                SyntaxView.this, p0);
      };
      return alloc;
    };
    
    public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
      // PENDING(prinz) implement bias properly
      bias[0] = Position.Bias.Forward;

      Rectangle alloc = (Rectangle)a;
      int x = (int) fx;
      int y = (int) fy;
      if(y < alloc.y) {
        return getStartOffset();
      } else if(y > alloc.y + alloc.height) {
        return getEndOffset() - 1;
      };
      
      alloc.height = metrics.getHeight();
      int line = (alloc.height > 0 ?
                   (y - alloc.y) / alloc.height
                 : lineCount - 1);
        
      if(line >= lineCount) {
        return getEndOffset() - 1;
      };
      
      int p0 = getStartOffset();
      int p1;
      if(lineCount == 1) {
        p1 = getEndOffset();
      } else {
        int lineEnds[] = getLineEnds();
        p1 = p0 + lineEnds[line];
        if(line > 0) {
          p0 += lineEnds[line - 1];
        };
      };
      
      if(x < alloc.x) {
        return p0;
      } else if(x > alloc.x + alloc.width) {
        return p1 - 1;
      };
      
      loadText(lineBuffer, p0, p1);
      int n = Utilities.getTabbedTextOffset(lineBuffer, metrics, alloc.x, x,
                                            SyntaxView.this, p0);
      return Math.min(p0 + n, p1 - 1);
    };
    
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
      update(e, a);
    };
    
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
      update(e, a);
    };
    
    private void update(DocumentEvent ev, Shape a) {
      int oldCount = lineCount;
      breakLines(ev.getOffset());
      if(oldCount != lineCount) {
        SyntaxView.this.preferenceChanged(this, false, true);
        updateDamage();
      } else if (a != null) {
        Component c = getContainer();
        Rectangle alloc = (Rectangle) a;
        c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
      };
    };
    
    final int getLineEnds()[] {
      if(lineCache == null) {
        return null;
      };
      
      int lineEnds[] = lineCache.get();
      if(lineEnds == null) {
        // Cache was GC'd; rebuild it :)
        return breakLines(getStartOffset());
      };
      
      return lineEnds;
    };
    
    final int breakLines(int startPos)[] {
      int lineEnds[] = (lineCache == null) ? null : lineCache.get();
      int oldLineEnds[] = lineEnds;
      int start = getStartOffset();
      int lineIndex = 0;
      if(lineEnds != null) {
        lineIndex = findLine(startPos - start);
        if(lineIndex > 0) {
          lineIndex--;
        };
      };
      
      int p0 = (lineIndex == 0) ? start : start + lineEnds[lineIndex - 1];  
      int p1 = getEndOffset();
      while(p0 < p1) {
        int p = calculateBreakPosition(p0, p1);
        p0 = (p == p0) ? ++p : p; // 4410243
        
        if(lineIndex == 0 && p0 >= p1) {
          // do not use cache if there's only one line
          lineCache = null;
          lineEnds = null;
          lineIndex = 1;
          break;
        } else if(lineEnds == null || lineIndex >= lineEnds.length) {
          // we have 2+ lines, and the cache is not big enough
          // we try to estimate total number of lines
          double growFactor = ((double)(p1 - start) / (p0 - start));
          int newSize = (int)Math.ceil((lineIndex + 1) * growFactor);
          newSize = Math.max(newSize, lineIndex + 2);
          int tmp[] = new int[newSize];
          if(lineEnds != null) {
            System.arraycopy(lineEnds, 0, tmp, 0, lineIndex);
          };
          lineEnds = tmp;
        };
        lineEnds[lineIndex++] = p0 - start;
      };
      
      lineCount = lineIndex;
      
      if(lineCount > 1) {
        // check if the cache is too big
        int maxCapacity = lineCount + lineCount / 3;
        if(lineEnds.length > maxCapacity) {
          int tmp[] = new int[maxCapacity];
          System.arraycopy(lineEnds, 0, tmp, 0, lineCount);
          lineEnds = tmp;
        };
      };
      
      if(lineEnds != null && lineEnds != oldLineEnds) {
        lineCache = new SoftReference<int[]>(lineEnds);
      };
      return lineEnds;
    };
    
    /**
     * Binary search in the cache for line containing specified offset
     * (which is relative to the beginning of the view). This method
     * assumes that cache exists.
     */
    private int findLine(int offset) {
      int lineEnds[] = lineCache.get();
      if(offset < lineEnds[0]) {
        return 0;
      } else if(offset > lineEnds[lineCount - 1]) {
        return lineCount;
      }
      return findLine(lineEnds, offset, 0, lineCount - 1);
    };
    
    private int findLine(int array[], int offset, int min, int max) {
      if(max - min <= 1)
        return max;
      int mid = (max + min) / 2;
      return (offset < array[mid]) ?
               findLine(array, offset, min, mid)
             : findLine(array, offset, mid, max);
    };
    
    int lineCount;
    SoftReference<int[]> lineCache = null;
  };
};
