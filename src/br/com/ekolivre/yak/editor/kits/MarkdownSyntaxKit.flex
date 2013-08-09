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

%%

%class MarkdownSyntaxKit

%public
%unicode
%type br.com.ekolivre.yak.editor.Token
%extends JFlexBasedSyntaxKit<MarkdownSyntaxKit.Context>

%{
  //
  public static final int DARING = 0x01;
  public static final int GITHUB = 0x02;
  public static final int STACKO = 0x04;
  
  //
  protected static class Context {
    private boolean italic;
    private boolean bold;
    
    private Context() {
      italic = false;
      bold = false;
    };
    
    private Context(Context other) {
      this.italic = other.italic;
      this.bold = other.bold;
    };
  };
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<Integer, String> dialects = 
    unmodifiableMap(new HashMap() {{
      put(DARING, "Daring Fireball");
      put(GITHUB, "GitHub Flavored");
      put(STACKO, "Stack Overflow Flavored");
    }});
  
  //
  @SuppressWarnings("unchecked")
  private static final Map<String, Integer> extensions_map =
    unmodifiableMap(new HashMap() {{
      put("md", DARING);
    }});
  
  //
  public MarkdownSyntaxKit() {
    return;
  };
  
  //
  @Override
  public Map<Integer, String> getDialectList() {
    return dialects;
  };
  
  //
  @Override
  public int getDefaultDialect() {
    return GITHUB;
  };
  
  //
  @Override
  public Map<String, Integer> getFileExtensions() {
    return extensions_map;
  };
  
  //
  @Override
  public String getContentType() {
    return "text/x-markdown";
  };
  
  // 
  @Override
  public String getKitName() {
    return "Markdown";
  };
  
  //
  private Context getActiveContext() {
    if(yystate != null) {
      if(yystate.userData() != null &&
         yystate.userData() instanceof Context) {
        return new Context((Context)yystate.userData());
      };
    };
    
    return new Context();
  };
  
  //
  private Token adequateToken(Context context) {
    if(context.bold && context.italic)
      return token(DEFAULT_BOLD_ITALIC, context);
    else if(context.bold)
      return token(DEFAULT_BOLD, context);
    else if(context.italic)
      return token(DEFAULT_ITALIC, context);
    else
      return token(DEFAULT, context);
  };
  
  //
  public int yypos() {
    return zzStartRead;
  };
%}

%%

/*" _ " {
  return token(DEFAULT);
}*/

"_" {
  Context context = getActiveContext();
  context.italic = !context.italic;
  return adequateToken(context);
}

[^ \t] {
  return adequateToken(getActiveContext());
}

.|\r|\n {
  return null;
}

<<EOF>> {
  return null;
}
