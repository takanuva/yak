package br.com.ekolivre.yak.editor.actions;

import java.awt.*;
import javax.swing.*;
import br.com.ekolivre.yak.editor.*;

public class UndoRedoAction extends AbstractEditorAction {
  @Override
  protected JComponent makeComponents()[] {
    return new JComponent[] {
      makeButton("icons/edit-undo.png"),
      makeButton("icons/edit-redo.png")
    };
  };
};
