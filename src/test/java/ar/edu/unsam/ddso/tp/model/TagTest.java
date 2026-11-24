package ar.edu.unsam.ddso.tp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TagTest {

    @Test
    void newTagIsActive() {
        Tag tag = new Tag();

        assertTrue(tag.isActive());
    }

    @Test
    void tagCanBeDeactivated() {
        Tag tag = new Tag();

        tag.deactivate();

        assertFalse(tag.isActive());
    }

    @Test
    void tagCanBeActivatedAgain() {
        Tag tag = new Tag();
        tag.deactivate();

        tag.activate();

        assertTrue(tag.isActive());
    }
}