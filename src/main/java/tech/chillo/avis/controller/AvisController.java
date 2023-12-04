package tech.chillo.avis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.chillo.avis.entity.Avis;
import tech.chillo.avis.service.AvisService;

@RestController
@RequestMapping("avis")
@RequiredArgsConstructor
public class AvisController {
    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createAvis(@RequestBody Avis avis){
        avisService.createAvis(avis);
    }
}
