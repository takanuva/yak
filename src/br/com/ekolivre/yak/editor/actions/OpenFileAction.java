package br.com.ekolivre.yak.editor.actions;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.prefs.*;
import javax.swing.filechooser.*;
import br.com.ekolivre.yak.editor.*;

public class OpenFileAction extends AbstractEditorAction {
  public static interface Callback {
    void run(File filename);
  };
  
  private JFrame frame;
  private Callback callback;
  private JFileChooser chooser = new JFileChooser();
  
  public OpenFileAction(JFrame frame, Callback callback) {
    this.frame = frame;
    this.callback = callback;
    
    chooser.setMultiSelectionEnabled(true);
    
    java.util.List<DefaultSyntaxKit> list =
      DefaultSyntaxKit.getArbitraryKitsList();
    
    for(DefaultSyntaxKit kit: list) {
      
      String exts[] = kit.extensionsAsArray();
      
      String label = kit.getKitName() + " (";
      for(String ext: exts)
        label += "*." + ext + ", ";
      label = label.substring(0, label.length() - 2) + ")";
      
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
        label,
        exts
      );
      
      chooser.addChoosableFileFilter(filter);
      
      if(kit.getContentType() == DefaultSyntaxKit.defaultContentType())
        chooser.setFileFilter(filter);
    };
  };
  
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/document-open.png", () -> {
        int ret = chooser.showOpenDialog(frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
          for(File file: chooser.getSelectedFiles())
            callback.run(file);
        };
      })
    };
  };
  
};
