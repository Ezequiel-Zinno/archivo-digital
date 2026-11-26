package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PieceRepository extends JpaRepository<Piece, Long> {

    @Query(
            """
            SELECT DISTINCT p
            FROM Piece p
            LEFT JOIN Digitization d ON d.piece = p
            LEFT JOIN d.catalogings c
            LEFT JOIN c.tag t
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:description IS NULL
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%')))
              AND (:date IS NULL OR p.date = :date)
              AND (:tag IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tag, '%')))
            """)
    List<Piece> searchPieces(
            @Param("name") String name,
            @Param("description") String description,
            @Param("date") LocalDate date,
            @Param("tag") String tag);
}