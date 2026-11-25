package ar.edu.unsam.ddso.tp.model.exceptions;

public class DuplicateTagNameException extends RuntimeException {

    public DuplicateTagNameException(String tagName) {
        super("Tag name already exists: " + tagName);
    }
}
