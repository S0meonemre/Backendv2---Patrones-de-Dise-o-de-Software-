package com.financorp.serf.controller;

import com.financorp.serf.model.Filial;
import com.financorp.serf.repository.FilialRepository;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/filiales")
@Tag(name = "Filiales")
public class FilialController {

    private final FilialRepository filialRepository;

    public FilialController(FilialRepository filialRepository) {
        this.filialRepository = filialRepository;
    }

    @GetMapping
    @Operation(summary = "Consultar filiales disponibles y su moneda local")
    public List<Filial> listar() {
        return filialRepository.findAll();
    }
}
