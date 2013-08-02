package br.com.ekolivre.yak.editor;

public class InvalidDialectException extends RuntimeException {
  public InvalidDialectException() {
    super("Invalid dialect code.");
  };
};
