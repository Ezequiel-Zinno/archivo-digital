package ar.edu.unsam.ddso.tp.exception;

import ar.edu.unsam.ddso.tp.model.exceptions.DuplicateTagNameException;
import ar.edu.unsam.ddso.tp.model.exceptions.InactiveTagException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagAlreadyCatalogingedException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTagNotFound(TagNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DigitizationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDigitizationNotFound(
            DigitizationNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CatalogingNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCatalogingNotFound(
            CatalogingNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TagAlreadyCatalogingedException.class)
    public ResponseEntity<Map<String, String>> handleTagAlreadyCataloged(
            TagAlreadyCatalogingedException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InactiveTagException.class)
    public ResponseEntity<Map<String, String>> handleInactiveTag(InactiveTagException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DuplicateTagNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateTagName(
            DuplicateTagNameException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
