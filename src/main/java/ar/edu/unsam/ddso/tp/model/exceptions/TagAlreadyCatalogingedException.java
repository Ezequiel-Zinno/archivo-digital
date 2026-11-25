package ar.edu.unsam.ddso.tp.model.exceptions;

public class TagAlreadyCatalogingedException extends RuntimeException {

    public TagAlreadyCatalogingedException(Long tagId) {
        super("Tag already cataloged for this digitization: " + tagId);
    }
}
