package ar.edu.unsam.ddso.tp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "digitizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Digitization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String url;

    @Column(nullable = false, length = 2000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "piece_id", nullable = false)
    private Piece piece;

    @OneToMany(mappedBy = "digitization")
    private Set<Cataloging> catalogings = new HashSet<>();
}
