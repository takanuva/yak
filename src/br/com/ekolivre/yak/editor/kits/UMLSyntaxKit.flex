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
package br.com.ekolivre.yak.editor.kits;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

%%

%class UMLSyntaxKit

%public
%unicode
%type br.com.ekolivre.yak.editor.Token
%extends JFlexBasedSyntaxKit<UMLSyntaxKit.UMLLexicalState>

%{
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new TreeMap() {{
      put("uml", null);
    }});
  
  protected enum UMLLexicalState {
    UMLClass,
    UMLExtends;
  };
  
  /**
   *
   */
  public class UMLClass {
    
  };
  
  /**
   *
   */
  public class UMLPanel extends JPanel {
    //
    private Map<String, UMLClass> classes;
    
    //
    private int known = 0;
    
    //
    public UMLPanel() {
      classes = new HashMap<String, UMLClass>();
    };
    
    //
    private void rebuildFrom(int x) {
      
    };
    
    //
    @SuppressWarnings("unchecked")
    private void buildDiagrams() {
      Iterable<Token> list = getDocument().getTokens(known);
      
      for(Token t: list) {
        final TokenState state = t.getState();
        if(state instanceof JFlexBasedSyntaxKit<?>.JFlexBasedTokenState) {
          
          final JFlexBasedTokenState state2 = (JFlexBasedTokenState)state;
          
          UMLLexicalState lex = state2.userData();
          
          if(lex != null) {
            
          };
          
        };
      };
      
    };
    
    //
    @Override
    public void paint(Graphics g) {
      super.paint(g);
      
      buildDiagrams();
      
    };
  };
  
  /**
   *
   */
  public class UMLRendererAction extends AbstractEditorAction {
    //
    @Override
    protected JComponent makeComponents()[] {
      return new JComponent[] {
        makeButton("icons/internet-web-browser.png", "Preview...", () -> {
          if(renderer == null) {
            renderer = addWidget(new UMLPanel());
          } else {
            renderer = delWidget(renderer);
          };
        })
      };
    };
  };
  
  //
  WidgetComponent renderer = null;
  
  //
  public UMLSyntaxKit() {
    return;
  };
  
  //
  @Override
  protected void onInstall() {
    super.onInstall();
    renderer = addWidget(new UMLPanel());
  };
  
  //
  @Override
  protected void onDeinstall() {
    renderer = delWidget(renderer);
    super.onDeinstall();
  };
  
  //
  @Override
  public final void onChangeAt(Token last) {
    if(renderer != null) {
      
      UMLPanel uml = (UMLPanel)renderer.getChild();
      
      if(last == null)
        uml.rebuildFrom(0);
      else uml.rebuildFrom(last.end());
      
      /*if(last == null)
        renderer.invalidate();
      else renderer.invalidate(last.end());*/
    };
  };
  
  //
  @Override
  public Map<Integer, String> getDialectList() {
    return null;
  };
  
  //
  @Override
  public int getDefaultDialect() {
    return 0;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  @Override
  public void populate(JComponent c) {
    super.populate(c);
    super.addAction(c, new UMLRendererAction());
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-uml";
  };
  
  // 
  @Override
  public String getKitName() {
    return "UML";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

%state YYCLASS
%state YYCLASS2
%state YYEXTENDS

%%

<YYINITIAL> {
  "class" {
    yypush(YYCLASS);
    return token(KEYWORD);
  }
  
  [^\ \t\r\n]+ {
    return token(DOC_COMMENT);
  }
}

<YYCLASS> {
  [^\ \t\r\n]+ {
    yybegin(YYCLASS2);
    return token(DEFAULT);
  }
}

<YYCLASS2> {
  "extends" {
    yypush(YYEXTENDS);
    return token(KEYWORD);
  }
  
  "class" {
    yybegin(YYCLASS);
    return token(KEYWORD);
  }
  
  [^\ \t\r\n]+ {
    return token(DEFAULT);
  }
}

<YYEXTENDS> {
  "extends" {
    return token(KEYWORD);
  }
  
  "class" {
    yypop();
    return token(KEYWORD);
  }
  
  [^\ \t\r\n]+ {
    return token(DEFAULT);
  }
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
