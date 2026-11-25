package ar.edu.unsam.ddso.tp.model.exceptions;

public class InactiveTagException extends RuntimeException {

    public InactiveTagException(String tagName) {
        super("Tag is inactive and cannot be cataloged: " + tagName);
    }
}
