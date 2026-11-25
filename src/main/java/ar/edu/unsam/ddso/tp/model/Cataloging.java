package ar.edu.unsam.ddso.tp.model;

import ar.edu.unsam.ddso.tp.model.exceptions.InactiveTagException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagAlreadyCatalogingedException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "catalogings",
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"digitization_id", "tag_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cataloging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "digitization_id", nullable = false)
    private Digitization digitization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public static Cataloging of(Digitization digitization, Tag tag) {
        Cataloging cataloging = new Cataloging();
        cataloging.setDigitization(digitization);
        cataloging.setTag(tag);
        cataloging.validateCanBeCreated();
        return cataloging;
    }

    public void validateCanBeCreated() {
        if (!tag.isActive()) {
            throw new InactiveTagException(tag.getName());
        }
    }

    public void validateDoesNotAlreadyExist(boolean alreadyExists) {
        if (alreadyExists) {
            throw new TagAlreadyCatalogingedException(tag.getId());
        }
    }
}
