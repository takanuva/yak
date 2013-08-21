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
package br.com.ekolivre.yak.editor.kits;

import java.util.*;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static br.com.ekolivre.yak.editor.TokenType.*;

/**
 * 
 */
%%

%class CSharpSyntaxKit

%public
%unicode
%type Token
%extends JFlexBasedSyntaxKit<CSharpSyntaxKit.Context>

%{
  //
  protected static class Context {
    //
    private boolean saw_paren;
    private boolean saw_angle;
    private boolean saw_class;
    private boolean saw_event;
    private boolean saw_set;
    
    //
    private boolean inside_class;
    private boolean inside_event;
    private boolean inside_set;
    
    //
    private Context old = null;
    
    //
    private Context() {
      resetView();
      
      inside_class = false;
      inside_event = false;
      inside_set = false;
    };
    
    //
    private Context(Context other) {
      if(other == null) {
        resetView();
        
        inside_class = false;
        inside_event = false;
        inside_set = false;
      } else {
        this.saw_paren = other.saw_paren;
        this.saw_angle = other.saw_angle;
        this.saw_class = other.saw_class;
        this.saw_event = other.saw_event;
        this.saw_set = other.saw_set;
        
        this.inside_class = other.inside_class;
        this.inside_event = other.inside_event;
        this.inside_set = other.inside_set;
        
        this.old = other.old;
      };
    };
    
    //
    private void resetView() {
      saw_paren = false;
      saw_angle = false;
      saw_class = false;
      saw_event = false;
      saw_set = false;
    };
    
    //
    private Context openBracket() {
      //
      Context res = new Context();
      
      //
      res.inside_class = this.saw_class;
      res.inside_event = this.saw_event;
      res.inside_set = this.inside_set || this.saw_set;
      
      //
      res.old = this;
      
      //
      return res;
    };
    
    //
    private Context closeBracket() {
      Context res = new Context(old);
      res.resetView();
      return res;
    };
    
    //
    public String toString() {
      return "<paren:"+saw_paren+";event:"+saw_event+","+inside_event+";class:"+
             saw_class+","+inside_class+">";
    };
  };
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("cs", null);
    }});
  
  //
  public CSharpSyntaxKit() {
    return;
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
  
  //
  @Override
  public String getContentType() {
    return "text/x-csharp";
  };
  
  // 
  @Override
  public String getKitName() {
    return "C#";
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

%%

<YYINITIAL> {
  //
  "//" {
    return checkComment("//");
  }
  
  //
  "/*" {
    return checkComment("/*", "*/");
  }
  
  //
  "abstract"   |
  "as"         |
  "base"       |
  "bool"       |
  "break"      |
  "byte"       |
  "case"       |
  "catch"      |
  "char"       |
  "checked"    |
//"class"      |
  "const"      |
  "continue"   |
  "decimal"    |
  "default"    |
  "delegate"   |
  "do"         |
  "double"     |
  "explicit"   |
//"event"      |
  "extern"     |
  "else"       |
  "enum"       |
  "false"      |
  "finally"    |
  "fixed"      |
  "float"      |
  "for"        |
  "foreach"    |
  "from"       |
  "goto"       |
  "if"         |
  "implicit"   |
  "in"         |
  "int"        |
  "interface"  |
  "internal"   |
  "is"         |
  "lock"       |
  "long"       |
  "new"        |
  "null"       |
  "namespace"  |
  "object"     |
  "operator"   |
  "out"        |
  "override"   |
  "params"     |
  "private"    |
  "protected"  |
  "public"     |
  "readonly"   |
  "ref"        |
  "return"     |
  "switch"     |
  "struct"     |
  "sbyte"      |
  "sealed"     |
  "short"      |
  "sizeof"     |
  "stackalloc" |
  "static"     |
  "string"     |
  "this"       |
  "throw"      |
  "true"       |
  "try"        |
  "typeof"     |
  "uint"       |
  "ulong"      |
  "unchecked"  |
  "unsafe"     |
  "ushort"     |
  "using"      |
  "virtual"    |
  "volatile"   |
  "void"       |
  "while"      {
    return token(KEYWORD, yystate.userData());
  }
  
  //
  "class" {
    Context aux = new Context(yystate.userData());
    aux.saw_class = true;
    return token(KEYWORD, aux);
  }
  
  //
  "event" {
    Context aux = new Context(yystate.userData());
    aux.saw_event = true;
    return token(KEYWORD, aux);
  }
  
  //
  "add" | "remove" {
    Context ctx = yystate.userData();
    if(ctx != null && ctx.inside_event)
      return token(KEYWORD, ctx);
    return token(IDENTIFIER, ctx);
  }
  
  //
  "get" {
    Context ctx = yystate.userData();
    if(ctx != null && ctx.old != null && ctx.old.inside_class
                                      && !ctx.old.saw_paren)
      return token(KEYWORD, ctx);
    return token(IDENTIFIER, ctx);
  }
  
  //
  "set" {
    Context ctx = yystate.userData();
    if(ctx != null && ctx.old != null && ctx.old.inside_class
                                      && !ctx.old.saw_paren) {
      ctx = new Context(ctx);
      ctx.saw_set = true;
      return token(KEYWORD, ctx);
    };
    return token(IDENTIFIER, ctx);
  }
  
  //
  "global"[\ \t\r\n]*"::" {
    yypushback(yylength() - 6);
    return token(KEYWORD);
  }
  
  //
  "partial"[\ \t\r\n]*("class"|"interface"|"struct"|"void") {
    yypushback(yylength() - 7);
    return token(KEYWORD);
  }
  
  //
  "where"[\ \t\r\n]*[:jletter:][:jletterdigit:]* {
    yypushback(yylength() - 5);
    
    Context ctx = yystate.userData();
    if(ctx != null && ctx.saw_class && ctx.saw_angle)
      return token(KEYWORD, ctx);
    
    return token(IDENTIFIER, ctx);
  }
  
  //
  "value" {
    Context ctx = yystate.userData();
    if(ctx != null && ctx.inside_set)
      return token(STANDARD, ctx);
    return token(IDENTIFIER, ctx);
  }
  
  //
  "yield"[\ \t\r\n]*"return" {
    yypushback(yylength() - 5);
    return token(KEYWORD);
  }
  
  //
  [:jletter:][:jletterdigit:]* {
    return token(IDENTIFIER, yystate.userData());
  }
  
  //
  "(" {
    Context ctx = yystate.userData();
    if(ctx == null)
      ctx = new Context();
    
    ctx.saw_paren = true;
    
    return token(PUNCTUATION, +'(', ctx);
  }
  
  //
  ")" {
    return token(PUNCTUATION, -'(', yystate.userData());
  }
  
  //
  "{" {
    Context ctx = yystate.userData();
    if(ctx == null)
      ctx = new Context();
    
    return token(PUNCTUATION, +'{', ctx.openBracket());
  }
  
  //
  "}" {
    Context ctx = yystate.userData();
    if(ctx == null)
      ctx = new Context();
    
    return token(PUNCTUATION, -'{', ctx.closeBracket());
  }
  
  //
  ";" {
    Context ctx = new Context(yystate.userData());
    
    ctx.resetView();
    
    return token(PUNCTUATION, ctx);
  }
  
  //
  ">" {
    Context ctx = new Context(yystate.userData());
    
    ctx.saw_angle = true;
    
    return token(PUNCTUATION, ctx);
  }
  
  //
  [<+=-?/&\^%!,.:|~]+ {
    return token(PUNCTUATION, yystate.userData());
  }
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
