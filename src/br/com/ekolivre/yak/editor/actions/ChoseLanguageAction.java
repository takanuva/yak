package br.com.ekolivre.yak.editor.actions;

import java.util.*;
import javax.swing.*;
import java.awt.Component;
import br.com.ekolivre.yak.editor.*;
import static java.lang.System.*;
import static java.util.Collections.*;

public class ChoseLanguageAction extends AbstractEditorAction {
  public static interface Callback {
    void run(String kit, Integer dialect);
  };
  
  static class DialectItem {
    Integer index;
    String value;
    String content_type;
    DialectItem(Integer index, String value, String content_type) {
      this.index = index;
      this.value = value;
      this.content_type = content_type;
    };
    @Override
    public String toString() {
      return this.value;
    };
  };
  
  private Callback callback;
  private String initial;
  private Integer dialect;
  
  public ChoseLanguageAction(String initial, Integer dialect,
                             Callback callback) {
    this.callback = callback;
    this.initial = initial;
    this.dialect = dialect;
  };
  
  @Override
  protected JComponent makeComponents()[] {
    //
    JComboBox<DialectItem> dialect_box = makeDialectBox();
    
    //
    JComponent res[] = new JComponent[] {
      makeComboBox(dialect_box),
      dialect_box
    };
    
    //
    return res;
  };
  
  private JComboBox<DefaultSyntaxKit> makeComboBox(
    JComboBox<DialectItem> dialect_box
  ) {
    //
    JComboBox<DefaultSyntaxKit> box = new JComboBox<>();
    
    //
    box.addActionListener(e -> {
      DefaultSyntaxKit item = (DefaultSyntaxKit)box.getSelectedItem();
      Map<Integer, String> dialects = item.getDialectList();
      
      if(dialects != null && dialects.size() > 0) {
        
        Integer d = dialect == null ? item.getDefaultDialect() : dialect;
        
        for(Map.Entry<Integer, String> i: dialects.entrySet()) {
          DialectItem aux = new DialectItem(i.getKey(), i.getValue(),
                                            item.getContentType());
          
          dialect_box.addItem(aux);
          
          if(i.getKey() == d)
            dialect_box.setSelectedItem(aux);
        };
        
        dialect_box.setVisible(true);
        dialect_box.setMaximumSize(dialect_box.getPreferredSize());
        
        dilectBoxAddCallback(dialect_box);
      };
      
    });
    
    //
    populate(box, dialect_box);
    
    //
    box.addActionListener(e -> {
      DefaultSyntaxKit item = (DefaultSyntaxKit)box.getSelectedItem();
      
      callback.run(item.getContentType(), null);
    });
    
    return box;
  };
  
  private void populate(JComboBox<DefaultSyntaxKit> box,
                        JComboBox<DialectItem> dialect_box) {
    
    
    List<DefaultSyntaxKit> list = DefaultSyntaxKit.getArbitraryKitsList();
    
    for(DefaultSyntaxKit kit: list) {
      box.addItem(kit);
      
      if(kit.getContentType() == initial)
        box.setSelectedItem(kit);
    };
    
    box.setMaximumSize(box.getPreferredSize());
  };
  
  private JComboBox<DialectItem> makeDialectBox() {
    //
    JComboBox<DialectItem> box = new JComboBox<>();
    box.setVisible(false);
    
    //
    return box;
  };
  
  private void dilectBoxAddCallback(JComboBox<DialectItem> box) {
    box.addActionListener(e -> {
      DialectItem item = (DialectItem)box.getSelectedItem();
      callback.run(item.content_type, item.index);
    });
  };
  
};
