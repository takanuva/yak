/*******************************************************************************
* Project: YAK Code Editor                                                     *
* License: GNU LGPL.                                                           *
* Author: Paulo H. "Taka" Torrens.                                             *
* E-Mail: paulotorrens@ekolivre.com.br                                         *
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
    private boolean saw_class;
    private boolean saw_event;
    
    //
    private boolean inside_class;
    private boolean inside_event;
    
    //
    private Context old = null;
    
    //
    private Context() {
      resetView();
      
      inside_class = false;
      inside_event = false;
    };
    
    //
    private Context(Context other) {
      if(other == null) {
        resetView();
        
        inside_class = false;
        inside_event = false;
      } else {
        this.saw_paren = other.saw_paren;
        this.saw_class = other.saw_class;
        this.saw_event = other.saw_event;
        
        this.inside_class = other.inside_class;
        this.inside_event = other.inside_event;
        
        this.old = other.old;
      };
    };
    
    //
    private void resetView() {
      saw_paren = false;
      saw_class = false;
      saw_event = false;
    };
    
    //
    private Context openBracket() {
      //
      Context res = new Context();
      
      //
      res.inside_class = this.saw_class;
      res.inside_event = this.saw_event;
      
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
  "get" | "set" {
    Context ctx = yystate.userData();
    if(ctx != null && ctx.old != null && ctx.old.inside_class
                                      && !ctx.old.saw_paren)
      return token(KEYWORD, ctx);
    return token(IDENTIFIER, ctx);
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
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
