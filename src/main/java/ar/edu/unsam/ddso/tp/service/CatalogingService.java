package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.CatalogingCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.CatalogingResponseDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.CatalogingNotFoundException;
import ar.edu.unsam.ddso.tp.exception.DigitizationNotFoundException;
import ar.edu.unsam.ddso.tp.exception.TagNotFoundException;
import ar.edu.unsam.ddso.tp.model.Cataloging;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.CatalogingRepository;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogingService {

    private final CatalogingRepository catalogingRepository;
    private final DigitizationRepository digitizationRepository;
    private final TagRepository tagRepository;

    public CatalogingService(
            CatalogingRepository catalogingRepository,
            DigitizationRepository digitizationRepository,
            TagRepository tagRepository) {
        this.catalogingRepository = catalogingRepository;
        this.digitizationRepository = digitizationRepository;
        this.tagRepository = tagRepository;
    }

    // ---------- Consultas ----------

    public List<CatalogingResponseDTO> tagsOfDigitization(Long digitizationId) {
        if (!digitizationRepository.existsById(digitizationId)) {
            throw new DigitizationNotFoundException(digitizationId);
        }
        return catalogingRepository.findByDigitizationId(digitizationId).stream()
                .map(this::toResponse)
                .toList();
    }

    // ---------- Operaciones ----------

    @Transactional
    public CatalogingResponseDTO catalog(
            Long digitizationId, CatalogingCreateRequestDTO request) {
        Digitization digitization =
                digitizationRepository
                        .findById(digitizationId)
                        .orElseThrow(() -> new DigitizationNotFoundException(digitizationId));
        Tag tag =
                tagRepository
                        .findById(request.tagId())
                        .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        Cataloging cataloging = Cataloging.of(digitization, tag);
        cataloging.validateDoesNotAlreadyExist(
                catalogingRepository.existsByDigitizationIdAndTagId(
                        digitizationId, request.tagId()));

        return toResponse(catalogingRepository.save(cataloging));
    }

    @Transactional
    public void uncatalog(Long digitizationId, Long tagId) {
        if (!digitizationRepository.existsById(digitizationId)) {
            throw new DigitizationNotFoundException(digitizationId);
        }
        if (!tagRepository.existsById(tagId)) {
            throw new TagNotFoundException(tagId);
        }
        if (!catalogingRepository.existsByDigitizationIdAndTagId(digitizationId, tagId)) {
            throw new CatalogingNotFoundException(digitizationId, tagId);
        }
        catalogingRepository.deleteByDigitizationIdAndTagId(digitizationId, tagId);
    }

    // ---------- Privados ----------

    private CatalogingResponseDTO toResponse(Cataloging cataloging) {
        Tag tag = cataloging.getTag();
        TagResponseDTO tagDto = new TagResponseDTO(tag.getId(), tag.getName(), tag.isActive());
        return new CatalogingResponseDTO(
                cataloging.getId(), cataloging.getDigitization().getId(), tagDto);
    }
}
