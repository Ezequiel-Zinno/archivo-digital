package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Cataloging;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogingRepository extends JpaRepository<Cataloging, Long> {

    List<Cataloging> findByDigitizationId(Long digitizationId);

    Optional<Cataloging> findByDigitizationIdAndTagId(Long digitizationId, Long tagId);

    boolean existsByDigitizationIdAndTagId(Long digitizationId, Long tagId);

    void deleteByDigitizationIdAndTagId(Long digitizationId, Long tagId);
}
