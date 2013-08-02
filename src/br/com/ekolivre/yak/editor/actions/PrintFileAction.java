package br.com.ekolivre.yak.editor.actions;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import br.com.ekolivre.yak.editor.*;

public class PrintFileAction extends AbstractEditorAction {
  private Callback callback;
  
  public PrintFileAction(Callback callback) {
    this.callback = callback;
  };
  
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/document-print.png", callback)
    };
  };
  
};
