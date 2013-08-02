package br.com.ekolivre.yak.editor.actions;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import br.com.ekolivre.yak.editor.*;

public class NewFileAction extends AbstractEditorAction {
  private Callback callback;
  
  public NewFileAction(Callback callback) {
    this.callback = callback;
  };
  
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/document-new.png", callback)
    };
  };
  
};
