package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.service.PieceSearchService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pieces")
public class PieceSearchController {

    private final PieceSearchService pieceSearchService;

    public PieceSearchController(PieceSearchService pieceSearchService) {
        this.pieceSearchService = pieceSearchService;
    }

    // ---------- Consultas ----------

    @GetMapping("/search")
    public List<PieceResponseDTO> searchPieces(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date,
            @RequestParam(name = "tag", required = false) String tag) {
        return pieceSearchService.searchPieces(name, description, date, tag);
    }
}