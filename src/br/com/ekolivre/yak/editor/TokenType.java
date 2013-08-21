/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
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
import java.awt.geom.*;
import java.util.regex.*;
import java.util.prefs.*;
import javax.swing.text.*;
import static java.lang.System.*;
import static br.com.ekolivre.yak.editor.AbstractTokenType.*;

/**
 *
 */
public enum TokenType implements AbstractTokenType {
  //                    +----------------------------------------+
  DEFAULT               (                0x000000                ),
  DEFAULT_BOLD          (             0x000000, BOLD             ),
  DEFAULT_ITALIC        (            0x000000, ITALIC            ),
  DEFAULT_BOLD_ITALIC   (    0x000000, (short)(BOLD | ITALIC)    ),
  IDENTIFIER            (                0x000000                ),
  SHEBANG               (0xFFFFFF, 0x0000FF, (short)(TEOL | BOLD)),
  HEADING               (0xFFFFFF, 0x0000FF, (short)(TEOL | BOLD)),
  DOC_COMMENT           (                0x3458C1                ),
  LINE_COMMENT          (                0x007F00                ),
  BLOCK_COMMENT         (                0x007F00                ),
  NESTED_COMMENT        (                                        ),
  HEREDOC_COMMENT       (                                        ),
  SPEC_COMMENT          (                0xC18A34                ),
  EMACS_TAG             (                                        ),
  URL                   (                                        ),
  PREPROCESSOR          (                0x7F7F00                ),
  PREPROCESSOR_KEYWORD  (                0x7F7F5F                ),
  PREPROCESSOR_STRING   (                0x8F7F5F                ),
  KEYWORD               (             0x00007F, BOLD             ),
  IMPL_KEYWORD          (             0xFF1493, BOLD             ),
  UNUSED_KEYWORD        (        0xFFFFFF, 0xFF0000, BOLD        ),
  DOC_KEYWORD           (                                        ),
  NUMBER                (                0x007F7F                ),
  STRING                (                0x7F007F                ),
  STRING_INCOMPLETE     (        0x000000, 0xD5BEE0, TEOL        ),
  CHARACTER             (                0x3F3FFF                ),
  CHARACTER_INCOMPLETE  (        0x000000, 0x7F7FFF, TEOL        ),
  STRING_ESCAPE         (                0xFF0000                ),
  STRING_FORMAT         (                0xFF44DD                ),
  CHARACTER_ESCAPE      (                0x0000BB                ),
  REGEXP                (                                        ),
  REGEXP_CLASS          (                                        ),
  REGEXP_COUNT          (                                        ),
  REGEXP_CONTROL        (                                        ),
  PUNCTUATION           (             0x000000, BOLD             ),
  DITRIGRAPH            (             0xFF0000, BOLD             ),
  PUNCT_MATCHED         (                                        ),
  PUNCT_UNMATCHED       (                                        ),
  CAR_OR_LABEL          (            0xFF8800, ITALIC            ),
  SYMBOL                (                0xAAAA00                ),
  STANDARD              (                0xFF0000                ),
  ANNOTATION            (                                        ),
  PERL_FUNCTION         (                                        ),
  PERL_SCALAR           (                                        ),
  PERL_ARRAY            (                                        ),
  PERL_MAP              (                                        ),
  PERL_GLOB             (                                        ),
  PERL_SUBSTITUTION     (                                        ),
  PERL_TRANSLITERATION  (                                        ),
  QXL                   (                                        ),
  QWL                   (                                        ),
  SGML_TAG              (                                        ),
  SGML_ATTR             (                                        ),
  SGML_ERROR            (                                        ),
  RED                   (                0xFF0000                ),
  BLUE                  (                0x0000FF                ),
  GREEN                 (                0x00FF00                );
  //                    +----------------------------------------+
  
  private Color fore;
  private Color back;
  private short flags;
  
  private TokenType() {
    this(0x000000);
  };
  private TokenType(int fore) {
    this(fore, null);
  };
  private TokenType(int fore, Integer back) {
    this(fore, back, (short)0);
  };
  private TokenType(int fore, short flags) {
    this(fore, null, flags);
  };
  private TokenType(int fore, Integer back, short flags) {
    this.fore = new Color(fore);
    if(back != null)
      this.back = new Color(back);
    this.flags = flags;
  };
  
  public AbstractTokenType misspell() {
    
    return new AbstractTokenType() {
      
      @Override
      public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                       Dimension size, boolean invert) {
        return TokenType.this.write(s, x, y, g, e, size, invert, true);
      };
      
      @Override
      public int compareTo(AbstractTokenType other) {
        return toString().compareTo(other.toString());
      };
      
      @Override
      public boolean isComment() {
        return TokenType.this.isComment();
      };
      
      @Override
      public String toString() {
        return TokenType.this.toString() + ":misspelled";
      };
    };
  };
  
  
  
  
  public Color getForeground() {
    return this.fore;
  };
  
  public Color getBackground() {
    return this.back;
  };
  
  public Color getInvertedForeground() {
    Color fore = getForeground();
    if(fore == null)
      return null;
    return new Color(255 - fore.getRed(), 255 - fore.getGreen(),
                     255 - fore.getBlue(), fore.getAlpha());
  };
  
  public Color getInvertedBackground() {
    Color back = getBackground();
    if(back == null)
      return null;
    return new Color(255 - back.getRed(), 255 - back.getGreen(),
                     255 - back.getBlue(), back.getAlpha());
  };
  
  public boolean isBold() {
    return (flags & BOLD) > 0;
  };
  
  public boolean isItalic() {
    return (flags & ITALIC) > 0;
  };
  
  public boolean goesToEndOfLine() {
    return (flags & TEOL) > 0;
  };
  
  @Override
  public int compareTo(AbstractTokenType other) {
    return toString().compareTo(other.toString());
  };
  
  @Override
  public int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                   Dimension size, boolean invert) {
    //
    return write(s, x, y, g, e, size, invert, false);
  };
  
  //
  @Override
  public boolean isComment() {
    return this == LINE_COMMENT ||
           this == BLOCK_COMMENT ||
           this == NESTED_COMMENT ||
           this == HEREDOC_COMMENT;
  };
  
  //
  protected int write(Segment s, int x, int y, Graphics g, SyntaxView e,
                      Dimension size, boolean invert, boolean misspell) {
    //
    if(s.length() == 0)
      return x;
    
    //
    if(invert)
      g.setColor(getInvertedBackground());
    else g.setColor(getBackground());
    
    //
    g.setFont(g.getFont().deriveFont(
      isBold() ?
        isItalic() ? 3 : 1
      : isItalic() ? 2 : 0
    ));
    
    //
    FontMetrics m = g.getFontMetrics();
    int a = m.getAscent();
    int h = a + m.getDescent();
    
    //
    int w = Utilities.getTabbedTextWidth(s, m, x, e, 0);
    
    //
    int rx = x - 1;
    int ry = y - a;
    int rw = w + 2;
    int rh = h;
    
    //
    if(getBackground() != null) {
      
      if(goesToEndOfLine()) {
        rw = size.width - rx;
      };
      
      g.fillRect(rx, ry, rw, rh);
      
    };
    
    if(misspell) {
      Graphics2D g2d = (Graphics2D)g;
      GeneralPath path = new GeneralPath();
      path.moveTo(rx, ry + rh - 1);
      for(int i = 0; i < rw; i += 4) {
        path.lineTo(rx + i, ry + rh - 3);
        path.lineTo(rx + i + 2, ry + rh -1);
      };
      g2d.setColor(Color.RED);
      g2d.draw(path);
    };
    
    //
    if(invert)
      g.setColor(getInvertedForeground());
    else g.setColor(getForeground());
    
    //
    return Utilities.drawTabbedText(s, x, y, g, e, 0);
  };
};
