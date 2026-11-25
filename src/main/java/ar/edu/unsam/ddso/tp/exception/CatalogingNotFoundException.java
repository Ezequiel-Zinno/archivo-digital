package ar.edu.unsam.ddso.tp.exception;

public class CatalogingNotFoundException extends RuntimeException {

    public CatalogingNotFoundException(Long digitizationId, Long tagId) {
        super(
                "Cataloging not found for digitization "
                        + digitizationId
                        + " and tag "
                        + tagId);
    }
}
